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
import org.springframework.ai.retriever.VectorStoreRetriever;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ch.xxx.aidoclibchat.domain.model.entity.DocumentVsRepository;

@Repository
public class DocumentVSRepositoryBean implements DocumentVsRepository {    
    private final VectorStore vectorStore;
    
	public DocumentVSRepositoryBean(JdbcTemplate jdbcTemplate, EmbeddingClient embeddingClient) {				
		this.vectorStore = new PgVectorStore(jdbcTemplate, embeddingClient);
	}
	
	public void add(List<Document> documents) {
		this.vectorStore.add(documents);
	}
	
	public List<Document> retrieve(String query, int k, double threshold) {
		return  new VectorStoreRetriever(vectorStore, k, threshold).retrieve(query);
	}
	
	public List<Document> retrieve(String query, int k) {
		return  new VectorStoreRetriever(vectorStore, k).retrieve(query);
	}
	
	public List<Document> retrieve(String query) {
		return new VectorStoreRetriever(vectorStore).retrieve(query);
	}
}
