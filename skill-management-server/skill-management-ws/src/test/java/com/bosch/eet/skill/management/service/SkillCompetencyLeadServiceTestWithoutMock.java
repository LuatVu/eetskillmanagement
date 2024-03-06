package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.bosch.eet.skill.management.dto.SkillCompetencyLeadDto;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillCompetencyLead;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.SkillCompetencyLeadRepository;
import com.bosch.eet.skill.management.repo.SkillGroupRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;

@SpringBootTest
@ActiveProfiles("dev")
public class SkillCompetencyLeadServiceTestWithoutMock {
	
	@Autowired
	private CompetencyLeadService competencyLeadService;
	
	@Autowired
	private PersonalRepository personalRepository;
	
	@Autowired
	private SkillCompetencyLeadRepository skillCompetencyLeadRepository;
	
	@Autowired
	private SkillGroupRepository skillGroupRepository;
	
	@Autowired
	private SkillRepository skillRepository;
	
	private Personal personal;
	
	private SkillGroup skillGroup;
	
	private SkillCompetencyLead competencyLead;

	@BeforeEach
	void prepare() {
		Pageable limit = PageRequest.of(0, 1);
		List<Personal> personals = personalRepository.findAll(limit).getContent();
		personal = personals.get(0);
		List<SkillGroup> skillGroups = skillGroupRepository.findAll(limit).getContent();
		skillGroup = skillGroups.get(0);
		List<Skill> skills = skillRepository.findAll(limit).getContent();
		Skill skill = skills.get(0);
		competencyLead = SkillCompetencyLead.builder()
				.personal(personal)
				.skillGroup(skillGroup)
				.skill(skill)
				.build();
		skillCompetencyLeadRepository.saveAndFlush(competencyLead);
	}
	
	@Test
	@DisplayName("Find all")
	void findAll() {
		List<SkillCompetencyLeadDto> competencyLeads = competencyLeadService.findAll();
		assertThat(competencyLeads).hasSizeGreaterThanOrEqualTo(1);
	}
	
	@Test
	@DisplayName("Delete competency lead")
	@Transactional
	void deleteCompetencyLead() {
		SkillCompetencyLeadDto competencyLeadDto = SkillCompetencyLeadDto.builder().personalId(personal.getId())
				.skillGroupId(skillGroup.getId()).build();
		competencyLeadService.deleteCompetencyLead(competencyLeadDto);
		List<SkillCompetencyLead> skillsByCompetencyLeadID = skillCompetencyLeadRepository
				.findByPersonalId(personal.getId());
		assertThat(skillsByCompetencyLeadID).isEmpty();;
	}
}
