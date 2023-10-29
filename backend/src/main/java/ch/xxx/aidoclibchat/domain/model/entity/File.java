package ch.xxx.aidoclibchat.domain.model.entity;

import ch.xxx.aidoclibchat.domain.common.FileType;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;

@Entity
public class File {
    private Long id;
    private String fileName;
    private FileType fileType;
    @Lob    
    private byte[] fileContent;



    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}