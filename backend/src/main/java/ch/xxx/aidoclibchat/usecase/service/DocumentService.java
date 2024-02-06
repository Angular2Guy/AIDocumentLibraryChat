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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import ch.xxx.aidoclibchat.domain.common.MetaData;
import ch.xxx.aidoclibchat.domain.common.MetaData.DataType;
import ch.xxx.aidoclibchat.domain.model.dto.AiDocumentResult;
import ch.xxx.aidoclibchat.domain.model.dto.SearchDto;
import ch.xxx.aidoclibchat.domain.model.entity.Document;
import ch.xxx.aidoclibchat.domain.model.entity.DocumentRepository;
import ch.xxx.aidoclibchat.domain.model.entity.DocumentVsRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class DocumentService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);
	private final DocumentRepository documentRepository;
	private final DocumentVsRepository documentVsRepository;
	private final ChatClient chatClient;
	private final String systemPrompt = """
			You're assisting with questions about documents in a catalog.\n 
			Use the information from the DOCUMENTS section to provide accurate answers.\n 
			If unsure, simply state that you don't know.\n" + "\n" + "DOCUMENTS:\n" + "{documents} 
			""";

	private final String ollamaPrompt = """ 
			You're assisting with questions about documents in a catalog.\n 
			Use the information from the DOCUMENTS section to provide accurate answers.\n 
			If unsure, simply state that you don't know.\n \n" + " {prompt} \n \n" + "DOCUMENTS:\n" + "{documents} 
			""";

	@Value("${embedding-token-limit:1000}")
	private Integer embeddingTokenLimit;
	@Value("${document-token-limit:1000}")
	private Integer documentTokenLimit;
	@Value("${spring.profiles.active:}")
	private String activeProfile;

	public DocumentService(DocumentRepository documentRepository, DocumentVsRepository documentVsRepository,
			ChatClient chatClient) {
		this.documentRepository = documentRepository;
		this.documentVsRepository = documentVsRepository;
		this.chatClient = chatClient;
	}

	@PostConstruct
	public void init() {
		LOGGER.info("Profile: {}", this.activeProfile);
	}

	public Long storeDocument(Document document) {
		var myDocument = this.documentRepository.save(document);
		Resource resource = new ByteArrayResource(document.getDocumentContent());
		var tikaDocuments = new TikaDocumentReader(resource).get();
		record TikaDocumentAndContent(org.springframework.ai.document.Document document, String content) {
		}
		var aiDocuments = tikaDocuments.stream()
				.flatMap(myDocument1 -> this.splitStringToTokenLimit(myDocument1.getContent(), embeddingTokenLimit)
						.stream().map(myStr -> new TikaDocumentAndContent(myDocument1, myStr)))
				.map(myTikaRecord -> new org.springframework.ai.document.Document(myTikaRecord.content(),
						myTikaRecord.document().getMetadata()))
				.peek(myDocument1 -> myDocument1.getMetadata().put(MetaData.ID, myDocument.getId().toString()))
				.peek(myDocument1 -> myDocument1.getMetadata().put(MetaData.DATATYPE,
						MetaData.DataType.DOCUMENT.toString()))
				.toList();

		LOGGER.info("Name: {}, size: {}, chunks: {}", document.getDocumentName(), document.getDocumentContent().length,
				aiDocuments.size());
		this.documentVsRepository.add(aiDocuments);
		return Optional.ofNullable(myDocument.getDocumentContent()).stream()
				.map(myContent -> Integer.valueOf(myContent.length).longValue()).findFirst().orElse(0L);
	}

	public AiDocumentResult queryDocuments(SearchDto searchDto) {
		// LOGGER.info("SearchType: {}", searchDto.getSearchType());
		var similarDocuments = this.documentVsRepository.retrieve(searchDto.getSearchString(),
				MetaData.DataType.DOCUMENT, searchDto.getResultAmount());
		// LOGGER.info("Documents: {}", similarDocuments.size());
		var mostSimilarDocs = similarDocuments.stream()
				.filter(myDoc -> myDoc.getMetadata().get(MetaData.DATATYPE).equals(DataType.DOCUMENT.toString()))
				.sorted((myDocA, myDocB) -> ((Float) myDocA.getMetadata().get(MetaData.DISTANCE))
						.compareTo(((Float) myDocB.getMetadata().get(MetaData.DISTANCE))))
				.limit(searchDto.getResultAmount()).toList();
		var mostSimilar = mostSimilarDocs.stream().findFirst();
		var documentChunks = mostSimilar.stream()
				.flatMap(mySimilar -> similarDocuments.stream().filter(mySimilar1 -> mySimilar1.getMetadata()
						.get(MetaData.ID).equals(mySimilar.getMetadata().get(MetaData.ID))))
				.toList();
		Message systemMessage = switch (searchDto.getSearchType()) {
		case SearchDto.SearchType.DOCUMENT ->
			this.getSystemMessage(documentChunks, this.documentTokenLimit, searchDto.getSearchString());
		case SearchDto.SearchType.PARAGRAPH ->
			this.getSystemMessage(mostSimilar.stream().toList(), this.documentTokenLimit, searchDto.getSearchString());
		default -> this.getSystemMessage(documentChunks, this.documentTokenLimit, searchDto.getSearchString());
		};
		UserMessage userMessage = this.activeProfile.contains("ollama") ? new UserMessage(systemMessage.getContent()) : new UserMessage(searchDto.getSearchString());
		Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
		LocalDateTime start = LocalDateTime.now();
		ChatResponse response = chatClient.call(prompt);
		LOGGER.info("AI response time: {}ms",
				ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli()
						- ZonedDateTime.of(start, ZoneId.systemDefault()).toInstant().toEpochMilli());
		var documents = mostSimilar.stream().map(myGen -> myGen.getMetadata().get(MetaData.ID))
				.filter(myId -> Optional.ofNullable(myId).stream().allMatch(myId1 -> (myId1 instanceof String)))
				.map(myId -> Long.parseLong(((String) myId))).map(this.documentRepository::findById)
				.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
		var docIds = mostSimilarDocs.stream()
				.filter(myDoc -> !mostSimilar.stream().anyMatch(
						myDoc1 -> myDoc1.getMetadata().get(MetaData.ID).equals(myDoc.getMetadata().get(MetaData.ID))))
				.map(myDoc -> myDoc.getMetadata().get(MetaData.ID)).filter(myId -> myId instanceof String)
				.map(idStr -> Long.valueOf((String) idStr)).toList();
		documents.addAll(this.documentRepository.findAllById(docIds));

		return new AiDocumentResult(searchDto.getSearchString(), response.getResults(), documents);
	}

	private Message getSystemMessage(List<org.springframework.ai.document.Document> similarDocuments, int tokenLimit,
			String prompt) {
		String documentStr = this.cutStringToTokenLimit(
				similarDocuments.stream().map(entry -> entry.getContent())
						.filter(myStr -> myStr != null && !myStr.isBlank()).collect(Collectors.joining("\n")),
				tokenLimit);
		SystemPromptTemplate systemPromptTemplate = this.activeProfile.contains("ollama")
				? new SystemPromptTemplate(this.ollamaPrompt)
				: new SystemPromptTemplate(this.systemPrompt);
		Message systemMessage = systemPromptTemplate.createMessage(Map.of("documents", documentStr, "prompt", prompt));
		return systemMessage;
	}

	private List<String> splitStringToTokenLimit(String documentStr, int tokenLimit) {
		List<String> splitStrings = new ArrayList<>();
		var tokens = new StringTokenizer(documentStr).countTokens();
		var chunks = Math.ceilDiv(tokens, tokenLimit);
		if (chunks == 0) {
			return splitStrings;
		}
		var chunkSize = Math.ceilDiv(documentStr.length(), chunks);
		var myDocumentStr = new String(documentStr);
		while (!myDocumentStr.isBlank()) {
			splitStrings
					.add(myDocumentStr.length() > chunkSize ? myDocumentStr.substring(0, chunkSize) : myDocumentStr);
			myDocumentStr = myDocumentStr.length() > chunkSize ? myDocumentStr.substring(chunkSize) : "";
		}
		return splitStrings;
	}

	private String cutStringToTokenLimit(String documentStr, int tokenLimit) {
		String cutString = new String(documentStr);
		while (tokenLimit < new StringTokenizer(cutString, " -.;,").countTokens()) {
			cutString = cutString.length() > 100 ? cutString.substring(0, cutString.length() - 100) : "";
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