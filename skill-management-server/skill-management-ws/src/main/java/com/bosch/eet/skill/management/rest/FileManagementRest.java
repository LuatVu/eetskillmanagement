package com.bosch.eet.skill.management.rest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.FileStorageDTO;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.service.FileStorageService;

import lombok.extern.slf4j.Slf4j;

@RequestMapping
@RestController
@Slf4j
public class FileManagementRest {

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private FileStorageService fileService;
	
	private Path tempUploadDirectory; // Default path, all file will be store in temp folder
	
	@PostConstruct
	public void initialize() {
		tempUploadDirectory = Paths.get(env.getProperty(Constants.UPLOADED_FOLDER_TEMP));
	}
	
	// Create 
	/**
	 * Special API to upload image into image folder, will throw error if anything that is not image uploaded to server
	 * @param file
	 * @param token
	 * @return
	 * @throws FileUploadException
	 * @throws NotExistElementException
	 * @throws IOException
	 */
	@PostMapping(Routes.REST_FILE_STORAGE_UPLOAD_IMAGE) 
	public FileStorageDTO uploadImg(
			@RequestParam("file") final MultipartFile file,
			@RequestParam("token") final String token) throws Exception {
		log.info("UploadImageFor FileStorage - fileName: " + file.getOriginalFilename() + ", token: " + token);
		Optional<String> fileExtention = fileService.getFileExtention(file.getOriginalFilename());
		if(fileExtention.isPresent() && fileService.checkFileType(fileExtention.get(), "IMAGE")) {
			final String fileDownloadPath = Routes.URI_FILE_STORAGE + Routes.REST_FILE_STORAGE_DOWNLOAD_IMAGE;
			final String saveFolderPath = env.getProperty(Constants.UPLOADED_FOLDER_IMAGE);
			return fileService.storeUploadedFile(file, token, new FileStorageDTO(), saveFolderPath, fileDownloadPath);
		} else {
			throw new RuntimeException(messageSource.getMessage(MessageCode.INVALID_FILE_TYPE.toString(), null, LocaleContextHolder.getLocale()));
		}
	}
	
	// Read
	@GetMapping(Routes.REST_FILE_STORAGE_DOWNLOAD + "/{token}/{fileName:.+}")
	public @ResponseBody ResponseEntity<Resource> download(
			@PathVariable("token") final String token,
			@PathVariable("fileName") final String fileName,
			final HttpServletRequest request) throws Exception {
		return fileService.getFileResponse(fileName, token, request, null);
	}
	
	// Delete
	@PutMapping(Routes.REST_FILE_STORAGE_DELETE_FILE+"/{token}/{fileName:.+}")
	public void deleteFile(@PathVariable("token") final String token,
			@PathVariable("fileName") final String fileName) throws Exception {
		fileService.deleteFile(fileName, token, null);
	}
	
	// Download Image 
	// -Special API to use on img tag -
	// Do not use this link as API call from front end because of security reason
	@GetMapping(value = Routes.REST_FILE_STORAGE_DOWNLOAD_IMAGE + "/{token}/{fileName:.+}")
	public ResponseEntity<Resource> downloadImage(
			@PathVariable("token") final String token,
			@PathVariable("fileName") final String fileName,
			final HttpServletRequest request) throws Exception {
		return fileService.loadImageFileResources(fileName, token, request, null);
	}
}
