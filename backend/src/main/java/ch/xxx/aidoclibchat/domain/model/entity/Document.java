package ch.xxx.aidoclibchat.domain.model.entity;

import ch.xxx.aidoclibchat.domain.common.DocumentType;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;

@Entity
public class Document {
    private Long id;
    private String documentName;
    private DocumentType DocumentType;
    @Lob    
    private byte[] documentContent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public DocumentType getDocumentType() {
        return DocumentType;
    }

    public void setDocumentType(DocumentType DocumentType) {
        this.DocumentType = DocumentType;
    }

    public byte[] getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(byte[] documentContent) {
        this.documentContent = documentContent;
    }

}