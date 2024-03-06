package com.bosch.eet.skill.management.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.ParseException;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.common.Status;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.entity.SkillType;
import com.bosch.eet.skill.management.repo.SkillGroupRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;
import com.bosch.eet.skill.management.repo.SkillTypeRepository;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class SkillFacadeTestWithoutMock {
	private static final String SKILL_NAME = "skill name unit testing";
	private static final String SKILL_TYPE = "skill type unit testing";
	private static final String SKILL_GROUP_NAME = "skill group name unit testing";
	private String skillId;
	private String skillGroupId;

	@Autowired
	private SkillRepository skillRepository;

	@Autowired
	private SkillTypeRepository skillTypeRepository;

	@Autowired
	private SkillGroupRepository skillGroupRepository;

	@Autowired
	private SkillFacade skillFacade;

	@BeforeEach
	void prepare() throws ParseException {
		SkillType skillType = skillTypeRepository.save(SkillType.builder().name(SKILL_TYPE).build());

		SkillGroup skillGroup = skillGroupRepository.save(SkillGroup.builder()
				.name(SKILL_GROUP_NAME).skillType(skillType).build());
		this.skillGroupId = skillGroup.getId();

		Skill skill = skillRepository.save(Skill.builder().name(SKILL_NAME).status(Status.ACTIVE.getLabel()).skillGroup(skillGroup).build());
		this.skillId = skill.getId();

		skillGroup.setSkills(Collections.singletonList(skill));
	}

	@Test
	@DisplayName("Delete skill group - happy")
	void testDeleteSkillGroupHappy() {
		skillFacade.deleteSkillGroup(skillGroupId);
		assertThat(skillGroupRepository.existsById(skillGroupId)).isFalse();
		assertThat(skillRepository.existsById(skillId)).isFalse();
	}

	@Test
	@DisplayName("Delete skill group - not found")
	void testDeleteSkillGroupNotFound() {
		assertThrows(Exception.class, () -> skillFacade.deleteSkillGroup("not exist skill group unit testing"));
	}
}
