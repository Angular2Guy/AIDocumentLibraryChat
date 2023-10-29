package ch.xxx.aidoclibchat.adapter.repository;

import java.util.Optional;

import ch.xxx.aidoclibchat.domain.model.entity.Document;
import ch.xxx.aidoclibchat.domain.model.entity.DocumentRepository;

public class DocumentRepositoryBean implements DocumentRepository {
    private final JpaDocumentRepository JpaDocumentRepository;

    public DocumentRepositoryBean(JpaDocumentRepository jpaDocumentRepository) {
        this.JpaDocumentRepository = jpaDocumentRepository;
    }

    @Override
    public Optional<Document> findById(Long id) {
        return this.JpaDocumentRepository.findById(id);        
    }

}