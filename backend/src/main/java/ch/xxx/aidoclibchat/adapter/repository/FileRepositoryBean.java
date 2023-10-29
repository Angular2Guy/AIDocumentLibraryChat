package ch.xxx.aidoclibchat.adapter.repository;

import java.util.Optional;

import ch.xxx.aidoclibchat.domain.model.entity.File;
import ch.xxx.aidoclibchat.domain.model.entity.FileRepository;

public class FileRepositoryBean implements FileRepository {
    private final JpaFileRepository JpaFileRepository;

    public FileRepositoryBean(JpaFileRepository jpaFileRepository) {
        this.JpaFileRepository = jpaFileRepository;
    }

    @Override
    public Optional<File> findById(Long id) {
        return this.JpaFileRepository.findById(id);        
    }

}