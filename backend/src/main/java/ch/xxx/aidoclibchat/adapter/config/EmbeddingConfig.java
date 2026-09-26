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
package ch.xxx.aidoclibchat.adapter.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class EmbeddingConfig {

    @Bean
    public EmbeddingModel embeddingModel() throws Exception {
        // 1. Raw ONNX Modell laden
        TransformersEmbeddingModel onnxModel = new TransformersEmbeddingModel();
        onnxModel.afterPropertiesSet();

        // 2. Mit unserem 2000er-Slicer dekorieren
        return new Qwen3TruncatingEmbeddingModel(onnxModel);
    }

    @Bean
    public PgVectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(2000) // Optional: Falls das Modell die Dimensionen nicht automatisch meldet, hier festlegen
                .build();
    }
}

