package com.bosch.eet.skill.management.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.entity.SkillHighlight;
import com.bosch.eet.skill.management.entity.SkillType;

@DataJpaTest
@ActiveProfiles("dev")
public class SkillHighlightRepositoryTest {
	
	@Autowired
	private SkillsHighlightReposistory skillsHighlightReposistory;
	
	@Autowired
	private PersonalRepository personalRepository;
	
	@Autowired
	private SkillGroupRepository skillGroupRepository;
	
	@Autowired
	private SkillRepository skillRepository;
	
	@Autowired
	private PersonalSkillRepository personalSkillRepository;
	
	private Skill skill1;
	
	private SkillHighlight skillHighlight;
	
	@BeforeEach
	void perpare() {
		Pageable limit = PageRequest.of(0, 2);// limit
		List<Personal> personals = personalRepository.findAll(limit).getContent();
		Personal personal = personals.get(0);
		SkillType skillType = SkillType.builder().id("88db5609-57c7-482f-a6bb-4de686560b85")
				.name("253bdd5d-767a-34c9-8f81-af68eab56b09").build();
		SkillGroup skillGroup = SkillGroup.builder().name("Skill group test").skillType(skillType).build();
		skill1 = Skill.builder().name("Name skill 1").description("NO").skillGroup(skillGroup).status("Active").build();
		PersonalSkill personalSkill = PersonalSkill.builder().personal(personal).skill(skill1).build();
		Set<PersonalSkill> personalSkillSet = new HashSet<>(personal.getPersonalSkills());
		personalSkillSet.add(personalSkill);
		skillHighlight = SkillHighlight.builder().personal(personal).personalSkill(personalSkill).build();
		
		//save
		skillGroupRepository.saveAndFlush(skillGroup);
		skillRepository.saveAndFlush(skill1);
		personalSkillRepository.saveAndFlush(personalSkill);
		skillsHighlightReposistory.saveAndFlush(skillHighlight);
	}

	@Test
	@DisplayName("Find Skill Highlight by skill id")
	void findByPersonalSkillSkillId() {
		List<SkillHighlight> skillHighlights = skillsHighlightReposistory.findByPersonalSkillSkillId(skill1.getId());
		assertThat(skillHighlights).hasSize(1);
		assertThat(skillHighlights).contains(skillHighlight);		
		skillHighlights = skillsHighlightReposistory.findByPersonalSkillSkillId("idnotexist");
		assertThat(skillHighlights).isEmpty();
		assertThat(skillHighlights).doesNotContain(skillHighlight);
	}
}
