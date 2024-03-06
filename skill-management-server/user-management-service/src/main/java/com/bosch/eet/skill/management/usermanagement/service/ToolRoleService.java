package com.bosch.eet.skill.management.usermanagement.service;

import java.util.List;

import com.bosch.eet.skill.management.usermanagement.dto.ToolRoleDto;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementException;

public interface ToolRoleService {
	
	List<ToolRoleDto> findAllBelongUser(final String userId) throws UserManagementException;
	
	void updateAllBelongUser(final String userId, final List<String> toolRoleIds) throws UserManagementException;
}
