package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.CategoryDto;
import com.bosch.eet.skill.management.entity.Category;

@Component
public final class CategoryConverterUtil {

	private CategoryConverterUtil() {
		// prevent instantiation
	}
	
	public static CategoryDto convertToDto (Category category) {
		return CategoryDto.builder()
				.id(category.getId())
				.name(category.getName())
				.build();
	}
	
	public static List<CategoryDto> convertToDtos (List<Category> listCategory) {
		List<CategoryDto> listDtos = new ArrayList<>();
		for (Category category : listCategory) {
			listDtos.add(convertToDto(category));
		}
		return listDtos;
	}

}
