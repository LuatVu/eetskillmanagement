package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.ProjectRoleDto;
import com.bosch.eet.skill.management.entity.ProjectRole;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public final class ProjectRoleConverterUtil {
	
	private ProjectRoleConverterUtil() {
		// prevent instantiation
	}
	
    public static ProjectRoleDto convertToDTO(ProjectRole projectRole) {
        return ProjectRoleDto.builder()
                .id(projectRole.getId())
                .name(projectRole.getName())
                .build();
	}
    public static List<ProjectRoleDto> convertToDTOs(List<ProjectRole> projectRoles) {
        List<ProjectRoleDto> projectRoleDtos = new ArrayList<>();
        for (ProjectRole projectrole : projectRoles) {
        	projectRoleDtos.add(convertToDTO(projectrole));
        }
        return projectRoleDtos;
    }
}
