package com.bosch.eet.skill.management.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bosch.eet.skill.management.dto.AddSkillDto;
import com.bosch.eet.skill.management.dto.CourseMemberDto;
import com.bosch.eet.skill.management.dto.PersonalCourseDto;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.PersonalProjectDto;
import com.bosch.eet.skill.management.dto.PersonalSkillEvaluationDto;
import com.bosch.eet.skill.management.dto.UpdateDto;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.ldap.exception.LdapException;
import com.bosch.eet.skill.management.usermanagement.entity.User;

public interface PersonalService {
	Page<PersonalDto> findAll(Pageable pageable, Map<String, String> q);

	HashMap<String, Object> getFilter();

	PersonalDto findById(String personalId, String ntid, Set<String> authSet);
	Personal findEntityById(String personalId);

	// @BeanMapping(nullValuePropertyMappingStrategy =
	// NullValuePropertyMappingStrategy.IGNORE)
	PersonalDto saveSkill(PersonalDto personalDto);

	PersonalDto saveImage(String id, byte[] personalPicture);

	Page<PersonalProjectDto> findProjectsByPersonalId(String personalId, Pageable pageable);

	Page<PersonalProjectDto> findProjectsDetailByPersonalId(String personalId, Pageable pageable);

	PersonalProjectDto findPersonalProjectById(String personalId, String personalProjectId);

	PersonalDto save(PersonalDto personalDto);

	PersonalProjectDto addPersonalProject(String personalId, PersonalProjectDto personalProjectDto);

	void deletePersonalProject(PersonalProjectDto personalProjectDto, Set<String> authorities, String authNTID);
	
	Personal savePersonalWithUser(PersonalDto personalDto);

    Personal addSkillsToPersonalWhenUserIsActivated(User user);

	Page<PersonalCourseDto> findCoursesByPersonalId(String personalI, Pageable pageable);

	PersonalCourseDto findPersonalCourseById(String personalId, String personalCourseId);

	String exportData(String personalId, List<PersonalProjectDto> projects, List<PersonalCourseDto> courses);

	PersonalCourseDto uploadCertificate(String personalCourseId, byte[] certificate);
	
	PersonalCourseDto addCourseByTrainingCourse(String personalId, String trainingCourseId);

	PersonalCourseDto assignCourseForPersonal(String trainingCourseId, CourseMemberDto courseMemberDto);

	PersonalCourseDto updateCourse(UpdateDto updateDto, String personalCourseId);

	PersonalCourseDto deleteCertificate(String personalCourseId);

    PersonalSkillEvaluationDto findSkillsByPersonalId(String personalId);
    
    PersonalSkillEvaluationDto findSkillsByPersonalIdV2(String personalId);

	PersonalDto addCoursesByListOfTrainingCourse(String personalId, List<String> trainingCourseId);

	void addNewAssociate(AddSkillDto addSkillDto) throws LdapException;

	PersonalProjectDto updatePersonalProject(String personalProjectId, PersonalProjectDto personalProjectDto);

	Personal addSkillToPersonal(Personal personal);
	
	void editAssociateInfo(AddSkillDto addSkillDto);

	PersonalDto findByIdForEdit(String personalId);

	boolean checkIsExisted(String associateNTId);

	Personal addRawPersonalWithoutPermission(Personal personal);
	
	void generateAssociateListExcel(HttpServletResponse response, List<String> personalStringList) throws IOException;
	
	List<PersonalDto> findAssociateNotEvaluate(String personalId);
	
	void deleteAssociate(String personalId);
}
