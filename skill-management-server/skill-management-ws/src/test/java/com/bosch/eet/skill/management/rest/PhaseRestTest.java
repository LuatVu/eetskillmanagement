package com.bosch.eet.skill.management.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.service.CommonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("dev")
public class PhaseRestTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private CommonService commonService;

	@Test
	@Transactional
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void testGetPhasesForDropdown() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PHASE_DROPDOWN))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(print());
	}
}
