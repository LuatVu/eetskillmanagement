package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bosch.eet.skill.management.dto.CategoryDto;
import com.bosch.eet.skill.management.entity.Category;
import com.bosch.eet.skill.management.repo.CategoryRepository;
import com.bosch.eet.skill.management.service.impl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    
	// find
	@Test
	@DisplayName("Find all category")
	void getCategory_returnListOfCategory() {
		List<Category> categories = Arrays.asList(Category.builder().id("1").name("a").build());
		when(categoryRepository.findAll()).thenReturn(categories);
		List<CategoryDto> actualResult = categoryService.findAllCategories();
		assertThat(actualResult).isNotEmpty();
		assertThat(actualResult).hasSize(1);
	}
    
	// find null
	@Test
	@DisplayName("Find empty category")
	void getDepartment_returnListOfEmptyDepartment() {
		List<Category> categories = Collections.emptyList();
		when(categoryRepository.findAll()).thenReturn(categories);
		List<CategoryDto> categorys = categoryService.findAllCategories();
		assertThat(categorys).isEmpty();
	}
}
