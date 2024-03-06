package com.bosch.eet.skill.management.usermanagement.converter;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.common.UserType;
import com.bosch.eet.skill.management.usermanagement.dto.GroupDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Group;

@Component
public class GroupConverter {
	
	private UserManagementConverter userConverter;
	
	@Autowired
	private RoleConverter roleConverter;
	
	@Autowired
	public void setUserManagementConverter(@Lazy UserManagementConverter userConverter) {
		this.userConverter = userConverter;
	}
	
	// Converter for Groups
	public GroupDTO convertToDTO(Group entity, boolean getRoleDetail) {
		GroupDTO dto = null;
		if (null != entity) {
			dto = new GroupDTO();
			dto.setGroupId(entity.getId());
			dto.setGroupName(entity.getName());
			dto.setDisplayName(entity.getDisplayName());
			dto.setDescription(entity.getDescription());
			dto.setStatus(entity.getStatus());
			dto.setUsers(entity.getUsersGroup()
					.stream()
					.filter(userGroup -> UserType.PERSON.getLabel().equals(userGroup.getUser().getType()))
					.map(userGroup -> userConverter.convertToDTO(userGroup.getUser()))
					.collect(Collectors.toList()));
			dto.setDistributionlists(entity.getUsersGroup()
					.stream()
					.filter(userGroup -> UserType.GROUP.getLabel().equals(userGroup.getUser().getType()))
					.map(userGroup -> userConverter.convertToDTO(userGroup.getUser()))
					.collect(Collectors.toList()));
			dto.setRoles(entity.getGroupRoles().stream().map(groupRole -> roleConverter.convertEntityToDTO(groupRole.getRole(), getRoleDetail)).collect(Collectors.toList()));
		}
		return dto;
	}
	
	public Group convertToEntity(GroupDTO dto) {
		Group entity = null;
		if (null != dto) {
			entity = new Group();
			entity.setId(dto.getGroupId());
			entity.setName(dto.getGroupName());
			entity.setDescription(dto.getDescription());
			entity.setStatus(dto.getStatus());
			entity.setDisplayName(dto.getDisplayName());
		}
		return entity;
	}
}
