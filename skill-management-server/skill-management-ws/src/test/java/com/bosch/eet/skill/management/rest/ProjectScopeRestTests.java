  package com.bosch.eet.skill.management.rest;

  import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
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
import com.bosch.eet.skill.management.dto.ProjectScopeDto;
import com.bosch.eet.skill.management.service.ProjectScopeService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

  /**
   * @author LUK1HC
   *
   */

  @Slf4j
  @SpringBootTest
  @AutoConfigureMockMvc()
  @ActiveProfiles("dev")
  class ProjectScopeRestTests {
      @MockBean
      private ProjectScopeService projectScopeService;

      @Autowired
      private MockMvc mockMvc;

      @DisplayName("Create ProjectScope - Happy case")
      @Test
      @WithMockUser(username = "admin", authorities = "VIEW_SYSTEM")
      void createProjectScope_HappyCase() throws Exception {
          ProjectScopeDto projectScopeDto = ProjectScopeDto.builder()
                  .name("testName").colour("#ffffff").hoverColour("#000000").build();
          when(projectScopeService.createNewProjectScope(projectScopeDto))
                  .thenReturn(projectScopeDto);

          Gson gson = new Gson();
          mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_SCOPE)
                  .content(gson.toJson(projectScopeDto))
                  .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                  .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
      }

      @DisplayName("Create ProjectScope - Access Denied")
      @Test
      @WithMockUser(username = "admin", authorities = "ROLE_USER")
      void createProjectScope_AccessDenied() throws Exception {
          ProjectScopeDto projectScopeDto = ProjectScopeDto.builder()
                  .name("testName").colour("#ffffff").hoverColour("#000000").build();
          when(projectScopeService.createNewProjectScope(projectScopeDto))
                  .thenReturn(projectScopeDto);

          Gson gson = new Gson();
          mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_SCOPE)
                  .content(gson.toJson(projectScopeDto))
                  .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                  .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isForbidden())
                  .andExpect(content().string(
          "{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
      }

      @DisplayName("Update ProjectScope - Happy case")
      @Test
      @WithMockUser(username = "admin", authorities = "VIEW_SYSTEM")
      void updateProjectScope_HappyCase() throws Exception {
          ProjectScopeDto projectScopeDto = ProjectScopeDto.builder()
                  .id("testId").name("testName").colour("#ffffff").hoverColour("#000000").build();
          when(projectScopeService.updateProjectScope(projectScopeDto))
                  .thenReturn(projectScopeDto);

          Gson gson = new Gson();
          mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_SCOPE_UPDATE)
                  .content(gson.toJson(projectScopeDto))
                  .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                  .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
      }

      @DisplayName("Update ProjectScope - Access Denied")
      @Test
      @WithMockUser(username = "admin", authorities = "ROLE_USER")
      void updateBoschProject_AccessDenied() throws Exception {
          ProjectScopeDto projectScopeDto = ProjectScopeDto.builder()
                  .name("testName").colour("#ffffff").hoverColour("#000000").build();
          when(projectScopeService.createNewProjectScope(projectScopeDto))
                  .thenReturn(projectScopeDto);

          Gson gson = new Gson();
          mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_SCOPE_UPDATE)
                  .content(gson.toJson(projectScopeDto))
                  .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                  .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isForbidden())
                  .andExpect(content().string(
          "{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
      }

      @DisplayName("Delete ProjectScope - Happy case")
      @Test
      @WithMockUser(username = "admin", authorities = "VIEW_SYSTEM")
      void deleteProjectScope_HappyCase() throws Exception {
          doNothing().when(projectScopeService).deleteProjectScope("testId");
          mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_SCOPE_DELETE, "testId")
                  .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                  .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
      }

      @DisplayName("Delete ProjectScope - Access Denied")
      @Test
      @WithMockUser(username = "admin", authorities = "ROLE_USER")
      void deleteBoschProject_AccessDenied() throws Exception {
          doNothing().when(projectScopeService).deleteProjectScope("testId");
          mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_SCOPE_DELETE, "testId")
                          .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                  .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isForbidden())
                  .andExpect(content().string(
          "{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
      }

      @DisplayName("Get all ProjectScope - Happy case")
      @Test
      @WithMockUser(username = "admin", authorities = "VIEW_SYSTEM")
      void getAlleProjectScope_HappyCase() throws Exception {
          ProjectScopeDto projectScopeDto = ProjectScopeDto.builder()
                  .id("testId").name("testName").colour("#ffffff").hoverColour("#000000").build();
          List<ProjectScopeDto> dtos = Collections.singletonList(projectScopeDto);
          when(projectScopeService.findAll())
                  .thenReturn(dtos);

          mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PROJECT_SCOPE)
                  .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                  .accept(MediaType.APPLICATION_JSON))
                  .andExpect(MockMvcResultMatchers.status().isOk());
      }

      @DisplayName("Get all ProjectScope - Access Denied")
      @Test
      void getAllBoschProject_AccessDenied() throws Exception {
          ProjectScopeDto projectScopeDto = ProjectScopeDto.builder()
                  .id("testId").name("testName").colour("#ffffff").hoverColour("#000000").build();
          List<ProjectScopeDto> dtos = Collections.singletonList(projectScopeDto);
          when(projectScopeService.findAll())
                  .thenReturn(dtos);

          mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PROJECT_SCOPE)
                          .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                          .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isUnauthorized())
                  .andExpect(content().string(
                          "{"
                                  + "\"error\":\"unauthorized\","
                                  + "\"error_description\":\"Full authentication is required to access this resource\""
                                  + "}"));
      }

  }

