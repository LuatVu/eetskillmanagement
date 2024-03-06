package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.FileStorageDTO;
import com.bosch.eet.skill.management.entity.FileStorage;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public final class FileStorageConverterUtil {
	
	private FileStorageConverterUtil() {
		// prevent instantiation
	}
	
	private static FileStorageDTO convertToDTO(FileStorage entity) {
		FileStorageDTO dto = new FileStorageDTO();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setExtension(entity.getExtension());
		dto.setSize(entity.getSize());
		dto.setUri(entity.getUri());
		dto.setToken(entity.getToken());
		dto.setType(entity.getType());
		dto.setDeleted(entity.isDeleted());
		return dto;
	}
	
	public static FileStorage convertToEntity(FileStorageDTO dto) {
		FileStorage entity = new FileStorage();
		entity.setId(dto.getId());
		entity.setName(dto.getName());
		entity.setExtension(dto.getExtension());
		entity.setSize(dto.getSize());
		entity.setUri(dto.getUri());
		entity.setToken(dto.getToken());
		entity.setType(dto.getType());
		entity.setDeleted(dto.isDeleted());
		return entity;
	}
	
	public static List<FileStorage> convertToEntities(List<FileStorageDTO> dtos) {
		List<FileStorage> entities = new ArrayList<>();
		for (FileStorageDTO dto : dtos) {
			entities.add(convertToEntity(dto));
		}
		return entities;
	}
	
	public static List<FileStorageDTO> convertToDTOs(List<FileStorage> entities) {
		List<FileStorageDTO> dtos = new ArrayList<>();
		for (FileStorage entity : entities) {
			dtos.add(convertToDTO(entity));
		}
		return dtos;
	}
}
