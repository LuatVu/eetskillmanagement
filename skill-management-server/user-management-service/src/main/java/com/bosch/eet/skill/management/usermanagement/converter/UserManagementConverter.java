package com.bosch.eet.skill.management.usermanagement.converter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.bosch.eet.skill.management.common.Status;
import com.bosch.eet.skill.management.common.UserType;
import com.bosch.eet.skill.management.usermanagement.dto.GroupDTO;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;
import com.bosch.eet.skill.management.usermanagement.dto.RoleDTO;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Group;
import com.bosch.eet.skill.management.usermanagement.entity.GroupRole;
import com.bosch.eet.skill.management.usermanagement.entity.Permission;
import com.bosch.eet.skill.management.usermanagement.entity.Role;
import com.bosch.eet.skill.management.usermanagement.entity.RolePermission;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;

@Component
public class UserManagementConverter {
	
	@Autowired
	private RoleConverter roleConverter;
	
	@Autowired
	private PermissionConverter permissionConverter;
	
	@Autowired
	private UserRepository userRepository;

	// Converter for Roles
	public RoleDTO convertToDTO(Role entity) {
		RoleDTO dto = null;
		if (null != entity) {
			dto = new RoleDTO();
			dto.setId(entity.getId());
			dto.setName(entity.getName());
			dto.setDisplayName(entity.getDisplayName());
			dto.setDescription(entity.getDescription());
			dto.setStatus(entity.getStatus());
			dto.setPriority(entity.getPriority());
		}
		return dto;
	}
	
	// Converter for Groups
	public GroupDTO convertToDTO(Group entity) {
		GroupDTO dto = null;
		if (null != entity) {
			dto = new GroupDTO();
			dto.setGroupId(entity.getId());
			dto.setGroupName(entity.getName());
			dto.setDisplayName(entity.getDisplayName());
			dto.setDescription(entity.getDescription());
			dto.setStatus(entity.getStatus());
		}
		return dto;
	}
	
	public Group convertToEntity(GroupDTO dto) {
		Group entity = null;
		if (null != dto) {
			entity = new Group();
			if(!StringUtils.isEmpty(dto.getGroupId())) {
				entity.setId(dto.getGroupId());
			}else {
				entity.setCreatedBy(userRepository.findByName("sysadmin").get().getId());
				entity.setCreatedDate(LocalDateTime.now());
			}			
			entity.setName(dto.getGroupName());
			entity.setDisplayName(dto.getDisplayName());
			entity.setStatus(dto.getStatus());
			entity.setDescription(dto.getDescription());
			
		}
		return entity;
	}
	
	// Converter for Permissions
	public PermissionDTO convertToDTO(Permission entity) {
		PermissionDTO dto = null;
		if (null != entity) {
			dto = new PermissionDTO();
			dto.setId(entity.getId());
			dto.setCode(entity.getCode());
			dto.setName(entity.getName());
			dto.setDescription(entity.getDescription());
			dto.setStatus(entity.getStatus());
		}
		return dto;
	}
	
	public Permission convertToEntity(PermissionDTO dto) {
		Permission entity = null;
		if (null != dto) {
			entity = new Permission();
			entity.setId(dto.getId());
			entity.setCode(dto.getCode());
			entity.setStatus(dto.getStatus());
			entity.setDescription(dto.getDescription());
		}
		return entity;
	}
	
	public UserDTO convertToDTO(User entity) {
		UserDTO userDTO = null;
		if (null != entity) {
			List<GroupRole> groupRoles = entity
					.getUserGroup()
					.stream()
					.map(usergroup -> {
						if (null != usergroup.getGroup() && !CollectionUtils.isEmpty(usergroup.getGroup().getGroupRoles())) {
							return usergroup.getGroup().getGroupRoles();							
						}
						return new ArrayList<GroupRole>();
					})
					.flatMap(Collection::stream).collect(Collectors.toList());
			
			List<RoleDTO> roles = groupRoles.stream()
					.map(groupRole -> roleConverter.convertEntityToDTO(groupRole.getRole(),true)) //
					.collect(Collectors.toList());
			
			List<PermissionDTO> permissions = groupRoles.stream()
					.map(groupRole -> {
						if (null != groupRole.getRole() && !CollectionUtils.isEmpty(groupRole.getRole().getRolePermissions())) {
							return groupRole.getRole().getRolePermissions();							
						}
						return new ArrayList<RolePermission>();
					})
					.flatMap(Collection::stream)
					.map(rolePermission -> permissionConverter.convertEntityToDTO(rolePermission.getPermission()))
					.filter(permission -> Status.ACTIVE.getLabel().equals(permission.getStatus()))
					.collect(Collectors.toList());
			
			userDTO = new UserDTO();
			userDTO.setId(entity.getId());
			userDTO.setId(entity.getId());
			userDTO.setName(entity.getName());
			userDTO.setStatus(entity.getStatus());
			userDTO.setDistributionList(UserType.GROUP.getLabel().equals(entity.getType()));
			userDTO.setDisplayName(entity.getDisplayName());
			userDTO.setEmail(entity.getEmail());
			userDTO.setRoles(roles);
			userDTO.setPermissions(permissions);
		}
		return userDTO;
	}
	
	public User convertToEntity(UserDTO dto) {
		User entity = null;
		if (null != dto) {
			entity = new User();
			entity.setId(dto.getId());
			entity.setName(dto.getName());
			entity.setDisplayName(dto.getDisplayName());
			entity.setEmail(dto.getEmail());
			entity.setStatus(Status.getNameByLabel(dto.getStatus()));
			entity.setType(dto.isDistributionList() ? UserType.GROUP.getLabel() : UserType.PERSON.getLabel());
		}
		return entity;
	}
}
