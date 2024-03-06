package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.bosch.eet.skill.management.entity.ProjectType;

@SpringBootTest
@ActiveProfiles("dev")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ProjectTypeServiceTestWithoutMock {
	@Autowired
	private ProjectTypeService projectTypeService;

	@Test
	@DisplayName("Test find all")
	void testFindAll() {
		List<ProjectType> projectTypes = projectTypeService.findAll();
		List<String> projectTypeNames = projectTypes.stream().map(item->item.getName()).collect(Collectors.toList());
		assertThat(projectTypes).hasSizeGreaterThanOrEqualTo(2);
		assertThat(projectTypeNames).contains("Bosch","Non-Bosch");
	}
}
