/**
 * 
 */
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

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.SkillTagDto;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.service.SkillTagService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author VOU6HC
 */

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("dev")
class SkillTagRestTest {

	@MockBean
    private SkillTagService skillTagService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Get list skill tags with positive case")
    @Test
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
            + "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void getSkillTags() throws Exception {
        List<SkillTagDto> skillTagsList = new ArrayList<>();
        SkillTagDto skillTagDto = SkillTagDto.builder()
                .id("testID-1")
                .name("Test skill tag name - 1")
                .build();
        skillTagsList.add(skillTagDto);

        when(skillTagService.findAllSkillTags())
                .thenReturn(skillTagsList);

        log.info(skillTagService.findAllSkillTags().toString());

        mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_SKILL_TAG)
                .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Get list skill tags with empty case")
    @Test
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
            + "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void getSkillTags_Empty() throws Exception {
    	List<SkillTagDto> skillTagsList = new ArrayList<>();

        when(skillTagService.findAllSkillTags())
                .thenReturn(skillTagsList);

        log.info(skillTagService.findAllSkillTags().toString());

        mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_SKILL_TAG)
                .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Create skill tag")
    @Test
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
            + "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void createSkillTag() throws Exception {
        SkillTagDto skillTagDto = SkillTagDto.builder()
        		.id("testID-1")
                .name("Test skill tag name - 1")
                .build();

        when(skillTagService.addSkillTag(skillTagDto))
                .thenReturn(skillTagDto);

        mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_SKILL_TAG)
                .content(objectMapper.writeValueAsString(skillTagDto))
                .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Create skill tag with empty case")
    @Test
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
            + "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void createSkillTag_Empty() throws Exception {
    	SkillTagDto skillTagDto = SkillTagDto.builder().build();

        when(skillTagService.addSkillTag(skillTagDto))
                .thenThrow(SkillManagementException.class);

        mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_SKILL_TAG)
                .contentType(objectMapper.writeValueAsString(skillTagDto))
                .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    @DisplayName("Delete skill tag")
    @Test
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
            + "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void deleteSkillTag() throws Exception {
    	SkillTagDto skillTagDto = SkillTagDto.builder()
    			.id("testID-1")
                .name("Test skill tag name - 1")
                .build();

        when(skillTagService.findById("testID-1"))
                .thenReturn(skillTagDto);

        when(skillTagService.deleteSkillTag(skillTagDto))
                .thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_SKILL_TAG + "/testID-1")
                .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Delete skill group with empty case")
    @Test
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
            + "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
        void deleteSkillTag_Fail() throws Exception {
        when(skillTagService.findById("testID-1"))
                .thenThrow(SkillManagementException.class);

        mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_SKILL_TAG + "/testID-1")
                .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }
    
    @DisplayName("Update order skill tag")
    @Test
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
            + "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
        void updateOrderSkillTag() throws Exception {
        SkillTagDto skillTagDto = SkillTagDto.builder()
                .id("1")
                .name("a")
                .order(1l)
                .build();
        List<SkillTagDto> skillTagDtos = new ArrayList<>();
        skillTagDtos.add(skillTagDto);
        
        when(skillTagService.updateOrder(skillTagDtos))
                .thenReturn(Constants.DONE);

        mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_SKILL_TAG_UPDATE_ORDER)
                .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsString(skillTagDtos))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
