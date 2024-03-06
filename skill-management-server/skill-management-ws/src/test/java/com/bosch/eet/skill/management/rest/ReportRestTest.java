package com.bosch.eet.skill.management.rest;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.GbUnitDto;
import com.bosch.eet.skill.management.dto.GroupProjectBySkillTag;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.ReportDto;
import com.bosch.eet.skill.management.service.ReportService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("test")
public class ReportRestTest {

	@MockBean
	private ReportService reportService;
	
    @Autowired
    private MockMvc mockMvc;

	@Test
	@DisplayName("Report project")
	@WithMockUser(username = "admin", authorities = { Constants.VIEW_REPORT })
	void reportProject() throws Exception {
		Map<String, String> filter = new HashMap<>();
		GbUnitDto gbUnitDto = GbUnitDto.builder().name("GBUNIT").projects(1).build();
		ProjectDto projectDto = ProjectDto.builder().projects(1).status("New").build();
		GroupProjectBySkillTag groupProjectBySkillTag = GroupProjectBySkillTag.builder().skillTagName("Skilltag")
				.count(1l).build();
		ReportDto reportDto = ReportDto.builder().projects(1)
				.projectBySkillTags(Collections.singletonList(groupProjectBySkillTag))
				.projectsByGb(Collections.singletonList(gbUnitDto))
				.projectsByStatus(Collections.singletonList(projectDto)).build();
		when(reportService.generateProjectReport(filter)).thenReturn(reportDto);
		mockMvc.perform(get(Routes.URI_REST_REPORT_PROJECT)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.projects", is(1)))
				.andExpect(jsonPath("$.data.projects_by_status[0].status", is("New")))
				.andExpect(jsonPath("$.data.projects_by_status[0].projects", is(1)))
				.andExpect(jsonPath("$.data.projects_by_gb[0].name", is("GBUNIT")))
				.andExpect(jsonPath("$.data.projects_by_gb[0].projects", is(1)))
				.andExpect(jsonPath("$.data.projects_by_skill_tags[0].skillTagName", is("Skilltag")))
				.andExpect(jsonPath("$.data.projects_by_skill_tags[0].count", is(1)))
				.andExpect(jsonPath("$.code", is("SUCCESS")));
	}

	@Test
	@DisplayName("Report project - Access denied")
	@WithMockUser(username = "admin", authorities = {})
	void reportProject_AccessDenied() throws Exception {
		mockMvc.perform(get(Routes.URI_REST_REPORT_PROJECT)).andExpect(status().isForbidden()).andExpect(content()
				.string("{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
	}
}
