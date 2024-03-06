package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.DepartmentGroupDto;
import com.bosch.eet.skill.management.entity.DepartmentGroup;

@Component
public final class DepartmentGroupConverterUtil {
	
	private DepartmentGroupConverterUtil() {
		// prevent instantiation
	}

	private static DepartmentGroupDto convertToDTO(DepartmentGroup departmentGroup) {
        return DepartmentGroupDto.builder()
                .id(departmentGroup.getId())
                .name(departmentGroup.getName())
                .listTeams(TeamConverterUtil.convertToDTOS(departmentGroup.getTeams()))
                .build();
    }

    public static List<DepartmentGroupDto> convertToDTOS(List<DepartmentGroup> departmentGroups) {
        List<DepartmentGroupDto> groupDtos = new ArrayList<>();
        for (DepartmentGroup departmentGroup : departmentGroups) {
        	groupDtos.add(convertToDTO(departmentGroup));
        }
        return groupDtos;
    }
    
    public static DepartmentGroupDto convertToSimpleDto(DepartmentGroup departmentGroup) {
    	if(departmentGroup != null) {
            return DepartmentGroupDto.builder()
                    .id(departmentGroup.getId())
                    .name(departmentGroup.getName())
                    .build();
    	}
        return DepartmentGroupDto.builder()
        		.id("")
        		.name("")
        		.build();
    }

}
