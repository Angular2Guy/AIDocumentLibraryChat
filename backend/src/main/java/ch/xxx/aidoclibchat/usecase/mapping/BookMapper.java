package ch.xxx.aidoclibchat.usecase.mapping;

import java.io.IOException;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import ch.xxx.aidoclibchat.domain.common.MetaData.DocumentType;
import ch.xxx.aidoclibchat.domain.exceptions.DocumentException;
import ch.xxx.aidoclibchat.domain.model.entity.Book;
import ch.xxx.aidoclibchat.domain.utils.Utils;

@Component
public class BookMapper {

	public Book toEntity(MultipartFile multipartFile) {
		var entity = new Book();
		try {
			entity.setBookFile(multipartFile.getBytes());
			entity.setTitle(multipartFile.getOriginalFilename());
			entity.setDocumentType(Optional.ofNullable(multipartFile.getContentType()).stream()
					.map(Utils::toDocumentType).findFirst().orElse(DocumentType.UNKNOWN));
		} catch (IOException e) {
			throw new DocumentException("IOException", e);
		}
		return entity;
	}
}
