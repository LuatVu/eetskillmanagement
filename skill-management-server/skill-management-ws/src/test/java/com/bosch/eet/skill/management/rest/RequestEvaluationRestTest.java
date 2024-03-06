package com.bosch.eet.skill.management.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.RequestEvaluationDetailDto;
import com.bosch.eet.skill.management.dto.RequestEvaluationDto;
import com.bosch.eet.skill.management.dto.RequestEvaluationHistoricalDto;
import com.bosch.eet.skill.management.dto.RequestEvaluationPendingDto;
import com.bosch.eet.skill.management.dto.SkillDescriptionDTO;
import com.bosch.eet.skill.management.dto.SkillLevelDTO;
import com.bosch.eet.skill.management.dto.UpdateRequestEvaluationDto;
import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.RequestEvaluation;
import com.bosch.eet.skill.management.entity.RequestEvaluationDetail;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.service.RequestEvaluationService;
import com.bosch.eet.skill.management.usermanagement.consts.MessageCode;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("dev")
class RequestEvaluationRestTest {

	@MockBean
	private RequestEvaluationService requestEvaluationService;

	@Mock
	private UserServiceImpl userService;

	private final int PAGE = 0;
	private final int SIZE = 5;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MessageSource messageSource;

	@Test
	@DisplayName("Find request evaluation detail happy case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void findRequestEvaluationDetail() throws Exception {
		RequestEvaluationDto requestEvaluationDto = RequestEvaluationDto.builder()
				.id("1")
				.approver("hihi")
				.requester("requester")
				.comment("jztr")
				.status("APPROVAL_PENDING")
				.build();

		when(requestEvaluationService.findByID("1"))
				.thenReturn(requestEvaluationDto);

		mockMvc.perform(get(Routes.URI_REST_REQUEST_EVALUATION + "/1")
			.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
			.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string("{\"data\":{\"id\":\"1\",\"requester\":\"requester\",\"approver\":\"hihi\",\"status\":\"APPROVAL_PENDING\",\"comment\":\"jztr\"},\"code\":\"SUCCESS\"}"));
	}

	@Test
	@DisplayName("Find request evaluation detail empty case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void findRequestEvaluationDetail_Empty() throws Exception {
		when(requestEvaluationService.findByID("123"))
				.thenReturn(RequestEvaluationDto.builder().build());

		mockMvc.perform(get(Routes.URI_REST_REQUEST_EVALUATION + "/123")
			.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
			.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string("{\"data\":{},\"code\":\"SUCCESS\"}"));
	}

	@Test
	@DisplayName("Find request evaluation detail throw exception case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void findRequestEvaluationDetail_ThrowException() throws Exception {
		when(requestEvaluationService.findByID("test"))
				.thenThrow(new SkillManagementException(MessageCode.SKM_REQUEST_EVALUATION_NOT_FOUND.toString(),
						messageSource.getMessage(MessageCode.SKM_REQUEST_EVALUATION_NOT_FOUND.toString(), null,
								LocaleContextHolder.getLocale()), null, HttpStatus.NOT_FOUND));

		mockMvc.perform(get(Routes.URI_REST_REQUEST_EVALUATION + "/test")).andExpect(status().is5xxServerError());
	}

	@Test
	@DisplayName("Get requests as requester happy case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void getRequestsRequester() throws Exception {
		Map<String, String> map = new HashMap<>();

		SkillDescriptionDTO skillDescriptionDTO = SkillDescriptionDTO.builder()
				.id("1")
				.skillId("1")
				.skillName("Test skill")
				.level("2")
				.expectedLevel("3")
				.competency("Back-end")
				.comment("This is comment")
				.experience(1)
				.status("Active")
				.build();

		SkillLevelDTO skillLevelDTO = SkillLevelDTO.builder()
				.level("2")
				.description("This is description")
				.build();
		List<SkillLevelDTO> skillLevelDTOS = new ArrayList<>();
		skillLevelDTOS.add(skillLevelDTO);

		skillDescriptionDTO.setSkillLevelDTOS(skillLevelDTOS);

		List<SkillDescriptionDTO> skillDescriptionDTOS = new ArrayList<>();

		skillDescriptionDTOS.add(skillDescriptionDTO);

		RequestEvaluationDto requestEvaluationDto = RequestEvaluationDto.builder()
				.id("1")
				.approver("hihi")
				.requester("requester")
				.comment("jztr")
				.status("APPROVAL_PENDING")
				.createdDate("2022-12-25")
				.updateDate("2022-12-26")
				.requestEvaluationDetails(skillDescriptionDTOS)
				.build();

		map.put("requester_id", "admin");
//		map.put("approver_id", "hihi");

		List<RequestEvaluationDto> requestEvaluationDtos = new ArrayList<>();
		requestEvaluationDtos.add(requestEvaluationDto);
		List<RequestEvaluationDto> pageRequestEvaluationDtos = new ArrayList<>();

		when(requestEvaluationService.findAll(map))
				.thenReturn(pageRequestEvaluationDtos);

		mockMvc.perform(get(Routes.URI_REST_REQUEST_EVALUATION)
				.param("requester", "true")
				.param("page", String.valueOf(PAGE))
				.param("size", String.valueOf(SIZE))
			.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
			.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string("{\"data\":[{\"id\":\"1\",\"requester\":\"requester\",\"approver\":\"hihi\",\"status\":\"APPROVAL_PENDING\",\"comment\":\"jztr\",\"request_evaluation_details\":[{\"id\":\"1\",\"comment\":\"This is comment\",\"experience\":1,\"status\":\"Active\",\"skill_id\":\"1\",\"skill\":\"Test skill\",\"current_level\":\"2\",\"expected_level\":\"3\",\"skill_group\":\"Back-end\",\"skill_description\":[{\"level\":\"2\",\"description\":\"This is description\"}]}],\"created_date\":\"2022-12-25\",\"updated_date\":\"2022-12-26\"}],\"code\":\"SUCCESS\"}"));
	}

	@Test
	@DisplayName("Get requests as approver happy case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void getRequestsApprover() throws Exception {
		Map<String, String> map = new HashMap<>();

		SkillDescriptionDTO skillDescriptionDTO = SkillDescriptionDTO.builder()
				.id("1")
				.skillId("1")
				.skillName("Test skill")
				.level("2")
				.expectedLevel("3")
				.competency("Back-end")
				.comment("This is comment")
				.experience(1)
				.status("Active")
				.build();

		SkillLevelDTO skillLevelDTO = SkillLevelDTO.builder()
				.level("2")
				.description("This is description")
				.build();
		List<SkillLevelDTO> skillLevelDTOS = new ArrayList<>();
		skillLevelDTOS.add(skillLevelDTO);

		skillDescriptionDTO.setSkillLevelDTOS(skillLevelDTOS);

		List<SkillDescriptionDTO> skillDescriptionDTOS = new ArrayList<>();

		skillDescriptionDTOS.add(skillDescriptionDTO);

		RequestEvaluationDto requestEvaluationDto = RequestEvaluationDto.builder()
				.id("1")
				.approver("hihi")
				.requester("requester")
				.comment("jztr")
				.status("APPROVAL_PENDING")
				.createdDate("2022-12-25")
				.updateDate("2022-12-26")
				.requestEvaluationDetails(skillDescriptionDTOS)
				.build();

		map.put("approver_id", "admin");

		List<RequestEvaluationDto> requestEvaluationDtos = new ArrayList<>();
		requestEvaluationDtos.add(requestEvaluationDto);

		when(requestEvaluationService.findAll(map))
				.thenReturn(requestEvaluationDtos);

		mockMvc.perform(get(Routes.URI_REST_REQUEST_EVALUATION)
				.param("requester", "false")
				.param("page", String.valueOf(PAGE))
				.param("size", String.valueOf(SIZE))
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string("{\"data\":[{\"id\":\"1\",\"requester\":\"requester\",\"approver\":\"hihi\",\"status\":\"APPROVAL_PENDING\",\"comment\":\"jztr\",\"request_evaluation_details\":[{\"id\":\"1\",\"comment\":\"This is comment\",\"experience\":1,\"status\":\"Active\",\"skill_id\":\"1\",\"skill\":\"Test skill\",\"current_level\":\"2\",\"expected_level\":\"3\",\"skill_group\":\"Back-end\",\"skill_description\":[{\"level\":\"2\",\"description\":\"This is description\"}]}],\"created_date\":\"2022-12-25\",\"updated_date\":\"2022-12-26\"}],\"code\":\"SUCCESS\"}"));
	}

	@Test
	@DisplayName("Get request empty case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void getRequests_Empty() throws Exception {
		PageRequest.of(0, 5);
		Map<String, String> hashMap = new HashMap<>();
		hashMap.put("approver_id", "admin");

		RequestEvaluationDto requestEvaluationDto = RequestEvaluationDto.builder().build();
		List<RequestEvaluationDto> requestEvaluationDtos = new ArrayList<>();
		requestEvaluationDtos.add(requestEvaluationDto);

		when(requestEvaluationService.findAll(hashMap))
				.thenReturn(requestEvaluationDtos);

		mockMvc.perform(get(Routes.URI_REST_REQUEST_EVALUATION)
				.param("requester", "false")
				.param("page", String.valueOf(PAGE))
				.param("size", String.valueOf(SIZE))
			.content(objectMapper.writeValueAsString(requestEvaluationDtos))
			.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
			.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string("{\"data\":[{}],\"code\":\"SUCCESS\"}"));
	}


	@Test
	@DisplayName("Update request evaluation empty case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void updateRequestDetail_EmptyCase() throws Exception {
		SkillDescriptionDTO skillDescriptionDTO = SkillDescriptionDTO.builder()
				.id("1")
				.skillId("1")
				.skillName("Test skill")
				.level("2")
				.expectedLevel("3")
				.competency("Back-end")
				.comment("This is comment")
				.experience(1)
				.status("Active")
				.build();

		SkillLevelDTO skillLevelDTO = SkillLevelDTO.builder()
				.level("2")
				.description("This is description")
				.build();
		List<SkillLevelDTO> skillLevelDTOS = new ArrayList<>();
		skillLevelDTOS.add(skillLevelDTO);

		skillDescriptionDTO.setSkillLevelDTOS(skillLevelDTOS);

		List<SkillDescriptionDTO> skillDescriptionDTOS = new ArrayList<>();

		skillDescriptionDTOS.add(skillDescriptionDTO);
		
    	UpdateRequestEvaluationDto requestEvaluationDto = UpdateRequestEvaluationDto.builder()
    			.approver("Phat")
    			.comment("hehe")
    			.status("PENDING")
    			.build();
		
//		when(requestEvaluationService.updateRequestDetail(requestEvaluationDetailDto.getId(), requestEvaluationDto))
//		.thenReturn(requestDto);
		
		mockMvc.perform(MockMvcRequestBuilders.put(Routes.URI_REST_REQUEST_EVALUATION)
				.content(objectMapper.writeValueAsString(requestEvaluationDto))
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is5xxServerError());

	}
	
	@Test
	@DisplayName("Notification request pending for mananger _ happy case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	public void notificationRequestPending_HappyCase() throws Exception {
		String personalId ="idtest";
		RequestEvaluationPendingDto repd = RequestEvaluationPendingDto.builder()
				.displayName("Unknow")
				.mail("404@gmail.com")
				.count(10l).build();
		when(requestEvaluationService.notificationRequestPending(personalId)).thenReturn(repd);
		
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_REQUEST_EVALUATION_PENDING, personalId)
				.characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("{\"data\":{\"displayName\":\"Unknow\",\"mail\":\"404@gmail.com\",\"count\":10},\"code\":\"SUCCESS\"}"));
				
	}
	
	
	@Test
	@DisplayName("Notification request pending for mananger _ wrong personal id")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	public void notificationRequestPending_WrongPersonalId() throws Exception {
		String personalId ="idtest";
		RequestEvaluationPendingDto repd = RequestEvaluationPendingDto.builder()
				.count(0l)
				.build();
		when(requestEvaluationService.notificationRequestPending(personalId)).thenReturn(repd);
		
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_REQUEST_EVALUATION_PENDING, personalId)
				.characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("{\"data\":{\"count\":0},\"code\":\"SUCCESS\"}"));
				
	}
	
	@Test
	@DisplayName("Notification request pending for mananger _ not passing personal id")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	public void notificationRequestPending_NotPassingPersonalId() throws Exception {
		
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_REQUEST_EVALUATION_PENDING,"")
				.characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is5xxServerError());
				
	}

	@Test
	@DisplayName("approve/reject request evaluation detail")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void approveOrRejectRequestEvaluationDetail() throws Exception {
		RequestEvaluationDetailDto requestEvaluationDetailDto = RequestEvaluationDetailDto.builder()
				.id("test_request_detail_1")
				.status(Constants.APPROVED)
				.build();

		RequestEvaluationDetail requestEvaluationDetail = RequestEvaluationDetail.builder()
				.id("test request detail 1")
				.status(Constants.PENDING)
				.build();

		RequestEvaluation.builder()
				.id("test_request")
				.comment("test comment 1")
				.build();

		when(requestEvaluationService.updateRequestDetail(requestEvaluationDetail.getId(), requestEvaluationDetailDto))
						.thenReturn(requestEvaluationDetailDto);

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_REQUEST_EVALUATION + "/test_request" + "/test_request_detail_1/update")
				.content(objectMapper.writeValueAsString(requestEvaluationDetailDto))
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	@DisplayName("approve/reject request evaluation")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void approveOrRejectRequestEvaluation() throws Exception {
		User.builder()
				.name("approver")
				.id("approverId")
				.build();

		UserDTO.builder()
				.name("approver")
				.id("approverId")
				.build();

		Personal.builder()
				.id("approverId")
				.personalNumber("12345678")
				.personalCode("ABC1HC")
				.level(Level.builder().build())
				.title("Title")
				.build();

		RequestEvaluation.builder()
				.id("test_request")
				.comment("test comment 1")
				.build();

		RequestEvaluationDto requestEvaluationDto = RequestEvaluationDto.builder()
				.id("test_request")
				.comment("test comment 1")
				.status(Constants.APPROVED)
				.build();

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_REQUEST_EVALUATION + "/test_request" + "/update")
				.content(objectMapper.writeValueAsString(requestEvaluationDto))
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Get requests evaluated happy case")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	void getRequestsEvaluated() throws Exception {
		SkillDescriptionDTO skillDescriptionDTO = SkillDescriptionDTO.builder()
				.id("1")
				.skillId("1")
				.skillName("Test skill")
				.level("2")
				.expectedLevel("3")
				.competency("Back-end")
				.comment("This is comment")
				.experience(1)
				.status("Active")
				.build();

		List<SkillDescriptionDTO> skillDescriptionDTOS = new ArrayList<>();

		skillDescriptionDTOS.add(skillDescriptionDTO);

		RequestEvaluationDto requestEvaluationDto = RequestEvaluationDto.builder()
				.id("1")
				.approver("hihi")
				.requester("requester")
				.comment("jztr")
				.status("APPROVED")
				.createdDate("2023-07-05")
				.updateDate("2023-07-05")
				.approvedDate("2023-07-06")
				.competencyLeadEvaluateAll("")
				.build();

		List<RequestEvaluationDto> requestEvaluationDtos = new ArrayList<>();
		requestEvaluationDtos.add(requestEvaluationDto);

		when(requestEvaluationService.findRequestEvaluated("admin"))
				.thenReturn(requestEvaluationDtos);

		mockMvc.perform(get(Routes.URI_REST_REQUEST_EVALUATED)
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string("{"
						+"\"data\":["
						+"{"
						+"\"id\":\"1\","
						+"\"requester\":\"requester\","
						+"\"approver\":\"hihi\","
						+"\"status\":\"APPROVED\","
						+"\"comment\":\"jztr\","
						+"\"competencyLeadEvaluateAll\":\"\","
						+"\"created_date\":\"2023-07-05\","
						+"\"updated_date\":\"2023-07-05\","
						+"\"approved_date\":\"2023-07-06\""
						+"}"
						+"],"
						+"\"code\":\"SUCCESS\""
						+"}"));
	}
	
	@Test
	@DisplayName("Find historical change level")
	@WithMockUser(username = "admin", authorities = {})
	void findHistoricalChangeLevel() throws Exception {
		String requesterId = "abcxyz";
		Integer page = 0;
		Integer size = 2;
		RequestEvaluationHistoricalDto dto1 = RequestEvaluationHistoricalDto.builder().skillName("Skill a").note("")
				.oldExp(1).oldLevel(1).skillCluster("Skill cluster A").currentExp(4).currentLevel(4)
				.date("2023-09-13 11:19:39.0").build();
		RequestEvaluationHistoricalDto dto2 = RequestEvaluationHistoricalDto.builder().skillName("Skill b").note("")
				.oldExp(0).oldLevel(0).skillCluster("Skill cluster A").currentExp(1).currentLevel(1)
				.date("2023-09-12 19:11:41.0").build();
		Map<String, String> query = new HashMap<>();
		query.put("page", "0");
		query.put("size", "2");
		Map<String, Object> result = new HashMap<>();
		result.put(Constants.HISTORICAL_CHANGE_LEVEL, new ArrayList<>(Arrays.asList(dto1, dto2)));
		result.put(Constants.TOTAL_PAGE, 1);
		result.put(Constants.TOTAL_ITEM, 2);

		when(requestEvaluationService.findHistorical(requesterId, page, size, query)).thenReturn(result);
		mockMvc.perform(get(Routes.URI_REST_HISTORICAL_LEVEL, requesterId).param("page", "0").param("size", "2"))
				.andExpect(status().isOk())
				.andExpect(content().string("{" + "\"data\":{" + "\"totalItem\":2," + "\"totalPage\":1,"
						+ "\"histories\":[" + "{" + "\"date\":\"2023-09-13 11:19:39.0\"," + "\"note\":\"\","
						+ "\"skill_name\":\"Skill a\"," + "\"skill_cluster\":\"Skill cluster A\"," + "\"new_level\":4,"
						+ "\"old_level\":1," + "\"old_exp\":1," + "\"new_exp\":4" + "}," + "{"
						+ "\"date\":\"2023-09-12 19:11:41.0\"," + "\"note\":\"\"," + "\"skill_name\":\"Skill b\","
						+ "\"skill_cluster\":\"Skill cluster A\"," + "\"new_level\":1," + "\"old_level\":0,"
						+ "\"old_exp\":0," + "\"new_exp\":1" + "}" + "]" + "}," + "\"code\":\"SUCCESS\"" + "}"));
	}
	
}