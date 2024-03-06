package com.bosch.eet.skill.management.service;

import java.util.List;

import com.bosch.eet.skill.management.dto.SkillTagDto;

public interface ProjectSkillTagService {

	List<SkillTagDto> findSkillTagsByProjectsId(String projectId);
}
