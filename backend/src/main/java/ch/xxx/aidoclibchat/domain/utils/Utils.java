package ch.xxx.aidoclibchat.domain.utils;

import org.springframework.http.MediaType;

import ch.xxx.aidoclibchat.domain.common.MetaData.DocumentType;

public class Utils {
	public static DocumentType toDocumentType(String mediaType) {
		var result = switch (mediaType) {
		case MediaType.APPLICATION_PDF_VALUE -> DocumentType.PDF;
		case MediaType.TEXT_HTML_VALUE -> DocumentType.HTML;
		case MediaType.TEXT_PLAIN_VALUE -> DocumentType.TEXT;
		case MediaType.APPLICATION_XML_VALUE -> DocumentType.XML;
		case MediaType.TEXT_XML_VALUE -> DocumentType.XML;
		default -> DocumentType.UNKNOWN;
		};
		return result;
	}
	
	public static MediaType toMediaType(DocumentType documentType) {
		var contentType = switch (documentType) {
		case DocumentType.PDF -> MediaType.APPLICATION_PDF;
		case DocumentType.HTML -> MediaType.TEXT_HTML;
		case DocumentType.TEXT -> MediaType.TEXT_PLAIN;
		case DocumentType.XML -> MediaType.APPLICATION_XML;
		case DocumentType.EPUB -> new MediaType("application", "epub+zip");
		default -> MediaType.ALL;
		};
		return contentType;
	}
}
