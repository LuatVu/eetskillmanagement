package com.bosch.eet.skill.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.converter.utils.CategoryConverterUtil;
import com.bosch.eet.skill.management.dto.CategoryDto;
import com.bosch.eet.skill.management.repo.CategoryRepository;
import com.bosch.eet.skill.management.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	
	private final CategoryRepository categoryRepository;
	
	
	@Override
	public List<CategoryDto> findAllCategories() {
		return CategoryConverterUtil.convertToDtos(categoryRepository.findAll());
	}

	
}
