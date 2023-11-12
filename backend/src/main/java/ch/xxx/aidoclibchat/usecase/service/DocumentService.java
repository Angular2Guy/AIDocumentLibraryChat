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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.client.AiClient;
import org.springframework.ai.client.AiResponse;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.SystemPromptTemplate;
import org.springframework.ai.prompt.messages.Message;
import org.springframework.ai.prompt.messages.UserMessage;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import ch.xxx.aidoclibchat.domain.model.dto.AiResult;
import ch.xxx.aidoclibchat.domain.model.entity.Document;
import ch.xxx.aidoclibchat.domain.model.entity.DocumentRepository;
import ch.xxx.aidoclibchat.domain.model.entity.DocumentVsRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class DocumentService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);
	private static final String ID = "id";
	private final DocumentRepository documentRepository;
	private final DocumentVsRepository documentVsRepository;
	private final AiClient aiClient;
	private String systemPrompt = "You're assisting with questions about documents in a catalog.\n"
			+ "Use the information from the DOCUMENTS section to provide accurate answers.\n"
			+ "If unsure, simply state that you don't know.\n" + "\n" + "DOCUMENTS:\n" + "{documents}";

	public DocumentService(DocumentRepository documentRepository, DocumentVsRepository documentVsRepository,
			AiClient aiClient) {
		this.documentRepository = documentRepository;
		this.documentVsRepository = documentVsRepository;
		this.aiClient = aiClient;
	}

	public Long storeDocument(Document document) {
		var myDocument = this.documentRepository.save(document);
		Resource resource = new ByteArrayResource(document.getDocumentContent());
		var documents = new TikaDocumentReader(resource).get();
		documents = documents.stream().flatMap(myDocument1 -> {
			myDocument1.getMetadata().put(ID, myDocument.getId());
			return Stream.of(myDocument1);
		}).toList();
		LOGGER.info("Name: {}, size: {}", document.getDocumentName(), documents.size());
		this.documentVsRepository.add(documents);
		return Optional.ofNullable(myDocument.getDocumentContent()).stream()
				.map(myContent -> Integer.valueOf(myContent.length).longValue()).findFirst().orElse(0L);
	}

	public AiResult queryDocuments(String query) {
		var similarDocuments = this.documentVsRepository.retrieve(query);
		Message systemMessage = this.getSystemMessage(similarDocuments, 2500);
		UserMessage userMessage = new UserMessage(query);
		Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
		AiResponse response = aiClient.generate(prompt);
		var documents = response.getGenerations().stream().map(myGen -> myGen.getInfo().get(ID))
				.filter(myId -> (myId instanceof Long)).map(myId -> this.documentRepository.findById((Long) myId))
				.filter(Optional::isPresent).map(Optional::get).toList();
		return new AiResult(query, response.getGenerations(), documents);
	}

	private Message getSystemMessage(List<org.springframework.ai.document.Document> similarDocuments, int tokenLimit) {
		String documents = similarDocuments.stream().map(entry -> entry.getContent())
				.filter(myStr -> myStr != null && !myStr.isBlank())
				.map(myStr -> this.cutStringToTokenLimit(myStr, tokenLimit)).collect(Collectors.joining("\n"));
		SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(this.systemPrompt);
		Message systemMessage = systemPromptTemplate.createMessage(Map.of("documents", documents));
		return systemMessage;

	}

	private String cutStringToTokenLimit(String documentStr, int tokenLimit) {
		String cutString = documentStr;
		while (tokenLimit < new StringTokenizer(cutString, " -.;,").countTokens()) {
			cutString = cutString.length() > 1000 ? cutString.substring(0, cutString.length() - 1000) : "";
		}
		return cutString;
	}

	public List<Document> getDocumentList() {
		return this.documentRepository.findAll();
	}

	public Optional<Document> getDocumentById(Long id) {
		return this.documentRepository.findById(id);
	}
}