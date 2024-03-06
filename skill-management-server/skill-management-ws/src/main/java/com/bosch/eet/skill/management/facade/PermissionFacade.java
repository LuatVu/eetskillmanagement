package com.bosch.eet.skill.management.facade;

import com.bosch.eet.skill.management.dto.response.AllPermissionsResponseDTO;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;

public interface PermissionFacade {

	AllPermissionsResponseDTO getAllPermissions();
	
	PermissionDTO updatePermission(PermissionDTO permissionDto);

}
