package ch.xxx.aidoclibchat.adapter.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import ch.xxx.aidoclibchat.domain.model.entity.Document;
import ch.xxx.aidoclibchat.domain.model.entity.DocumentRepository;

@Repository
public class DocumentRepositoryBean implements DocumentRepository {
    private final JpaDocumentRepository JpaDocumentRepository;

    public DocumentRepositoryBean(JpaDocumentRepository jpaDocumentRepository) {
        this.JpaDocumentRepository = jpaDocumentRepository;
    }

    @Override
    public Optional<Document> findById(Long id) {
        return this.JpaDocumentRepository.findById(id);        
    }

    @Override
    public Document save(Document document) {
        return this.JpaDocumentRepository.save(document);
    }

}