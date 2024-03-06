package com.bosch.eet.skill.management.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.dto.FileStorageDTO;

public interface FileStorageService {

	/**
	 * Store file in file system, will store to temp if path is null
	 * @param <E>
	 * @param file
	 * @param token
	 * @param fileStorageDTO
	 * @param saveFolderPath
	 * @return
	 * @throws NotExistElementException
	 * @throws IOException
	 */
	public FileStorageDTO store(
			final MultipartFile file, 
			String token, FileStorageDTO fileStorageDTO,
			final String saveFolderPath) throws RuntimeException, IOException;
	
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
	public FileStorageDTO storeUploadedFile(
			final MultipartFile file, 
			String token, 
			FileStorageDTO fileStorageDTO, 
			final String saveFolderPath, 
			final String downloadPath) throws RuntimeException, IOException;
	
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
	public ResponseEntity<Resource> loadImageFileResources(
			final String fileName, 
			final String token, 
			final HttpServletRequest request, 
			final String filePath) throws IllegalAccessException,  IOException;
	
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
	public ResponseEntity<Resource> loadGenericFileResources(
			final String fileName, 
			final String token, 
			final HttpServletRequest request, 
			final String filePath) throws IOException;
	
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
	public ResponseEntity<Resource> getFileResponse(
			final String fileName, 
			final String token, 
			final HttpServletRequest request, 
			final String filePath) throws NoSuchMessageException, IOException;
	
	/**
	 * Load file from file storage in server, throw error if file not found
	 * @param fileName
	 * @param token
	 * @return
	 * @throws NoSuchMessageException
	 * @throws NotExistElementException
	 * @throws MalformedURLException
	 */
	public Resource loadFileResources(
			final String fileName, 
			final String token, 
			final String filePath) throws MalformedURLException;
	
	/**
	 * Get type of file
	 * @param fileName
	 * @return
	 */
	public Optional<String> getFileExtention(String fileName);
	
	/**
	 * Remove a file in directory and then remove the directory if that directory is empty
	 * @param fileName
	 * @param token
	 * @param filePath
	 * @return
	 * @throws IOException
	 * @throws NotExistElementException
	 */
	public boolean deleteFile(
			final String fileName, 
			final String token, 
			final String filePath) throws IOException;

	/**
	 * Check if file type is valid
	 * @param fileExtention
	 * @param fileType
	 * @return
	 */
	boolean checkFileType(String fileExtention, String fileType);
}
