package ch.xxx.aidoclibchat.domain.model.entity;

import java.util.Optional;

public interface DocumentRepository { 
    Optional<Document> findById(Long id);
}