package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.bosch.eet.skill.management.dto.PhaseDto;
import com.bosch.eet.skill.management.dto.VModelDto;
import com.bosch.eet.skill.management.service.impl.CommonServiceImpl;

@SpringBootTest
@ActiveProfiles("dev")
public class CommonServiceTestWithoutMock {

	@Autowired
	private CommonServiceImpl commonService;
	
	@Test
	@Transactional
	void testGetVmodel() {
		VModelDto vModelDto = commonService.getVModel();
		List<PhaseDto> phaseDtos = vModelDto.getPhases();
		List<String> namePhase = phaseDtos.stream().map(item -> item.getName()).collect(Collectors.toList());
		assertThat(phaseDtos).isNotEmpty();
		assertThat(namePhase).contains("ciDashboard");
		assertThat(namePhase).doesNotContain("softwareDashboard");
	}
}
