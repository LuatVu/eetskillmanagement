package com.bosch.eet.skill.management.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.SkillEvaluationDto;
import com.bosch.eet.skill.management.dto.UpdateSkillEvaluationDto;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.service.SkillEvaluationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("dev")
class SkillEvaluationRestTest {

	@MockBean
	private SkillEvaluationService skillEvaluationService;

	private final int PAGE = 0;
	private final int SIZE = 5;

	@Autowired
	private MockMvc mockMvc;

	@DisplayName("Test update success")
	@Test
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void testUpdate() throws Exception {

		UpdateSkillEvaluationDto updateSkillEvaluationDto = UpdateSkillEvaluationDto.builder().id("id-test")
				.name("name test").approver("cb43b124-e79c-40e5-91fb-a7b1c1a16328").status("pending test")
				.level("level test").newLevel("new level test").build();

		UpdateSkillEvaluationDto skillUpdate = UpdateSkillEvaluationDto.builder().id("id-test").name("name test")
				.approver("cb43b124-e79c-40e5-91fb-a7b1c1a16328").status("pending test").level("level test")
				.newLevel("new level test").build();

		Personal personalApr = Personal.builder().id("approver if test").build();

		Set<PersonalSkill> personalSkills = new HashSet<PersonalSkill>();
		personalSkills
				.add(PersonalSkill.builder().id("Skill-id").skill(Skill.builder().name("name test").build()).build());

		Personal personalReq = Personal.builder().id("requester id test").personalSkills(personalSkills).build();

		SkillEvaluationDto skillEvaluationDto = SkillEvaluationDto.builder().id("id-test").approverDate("2022-12-09")
				.approver(personalApr.getId()).status("pending test").requester(personalReq.getId()).build();

		when(skillEvaluationService.findById("evaluate id")).thenReturn(skillEvaluationDto);

		when(skillEvaluationService.saveSkillEvaluation(updateSkillEvaluationDto)).thenReturn(skillUpdate);

		when(skillEvaluationService.saveForwardedSkillEvaluation(updateSkillEvaluationDto)).thenReturn(skillUpdate);

		mockMvc.perform(get(Routes.URI_REST_SKILL_EVALUATION_ID, "evaluate id").param("page", String.valueOf(PAGE))
				.param("size", String.valueOf(SIZE))).andExpect(status().isOk())
				.andExpect(content().string(
						"{\"id\":\"id-test\",\"requester\":\"requester id test\",\"approver\":\"approver if test\",\"status\":\"pending test\",\"approver_date\":\"2022-12-09\"}"))
				.andExpect(status().isOk());
	}

	@DisplayName("Test update success return null")
	@Test
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void testUpdate_nullSkillEvaluation() throws Exception {

		SkillEvaluationDto skillEvaluationDto = SkillEvaluationDto.builder().build();

		when(skillEvaluationService.findById("evaluate id")).thenReturn(skillEvaluationDto);

		mockMvc.perform(get(Routes.URI_REST_SKILL_EVALUATION_ID, "evaluate id").param("page", String.valueOf(PAGE))
				.param("size", String.valueOf(SIZE))).andExpect(status().isOk()).andExpect(content().string("{}"))
				.andExpect(status().isOk());
	}
}
