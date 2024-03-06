package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.PageConfigDTO;
import com.bosch.eet.skill.management.entity.PageConfig;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public final class PageConfigConverterUtil {

	private PageConfigConverterUtil() {
		// prevent instantiation
	}
	
	public static PageConfigDTO convertToDTO(PageConfig entity) {
		PageConfigDTO dto = new PageConfigDTO();
		dto.setId(entity.getId());
		dto.setCreatedBy(entity.getCreatedBy());
		dto.setCreatedDate(entity.getCreatedDate());
		dto.setModifiedBy(entity.getModifiedBy());
		dto.setModifiedDate(entity.getModifiedDate());
		dto.setName(entity.getName());
		dto.setPageConfigDetailsDTOs(entity.getPageConfigDetailsDTOs());
		dto.setStatus(entity.getStatus());
		return dto;
	}
	
	public static PageConfig convertToEntity(PageConfigDTO dto) {
		PageConfig entity = new PageConfig();
		entity.setId(dto.getId());
		entity.setCreatedBy(dto.getCreatedBy());
		entity.setCreatedDate(dto.getCreatedDate());
		entity.setModifiedBy(dto.getModifiedBy());
		entity.setModifiedDate(dto.getModifiedDate());
		entity.setName(dto.getName());
		entity.setPageConfigDetailsDTOs(dto.getPageConfigDetailsDTOs());
		entity.setStatus(dto.getStatus());
		return entity;
	}
	
	public static List<PageConfig> convertToEntities(List<PageConfigDTO> dtos) {
		List<PageConfig> entities = new ArrayList<>();
		for (PageConfigDTO dto : dtos) {
			entities.add(convertToEntity(dto));
		}
		return entities;
	}
	
	public static List<PageConfigDTO> convertToDTOs(List<PageConfig> entities) {
		List<PageConfigDTO> dtos = new ArrayList<>();
		for (PageConfig entity : entities) {
			dtos.add(convertToDTO(entity));
		}
		return dtos;
	}
}
