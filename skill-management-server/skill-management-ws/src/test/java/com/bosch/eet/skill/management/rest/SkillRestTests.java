package com.bosch.eet.skill.management.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import com.bosch.eet.skill.management.dto.LevelExpected;
import com.bosch.eet.skill.management.dto.PersonalSkillDto;
import com.bosch.eet.skill.management.dto.SkillDto;
import com.bosch.eet.skill.management.dto.SkillExpectedLevelDto;
import com.bosch.eet.skill.management.dto.SkillExperienceLevelDTO;
import com.bosch.eet.skill.management.dto.SkillHighlightDto;
import com.bosch.eet.skill.management.dto.SkillManagementDto;
import com.bosch.eet.skill.management.elasticsearch.document.ResultQuery;
import com.bosch.eet.skill.management.elasticsearch.repo.PersonalElasticRepository;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.PersonalSkillRepository;
import com.bosch.eet.skill.management.repo.SkillLevelRepository;
import com.bosch.eet.skill.management.service.SkillService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("dev")
class SkillRestTests {

	@MockBean
	private SkillService skillService;
	
	@Mock
	private SkillService skillService1;
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Mock
    private PersonalRepository personalRepository;
    
    @Mock
    private PersonalSkillRepository personalSkillRepository;
    
    @Mock
    private SkillLevelRepository skillLevelRepository;
    
	@MockBean
	private PersonalElasticRepository personalElasticRepository;

	private final int PAGE = 0;
	private final int SIZE = 5;

	@Test
	@DisplayName("Find all skill experience levels happy case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void findSkillLevels_specificSkill() throws Exception {
		SkillExperienceLevelDTO skillExperienceLevelDTO = SkillExperienceLevelDTO.builder()
				.id("1")
				.name("Test")
				.description("This is a test")
				.build();

		SkillExperienceLevelDTO skillExperienceLevelDTO1 = SkillExperienceLevelDTO.builder()
				.id("2")
				.name("Another test")
				.description("Test 1 2 3 4")
				.build();

		SkillGroup skillGroup = SkillGroup.builder()
				.id("1")
				.name("Test group")
				.build();

		SkillDto.builder()
				.id("1")
				.name("Test skill")
				.skillGroup(skillGroup.getId())
				.build();

		List<SkillExperienceLevelDTO> skillExperienceLevelDtos = new ArrayList<>();

		skillExperienceLevelDtos.add(skillExperienceLevelDTO);
		skillExperienceLevelDtos.add(skillExperienceLevelDTO1);

		when(skillService.findAllSkillExperienceBySkillId("1"))
				.thenReturn(skillExperienceLevelDtos);

		log.info(skillService.findAllSkillExperienceBySkillId("1").toString());

		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_SKILL + "/1" + "/level")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}


	@Test
	@DisplayName("Get empty skill experience details of an existing skill")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void getEmptySkillExperience_existingSkill() throws Exception {
		SkillGroup skillGroup = SkillGroup.builder()
				.id("1")
				.name("Test group")
				.build();

		SkillDto.builder()
				.id("1")
				.name("This is a test")
				.skillGroup(skillGroup.getId())
				.build();

		new ArrayList<>();

		log.info(skillService.findAllSkillExperienceBySkillId("1").toString());

		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_SKILL + "/1" + "/level")
			.param("page", String.valueOf(PAGE))
			.param("size", String.valueOf(SIZE)).contentType(MediaType.APPLICATION_JSON)
			.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("Delete skill happy case")
	@WithMockUser(username = "admin", authorities = { Constants.VIEW_SYSTEM })
	void deleteSkill() throws Exception {
		String skillId = "skillid";

		when(skillService.deleteSkill(skillId)).thenReturn(Constants.SUCCESS);

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_DELETE_SKILL, skillId)
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.code", is("SUCCESS"))).andExpect(jsonPath("$.data", is("Success")));
	}

	@Test
	@DisplayName("Delete skill happy case - Access denied")
	@WithMockUser(username = "admin", authorities = {})
	void deleteSkill_AccessDenied() throws Exception {
		String skillId = "skillid";
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_DELETE_SKILL, skillId)
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(content().string(
						"{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
	}

	@Test
	@DisplayName("Delete skill - skill id not found")
	@WithMockUser(username = "admin", authorities = {Constants.VIEW_SYSTEM})
	void deleteSkill_Empty() throws Exception {
		
		when(skillService.findById("1"))
				.thenThrow(SkillManagementException.class);

		mockMvc.perform(MockMvcRequestBuilders.delete(Routes.URI_REST_SKILL + "/1"))
				.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}

	@Test
	@DisplayName("Edit skill happy case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void editSkill() throws Exception {
		List<SkillExperienceLevelDTO> skillExperienceLevelDTOS = new ArrayList<>();
		SkillExperienceLevelDTO skillExperienceLevelDTO = SkillExperienceLevelDTO.builder()
				.id("1")
				.name("Level 1")
				.description("Test description")
				.build();
		skillExperienceLevelDTOS.add(skillExperienceLevelDTO);

		SkillDto skillDto = SkillDto.builder()
				.id("1")
				.name("Test")
				.skillGroup("Test")
				.build();

		List<String> competencyLeadList = new ArrayList<>();
		competencyLeadList.add("Luan");

		SkillManagementDto skillManagementDto = SkillManagementDto.builder()
				.name("Test skill")
				.skillGroup("Test group")
				.skillExperienceLevels(skillExperienceLevelDTOS)
				.competencyLeads(competencyLeadList)
				.build();

		when(skillService.findById("1"))
				.thenReturn(skillDto);

		when(skillService.editNewSkill(skillManagementDto))
				.thenReturn(true);

		mockMvc.perform(MockMvcRequestBuilders.put(Routes.URI_REST_SKILL + "/1")
				.content(objectMapper.writeValueAsString(skillManagementDto))
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("Edit skill fail case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void editSkill_Fail() throws Exception {
		SkillDto skillDto = SkillDto.builder().build();

		List<SkillExperienceLevelDTO> skillExperienceLevelDTOS = new ArrayList<>();
		SkillExperienceLevelDTO skillExperienceLevelDTO = SkillExperienceLevelDTO.builder()
				.id("1")
				.name("Level 1")
				.description("Test description")
				.build();
		skillExperienceLevelDTOS.add(skillExperienceLevelDTO);

		List<String> competencyLeadList = new ArrayList<>();
		competencyLeadList.add("Luan");

		SkillManagementDto skillManagementDto = SkillManagementDto.builder()
				.name("Test skill")
				.skillGroup("Test group")
				.skillExperienceLevels(skillExperienceLevelDTOS)
				.competencyLeads(competencyLeadList)
				.build();

		when(skillService.findById("1"))
				.thenReturn(skillDto);

		when(skillService.editNewSkill(skillManagementDto))
				.thenReturn(false);

		mockMvc.perform(MockMvcRequestBuilders.put(Routes.URI_REST_SKILL + "/1")
				.content(objectMapper.writeValueAsString(skillManagementDto))
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}
	
	@Test
	@DisplayName("Find skills highlight (happy case)")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	public void findSkillHighlight_HappyCase() throws Exception {
		String idPersonal = "12";
		SkillHighlightDto psd1 = SkillHighlightDto.builder()
				.skillId("3")
				.level("1")
				.experience(3)
				.build();
		SkillHighlightDto psd2 = SkillHighlightDto.builder()
				.skillId("2")
				.level("1")
				.experience(1)
				.build();
		List<SkillHighlightDto> dtos = new ArrayList<SkillHighlightDto>();
		dtos.add(psd1);
		dtos.add(psd2);
		
		when(skillService.findSkillsHighlight(idPersonal)).thenReturn(dtos);;
		
		 mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_SKILL_HIGHLIGHT, idPersonal)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string("{\"data\":[{\"level\":\"1\",\"experience\":3,\"skill_id\":\"3\"}"
						+ ",{\"level\":\"1\",\"experience\":1,\"skill_id\":\"2\"}]"
						+ ",\"code\":\"SUCCESS\"}"));
	}
	
	@Test
	@DisplayName("find skills highlight with wrong personal id or not exist")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	public void findSkillsHighlight_WrongPersonalID() throws Exception {
		String idPersonal = "1";
		when(skillService.findSkillsHighlight(idPersonal)).thenReturn(new ArrayList<SkillHighlightDto>());

		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_SKILL_HIGHLIGHT, idPersonal)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string("{"
						+ "\"data\":[],"
						+ "\"code\":\"SUCCESS\""
						+ "}"));
	}
	
	@Test
	@DisplayName("Save skills highlight (happy case)")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	public void saveSkillHighlight_HappyCase() throws Exception {
		String idPersonal ="1";
		List<String> skillIds = new ArrayList<>();
		skillIds.add("j");

		List<PersonalSkillDto> skillHighlightDtos = new ArrayList<>();
		PersonalSkillDto s1 = PersonalSkillDto.builder().skillId("j").level("2").experience(3).build();
		skillHighlightDtos.add(s1);  	

		when(skillService.saveSkillsHighlight(idPersonal, skillIds)).thenReturn(skillHighlightDtos);
		assertThat(skillService.saveSkillsHighlight(idPersonal, skillIds).get(0).getSkillId()).isEqualTo("j");     
		
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_SAVE_SKILL_HIGHLIGHT, idPersonal)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content("[\r\n"
						+ "   \"j\"\r\n"
						+ "]")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());//because in call repository return null
	}
	
	@Test
	@DisplayName("Save skills highlight (no body)")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	public void saveSkillHighlight_NoBody() throws Exception {
		String idPersonal ="1";
		List<String> skillIds = new ArrayList<>();
		skillIds.add("j");

		List<PersonalSkillDto> skillHighlightDtos = new ArrayList<>();
		PersonalSkillDto s1 = PersonalSkillDto.builder().skillId("j").level("2").experience(3).build();
		skillHighlightDtos.add(s1);  	

		when(skillService.saveSkillsHighlight(idPersonal, skillIds)).thenReturn(skillHighlightDtos);
		
		assertThat(skillService.saveSkillsHighlight(idPersonal, skillIds).get(0).getSkillId()).isEqualTo( "j");     
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_SAVE_SKILL_HIGHLIGHT, idPersonal)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is5xxServerError());//because in call repository return null
	}
	
	@Test
	@DisplayName("Save skills highlight (skill id wrong)")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	public void saveSkillHighlight_idSkillWrong() throws Exception {
		String idPersonal ="1";
		List<String> skillIds = new ArrayList<>();
		skillIds.add("j");

		List<PersonalSkillDto> skillHighlightDtos = new ArrayList<>(); 	

		when(skillService.saveSkillsHighlight(idPersonal, skillIds)).thenReturn(skillHighlightDtos);
		
		assertThat(skillService.saveSkillsHighlight(idPersonal, skillIds)).isEmpty();	
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_SAVE_SKILL_HIGHLIGHT, idPersonal)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content("[\r\n"
						+ "   \"idwrong\"\r\n"
						+ "]")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());//because in call repository return null
	}
	
	@Test
	@DisplayName("Save skills highlight (personal id wrong)")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	public void saveSkillHighlight_PersonalIdWrong() throws Exception {
		List<String> skillIds = new ArrayList<>();
		skillIds.add("j");

		when(skillService.saveSkillsHighlight("idwrongornotexits", skillIds)).thenThrow(SkillManagementException.class);
		
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_SAVE_SKILL_HIGHLIGHT, "idwrongornotexits")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content("[\r\n"
						+ "   \"j\"\r\n"
						+ "]")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is5xxServerError());//because in call repository return null
	}

	@Test
	@DisplayName("Get skill expected - Happy case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	public void getSkillExpected_HappCase() throws Exception {
		int page = 0;
		int size = 10;
		List<String> skillCluster = new ArrayList<>();
		skillCluster.add("sk1");
		skillCluster.add("sk2");
		skillCluster.add("sk3");
		
		List<SkillExpectedLevelDto> skillExpectedLevel = new ArrayList<>();
		List<LevelExpected> levelExpecteds = new ArrayList<>();
		levelExpecteds.add( LevelExpected.builder().idLevel("idlevel1").nameLevel("L50").value(1).build());
		levelExpecteds.add( LevelExpected.builder().idLevel("idlevel2").nameLevel("L51").value(0).build());
		SkillExpectedLevelDto seld1 = SkillExpectedLevelDto.builder().idSkill("id1").nameSkill("skill1").levelExpecteds(levelExpecteds).build();
		skillExpectedLevel.add(seld1);
		
		Map<String, Object> mapResponse = new HashMap<>();
		mapResponse.put(Constants.SKILLS, skillExpectedLevel);
		mapResponse.put(Constants.TOTAL_PAGE, 3);
		mapResponse.put(Constants.TOTAL_ITEM, skillExpectedLevel.size());
		
		when(skillService.getSkillLevelExpected(page, size, skillCluster)).thenReturn(mapResponse);

		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_SKILL_EXPECTED)
				.param("page", String.valueOf(0)).param("size", String.valueOf(10))
				.param("skillCluster", "sk1,sk2,sk3")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string("{" + "\"data\":{" + "\"skills\":[" + "{" + "\"idSkill\":\"id1\","
						+ "\"nameSkill\":\"skill1\"," + "\"levelExpecteds\":[" + "{" + "\"idLevel\":\"idlevel1\","
						+ "\"nameLevel\":\"L50\"," + "\"value\":1" + "}," + "{" + "\"idLevel\":\"idlevel2\","
						+ "\"nameLevel\":\"L51\"," + "\"value\":0" + "}" + "]" + "}" + "]," + "\"totalItem\":1,"
						+ "\"totalPage\":3" + "}," + "\"code\":\"SUCCESS\"" + "}"));
	}
	
	@Test
	@DisplayName("Get skill expected - not skill cluster")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	public void getSkillExpected_NotSkillCluster() throws Exception {
		int page = 0;
		int size = 10;
		
		List<SkillExpectedLevelDto> skillExpectedLevel = new ArrayList<>();
		List<LevelExpected> levelExpecteds = new ArrayList<>();
		levelExpecteds.add( LevelExpected.builder().idLevel("idlevel1").nameLevel("L50").value(1).build());
		levelExpecteds.add( LevelExpected.builder().idLevel("idlevel2").nameLevel("L51").value(0).build());
		SkillExpectedLevelDto seld1 = SkillExpectedLevelDto.builder().idSkill("id1").nameSkill("skill1").levelExpecteds(levelExpecteds).build();
		skillExpectedLevel.add(seld1);
		
		Map<String, Object> mapResponse = new HashMap<>();
		mapResponse.put(Constants.SKILLS, skillExpectedLevel);
		mapResponse.put(Constants.TOTAL_PAGE, 3);
		mapResponse.put(Constants.TOTAL_ITEM, skillExpectedLevel.size());
		
		when(skillService.getSkillLevelExpected(page, size, null)).thenReturn(mapResponse);

		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_SKILL_EXPECTED)
				.param("page", String.valueOf(0)).param("size", String.valueOf(10))
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string("{" + "\"data\":{" + "\"skills\":[" + "{" + "\"idSkill\":\"id1\","
						+ "\"nameSkill\":\"skill1\"," + "\"levelExpecteds\":[" + "{" + "\"idLevel\":\"idlevel1\","
						+ "\"nameLevel\":\"L50\"," + "\"value\":1" + "}," + "{" + "\"idLevel\":\"idlevel2\","
						+ "\"nameLevel\":\"L51\"," + "\"value\":0" + "}" + "]" + "}" + "]," + "\"totalItem\":1,"
						+ "\"totalPage\":3" + "}," + "\"code\":\"SUCCESS\"" + "}"));
	}
	
	@Test
	@DisplayName("Save skill level - Happy case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	public void saveSkillLevel_HappCase() throws Exception {
		
		List<SkillExpectedLevelDto> skillExpectedLevel = new ArrayList<>();
		List<LevelExpected> levelExpecteds = new ArrayList<>();
		levelExpecteds.add( LevelExpected.builder().idLevel("idlevel1").value(1).build());
		levelExpecteds.add( LevelExpected.builder().idLevel("idlevel2").value(0).build());
		SkillExpectedLevelDto seld1 = SkillExpectedLevelDto.builder().idSkill("id1").nameSkill("skill1").levelExpecteds(levelExpecteds).build();
		skillExpectedLevel.add(seld1);
		
		when(skillService.updateSkillLevel(skillExpectedLevel)).thenReturn(Constants.DONE);

		mockMvc.perform(MockMvcRequestBuilders.put(Routes.URI_REST_SKILL_EXPECTED_UPDATE)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content("[" + "{" + "\"idSkill\":\"id1\"," + "\"levelExpecteds\":[" + "{" + "\"idLevel\":\"idlevel1\","
						+ "\"value\":1" + "}," + "{" + "\"idLevel\":\"idlevel2\"," + "\"value\":0" + "}" + "]" + "}"
						+ "]")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string("{" + "    \"data\": \"Done\"," + "    \"code\": \"SUCCESS\"" + "}"));
	}
	
	@DisplayName("Get list skill from elastic search - Happy case")
	@Test
	@WithMockUser(username = "admin", authorities = "VIEW_ALL_PROJECTS")
	void querySkillFromElasticSearch_HappyCase() throws Exception {
		String indexName = "skill";
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
	
	@DisplayName("Get list skill from elasticsearch - Access Denied")
	@Test
	@WithMockUser(username = "admin", authorities = "ROLE_USER")
	void querySkillFromElasticSearch_AcessDenied() throws Exception {
		String indexName = "skill";
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
