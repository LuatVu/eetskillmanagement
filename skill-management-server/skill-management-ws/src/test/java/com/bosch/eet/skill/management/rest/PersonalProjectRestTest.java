package com.bosch.eet.skill.management.rest;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.PersonalProjectDto;
import com.bosch.eet.skill.management.exception.EETResponseException;
import com.bosch.eet.skill.management.service.PersonalService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("dev")
class PersonalProjectRestTest {

    @MockBean
    private PersonalService personalService;

    private final int PAGE = 0;
    private final int SIZE = 5;
    private final Pageable pageable = PageRequest.of(PAGE, SIZE);


    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("findAll happy case")
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER," +
            "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void findAllPersonalProject() throws Exception {
        List<PersonalProjectDto> personalProjectDtos = new ArrayList<>();
        PersonalProjectDto personalProjectDto = new PersonalProjectDto();
        personalProjectDto.setName("test");
        personalProjectDtos.add(personalProjectDto);
        Page<PersonalProjectDto> pagePersonalProjectDtos = new PageImpl<>(personalProjectDtos);
        when(personalService.findProjectsByPersonalId("id",pageable)).thenReturn(pagePersonalProjectDtos);

        mockMvc.perform(get(Routes.URI_REST_PERSON_ID_PROJECT,"id")
                        .param("page", String.valueOf(PAGE))
                        .param("size", String.valueOf(SIZE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("test")));
    }
    @Test
    @DisplayName("findAll empty case")
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER," +
            "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void findAllProject_whenEmpty() throws Exception {
        List<PersonalProjectDto> personalProjectDtos = new ArrayList<>();
        Page<PersonalProjectDto> pagePersonalProjectDtos = new PageImpl<>(personalProjectDtos);
        when(personalService.findProjectsByPersonalId("id", pageable)).thenReturn(pagePersonalProjectDtos);

        mockMvc.perform(get(Routes.URI_REST_PERSON_ID_PROJECT, "id")
                        .param("page", String.valueOf(PAGE))
                        .param("size", String.valueOf(SIZE)))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    @DisplayName("find Project by ID")
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER," +
            "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void findAllProjectById() throws Exception {
        PersonalProjectDto personalProjectDto = PersonalProjectDto.builder()
                .name("project name")
                .teamSize("15")
                .id("id")
                .build();
        when(personalService.findPersonalProjectById("id","id2")).thenReturn(
                personalProjectDto
        );
        mockMvc.perform(get(Routes.URI_REST_PERSON_ID_PROJECT_ID, "id", "id2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("project name")))
                .andExpect(jsonPath("$.team_size", is("15")))
                .andExpect(jsonPath("$.id", is("id")));
    }

    @Test
    @DisplayName("find Project by ID empty case")
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER," +
            "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void findAllProjectById_whenEmpty() throws Exception {
        when(personalService.findPersonalProjectById("id","id2"))
                .thenThrow(new EETResponseException(String.valueOf(NOT_FOUND.value()), "Unable to find resource", null));
        mockMvc.perform(get(Routes.URI_REST_PERSON_ID_PROJECT_ID, "id","id2"))
                .andExpect(status().is4xxClientError());
    }
   

}
