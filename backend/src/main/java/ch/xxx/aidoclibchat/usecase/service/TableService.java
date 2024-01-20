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

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.document.Document;
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
import ch.xxx.aidoclibchat.domain.model.entity.Work;
import ch.xxx.aidoclibchat.domain.model.entity.WorkLink;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TableService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TableService.class);
	private final ImportClient importClient;
	private final ImportService importService;
	private final DocumentVsRepository documentVsRepository;
	private final ChatClient chatClient;
	private final String systemPrompt = "You're assisting with questions about documents in a catalog.\n"
			+ "Use the information from the DOCUMENTS section to provide accurate answers.\n"
			+ "If unsure, simply state that you don't know.\n" + "\n" + "DOCUMENTS:\n" + "{documents}";

	private final String ollamaPrompt = "You're assisting with questions about documents in a catalog.\n"
			+ "Use the information from the DOCUMENTS section to provide accurate answers.\n"
			+ "If unsure, simply state that you don't know.\n \n" + " {prompt} \n \n" + "DOCUMENTS:\n" + "{documents}";

	public TableService(ImportClient importClient, ImportService importService, ChatClient chatClient,
			DocumentVsRepository documentVsRepository) {
		this.importClient = importClient;
		this.importService = importService;
		this.chatClient = chatClient;
		this.documentVsRepository = documentVsRepository;
	}

	public void searchTables(SearchDto searchDto) {
		var tableDocuments = this.documentVsRepository.retrieve(searchDto.getSearchString(), MetaData.DataType.TABLE,
				searchDto.getResultAmount());
		var columnDocuments = this.documentVsRepository.retrieve(searchDto.getSearchString(), MetaData.DataType.COLUMN,
				searchDto.getResultAmount());
		var rowDocuments = this.documentVsRepository.retrieve(searchDto.getSearchString(), MetaData.DataType.ROW,
				searchDto.getResultAmount());
		LOGGER.info("Table: ");
		tableDocuments.forEach(myDoc -> LOGGER.info("name: {}, distance: {}",
				myDoc.getMetadata().get(MetaData.DATANAME), myDoc.getMetadata().get(MetaData.DISTANCE)));
		LOGGER.info("Column: ");
		columnDocuments.forEach(myDoc -> LOGGER.info("name: {}, distance: {}",
				myDoc.getMetadata().get(MetaData.DATANAME), myDoc.getMetadata().get(MetaData.DISTANCE)));
		LOGGER.info("Row: ");
		rowDocuments.forEach(myDoc -> LOGGER.info("name: {}, content: {}, distance: {}", myDoc.getMetadata().get(MetaData.DATANAME), myDoc.getContent(),
				myDoc.getMetadata().get(MetaData.DISTANCE)));
	}

	@Async
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
		return result;
	}

	private Document map(Subject subject) {
		var result = new Document(subject.getSubject());
		result.getMetadata().put(MetaData.ID, subject.getWorkId());
		result.getMetadata().put(MetaData.DATATYPE, MetaData.DataType.ROW.toString());
		result.getMetadata().put(MetaData.DATANAME, "subject");
		return result;
	}

	private Document map(ColumnMetadata columnMetadata) {
		var result = new Document(columnMetadata.getColumnDescription());
		result.getMetadata().put(MetaData.ID, columnMetadata.getId().toString());
		result.getMetadata().put(MetaData.DATATYPE, MetaData.DataType.COLUMN.toString());
		result.getMetadata().put(MetaData.DATANAME, columnMetadata.getColumnName());
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
		result.getMetadata().put(MetaData.PRIMARY_KEY, false);
		result.getMetadata().put(MetaData.TABLE_DDL, tableMetadata.getTableDdl());
		return result;
	}
}
