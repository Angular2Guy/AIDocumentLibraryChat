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
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ch.xxx.aidoclibchat.domain.common.MetaData;
import ch.xxx.aidoclibchat.domain.common.MetaData.DataType;
import ch.xxx.aidoclibchat.domain.model.dto.AiDocumentResult;
import ch.xxx.aidoclibchat.domain.model.dto.ChapterHeading;
import ch.xxx.aidoclibchat.domain.model.dto.SearchDto;
import ch.xxx.aidoclibchat.domain.model.entity.Book;
import ch.xxx.aidoclibchat.domain.model.entity.BookRepository;
import ch.xxx.aidoclibchat.domain.model.entity.Chapter;
import ch.xxx.aidoclibchat.domain.model.entity.ChapterRepository;
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
	private final BookRepository bookRepository;
	private final ChapterRepository chapterRepository;
	private final ChatClient chatClient;
	private final LlmSemanticTextSplitter llmSemanticTextSplitter;
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

	private final String bookPrompt = """
			You're an english professor and expert in long text summaries. Your job is creating a summary of a text.\n
			Create a summary in bullit points of the most important points of the text. Create a short and precise description of the most important points. \n
			Write the summary only in english language and grammar. \n

			Follow these Rules:
			-If a new character is mentioned introduce the character. \n
			-Write only in english language and grammar. \n
			-Write  in third person. \n
			-Write in present tense. \n
			-Address characters by name. Do not use words like narrator or protagonist. \n
			-Write the summary in short and precise bullet points. \n
			-Write as few and precise bullet points as possible. \n
			-Write short logical grounded descriptions of the most important points in the text. \n

			TEXT: {text}
			""";

	@Value("${embedding-token-limit:1000}")
	private Integer embeddingTokenLimit;
	@Value("${document-token-limit:1000}")
	private Integer documentTokenLimit;
	@Value("${spring.profiles.active:}")
	private String activeProfile;
	private Integer documentWordLimit;

	public DocumentService(DocumentRepository documentRepository, DocumentVsRepository documentVsRepository,
						   LlmSemanticTextSplitter llmSemanticTextSplitter, ChatClient.Builder builder,
						   BookRepository bookRepository, ChapterRepository chapterRepository) {
		this.documentRepository = documentRepository;
		this.documentVsRepository = documentVsRepository;
		this.chatClient = builder.build();
		this.bookRepository = bookRepository;
		this.chapterRepository = chapterRepository;
		this.llmSemanticTextSplitter = llmSemanticTextSplitter;
	}

	@PostConstruct
	public void init() {
		LOGGER.info("Profile: {}", this.activeProfile);
		this.documentWordLimit = Long.valueOf(Math.round(this.documentTokenLimit * 0.7)).intValue();
		LOGGER.info("Documents word limit: {}", this.documentWordLimit);
	}

	public Book storeBook(Book book, List<ChapterHeading> chapterHeadings) {
		var tikaText = new TikaDocumentReader(new ByteArrayResource(book.getBookFile())).get().stream()
				.map(org.springframework.ai.document.Document::getFormattedContent).collect(Collectors.joining("")).lines().toList();
		book.setTitle(tikaText.stream().filter(myLine -> myLine.contains("Title:"))
				.map(myLine -> myLine.replace("Title:", "").trim()).findFirst().orElse("Unknown"));
		book.setAuthor(tikaText.stream().filter(myLine -> myLine.contains("Author:"))
				.map(myLine -> myLine.replace("Author:", "").trim()).findFirst().orElse("Unknown"));
		var myBook = this.bookRepository.save(book);
		// TODO split the content of the one tikaDocument
		int dropLines = 0;
		for (int i = 0; i < tikaText.size(); i++) {
			dropLines = tikaText.get(i).equalsIgnoreCase(chapterHeadings.getFirst().title()) ? i : dropLines;
		}
		var atomicRef = new AtomicReference<List<String>>(
				tikaText.stream().skip(dropLines > 0 ? dropLines - 1 : 0).toList());
		var myChapters = chapterHeadings.stream()
				.filter(heading -> !chapterHeadings.getFirst().title().equals(heading.title()))
				.flatMap(heading -> Stream.of(this.createChapter(myBook, heading.title(), atomicRef))).toList();
		myChapters = this.chapterRepository.saveAll(myChapters);
		// LOGGER.info(myChapters.getLast().getChapterText());
		myBook.getChapters().addAll(myChapters);
		return myBook;
	}

	public Optional<Book> findBookByUuid(String uuidStr) {
		return this.bookRepository.findById(UUID.fromString(uuidStr));
	}

	public List<Book> findBooksByTitleAuthor(String titleAuthor) {
		return Optional.ofNullable(titleAuthor).stream().filter(myStr -> myStr.trim().length() > 2)
				.map(String::toLowerCase)
				.flatMap(myStr -> Stream.of(this.bookRepository.findByTitleAuthorWithChapters(myStr)))
				.flatMap(List::stream).toList();
	}

	@Async
	public void addBookSummaries(Book book) {
		var myChapters = book.getChapters().stream().map(myChapter -> this.addChapterSummary(myChapter)).toList();
		// LOGGER.info(myChapters.getLast().getSummary());
		var summaries = myChapters.stream().map(Chapter::getChapterText)
				.reduce((acc, myChapter) -> acc + "\n" + myChapter);
		book.setSummary(this.chatClient.prompt().user(u -> u.text(this.bookPrompt).params(Map.of("text", summaries)))
				.call().content());
		// LOGGER.info(myBook.getSummary());
		LOGGER.info("Summary generated file: {}", book.getTitle());
		this.bookRepository.save(book);
	}

	private Chapter addChapterSummary(final Chapter myChapter) {
		var answer = this.chatClient.prompt()
				.user(u -> u.text(this.bookPrompt).params(Map.of("text", myChapter.getChapterText()))).call().content();
		myChapter.setSummary(answer);
		var resultChapter = this.chapterRepository.save(myChapter);
		LOGGER.info("Summary generated for: {}", resultChapter.getTitle());
		return resultChapter;
	}

	private Chapter createChapter(Book book, String heading, AtomicReference<List<String>> atomicRef) {
		var result = new Chapter();
		result.setTitle(atomicRef.get().stream().filter(Predicate.not(String::isBlank)).findFirst().orElse(""));
		result.setBook(book);
		var chapterText = atomicRef.get().stream().takeWhile(myLine -> !myLine.contains(heading))
				.collect(Collectors.joining(System.lineSeparator()));
		result.setChapterText(chapterText);
		atomicRef.set(atomicRef.get().stream().dropWhile(myLine -> !myLine.contains(heading)).toList());
		return result;
	}

	public Long storeDocument(Document document) {
		var myDocument = this.documentRepository.save(document);
		var tikaDocuments = new TikaDocumentReader(new ByteArrayResource(document.getDocumentContent())).get();
		record TikaDocumentAndContent(org.springframework.ai.document.Document document, String content) {
		}
		var aiDocumentsChunks = tikaDocuments.stream()
				.flatMap(myDocument1 -> this.splitStringToTokenLimit(Optional.ofNullable(myDocument1).stream()
								.map(org.springframework.ai.document.Document::getText).filter(Objects::nonNull).findFirst().orElse(""),
								this.documentWordLimit)
						.stream().map(myStr -> new TikaDocumentAndContent(myDocument1, myStr)))
				.map(myTikaRecord -> new org.springframework.ai.document.Document(myTikaRecord.content(),
						myTikaRecord.document().getMetadata()))
				.peek(myDocument1 -> myDocument1.getMetadata().put(MetaData.ID, myDocument.getId().toString()))
				.peek(myDocument1 -> myDocument1.getMetadata().put(MetaData.DATATYPE,
						MetaData.DataType.DOCUMENT.toString()))
				.toList();

		var aiDocuments = this.llmSemanticTextSplitter.transform(aiDocumentsChunks);

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
		UserMessage userMessage = this.activeProfile.contains("ollama") ? new UserMessage(systemMessage.getText())
				: new UserMessage(searchDto.getSearchString());
		LocalDateTime start = LocalDateTime.now();
		var response = chatClient.prompt().system(s -> s.text(systemMessage.getText()))
				.user(u -> u.text(userMessage.getText())).call().chatResponse();
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
				similarDocuments.stream().map(org.springframework.ai.document.Document::getText)
				.filter(Predicate.not(Objects::isNull))
						.filter(Predicate.not(String::isBlank)).collect(Collectors.joining(System.lineSeparator())),
				tokenLimit);
		SystemPromptTemplate systemPromptTemplate = this.activeProfile.contains("ollama")
				? new SystemPromptTemplate(this.ollamaPrompt)
				: new SystemPromptTemplate(this.systemPrompt);
		Message systemMessage = systemPromptTemplate.createMessage(Map.of("documents", documentStr, "prompt", prompt));
		return systemMessage;
	}

	private List<String> splitStringToTokenLimit(String documentStr, int tokenLimit) {
		List<String> chunks = new ArrayList<>();
		var paragraphs = Arrays.stream(documentStr.split(System.lineSeparator()))
				.filter(s -> !s.trim().isEmpty()).toList();
		var chunkWords = 0;
		var chunk = "";
		for(String paragraph : paragraphs) {
			chunkWords += new StringTokenizer(chunk, " -.;,").countTokens();
			var paragraphWords = new StringTokenizer(paragraph, " -.;,").countTokens();
			if(chunkWords + paragraphWords + 1 > documentWordLimit) {
				chunks.add(chunk);
				chunk = "";
				chunkWords = 0;
			}
			chunk += paragraph + System.lineSeparator();
			chunkWords += paragraphWords;
		}
		return chunks;
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