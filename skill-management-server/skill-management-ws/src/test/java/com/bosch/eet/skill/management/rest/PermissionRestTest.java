package com.bosch.eet.skill.management.rest;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

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

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.response.AllPermissionsResponseDTO;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.facade.PermissionFacade;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionCategoryDTO;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("test")
public class PermissionRestTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PermissionFacade permissionFacade;
    
    @Autowired
    private ObjectMapper objectMapper;

	@Test
	@DisplayName("Get all permission - Happy case")
	@WithMockUser(username = "admin", authorities = { "VIEW_USER_MANAGEMENT" })
	void getAllPermission_Happycase() throws Exception {
		String permissionId = "permissionid";
		String permissionCategoryId = "permissionCategoryId";
		Short sequence = 1;

		PermissionDTO permissionDto = PermissionDTO.builder().id(permissionId).code("VIEW_ALL_USER")
				.name("View all user").permissionCategoriesId(permissionCategoryId).status("Active")
				.description("View all user").belongsToRole(false).build();

		PermissionCategoryDTO permissionCategoryDTO = PermissionCategoryDTO.builder().id(permissionCategoryId)
				.code("ADMIN").name("Administration").sequence(sequence).build();

		AllPermissionsResponseDTO allPermissionsResponseDTO = new AllPermissionsResponseDTO();
		allPermissionsResponseDTO.setPermissionDTOs(Collections.singletonList(permissionDto));
		allPermissionsResponseDTO.setPermissionCategoryDTOs(Collections.singletonList(permissionCategoryDTO));

		when(permissionFacade.getAllPermissions()).thenReturn(allPermissionsResponseDTO);

		mockMvc.perform(get(Routes.URI_REST_PERMISSION).contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)).andExpect(status().isOk())
				.andExpect(jsonPath("$.code", is("SUCCESS")));
	}
	
	@Test
	@DisplayName("Get all permission - Access denied")
	@WithMockUser(username = "admin", authorities = {})
	void getAllPermission_AccessDenied() throws Exception {
		mockMvc.perform(get(Routes.URI_REST_PERMISSION).contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)).andExpect(status().isForbidden())
				.andExpect(jsonPath("$.error", is("access_denied")))
				.andExpect(jsonPath("$.error_description", is("Access is denied")));;
				
	}
	
    @Test
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER," +
            "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    public void updatePermission() throws Exception {

        PermissionDTO permissionDto = PermissionDTO.builder()
                .id("testId")
                .build();

        when(permissionFacade.updatePermission(any())).thenReturn(permissionDto);

        mockMvc.perform(post(Routes.URI_REST_PERMISSION + "/testId/update")
	        		.content(this.objectMapper.writeValueAsString(permissionDto))
	                .contentType(MediaType.APPLICATION_JSON)
	                .characterEncoding(StandardCharsets.UTF_8)
	                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(MessageCode.SUCCESS.toString())))
                .andExpect(jsonPath("$.data.id", is("testId")));
    }
}
