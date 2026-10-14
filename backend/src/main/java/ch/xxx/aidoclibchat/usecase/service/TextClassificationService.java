/**
 * Copyright 2023 Sven Loesekann
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.xxx.aidoclibchat.usecase.service;

import ch.xxx.aidoclibchat.domain.model.dto.ClassificationResult;
import ch.xxx.aidoclibchat.domain.model.dto.ClassificationRow;
import ch.xxx.aidoclibchat.domain.model.dto.ClassificationSummary;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Gatherer;

@Service
public class TextClassificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextClassificationService.class);
    private final ChatClient chatClient;

    private final String promptStr = """
            You are a strict text classification API. Your task is to analyze the user text and classify it into exactly one of these categories:
                           - Politics (Content related to government, elections, policy, laws)
                           - Sport (Content related to athletics, sports teams, tournaments, or athletes)
                           - Technology (Content related to software, hardware, AI, internet, or gadgets)
                           - Entertainment (Content related to movies, music, celebrities, TV, art)
                           - Business (Content related to companies, markets, finance, trade, or corporate news)
            
                           You must only choose from these 5 specific strings for the 'category' field.
            
                           Additionally, provide a 'confidenceScore' field as a floating-point number between 0.0 and 1.0, representing how confident you are in your assignment.
            """;

    public TextClassificationService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public ClassificationSummary classify(List<ClassificationRow> rows) {
        var results = rows.stream().map(this::createResult).toList();
        //var summary = results.stream().gather()
        return new ClassificationSummary(List.of(), results);
    }
/*
    public static <T> Gatherer<T, List<T>, List<T>> batchBy() {
        return Gatherer.ofSequential(
                // 1. Initializer: Create the local buffer
                ArrayList::new,

                // 2. Integrator: Consume upcoming elements
                (List<T> state, T element, Gatherer.Downstream<List<T>> downstream) -> {
                    state.add(element);
                    if (state.size() == batchSize) {
                        List<T> fullBatch = List.copyOf(state);
                        state.clear(); // reset local buffer
                        return downstream.push(fullBatch); // push downstream & check if we should continue
                    }
                    return true; // continue stream processing
                },

                // 3. Finisher: Handle remaining dangling items when the source closes
                (List<T> state, Gatherer.Downstream<List<T>> downstream) -> {
                    if (!state.isEmpty()) {
                        downstream.push(List.copyOf(state));
                    }
                }
        );
    }
*/
    private @NonNull ClassificationResult createResult(ClassificationRow row) {
        record ModelClassificationResult(String category, Double confidenceScore) {
            private static final Map<String, Integer> SCHEMAS = Map.of(
                    "Politics", 0,
                    "Sport", 1,
                    "Technology", 2,
                    "Entertainment", 3,
                    "Business", 4
            );
            // Dynamic mapping method to fetch your required 0-4 integer
            public Integer getClassIndex() {
                return SCHEMAS.getOrDefault(category, -1);
            }
        }
        var resultOpt = Optional.ofNullable(this.chatClient.prompt().system(promptStr).user(row.text())
                .call().entity(ModelClassificationResult.class, ChatClient.EntityParamSpec::validateSchema));
        return new ClassificationResult(resultOpt.stream().map(ModelClassificationResult::category).findFirst().orElse("NoMatch"),
                Integer.parseInt(row.label()),
                resultOpt.stream().map(ModelClassificationResult::getClassIndex).findFirst().orElse(-1),
                resultOpt.stream().map(ModelClassificationResult::confidenceScore).findFirst().orElse(0.0D),
                Integer.valueOf(Integer.parseInt(row.label())).equals(resultOpt.stream().map(ModelClassificationResult::getClassIndex).findFirst().orElse(-1)) ? "" : row.text());
    }
}
