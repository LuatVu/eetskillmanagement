package com.bosch.eet.skill.management.rest;

import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.bosch.eet.skill.management.dto.SkillCompetencyLeadDto;
import com.bosch.eet.skill.management.dto.SkillDto;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.exception.EETResponseException;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.service.CompetencyLeadService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DID8HC
 * @author TAY3HC
 */

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("dev")
class CompetencyLeadRestTest {

	@MockBean
	private CompetencyLeadService competencyLeadService;

	private final int PAGE = 0;
	private final int SIZE = 5;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("findSkillsByCompetencyLead happy case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void findSkillsByCompetencyLead() throws Exception {
		List<SkillDto> skillDtos = new ArrayList<>();
		SkillDto skillDto1 = SkillDto.builder().name("NETWORK").competency("DevOps")
				.id("02bd0deb-1801-42cd-ac24-b316ee117974").build();
		skillDtos.add(skillDto1);

		when(competencyLeadService.findSkillsByCompetencyLeadId("admin")).thenReturn(skillDtos);

		mockMvc.perform(get(Routes.URI_REST_COMPETENCY_LEAD_ID, "admin").param("page", String.valueOf(PAGE))
				.param("size", String.valueOf(SIZE))).andExpect(status().isOk())
				.andExpect(content().string(
						"{\"data\":[{\"name\":\"NETWORK\",\"competency\":\"DevOps\",\"skill_id\":\"02bd0deb-1801-42cd-ac24-b316ee117974\"}],\"code\":\"SUCCESS\"}"));
	}

	@Test
	@DisplayName("findfindSkillsByCompetencyLead empty skill case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void findSkillsByCompetencyLead_whenEmpty() throws Exception {
		List<SkillDto> skillDtos = new ArrayList<>();
		when(competencyLeadService.findSkillsByCompetencyLeadId("admin")).thenReturn(skillDtos);

		mockMvc.perform(get(Routes.URI_REST_COMPETENCY_LEAD_ID, "admin").param("page", String.valueOf(PAGE))
				.param("size", String.valueOf(SIZE))).andExpect(status().isOk())
				.andExpect(content().string("{\"data\":[],\"code\":\"SUCCESS\"}"));
	}

	@Test
	@DisplayName("findfindSkillsByCompetencyLead failed case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void findSkillsByCompetencyLeadEmptyCompetencyLead() throws Exception {
		when(competencyLeadService.findSkillsByCompetencyLeadId("0")).thenThrow(
				new EETResponseException(String.valueOf(NOT_FOUND.value()), "Cannot find competencylead", null));

		mockMvc.perform(get(Routes.URI_REST_COMPETENCY_LEAD_ID, "0").param("page", String.valueOf(PAGE)).param("size",
				String.valueOf(SIZE))).andExpect(status().is4xxClientError());
	}

	@Test
	@DisplayName("Create skill for competency lead happy case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void createSkillForCompetencyLead() throws Exception {
		List<SkillCompetencyLeadDto> skillCompetencyLeadDtos = new ArrayList<>();

		Skill skill = Skill.builder()
				.id("1")
				.name("Test skill")
				.status("Active")
				.build();

		List<String> skillIdList = Arrays.asList(skill.getStatus());

		List<String> skillNameList = Arrays.asList(skill.getName());

		SkillCompetencyLeadDto skillCompetencyLeadDto = SkillCompetencyLeadDto.builder()
				.personalId("admin")
				.displayName("Admin")
				.skillIds(skillIdList)
				.skillNames(skillNameList)
				.description("This is description")
				.build();

		skillCompetencyLeadDtos.add(skillCompetencyLeadDto);

		when(competencyLeadService.save(skillCompetencyLeadDtos))
				.thenReturn(0);

		log.info(Integer.toString(competencyLeadService.save(skillCompetencyLeadDtos)));

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_COMPETENCY_LEAD)
				.content(objectMapper.writeValueAsString(skillCompetencyLeadDtos))
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("Create skill for competency lead failed case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void createSkillsForCompetencyLead_Fail() throws Exception {
		when(competencyLeadService.save(Arrays.asList(SkillCompetencyLeadDto.builder().build())))
				.thenThrow(SkillManagementException.class);

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_COMPETENCY_LEAD)
				.content(objectMapper.writeValueAsString(Arrays.asList(SkillCompetencyLeadDto.builder().build())))
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}
}