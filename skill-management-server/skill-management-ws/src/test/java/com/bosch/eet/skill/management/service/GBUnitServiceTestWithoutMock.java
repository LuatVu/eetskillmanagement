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
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.entity.GbUnit;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class GBUnitServiceTestWithoutMock {

	@Autowired
	private GbUnitService gbUnitService;

	@Test
	@DisplayName("Find all gb unit")
	void testFindAll() {
		List<GbUnit> gbunit = gbUnitService.findAll();
		List<String> nameGBUnits = gbunit.stream().map(item -> item.getName()).collect(Collectors.toList());
		assertThat(gbunit).hasSizeGreaterThanOrEqualTo(2);
		assertThat(nameGBUnits).contains("MS", "PS");
	}
}
