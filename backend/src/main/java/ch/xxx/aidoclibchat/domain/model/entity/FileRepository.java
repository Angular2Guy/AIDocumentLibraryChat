package ch.xxx.aidoclibchat.domain.model.entity;

import java.util.Optional;

public interface FileRepository { 
    Optional<File> findById(Long id);
}