package com.bosch.eet.skill.management.service;

import java.util.List;

import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.SkillCompetencyLeadDto;
import com.bosch.eet.skill.management.dto.SkillDto;

public interface CompetencyLeadService {
    int save(List<SkillCompetencyLeadDto> skillCompetencyLeadDtos);

    List<SkillCompetencyLeadDto> findAllCompetencyLead();
    
	List<SkillDto> findSkillsByCompetencyLeadId(String competencyLeadId);

	List<SkillCompetencyLeadDto> findAll();

	List<SkillCompetencyLeadDto> findCompetencyLeadBySkillId(String skillId);

	List<PersonalDto> findCompetencyLeadBySkillGroupId(String skillGroupId);    
	
	void deleteCompetencyLead(SkillCompetencyLeadDto skillCompetencyLead);
}
