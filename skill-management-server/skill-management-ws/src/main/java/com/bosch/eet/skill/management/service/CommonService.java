package com.bosch.eet.skill.management.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.dto.CommonTaskDto;
import com.bosch.eet.skill.management.dto.DepartmentGroupDto;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.ProjectRoleDto;
import com.bosch.eet.skill.management.dto.SkillCompetencyLeadDto;
import com.bosch.eet.skill.management.dto.TeamDto;
import com.bosch.eet.skill.management.dto.VModelDto;
import com.bosch.eet.skill.management.ldap.exception.LdapException;

public interface CommonService {

//    List<String> findAllProjectRole();

    List<String> findAllGB();

	Page<CommonTaskDto> findByProjectRoleId(String Id, Pageable pageable);

    String addPersonalInfosFromExcel(MultipartFile file, String createdBy, String departmentName);
    void addPersonalSkillInfosFromExcel(MultipartFile file, String modifiedBy, String personalId);

    boolean uploadSkill(MultipartFile file);

	Page<ProjectRoleDto> findAllRoles(Pageable pageable, Map<String, String> q);

	List<CommonTaskDto> findAllCommonTask();

	List<ProjectRoleDto> deleteProjectRole(List<ProjectRoleDto> projectRoleDtos);

	List<CommonTaskDto> deleteCommonTask(List<CommonTaskDto> commonTaskDtos);

	ProjectRoleDto addNewProjectRole(ProjectRoleDto projectRoleDto);

	CommonTaskDto addNewCommonTask(CommonTaskDto commonTaskDto);

	List<PersonalDto> findAllPersonalManagerRole();
	
	List<TeamDto> findAllTeam();
	
	void addSkillCluster(MultipartFile file, String createdBy, String personalId);
	
	List<DepartmentGroupDto> findAllDepartmentGroup();

	DepartmentGroupDto findByTeamId(String id);
	
	void sendMailRequestPending();
	
	PersonalDto findLineManager(String idPersonal);
	
	VModelDto getVModel();
	
	String updateEmail() throws LdapException;

	String updatePersonalSkillGroup();

	List<SkillCompetencyLeadDto> findCompetencyLeadByRequest(String requestId);
}
