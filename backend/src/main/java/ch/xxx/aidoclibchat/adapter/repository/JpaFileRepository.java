package ch.xxx.aidoclibchat.adapter.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.xxx.aidoclibchat.domain.model.entity.File;

public interface JpaFileRepository extends JpaRepository<File, Long> {    
    Optional<File> findById(Long id);
}