package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.DepartmentDto;
import com.bosch.eet.skill.management.entity.Department;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public final class DepartmentConverterUtil {
	
	private DepartmentConverterUtil() {
		// prevent instantiation
	}
	
	private static DepartmentDto convertToDto (Department department) {
		return DepartmentDto.builder()
				.id(department.getId())
				.name(department.getName())
				.build();
	}
	
	public static List<DepartmentDto> convertToDtos (List<Department> listDepartments) {
		List<DepartmentDto> listDtos = new ArrayList<>();
		for (Department department : listDepartments) {
			listDtos.add(convertToDto(department));
		}
		return listDtos;
	}

}
