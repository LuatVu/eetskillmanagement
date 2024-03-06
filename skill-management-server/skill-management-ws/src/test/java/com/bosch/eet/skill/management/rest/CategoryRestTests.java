package com.bosch.eet.skill.management.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.CategoryDto;
import com.bosch.eet.skill.management.service.CategoryService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("dev")
class CategoryRestTests {

	@MockBean
	private CategoryService categoryService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("Find all category happy case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void findAllCategory() throws Exception {
		CategoryDto category1 = CategoryDto.builder()
				.id("1")
				.name("Test1")
				.build();

		CategoryDto category2 = CategoryDto.builder()
				.id("1")
				.name("Test2")
				.build();

		List<CategoryDto> categoryList = new ArrayList<>();

		categoryList.add(category1);
		categoryList.add(category2);

		when(categoryService.findAllCategories())
				.thenReturn(categoryList);

		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_CATEGORY)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string("{\"data\":[\"Test1\",\"Test2\"],\"code\":\"SUCCESS\"}"));
	}


	@Test
	@DisplayName("Get empty list of category")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void findEmptyCategoryList() throws Exception {
		List<CategoryDto> categoryDtos = new ArrayList<>();

		when(categoryService.findAllCategories())
				.thenReturn(categoryDtos);

		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_CATEGORY)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(content().string("{\"data\":[],\"code\":\"SUCCESS\"}"));

	}
}
