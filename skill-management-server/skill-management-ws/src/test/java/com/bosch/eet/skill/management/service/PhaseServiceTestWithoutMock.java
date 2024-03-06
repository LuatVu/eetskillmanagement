package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.bosch.eet.skill.management.entity.Phase;

@SpringBootTest
@ActiveProfiles("dev")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PhaseServiceTestWithoutMock {
	
	@Autowired
	private PhaseService phaseService;

	@Test
	@DisplayName("Test find all by list description like")
	void testFindAllByListDescriptionLike() {
		String des1 = "Software Integration & System Integration Test";
		String des2 = "Software Design & Model Rapid Prototyping";
		String des3 = "Generic review";
		String des4 = "Change Request and Problem Resolution Management";
		String des5 = "CI Dashboard";
		String des6 = "Project Management and Configuration Management";
		String des7 = "Software Construction";
		List<String> descriptionPhases = new ArrayList<>(Arrays.asList(des1, des2, des3, des4, des5, des6, des7));
		List<Phase> phases = phaseService.findAllByListDescriptionLike(descriptionPhases);
		List<String> namePhases = phases.stream().map(item->item.getDescription()).collect(Collectors.toList());
		assertThat(descriptionPhases.size()).isEqualTo(phases.size());		
		assertThat(namePhases).contains(des1,des2,des3,des4,des5,des6,des7);
	}
}
