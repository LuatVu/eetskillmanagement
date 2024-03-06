package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.ProjectScopeDto;
import com.bosch.eet.skill.management.entity.ProjectScope;

@Component
public final class ProjectScopeConverterUtil {

	private ProjectScopeConverterUtil() {
		// prevent instantiation
	}
	
	public static ProjectScopeDto convertToDto(ProjectScope projectScope) {
		return ProjectScopeDto.builder()
                .id(projectScope.getId())
                .name(projectScope.getName())
				.colour(projectScope.getColour())
				.hoverColour(projectScope.getHoverColour())
                .build();
		
	}
	
	public static List<ProjectScopeDto> convertToDtos(List<ProjectScope> listProjectScope) {
		List<ProjectScopeDto> listDtos = new ArrayList<>();
		for (ProjectScope projectScope : listProjectScope) {
			listDtos.add(convertToDto(projectScope));
		}
		return listDtos;
	}
}
