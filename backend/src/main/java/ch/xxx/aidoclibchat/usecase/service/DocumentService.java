package ch.xxx.aidoclibchat.usecase.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import ch.xxx.aidoclibchat.domain.model.entity.Document;
import ch.xxx.aidoclibchat.domain.model.entity.DocumentRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Long storeDocument(Document document) {
        var myDocument = this.documentRepository.save(document);
        return Optional.ofNullable(myDocument.getDocumentContent()).stream().map(myContent -> Integer.valueOf(myContent.length).longValue()).findFirst().orElse(0L);
    }
}