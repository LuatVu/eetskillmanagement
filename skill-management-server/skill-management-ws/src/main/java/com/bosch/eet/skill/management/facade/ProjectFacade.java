package com.bosch.eet.skill.management.facade;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.dto.ProjectDto;

public interface ProjectFacade {
	ProjectDto save(ProjectDto projectDto);
	
	List<ProjectDto> importProject(String ntid,MultipartFile file);
	
    boolean deleteProject(ProjectDto projectDto);

	public ProjectDto editProjectPortfolio(ProjectDto projectDto);

}
