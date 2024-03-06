package com.bosch.eet.skill.management.rest;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.common.Status;
import com.bosch.eet.skill.management.dto.response.RoleResponseDTO;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.facade.RoleFacade;
import com.bosch.eet.skill.management.usermanagement.dto.role.AddRoleDTO;
import com.bosch.eet.skill.management.usermanagement.dto.role.UpdateRoleDTO;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementBusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("test")
public class RoleRestTest {

    public static final String baseGroupUrl = Routes.URI_REST_GROUP;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleFacade roleFacade;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MessageSource messageSource;

    @Test
    @WithMockUser(username = "admin", authorities = {"VIEW_USER_MANAGEMENT"})
    public void getAllRoles_returnRoles() throws Exception {

        RoleResponseDTO responseDTO = RoleResponseDTO.builder()
                .name("test")
                .build();

        when(roleFacade.getAllRoles()).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get(Routes.URI_REST_ROLE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(MessageCode.SUCCESS.toString())))
                .andExpect(jsonPath("$.data[0].name", is("test")));
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {})
    @DisplayName("Get all roles - access denied")
    void getAllRoles_AccessDenied() throws Exception {
        mockMvc.perform(get(Routes.URI_REST_ROLE))
        .andExpect(status().isForbidden())
		.andExpect(jsonPath("$.error", is("access_denied")))
		.andExpect(jsonPath("$.error_description", is("Access is denied")));
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {"VIEW_USER_MANAGEMENT"})
    void getRoleDetail() throws Exception {
    	String roleId ="roleid";

        RoleResponseDTO responseDTO = RoleResponseDTO.builder()
                .name("test")
                .build();

        when(roleFacade.getRoleDetail(roleId)).thenReturn(responseDTO);

        mockMvc.perform(get(Routes.URI_REST_ROLE+Routes.URI_REST_ID, roleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(MessageCode.SUCCESS.toString())))
                .andExpect(jsonPath("$.data.name", is("test")));
    }
    
	@Test
	@WithMockUser(username = "admin", authorities = {})
	@DisplayName("Get detaild role - Access Denied")
	void getRoleDetail_AccessDenied() throws Exception {
		String roleId = "roleid";

		mockMvc.perform(get(Routes.URI_REST_ROLE + Routes.URI_REST_ID, roleId)).andExpect(status().isForbidden())
				.andExpect(jsonPath("$.error", is("access_denied")))
				.andExpect(jsonPath("$.error_description", is("Access is denied")));
	}
	
	@Test
	@WithMockUser(username = "admin", authorities = {"VIEW_USER_MANAGEMENT"})
	@DisplayName("Get detail role - Role not found")
	void getRoleDetail_RoleNotFound() throws Exception {
		String roleId = "roleid";

		when(roleFacade.getRoleDetail(roleId)).thenThrow(new UserManagementBusinessException(
				com.bosch.eet.skill.management.usermanagement.consts.MessageCode.ROLE_NOT_EXIST_MSG.toString(),
				messageSource.getMessage(
						com.bosch.eet.skill.management.usermanagement.consts.MessageCode.ROLE_NOT_EXIST_MSG.toString(),
						null, LocaleContextHolder.getLocale())));

		mockMvc.perform(get(Routes.URI_REST_ROLE + Routes.URI_REST_ID, roleId)).andExpect(status().is5xxServerError())
				.andExpect(jsonPath("$.code", is("ROLE_NOT_EXIST_MSG")))
				.andExpect(jsonPath("$.message", is("Role not exist")));
	}

    @Test
    @WithMockUser(username = "admin", authorities = {"CREATE_ROLE"})
    public void createRole_returnRole() throws Exception {

        AddRoleDTO addRoleDTO = AddRoleDTO.builder()
                .name("test")
                .status(Status.ACTIVE.getLabel())
                .build();

        RoleResponseDTO responseDTO = RoleResponseDTO.builder()
                .name("test")
                .build();

        when(roleFacade.addRole(any(), anyString())).thenReturn(responseDTO);

        mockMvc.perform(post(Routes.URI_REST_ROLE)
                        .content(objectMapper.writeValueAsString(addRoleDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(MessageCode.SUCCESS.toString())))
                .andExpect(jsonPath("$.data.name", is("test")));
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {})
    @DisplayName("Create role - Access denied")
    void createRole_AccessDenied() throws Exception {

        AddRoleDTO addRoleDTO = AddRoleDTO.builder()
                .name("test")
                .status(Status.ACTIVE.getLabel())
                .build();

        mockMvc.perform(post(Routes.URI_REST_ROLE)
                        .content(objectMapper.writeValueAsString(addRoleDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isForbidden())
		.andExpect(jsonPath("$.error", is("access_denied")))
		.andExpect(jsonPath("$.error_description", is("Access is denied")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"EDIT_ROLE"})
    public void updateRole_returnRole() throws Exception {

        UpdateRoleDTO updateRoleDTO = UpdateRoleDTO.builder()
                .name("test")
                .status(Status.ACTIVE.getLabel())
                .build();

        RoleResponseDTO responseDTO = RoleResponseDTO.builder()
                .name("test")
                .build();

        when(roleFacade.updateRole(any(), any(), anyString())).thenReturn(responseDTO);

        mockMvc.perform(post(Routes.URI_REST_ROLE + "/{id}", "test")
                        .content(objectMapper.writeValueAsString(updateRoleDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(MessageCode.SUCCESS.toString())))
                .andExpect(jsonPath("$.data.name", is("test")));
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {})
    @DisplayName("Update role - Access denied")
    void updateRole_AccessDenied() throws Exception {

        UpdateRoleDTO updateRoleDTO = UpdateRoleDTO.builder()
                .name("test")
                .status(Status.ACTIVE.getLabel())
                .build();

        mockMvc.perform(post(Routes.URI_REST_ROLE + "/{id}", "test")
                        .content(objectMapper.writeValueAsString(updateRoleDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("access_denied")))
        		.andExpect(jsonPath("$.error_description", is("Access is denied")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"DELETE_ROLE"})
    public void deleteRole_returnRole() throws Exception {

        doNothing().when(roleFacade).deleteRole(anyString(), anyString());

        mockMvc.perform(post(Routes.URI_REST_DELETE_ROLE, "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(MessageCode.SUCCESS.toString())));
    }
    
	@Test
	@WithMockUser(username = "admin", authorities = {})
	@DisplayName("Delete role - Access denied")
	void deleteRole_AccessDenied() throws Exception {
		mockMvc.perform(post(Routes.URI_REST_DELETE_ROLE, "test")).andExpect(status().isForbidden())
				.andExpect(jsonPath("$.error", is("access_denied")))
				.andExpect(jsonPath("$.error_description", is("Access is denied")));
	}
}
