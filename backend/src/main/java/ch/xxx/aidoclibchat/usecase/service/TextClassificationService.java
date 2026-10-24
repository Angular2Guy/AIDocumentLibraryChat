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
import org.springaicommunity.typesafe.TypeSafeClient;
import org.springaicommunity.typesafe.question.Choice;
import org.springaicommunity.typesafe.question.Noul;
import org.springaicommunity.typesafe.question.Question;
import org.springaicommunity.typesafe.response.SystemOneResponse;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TextClassificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextClassificationService.class);
    private static final Map<String, ? extends Question> QUESTIONS = Map.of(
            "category", Choice.builder()
                    .instructions("You are a strict text classification API. Your task is to analyze the user text and classify it into exactly one of these categories.")
                    .option("Politics", "Content related to government, elections, policy, laws")
                    .option("Sport", "Content related to athletics, sports teams, tournaments, or athletes")
                    .option("Technology", "Content related to software, hardware, AI, internet, or gadgets")
                    .option("Entertainment", "Content related to movies, music, celebrities, TV, art")
                    .option("Business", "Content related to companies, markets, finance, trade, or corporate news")
                    .build(),
            "confidenceScore", Noul.of("Is the model highly confident in this category assignment?")
    );
    private static final Map<String, Integer> SCHEMAS = Map.of(
            "Politics", 0,
            "Sport", 1,
            "Technology", 2,
            "Entertainment", 3,
            "Business", 4
    );
    private final TypeSafeClient typeSafeClient;

    public TextClassificationService(TypeSafeClient typeSafeClient) {
        this.typeSafeClient = typeSafeClient;
    }

    public ClassificationSummary classify(List<ClassificationRow> rows) {
        var results = rows.stream().map(this::createResult).toList();
        var classifiedMap = results.stream().collect(Collectors.groupingBy(ClassificationResult::provided, Collectors.mapping(value -> value, Collectors.toList())));
        var summaries = classifiedMap.values().stream().filter(Predicate.not(List::isEmpty))
                .flatMap(classificationResultList -> Stream.of(createResult(classificationResultList))).toList();
        LOGGER.info(new ClassificationSummary(summaries, List.of()).toString());
        return new ClassificationSummary(summaries, results.stream().filter(value -> !value.provided().equals(value.found())).toList());
    }

    private static @NonNull ClassificationResult createResult(List<ClassificationResult> classifiedList) {
        return new ClassificationResult(classifiedList.getFirst().label(), classifiedList.size(),
                Long.valueOf(classifiedList.stream().filter(value -> value.provided().equals(value.found())).count()).intValue(),
                classifiedList.stream().map(ClassificationResult::score).reduce(0.0D, Double::sum) / classifiedList.size(), "");
    }

    private @NonNull ClassificationResult createResult(ClassificationRow row) {
        var start = LocalDateTime.now();
        Map<String, String> state = Map.of("text", row.text());
        SystemOneResponse response = typeSafeClient.systemOne(state, QUESTIONS);
        String predictedCategory = response.choiceValue("category");
        Double confidenceScore = response.noulValue("confidenceScore");
        Integer predictedIndex = SCHEMAS.getOrDefault(predictedCategory, -1);
        Integer expectedIndex = Integer.parseInt(row.label());
        LOGGER.info(String.format("Run for %,d ms", Duration.between(start, LocalDateTime.now()).toMillis())
                + " - Row category: " + SCHEMAS.entrySet().stream().filter(entry -> Integer.valueOf(Integer.parseInt(row.label())).equals(entry.getValue()))
                .map(Map.Entry::getKey).findFirst().orElse("NoMatch") + " - Predicted Category: " + predictedCategory + " - Score: " + confidenceScore);
        return new ClassificationResult(
                predictedCategory,
                expectedIndex,
                predictedIndex,
                confidenceScore,
                expectedIndex.equals(predictedIndex) ? "" : row.text()
        );
    }
}
