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

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.embedding.EmbeddingModel;
import java.util.ArrayList;
import java.util.List;

public class VectorSemanticTextSplitter implements DocumentTransformer {

    private final EmbeddingModel embeddingModel;
    private final double similarityThreshold; // e.g., 0.7

    public VectorSemanticTextSplitter(EmbeddingModel embeddingModel, double similarityThreshold) {
        this.embeddingModel = embeddingModel;
        this.similarityThreshold = similarityThreshold;
    }

    @Override
    public List<Document> apply(List<Document> documents) {
        List<Document> chunkedDocs = new ArrayList<>();

        for (Document doc : documents) {
            // 1. Split text into simple sentences (Regex or BreakIterator)
            String[] sentences = doc.getText().split("(?<=[.!?])\\s+");
            if (sentences.length == 0) continue;

            // 2. Compute embeddings for all sentences
            List<float[]> embeddings = embeddingModel.embed(List.of(sentences));

            StringBuilder currentChunk = new StringBuilder(sentences[0]);

            for (int i = 1; i < sentences.length; i++) {
                // 3. Compute cosine similarity between sentence (i-1) and sentence (i)
                double similarity = cosineSimilarity(embeddings.get(i - 1), embeddings.get(i));

                // 4. Break chunk if similarity drops below threshold
                if (similarity < similarityThreshold) {
                    chunkedDocs.add(new Document(currentChunk.toString(), doc.getMetadata()));
                    currentChunk = new StringBuilder(sentences[i]);
                } else {
                    currentChunk.append(" ").append(sentences[i]);
                }
            }
            // Add the final remaining chunk
            if (!currentChunk.isEmpty()) {
                chunkedDocs.add(new Document(currentChunk.toString(), doc.getMetadata()));
            }
        }
        return chunkedDocs;
    }

    private double cosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
