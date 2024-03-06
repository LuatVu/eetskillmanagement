package com.bosch.eet.skill.management.rest;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityExistsException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.FileStorageDTO;
import com.bosch.eet.skill.management.dto.PhaseDto;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.ProjectMemberDto;
import com.bosch.eet.skill.management.dto.SimplePersonalProjectDto;
import com.bosch.eet.skill.management.dto.SkillTagDto;
import com.bosch.eet.skill.management.dto.VModelDto;
import com.bosch.eet.skill.management.elasticsearch.document.ResultQuery;
import com.bosch.eet.skill.management.elasticsearch.repo.PersonalElasticRepository;
import com.bosch.eet.skill.management.exception.BadRequestException;
import com.bosch.eet.skill.management.exception.EETResponseException;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.ResourceNotFoundException;
import com.bosch.eet.skill.management.facade.ProjectFacade;
import com.bosch.eet.skill.management.repo.ProjectRepository;
import com.bosch.eet.skill.management.service.CommonService;
import com.bosch.eet.skill.management.service.PersonalService;
import com.bosch.eet.skill.management.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("dev")
class ProjectRestTest {

    @MockBean
    private ProjectService projectService;
    @MockBean
    private ProjectFacade projectFacade;
    @MockBean
    private ProjectRepository projectRepository;
    @MockBean
    private CommonService commonService;

    private final int PAGE = 0;
    private final int SIZE = 5;
    private final Pageable pageable = PageRequest.of(PAGE, SIZE);

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private MessageSource messageSource;
	@MockBean
	private PersonalElasticRepository personalElasticRepository;
	
	@MockBean
	private PersonalService personalService;
	
	@Autowired
	private ObjectMapper objectMapper;

    @Test
    @DisplayName("findAll happy case")
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER," +
            "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void findAllProject() throws Exception {
        List<ProjectDto> projectDtos = new ArrayList<>();
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("test");
        projectDtos.add(projectDto);
        Page<ProjectDto> pageProjectDtos = new PageImpl<>(projectDtos);
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("page", String.valueOf(PAGE));
        filterParams.put("size", String.valueOf(SIZE));
        
        when(projectService.findAll(pageable, filterParams)).thenReturn(pageProjectDtos);

        mockMvc.perform(get(Routes.URI_REST_PROJECT)
                        .param("page", String.valueOf(PAGE))
                        .param("size", String.valueOf(SIZE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("test")));
    }

    @Test
    @DisplayName("findAll empty case")
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER," +
            "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void findAllProject_whenEmpty() throws Exception {
        List<ProjectDto> projectDtos = new ArrayList<>();
        Page<ProjectDto> pageProjectDtos = new PageImpl<>(projectDtos);
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("page", String.valueOf(PAGE));
        filterParams.put("size", String.valueOf(SIZE));
        when(projectService.findAll(pageable, filterParams)).thenReturn(pageProjectDtos);

        mockMvc.perform(get(Routes.URI_REST_PROJECT)
                        .param("page", String.valueOf(PAGE))
                        .param("size", String.valueOf(SIZE)))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    @DisplayName("find Project by ID")
    @WithMockUser(username = "admin", authorities = {Constants.VIEW_ALL_PROJECTS})
    void findAllProjectById() throws Exception {
        ProjectDto projectDto = ProjectDto.builder()
                .name("project name")
                .teamSize("15")
                .id("id")
                .build();
        when(projectService.findById("id")).thenReturn(
                projectDto
        );
        mockMvc.perform(get(Routes.URI_REST_PROJECT_ID, "id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name", is("project name")))
                .andExpect(jsonPath("$.data.team_size", is("15")))
                .andExpect(jsonPath("$.data.project_id", is("id")));
    }
    
	@Test
	@DisplayName("find Project by ID - Access denied")
	@WithMockUser(username = "admin", authorities = {})
	void findAllProjectById_AccessDenied() throws Exception {
		ProjectDto projectDto = ProjectDto.builder().name("project name").teamSize("15").id("id").build();
		when(projectService.findById("id")).thenReturn(projectDto);
		mockMvc.perform(get(Routes.URI_REST_PROJECT_ID, "id")).andExpect(status().isForbidden()).andExpect(content()
				.string("{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
	}

    @Test
    @DisplayName("find Project by ID empty case")
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER," +
            "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void findAllProjectById_whenEmpty() throws Exception {
        when(projectService.findById("id"))
                .thenThrow(new EETResponseException(String.valueOf(NOT_FOUND.value()), "Unable to find resource", null));
        mockMvc.perform(get(Routes.URI_REST_PROJECT_ID, "id"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Delete project happy case")
    @WithMockUser(username = "admin", authorities = {Constants.DELETE_PROJECT})
    void deleteProject() throws Exception {
        ProjectDto projectDto = ProjectDto.builder()
                .id("1")
                .name("Test project")
                .projectType("Bosch")
                .status("New")
                .createdBy("Test")
                .description("Description")
                .pmName("Leader")
                .challenge("Challenge")
                .startDate("2022-12-25")
                .endDate("2022-12-26")
                .build();

        when(projectService.findById(projectDto.getId())).thenReturn(projectDto);
        when(projectFacade.deleteProject(projectDto)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_DELETE_PROJECT , "1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Delete empty project")
    @WithMockUser(username = "admin", authorities = "{CREATE_ROLE, DELETE_ROLE, DELETE_USER, UPDATE_ROLE, UPDATE_USER," +
            "VIEW_ALL_ROLE, VIEW_ALL_USER, VIEW_PERMISSION, VIEW_ROLE, VIEW_USER}")
    void deleteEmptyProject() throws Exception {
        when(projectService.findById("id"))
                .thenThrow(new EETResponseException(String.valueOf(NOT_FOUND.value()), "Project not found", null));
        mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_DELETE_PROJECT , "id"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
    
    @Test
	@DisplayName("find all filter")
	@WithMockUser(username = "admin", authorities = {Constants.VIEW_ALL_PROJECTS})
	void findAllFilter() throws Exception {
    	
    	HashMap<String, Object> filters = new HashMap();
    	List<String> gbUnitList = Arrays.asList("gb 1", "gb 2");
    	List<String> deptList = Arrays.asList("dept 1", "dept 2");
    	List<String> projectTypeList = Arrays.asList("pt 1", "pt 2");
		
    	filters.put("customer_gb_filter", gbUnitList);
    	filters.put("team_filter", deptList);
    	filters.put("project_type_filter", projectTypeList);	
		
		when(projectService.getFilter()).thenReturn(filters);
				
		mockMvc.perform(get(Routes.URI_REST_PROJECT_FILTER).param("page", String.valueOf(PAGE)).param("size",
				String.valueOf(SIZE))).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.project_type_filter", is(projectTypeList)))
				.andExpect(jsonPath("$.data.customer_gb_filter", is(gbUnitList)))
				.andExpect(jsonPath("$.data.team_filter", is(deptList))).andExpect(jsonPath("$.code", is("SUCCESS")));

	}
    
	@Test
	@DisplayName("find all filter - Access denied")
	@WithMockUser(username = "admin", authorities = {})
	void findAllFilter_AccessDenied() throws Exception {
		mockMvc.perform(get(Routes.URI_REST_PROJECT_FILTER).param("page", String.valueOf(PAGE)).param("size",
				String.valueOf(SIZE))).andExpect(status().isForbidden())
				.andExpect(content().string(
						"{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));

	}
    
    @Test
    @DisplayName("Import project from excel file happy case")
	@WithMockUser(username = "admin", authorities = {Constants.ADD_BOSCH_PROJECT})
    public void importProjectFromExcel_HappyCase() throws Exception {
    	String ntid = "1";
    	byte[] file = new byte[0];
    	String xlsxFile = Base64.getEncoder().encodeToString(file);
    	MockMultipartFile mockMultipartFile = new MockMultipartFile("file", xlsxFile.getBytes());
    	when(projectFacade.importProject(xlsxFile, mockMultipartFile))
    	.thenReturn(new ArrayList<>());
    	mockMvc.perform(MockMvcRequestBuilders.multipart(Routes.URI_REST_IMPORT_PROJECT, ntid)
		.file(mockMultipartFile))
		.andExpect(jsonPath("$.data.existed_project_list", is(Collections.emptyList())))
		.andExpect(jsonPath("$.code", is("SUCCESS")));
    			
    }
    
	@Test
	@DisplayName("Import project from excel file - Access denied")
	@WithMockUser(username = "admin", authorities = {})
	void importProjectFromExcel_AccessDenied() throws Exception {
		String ntid = "1";
		byte[] file = new byte[0];
		String xlsxFile = Base64.getEncoder().encodeToString(file);
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", xlsxFile.getBytes());
		mockMvc.perform(MockMvcRequestBuilders.multipart(Routes.URI_REST_IMPORT_PROJECT, ntid).file(mockMultipartFile))
				.andExpect(status().isForbidden()).andExpect(content().string(
						"{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
	}
    
    @Test
	@DisplayName("Update additional task - personal")
	@WithMockUser(username = "admin", authorities = "EDIT_ASSOCIATE_INFO_PERMISSION")
	void updateAdditionalTask() throws Exception {
		String ntid = "admin";
		String[] permission = { Constants.EDIT_ASSOCIATE_INFO_PERMISSION };
		Set<String> authSet = new HashSet<>(Arrays.asList(permission));

		SimplePersonalProjectDto simplePersonalProjectDto = SimplePersonalProjectDto.builder().id("idpersonalproject")
				.projectId("projectid").roleId("roleprojectid").additionalTasks("Testupdateadfsadffafsditionaltaskforreal").build();
		when(projectService.updateAdditionalTask(ntid, simplePersonalProjectDto, authSet)).thenReturn("Success");

		mockMvc.perform(post(Routes.URI_REST_EDIT_ADDITIONAL_TASK).content("{"
				+"\"id\":\"idpersonalproject\","
				+"\"additional_tasks\":\"Testupdateadfsadffafsditionaltaskforreal\","
				+"\"role_id\":\"roleprojectid\","
				+"\"project_id\":\"projectid\""
				+"}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", is("Success")))
				.andExpect(jsonPath("$.code", is("SUCCESS")));
	}
    
	@Test
	@DisplayName("Update additional task - can not found personal project")
	@WithMockUser(username = "admin", authorities = "EDIT_ASSOCIATE_INFO_PERMISSION")
	void updateAdditionalTask_canNotFoundPersonalProject() throws Exception {
		String ntid = "admin";
		String[] permission = { Constants.EDIT_ASSOCIATE_INFO_PERMISSION };
		Set<String> authSet = new HashSet<>(Arrays.asList(permission));

		SimplePersonalProjectDto simplePersonalProjectDto = SimplePersonalProjectDto.builder().id("idpersonalproject")
				.projectId("projectid").roleId("roleprojectid")
				.additionalTasks("Testupdateadfsadffafsditionaltaskforreal").build();
		when(projectService.updateAdditionalTask(ntid, simplePersonalProjectDto, authSet))
				.thenThrow(new ResourceNotFoundException(messageSource.getMessage(
						MessageCode.PERSONAL_PROJECT_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale())));

		mockMvc.perform(post(Routes.URI_REST_EDIT_ADDITIONAL_TASK)
				.content("{" + "\"id\":\"idpersonalproject\","
						+ "\"additional_tasks\":\"Testupdateadfsadffafsditionaltaskforreal\","
						+ "\"role_id\":\"roleprojectid\"," + "\"project_id\":\"projectid\"" + "}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8))
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.message", is("Personal project not found")))
				.andExpect(jsonPath("$.code", is("404 NOT_FOUND")));
	}
	
	@Test
	@DisplayName("Update additional task - access dinied")
	@WithMockUser(username = "admin" , authorities = "ROLE_USER")
	void updateAdditionalTask_AccessDenied() throws Exception {
		String ntid = "admin";
		String[] permission = { "ROLE_USER" };
		Set<String> authSet = new HashSet<>(Arrays.asList(permission));

		SimplePersonalProjectDto simplePersonalProjectDto = SimplePersonalProjectDto.builder().id("idpersonalproject")
				.projectId("projectid").roleId("roleprojectid")
				.additionalTasks("Testupdateadfsadffafsditionaltaskforreal").build();
		when(projectService.updateAdditionalTask(ntid, simplePersonalProjectDto, authSet))
				.thenThrow(new AccessDeniedException(messageSource.getMessage(MessageCode.ACCESS_DENIED.toString(),
						null, LocaleContextHolder.getLocale())));

		mockMvc.perform(post(Routes.URI_REST_EDIT_ADDITIONAL_TASK)
				.content("{" + "\"id\":\"idpersonalproject\","
						+ "\"additional_tasks\":\"Testupdateadfsadffafsditionaltaskforreal\","
						+ "\"role_id\":\"roleprojectid\"," + "\"project_id\":\"projectid\"" + "}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8))
				.andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code", is("NOT_AUTHORIZATION")))
				.andExpect(jsonPath("$.message", is("Access is denied")));
	}
	
	@Test
	@DisplayName("Update additional task - assign duplicate project")
	@WithMockUser(username = "admin" ,  authorities = "EDIT_ASSOCIATE_INFO_PERMISSION")
	void updateAdditionalTask_AssignDuplicateProject() throws Exception {
		String ntid = "admin";
		String[] permission = { Constants.EDIT_ASSOCIATE_INFO_PERMISSION };
		Set<String> authSet = new HashSet<>(Arrays.asList(permission));

		SimplePersonalProjectDto simplePersonalProjectDto = SimplePersonalProjectDto.builder().id("idpersonalproject")
				.projectId("projectid").roleId("roleprojectid")
				.additionalTasks("Testupdateadfsadffafsditionaltaskforreal").build();
		when(projectService.updateAdditionalTask(ntid, simplePersonalProjectDto, authSet))
				.thenThrow(new EntityExistsException(messageSource.getMessage(MessageCode.PROJECT_IS_DUPLICATE.toString(),
						null, LocaleContextHolder.getLocale())));

		mockMvc.perform(post(Routes.URI_REST_EDIT_ADDITIONAL_TASK)
				.content("{" + "\"id\":\"idpersonalproject\","
						+ "\"additional_tasks\":\"Testupdateadfsadffafsditionaltaskforreal\","
						+ "\"role_id\":\"roleprojectid\"," + "\"project_id\":\"projectid\"" + "}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8))
				.andExpect(status().isConflict()).andExpect(jsonPath("$.code", is("409 CONFLICT")))
				.andExpect(jsonPath("$.message", is("Project is existed")));
	}
	
	@Test
	@DisplayName("Update additional task - wrong project")
	@WithMockUser(username = "admin", authorities = "EDIT_ASSOCIATE_INFO_PERMISSION")
	void updateAdditionalTask_WrongProject() throws Exception {
		String ntid = "admin";
		
		String[] permission = { Constants.EDIT_ASSOCIATE_INFO_PERMISSION };
		Set<String> authSet = new HashSet<>(Arrays.asList(permission));
		
		SimplePersonalProjectDto simplePersonalProjectDto = SimplePersonalProjectDto.builder().id("idpersonalproject")
				.projectId("projectid").roleId("roleprojectid")
				.additionalTasks("Testupdateadfsadffafsditionaltaskforreal").build();
		when(projectService.updateAdditionalTask(ntid, simplePersonalProjectDto, authSet))
				.thenThrow(new ResourceNotFoundException(messageSource.getMessage(MessageCode.PROJECT_NOT_FOUND.toString(),
						null, LocaleContextHolder.getLocale())));
		
		mockMvc.perform(post(Routes.URI_REST_EDIT_ADDITIONAL_TASK).content("{" + "\"id\":\"idpersonalproject\","
				+ "\"additional_tasks\":\"Testupdateadfsadffafsditionaltaskforreal\","
				+ "\"role_id\":\"roleprojectid\"," + "\"project_id\":\"projectid\"" + "}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message", is("Project not found")))
				.andExpect(jsonPath("$.code", is("404 NOT_FOUND")));
	}
	
	@Test
	@DisplayName("Update additional task - wrong project role")
	@WithMockUser(username = "admin", authorities = "EDIT_ASSOCIATE_INFO_PERMISSION")
	void updateAdditionalTask_WrongProjeRole() throws Exception {
		String ntid = "admin";
		
		String[] permission = { Constants.EDIT_ASSOCIATE_INFO_PERMISSION };
		Set<String> authSet = new HashSet<>(Arrays.asList(permission));

		SimplePersonalProjectDto simplePersonalProjectDto = SimplePersonalProjectDto.builder().id("idpersonalproject")
				.projectId("projectid").roleId("roleprojectid")
				.additionalTasks("Testupdateadfsadffafsditionaltaskforreal").build();
		
		when(projectService.updateAdditionalTask(ntid, simplePersonalProjectDto, authSet))
		.thenThrow(new ResourceNotFoundException(messageSource.getMessage(MessageCode.PROJECT_ROLE_NOT_FOUND.toString(),
				null, LocaleContextHolder.getLocale())));
		
		mockMvc.perform(post(Routes.URI_REST_EDIT_ADDITIONAL_TASK).content("{" + "\"id\":\"idpersonalproject\","
				+ "\"additional_tasks\":\"Testupdateadfsadffafsditionaltaskforreal\","
				+ "\"role_id\":\"roleprojectid\"," + "\"project_id\":\"projectid\"" + "}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message", is("Project role not found")))
				.andExpect(jsonPath("$.code", is("404 NOT_FOUND")));
	}
	
	@Test
	@DisplayName("Update additional task - can not assign another Non Bosch project")
	@WithMockUser(username = "admin", authorities = "EDIT_ASSOCIATE_INFO_PERMISSION")
	void updateAdditionalTask_canNotAssignAnotherNonBoschProject() throws Exception {
		String ntid = "admin";
		
		String[] permission = { Constants.EDIT_ASSOCIATE_INFO_PERMISSION };
		Set<String> authSet = new HashSet<>(Arrays.asList(permission));

		SimplePersonalProjectDto simplePersonalProjectDto = SimplePersonalProjectDto.builder().id("idpersonalproject")
				.projectId("projectid").roleId("roleprojectid")
				.additionalTasks("Testupdateadfsadffafsditionaltaskforreal").build();
		
		when(projectService.updateAdditionalTask(ntid, simplePersonalProjectDto, authSet))
		.thenThrow(new BadRequestException(messageSource.getMessage(MessageCode.CANNOT_ASSIGN_ANOTHER_NON_BOSCH.toString(),
				null, LocaleContextHolder.getLocale())));
		
		mockMvc.perform(post(Routes.URI_REST_EDIT_ADDITIONAL_TASK).content("{" + "\"id\":\"idpersonalproject\","
				+ "\"additional_tasks\":\"Testupdateadfsadffafsditionaltaskforreal\","
				+ "\"role_id\":\"roleprojectid\"," + "\"project_id\":\"projectid\"" + "}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Non-Bosch associates cannot be assigned to another project")))
				.andExpect(jsonPath("$.code", is("400 BAD_REQUEST")));
	}
	
	@DisplayName("check permission when delete bosch project")
	@WithMockUser(username = "admin", authorities = "{REMOVE_PROJECT}")
	void checkPermissionDeleteBoschProject() throws Exception {
		String projectId = "idtest";
		mockMvc.perform(post(Routes.URI_REST_DELETE_PROJECT, projectId))
				.andExpect(status().is4xxClientError())
				.andExpect(content().string(
						"{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
	}

    @Test
    @DisplayName("Get project list for dropdown")
	@WithMockUser(username = "admin", authorities = {Constants.VIEW_ALL_PROJECTS})
    public void testGetAllProjectForDropdown() throws Exception {
    	mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PROJECT_DROP_DOWN)
    			.param("project_type", "Bosch"))
		        .andExpect(status().isOk());
    }
    
	@Test
	@DisplayName("Get project list for dropdown - Access denied")
	@WithMockUser(username = "admin", authorities = {})
	void testGetAllProjectForDropdown_AccessDenied() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PROJECT_DROP_DOWN).param("project_type", "Bosch"))
				.andExpect(status().is4xxClientError()).andExpect(content().string(
						"{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
	}
    
    @DisplayName("Get list project from elastic search - Happy case")
	@Test
	@WithMockUser(username = "admin", authorities = "VIEW_ALL_PROJECTS")
	void queryProjectFromElasticSearch_HappyCase() throws Exception {
		String indexName = "project";
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
	
	@DisplayName("Get list project from elasticsearch - Access Denied")
	@Test
	@WithMockUser(username = "admin", authorities = "ROLE_USER")
	void queryProjectFromElasticSearch_AcessDenied() throws Exception {
		String indexName = "project";
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_ELASTIC_QUERY, indexName)
				.content("{" + "\"query\":\"\"," + "\"size\":100," + "\"from\":0" + "}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(content().string("{"
						+"\"error\":\"access_denied\","
						+"\"error_description\":\"Access is denied\""
						+"}"));
	}
	
	@DisplayName("Get list customer gb from elastic search - Happy case")
	@Test
	@WithMockUser(username = "admin", authorities = "VIEW_ALL_PROJECTS")
	void queryCustomerGBFromElasticSearch_HappyCase() throws Exception {
		String indexName = "customer_gb";
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
	
	@DisplayName("Get list customer gb from elasticsearch - Access Denied")
	@Test
	@WithMockUser(username = "admin", authorities = {})
	void queryCustomerGBFromElasticSearch_AcessDenied() throws Exception {
		String indexName = "customer_gb";
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_ELASTIC_QUERY, indexName)
				.content("{" + "\"query\":\"\"," + "\"size\":100," + "\"from\":0" + "}")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(content().string("{"
						+"\"error\":\"access_denied\","
						+"\"error_description\":\"Access is denied\""
						+"}"));
	}
	
	@DisplayName("Get v-model - Happycase")
	@Test
	@WithMockUser(username = "admin", authorities = { Constants.VIEW_ALL_PROJECTS })
	void getVmodel_HappyCase() throws Exception {
		ProjectDto projectDto = ProjectDto.builder().id("idtest").name("Migration").gbUnit("PS").customerGb("PS-EC")
				.isTopProject(false).build();
		PhaseDto phaseDto = PhaseDto.builder().id("idtest").name("systemIntegration")
				.projects(Collections.singletonList(projectDto)).build();
		List<PhaseDto> phaseDtos = new ArrayList<>();
		phaseDtos.add(phaseDto);
		VModelDto vmodelDto = VModelDto.builder().phases(phaseDtos).build();
		when(commonService.getVModel()).thenReturn(vmodelDto);

		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PHASE).contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string("{" + "\"data\":{" + "\"phases\":[" + "{" + "\"id\":\"idtest\","
						+ "\"name\":\"systemIntegration\"," + "\"projects\":[" + "{" + "\"name\":\"Migration\","
						+ "\"project_id\":\"idtest\"," + "\"gb_unit\":\"PS\"," + "\"customer_gb\":\"PS-EC\","
						+ "\"top_project\":false" + "}" + "]" + "}" + "]" + "}," + "\"code\":\"SUCCESS\"" + "}"));
	}
	
	@DisplayName("Get v-model - Access denied")
	@Test
	@WithMockUser(username = "admin", authorities = { })
	void getVmodel_Accessdenied() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PHASE).contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(content().string("{"
						+"\"error\":\"access_denied\","
						+"\"error_description\":\"Access is denied\""
						+"}"));
	}
	
	@DisplayName("Add bosch project")
	@Test
	@WithMockUser(username = "admin", authorities = { Constants.ADD_BOSCH_PROJECT })
	void addProject() throws Exception {
		ProjectMemberDto memberDto = ProjectMemberDto.builder().id("idpersonal").name("Nguyen Duy Long").role("Dev")
				.roleId("1").additionalTask("Coding").startDate("2023-09-25").build();
		PhaseDto phaseDto = PhaseDto.builder().id("idphase").name("Phase test").description("No des")
				.projects(Collections.emptyList()).build();
		SkillTagDto skillTagDto = SkillTagDto.builder().id("idskilltag").name("Name skill tag").order(1l).build();
		ProjectDto projectDto = ProjectDto.builder().name("Project own 6").startDate("2023-09-25")
				.pmName("FIXED-TERM Nguyen Duy Long (MS/EET11)").challenge("Challenge is too hard").status("status")
				.projectType("Bosch").projectTypeId("b8987dfa-f6e2-99d3-f511-3df3286bd7c0").objective("Nocap")
				.gbUnit("MS").teamSize("10").referenceLink("none").isTopProject(true).description("Nodes")
				.members(Collections.singletonList(memberDto)).phaseDtoSet(Collections.singleton(phaseDto))
				.skillTags(Collections.singleton(skillTagDto)).customerGb("Customer gb").stakeholder("Stakeholder")
				.createdBy("admin").build();
		when(projectFacade.save(projectDto)).thenReturn(projectDto);
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT).contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(projectDto))).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.data.name", is("Project own 6")))
				.andExpect(jsonPath("$.data.challenge", is("Challenge is too hard")))
				.andExpect(jsonPath("$.data.stakeholder", is("Stakeholder")))
				.andExpect(jsonPath("$.data.start_date", is("2023-09-25")))
				.andExpect(jsonPath("$.data.pm_name", is("FIXED-TERM Nguyen Duy Long (MS/EET11)")))
				.andExpect(jsonPath("$.code", is("SUCCESS")));
	}
	
	@DisplayName("Add Bosch project - Access denied")
	@Test
	@WithMockUser(username = "admin", authorities = {})
	void addProject_Accessdenied() throws Exception {
		ProjectMemberDto memberDto = ProjectMemberDto.builder().id("idpersonal").name("Nguyen Duy Long").role("Dev")
				.roleId("1").additionalTask("Coding").startDate("2023-09-25").build();
		PhaseDto phaseDto = PhaseDto.builder().id("idphase").name("Phase test").description("No des")
				.projects(Collections.emptyList()).build();
		SkillTagDto skillTagDto = SkillTagDto.builder().id("idskilltag").name("Name skill tag").order(1l).build();
		ProjectDto projectDto = ProjectDto.builder().name("Project own 6").startDate("2023-09-25")
				.pmName("FIXED-TERM Nguyen Duy Long (MS/EET11)").challenge("Challenge is too hard").status("status")
				.projectType("Bosch").projectTypeId("b8987dfa-f6e2-99d3-f511-3df3286bd7c0").objective("Nocap")
				.gbUnit("MS").teamSize("10").referenceLink("none").isTopProject(true).description("Nodes")
				.members(Collections.singletonList(memberDto)).phaseDtoSet(Collections.singleton(phaseDto))
				.skillTags(Collections.singleton(skillTagDto)).customerGb("Customer gb").stakeholder("Stakeholder")
				.createdBy("admin").build();

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT).contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(projectDto)))
				.andExpect(MockMvcResultMatchers.status().isForbidden()).andExpect(content().string(
						"{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
	}
	
	@DisplayName("Update project")
	@Test
	@WithMockUser(username = "admin", authorities = { Constants.EDIT_PROJECT })
	void updateProject() throws Exception {
		ProjectMemberDto memberDto = ProjectMemberDto.builder().id("idpersonal").name("Nguyen Duy Long").role("Dev")
				.roleId("1").additionalTask("Coding").startDate("2023-09-25").build();
		PhaseDto phaseDto = PhaseDto.builder().id("idphase").name("Phase test").description("No des")
				.projects(Collections.emptyList()).build();
		SkillTagDto skillTagDto = SkillTagDto.builder().id("idskilltag").name("Name skill tag").order(1l).build();
		ProjectDto projectDto = ProjectDto.builder().id("idproject").name("Project own 6").startDate("2023-09-25")
				.pmName("FIXED-TERM Nguyen Duy Long (MS/EET11)").challenge("Challenge is too hard").status("status")
				.projectType("Bosch").projectTypeId("b8987dfa-f6e2-99d3-f511-3df3286bd7c0").objective("Nocap")
				.gbUnit("MS").teamSize("10").referenceLink("none").isTopProject(true).description("Nodes")
				.members(Collections.singletonList(memberDto)).phaseDtoSet(Collections.singleton(phaseDto))
				.skillTags(Collections.singleton(skillTagDto)).customerGb("Customer gb").stakeholder("Stakeholder")
				.createdBy("admin").build();

		when(projectFacade.save(projectDto)).thenReturn(projectDto);

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_UPDATE_PROJECT, "idproject")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(projectDto)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.data.name", is("Project own 6")))
				.andExpect(jsonPath("$.data.challenge", is("Challenge is too hard")))
				.andExpect(jsonPath("$.data.stakeholder", is("Stakeholder")))
				.andExpect(jsonPath("$.data.start_date", is("2023-09-25")))
				.andExpect(jsonPath("$.data.pm_name", is("FIXED-TERM Nguyen Duy Long (MS/EET11)")))
				.andExpect(jsonPath("$.code", is("SUCCESS")));
	}
	
	@DisplayName("Update project - Access denied")
	@Test
	@WithMockUser(username = "admin", authorities = {})
	void updateProject_Accessdenied() throws Exception {
		ProjectMemberDto memberDto = ProjectMemberDto.builder().id("idpersonal").name("Nguyen Duy Long").role("Dev")
				.roleId("1").additionalTask("Coding").startDate("2023-09-25").build();
		PhaseDto phaseDto = PhaseDto.builder().id("idphase").name("Phase test").description("No des")
				.projects(Collections.emptyList()).build();
		SkillTagDto skillTagDto = SkillTagDto.builder().id("idskilltag").name("Name skill tag").order(1l).build();
		ProjectDto projectDto = ProjectDto.builder().id("idproject").name("Project own 6").startDate("2023-09-25")
				.pmName("FIXED-TERM Nguyen Duy Long (MS/EET11)").challenge("Challenge is too hard").status("status")
				.projectType("Bosch").projectTypeId("b8987dfa-f6e2-99d3-f511-3df3286bd7c0").objective("Nocap")
				.gbUnit("MS").teamSize("10").referenceLink("none").isTopProject(true).description("Nodes")
				.members(Collections.singletonList(memberDto)).phaseDtoSet(Collections.singleton(phaseDto))
				.skillTags(Collections.singleton(skillTagDto)).customerGb("Customer gb").stakeholder("Stakeholder")
				.createdBy("admin").build();

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_UPDATE_PROJECT, "idproject")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(projectDto)))
				.andExpect(MockMvcResultMatchers.status().isForbidden()).andExpect(content().string(
						"{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
	}
	
	@DisplayName("Get project portfolio")
	@Test
	@WithMockUser(username = "admin", authorities = {} )
	void geProjectPortfolio() throws Exception {
		String layoutName = "project_benefit";
		String id = "projectid";
		String result = "test";
		when(projectService.getInforPortfolio(id, layoutName)).thenReturn(result);
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PROJECT_PORTFOLIO_DETAIL, id, layoutName)
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string("{\"data\":\"test\",\"code\":\"SUCCESS\"}"));
	}
	
	@DisplayName("Get project portfolio - Layout not exist")
	@Test
	@WithMockUser(username = "admin", authorities = {})
	void geProjectPortfolio_LayoutNotExist() throws Exception {
		String layoutName = "project_benefitds";
		String id = "projectid";
		when(projectService.getInforPortfolio(id, layoutName)).thenThrow(new BadRequestException(messageSource
				.getMessage(MessageCode.INVALID_LAYOUT.toString(), null, LocaleContextHolder.getLocale())));
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PROJECT_PORTFOLIO_DETAIL, id, layoutName)
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("code", is("400 BAD_REQUEST"))).andExpect(
						jsonPath("message", is(messageSource.getMessage(MessageCode.INVALID_LAYOUT.toString(),
								null, LocaleContextHolder.getLocale()))));
	}
	
	@DisplayName("Update information portfolio")
	@Test
	@WithMockUser(username = "admin", authorities = { Constants.EDIT_PROJECT })
	void updateInfoPortfolio() throws Exception {
		String layoutName = "project_benefit";
		String id = "projectid";
		byte[] file = new byte[255];
		String xlsxFile = Base64.getEncoder().encodeToString(file);
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", xlsxFile.getBytes());
		FileStorageDTO fileStorageDTO = FileStorageDTO.builder().id("dsds").deleted(false).extension("txt").size(1l)
				.name("dddd.txt").build();
		when(projectService.updateInfoPortfolio(id, mockMultipartFile, layoutName)).thenReturn(fileStorageDTO);
		mockMvc.perform(MockMvcRequestBuilders.multipart(Routes.URI_REST_PROJECT_PORTFOLIO_DETAIL, id, layoutName)
				.file(mockMultipartFile).contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string(""
						+ "{" + "\"data\":{" + "\"id\":\"dsds\"," + "\"name\":\"dddd.txt\"," + "\"extension\":\"txt\","
						+ "\"size\":1," + "\"deleted\":false" + "}," + "\"code\":\"SUCCESS\"" + "}"));
	}
	
	@DisplayName("Update information portfolio - Access denied")
	@Test
	@WithMockUser(username = "admin", authorities = {})
	void updateInfoPortfolio_AccessDenied() throws Exception {
		String layoutName = "project_benefit";
		String id = "projectid";
		byte[] file = new byte[255];
		String xlsxFile = Base64.getEncoder().encodeToString(file);
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", xlsxFile.getBytes());
		mockMvc.perform(MockMvcRequestBuilders.multipart(Routes.URI_REST_PROJECT_PORTFOLIO_DETAIL, id, layoutName)
				.file(mockMultipartFile).contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isForbidden()).andExpect(content().string(
						"{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
	}
	
	@DisplayName("Update information portfolio - Layout not exist")
	@Test
	@WithMockUser(username = "admin", authorities = { Constants.EDIT_PROJECT })
	void updateInfoPortfolio_LayoutNotExist() throws Exception {
		String layoutName = "project_benefiddt";
		String id = "projectid";
		byte[] file = new byte[255];
		String xlsxFile = Base64.getEncoder().encodeToString(file);
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", xlsxFile.getBytes());
		when(projectService.updateInfoPortfolio(id, mockMultipartFile, layoutName))
				.thenThrow(new BadRequestException(messageSource.getMessage(MessageCode.INVALID_LAYOUT.toString(), null,
						LocaleContextHolder.getLocale())));
		mockMvc.perform(MockMvcRequestBuilders.multipart(Routes.URI_REST_PROJECT_PORTFOLIO_DETAIL, id, layoutName)
				.file(mockMultipartFile).contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("code", is("400 BAD_REQUEST"))).andExpect(jsonPath("message", is(messageSource
						.getMessage(MessageCode.INVALID_LAYOUT.toString(), null, LocaleContextHolder.getLocale()))));
	}
	
	@DisplayName("Update information portfolio - Project type is non bosch")
	@Test
	@WithMockUser(username = "admin", authorities = { Constants.EDIT_PROJECT })
	void updateInfoPortfolio_ProjectTypeIsNonBosch() throws Exception {
		String layoutName = "project_benefiddt";
		String id = "projectid";
		byte[] file = new byte[255];
		String xlsxFile = Base64.getEncoder().encodeToString(file);
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", xlsxFile.getBytes());
		when(projectService.updateInfoPortfolio(id, mockMultipartFile, layoutName))
				.thenThrow(new BadRequestException(
						messageSource.getMessage(MessageCode.CANNOT_ADD_PORTFOLIO_FOR_NON_BOSCH.toString(), null,
								LocaleContextHolder.getLocale())));
		mockMvc.perform(MockMvcRequestBuilders.multipart(Routes.URI_REST_PROJECT_PORTFOLIO_DETAIL, id, layoutName)
				.file(mockMultipartFile).contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("code", is("400 BAD_REQUEST")))
				.andExpect(jsonPath("message",
						is(messageSource.getMessage(MessageCode.CANNOT_ADD_PORTFOLIO_FOR_NON_BOSCH.toString(), null,
								LocaleContextHolder.getLocale()))));
	}
	

	@DisplayName("Get project portfolio - HappyCase")
	@Test
	void getProjectPortfolio_HappyCase() throws Exception {
		ProjectDto projectDto = ProjectDto.builder()
				.id("idTest").name("Migration").customerGb("PS-EC")
				.teamSize("2").highlight("testHighlight").build();
		Set<SkillTagDto> skillTagDtos = new HashSet<>(Collections.singletonList(SkillTagDto.builder().id("tagId").name("testTag").build()));
		projectDto.setSkillTags(skillTagDtos);

		when(projectService.getPortfolio("idTest")).thenReturn(projectDto);

		mockMvc.perform(MockMvcRequestBuilders.get(Routes.UIR_REST_PROJECT_PORTFOLIO, "idTest")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string("{" + "\"data\":{" + "\"name\":\"Migration\","
						+ "\"highlight\":\"testHighlight\"," + "\"project_id\":\"idTest\"," + "\"team_size\":\"2\","
						+ "\"customer_gb\":\"PS-EC\"," + "\"top_project\":false," + "\"skill_tags\":["
						+ "{" + "\"id\":\"tagId\"," + "\"name\":\"testTag\"," + "\"projectCount\":0" + "}" + "]" + "},"
						+ "\"code\":\"SUCCESS\"" + "}"));
	}


	@DisplayName("Update project portfolio - happy case")
	@Test
	@WithMockUser(username = "admin", authorities = { Constants.EDIT_PROJECT })
	void updateProjectPortfolio_happycase() throws Exception {
		ProjectDto projectDto = ProjectDto.builder()
				.id("idTest").name("Migration").customerGb("PS-EC")
				.teamSize("2").highlight("testHighlight").build();
		Set<SkillTagDto> skillTagDtos = new HashSet<>(Collections.singletonList(SkillTagDto.builder().id("tagId").name("testTag").projects(Collections.emptySet()).build()));
		projectDto.setSkillTags(skillTagDtos);

		when(projectFacade.editProjectPortfolio(projectDto)).thenReturn(projectDto);

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.UIR_REST_PROJECT_PORTFOLIO, "idTest")
						.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(projectDto)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string("{" + "\"data\":{" + "\"name\":\"Migration\","
						+ "\"highlight\":\"testHighlight\"," + "\"project_id\":\"idTest\"," + "\"team_size\":\"2\","
						+ "\"customer_gb\":\"PS-EC\"," + "\"top_project\":false," + "\"skill_tags\":[" + "{"
						+ "\"id\":\"tagId\"," + "\"name\":\"testTag\"," + "\"projects\":[]," + "\"projectCount\":0"
						+ "}" + "]" + "}," + "\"code\":\"SUCCESS\"" + "}"));
	}

	@DisplayName("Update project portfolio - Access denied")
	@Test
	@WithMockUser(username = "admin", authorities = {})
	void updateProjectPortfolio_AccessDenied() throws Exception {
		ProjectDto projectDto = ProjectDto.builder().id("idTest").name("Migration").customerGb("PS-EC")
				.teamSize("2").highlight("testHighlight").build();

		when(projectFacade.editProjectPortfolio(projectDto)).thenReturn(projectDto);

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.UIR_REST_PROJECT_PORTFOLIO, "idTest")
						.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(projectDto)))
				.andExpect(MockMvcResultMatchers.status().isForbidden()).andExpect(content().string(
						"{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
	}

}
