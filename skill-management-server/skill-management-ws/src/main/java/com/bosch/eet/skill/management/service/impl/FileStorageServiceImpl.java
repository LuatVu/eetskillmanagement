package com.bosch.eet.skill.management.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.converter.utils.FileStorageConverterUtil;
import com.bosch.eet.skill.management.dto.FileStorageDTO;
import com.bosch.eet.skill.management.entity.FileStorage;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.repo.FileStorageRepository;
import com.bosch.eet.skill.management.service.FileStorageService;

@Service
public class FileStorageServiceImpl implements FileStorageService {

	@Autowired
	private Environment env;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private FileStorageRepository fileStorageRepo;
	
	private Path tempUploadDirectory; // Default path, all file will be store in temp folder
	
	@PostConstruct
	public void initialize() {
		tempUploadDirectory = Paths.get(env.getProperty(Constants.UPLOADED_FOLDER_TEMP));
	}
	
	@Override
	public FileStorageDTO store(final MultipartFile file, String token, FileStorageDTO fileStorageDTO,final String saveFolderPath) throws RuntimeException, IOException {
		if((null != file && file.isEmpty()) || null == file) {
			throw new RuntimeException(
					messageSource.getMessage(
							MessageCode.FAILED_STORE_EMPTY_FILE.toString(), 
							null, 
							LocaleContextHolder.getLocale()));
		} else {
			final String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			if(!StringUtils.hasLength(fileName) && fileName.contains("..")) {
				throw new RuntimeException(
						messageSource.getMessage(
								MessageCode.FAILED_STORE_RELATIVE_PATH.toString(), 
								null, 
								LocaleContextHolder.getLocale()));
			} else {
				token = (!StringUtils.hasLength(token) && token.length() > 0) ? token : UUID.randomUUID().toString();
				
				final Path tempUploadPath = !StringUtils.hasLength(saveFolderPath) ? 
						Paths.get(saveFolderPath, token, fileName).toAbsolutePath().normalize() 
					  : Paths.get(tempUploadDirectory.toString(), token, fileName).toAbsolutePath().normalize();
				if(!tempUploadPath.toFile().exists()) {
					Files.createDirectories(tempUploadPath);
				}
				Files.copy(file.getInputStream(), tempUploadPath, StandardCopyOption.REPLACE_EXISTING);
				fileStorageDTO.setToken(token);
				fileStorageDTO.setName(fileName);
				final String extension = (fileName.lastIndexOf('.') != -1 && fileName.lastIndexOf('.') != 0) ? fileName.substring(fileName.lastIndexOf('.') + 1) : ""; 
				fileStorageDTO.setExtension(extension);
				fileStorageDTO.setSize(file.getSize());
			}
		}
		return fileStorageDTO;
	}
	/**
	 * Store the uploaded file
	 * @param <E>
	 * @param file
	 * @param token
	 * @param fileStorageDTO
	 * @param saveFolderPath
	 * @param downloadPath
	 * @return
	 * @throws NotExistElementException
	 * @throws IOException
	 */
	@Override
	public FileStorageDTO storeUploadedFile(
			final MultipartFile file, 
			String token, 
			FileStorageDTO fileStorageDTO, 
			final String saveFolderPath, 
			final String downloadPath) throws RuntimeException, IOException {
		// Store the file
		fileStorageDTO = store(file, token, fileStorageDTO, saveFolderPath);
		
		// Modify The content to provide additional info
		final String fileDownloadUri = env.getProperty(Constants.APP_API_BASE_URL) + Routes.SLASH + downloadPath + Routes.SLASH + fileStorageDTO.getToken() + Routes.SLASH + fileStorageDTO.getName();
		if(!StringUtils.hasLength(downloadPath)) {
			fileStorageDTO.setUri(fileDownloadUri);			
		}
		fileStorageDTO.setType(file.getContentType());
		fileStorageDTO.setSize(file.getSize());
		
		FileStorage entity = FileStorageConverterUtil.convertToEntity(fileStorageDTO);
		fileStorageRepo.save(entity);
		return fileStorageDTO;
	}
	
	/**
	 * Response to image resource request from front end
	 * @param fileName
	 * @param token
	 * @param request
	 * @return
	 * @throws NoSuchMessageException
	 * @throws IllegalAccessException
	 * @throws NotExistElementException
	 * @throws IOException
	 */
	@Override
	public ResponseEntity<Resource> loadImageFileResources(final String fileName, final String token, final HttpServletRequest request, final String filePath) throws IllegalAccessException, RuntimeException, IOException {
		// Check file type
		Optional<String> fileType = getFileExtention(fileName);
		if (fileType.isPresent() && checkFileType(fileType.get(), "IMAGE")) {
			return getFileResponse(fileName, token, request, filePath);
		}
		throw new IllegalAccessException(
				messageSource.getMessage(
						MessageCode.BAD_REQUEST.toString(),
						new String[] {fileName},
						LocaleContextHolder.getLocale()));
	}
	
	/**
	 * Response to image resource request from front end
	 * @param fileName
	 * @param token
	 * @param request
	 * @return
	 * @throws NoSuchMessageException
	 * @throws IllegalAccessException
	 * @throws NotExistElementException
	 * @throws IOException
	 */
	@Override
	public ResponseEntity<Resource> loadGenericFileResources(final String fileName, final String token, final HttpServletRequest request, final String filePath) throws RuntimeException, IOException {
		return getFileResponse(fileName, token, request, filePath);
	}
	
	/**
	 * Response to a file request from front end, throw error if file does not exist
	 * @param fileName
	 * @param token
	 * @param request
	 * @return
	 * @throws NoSuchMessageException
	 * @throws NotExistElementException
	 * @throws IOException
	 */
	@Override
	public ResponseEntity<Resource> getFileResponse(final String fileName, final String token, final HttpServletRequest request, final String filePath) throws RuntimeException, IOException {
		// Load file
		final Resource resource = loadFileResources(fileName, token, filePath);
		
		// Get content type 
		String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		contentType = (contentType == null) ? Constants.FILE_CONTENT_TYPE : contentType;
		
		return ResponseEntity
				.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
	
	/**
	 * Load file from file storage in server, throw error if file not found
	 * @param fileName
	 * @param token
	 * @return
	 * @throws NoSuchMessageException
	 * @throws RuntimeException
	 * @throws MalformedURLException
	 */
	@Override
	public Resource loadFileResources(final String fileName, final String token, final String filePath) throws RuntimeException, MalformedURLException {
		final Path actualFilePath = Paths.get(filePath, token, fileName).toAbsolutePath().normalize(); 
		final Path imageStorageDirectory = Paths.get(env.getProperty(Constants.UPLOADED_FOLDER_IMAGE), token, fileName).toAbsolutePath().normalize();
		final Path tempUploadPath = Paths.get(env.getProperty(Constants.UPLOADED_FOLDER_TEMP), token, fileName).toAbsolutePath().normalize();
	
		final Resource actualFileResource = new UrlResource(actualFilePath.toUri());
		final Resource imageStorageResource = new UrlResource(imageStorageDirectory.toUri());
		final Resource tempUploadResource = new UrlResource(tempUploadPath.toUri());
		
		if(tempUploadResource.exists()) {
			return tempUploadResource;
		}else if(imageStorageResource.exists()) {
			return imageStorageResource;
		}else if (actualFileResource.exists()) {
			return actualFileResource;
		} else {
			throw new RuntimeException(
					messageSource.getMessage(
							MessageCode.FILE_NOT_FOUND.toString(), 
							new String[] {fileName}, 
							LocaleContextHolder.getLocale()));
		}
	}
	
	/**
	 * Get type of file
	 * @param fileName
	 * @return
	 */
	@Override
	public Optional<String> getFileExtention(String fileName) {
		return Optional.ofNullable(fileName).filter(f -> f.contains(".")).map(f -> f.substring(fileName.lastIndexOf('.') + 1));
	}
	
	@Override
	public boolean checkFileType(final String fileExtention, final String fileType) {
		boolean output = false;
		switch (fileType) {
		case "IMAGE":
			output = Arrays.stream(Constants.getSupportedIMGType()).anyMatch(fileExtention::equals);
			return output;
		default:
			return output;
		}
	}
	
	/**
	 * Remove a file in directory and then remove the directory if that directory is empty
	 * @param fileName
	 * @param token
	 * @param filePath
	 * @return
	 * @throws IOException
	 * @throws NotExistElementException
	 */
	@Override
	public boolean deleteFile(final String fileName, final String token, final String filePath) throws IOException, RuntimeException {
		final Path tempUploadFilePath = Paths.get(tempUploadDirectory.toString(), token, fileName).toAbsolutePath().normalize();
		
		// Validate files in directory
		if(StringUtils.hasLength(fileName) || StringUtils.hasLength(token) || (StringUtils.hasLength(filePath) && !tempUploadFilePath.toFile().exists())) {
			throw new RuntimeException(
					messageSource.getMessage(
							MessageCode.FILE_NOT_FOUND.toString(), 
							new String[] {fileName}, 
							LocaleContextHolder.getLocale()));
		}
		Path realUploadFilePath = null;
		Path fileDirectoryPath = null;
		
		if(!StringUtils.hasLength(filePath)) {
			realUploadFilePath = Paths.get(filePath, token, fileName).toAbsolutePath().normalize();
		}
		
		if(null != realUploadFilePath && realUploadFilePath.toFile().exists()) {
			Files.delete(realUploadFilePath);
			fileDirectoryPath = Paths.get(filePath, token).toAbsolutePath().normalize();
		} else if(tempUploadFilePath.toFile().exists()) {
			Files.delete(tempUploadFilePath);
			fileDirectoryPath = Paths.get(tempUploadDirectory.toString(), token).toAbsolutePath().normalize();
		} else {
			return false;
		}
		if(fileDirectoryPath.toFile().exists() && fileDirectoryPath.toFile().isDirectory() && fileDirectoryPath.toFile().list().length == 0) {
			Files.delete(fileDirectoryPath);
		}
		return true;
	}
}
