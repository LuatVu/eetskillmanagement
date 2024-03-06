package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.dto.FileStorageDTO;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.ProjectMemberDto;
import com.bosch.eet.skill.management.entity.Customer;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalProject;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectRole;
import com.bosch.eet.skill.management.entity.ProjectType;
import com.bosch.eet.skill.management.exception.BadRequestException;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.repo.CustomerRepository;
import com.bosch.eet.skill.management.repo.PersonalProjectRepository;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.PhaseProjectRepository;
import com.bosch.eet.skill.management.repo.ProjectRepository;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class ProjectServiceTestWithoutMock {

	private final String TEST_PROJECT_NAME = "Data jpa project without mock";

	private Project project;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private PersonalRepository personalRepository;

	@Autowired
	private PersonalProjectRepository personalProjectRepository;

	private Personal personal;

	@Autowired
	private MessageSource messageSource;

	@Value("${project.path}")
	private String projectStorageDir;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PhaseProjectRepository phaseProjectRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@BeforeEach
	void prepare() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date = format.parse("2023/02/02");
		ProjectType projectType = ProjectType.builder().id("b8987dfa-f6e2-99d3-f511-3df3286bd7c0").name("Bosch")
				.build();
		Customer customer = Customer.builder().name("test-customer").build();
		customer = customerRepository.saveAndFlush(customer);
		project = Project.builder().name(TEST_PROJECT_NAME).startDate(date).projectType(projectType).teamSize("2")
				.createdBy("GLO7HC").createdDate(date).hightlight("test highlight").problemStatement("").solution("")
				.benefits("").customer(customer).build();
		personal = personalRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);
		ProjectRole projectRole = ProjectRole.builder().id("1").name("Dev").description("Default").status("Active")
				.sequence(1).build();
		PersonalProject personalProject = PersonalProject.builder().personal(personal).project(project)
				.projectRole(projectRole).build();

		projectRepository.saveAndFlush(project);
		personalProjectRepository.saveAndFlush(personalProject);
		project.setPersonalProject(Collections.singletonList(personalProject));
	}

	@Test
	@DisplayName("Add Bosch Project")
	void testAddProject() throws JsonMappingException, JsonProcessingException {

		String json = "{" + "\"name\":\"Projectunittesr2\"," + "\"start_date\":\"2023-09-25\","
				+ "\"pm_name\":\"FIXED-TERMNguyenDuyLong(MS/EET11)\"," + "\"status\":\"New\","
				+ "\"project_type\":\"Bosch\"," + "\"project_type_id\":\"b8987dfa-f6e2-99d3-f511-3df3286bd7c0\","
				+ "\"objective\":\"Nocap\"," + "\"gb_unit\":\"MS\"," + "\"team_size\":\"10\","
				+ "\"description\":\"Nodes\"," + "\"stakeholder\":\"MSSS\"," + "\"project_phases\":[" + "{"
				+ "\"id\":\"c6b069d8-1f4d-aeee-585f-e07a5922976a\"," + "\"name\":\"prjConfigManagement\","
				+ "\"description\":\"ProjectManagementandConfigurationManagement\"" + "}," + "{"
				+ "\"id\":\"5ba242eb-f565-fbef-bed4-6ab7e03ae81c\"," + "\"name\":\"prjPlanCreationDeli\","
				+ "\"description\":\"ProductManagement-Planning,CreationandDelivery\"" + "}" + "]," + "\"skill_tags\":["
				+ "{" + "\"name\":\"Skilltagunittest\"" + "}" + "]," + "\"customer_gb\":\"test-customer\"" + "}";
		ProjectDto projectDto = objectMapper.readValue(json, ProjectDto.class);
		User user = userRepository.findAll().get(0);
		ProjectMemberDto projectMemberDto = ProjectMemberDto.builder().id(user.getId()).name(user.getDisplayName())
				.role("Dev").roleId("1").build();
		projectDto.setMembers(Collections.singletonList(projectMemberDto));
		projectDto.setCreatedBy("glo7hc");
		projectDto = projectService.save(projectDto);
		String idProject = projectDto.getId();

		// check id project after add new project
		assertThat(idProject).isNotEmpty();

		Optional<List<String>> phaseIdsOpt = phaseProjectRepository.findPhaseIdByProjectId(idProject);
		List<String> phaseIds = phaseIdsOpt.get();
		// check project is exist a phase specific after save a new project
		assertThat(phaseIds).isNotEmpty();
		// check size phase of project after save
		assertThat(phaseIds).hasSize(2);
	}

	@Test
	@DisplayName("Update info Portfolio")
	void updateInfoPortfolio() throws IOException {
		MultipartFile file = new MockMultipartFile("test", "test.txt", "application/txt",
				new ClassPathResource("test.txt").getInputStream());
		FileStorageDTO fileStorageDTO = projectService.updateInfoPortfolio(project.getId(), file, "project_benefit");
		assertThat(Constants.TXT_EXT).isEqualTo(fileStorageDTO.getExtension());
		assertThat("project_benefit" + Constants.UNDERSCORE + project.getId() + Constants.DOT + Constants.TXT_EXT)
				.isEqualTo(fileStorageDTO.getName());
		File folder = new File(projectStorageDir + Constants.SLASH + project.getId());
		assertThat(folder).exists();
		assertThat(folder).isDirectory();
		assertThat(folder.list()).hasSize(1);
		assertThat(folder.list()).hasSize(2);
	}

	@Test
	@DisplayName("Update info Portfolio - Layout not exist")
	void updateInfoPortfolio_LayoutNotExist() throws IOException {
		MultipartFile file = new MockMultipartFile("test", "test.txt", "application/txt",
				new ClassPathResource("test.txt").getInputStream());
		Exception exception = assertThrows(BadRequestException.class,
				() -> projectService.updateInfoPortfolio(project.getId(), file, "project_bedsdsnefit"));
		assertThat(
				messageSource.getMessage(MessageCode.INVALID_LAYOUT.toString(), null, LocaleContextHolder.getLocale()))
				.isEqualTo(exception.getMessage());
	}

	@Test
	@DisplayName("Update information portfolio - Project type is non bosch")
	void updateInfoPortfolio_ProjectTypeIsNonBosch() throws IOException {
		ProjectType projectType = ProjectType.builder().id("542f3498-9ee1-6183-e0c2-5d83aaa09b03").name("Non-Bosch")
				.build();
		project.setProjectType(projectType);
		MultipartFile file = new MockMultipartFile("test", "test.txt", "application/txt",
				new ClassPathResource("test.txt").getInputStream());
		Exception exception = assertThrows(BadRequestException.class,
				() -> projectService.updateInfoPortfolio(project.getId(), file, "project_bedsdsnefit"));
		assertThat(
				messageSource.getMessage(MessageCode.CANNOT_ADD_PORTFOLIO_FOR_NON_BOSCH.toString(), null,
						LocaleContextHolder.getLocale()))
				.isEqualTo(exception.getMessage());
	}

	@DisplayName("Get project portfolio")
	@Test
	void geProjectPortfolio() throws Exception {
		MultipartFile file = new MockMultipartFile("test", "test.txt", "application/txt",
				new ClassPathResource("test.txt").getInputStream());
		projectService.updateInfoPortfolio(project.getId(), file, "project_benefit");
		String result = projectService.getInforPortfolio(project.getId(), "project_benefit");
		assertThat(result).isNotEmpty();
	}

	@DisplayName("Get project portfolio - Layout not exist")
	@Test
	void geProjectPortfolio_LayoutNotExist() throws Exception {
		Exception exception = assertThrows(BadRequestException.class,
				() -> projectService.getInforPortfolio(project.getId(), "project_bendsdsefit"));
		assertThat(messageSource.getMessage(MessageCode.INVALID_LAYOUT.toString(), null, LocaleContextHolder.getLocale()))
				.isEqualTo(exception.getMessage());
	}

	@DisplayName("Check folder is available after delete project")
	@Test
	void checkFolderIsAvailableAfterDeleteProject() throws Exception {
		MultipartFile file = new MockMultipartFile("test", "test.txt", "application/txt",
				new ClassPathResource("test.txt").getInputStream());
		projectService.updateInfoPortfolio(project.getId(), file, "project_benefit");
		ProjectDto projectDto = ProjectDto.builder().id(project.getId()).build();
		projectService.deleteProject(projectDto);
		File folder = new File(projectStorageDir + Constants.SLASH + project.getId());
		assertThat(folder).doesNotExist();
		assertThat(folder.isDirectory()).isFalse();
	}

	@Test
	@DisplayName("Get project portfolio - happy case")
	void testGetProjectPortfolioHappy() {
		ProjectDto projectDto = projectService.getPortfolio(project.getId());

		assertThat(TEST_PROJECT_NAME).isEqualTo(projectDto.getName());
		assertThat(projectDto.getTeamSize()).hasSize(2);
		assertThat("test highlight").isEqualTo(projectDto.getHighlight());
		assertThat("test-customer").isEqualTo(projectDto.getCustomerGb());
		assertNull(projectDto.getSkillTags());
	}

	@Test
	@DisplayName("Get project portfolio - not found")
	void testGetProjectPortfolioNotFound() {
		assertThrows(SkillManagementException.class, () -> projectService.getPortfolio("unexisted Id"));
	}

	@Test
	@DisplayName("Edit project portfolio - happy case")
	void testEditProjectPortfolioHappy() {
		Project projectBeforeEdit = projectRepository.findById(project.getId()).orElse(null);
		assertNotNull(projectBeforeEdit);
		assertThat(TEST_PROJECT_NAME).isEqualTo(projectBeforeEdit.getName());
		assertThat("test highlight").isEqualTo(projectBeforeEdit.getHightlight());

		ProjectDto projectDto = ProjectDto.builder().id(project.getId()).name(TEST_PROJECT_NAME)
				.customerGb("test-customer").teamSize("1").highlight("test highlight edited").build();

		projectService.editPortfolio(projectDto);

		Project projectAfterEdited = projectRepository.findById(project.getId()).orElse(null);
		assertNotNull(projectAfterEdited);
		assertThat("test highlight edited").isEqualTo(projectAfterEdited.getHightlight());
	}

	@Test
	@DisplayName("Edit project portfolio - null id")
	void testEditProjectPortfolioNullId() {
		ProjectDto projectDto = ProjectDto.builder().name(TEST_PROJECT_NAME).customerGb("test-customer").teamSize("1")
				.highlight("test highlight edited").build();
		assertThrows(SkillManagementException.class, () -> projectService.editPortfolio(projectDto));
	}

	@Test
	@DisplayName("Edit project portfolio - not found")
	void testEditProjectPortfolioNotFound() {
		ProjectDto projectDto = ProjectDto.builder().id("unexisted Id").name(TEST_PROJECT_NAME)
				.customerGb("test-customer").teamSize("1").highlight("test highlight edited").build();
		assertThrows(SkillManagementException.class, () -> projectService.editPortfolio(projectDto));
	}

}
