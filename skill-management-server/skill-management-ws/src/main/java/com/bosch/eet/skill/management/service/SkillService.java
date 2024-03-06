package com.bosch.eet.skill.management.service;

import java.util.List;
import java.util.Map;

import com.bosch.eet.skill.management.dto.AddSkillDto;
import com.bosch.eet.skill.management.dto.PersonalSkillDto;
import com.bosch.eet.skill.management.dto.SkillDto;
import com.bosch.eet.skill.management.dto.SkillExpectedLevelDto;
import com.bosch.eet.skill.management.dto.SkillExperienceLevelDTO;
import com.bosch.eet.skill.management.dto.SkillGroupLevelDto;
import com.bosch.eet.skill.management.dto.SkillHighlightDto;
import com.bosch.eet.skill.management.dto.SkillManagementDto;
import com.bosch.eet.skill.management.entity.Skill;

public interface SkillService {

	List<String> findAllSkills();
	
	List<String> findAllSkillsName();

	List<SkillExperienceLevelDTO> findAllSkillExperienceBySkillId(String skillId);

	List<PersonalSkillDto> addSkillsByListOfSkillGroups(AddSkillDto addSkillDto);

	Skill saveSkillManagement(SkillManagementDto skillManagementDto);
	
	SkillDto findById(String skillId);

	String deleteSkill(String skillId);

	Map<String, SkillGroupLevelDto> findAllSkillLevel();

	SkillGroupLevelDto findSkillExperienceDetailBySkillId(String skillId);

	boolean editNewSkill(SkillManagementDto skillManagementDto);
	
	List<SkillHighlightDto> findSkillsHighlight(String idPersonal);
	
	List<PersonalSkillDto> saveSkillsHighlight(String idPersonal, List<String> personalSkillDtos);

	Skill saveAndSyncElasticSkill(Skill skill);

	List<Skill> findAllByName(String name);
	
	Map<String, Object> getSkillLevelExpected(Integer pageNumber, Integer size, List<String> skillCluster);
	
	String updateSkillLevel(List<SkillExpectedLevelDto> skillExpectedLevelDtos);
}
