package com.bosch.eet.skill.management.service;

import java.util.List;

import com.bosch.eet.skill.management.dto.ProjectScopeDto;

public interface ProjectScopeService {
	public List<ProjectScopeDto> findAll();

	public ProjectScopeDto findById(String projectScopeId);

	public ProjectScopeDto createNewProjectScope(ProjectScopeDto projectScopeDto);

	public ProjectScopeDto updateProjectScope(ProjectScopeDto projectScopeDto);

	public void deleteProjectScope(String projectScopeId);

}
