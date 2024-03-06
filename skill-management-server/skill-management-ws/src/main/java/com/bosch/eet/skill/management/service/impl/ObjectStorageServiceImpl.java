package com.bosch.eet.skill.management.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.exception.BadRequestException;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.service.ObjectStorageService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ObjectStorageServiceImpl implements ObjectStorageService {

    MessageSource messageSource;

    String FILE_NAME_REGEX = "^[a-zA-Z0-9_-]+\\.[a-zA-Z0-9]+$";


    /**
     * Upload file to storage directory
     *
     * @param file
     * @param fileName
     * @throws IOException
     */
    @Override
    public void putObject(MultipartFile file, String fileName, String pathFile) throws IOException {
        catchErrorForFileAndFileName(file, fileName);
        createRootDirIfNotExist(pathFile);
        
        Path filePath = Paths.get(pathFile, fileName).toAbsolutePath().normalize();
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Download file by objectName
     *
     * @param objectName
     * @return fileResource
     * @throws FileNotFoundException
     */
    @Override
    public Resource getObject(String objectName, String path) throws FileNotFoundException {
        Path filePath = Paths.get(path, objectName).toAbsolutePath().normalize();
        Resource fileResource = new FileSystemResource(filePath);

        if (fileResource.exists()) {
            return fileResource;
        } else {
            throw new FileNotFoundException("File not found: " + objectName);
        }
    }

    void catchErrorForFileAndFileName(MultipartFile file, String fileName) {
        log.info("filename: {}",fileName);
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(
                    messageSource.getMessage(
                            MessageCode.FAILED_STORE_EMPTY_FILE.toString(),
                            null,
                            LocaleContextHolder.getLocale()));
        }
        if (StringUtils.isBlank(fileName) || !Pattern.matches(FILE_NAME_REGEX, fileName)) {
            throw new BadRequestException(
                    messageSource.getMessage(
                            MessageCode.FAILED_STORE_RELATIVE_PATH.toString(),
                            null,
                            LocaleContextHolder.getLocale()));
        }
    }

    void createRootDirIfNotExist(String path) {
        File storageFolder = new File(path);
        if (!storageFolder.exists()) {
            storageFolder.mkdirs();
        }
    }

    /**
     * Delete folder storage
     *
     * @param objectName
     * @return fileResource
     * @throws FileNotFoundException
     */
	@Override
	public void deleteFileOrFolder(String path) {
		try {
            Path directoryPath = Paths.get(path);
            if(Files.exists(directoryPath)) {
                FileUtils.cleanDirectory(new File(path));
                Files.deleteIfExists(directoryPath);
            }
		} catch (IOException e) {
			throw new BadRequestException(messageSource.getMessage(MessageCode.DELETE_FILE_ERROR.toString(),
					null, LocaleContextHolder.getLocale()));
		}
	}
}
