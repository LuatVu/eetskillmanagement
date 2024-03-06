package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.dto.ProjectScopeDto;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectScope;
import com.bosch.eet.skill.management.entity.ProjectType;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.repo.ProjectRepository;
import com.bosch.eet.skill.management.repo.ProjectScopeRepository;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class ProjectScopeServiceTestWithoutMock {

	private static final String TEST_NAME_1 = "Test name 1";

	private static final String TEST_NAME_2 = "Test name 2";
	
	private ProjectScope projectScope1;
	
	private ProjectScope projectScope2;

	private String projectId;

	@Autowired
	private ProjectScopeService projectScopeService ;
	
	@Autowired
	private ProjectScopeRepository projectScopeRepository;
	
	@Autowired
	private ProjectRepository projectRepository;

	@BeforeEach
	void prepareData() throws ParseException {
		projectScope1 = ProjectScope.builder().name(TEST_NAME_1).colour("#ffffff").hoverColour("#000000").build();
		projectScope2 = ProjectScope.builder().name(TEST_NAME_2).colour("#000000").hoverColour("#ffffff").build();
		projectScopeRepository.saveAllAndFlush(Arrays.asList(projectScope1, projectScope2));
		ProjectType projectType = ProjectType.builder().id("b8987dfa-f6e2-99d3-f511-3df3286bd7c0").name("Bosch")
				.build();
		Project project = Project.builder()
				.createdBy("test")
				.createdDate(new Date())
				.startDate(new Date())
				.projectType(projectType)
				.name("project scope test")
				.projectScope(projectScope1)
				.build();

		projectId = projectRepository.saveAndFlush(project).getId();
	}

	@Test
	@DisplayName("Test find all projectScope - happy")
	void testFindAll() {
		List<ProjectScopeDto> projectScopeDtos = projectScopeService.findAll();
		assertThat(projectScopeDtos).hasSizeGreaterThanOrEqualTo(2);
		Set<String> scopeNames = projectScopeDtos.stream().map(ProjectScopeDto::getName).collect(Collectors.toSet());
		assertThat(scopeNames).contains(TEST_NAME_1);
	}
	
	@Test
	@DisplayName("Test find projectScope by id - id not exist")
	void testFindByIdNotExist() {
		assertThrows(SkillManagementException.class, () -> projectScopeService.findById("not exist Id"));
	}

	@Test
	@DisplayName("Test find projectScope by id - happy")
	void testFindById() {
		ProjectScopeDto projectScopeDto = projectScopeService.findById(projectScope1.getId());
		assertThat(TEST_NAME_1).isEqualTo(projectScopeDto.getName());
		assertThat("#ffffff").isEqualTo(projectScopeDto.getColour());
		assertThat("#000000").isEqualTo(projectScopeDto.getHoverColour());
	}

	@Test
	@DisplayName("Test create new projectScope - happy")
	void testCreate() throws IOException{
		ProjectScopeDto projectScopeDto = ProjectScopeDto.builder()
				.name("test new scope")
				.colour("#4ca13f")
				.hoverColour("#000000")
				.build();
		ProjectScopeDto result = projectScopeService.createNewProjectScope( projectScopeDto);
		assertNotNull(result);
		assertThat(result.getName()).isEqualTo(projectScopeDto.getName());
		assertThat(result.getColour()).isEqualTo(projectScopeDto.getColour());
		assertThat(result.getHoverColour()).isEqualTo(projectScopeDto.getHoverColour());
	}

	@Test
	@DisplayName("Test create new projectScope - name existed")
	void testCreateNameExisted() throws IOException{
		ProjectScopeDto projectScopeDto = ProjectScopeDto.builder()
				.name(TEST_NAME_1)
				.colour("#4ca13f")
				.hoverColour("#000000")
				.build();
		assertThrows(SkillManagementException.class, () -> projectScopeService.createNewProjectScope(projectScopeDto));
	}

	@Test
	@DisplayName("Test update projectScope - happy")
	void testUpdate() throws IOException{
		ProjectScopeDto projectScopeDto = ProjectScopeDto.builder()
				.id(projectScope2.getId())
				.name(TEST_NAME_2 + " edited")
				.colour("#4ca13f")
				.hoverColour("#ffffff")
				.build();
		
		assertThat(projectScope2.getId()).isEqualTo(projectScopeDto.getId());
		assertNotEquals(projectScope2.getName(), projectScopeDto.getName());
		assertNotEquals(projectScope2.getColour(), projectScopeDto.getColour());
		projectScopeService.updateProjectScope(projectScopeDto);
		ProjectScope savedProjectScope = projectScopeRepository.findById(projectScope2.getId()).get();
		assertNotNull(savedProjectScope);
		assertThat(projectScopeDto.getId()).isEqualTo(savedProjectScope.getId());
		assertThat(projectScopeDto.getName()).isEqualTo(savedProjectScope.getName());
		assertThat(projectScopeDto.getColour()).isEqualTo(savedProjectScope.getColour());
		assertThat(projectScopeDto.getHoverColour()).isEqualTo(savedProjectScope.getHoverColour());
	}

	@Test
	@DisplayName("Test update projectScope - not exist")
	void testUpdateNotExist() throws IOException{
		ProjectScopeDto projectScopeDto = ProjectScopeDto.builder()
				.id("not exist id")
				.name(TEST_NAME_2 + " edited")
				.colour("#4ca13f")
				.hoverColour("#ffffff")
				.build();
		assertThrows(SkillManagementException.class, () -> projectScopeService.updateProjectScope(projectScopeDto));
	}

	@Test
	@DisplayName("Test delete customer - happy")
	void testDelete() throws IOException {
		projectScopeService.deleteProjectScope(projectScope1.getId());
		projectRepository.flush();
		Optional<ProjectScope> projectScopeOptional = projectScopeRepository.findById(projectScope1.getId());
		assertThat(projectScopeOptional).isNotPresent();
	}

	@Test
	@DisplayName("Test delete customer - not exist")
	void testDeleteNotExist() throws IOException {
		assertThrows(SkillManagementException.class, () ->
						projectScopeService.deleteProjectScope("not exist Id"));
	}
}
