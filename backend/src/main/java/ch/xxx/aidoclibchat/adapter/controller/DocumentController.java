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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.xxx.aidoclibchat.domain.model.dto.DocumentDto;
import ch.xxx.aidoclibchat.usecase.mapping.DocumentMapper;
import ch.xxx.aidoclibchat.usecase.service.DocumentService;

@RestController
@RequestMapping("rest/document")
public class DocumentController {
	private final DocumentMapper documentMapper;
	private final DocumentService documentService;

	public DocumentController(DocumentMapper documentMapper, DocumentService documentService) {
		this.documentMapper = documentMapper;
		this.documentService = documentService;
	}

	@PostMapping("/upload")
	public long handleDocumentUpload(@RequestParam("document") MultipartFile document) {
		var docSize = this.documentService.storeDocument(this.documentMapper.toEntity(document));
		return docSize;
	}

	@GetMapping("/list")
	public List<DocumentDto> getDocumentList() {
		return this.documentService.getDocumentList().stream()
				.flatMap(myDocument -> Stream.of(this.documentMapper.toDto(myDocument))).flatMap(myDocument -> {
					myDocument.setDocumentContent(null);
					return Stream.of(myDocument);
				}).toList();
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<DocumentDto> getDocument(@PathVariable("id") Long id) {
		return ResponseEntity.ofNullable(this.documentService.getDocumentById(id).stream()
				.map(myDocument -> this.documentMapper.toDto(myDocument)).findFirst().orElse(null));
	}
}