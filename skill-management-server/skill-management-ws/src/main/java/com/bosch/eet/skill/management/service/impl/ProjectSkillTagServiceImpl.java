package com.bosch.eet.skill.management.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.converter.utils.SkillTagConverterUtil;
import com.bosch.eet.skill.management.dto.SkillTagDto;
import com.bosch.eet.skill.management.entity.SkillTag;
import com.bosch.eet.skill.management.repo.ProjectSkillTagRepository;
import com.bosch.eet.skill.management.service.ProjectSkillTagService;

@Service
public class ProjectSkillTagServiceImpl implements ProjectSkillTagService {

	@Autowired
	private ProjectSkillTagRepository projectSkillTagRepository;
	
	@Override
	public List<SkillTagDto> findSkillTagsByProjectsId(String projectId) {
		List<SkillTag> skillTags = projectSkillTagRepository.findSkillTagsByProjectsId(projectId);
		return skillTags.stream().map(SkillTagConverterUtil::convertToSimpleDTO).collect(Collectors.toList());
	}

}
