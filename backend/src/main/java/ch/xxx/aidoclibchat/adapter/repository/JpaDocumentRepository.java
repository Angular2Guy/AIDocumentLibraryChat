package ch.xxx.aidoclibchat.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.xxx.aidoclibchat.domain.model.entity.Document;

public interface JpaDocumentRepository extends JpaRepository<Document, Long> {    
    
}