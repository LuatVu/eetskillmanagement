package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.CommonTaskDto;
import com.bosch.eet.skill.management.entity.CommonTask;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public final class CommonTaskConverterUtil {
	
	private CommonTaskConverterUtil() {
		// prevent instantiation
	}
	
    public static CommonTaskDto convertToDTO(CommonTask commonTask) {
        return CommonTaskDto.builder()
                .id(commonTask.getId())
                .name(commonTask.getName())
                .project_role_id(commonTask.getProjectRole().getId())
                .build();
	}
    public static List<CommonTaskDto> convertToDTOs(List<CommonTask> commonTasks) {
        List<CommonTaskDto> commonTaskDtos = new ArrayList<>();
        for (CommonTask commonTask : commonTasks) {
            commonTaskDtos.add(convertToDTO(commonTask));
        }
        return commonTaskDtos;
    }
}
