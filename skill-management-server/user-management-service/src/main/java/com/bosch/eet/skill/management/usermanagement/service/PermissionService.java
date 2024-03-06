package com.bosch.eet.skill.management.usermanagement.service;

import java.util.List;

import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Permission;

public interface PermissionService {

	PermissionDTO findPermissionByName(String permissionName);
	
	List<PermissionDTO> getAllPermissions();

	List<PermissionDTO> findAllBelongRoles(List<String> roleIds);

	List<Permission> findAllPermission();
	
	PermissionDTO updatePermission(PermissionDTO permissionDto);
}
