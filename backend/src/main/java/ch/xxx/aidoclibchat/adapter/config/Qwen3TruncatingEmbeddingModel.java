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

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Qwen3TruncatingEmbeddingModel implements EmbeddingModel {

    private final EmbeddingModel delegate;
    private static final int TARGET_DIMENSION = 2000;

    public Qwen3TruncatingEmbeddingModel(EmbeddingModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        EmbeddingResponse response = this.delegate.call(request);

        // Verarbeitet alle Vektoren in der Antwortschleife
        List<Embedding> truncatedEmbeddings = response.getResults().stream()
                .map(embedding -> {
                    float[] originalVector = embedding.getOutput();
                    float[] processedVector = truncateAndNormalize(originalVector);
                    return new Embedding(processedVector, embedding.getIndex());
                })
                .collect(Collectors.toList());

        return new EmbeddingResponse(truncatedEmbeddings, response.getMetadata());
    }

    @Override
    public float[] embed(Document document) {
        return truncateAndNormalize(this.delegate.embed(document));
    }

    @Override
    public float[] embed(String text) {
        return truncateAndNormalize(this.delegate.embed(text));
    }

    @Override
    public int dimensions() {
        // Meldet 2000 an Spring AI / PgVectorStore, damit das DB-Schema synchron bleibt
        return TARGET_DIMENSION;
    }

    /**
     * Nutzt Qwen3 Matryoshka Funktionalität: Schneidet den Vektor ab
     * und normalisiert ihn mathematisch (L2-Norm) für präzise pgvector-Abfragen.
     */
    private float[] truncateAndNormalize(float[] original) {
        if (original == null) {
            return new float[0];
        }

        // 1. Slice auf exakt 2000 Dimensionen
        float[] sliced = Arrays.copyOf(original, TARGET_DIMENSION);

        // 2. L2-Norm (Euklidische Länge) berechnen
        float sum = 0;
        for (float v : sliced) {
            sum += v * v;
        }
        float norm = (float) Math.sqrt(sum);

        // 3. Vektor auf Einheitslänge skalieren
        if (norm > 0) {
            for (int i = 0; i < sliced.length; i++) {
                sliced[i] /= norm;
            }
        }
        return sliced;
    }
}
