package ch.xxx.aidoclibchat.adapter.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("rest/file")
public class FileController {
    @PostMapping
    public long handleFileUpload(@RequestParam("file") MultipartFile file) {        
        return file.getSize();
    }
}