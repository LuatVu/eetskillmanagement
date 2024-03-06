package com.bosch.eet.skill.management.usermanagement.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.usermanagement.converter.PermissionCategoryConverter;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionCategoryDTO;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;
import com.bosch.eet.skill.management.usermanagement.entity.PermissionCategory;
import com.bosch.eet.skill.management.usermanagement.repo.PermissionCategoryRepository;
import com.bosch.eet.skill.management.usermanagement.service.PermissionCategoryService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PermissionCategoryServiceImpl implements PermissionCategoryService {
	
	@Autowired
	PermissionCategoryRepository permissionCategoryRepo;
	
	@Autowired
	PermissionCategoryConverter permissionCategoryConverter;

	@Override
	public List<PermissionCategoryDTO> getAllPermissionCategories() {
		try {
			List<PermissionCategory> entities = permissionCategoryRepo.findAll();
			return permissionCategoryConverter.convertEntitiesToDTOs(entities);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			return null;
		}
	}

	@Override
	public List<PermissionDTO> getAllPermissionsByPermissionCategoryId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PermissionCategory> findAllPermissionCategories() {
		return permissionCategoryRepo.findAll();
	}
}
