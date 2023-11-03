/**
 *    Copyright 2023 Sven Loesekann
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.aidoclibchat.adapter.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    
    @Override
    public List<Document> findAll() {
    	return this.JpaDocumentRepository.findAll(PageRequest.of(0, 100, Sort.by("documentName"))).toList();
    }
}