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
package ch.xxx.aidoclibchat.adapter.controller;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.xxx.aidoclibchat.domain.model.dto.BookDto;
import ch.xxx.aidoclibchat.domain.model.dto.ChapterPages;
import ch.xxx.aidoclibchat.domain.model.dto.DocumentDto;
import ch.xxx.aidoclibchat.domain.model.dto.DocumentSearchDto;
import ch.xxx.aidoclibchat.domain.model.dto.SearchDto;
import ch.xxx.aidoclibchat.domain.utils.Utils;
import ch.xxx.aidoclibchat.usecase.mapping.BookMapper;
import ch.xxx.aidoclibchat.usecase.mapping.DocumentMapper;
import ch.xxx.aidoclibchat.usecase.service.DocumentService;

@RestController
@RequestMapping("rest/document")
public class DocumentController {
	private final DocumentMapper documentMapper;
	private final DocumentService documentService;
	private final BookMapper bookMapper;

	public DocumentController(DocumentMapper documentMapper, DocumentService documentService, BookMapper bookMapper) {
		this.documentMapper = documentMapper;
		this.documentService = documentService;
		this.bookMapper = bookMapper;
	}

	@PostMapping("/upload")
	public long handleDocumentUpload(@RequestParam("file") MultipartFile document) {
		var docSize = this.documentService.storeDocument(this.documentMapper.toEntity(document));
		return docSize;
	}

	@PostMapping("/upload-book")
	public BookDto handleBookUpload(@RequestParam("file") MultipartFile bookFile,
			@RequestParam("chapters") List<ChapterPages> chapters) {
		var book = this.documentService.storeBook(this.bookMapper.toEntity(bookFile), chapters);
		this.documentService.addBookSummaries(book);
		return BookMapper.toDto(book);
	}

	@GetMapping("/book/{uuid}")
	public ResponseEntity<BookDto> getBookByUuid(@PathVariable("uuid") String uuid) {
		return this.documentService.findBookByUuid(uuid).stream().map(BookMapper::toDto).findFirst()
				.map(result -> ResponseEntity.ok(result)).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/search-book-titles/{title}")
	public List<BookDto> getBooksByTitle(@PathVariable("title") String title) {
		return List.of();
	}

	@GetMapping("/search-book-authors/{author}")
	public List<BookDto> getBooksByAuthor(@PathVariable("author") String author) {
		return List.of();
	}

	@GetMapping("/list")
	public List<DocumentDto> getDocumentList() {
		return this.documentService.getDocumentList().stream()
				.flatMap(myDocument -> Stream.of(this.documentMapper.toDto(myDocument))).flatMap(myDocument -> {
					myDocument.setDocumentContent(null);
					return Stream.of(myDocument);
				}).toList();
	}

	@GetMapping("/doc/{id}")
	public ResponseEntity<DocumentDto> getDocument(@PathVariable("id") Long id) {
		return this.documentService.getDocumentById(id).stream().map(this.documentMapper::toDto).findFirst()
				.map(result -> ResponseEntity.ok(result)).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/content/{id}")
	public ResponseEntity<byte[]> getDocumentContent(@PathVariable("id") Long id) {
		var resultOpt = this.documentService.getDocumentById(id).stream().map(this.documentMapper::toDto).findFirst();
		var result = resultOpt.stream().map(this::toResultEntity).findFirst().orElse(ResponseEntity.notFound().build());
		return result;
	}

	private ResponseEntity<byte[]> toResultEntity(DocumentDto documentDto) {
		return ResponseEntity.ok().contentType(Utils.toMediaType(documentDto.getDocumentType()))
				.body(documentDto.getDocumentContent());
	}

	@PostMapping("/search")
	public DocumentSearchDto postDocumentSearch(@RequestBody SearchDto searchDto) {
		searchDto.setResultAmount(searchDto.getResultAmount() < 4 ? 4 : searchDto.getResultAmount());
		var result = this.documentMapper.toDto(this.documentService.queryDocuments(searchDto));
		return result;
	}
}