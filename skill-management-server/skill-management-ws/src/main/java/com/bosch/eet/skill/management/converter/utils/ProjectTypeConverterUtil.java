package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.ProjectTypeDto;
import com.bosch.eet.skill.management.entity.ProjectType;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public final  class ProjectTypeConverterUtil {

	private ProjectTypeConverterUtil() {
		// prevent instantiation
	}
	
    public static ProjectTypeDto convertToDTO(ProjectType projectType) {
        return ProjectTypeDto.builder()
                .id(projectType.getId())
                .name(projectType.getName())
                .build();
	}
    public static List<ProjectTypeDto> convertToDTOs(List<ProjectType> projectTypes) {
        List<ProjectTypeDto> projectTypeDtos = new ArrayList<>();
        for (ProjectType projectType : projectTypes) {
        	projectTypeDtos.add(convertToDTO(projectType));
        }
        return projectTypeDtos;
    }
}
