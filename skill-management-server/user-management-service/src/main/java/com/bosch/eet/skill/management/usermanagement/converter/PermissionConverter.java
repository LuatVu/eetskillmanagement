package com.bosch.eet.skill.management.usermanagement.converter;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Permission;

@Component
public class PermissionConverter {
	
	public Permission convertDTOToEntity(final PermissionDTO dto) {
		Permission entity = new Permission();
		if (dto != null) {
			if (!StringUtils.isEmpty(dto.getId())) { // case update
				entity.setId(dto.getId());
			}
			entity.setCode(dto.getCode());
			entity.setName(dto.getName());
			entity.setDescription(dto.getDescription());
			entity.setStatus(dto.getStatus());
			entity.setCreatedBy(dto.getCreatedBy());
			entity.setCreatedDate(dto.getCreatedDate());
			entity.setModifiedBy(dto.getModifiedBy());
			entity.setModifiedDate(dto.getModifiedDate());
		}
		return entity;
	}	

	public List<Permission> convertDTOsToEntities(final List<PermissionDTO> dtos) {
		List<Permission> entities = new LinkedList<>();
		if (!CollectionUtils.isEmpty(dtos)) {
			for (PermissionDTO dto : dtos) {
				new Permission();
				Permission entity = convertDTOToEntity(dto);
				entities.add(entity);
			}
		}
		return entities;
	}
	
	public PermissionDTO convertEntityToDTO(final Permission entity) {
		PermissionDTO dto = new PermissionDTO();
		if (entity != null) {
			dto.setId(entity.getId());
			dto.setCode(entity.getCode());
			dto.setName(entity.getName());
			dto.setDescription(entity.getDescription());
			dto.setStatus(entity.getStatus());
		}
		return dto;
	}
	public List<PermissionDTO> convertEntitiesToDTOs(final List<Permission> entities) {
		List<PermissionDTO> dtos = new LinkedList<>();
		if (!CollectionUtils.isEmpty(entities)) {
			for (Permission entity : entities) {
				PermissionDTO dto = convertEntityToDTO(entity);
				dtos.add(dto);
			}
		}
		return dtos;
	}	
	
}
