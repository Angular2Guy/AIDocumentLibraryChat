package ch.xxx.aidoclibchat.domain.model.entity;

import ch.xxx.aidoclibchat.domain.common.DocumentType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class Document {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;
    private String documentName;
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
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
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public byte[] getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(byte[] documentContent) {
        this.documentContent = documentContent;
    }

}