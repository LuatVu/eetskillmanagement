package com.bosch.eet.skill.management.usermanagement.converter;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.bosch.eet.skill.management.usermanagement.dto.PermissionCategoryDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Permission;
import com.bosch.eet.skill.management.usermanagement.entity.PermissionCategory;

@Component
public class PermissionCategoryConverter {

	@Autowired
	PermissionConverter permissionConverter;
	
	public PermissionCategory convertDTOToEntity(final PermissionCategoryDTO dto) {
		PermissionCategory entity = new PermissionCategory();
		if (dto != null) {
			if (!StringUtils.isEmpty(dto.getId())) { // case update
				entity.setId(dto.getId());
			}
			entity.setCode(dto.getCode());
			entity.setName(dto.getName());
			entity.setSequence(dto.getSequence());
		}
		return entity;
	}	

	public List<PermissionCategory> convertDTOsToEntities(final List<PermissionCategoryDTO> dtos) {
		List<PermissionCategory> entities = new LinkedList<>();
		if (!CollectionUtils.isEmpty(dtos)) {
			for (PermissionCategoryDTO dto : dtos) {
				PermissionCategory entity = convertDTOToEntity(dto);
				entities.add(entity);
			}
		}
		return entities;
	}
	
	public PermissionCategoryDTO convertEntityToDTO(final PermissionCategory entity) {
		PermissionCategoryDTO dto = new PermissionCategoryDTO();
		if (entity != null) {
			dto.setId(entity.getId());
			dto.setCode(entity.getCode());
			dto.setName(entity.getName());
			
			List<Permission> permissions = entity.getPermissions();
			dto.setPermissionDTOS(permissionConverter.convertEntitiesToDTOs(permissions));
		}
		return dto;
	}
	
	public List<PermissionCategoryDTO> convertEntitiesToDTOs(final List<PermissionCategory> entities) {
		List<PermissionCategoryDTO> dtos = new LinkedList<>();
		if (!CollectionUtils.isEmpty(entities)) {
			for (PermissionCategory entity : entities) {
				PermissionCategoryDTO dto = convertEntityToDTO(entity);
				dtos.add(dto);
			}
		}
		return dtos;
	}	
}
