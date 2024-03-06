package com.bosch.eet.skill.management.facade;

import java.util.List;

import com.bosch.eet.skill.management.dto.response.RoleResponseDTO;
import com.bosch.eet.skill.management.usermanagement.dto.role.AddRoleDTO;
import com.bosch.eet.skill.management.usermanagement.dto.role.UpdateRoleDTO;

public interface RoleFacade {

	List<RoleResponseDTO> getAllRoles();

	RoleResponseDTO getRoleDetail(String id);

	RoleResponseDTO addRole(AddRoleDTO roleDTO, String creatingUserName);

	RoleResponseDTO updateRole(String id, UpdateRoleDTO roleDTO, String modifyingUserName);

	void deleteRole(String id, String modifyingUserName);

}
