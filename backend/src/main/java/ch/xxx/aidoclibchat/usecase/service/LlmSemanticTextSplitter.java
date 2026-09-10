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

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LlmSemanticTextSplitter implements DocumentTransformer {

    private final ChatModel chatModel;

    // Define the system instructions guiding the LLM where to place the tags
    private static final String SYSTEM_PROMPT = """
            You are an expert text segmentation assistant. 
            Your job is to read the provided text and insert the tag '<split-here/>' at every point where the semantic topic or context changes.
            Do not alter any of the original wording, punctuation, or formatting. Only insert the tags.
            Return ONLY the modified text containing the original content and the inserted tags. Do not include markdown code blocks or introduction/conclusion phrases.
            """;

    public LlmSemanticTextSplitter(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public List<Document> apply(List<Document> documents) {
        // Process each document, split them by the tag, and flatten into a final list
        return documents.stream()
                .flatMap(doc -> splitDocumentSemantically(doc).stream())
                .collect(Collectors.toList());
    }

    private List<Document> splitDocumentSemantically(Document originalDoc) {
        // 1. Construct the prompt with instructions and the raw content
        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(SYSTEM_PROMPT);
        var systemMessage = systemTemplate.createMessage();

        // Pass the document text as the user message
        var userMessage = new org.springframework.ai.chat.messages.UserMessage(originalDoc.getText());
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        // 2. Call the LLM
        String taggedText = chatModel.call(prompt).getResult().getOutput().getText();

        // 3. Clean up potential markdown formatting the LLM might have wrapped around the response
        if (taggedText.startsWith("```") && taggedText.endsWith("```")) {
            taggedText = taggedText.replaceAll("^```[a-zA-Z]*\\n|\\n```$", "");
        }

        // 4. Split the tagged string into separate chunks
        String[] chunks = taggedText.split("<split-here/>");

        // 5. Convert chunks back to individual Documents, preserving original metadata
        return Arrays.stream(chunks)
                .map(String::trim)
                .filter(text -> !text.isEmpty())
                .map(text -> new Document(text, originalDoc.getMetadata()))
                .collect(Collectors.toList());
    }
}
