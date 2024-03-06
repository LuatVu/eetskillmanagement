package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.dto.SkillTypeDto;
import com.bosch.eet.skill.management.entity.SkillType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
public class SkillTypeServiceTest {

    @Autowired
    private SkillTypeService skillTypeService;

	@Test
	@DisplayName("Find all skill types")
	@Transactional
	void findAllSkillType() {
		List<SkillTypeDto> listSkillTypeDTO = skillTypeService.findAllSkillType();
        assertThat(listSkillTypeDTO).isNotEmpty();
		SkillTypeDto skillTypeDto1 = SkillTypeDto.builder()
				.id("88db5609-57c7-482f-a6bb-4de686560b85")
				.name("Behavioral")
				.build();
        assertThat(listSkillTypeDTO).contains(skillTypeDto1);
		SkillTypeDto skillTypeDto2 = SkillTypeDto.builder()
				.id("iddummy")
				.name("Name dummy")
				.build();
        assertThat(listSkillTypeDTO).doesNotContain(skillTypeDto2);
		SkillTypeDto skillTypeDto3 = SkillTypeDto.builder()
				.id("253bdd5d-767a-34c9-8f81-af68eab56b09")
				.name("Technical")
				.build();
		assertThat(listSkillTypeDTO).contains(skillTypeDto3);
	}
	
	@Test
	@DisplayName("Find all")
	@Transactional
	void findAll() {
		List<SkillType> skillTypes = skillTypeService.findAll();
		assertThat(skillTypes).hasSize(2);
	}

}