package ch.xxx.aidoclibchat.domain.exceptions;

public class DocumentException extends RuntimeException {
	private static final long serialVersionUID = -5601313921637319936L;

	public DocumentException(String message, Throwable th) {
		super(message, th);
	}
}
