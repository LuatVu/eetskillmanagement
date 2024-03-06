package com.bosch.eet.skill.management.service;

import java.util.List;

import com.bosch.eet.skill.management.dto.CategoryDto;

public interface CategoryService {

	List<CategoryDto> findAllCategories();

}
