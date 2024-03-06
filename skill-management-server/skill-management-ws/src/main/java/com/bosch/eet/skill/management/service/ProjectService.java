package com.bosch.eet.skill.management.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.dto.CustomerGbDto;
import com.bosch.eet.skill.management.dto.FileStorageDTO;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.ProjectMemberDto;
import com.bosch.eet.skill.management.dto.ProjectTypeDto;
import com.bosch.eet.skill.management.dto.SimplePersonalProjectDto;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectSkillTag;
import com.bosch.eet.skill.management.entity.SkillTag;

public interface ProjectService {
    Page<ProjectDto> findAll(Pageable pageable, Map<String,String> q);

    List<ProjectTypeDto> findAll();

    List<ProjectDto> findAllForDropdown(Map<String, String> filterParams);

    ProjectDto findById(String projectId);

    ProjectDto save(ProjectDto projectDto);

    ProjectDto deleteProject(ProjectDto projectDto);
    
    HashMap<String, Object> getFilter();
    
    List<ProjectDto> importProject(String ntid,MultipartFile file, List<Project> savedProjectList);

    Project saveAndSyncElasticProject(Project project);

    List<CustomerGbDto> findAllByCustomerGb();
    
    void buildSkillTag(ProjectDto projectDto, Project saveProject, List<SkillTag> skillTagsModified,
                       List<ProjectSkillTag> projectSkillTags);
    
	String updateAdditionalTask(String ntid, SimplePersonalProjectDto simplePersonalProjectDto, Set<String> auth);

    void validateProjectMemberList(List<ProjectMemberDto> memberSet, Date projectStartDate, Date projectEndDate);
    
    FileStorageDTO updateInfoPortfolio(String id, MultipartFile file, String layout);
    
    String getInforPortfolio(String projectId, String layoutValue);

    ProjectDto getPortfolio(String projectId);

    Project editPortfolio(ProjectDto projectDto);
}
