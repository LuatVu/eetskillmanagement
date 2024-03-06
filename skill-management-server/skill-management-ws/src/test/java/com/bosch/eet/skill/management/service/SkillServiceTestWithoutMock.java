package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.elasticsearch.repo.SkillElasticRepository;
import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.RequestEvaluation;
import com.bosch.eet.skill.management.entity.RequestEvaluationDetail;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillCompetencyLead;
import com.bosch.eet.skill.management.entity.SkillExperienceLevel;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.entity.SkillHighlight;
import com.bosch.eet.skill.management.entity.SkillLevel;
import com.bosch.eet.skill.management.exception.ResourceNotFoundException;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.PersonalSkillRepository;
import com.bosch.eet.skill.management.repo.RequestEvaluationDetailRepository;
import com.bosch.eet.skill.management.repo.RequestEvaluationRepository;
import com.bosch.eet.skill.management.repo.SkillCompetencyLeadRepository;
import com.bosch.eet.skill.management.repo.SkillExperienceLevelRepository;
import com.bosch.eet.skill.management.repo.SkillGroupRepository;
import com.bosch.eet.skill.management.repo.SkillLevelRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;
import com.bosch.eet.skill.management.repo.SkillsHighlightReposistory;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SkillServiceTestWithoutMock {

	@Autowired
	private SkillService skillService;

	@Autowired
	private SkillRepository skillRepository;

	@Autowired
	private SkillGroupRepository skillGroupRepository;

	@Autowired
	private PersonalRepository personalRepository;

	@Autowired
	private PersonalSkillRepository personalSkillRepository;

	@Autowired
	private SkillLevelRepository skillLevelRepository;

	@Autowired
	private SkillExperienceLevelRepository skillExperienceLevelRepository;

	@Autowired
	private RequestEvaluationRepository requestEvaluationRepository;

	@Autowired
	private RequestEvaluationDetailRepository requestEvaluationDetailRepository;

	@Autowired
	private SkillsHighlightReposistory skillsHighlightReposistory;

	@Autowired
	private SkillCompetencyLeadRepository skillCompetencyLeadRepository;

	@Autowired
	private SkillElasticRepository skillElasticRepository;

	private String skillExpLevelName = "Name test skill exp level";

	private Skill skill1;

	private Skill skill2;

	@BeforeEach
	void prepare() throws ParseException {
		String dateStr1 = "2023/02/02";
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date1 = format.parse(dateStr1);

		Pageable limit = PageRequest.of(0, 2);// limit
		List<Personal> personals = personalRepository.findAll(limit).getContent();

		SkillGroup skillGroup = skillGroupRepository.findById("28c22540-d718-0fe5-60e2-a9518f0b4190").get();
		Personal personal = personals.get(0);
		skill1 = Skill.builder().name("Name skill 1").description("NO").skillGroup(skillGroup).status("Active").build();
		skill2 = Skill.builder().name("Name skill 2").description("NO").skillGroup(skillGroup).status("Active").build();
		Level level = Level.builder().id("47bd0d20-a4ec-483e-af1e-ea5fb4114726").description("L52").name("L52").build();
		SkillExperienceLevel skillExperienceLevel = SkillExperienceLevel.builder().name(skillExpLevelName)
				.description("test").level(level).skill(skill1).skillGroup(skillGroup).build();
		PersonalSkill personalSkill = PersonalSkill.builder().personal(personal).skill(skill1).build();
		SkillLevel skillLevel = SkillLevel.builder().level(level).levelLable("Level 1").skill(skill1)
				.skillGroup(skillGroup).build();
		RequestEvaluation requestEvaluation = RequestEvaluation.builder().requester(personal).approver(personal)
				.status("APPROVED").build();
		RequestEvaluationDetail requestEvaluationDetail = RequestEvaluationDetail.builder().skill(skill1)
				.status("APPROVED").approver(personal).currentExp(1).currentLevel(1).createdDate(date1).build();
		SkillHighlight skillHighlight = SkillHighlight.builder().personal(personal).personalSkill(personalSkill)
				.build();
		SkillCompetencyLead skillCompetencyLead = SkillCompetencyLead.builder().personal(personal).skill(skill1)
				.skillGroup(skillGroup).build();

		// set
		Set<PersonalSkill> personalSkillSet = new HashSet<>(personal.getPersonalSkills());
		personalSkillSet.add(personalSkill);
		skill1.setPersonalSkill(Collections.singletonList(personalSkill));
		personal.setPersonalSkills(personalSkillSet);
		requestEvaluation.setRequestEvaluationDetails(Collections.singletonList(requestEvaluationDetail));
		requestEvaluationDetail.setRequestEvaluation(requestEvaluation);

		// save
		skillRepository.saveAllAndFlush(Arrays.asList(skill1, skill2));
		skillExperienceLevelRepository.saveAndFlush(skillExperienceLevel);
		personalSkillRepository.saveAndFlush(personalSkill);
		personalRepository.saveAndFlush(personal);
		skillLevelRepository.saveAndFlush(skillLevel);
		requestEvaluationRepository.saveAndFlush(requestEvaluation);
		requestEvaluationDetailRepository.saveAndFlush(requestEvaluationDetail);
		skillsHighlightReposistory.saveAndFlush(skillHighlight);
		skillCompetencyLeadRepository.saveAndFlush(skillCompetencyLead);
	}

	@Test
	@DisplayName("Delete skill")
	void deleteSkill() {
		String message = skillService.deleteSkill(skill1.getId());
		assertThat(Constants.SUCCESS).isEqualTo(message);
		assertThat(skillElasticRepository.existById(skill1.getId())).isFalse();
	}

	@Test
	@DisplayName("Delete skill - Skill Not Found")
	void deleteSkill_SkillNotFound() {
		Exception exception = assertThrows(ResourceNotFoundException.class,
				() -> skillService.deleteSkill("idnotexist"));
		assertThat("Skill not found").isEqualTo(exception.getMessage());
	}

	@Test
	@DisplayName("Delete skill - Not reference")
	void deleteSkill_NotRefrence() {
		String message = skillService.deleteSkill(skill2.getId());
		assertThat(Constants.SUCCESS).isEqualTo(message);
	}
}
