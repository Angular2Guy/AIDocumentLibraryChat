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
import ch.xxx.aidoclibchat.domain.model.dto.SearchDto;
import ch.xxx.aidoclibchat.domain.model.entity.Document;
import ch.xxx.aidoclibchat.domain.model.entity.DocumentRepository;
import ch.xxx.aidoclibchat.domain.model.entity.DocumentVsRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class DocumentService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);
	private static final String ID = "id";
	private static final String DISTANCE = "distance";
	private static final Integer CHUNK_TOKEN_LIMIT = 5000;
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
		var tikaDocuments = new TikaDocumentReader(resource).get();
		record TikaDocumentAndContent(org.springframework.ai.document.Document document, String content) {
		}
		var aiDocuments = tikaDocuments.stream()
				.flatMap(myDocument1 -> this.splitStringToTokenLimit(myDocument1.getContent(), CHUNK_TOKEN_LIMIT).stream()
						.map(myStr -> new TikaDocumentAndContent(myDocument1, myStr)))
				.map(myTikaRecord -> new org.springframework.ai.document.Document(myTikaRecord.content(),
						myTikaRecord.document().getMetadata()))
				.peek(myDocument1 -> myDocument1.getMetadata().put(ID, myDocument.getId().toString())).toList();

		LOGGER.info("Name: {}, size: {}, chunks: {}", document.getDocumentName(), document.getDocumentContent().length,
				aiDocuments.size());
		this.documentVsRepository.add(aiDocuments);
		return Optional.ofNullable(myDocument.getDocumentContent()).stream()
				.map(myContent -> Integer.valueOf(myContent.length).longValue()).findFirst().orElse(0L);
	}

	public AiResult queryDocuments(SearchDto searchDto) {
		//LOGGER.info("SearchType: {}", searchDto.getSearchType());
		var similarDocuments = this.documentVsRepository.retrieve(searchDto.getSearchString());
		//LOGGER.info("Documents: {}", similarDocuments.size());
		var mostSimilar = similarDocuments.stream()
				.sorted((myDocA, myDocB) -> ((Float) myDocA.getMetadata().get(DISTANCE))
						.compareTo(((Float) myDocB.getMetadata().get(DISTANCE))))
				.findFirst();
		var documentChunks = mostSimilar.stream()
				.flatMap(mySimilar -> similarDocuments.stream()
						.filter(mySimilar1 -> mySimilar1.getMetadata().get(ID).equals(mySimilar.getMetadata().get(ID))))
				.toList();
		Message systemMessage = switch (searchDto.getSearchType()) {
		case SearchDto.SearchType.DOCUMENT -> this.getSystemMessage(documentChunks,
				(documentChunks.size() <= 0 ? 2000 : Math.floorDiv(2000, documentChunks.size())));
		case SearchDto.SearchType.PARAGRAPH -> this.getSystemMessage(mostSimilar.stream().toList(), 2000);
		};
		UserMessage userMessage = new UserMessage(searchDto.getSearchString());
		Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
		LocalDateTime start = LocalDateTime.now();
		AiResponse response = aiClient.generate(prompt);
		LOGGER.info("AI response time: {}ms",
				ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli()
						- ZonedDateTime.of(start, ZoneId.systemDefault()).toInstant().toEpochMilli());
		var documents = mostSimilar.stream().map(myGen -> myGen.getMetadata().get(ID))
				.filter(myId -> Optional.ofNullable(myId).stream().allMatch(myId1 -> (myId1 instanceof String)))
				.map(myId -> Long.parseLong(((String) myId))).map(myId -> this.documentRepository.findById(myId))
				.filter(Optional::isPresent).map(Optional::get).toList();
		return new AiResult(searchDto.getSearchString(), response.getGenerations(), documents);
	}

	private Message getSystemMessage(List<org.springframework.ai.document.Document> similarDocuments, int tokenLimit) {
		String documents = similarDocuments.stream().map(entry -> entry.getContent())
				.filter(myStr -> myStr != null && !myStr.isBlank())
				.map(myStr -> this.cutStringToTokenLimit(myStr, tokenLimit)).collect(Collectors.joining("\n"));
		SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(this.systemPrompt);
		Message systemMessage = systemPromptTemplate.createMessage(Map.of("documents", documents));
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