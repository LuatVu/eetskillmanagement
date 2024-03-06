  package com.bosch.eet.skill.management.rest;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.AddSkillDto;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.PersonalProjectDto;
import com.bosch.eet.skill.management.elasticsearch.document.ResultQuery;
import com.bosch.eet.skill.management.elasticsearch.repo.PersonalElasticRepository;
import com.bosch.eet.skill.management.exception.EETResponseException;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.repo.PersonalProjectRepository;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.service.PersonalService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LUK1HC
 *
 */

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("dev")
class PersonalRestTests {

	@MockBean
	private PersonalService personalService;
	@MockBean
	private PersonalRepository personalRepository;
	@MockBean
	private PersonalProjectRepository personalProjectRepository;
	@MockBean
	private PersonalElasticRepository personalElasticRepository;

	private final int PAGE = 0;
	private final int SIZE = 5;
	private final Pageable pageable = PageRequest.of(PAGE, SIZE);

	@Autowired
	private MockMvc mockMvc;
	
    @Autowired
    private MessageSource messageSource;

	@DisplayName("Test associate not evaluate yet - Happy case")
	@Test
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void testAssociateNotEvaluateYet() throws Exception {
		List<String> person1SkillCluster = new ArrayList<>();
		person1SkillCluster.add("sk1");
		List<String> person2SkillCluster = new ArrayList<>();
		person2SkillCluster.add("sk1");
		person2SkillCluster.add("sk2");
		List<String> person3SkillCluster = new ArrayList<>();
		person3SkillCluster.add("sk1");
		person3SkillCluster.add("sk2");
		person3SkillCluster.add("sk3");
		
		List<PersonalDto> personalDtos = new ArrayList<>();
		personalDtos.add(PersonalDto.builder().id("psId1").name("ps1").level("L51").skillCluster(person1SkillCluster).team("team1")
				.experiencedNonBoschType(1).build());
		personalDtos.add(PersonalDto.builder().id("psId2").name("ps2").level("L50").skillCluster(person2SkillCluster).team("team1")
				.experiencedNonBoschType(0).build());
		
		when(personalService.findAssociateNotEvaluate("manager_id")).thenReturn(personalDtos);
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PERSON_NOT_EVALUATE)
				.contextPath("")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string("{" + "\"data\":["+ 
						"{" + "\"id\":\"psId1\"" + "\"name\":\"ps1\"," + "\"level\":\"L51\","
						+ "\"team\":\"team1\"," + "\"experienced_non_bosch_type\": 1," +  "\"skillCluster\": [\"sk1\"]," + "}," + 
						"{" + "\"id\":\"psId2\"" + "\"name\":\"ps2\"," + "\"level\":\"L50\","
						+ "\"team\":\"team3\"," + "\"experienced_non_bosch_type\": 0," + "\"skillCluster\": [\"sk1\",\"sk2\"]," + "},"
						+ "]," + "\"code\":\"SUCCESS\"" + "}"));
	}
	
	@Test
    @DisplayName("Find personal by id (happy case)")
	@WithMockUser(username = "admin", authorities = "ROLE_USER")
    void loadPersonalInfor_happycase() throws Exception {
    	String idP = "test";
    	String ntid="admin";
    	PersonalDto personalDto = PersonalDto.builder()
                .id(idP)
                .build();
		Set<String> auth = new HashSet<>();
		auth.add("ROLE_USER");
		when(personalService.findById(idP,ntid, auth )).thenReturn(personalDto);
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PERSON_ID, idP)
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string("{"
						+"\"id\":\"test\","
						+"\"experienced_non_bosch_type\":0"
						+"}"));
    }
    
    @Test
    @DisplayName("Find personal by id - permision for manager")
    @WithMockUser(username = "admin", authorities = "VIEW_ASSOCIATE_INFO_PERMISSION")
    void loadPersonalInfor_permissionForManager() throws Exception {
    	String idP = "test";
    	String ntid="admin";
    	PersonalDto personalDto = PersonalDto.builder()
                .id(idP)
                .build();
    	String[] permission = { Constants.VIEW_ASSOCIATE_INFO_PERMISSION };
		Set<String> auth = new HashSet<>(Arrays.asList(permission));
		when(personalService.findById(idP,ntid, auth )).thenReturn(personalDto);
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PERSON_ID, idP)
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andExpect(content().string("{"
						+"\"id\":\"test\","
						+"\"experienced_non_bosch_type\":0"
						+"}"));
    }
    
	@Test
	@WithMockUser(username = "admin", authorities = "ROLE_USER")
	@DisplayName("Find personal by id - access denied")
	void loadPersonalInfor_accessDenied() throws Exception {
		String idP = "test";
		String ntid = "admin";
		String[] permission = { "ROLE_USER" };
		Set<String> auth = new HashSet<>(Arrays.asList(permission));
		when(personalService.findById(idP, ntid, auth)).thenThrow(new AccessDeniedException(
				messageSource.getMessage(MessageCode.ACCESS_DENIED.toString(), null, LocaleContextHolder.getLocale())));
		mockMvc.perform(
				MockMvcRequestBuilders.get(Routes.URI_REST_PERSON_ID, idP).contentType(MediaType.APPLICATION_JSON)
						.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andExpect(jsonPath("$.code", is("NOT_AUTHORIZATION")))
				.andExpect(jsonPath("$.message", is("Access is denied")));
	}
    
	@Test
	@DisplayName("Find personal by id - not found")
	@WithMockUser(username = "admin", authorities = "ROLE_USER")
	void loadPersonalInfor_NotFound() throws Exception {
		String idP = "test";
		String ntid = "admin";
		String[] permission = { "ROLE_USER" };
		Set<String> auth = new HashSet<>(Arrays.asList(permission));
		when(personalService.findById(idP, ntid, auth)).thenThrow(
				new EETResponseException(String.valueOf(NOT_FOUND.value()), "Unable to find resource", null));
		mockMvc.perform(
				MockMvcRequestBuilders.get(Routes.URI_REST_PERSON_ID, idP).contentType(MediaType.APPLICATION_JSON)
						.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(jsonPath("$.code", is("404")))
				.andExpect(jsonPath("$.message", is("Unable to find resource")));
	}
	@DisplayName("Edit associates - Happy case")
	@Test
	@WithMockUser(username = "admin", authorities = "EDIT_ASSOCIATE_INFO_PERMISSION")
	void editAssociates_HappyCase() throws Exception {
		String personalId ="personalid";
		AddSkillDto addSkillDto = AddSkillDto.builder().build();
		doNothing().when(personalService).editAssociateInfo(addSkillDto);
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PERSON_EDIT, personalId)
				.content("{"
						+ "    \"personalDto\": {"
						+ "        \"id\": \"2bf665e5-e167-4caf-a7df-0173a9718980\","
						+ "        \"name\": \"DO PHAM TRUNG HIEU\","
						+ "        \"level_id\": \"be9c087a-4006-4640-826f-725f956d1892\","
						+ "        \"code\": \"DOI7HC\","
						+ "        \"gender\": \"M\","
						+ "        \"email\": \"Hieu.DoPhamTrung@vn.bosch.com\","
						+ "        \"department\": \"e6854c09-54f8-4e39-b31b-3ecb8b3c64a6\","
						+ "        \"group\": \"23bd2502-f45f-11ed-98de-0050569564ac\","
						+ "        \"team\": \"a5ab44b4-2487-4d80-a21c-a3e5c701319f\","
						+ "        \"title\": \"Senior Engineer\","
						+ "        \"location\": \"HCM\","
						+ "        \"joinDate\": \"2022-06-15\","
						+ "        \"manager\": \"361a4b59-fe5f-46eb-8e80-b1eaca796a03\","
						+ "        \"experienced_non_bosch\": \"0\","
						+ "        \"experienced_non_bosch_type\": 12"
						+ "    },"
						+ "    \"skill_group_ids\": ["
						+ "        \"7eab3beb-5c3b-4d58-8ee7-91991b6031f0\""
						+ "    ],"
						+ "    \"experienced_non_bosch\": 0"
						+ "}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.code", is("SUCCESS")));
	}
	
	@DisplayName("Edit associates - Acess Denied")
	@Test
	@WithMockUser(username = "admin", authorities = "ROLE_USER")
	void editAssociates_AcessDenied() throws Exception {
		String personalId ="personalid";
		AddSkillDto addSkillDto = AddSkillDto.builder().build();
		doNothing().when(personalService).editAssociateInfo(addSkillDto);
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PERSON_EDIT, personalId)
				.content("{"
						+ "    \"personalDto\": {"
						+ "        \"id\": \"2bf665e5-e167-4caf-a7df-0173a9718980\","
						+ "        \"name\": \"DO PHAM TRUNG HIEU\","
						+ "        \"level_id\": \"be9c087a-4006-4640-826f-725f956d1892\","
						+ "        \"code\": \"DOI7HC\","
						+ "        \"gender\": \"M\","
						+ "        \"email\": \"Hieu.DoPhamTrung@vn.bosch.com\","
						+ "        \"department\": \"e6854c09-54f8-4e39-b31b-3ecb8b3c64a6\","
						+ "        \"group\": \"23bd2502-f45f-11ed-98de-0050569564ac\","
						+ "        \"team\": \"a5ab44b4-2487-4d80-a21c-a3e5c701319f\","
						+ "        \"title\": \"Senior Engineer\","
						+ "        \"location\": \"HCM\","
						+ "        \"joinDate\": \"2022-06-15\","
						+ "        \"manager\": \"361a4b59-fe5f-46eb-8e80-b1eaca796a03\","
						+ "        \"experienced_non_bosch\": \"0\","
						+ "        \"experienced_non_bosch_type\": 12"
						+ "    },"
						+ "    \"skill_group_ids\": ["
						+ "        \"7eab3beb-5c3b-4d58-8ee7-91991b6031f0\""
						+ "    ],"
						+ "    \"experienced_non_bosch\": 0"
						+ "}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(content().string("{"
						+"\"error\":\"access_denied\","
						+"\"error_description\":\"Access is denied\""
						+"}"));
	}

	@DisplayName("Add associates - Happy case")
	@Test
	@WithMockUser(username = "admin", authorities = "EDIT_ASSOCIATE_INFO_PERMISSION")
	void addAssociates_HappyCase() throws Exception {
		AddSkillDto addSkillDto = AddSkillDto.builder().build();
		doNothing().when(personalService).addNewAssociate(addSkillDto);
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PERSON)
				.content("{" + "    \"personalDto\": {" + "        \"id\": \"2bf665e5-e167-4caf-a7df-0173a9718980\","
						+ "        \"name\": \"DO PHAM TRUNG HIEU\","
						+ "        \"level_id\": \"be9c087a-4006-4640-826f-725f956d1892\","
						+ "        \"code\": \"DOI7HC\"," + "        \"gender\": \"M\","
						+ "        \"email\": \"Hieu.DoPhamTrung@vn.bosch.com\","
						+ "        \"department\": \"e6854c09-54f8-4e39-b31b-3ecb8b3c64a6\","
						+ "        \"group\": \"23bd2502-f45f-11ed-98de-0050569564ac\","
						+ "        \"team\": \"a5ab44b4-2487-4d80-a21c-a3e5c701319f\","
						+ "        \"title\": \"Senior Engineer\"," + "        \"location\": \"HCM\","
						+ "        \"joinDate\": \"2022-06-15\","
						+ "        \"manager\": \"361a4b59-fe5f-46eb-8e80-b1eaca796a03\","
						+ "        \"experienced_non_bosch\": \"0\"," + "        \"experienced_non_bosch_type\": 12"
						+ "    }," + "    \"skill_group_ids\": [" + "        \"7eab3beb-5c3b-4d58-8ee7-91991b6031f0\""
						+ "    ]," + "    \"experienced_non_bosch\": 0" + "}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.code", is("ADD_ASSOCIATE_SUCCESSFUL")));
	}
	
	@DisplayName("Add associates - Acess Denied")
	@Test
	@WithMockUser(username = "admin", authorities = "ROLE_USER")
	void addAssociates_AcessDenied() throws Exception {
		AddSkillDto addSkillDto = AddSkillDto.builder().build();
		doNothing().when(personalService).addNewAssociate(addSkillDto);
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PERSON)
				.content("{" + "    \"personalDto\": {" + "        \"id\": \"2bf665e5-e167-4caf-a7df-0173a9718980\","
						+ "        \"name\": \"DO PHAM TRUNG HIEU\","
						+ "        \"level_id\": \"be9c087a-4006-4640-826f-725f956d1892\","
						+ "        \"code\": \"DOI7HC\"," + "        \"gender\": \"M\","
						+ "        \"email\": \"Hieu.DoPhamTrung@vn.bosch.com\","
						+ "        \"department\": \"e6854c09-54f8-4e39-b31b-3ecb8b3c64a6\","
						+ "        \"group\": \"23bd2502-f45f-11ed-98de-0050569564ac\","
						+ "        \"team\": \"a5ab44b4-2487-4d80-a21c-a3e5c701319f\","
						+ "        \"title\": \"Senior Engineer\"," + "        \"location\": \"HCM\","
						+ "        \"joinDate\": \"2022-06-15\","
						+ "        \"manager\": \"361a4b59-fe5f-46eb-8e80-b1eaca796a03\","
						+ "        \"experienced_non_bosch\": \"0\"," + "        \"experienced_non_bosch_type\": 12"
						+ "    }," + "    \"skill_group_ids\": [" + "        \"7eab3beb-5c3b-4d58-8ee7-91991b6031f0\""
						+ "    ]," + "    \"experienced_non_bosch\": 0" + "}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isForbidden())
						.andExpect(content().string("{"
								+"\"error\":\"access_denied\","
								+"\"error_description\":\"Access is denied\""
								+"}"));
	}
	
	@DisplayName("Get list associate from elasticsearch - Happy case")
	@Test
	@WithMockUser(username = "admin", authorities = "VIEW_ASSOCIATE_INFO_PERMISSION")
	void queryAssociateFromElasticSearch_HappyCase() throws Exception {
		String indexName = "personal";
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
	
	@DisplayName("Get list associate from elasticsearch - Access Denied")
	@Test
	@WithMockUser(username = "admin", authorities = "ROLE_USER")
	void queryAssociateFromElasticSearch_AcessDenied() throws Exception {
		String indexName = "personal";
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_ELASTIC_QUERY, indexName)
				.content("{" + "\"query\":\"\"," + "\"size\":100," + "\"from\":0" + "}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(content().string("{"
						+"\"error\":\"access_denied\","
						+"\"error_description\":\"Access is denied\""
						+"}"));
	}

	
	@DisplayName("Assign Bosch project - Happy case")
	@Test
	@WithMockUser(username = "admin", authorities = "EDIT_ASSOCIATE_INFO_PERMISSION")
	void assignBoschProject_HappyCase() throws Exception {
		String associateId = "abcxyz";
		PersonalProjectDto personalProjectDto = PersonalProjectDto.builder().id("iddummpy").status("On-going")
				.tasks(Collections.emptyList()).projectId("project_id_mock").roleId("1").teamSize("4")
				.pmName("NguyenDuyLong").skillTags(Collections.emptySet()).createdBy("admin").build();
		when(personalService.addPersonalProject(associateId, personalProjectDto))
				.thenReturn(personalProjectDto);
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PERSON_ID_PROJECT, associateId)
				.content("{" + "\"id\":\"iddummpy\"," + "\"status\":\"On-going\"," + "\"tasks\":[],"
						+ "\"project_id\":\"project_id_mock\"," + "\"role_id\":\"1\"," + "\"team_size\":\"4\","
						+ "\"pm_name\":\"NguyenDuyLong\"," + "\"skill_tags\":[" + "]" + "}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string("{" + "\"id\":\"iddummpy\"," + "\"status\":\"On-going\"," + "\"tasks\":[],"
						+ "\"createdBy\":\"admin\"," + "\"project_id\":\"project_id_mock\"," + "\"role_id\":\"1\","
						+ "\"team_size\":\"4\"," + "\"pm_name\":\"NguyenDuyLong\"," + "\"skill_tags\":[]" + "}"));
	}
	
	@DisplayName("Assign Bosch project - Access Denied")
	@Test
	@WithMockUser(username = "admin", authorities = "ROLE_USER")
	void assignBoschProject_AcessDenied() throws Exception {
		String associateId = "abcxyz";
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PERSON_ID_PROJECT, associateId)
				.content("{" + "\"id\":\"iddummpy\"," + "\"status\":\"On-going\"," + "\"tasks\":[],"
						+ "\"project_id\":\"project_id_mock\"," + "\"role_id\":\"1\"," + "\"team_size\":\"4\","
						+ "\"pm_name\":\"NguyenDuyLong\"," + "\"skill_tags\":[" + "]" + "}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(content().string(
						"{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
	}
}

