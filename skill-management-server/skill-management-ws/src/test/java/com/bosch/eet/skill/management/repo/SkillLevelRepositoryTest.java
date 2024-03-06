package com.bosch.eet.skill.management.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.entity.SkillLevel;

@DataJpaTest
@ActiveProfiles("dev")
public class SkillLevelRepositoryTest {
	
	@Autowired
	private SkillLevelRepository skillLevelRepository;
	
	@Autowired
	private SkillGroupRepository skillGroupRepository;
	
	@Autowired
	private SkillRepository skillRepository;
	
	private SkillLevel skillLevel;
	
	private Skill skill1;
	
	@BeforeEach
	void perpare() {
		SkillGroup skillGroup = skillGroupRepository.findById("28c22540-d718-0fe5-60e2-a9518f0b4190").get();
		skill1 = Skill.builder().name("Name skill 1").description("NO").skillGroup(skillGroup).status("Active")
				.build();

		Level level = Level.builder().id("47bd0d20-a4ec-483e-af1e-ea5fb4114726").description("L52").name("L52").build();
		skillLevel = SkillLevel.builder().level(level).levelLable("Level 1").skill(skill1)
				.skillGroup(skillGroup).build();
		skillRepository.saveAndFlush(skill1);
		skillLevelRepository.saveAndFlush(skillLevel);
	}

	@Test
	@DisplayName("Find by skill id")
	void findBySkillId() {
		List<SkillLevel> skillLevels = skillLevelRepository.findBySkillId(skill1.getId());
		assertThat(skillLevels).hasSize(1);
		assertThat(skillLevels).contains(skillLevel);
		skillLevels = skillLevelRepository.findBySkillId("idnotexist");
		assertThat(skillLevels).isEmpty();
		assertThat(skillLevels).doesNotContain(skillLevel);
	}
}
