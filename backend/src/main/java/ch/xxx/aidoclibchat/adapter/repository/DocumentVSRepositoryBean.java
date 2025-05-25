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
package ch.xxx.aidoclibchat.adapter.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.postgresql.util.PGobject;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.Filter.ExpressionType;
import org.springframework.ai.vectorstore.filter.Filter.Key;
import org.springframework.ai.vectorstore.filter.Filter.Value;
import org.springframework.ai.vectorstore.filter.FilterExpressionConverter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgvector.PGvector;

import ch.xxx.aidoclibchat.domain.common.MetaData;
import ch.xxx.aidoclibchat.domain.common.MetaData.DataType;
import ch.xxx.aidoclibchat.domain.model.entity.DocumentVsRepository;

@Repository
public class DocumentVSRepositoryBean implements DocumentVsRepository {
	private final String vectorTableName;
	private final VectorStore vectorStore;
	private final JdbcTemplate jdbcTemplate;
	private final ObjectMapper objectMapper;
	private final FilterExpressionConverter filterExpressionConverter;

	public DocumentVSRepositoryBean(JdbcTemplate jdbcTemplate, @Qualifier("embeddingModel") EmbeddingModel embeddingClient,
			ObjectMapper objectMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.objectMapper = objectMapper;
		this.vectorStore = PgVectorStore.builder(jdbcTemplate, embeddingClient).build();
		this.filterExpressionConverter = ((PgVectorStore) this.vectorStore).filterExpressionConverter;
		this.vectorTableName = PgVectorStore.DEFAULT_TABLE_NAME;
	}

	@Override
	public void add(List<Document> documents) {
		this.vectorStore.add(documents);
	}

	@Override
	public List<Document> retrieve(String query, DataType dataType, int k, double threshold) {
		return this.vectorStore.similaritySearch(SearchRequest
				.builder().query(query).filterExpression(new Filter.Expression(ExpressionType.EQ,
						new Key(MetaData.DATATYPE), new Value(dataType.toString())))
				.topK(k).similarityThreshold(threshold).build());

	}

	@Override
	public List<Document> retrieve(String query, DataType dataType, int k) {
		return this.vectorStore.similaritySearch(SearchRequest.builder().query(query).filterExpression(
				new Filter.Expression(ExpressionType.EQ, new Key(MetaData.DATATYPE), new Value(dataType.toString())))
				.topK(k).build());
	}

	@Override
	public List<Document> retrieve(String query, DataType dataType) {
		return this.vectorStore.similaritySearch(SearchRequest.builder().query(query).filterExpression(
				new Filter.Expression(ExpressionType.EQ, new Key(MetaData.DATATYPE), new Value(dataType.toString())))
				.build());
	}

	@Override
	public List<Document> findAllTableDocuments() {
		String nativeFilterExpression = this.filterExpressionConverter.convertExpression(new Filter.Expression(
				ExpressionType.NE, new Key(MetaData.DATATYPE), new Value(DataType.DOCUMENT.toString())));

		String jsonPathFilter = " WHERE metadata::jsonb @@ '" + nativeFilterExpression + "'::jsonpath ";

		return this.jdbcTemplate.query(
				String.format("SELECT * FROM %s %s LIMIT ? ", this.vectorTableName, jsonPathFilter),
				new DocumentRowMapper(this.objectMapper), 100000);
	}

	@Override
	public void deleteByIds(List<String> ids) {
		this.vectorStore.delete(ids);
	}

	private static class DocumentRowMapper implements RowMapper<Document> {

		private static final String COLUMN_EMBEDDING = "embedding";

		private static final String COLUMN_METADATA = "metadata";

		private static final String COLUMN_ID = "id";

		private static final String COLUMN_CONTENT = "content";

		private final ObjectMapper objectMapper;

		public DocumentRowMapper(ObjectMapper objectMapper) {
			this.objectMapper = objectMapper;
		}

		@Override
		public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
			String id = rs.getString(COLUMN_ID);
			String content = rs.getString(COLUMN_CONTENT);
			PGobject pgMetadata = rs.getObject(COLUMN_METADATA, PGobject.class);
			PGobject embedding = rs.getObject(COLUMN_EMBEDDING, PGobject.class);

			Map<String, Object> metadata = toMap(pgMetadata);
			metadata.put(COLUMN_EMBEDDING, this.toDoubleArray(embedding));

			Document document = new Document(id, content, metadata);

			return document;
		}

		private float[] toDoubleArray(PGobject embedding) throws SQLException {
			return new PGvector(embedding.getValue()).toArray();
		}

		@SuppressWarnings("unchecked")
		private Map<String, Object> toMap(PGobject pgObject) {

			String source = pgObject.getValue();
			try {
				return (Map<String, Object>) objectMapper.readValue(source, Map.class);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
