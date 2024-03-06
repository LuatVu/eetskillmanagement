package com.bosch.eet.skill.management.rest;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.nio.charset.StandardCharsets;

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
import com.bosch.eet.skill.management.elasticsearch.document.ResultQuery;
import com.bosch.eet.skill.management.elasticsearch.repo.PersonalElasticRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("dev")
class CourseRestTest {
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private PersonalElasticRepository personalElasticRepository;

    @DisplayName("Get list course from elastic search - Happy case")
	@Test
	@WithMockUser(username = "admin", authorities = "VIEW_DEPARTMENT_LEARNING")
	void queryCourseFromElasticSearch_HappyCase() throws Exception {
		String indexName = "course";
		ResultQuery resultQuery = ResultQuery.builder().timeTook(0.016f).numberOfResults(1).elements("").build();
		when(personalElasticRepository.searchFromQuery(indexName, "", 100, 0)).thenReturn(resultQuery);
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_ELASTIC_QUERY, indexName)
				.content("{" + "\"query\":\"\"," + "\"size\":100," + "\"from\":0" + "}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.code", is("SUCCESS")))
				.andExpect(content().string("{"
						+"\"data\":{"
						+"\"timeTook\":0.016,"
						+"\"numberOfResults\":1,"
						+"\"elements\":\"\""
						+"},"
						+"\"code\":\"SUCCESS\""
						+"}"));
	}
	
	@DisplayName("Get list course from elasticsearch - Access Denied")
	@Test
	@WithMockUser(username = "admin", authorities = "ROLE_USER")
	void queryCourseFromElasticSearch_AcessDenied() throws Exception {
		String indexName = "course";
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_ELASTIC_QUERY, indexName)
				.content("{" + "\"query\":\"\"," + "\"size\":100," + "\"from\":0" + "}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(content().string("{"
						+"\"error\":\"access_denied\","
						+"\"error_description\":\"Access is denied\""
						+"}"));
	}
}
