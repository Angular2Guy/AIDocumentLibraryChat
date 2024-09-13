package ch.xxx.aidoclibchat.usecase.mapping;

import java.io.IOException;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import ch.xxx.aidoclibchat.domain.common.MetaData.DocumentType;
import ch.xxx.aidoclibchat.domain.exceptions.DocumentException;
import ch.xxx.aidoclibchat.domain.model.dto.BookDto;
import ch.xxx.aidoclibchat.domain.model.dto.ChapterDto;
import ch.xxx.aidoclibchat.domain.model.entity.Book;
import ch.xxx.aidoclibchat.domain.model.entity.Chapter;
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

	public static BookDto toDto(Book entity) {
		return new BookDto(entity.getId(), entity.getTitle(), entity.getAuthor(), entity.getDocumentType(),
				entity.getSummary(), entity.getChapters().stream().map(BookMapper::toDto).toList());
	}

	private static ChapterDto toDto(Chapter entity) {
		return new ChapterDto(entity.getId(), entity.getTitle(), entity.getChapterText(), entity.getSummary());
	}
}
