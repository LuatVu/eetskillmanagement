package com.bosch.eet.skill.management.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;

@DataJpaTest
@ActiveProfiles("dev")
@Transactional
public class PersonalSkillReposistoryTest {
	private String personalId;
	
	@Autowired
	private SkillRepository skillRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PersonalRepository personalRepository;

	@Autowired
	private PersonalSkillRepository personalSkillRepository;
	
	@BeforeEach
	void setUpData() {
		Skill skill1 = Skill.builder().name("skill test 1").status("Active").build();
		Skill skill2 = Skill.builder().name("skill test 2").status("Active").build();
		Skill skill3 = Skill.builder().name("skill test 3").status("Active").build();
		Skill skill4 = Skill.builder().name("skill test 4").status("Active").build();
		Skill skill5 = Skill.builder().name("skill test 5").status("Active").build();
		List<Skill> skillList = Arrays.asList(skill1, skill2, skill3, skill4, skill5);
		List<Skill> savedSkillList = skillRepository.saveAllAndFlush(skillList);
		
		User user = User.builder().name("testUserName").createdBy("").createdDate(LocalDateTime.now()).type("Person").status("Active").build();
		personalId = userRepository.saveAndFlush(user).getId();
		
		Personal personal = Personal.builder().id(personalId).personalCode("testCode").title("developer").build();
		personalRepository.saveAndFlush(personal);
		
		PersonalSkill personalSkill1 = PersonalSkill.builder().personal(personal).skill(savedSkillList.get(0)).level(0).build();
		PersonalSkill personalSkill2 = PersonalSkill.builder().personal(personal).skill(savedSkillList.get(1)).level(1).build();
		PersonalSkill personalSkill3 = PersonalSkill.builder().personal(personal).skill(savedSkillList.get(2)).level(2).build();
		PersonalSkill personalSkill4 = PersonalSkill.builder().personal(personal).skill(savedSkillList.get(3)).level(3).build();
		PersonalSkill personalSkill5 = PersonalSkill.builder().personal(personal).skill(savedSkillList.get(4)).level(0).build();
		List<PersonalSkill> PersonalSkillList = Arrays.asList(personalSkill1, personalSkill2, personalSkill3, personalSkill4, personalSkill5);
		personalSkillRepository.saveAllAndFlush(PersonalSkillList);
	}
	
	@Test
	void testFindSkillByPesonalIdAndNotEqualLevel() {
		List<Skill> resultSkillList = personalSkillRepository.findSkillByPesonalIdAndNotEqualLevel(personalId, Constants.DEFAULT_FLOAT_SKILL_LEVEL);
		Set<String> expectedSkillNameSet = new HashSet<>(Arrays.asList("skill test 2", "skill test 3", "skill test 4"));
		
		assertThat(resultSkillList).hasSize(3);
		assertThat(expectedSkillNameSet).contains(resultSkillList.get(0).getName(),resultSkillList.get(1).getName(),resultSkillList.get(2).getName());
	}
}
