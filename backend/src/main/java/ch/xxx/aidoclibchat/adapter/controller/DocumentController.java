package ch.xxx.aidoclibchat.adapter.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("rest/document")
public class DocumentController {
    @PostMapping
    public long handleDocumentUpload(@RequestParam("document") MultipartFile document) {        
        return document.getSize();
    }
}