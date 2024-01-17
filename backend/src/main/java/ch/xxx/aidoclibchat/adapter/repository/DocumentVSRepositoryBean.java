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

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.Filter.ExpressionType;
import org.springframework.ai.vectorstore.filter.Filter.Key;
import org.springframework.ai.vectorstore.filter.Filter.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ch.xxx.aidoclibchat.domain.common.MetaData;
import ch.xxx.aidoclibchat.domain.common.MetaData.DataType;
import ch.xxx.aidoclibchat.domain.model.entity.DocumentVsRepository;

@Repository
public class DocumentVSRepositoryBean implements DocumentVsRepository {
	private final VectorStore vectorStore;

	public DocumentVSRepositoryBean(JdbcTemplate jdbcTemplate, EmbeddingClient embeddingClient) {
		this.vectorStore = new PgVectorStore(jdbcTemplate, embeddingClient);
	}

	@Override
	public void add(List<Document> documents) {
		this.vectorStore.add(documents);
	}

	@Override
	public List<Document> retrieve(String query, DataType dataType, int k, double threshold) {
		return this.vectorStore.similaritySearch(SearchRequest.query(query)
				.withFilterExpression(
						new Filter.Expression(ExpressionType.EQ, new Key(MetaData.DATATYPE), new Value(dataType.toString())))
				.withTopK(k).withSimilarityThreshold(threshold));
	}

	@Override
	public List<Document> retrieve(String query, DataType dataType, int k) {
		return this.vectorStore.similaritySearch(SearchRequest.query(query)
				.withFilterExpression(
						new Filter.Expression(ExpressionType.EQ, new Key(MetaData.DATATYPE), new Value(dataType.toString())))
				.withTopK(k));
	}

	@Override
	public List<Document> retrieve(String query, DataType dataType) {
		return this.vectorStore.similaritySearch(SearchRequest.query(query).withFilterExpression(
				new Filter.Expression(ExpressionType.EQ, new Key(MetaData.DATATYPE), new Value(dataType.toString()))));
	}

	@Override
	public List<Document> findAllTableDocuments() {
		return this.vectorStore.similaritySearch(SearchRequest.defaults().withSimilarityThresholdAll().withTopK(Integer.MAX_VALUE).withFilterExpression(new Filter.Expression(
				ExpressionType.OR,
				new Filter.Expression(ExpressionType.EQ, new Key(MetaData.DATATYPE), new Value(DataType.COLUMN.toString())),
				new Filter.Expression(ExpressionType.EQ, new Key(MetaData.DATATYPE), new Value(DataType.TABLE.toString())))));
	}
	
	@Override
	public void deleteByIds(List<String> ids) {
		this.vectorStore.delete(ids);
	}
}
