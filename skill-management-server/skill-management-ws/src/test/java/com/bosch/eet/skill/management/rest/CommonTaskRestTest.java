package com.bosch.eet.skill.management.rest;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.CommonTaskDto;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.PhaseDto;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.VModelDto;
import com.bosch.eet.skill.management.service.CommonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("dev")
public class CommonTaskRestTest {
    @MockBean
    private CommonService commonService;
    @Autowired
    private MockMvc mockMvc;

    private final int PAGE = 0;
    private final int SIZE = 5;
    private final Pageable pageable = PageRequest.of(PAGE, SIZE);

    @Test
    @DisplayName("find All CommonTask By Id")
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
	    + "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void findAllCommonTaskById() throws Exception {
	CommonTaskDto commontaskDto1 = CommonTaskDto.builder().id("1").name("task 1").project_role_id("1").build();

	CommonTaskDto commontaskDto2 = CommonTaskDto.builder().id("2").name("task 2").project_role_id("1").build();
	List<CommonTaskDto> commontaskDtos = new ArrayList<>();
	commontaskDtos.add(commontaskDto1);
	commontaskDtos.add(commontaskDto2);

	Page<CommonTaskDto> pagePersonalDtos = new PageImpl<>(commontaskDtos);
	when(commonService.findByProjectRoleId("1", pageable)).thenReturn(pagePersonalDtos);

	mockMvc.perform(get(Routes.URI_REST_PROJECT_ROLE + "/1" + "/tasks").param("page", String.valueOf(PAGE))
		.param("size", String.valueOf(SIZE)).contentType(MediaType.APPLICATION_JSON)
		.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andExpect(jsonPath("$[0].name", is("task 1")))
		.andExpect(jsonPath("$[1].name", is("task 2")));
    }

    @Test
    @DisplayName("find All CommonTask By Id return Empty value")
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
	    + "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void findAllCommonTaskByIdEmpty() throws Exception {
	List<CommonTaskDto> commontaskDtos = new ArrayList<>();
	Page<CommonTaskDto> pagePersonalDtos = new PageImpl<>(commontaskDtos);
	when(commonService.findByProjectRoleId("1", pageable)).thenReturn(pagePersonalDtos);

	mockMvc.perform(get(Routes.URI_REST_PROJECT_ROLE + "/1" + "/tasks").param("page", String.valueOf(PAGE))
		.param("size", String.valueOf(SIZE)).contentType(MediaType.APPLICATION_JSON)
		.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andExpect(content().string("[]"));
    }

    @Test
    @DisplayName("find All CommonTask By Id return Empty id value")
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
	    + "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void findAllCommonTaskByIdEmptyId() throws Exception {
	List<CommonTaskDto> commontaskDtos = new ArrayList<>();
	Page<CommonTaskDto> pagePersonalDtos = new PageImpl<>(commontaskDtos);
	when(commonService.findByProjectRoleId("", pageable)).thenReturn(pagePersonalDtos);

	mockMvc.perform(get(Routes.URI_REST_PROJECT_ROLE + "" + "/tasks").param("page", String.valueOf(PAGE))
		.param("size", String.valueOf(SIZE)).contentType(MediaType.APPLICATION_JSON)
		.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is4xxClientError());

    }
    
    @Test
	@DisplayName("Find line manager (happy case)")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	public void findLineManager_HappyCase() throws Exception {
		String idLineManager = "12";
		PersonalDto manager = PersonalDto.builder().id(idLineManager).name("Test name").build();
		when(commonService.findLineManager(idLineManager)).thenReturn(manager);
		 mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_LINE_MANAGER, idLineManager)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string("{\"data\":{\"id\":\"12\",\"name\":\"Test name\"}"
						+ ",\"code\":\"SUCCESS\"}"));
	}
	
	@Test
	@DisplayName("find skills highlight with wrong personal id or not exist")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	public void findSkillsHighlight_WrongPersonalID() throws Exception {
		String idLineManager = "1";
		when(commonService.findLineManager(idLineManager)).thenReturn(PersonalDto.builder().build());
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_LINE_MANAGER, idLineManager)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string("{\"data\":{}"
						+ ",\"code\":\"SUCCESS\"}"));
	}

	@Test
	@DisplayName("get V model")
	@WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER,"
			+ "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
	public void getVModel() throws Exception {
		PhaseDto phaseDto = PhaseDto.builder()
				.id("testPhaseDto")
				.name("Software Integration & System Integration Test")
				.projects(new ArrayList<ProjectDto>())
				.build();
		ArrayList<PhaseDto> phaseDtoArrayList = new ArrayList<>();
		phaseDtoArrayList.add(phaseDto);
		VModelDto vModelDto = VModelDto.builder()
				.phases(phaseDtoArrayList)
				.build();
		when(commonService.getVModel()).thenReturn(vModelDto);

		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PHASE)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(content().string("{\"code\":\"SUCCESS\"}"));
				.andExpect(content().string("{\"data\":{\"pharses\":[{\"id\":\"testPhaseDto\",\"name\":\"Software Integration & System Integration Test\",\"projects\":[]}]},\"code\":\"SUCCESS\"}"));
	}

}
