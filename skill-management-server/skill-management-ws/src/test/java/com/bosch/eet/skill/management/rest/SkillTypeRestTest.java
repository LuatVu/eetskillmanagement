/**
 * 
 */
package com.bosch.eet.skill.management.rest;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
import com.bosch.eet.skill.management.dto.SkillTypeDto;
import com.bosch.eet.skill.management.service.SkillTypeService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author VOU6HC
 */

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("dev")
class SkillTypeRestTest {

	@MockBean
    private SkillTypeService skillTypeService;

    @Autowired
    private MockMvc mockMvc;

	@DisplayName("Get all skill type - Happy case")
	@Test
	@WithMockUser(username = "admin", authorities = { Constants.VIEW_EXPECTED_SKILL_LEVEL })
	void findAllSkillType() throws Exception {
		SkillTypeDto skillTypeDto1 = SkillTypeDto.builder().id("idtest1").name("Behavioral").build();
		SkillTypeDto skillTypeDto2 = SkillTypeDto.builder().id("idtest2").name("Technical").build();
		List<SkillTypeDto> skillTypeDtos = new ArrayList<>();
		skillTypeDtos.add(skillTypeDto1);
		skillTypeDtos.add(skillTypeDto2);

		when(skillTypeService.findAllSkillType()).thenReturn(skillTypeDtos);

		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_SKILL_TYPE).contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string("{" + "\"data\":[" + "{" + "\"id\":\"idtest1\"," + "\"name\":\"Behavioral\""
						+ "}," + "{" + "\"id\":\"idtest2\"," + "\"name\":\"Technical\"" + "}" + "],"
						+ "\"code\":\"SUCCESS\"" + "}"));
	}

	@DisplayName("Get all skill type - Access denied")
	@Test
	@WithMockUser(username = "admin", authorities = {})
	void findAllSkillType_AccessDenied() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_SKILL_TYPE).contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(jsonPath("$.error", is("access_denied")))
				.andExpect(jsonPath("$.error_description", is("Access is denied")));
	}
}
