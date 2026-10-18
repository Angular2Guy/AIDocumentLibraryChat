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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TextClassificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextClassificationService.class);
    private final ChatClient chatClient;

    private static final String PROMPT_STR = """
            You are a strict text classification API. Your task is to analyze the user text and classify it into exactly one of these categories:
                           - Politics (Content related to government, elections, policy, laws)
                           - Sport (Content related to athletics, sports teams, tournaments, or athletes)
                           - Technology (Content related to software, hardware, AI, internet, or gadgets)
                           - Entertainment (Content related to movies, music, celebrities, TV, art)
                           - Business (Content related to companies, markets, finance, trade, or corporate news)
            
                           You must only choose from these 5 specific strings for the 'category' field.
            
                           Additionally, provide a 'confidenceScore' field as a floating-point number between 0.0 and 1.0, representing how confident you are in your assignment.
            """;

    public TextClassificationService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public ClassificationSummary classify(List<ClassificationRow> rows) {
        var results = rows.stream().map(this::createResult).toList();
        var classifiedMap = results.stream().collect(Collectors.groupingBy(ClassificationResult::provided, Collectors.mapping(value -> value, Collectors.toList())));
        var summaries = classifiedMap.values().stream().filter(List::isEmpty)
                .flatMap(classificationResultList -> Stream.of(createResult(classificationResultList))).toList();
        return new ClassificationSummary(summaries, results);
    }

    private static @NonNull ClassificationResult createResult(List<ClassificationResult> classifiedList) {
        return new ClassificationResult(classifiedList.getFirst().label(), classifiedList.size(),
                Long.valueOf(classifiedList.stream().filter(value -> value.provided().equals(value.found())).count()).intValue(),
                classifiedList.stream().map(ClassificationResult::score).reduce(0.0D, Double::sum) / classifiedList.size(), "");
    }

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
            public String getCategory(Integer id) {
                return SCHEMAS.entrySet().stream().filter(entry -> id.equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse("NoMatch");
            }
        }
        var start = LocalDateTime.now();
        var resultOpt = Optional.ofNullable(this.chatClient.prompt().system(PROMPT_STR).user(row.text())
                .call().entity(ModelClassificationResult.class, ChatClient.EntityParamSpec::validateSchema));
        LOGGER.info(String.format("Run for %,d ms",Duration.between(start, LocalDateTime.now()).toMillis())+" - "+ resultOpt.map(value -> "Row category: "+value.getCategory(Integer.parseInt(row.label()))+" - "+value.toString()).orElse("Optional.empty()"));
        return new ClassificationResult(resultOpt.stream().map(ModelClassificationResult::category).findFirst().orElse("NoMatch"),
                Integer.parseInt(row.label()),
                resultOpt.stream().map(ModelClassificationResult::getClassIndex).findFirst().orElse(-1),
                resultOpt.stream().map(ModelClassificationResult::confidenceScore).findFirst().orElse(0.0D),
                Integer.valueOf(Integer.parseInt(row.label())).equals(resultOpt.stream().map(ModelClassificationResult::getClassIndex).findFirst().orElse(-1)) ? "" : row.text());
    }
}
