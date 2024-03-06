package com.bosch.eet.skill.management.usermanagement.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.usermanagement.dto.RoleDTO;
import com.bosch.eet.skill.management.usermanagement.dto.role.AddRoleDTO;
import com.bosch.eet.skill.management.usermanagement.dto.role.UpdateRoleDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Role;

public interface RoleService {

	List<Role> getAllRoles();
	
	Role getRoleDetail(String id);
	
	Role addRole(AddRoleDTO roleDTO, String creatingUserName);
	
	Role updateRole(UpdateRoleDTO role, String modifyingUserName);
	
	void deleteRole(String id, String modifyingUserName);
	
	List<RoleDTO> findByIdIn(List<String> roleIds);
	@Transactional
	List<RoleDTO> findAllBelongUser(String userId);

	Role findRoleByRoleName(String roleName);
}