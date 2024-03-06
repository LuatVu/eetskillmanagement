package com.bosch.eet.skill.management.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageService {
    void putObject(MultipartFile file, String fileName, String filePath) throws IOException;

    Resource getObject(String objectName, String path) throws FileNotFoundException;
    
    void deleteFileOrFolder(String path);
    
}
