package com.bosch.eet.skill.management.service;

import java.util.List;

import com.bosch.eet.skill.management.dto.SkillClusterDTO;
import com.bosch.eet.skill.management.dto.SkillGroupDto;
import com.bosch.eet.skill.management.entity.SkillGroup;

public interface SkillGroupService {

	List<SkillGroupDto> findAllSkillGroups();
	
	List<String> findSkillIdBySkillGroup(String skillGroupId);

	SkillGroupDto findById(String skillGroupId);

	SkillGroupDto addSkillGroup(SkillGroupDto skillGroupDto);

	boolean deleteSkillGroup(SkillGroup skillGroup);

	List<SkillClusterDTO> findAllSkillGroupsWithSkills();

}
