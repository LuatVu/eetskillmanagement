package com.bosch.eet.skill.management.usermanagement.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.common.ObjectMapperUtils;
import com.bosch.eet.skill.management.common.Status;
import com.bosch.eet.skill.management.usermanagement.dto.ToolRoleDto;
import com.bosch.eet.skill.management.usermanagement.entity.ToolRole;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.entity.UserToolRole;
import com.bosch.eet.skill.management.usermanagement.repo.ToolRoleRepository;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;
import com.bosch.eet.skill.management.usermanagement.repo.UserToolRoleRepository;
import com.bosch.eet.skill.management.usermanagement.service.ToolRoleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ToolRoleServiceImpl implements ToolRoleService{
	
	@Autowired
	private ToolRoleRepository repo;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserToolRoleRepository userToolRoleRepository;
	
	@Override
	public List<ToolRoleDto> findAllBelongUser(final String userId) {
		final List<ToolRole> entities = repo.findAllByUserId(userId, Status.ACTIVE.getLabel());
		List<ToolRoleDto> toolRoleDtos = ObjectMapperUtils.mapAll(entities, ToolRoleDto.class);
		for (ToolRoleDto toolRoleDto : toolRoleDtos) {
			ToolRole toolRole = entities.stream().filter(dto -> dto.getId().equals(toolRoleDto.getId())).findFirst().get();
			setUserHasToolRole(toolRole, toolRoleDto, userId);
		}
		return toolRoleDtos;
	}
	
	private void setUserHasToolRole(final ToolRole toolRole, ToolRoleDto toolRoleDto, final String userId) {
		Optional<UserToolRole> userToolRoleOpt = toolRole.getUserToolRoles().stream().filter(dto -> dto.getUser().getId().equals(userId)).findFirst();
		
		// If user not has tool role, UserToolRole is null
		if (userToolRoleOpt.isPresent()) {
			toolRoleDto.setIsChecked(true);
		} else {
			toolRoleDto.setIsChecked(false);
		}
	}

	@Override
	public void updateAllBelongUser(final String userId, final List<String> toolRoleIds) {
		List<UserToolRole> entities = new LinkedList<UserToolRole>();
		User user = userRepository.findById(userId).get();
		for (String toolRoleId : toolRoleIds) {
			ToolRole toolRole = repo.findById(toolRoleId).get();
			
			UserToolRole entity = new UserToolRole();
			entity.setUser(user);
			entity.setToolRole(toolRole);
			
			entities.add(entity);
		}
		
		userToolRoleRepository.saveAll(entities);
	}
}
