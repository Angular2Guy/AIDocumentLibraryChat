/**
 *    Copyright 2023 Sven Loesekann
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.aidoclibchat.usecase.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ch.xxx.aidoclibchat.domain.client.ImportClient;
import ch.xxx.aidoclibchat.domain.common.MetaData;
import ch.xxx.aidoclibchat.domain.model.dto.SearchDto;
import ch.xxx.aidoclibchat.domain.model.entity.Artist;
import ch.xxx.aidoclibchat.domain.model.entity.ColumnMetadata;
import ch.xxx.aidoclibchat.domain.model.entity.DocumentVsRepository;
import ch.xxx.aidoclibchat.domain.model.entity.Museum;
import ch.xxx.aidoclibchat.domain.model.entity.MuseumHours;
import ch.xxx.aidoclibchat.domain.model.entity.Subject;
import ch.xxx.aidoclibchat.domain.model.entity.TableMetadata;
import ch.xxx.aidoclibchat.domain.model.entity.TableMetadataRepository;
import ch.xxx.aidoclibchat.domain.model.entity.Work;
import ch.xxx.aidoclibchat.domain.model.entity.WorkLink;
import ch.xxx.aidoclibchat.domain.utils.StreamHelpers;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TableService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TableService.class);
	private static final Double MAX_ROW_DISTANCE = 0.30;
	private final ImportClient importClient;
	private final ImportService importService;
	private final DocumentVsRepository documentVsRepository;
	private final TableMetadataRepository tableMetadataRepository;
	private final ChatClient chatClient;
	private final JdbcTemplate jdbcTemplate;
	private final String systemPrompt = """
			 You are a Postgres expert. Given an input question, create syntactically correct Postgres query. \n
			 Unless the user  specifies in the question a specific number of examples to  obtain, query for at most 100 results using the LIMIT clause \n
			 as per Postgres. You order the results to return the  most informative data in the database. Never query for all  columns from a table. \n
			 You must query only the columns that  are needed to answer the question. Wrap each column name in  double quotes to denote them as delimited identifiers. \n
			 Pay attention to use only the column names you can see in the tables below. Be careful to not query for columns that do not exist. \n
			 Also, pay attention to which column is in which table. \n
			 Pay attention to use date('now') function to get the current date, if the question involves \"today\". \n
			 Prefix the selected column names with the table name. Make sure all tables of the columns are added to the from clause. \n
			 Make sure the column names are from the right table. Exclude all columns without table entry in the from clause. \n
			 Create only the Sql query. Remove any comment or explanation. \n
			 If unsure, simply state that you don't know. \n
			 Include these columns in the query: {columns} \n
			 Only use the following tables: {schemas};\n
			 %s \n
			""";

	private final String ollamaPrompt = systemPrompt + " Question: {prompt} \n";
	private final String columnMatch = " Join this column: {joinColumn} of this table: {joinTable} where the column has this value: {columnValue}\n";
	@Value("${spring.profiles.active:}")
	private String activeProfile;

	public TableService(ImportClient importClient, ImportService importService, ChatClient chatClient,
			JdbcTemplate jdbcTemplate, TableMetadataRepository tableMetadataRepository,
			DocumentVsRepository documentVsRepository) {
		this.importClient = importClient;
		this.importService = importService;
		this.chatClient = chatClient;
		this.documentVsRepository = documentVsRepository;
		this.tableMetadataRepository = tableMetadataRepository;
		this.jdbcTemplate = jdbcTemplate;
	}

	public SqlRowSet searchTables(SearchDto searchDto) {
		var tableDocuments = this.documentVsRepository.retrieve(searchDto.getSearchString(), MetaData.DataType.TABLE,
				searchDto.getResultAmount());
		var columnDocuments = this.documentVsRepository.retrieve(searchDto.getSearchString(), MetaData.DataType.COLUMN,
				searchDto.getResultAmount());
		List<String> rowSearchStrs = new ArrayList<>();
		if(searchDto.getSearchString().split("[ -.;,]").length > 5) {			
			var tokens = List.of(searchDto.getSearchString().split("[ -.;,]"));		
			for(int i = 0;i<tokens.size();i = i+3) {
				rowSearchStrs.add(tokens.size() <= i + 3 ? "" : tokens.subList(i, tokens.size() >= i +6 ? i+6 : tokens.size()).stream().collect(Collectors.joining(" ")));
			}
		}
		var rowDocuments = rowSearchStrs.stream().filter(myStr -> !myStr.isBlank()) .flatMap(myStr -> this.documentVsRepository.retrieve(myStr, MetaData.DataType.ROW,
				searchDto.getResultAmount()).stream()).toList();

//		LOGGER.info("Table: ");
//		tableDocuments.forEach(myDoc -> LOGGER.info("name: {}, distance: {}",
//				myDoc.getMetadata().get(MetaData.DATANAME), myDoc.getMetadata().get(MetaData.DISTANCE)));
//		LOGGER.info("Column: ");
//		columnDocuments.forEach(myDoc -> LOGGER.info("name: {}, distance: {}",
//				myDoc.getMetadata().get(MetaData.DATANAME), myDoc.getMetadata().get(MetaData.DISTANCE)));
//		LOGGER.info("Row: ");
//		rowDocuments.forEach(
//				myDoc -> LOGGER.info("name: {}, content: {}, distance: {}", myDoc.getMetadata().get(MetaData.DATANAME),
//						myDoc.getContent(), myDoc.getMetadata().get(MetaData.DISTANCE)));

		final Float minRowDistance = rowDocuments.stream()
				.map(myDoc -> (Float) myDoc.getMetadata().getOrDefault(MetaData.DISTANCE, 1.0f)).sorted().findFirst()
				.orElse(1.0f);
		LOGGER.info("MinRowDistance: {}", minRowDistance);
		var sortedRowDocs = rowDocuments.stream().sorted(this.compareDistance()).toList();
		var sortedColumnDocs = columnDocuments.stream().sorted(this.compareDistance()).toList();
		var sortedTableDocs = tableDocuments.stream().sorted(this.compareDistance()).toList();
		SystemPromptTemplate systemPromptTemplate = this.activeProfile.contains("ollama")
				? new SystemPromptTemplate(minRowDistance > MAX_ROW_DISTANCE ? String.format(this.ollamaPrompt, "")
						: String.format(this.ollamaPrompt, columnMatch))
				: new SystemPromptTemplate(minRowDistance > MAX_ROW_DISTANCE ? String.format(this.systemPrompt, "")
						: String.format(this.systemPrompt, columnMatch));
		List<Document> filteredColDocs = sortedColumnDocs.stream()
				.filter(myRowDoc -> sortedTableDocs.stream().limit(3)
						.anyMatch(myTableDoc -> myTableDoc.getMetadata().get(MetaData.TABLE_NAME)
								.equals(myRowDoc.getMetadata().get(MetaData.TABLE_NAME))))
				.filter(StreamHelpers
						.distinctByKey(myRowDoc -> ((String) myRowDoc.getMetadata().get(MetaData.DATANAME))))
				.limit(5).toList();
		Set<String> columnNames = filteredColDocs.stream()
				.map(myDoc -> ((String) myDoc.getMetadata().get(MetaData.DATANAME))).collect(Collectors.toSet());
		List<String> tableMetadataTableNames = filteredColDocs.stream()
				.map(myDoc -> ((String) myDoc.getMetadata().get(MetaData.TABLE_NAME))).distinct().toList();
		record TableNameSchema(String name, String schema) {
		}
		List<TableNameSchema> tableRecords = this.tableMetadataRepository.findByTableNameIn(tableMetadataTableNames)
				.stream()
				.map(tableMetaData -> new TableNameSchema(tableMetaData.getTableName(), tableMetaData.getTableDdl()))
				.collect(Collectors.toList());
		final AtomicReference<String> joinColumn = new AtomicReference<String>("");
		final AtomicReference<String> joinTable = new AtomicReference<String>("");
		final AtomicReference<String> columnValue = new AtomicReference<String>("");
		sortedRowDocs.stream().filter(myDoc -> minRowDistance <= MAX_ROW_DISTANCE).findFirst().ifPresent(myRowDoc -> {
			joinTable.set(((String) myRowDoc.getMetadata().get(MetaData.TABLE_NAME)));
			joinColumn.set(((String) myRowDoc.getMetadata().get(MetaData.DATANAME)));
			columnNames.add(((String) myRowDoc.getMetadata().get(MetaData.DATANAME)));
			columnValue.set(myRowDoc.getContent());
			this.tableMetadataRepository
					.findByTableNameIn(List.of(((String) myRowDoc.getMetadata().get(MetaData.TABLE_NAME)))).stream()
					.map(myTableMetadata -> new TableNameSchema(myTableMetadata.getTableName(),
							myTableMetadata.getTableDdl()))
					.findFirst().ifPresent(myRecord -> tableRecords.add(myRecord));
		});
		Message systemMessage = systemPromptTemplate
				.createMessage(Map.of("columns", columnNames.stream().collect(Collectors.joining(",")), "schemas",
						tableRecords.stream().map(myRecord -> myRecord.schema()).collect(Collectors.joining(";")),
						"prompt", searchDto.getSearchString(), "joinColumn", joinColumn.get(), "joinTable",
						joinTable.get(), "columnValue", columnValue.get()));
		UserMessage userMessage = this.activeProfile.contains("ollama") ? new UserMessage(systemMessage.getContent())
				: new UserMessage(searchDto.getSearchString());
		Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
//		LOGGER.info("Prompt: {}", prompt.getContents());
		var chatStart = new Date();
		ChatResponse response = chatClient.call(prompt);
		String chatResult = response.getResults().stream().map(myGen -> myGen.getOutput().getContent())
				.collect(Collectors.joining(","));
		LOGGER.info("AI response time: {}ms", new Date().getTime() - chatStart.getTime());
		LOGGER.info("AI response: {}", chatResult);
		String sqlQuery = chatResult; 
		sqlQuery = sqlQuery.indexOf("'''") < 0 ? sqlQuery : sqlQuery.substring(sqlQuery.indexOf("'''") + 3);
		sqlQuery = sqlQuery.indexOf("```") < 0 ? sqlQuery : sqlQuery.substring(sqlQuery.indexOf("```") + 3);
		sqlQuery = sqlQuery.indexOf("\"\"\"") < 0 ? sqlQuery : sqlQuery.substring(sqlQuery.indexOf("\"\"\"") + 3);
		sqlQuery = sqlQuery.toLowerCase().indexOf("select") < 0 ? sqlQuery : sqlQuery.substring(sqlQuery.toLowerCase().indexOf("select"));
		sqlQuery = sqlQuery.indexOf(";") < 0 ? sqlQuery : sqlQuery.substring(0, sqlQuery.indexOf(";") + 1);
		LOGGER.info("Sql query: {}", sqlQuery);		
		SqlRowSet rowSet = this.jdbcTemplate.queryForRowSet(sqlQuery);
		return rowSet;
	}

	private Comparator<? super Document> compareDistance() {
		return (myDocA, myDocB) -> ((Float) myDocA.getMetadata().get(MetaData.DISTANCE))
				.compareTo(((Float) myDocB.getMetadata().get(MetaData.DISTANCE)));
	}

	public void importData() {
		var start = new Date();
		LOGGER.info("Import started.");
		List<Artist> artists = this.importClient.importArtists();
		List<Museum> museums = this.importClient.importMuseums();
		List<MuseumHours> museumHours = this.importClient.importMuseumHours();
		List<Work> works = this.importClient.importWorks();
		List<Subject> subjects = this.importClient.importSubjects();
		List<WorkLink> workLinks = this.importClient.importWorkLinks();
		LOGGER.info("Data fetched in {}ms", new Date().getTime() - start.getTime());
		var deleteStart = new Date();
		this.importService.deleteData();
		LOGGER.info("Data deleted in {}ms", new Date().getTime() - deleteStart.getTime());
		var saveStart = new Date();
		this.importService.saveAllData(works, museumHours, museums, artists, subjects, workLinks);
		LOGGER.info("Data saved in {}ms", new Date().getTime() - saveStart.getTime());
		var embeddingsStart = new Date();
		this.updateEmbeddings();
		LOGGER.info("Embeddings updated in {}ms", new Date().getTime() - embeddingsStart.getTime());
		LOGGER.info("Import done in {}ms.", new Date().getTime() - start.getTime());
	}

	private void updateEmbeddings() {
		var columnStart = new Date();
		List<String> ids = this.importService.findAllTableDocuments().stream().map(myDocument -> myDocument.getId())
				.toList();
		this.importService.deleteByIds(ids);
		List<TableMetadata> tablesWithColumns = this.importService.findAllWithColumns();
		Stream<Document> columns = tablesWithColumns.stream().flatMap(myTable -> myTable.getColumnMetadata().stream())
				.map(this::map);
		List<Document> allDocs = Stream.concat(tablesWithColumns.stream().map(this::map), columns).toList();
		this.importService.addDocuments(allDocs);
		LOGGER.info("Column Embeddings updated {}ms", new Date().getTime() - columnStart.getTime());
		var rowStart = new Date();
		List<Document> rowDocs = Stream.concat(
				this.importService.findAllSubjects().stream()
						.filter(mySubject -> Optional.ofNullable(mySubject.getSubject()).stream()
								.allMatch(mySubjectStr -> !mySubjectStr.isBlank()))
						.map(this::map),
				this.importService.findAllWorks().stream().filter(myWork -> Optional.ofNullable(myWork.getStyle())
						.stream().allMatch(myStyle -> !myStyle.isBlank())).map(this::map))
				.toList();
		this.importService.addDocuments(rowDocs);
		LOGGER.info("Row Embeddings updated {}ms", new Date().getTime() - rowStart.getTime());
	}

	private Document map(Work work) {
		var result = new Document(work.getStyle());
		result.getMetadata().put(MetaData.ID, work.getId());
		result.getMetadata().put(MetaData.DATATYPE, MetaData.DataType.ROW.toString());
		result.getMetadata().put(MetaData.DATANAME, "style");
		result.getMetadata().put(MetaData.TABLE_NAME, "museum_hours");
		return result;
	}

	private Document map(Subject subject) {
		var result = new Document(subject.getSubject());
		result.getMetadata().put(MetaData.ID, subject.getWorkId());
		result.getMetadata().put(MetaData.DATATYPE, MetaData.DataType.ROW.toString());
		result.getMetadata().put(MetaData.DATANAME, "subject");
		result.getMetadata().put(MetaData.TABLE_NAME, "subject");
		return result;
	}

	private Document map(ColumnMetadata columnMetadata) {
		var result = new Document(columnMetadata.getColumnDescription());
		result.getMetadata().put(MetaData.ID, columnMetadata.getId().toString());
		result.getMetadata().put(MetaData.DATATYPE, MetaData.DataType.COLUMN.toString());
		result.getMetadata().put(MetaData.DATANAME, columnMetadata.getColumnName());
		result.getMetadata().put(MetaData.TABLE_NAME, columnMetadata.getTableMetadata().getTableName());
		result.getMetadata().put(MetaData.PRIMARY_KEY, columnMetadata.isColumnPrimaryKey());
		Optional.ofNullable(columnMetadata.getReferenceTableName()).stream().filter(myStr -> !myStr.isBlank())
				.findFirst().ifPresent(myStr -> result.getMetadata().put(MetaData.REFERENCE_TABLE, myStr));
		Optional.ofNullable(columnMetadata.getReferenceTableColumn()).stream().filter(myStr -> !myStr.isBlank())
				.findFirst().ifPresent(myStr -> result.getMetadata().put(MetaData.REFERENCE_COLUMN, myStr));
		return result;
	}

	private Document map(TableMetadata tableMetadata) {
		var result = new Document(tableMetadata.getTableDescription());
		result.getMetadata().put(MetaData.ID, tableMetadata.getId().toString());
		result.getMetadata().put(MetaData.DATATYPE, MetaData.DataType.TABLE.toString());
		result.getMetadata().put(MetaData.DATANAME, tableMetadata.getTableName());
		result.getMetadata().put(MetaData.TABLE_NAME, tableMetadata.getTableName());
		result.getMetadata().put(MetaData.PRIMARY_KEY, false);
		result.getMetadata().put(MetaData.TABLE_DDL, tableMetadata.getTableDdl());
		return result;
	}
}
