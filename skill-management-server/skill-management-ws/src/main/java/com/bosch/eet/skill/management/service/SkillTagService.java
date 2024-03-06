/**
 * 
 */
package com.bosch.eet.skill.management.service;

import java.util.List;

import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.SkillTagDto;
import com.bosch.eet.skill.management.entity.SkillTag;

/**
 * @author VOU6HC
 */
public interface SkillTagService {
	List<SkillTagDto> findAllSkillTags();
	
	SkillTagDto findById(String skillTagId);

	List<SkillTag> saveAll(Iterable<SkillTag> skillTags);

	List<SkillTag> findAll();

	SkillTag findByNameIgnoreCase(String name);

	List<ProjectDto> findProjectsBySkillTagsId(String skillTagId);

	SkillTagDto addSkillTag(SkillTagDto skillTagRequestDto);

	SkillTagDto updateSkillTag(String skillTagId, SkillTagDto skillTagRequestDto);
	
	boolean deleteSkillTag(SkillTagDto skillTagRequestDto);

	String updateOrder(List<SkillTagDto> skillTagDtos);

	String replaceSkillTag(List<SkillTagDto> skillTagRequests);
}
