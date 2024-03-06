package com.bosch.eet.skill.management.rest;

import static org.mockito.Mockito.when;

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
import com.bosch.eet.skill.management.dto.SkillGroupDto;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.SkillFacade;
import com.bosch.eet.skill.management.service.SkillGroupService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/*
 * @author TAY3HC
 *
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("dev")
class SkillGroupRestTest {

    @MockBean
    private SkillGroupService skillGroupService;

    @MockBean
    private SkillFacade skillFacade;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Get list skill groups happy case")
    @Test
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
            + "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void getSkillGroups() throws Exception {
        List<SkillGroupDto> skillGroupList = new ArrayList<>();
        SkillGroupDto skillGroupDto = SkillGroupDto.builder()
                .id("1")
                .name("a")
                .build();
        skillGroupList.add(skillGroupDto);

        when(skillGroupService.findAllSkillGroups())
                .thenReturn(skillGroupList);

        log.info(skillGroupService.findAllSkillGroups().toString());

        mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_SKILL_GROUP)
                .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Get list skill groups empty case")
    @Test
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
            + "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void getSkillGroups_Empty() throws Exception {
        List<SkillGroupDto> skillGroupList = new ArrayList<>();

        when(skillGroupService.findAllSkillGroups())
                .thenReturn(skillGroupList);

        log.info(skillGroupService.findAllSkillGroups().toString());

        mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_SKILL_GROUP)
                .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Create skill group")
    @Test
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
            + "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void createSkillGroup() throws Exception {
        SkillGroupDto skillGroupDto = SkillGroupDto.builder()
                .id("1")
                .name("a")
                .build();

        when(skillGroupService.addSkillGroup(skillGroupDto))
                .thenReturn(skillGroupDto);

        mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_SKILL_GROUP)
                .content(objectMapper.writeValueAsString(skillGroupDto))
                .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Create skill group empty case")
    @Test
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
            + "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void createSkillGroup_Empty() throws Exception {
        SkillGroupDto skillGroupDto = SkillGroupDto.builder().build();

        when(skillGroupService.addSkillGroup(skillGroupDto))
                .thenThrow(SkillManagementException.class);

        mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_SKILL_GROUP)
                .contentType(objectMapper.writeValueAsString(skillGroupDto))
                .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    @DisplayName("Delete skill group")
    @Test
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
            + "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void deleteSkillGroup() throws Exception {
        when(skillFacade.deleteSkillGroup("1"))
                .thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete(Routes.URI_REST_SKILL_GROUP + "/1")
                .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Delete skill group empty case")
    @Test
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
            + "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
        void deleteSkillGroup_Fail() throws Exception {
        when(skillGroupService.findById("1"))
                .thenThrow(SkillManagementException.class);

        mockMvc.perform(MockMvcRequestBuilders.delete(Routes.URI_REST_SKILL_GROUP + "/1")
                .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }
}
