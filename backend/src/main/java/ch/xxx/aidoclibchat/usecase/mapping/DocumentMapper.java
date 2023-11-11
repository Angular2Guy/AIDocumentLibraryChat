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
package ch.xxx.aidoclibchat.usecase.mapping;

import java.io.IOException;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import ch.xxx.aidoclibchat.domain.common.DocumentType;
import ch.xxx.aidoclibchat.domain.model.dto.AiResult;
import ch.xxx.aidoclibchat.domain.model.dto.DocumentDto;
import ch.xxx.aidoclibchat.domain.model.dto.DocumentSearchDto;
import ch.xxx.aidoclibchat.domain.model.entity.Document;

@Component
public class DocumentMapper {
	public Document toEntity(MultipartFile multipartFile) {
		var entity = new Document();
		try {
			entity.setDocumentContent(multipartFile.getBytes());
			entity.setDocumentName(multipartFile.getOriginalFilename());
			entity.setDocumentType(Optional.ofNullable(multipartFile.getContentType()).stream()
					.filter(myContentType -> myContentType.contains("pdf")).map(x -> DocumentType.PDF).findFirst()
					.orElse(DocumentType.UNKNOWN));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return entity;
	}

	public Document toEntity(DocumentDto dto) {
		var entity = new Document();
		entity.setDocumentContent(dto.getDocumentContent());
		entity.setDocumentName(dto.getDocumentName());
		entity.setDocumentType(dto.getDocumentType());
		entity.setId(dto.getId());
		return entity;
	}

	public DocumentDto toDto(Document entity) {
		var dto = new DocumentDto();
		dto.setDocumentContent(entity.getDocumentContent());
		dto.setDocumentName(entity.getDocumentName());
		dto.setDocumentType(entity.getDocumentType());
		dto.setId(entity.getId());
		return dto;
	}
	
	public DocumentSearchDto toDto(AiResult aiResult) {
		var dto = new DocumentSearchDto();
		dto.setDocuments(aiResult.documents().stream().map(myDoc -> this.toDto(myDoc)).toList());
		dto.setResultStrings(aiResult.generations().stream().map(myGen -> myGen.getText()).toList());
		dto.setSearchString(aiResult.searchString());
		return dto;
	}
}