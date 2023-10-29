package ch.xxx.aidoclibchat.adapter.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.xxx.aidoclibchat.usecase.mapping.DocumentMapper;
import ch.xxx.aidoclibchat.usecase.service.DocumentService;


@RestController
@RequestMapping("rest/document")
public class DocumentController {
    private final DocumentMapper documentMapper;
    private final DocumentService documentService;

    public DocumentController(DocumentMapper documentMapper, DocumentService documentService) {
        this.documentMapper = documentMapper;
        this.documentService = documentService;
    }

    @PostMapping
    public long handleDocumentUpload(@RequestParam("document") MultipartFile document) {        
        var docSize = this.documentService.storeDocument(this.documentMapper.toEntity(document));
        return docSize;
    }
}