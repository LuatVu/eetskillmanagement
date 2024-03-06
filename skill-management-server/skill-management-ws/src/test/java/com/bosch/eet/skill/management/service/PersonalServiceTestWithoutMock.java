package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.dto.PersonalProjectDto;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectRole;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.ProjectRepository;
import com.bosch.eet.skill.management.repo.ProjectRoleRepository;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PersonalServiceTestWithoutMock {
	
	@Autowired
	private PersonalService personalService;
	
	@Autowired
	private PersonalRepository personalRepository;
	
	@Autowired
	private ProjectRoleRepository projectRoleRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	private Personal personal;
	
	private ProjectRole projectRole;
	
	private Project project;

	@BeforeEach
	void prepareData() {
		Pageable limit = PageRequest.of(0, 1);
		List<Personal> personals = personalRepository.findAll(limit).getContent();
		personal = personals.get(0);
		
		projectRole = projectRoleRepository.getById("1");
		
		List<Project> projects = projectRepository.findAll(limit).getContent();
		project=projects.get(0);
	}
	
	@Test
	@DisplayName("Add personal project")
	void addPersonalProject() {
		String personalId = personal.getId();
		PersonalProjectDto personalProjectDto = PersonalProjectDto.builder().roleId(projectRole.getId())
				.memberStartDate("2022/2/2").memberEndDate("2022/2/2").projectId(project.getId()).build();
		PersonalProjectDto personalProjectDtoResult = personalService.addPersonalProject(personalId, personalProjectDto);
		assertThat(project.getLeader()).isEqualTo(personalProjectDtoResult.getPmName());
	}
}
