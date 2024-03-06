package com.bosch.eet.skill.management.service.impl;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.Status;
import com.bosch.eet.skill.management.common.UserType;
import com.bosch.eet.skill.management.converter.PersonalConverter;
import com.bosch.eet.skill.management.converter.PersonalProjectConverter;
import com.bosch.eet.skill.management.converter.SkillConverter;
import com.bosch.eet.skill.management.converter.utils.CommonTaskConverterUtil;
import com.bosch.eet.skill.management.converter.utils.PersonalCourseConverterUtil;
import com.bosch.eet.skill.management.dto.AddSkillDto;
import com.bosch.eet.skill.management.dto.CommonTaskDto;
import com.bosch.eet.skill.management.dto.CourseDto;
import com.bosch.eet.skill.management.dto.CourseMemberDto;
import com.bosch.eet.skill.management.dto.PersonalCourseDto;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.PersonalProjectDto;
import com.bosch.eet.skill.management.dto.PersonalSkillEvaluationDto;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.ProjectMemberDto;
import com.bosch.eet.skill.management.dto.SkillDescriptionDTO;
import com.bosch.eet.skill.management.dto.SkillDto;
import com.bosch.eet.skill.management.dto.SkillLevelDTO;
import com.bosch.eet.skill.management.dto.UpdateDto;
import com.bosch.eet.skill.management.elasticsearch.document.PersonalDocument;
import com.bosch.eet.skill.management.elasticsearch.repo.PersonalElasticRepository;
import com.bosch.eet.skill.management.elasticsearch.repo.PersonalSpringElasticRepository;
import com.bosch.eet.skill.management.entity.CommonTask;
import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalCourse;
import com.bosch.eet.skill.management.entity.PersonalProject;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.PersonalSkillGroup;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectRole;
import com.bosch.eet.skill.management.entity.ProjectSkillTag;
import com.bosch.eet.skill.management.entity.ProjectType;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.entity.SkillLevel;
import com.bosch.eet.skill.management.entity.SkillTag;
import com.bosch.eet.skill.management.entity.Team;
import com.bosch.eet.skill.management.entity.TrainingCourse;
import com.bosch.eet.skill.management.exception.BadRequestException;
import com.bosch.eet.skill.management.exception.EETResponseException;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.util.Utility;
import com.bosch.eet.skill.management.ldap.exception.LdapException;
import com.bosch.eet.skill.management.ldap.model.LdapInfo;
import com.bosch.eet.skill.management.ldap.service.LdapService;
import com.bosch.eet.skill.management.mail.EmailService;
import com.bosch.eet.skill.management.repo.CommonTaskRepository;
import com.bosch.eet.skill.management.repo.LevelRepository;
import com.bosch.eet.skill.management.repo.PersonalCourseRepository;
import com.bosch.eet.skill.management.repo.PersonalProjectRepository;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.PersonalSkillGroupRepository;
import com.bosch.eet.skill.management.repo.PersonalSkillRepository;
import com.bosch.eet.skill.management.repo.ProjectRepository;
import com.bosch.eet.skill.management.repo.ProjectRoleRepository;
import com.bosch.eet.skill.management.repo.ProjectSkillTagRepository;
import com.bosch.eet.skill.management.repo.ProjectTypeRepository;
import com.bosch.eet.skill.management.repo.SkillExperienceLevelRepository;
import com.bosch.eet.skill.management.repo.SkillGroupRepository;
import com.bosch.eet.skill.management.repo.SkillLevelRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;
import com.bosch.eet.skill.management.repo.SkillTagRepository;
import com.bosch.eet.skill.management.repo.TeamRepository;
import com.bosch.eet.skill.management.repo.TrainingCourseRepository;
import com.bosch.eet.skill.management.service.PersonalService;
import com.bosch.eet.skill.management.service.ProjectService;
import com.bosch.eet.skill.management.specification.PersonalCourseSpecification;
import com.bosch.eet.skill.management.specification.PersonalProjectSpecification;
import com.bosch.eet.skill.management.specification.PersonalSpecification;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Group;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.entity.UserGroup;
import com.bosch.eet.skill.management.usermanagement.repo.GroupRepository;
import com.bosch.eet.skill.management.usermanagement.repo.UserGroupRepository;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;
import com.bosch.eet.skill.management.usermanagement.service.UserService;
import com.itextpdf.html2pdf.HtmlConverter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor

public class PersonalServiceImpl implements PersonalService {

    @Value("${server.port}")
    private String port;
    
    @Value("${level.path}")
    private String levelPath;
    
    private static final String DEFAULT_PERSONAL_LEVEL = "f1f7a51a-5b92-414e-afd1-0bf59a827077";
    
    private static final String DEFAULT_PERSONAL_TITLE = "Developer";

    private final PersonalSkillRepository personalSkillRepository;

    private final PersonalCourseRepository personalCourseRepository;

    private final ProjectRepository projectRepository;

    private final PersonalProjectRepository personalProjectRepository;

    private final ProjectRoleRepository projectRoleRepository;

    private final CommonTaskRepository commonTaskRepository;

    private final PersonalRepository personalRepository;

    private final SkillRepository skillRepository;

    private final ProjectTypeRepository projectTypeRepository;

    private final SkillGroupRepository skillGroupRepository;
    
    private final GroupRepository groupRepository;
    
    private final SkillLevelRepository skillLevelRepository;

    private final PersonalConverter personalConverter;

    private final PersonalProjectConverter personalProjectConverter;
    
    private final CommonTaskConverterUtil commonTaskConverter;

    private final LevelRepository levelRepository;

    private final TeamRepository teamRepository;

    private final MessageSource messageSource;

    private final TrainingCourseRepository trainingCourseRepository;

    private final SkillExperienceLevelRepository skillExperienceLevelRepository;
    
    private final UserGroupRepository userGroupRepository;
        
    private final UserService userService;
    
    private final LdapService ldapService;
    
    private final SkillConverter skillConverter;
    
    private final PersonalElasticRepository personalElasticRepository;
    
    private final UserRepository userRepository;
    
    private final ProjectService projectService;
    
    private final SkillTagRepository skillTagRepository;
    
    private final ProjectSkillTagRepository projectSkillTagRepository;

    private final PersonalSkillGroupRepository personalSkillGroupRepository;

    private final PersonalSpringElasticRepository personalSpringElasticRepository;

    private final String[] excelHeaderTitleArray = {
    		"Associate Name",
    		"NTID",
    		"Department",
			"Group",
			"Team",
			"Gender",
			"Email",
			"Level",
			"Title",
			"Line Manager",
			"Bosch Exp",
			"Non Bosch Exp",
			"Total Exp",
			"Main Skill Cluster",
			"Skills",
			"Joined Date",
			"Location",
			"Brief Information",
			"Skill Highlight"
        };
    
    @Autowired
    private EmailService emailService;
    
    
    @Override
    public Page<PersonalDto> findAll(Pageable pageable, Map<String, String> filterParams) {
        Specification<Personal> specification = PersonalSpecification.search(filterParams);
        Page<PersonalDto> personalDtos;
        List<PersonalDto> mainList;
        String exp = filterParams.get("exp");
        if (StringUtils.isNotBlank(exp)) {
            Page<Personal> personals = personalRepository.findAll(specification, Pageable.unpaged());
            log.info("personalRepository.findAll: {}", personals.getTotalElements());
            personalDtos = personals.map(personalConverter::convertToSearchDTO);
            mainList = new ArrayList<>();
            String[] ranges = exp.split("-");
            if (ranges.length < 2) {
            	String experience = ranges[0];
            	int expInt = Integer.parseInt(experience);
            	mainList = personalDtos.getContent().stream().filter(x -> {
            		return Integer.parseInt(x.getTotalExperienced()) > expInt;
            	}).collect(Collectors.toList());
            } else {
                String start = ranges[0];
                String end = ranges[1];
                if (StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)) {
                    int startInt = Integer.parseInt(start);
                    int endInt = Integer.parseInt(end);
                	mainList = personalDtos.getContent().stream().filter(x -> {
                		return Integer.parseInt(x.getTotalExperienced()) >= startInt 
                				&& Integer.parseInt(x.getTotalExperienced()) <= endInt;
                	}).collect(Collectors.toList());

                }
            }

        } else {
            Page<Personal> personals = personalRepository.findAll(specification, pageable);
            log.info("personalRepository.findAll: {}", personals.getTotalElements());
           personalDtos = personals.map(personalConverter::convertToSearchDTO);
           return personalDtos;
        }
        return new PageImpl<>(mainList, pageable, personalDtos.getTotalElements());
    }

    @Override
    public HashMap<String, Object> getFilter() {
        List<String> collectTeam = teamRepository.findByOrderByName().stream().map(Team::getName).collect(Collectors.toList());
        List<Level> levelList = levelRepository.findByOrderByName();
        List<Skill> skillNameList = skillRepository.findNameList();
        List<String> collectSkill = skillNameList.stream().map(Skill::getName).collect(Collectors.toList());
        List<SkillGroup> skillGroupNameList = skillGroupRepository.findNameByTypeList(Constants.TECHNICAL_SKILL_GROUP);
        List<String> collectSkillGroup = skillGroupNameList.stream().map(SkillGroup::getName).collect(Collectors.toList());
        List<String> collectLevel = levelList.stream().map(Level::getName).collect(Collectors.toList());
        HashMap<String, Object> result = new HashMap<>();
        result.put("team_filter", collectTeam);
        result.put("skill_filter", collectSkill);
        result.put("skill_group_filter", collectSkillGroup);
        result.put("level_filter", collectLevel);
        return result;
    }

    @Override
    public PersonalDto findById(String personalId, String ntid, Set<String> authSet) {
        Optional<Personal> personalEntity = personalRepository.findById(personalId);
        if (personalEntity.isPresent()) {
            Personal personal = personalEntity.get();
			if (!personal.getPersonalCode().equals(ntid)
					&& !authSet.contains(Constants.VIEW_ASSOCIATE_INFO_PERMISSION)) {
				throw new AccessDeniedException(messageSource.getMessage(MessageCode.ACCESS_DENIED.toString(), null,
						LocaleContextHolder.getLocale()));
			}
            StopWatch watch = new StopWatch();
            watch.start();
            PersonalDto result = personalConverter.convertToDTOV2(personal);
            watch.stop();
            log.info("Personal converter " + watch.getTotalTimeMillis() + "ms ~= " + watch.getTotalTimeSeconds() + "s ~= ");
            return result;
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Unable to find resource", null);
        }
    }

    @Override
    public Personal findEntityById(String personalId) {
        return personalRepository.findById(personalId).orElse(null);
    }

    @Override
    public PersonalDto findByIdForEdit(String personalId) {
    	Optional<Personal> personalEntity = personalRepository.findById(personalId);
    	if (personalEntity.isPresent()) {
    		Personal personal = personalEntity.get();
    		StopWatch watch = new StopWatch();
    		watch.start();
    		PersonalDto result = personalConverter.convertToAddSkillDto(personal);
    		watch.stop();
    		log.info("Personal converter " + watch.getTotalTimeMillis() + "ms ~= " + watch.getTotalTimeSeconds() + "s ~= ");
    		return result;
    	} else {
    		throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Unable to find resource", null);
    	}
    }

    @Override
    public Personal addRawPersonalWithoutPermission(Personal personal) {
        personal = personalRepository.save(personal);
        personalElasticRepository.updatePersonal(personal);
        return personal;
    }


    /**
     * @author DUP5HC
     */

//    Save Associate Avatar
    @Transactional
    @Override
    public PersonalDto saveImage(String id, byte[] personalPicture) {
        Optional<Personal> personalEntity = personalRepository.findById(id);
        if (personalEntity.isPresent()) {
            Personal personal = personalEntity.get();
            String avatar = Base64.getEncoder().encodeToString(personalPicture);
            personal.setPicture(avatar);
            return personalConverter.convertToDTO(personal);
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Unable to find Associate!!", null);
        }
    }

    /*
     * @author TAY3HC
     */

    @Override
    @Transactional
    public PersonalDto saveSkill(PersonalDto personalDto) {

        // Get personal from repository
        Optional<Personal> personalOptional = personalRepository.findById(personalDto.getId());
        if (personalOptional.isPresent()) {
            Personal personal = personalOptional.get();
            // Get personal skill from personal
            List<PersonalSkill> personalSkills = new ArrayList<>(personal.getPersonalSkills());
            // Use repository to delete skill from the personal skill above
            if (!personalSkills.isEmpty()) {
                for (int i = 0; i < personalSkills.size(); i++) {
                    PersonalSkill personalSkill = personalSkills.get(i);
                    personalSkillRepository.deleteById(personalSkill.getId());
                }
            }
            // Create the personal skill by skill in the DTO
            List<SkillDto> skillDtos = personalDto.getSkills();
            if (!skillDtos.isEmpty()) {
                for (int i = 0; i < skillDtos.size(); i++) {
                    SkillDto skillDto = skillDtos.get(i);
                    savePersonalSkill(personal, skillDto);
                }
            } else {
                throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Cannot find the skill!", null);
            }
        }
        return personalDto;
    }

    public void savePersonalSkill(Personal personal, SkillDto skillDto) {
        PersonalSkill personalSkill = new PersonalSkill();
        personalSkill.setLevel(Utility.validateSkillLevel(skillDto.getLevel()));
        personalSkill.setExperience(skillDto.getExperienceNumber());
        // Set skill
        Optional<Skill> skillEntity = skillRepository.findById(skillDto.getId());
        if (skillEntity.isPresent()) {
            personalSkill.setSkill(skillEntity.get());
        }
        personalSkill.setPersonal(personal);
        personalSkillRepository.save(personalSkill);
    }

    private void saveCourse(PersonalDto personalDto, @NonNull Personal personal) {
        if (CollectionUtils.isEmpty(personalDto.getCourses())) { // Do not have course to update
            return;
        }

        // Delete courses from the personal courses
        if (!CollectionUtils.isEmpty(personal.getPersonalCourse())) {
            personalCourseRepository.deleteAllById(personal.getPersonalCourse().stream().map(PersonalCourse::getId).collect(Collectors.toList()));
        }

        // Create the personal courses by courses in the DTO
        savePersonalCourses(personal, personalDto.getCourses());
    }

    public void savePersonalCourses(Personal personal, List<CourseDto> courseDtos) {
        if (CollectionUtils.isEmpty(courseDtos)) {
            return;
        }

        List<PersonalCourse> personalCourses = new ArrayList<>();
        for (CourseDto courseDto: courseDtos) {
            PersonalCourse personalCourse = new PersonalCourse();

            try {
                personalCourse.setStartDate(Constants.SIMPLE_DATE_FORMAT.parse(courseDto.getStartDate()));
            } catch (ParseException e) {
                e.printStackTrace();
                throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Wrong date format", null);
            }

            try {
                personalCourse.setEndDate(Constants.SIMPLE_DATE_FORMAT.parse(courseDto.getEndDate()));
            } catch (ParseException e) {
                e.printStackTrace();
                throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Wrong date format", null);
            }

            personalCourse.setStatus(courseDto.getStatus());
            // Set course
            Optional<TrainingCourse> trainingCourseEntity = trainingCourseRepository.findById(courseDto.getId());
            trainingCourseEntity.ifPresent(personalCourse::setCourse);
            personalCourse.setPersonal(personal);
            personalCourses.add(personalCourse);
        }

        personalCourseRepository.saveAll(personalCourses);
    }


    @Override
    public Page<PersonalProjectDto> findProjectsByPersonalId(String personalId, Pageable pageable) {
        HashMap<String, String> hmSearch = new HashMap<>();
        hmSearch.put("personal_id", personalId);
        Specification<PersonalProject> specification = PersonalProjectSpecification.search(hmSearch);
        Page<PersonalProject> personalProjects = personalProjectRepository.findAll(specification, pageable);
        return personalProjects.map(personalProjectConverter::convertToDetailDTO);
    }

    @Override
    public Page<PersonalProjectDto> findProjectsDetailByPersonalId(String personalId, Pageable pageable) {
        HashMap<String, String> hmSearch = new HashMap<>();
        hmSearch.put("personal_id", personalId);
        Specification<PersonalProject> specification = PersonalProjectSpecification.search(hmSearch);
        Page<PersonalProject> personalProjects = personalProjectRepository.findAll(specification, pageable);
        List<PersonalProjectDto> personalProjectDtos = new ArrayList<>();
        for (PersonalProject personalProject : personalProjects.getContent()) {
            PersonalProjectDto personalProjectDto = setCommonTask(personalProject);
            personalProjectDtos.add(personalProjectDto);
        }
        return new PageImpl<>(personalProjectDtos, pageable, personalProjectDtos.size());
    }

    @Override
    public PersonalProjectDto findPersonalProjectById(String personalId, String personalProjectId) {
        HashMap<String, String> hmSearch = new HashMap<>();
        hmSearch.put("personal_id", personalId);
        hmSearch.put("personal_project_id", personalProjectId);
        Specification<PersonalProject> specification = PersonalProjectSpecification.search(hmSearch);
        Page<PersonalProject> personalProjects = personalProjectRepository.findAll(specification, Pageable.unpaged());
        if (!personalProjects.getContent().isEmpty()) {
            PersonalProject personalProject = personalProjects.getContent().get(0);
            return setCommonTask(personalProject);
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Cannot find the project", null);
        }

    }

    public PersonalProjectDto savePersonalProjectById(PersonalProjectDto personalProjectDto) {
        Optional<PersonalProject> personalProjectOpt = personalProjectRepository.findById(personalProjectDto.getId());
        if (personalProjectOpt.isPresent()) {
            PersonalProject personalProject = personalProjectOpt.get();
            Optional<ProjectRole> projectRoleEntity = projectRoleRepository.findById(personalProjectDto.getRoleId());
            projectRoleEntity.ifPresent(personalProject::setProjectRole);
            personalProjectConverter.mapDataToEntity(personalProjectDto, personalProject);
            return personalProjectConverter.convertToDetailDTO(personalProjectRepository.save(personalProject));
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Cannot find the project", null);
        }
    }

    @Override
    @Transactional
    public PersonalDto save(PersonalDto personalDto) {
    	// Validate 
    	if (StringUtils.isBlank(personalDto.getId())) { // Missing id
    		throw new SkillManagementException(messageSource.getMessage(
	                 MessageCode.INVALID_ID.toString(),
	                 null,
	                 LocaleContextHolder.getLocale()),
	         MessageCode.INVALID_ID.toString(),
	         null,
	         HttpStatus.BAD_REQUEST);
    	}

        Personal personal = personalRepository.findById(personalDto.getId()).orElse(null);
        if (personal == null) {
            throw new SkillManagementException(messageSource.getMessage(
                    MessageCode.SKM_USER_NOT_FOUND.toString(),
                    null,
                    LocaleContextHolder.getLocale()),
                    MessageCode.SKM_USER_NOT_FOUND.toString(),
                    null,
                    NOT_FOUND);
        }

        if(personalDto.getTeamId() != null && teamRepository.existsById(personalDto.getTeamId())) {
            personal.setTeam(teamRepository.getById(personalDto.getTeamId()));
        }
        if(personalDto.getManager() != null && userRepository.existsById(personalDto.getManager())) {
            personal.setManager(userRepository.getById(personalDto.getManager()));
        }

        personal.setUpdated(false);
    	
    	if (!CollectionUtils.isEmpty(personalDto.getCourses())) {
            saveCourse(personalDto, personal);
    	}

        if (StringUtils.isNotBlank(personalDto.getExperiencedNonBosch())) {
        	personal.setExperiencedNonBosch(personalDto.getExperiencedNonBosch());
        } else {
            personal.setExperiencedNonBosch("0");
        }
        if (StringUtils.isNotBlank(personalDto.getBriefInfo())) {
        	personal.setBriefInfo(personalDto.getBriefInfo());
        } else {
            personal.setBriefInfo("");
        }

        personalRepository.save(personal);
        personalElasticRepository.updatePersonal(personal);
        return personalDto;
    }

    // Add non-Bosch project - assign Bosch project
    @Override
    @Transactional
    public PersonalProjectDto addPersonalProject(String personalId, PersonalProjectDto personalProjectDto) {
        PersonalProject personalProject = new PersonalProject();
        // Find personal
        Optional<Personal> personalOpt = personalRepository.findById(personalId);
        try {
            personalProject.setPersonal(personalOpt.get());
        } catch (Exception e) {
            e.printStackTrace();
            throw new EETResponseException(String.valueOf(BAD_REQUEST.value()), "This person doesn't exist!", null);
        }

        Optional<ProjectRole> projectRoleOpt = projectRoleRepository
                .findById(personalProjectDto.getRoleId());
        if (projectRoleOpt.isPresent()) {
            personalProject.setProjectRole(projectRoleOpt.get());
        } else {
            throw new EETResponseException(String.valueOf(BAD_REQUEST.value()), "This role doesn't exist!", null);
        }

        Project modelSave;
        if (StringUtils.isNotBlank(personalProjectDto.getProjectId())) {
            Optional<Project> projectOptional = projectRepository.findById(personalProjectDto.getProjectId());
            if (projectOptional.isPresent()) {
                modelSave = projectOptional.get();
            } else {
                throw new EETResponseException(String.valueOf(BAD_REQUEST.value()),
                        "Project doesn't exist!", null);
            }

            if(StringUtils.isNotBlank(personalProjectDto.getMemberStartDate())){
                personalProject.setStartDate(Utility.parseSimpleDateFormat(personalProjectDto.getMemberStartDate()));
            } else {
                throw new EETResponseException(String.valueOf(BAD_REQUEST.value()),
                        "Member start date can't be null!", null);
            }

            if(StringUtils.isNotBlank(personalProjectDto.getMemberEndDate())){
                personalProject.setEndDate(Utility.parseSimpleDateFormat(personalProjectDto.getMemberEndDate()));
            }

        	List<PersonalProject> personalProjects = personalProjectRepository.findByPersonalIdAndProjectId(personalId, personalProjectDto.getProjectId());
        	if (!personalProjects.isEmpty()) {
                List<ProjectMemberDto> memberDtoList = personalProjects.stream()
                        .map(personalProjectConverter::convertPersonalProjectToProjectMemberDto)
                        .filter(item -> item.getStartDateDate() != null)
                        .collect(Collectors.toList());

                memberDtoList.add(ProjectMemberDto.builder().startDate(personalProjectDto.getMemberStartDate())
                        .endDate(personalProjectDto.getMemberEndDate()).id(personalId).build());

                projectService.validateProjectMemberList(memberDtoList, modelSave.getStartDate(), modelSave.getEndDate());
            }
        } else {
        	Project existProject = projectRepository.findByName(personalProjectDto.getName()).orElse(null);
            if (existProject != null) {
                throw new BadRequestException(messageSource.getMessage(MessageCode.PROJECT_NAME_EXIST.toString(), null,
                        LocaleContextHolder.getLocale()));
            }
        	
            Project model = Project.builder()
                    .name(personalProjectDto.getName())
                    .challenge(personalProjectDto.getChallenge())
                    .leader(personalProjectDto.getPmName())
                    .targetObject(personalProjectDto.getObjective())
                    .createdBy(personalProjectDto.getCreatedBy())
                    .status(personalProjectDto.getStatus())
                    .description(personalProjectDto.getDescription())
                    .technologyUsed(personalProjectDto.getTechnologyUsed())
                    .teamSize(personalProjectDto.getTeamSize())
                    .build();

            // Project type can be null if not found
            Optional<ProjectType> projectTypeOpt = projectTypeRepository
                    .findByName(Constants.NONBOSCH);
            if (projectTypeOpt.isPresent()) {
                model.setProjectType(projectTypeOpt.get());
            } else {
                throw new EETResponseException(String.valueOf(BAD_REQUEST.value()),
                        "This project type doesn't exist!", null);
            }
            
            Date startDate;
            Date endDate;

            if (StringUtils.isNotBlank(personalProjectDto.getStartDate())) {
                startDate = Utility.parseSimpleDateFormat(personalProjectDto.getStartDate());
                model.setStartDate(startDate);
            } else {
                throw new EETResponseException(String.valueOf(BAD_REQUEST.value()),
                        "Start date can't be null!", null);
            }
            
            if (StringUtils.isNotBlank(personalProjectDto.getEndDate())) {
                endDate = Utility.parseSimpleDateFormat(personalProjectDto.getEndDate());
                model.setEndDate(endDate);
                
                // Throw the exception if the startDate is after endDate or on same day
                if(startDate.after(endDate) || Constants.SIMPLE_DATE_FORMAT.format(startDate).equals(Constants.SIMPLE_DATE_FORMAT.format(endDate))){
                    throw new EETResponseException(String.valueOf(BAD_REQUEST.value()),
                            "End date must after start date", null);
                }
            }
            
            modelSave = projectRepository.save(model);
            
            // Project skill tag
            List<SkillTag> skillTagsModified = new ArrayList<>();
            List<ProjectSkillTag> projectSkillTags = new ArrayList<>();
            projectService.buildSkillTag(ProjectDto.builder().skillTags(personalProjectDto.getSkillTags()).build(),
                    modelSave, skillTagsModified, projectSkillTags);
            
            skillTagRepository.saveAll(skillTagsModified);
            projectSkillTags= projectSkillTagRepository.saveAll(projectSkillTags);
            modelSave.setProjectSkillTags(new HashSet<>(projectSkillTags));
        }
        personalProject.setProject(modelSave);

        // Additional task
        if (personalProjectDto.getAdditionalTasks() != null) {
            personalProject.setAdditionalTask(personalProjectDto.getAdditionalTasks());
        }

        // Create project
        PersonalProject savedpersonalProject = personalProjectRepository.save(personalProject);
        modelSave.setPersonalProject(new ArrayList<PersonalProject>(Arrays.asList(personalProject)));
        projectRepository.save(modelSave);
        
        return personalProjectConverter.convertToDetailDTO(savedpersonalProject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePersonalProject(PersonalProjectDto personalProjectDto, Set<String> authorities, String authNTID) {
        try {
            String projectType = personalProjectDto.getProjectType();
            
            // Bosch project (remove member from project)
            if(StringUtils.isNotBlank(projectType) && projectType.equalsIgnoreCase(Constants.BOSCH)){
                if(authorities.contains(Constants.EDIT_ASSOCIATE_INFO_PERMISSION)) {
                    personalProjectRepository.deleteById(personalProjectDto.getId());
                }else{
                    throw new SkillManagementException(MessageCode.NOT_AUTHORIZATION.toString(),
                            messageSource.getMessage(MessageCode.NOT_AUTHORIZATION.toString(), null,
                                    LocaleContextHolder.getLocale()), null, UNAUTHORIZED);
                }
            }
            
            // non-Bosch project
            else if (StringUtils.isNotBlank(projectType) && projectType.equalsIgnoreCase(Constants.NONBOSCH)) {
                
                //Check authenticate to check removing own project or have EDIT_ASSOCIATE_INFO_PERMISSION permission
                Optional<PersonalProject> personalProjectOpt = personalProjectRepository.findById(personalProjectDto.getId());
                String personalId = null;
                if(personalProjectOpt.isPresent()) {
					personalId = personalProjectOpt.get().getPersonal().getPersonalCode();
				}
                boolean isSamePerson = false;
                if(StringUtils.isNotBlank(personalId) && StringUtils.isNotBlank(authNTID)){
                    if(personalId.equalsIgnoreCase(authNTID)){
                        isSamePerson = true;
                    }
                }
                if(!isSamePerson && !authorities.contains(Constants.EDIT_ASSOCIATE_INFO_PERMISSION)){
                    throw new SkillManagementException(MessageCode.NOT_AUTHORIZATION.toString(),
                            messageSource.getMessage(MessageCode.NOT_AUTHORIZATION.toString(), null,
                                    LocaleContextHolder.getLocale()), null, UNAUTHORIZED);
                }
                personalProjectRepository.deleteById(personalProjectDto.getId());
                
                // also delete non-Bosch project
                String projectId = personalProjectDto.getProjectId();
                projectSkillTagRepository.deleteByProjectId(projectId);
                projectRepository.deleteById(projectId);
            } else {
                throw new SkillManagementException(MessageCode.NO_MATCHING_PROJECT_TYPE_FOUND.toString(),
                        messageSource.getMessage(MessageCode.NO_MATCHING_PROJECT_TYPE_FOUND.toString(), null,
                                LocaleContextHolder.getLocale()), null, BAD_REQUEST);
            }
        } catch(Exception e){
            if(e.getMessage().equals(messageSource.getMessage(MessageCode.NO_MATCHING_PROJECT_TYPE_FOUND.toString(),
                    null, LocaleContextHolder.getLocale())) ||
                    e.getMessage().equals(messageSource.getMessage(MessageCode.NOT_AUTHORIZATION.toString(),
                    null, LocaleContextHolder.getLocale()))){
                throw e;
            }else {
                log.error("Delete personal project fail: {}", e.getMessage());
                throw new SkillManagementException(MessageCode.ERROR_DELETING_PERSONAL_PROJECT.toString(),
                        messageSource.getMessage(MessageCode.ERROR_DELETING_PERSONAL_PROJECT.toString(), null,
                                LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
            }
        }
    }


    @Override
	@Transactional(rollbackFor = Exception.class)
    public Personal savePersonalWithUser(PersonalDto personalDto) {

        Level level = levelRepository.findLevelById(DEFAULT_PERSONAL_LEVEL).orElseThrow(
                () -> new SkillManagementException(
                        MessageCode.DEFAULT_LEVEL_NOT_INITIALIZED.toString(),
                        messageSource.getMessage(
                                MessageCode.DEFAULT_LEVEL_NOT_INITIALIZED.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        ),
                        null
                )
        );

        Personal personal = Personal.builder()
                .id(personalDto.getId())
                .personalCode(personalDto.getCode())
                .title(DEFAULT_PERSONAL_TITLE)
                .level(level)
                .updated(false)
                .build();

        return personalRepository.save(personal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Personal addSkillsToPersonalWhenUserIsActivated(User user) {

        String userId = user.getId();

        Optional<Personal> personalOpt = personalRepository.findById(userId);

        Personal personal;
        if (!personalOpt.isPresent()) {
            Level level = levelRepository.findLevelById(DEFAULT_PERSONAL_LEVEL).orElseThrow(
                    () -> new SkillManagementException(
                            MessageCode.DEFAULT_LEVEL_NOT_INITIALIZED.toString(),
                            messageSource.getMessage(
                                    MessageCode.DEFAULT_LEVEL_NOT_INITIALIZED.toString(),
                                    null,
                                    LocaleContextHolder.getLocale()
                            ),
                            null
                    )
            );
            personal = Personal.builder()
                    .id(userId)
                    .personalCode(user.getName())
                    .title(DEFAULT_PERSONAL_TITLE)
                    .level(level)
                    .updated(false)
                    .build();
        } else {
            personal = personalOpt.get();
        }

        if (personal.getLevel() == null) {
            Level level = levelRepository.findLevelById(DEFAULT_PERSONAL_LEVEL).orElseThrow(
                    () -> new SkillManagementException(
                            MessageCode.DEFAULT_LEVEL_NOT_INITIALIZED.toString(),
                            messageSource.getMessage(
                                    MessageCode.DEFAULT_LEVEL_NOT_INITIALIZED.toString(),
                                    null,
                                    LocaleContextHolder.getLocale()
                            ),
                            null
                    )
            );

            personal.setLevel(level);
        }

        return personal;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async
    public Personal addSkillToPersonal(Personal personal) {
    	List<Skill> allSkill = skillRepository.findAll();
    	for(Skill skill : allSkill) {
    		savePersonalSkill(personal, skillConverter.convertToPersonalSkillDto(skill));
    	}
    	return personal;
    }

    //  Get List Personal Course
    @Override
    public Page<PersonalCourseDto> findCoursesByPersonalId(String personalI, Pageable pageable) {
        HashMap<String, String> hmSearch = new HashMap<>();
        hmSearch.put("personal_id", personalI);
        Specification<PersonalCourse> specification = PersonalCourseSpecification.search(hmSearch);
        Page<PersonalCourse> personalCourses = personalCourseRepository.findAll(specification, pageable);
        return personalCourses.map(PersonalCourseConverterUtil::convertToSearchDTO);
    }

    //  Get Personal Course by CourseId
    @Override
    public PersonalCourseDto findPersonalCourseById(String personalId, String personalCourseId) {
        HashMap<String, String> hmSearch = new HashMap<>();
        hmSearch.put("personal_id", personalId);
        hmSearch.put("personal_course_id", personalCourseId);
        Specification<PersonalCourse> specification = PersonalCourseSpecification.search(hmSearch);
        Page<PersonalCourse> PersonalCourses = personalCourseRepository.findAll(specification, Pageable.unpaged());
        if (!PersonalCourses.getContent().isEmpty()) {
            PersonalCourse personalCourse = PersonalCourses.getContent().get(0);
            return PersonalCourseConverterUtil.convertToDetailDTO(personalCourse);
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Cannot find the course", null);
        }
    }

    //  Update Personal Course Dates and Status
    @Override
    @Transactional
    public PersonalCourseDto updateCourse(UpdateDto updateDto, String personalCourseId) {
        Optional<PersonalCourse> personalCourseEntity = personalCourseRepository.findById(personalCourseId);
        if (personalCourseEntity.isPresent()) {
            PersonalCourse personalCourse = personalCourseEntity.get();
            Date start;
            Date end;
            try {
                start = Constants.SIMPLE_DATE_FORMAT.parse(updateDto.getStartDate());
            } catch (ParseException e) {
                e.printStackTrace();
                throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Wrong date format", null);
            }
            try {
                end = Constants.SIMPLE_DATE_FORMAT.parse(updateDto.getEndDate());
            } catch (ParseException e) {
                e.printStackTrace();
                throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Wrong date format", null);
            }
            personalCourse.setStartDate(start);
            personalCourse.setEndDate(end);
            if (!Objects.isNull(updateDto.getStatus())) {
                personalCourse.setStatus(updateDto.getStatus());
            }
            return PersonalCourseConverterUtil.convertToDetailDTO(personalCourse);
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Course not found!!", null);
        }
    }

    //	Add Course by TrainingCourse id
    @Override
    @Transactional
    public PersonalCourseDto addCourseByTrainingCourse(String personalId, String trainingCourseId) {
        Optional<TrainingCourse> trainingOpt = trainingCourseRepository.findById(trainingCourseId);
        Optional<Personal> personOpt = personalRepository.findById(personalId);

        if (trainingOpt.isPresent() && personOpt.isPresent()) {
            TrainingCourse trainingCourse = trainingOpt.get();
            Personal person = personOpt.get();
            PersonalCourse newPersonalCourse = new PersonalCourse();
            newPersonalCourse.setCourse(trainingCourse);
            newPersonalCourse.setPersonal(person);
            newPersonalCourse.setCourseType(trainingCourse.getCourse().getCourseType());
            newPersonalCourse.setDuration(trainingCourse.getEffort());
            Date startDate = new Date();
            newPersonalCourse.setStartDate(startDate);
            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR, trainingCourse.getEffort());
            newPersonalCourse.setEndDate(c.getTime());
            newPersonalCourse.setTrainer(trainingCourse.getTrainer());
            newPersonalCourse.setStatus(trainingCourse.getStatus());
            newPersonalCourse.setCertificate(null);
            final PersonalCourse savedCourse = personalCourseRepository.save(newPersonalCourse);

            return PersonalCourseConverterUtil.convertToDetailDTO(savedCourse);
        } else {
            throw new SkillManagementException(MessageCode.CANNOT_FIND_COURSE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_FIND_COURSE.toString(),
                            null,
                            LocaleContextHolder.getLocale()),
                    null,
                    NOT_FOUND);
        }
    }
    
//	Assign Learning Course by List of Course Id
    @Override
    @Transactional
    public PersonalDto addCoursesByListOfTrainingCourse(String personalId, List<String> listTrainingCourseId) {
        Optional<Personal> personOpt = personalRepository.findById(personalId);
        if (personOpt.isPresent()) {
            Personal person = personOpt.get();
            List<PersonalCourse> listAssignedCourse = personalCourseRepository.findByPersonalId(personalId);

            for (String courseId : listTrainingCourseId) {
                boolean isRegistered = false;
                for(PersonalCourse assignedCourse: listAssignedCourse){
                    if (assignedCourse.getCourse().getId().equalsIgnoreCase(courseId)){
                        isRegistered = true;
                        log.info(messageSource.getMessage(com.bosch.eet.skill.management.usermanagement.consts.MessageCode.SKM_COURSE_ALREADY_EXIST.toString(), null,
                                LocaleContextHolder.getLocale()));
                        break;
                    }
                }
                if (!isRegistered) {
                    addCourseByTrainingCourse(personalId, courseId);
                }
            }
                return personalConverter.convertToSearchDTO(person);
        } else {
            throw new SkillManagementException(messageSource.getMessage(
                    MessageCode.PERSONAL_ID_NOT_EXIST.toString(),
                    null,
                    LocaleContextHolder.getLocale()),
            MessageCode.PERSONAL_ID_NOT_EXIST.toString(),
            null,
            NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public PersonalCourseDto assignCourseForPersonal(String trainingCourseId, CourseMemberDto courseMemberDto) {
        Optional<TrainingCourse> trainingCourseOpt = trainingCourseRepository
                .findById(trainingCourseId);
        Optional<Personal> personalOpt = personalRepository
                .findById(courseMemberDto.getId());

        if (trainingCourseOpt.isPresent() && personalOpt.isPresent()) {
            TrainingCourse trainingCourse = trainingCourseOpt.get();
            Personal personal = personalOpt.get();
            PersonalCourse personalCourse = new PersonalCourse();
            personalCourse.setCourse(trainingCourse);
            personalCourse.setPersonal(personal);
            personalCourse.setCourseType(trainingCourse.getCourse().getCourseType());
            personalCourse.setDuration(trainingCourse.getEffort());
            try {
                personalCourse.setStartDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(courseMemberDto.getStartDate()));
            } catch (ParseException e) {
                throw new SkillManagementException(MessageCode.INVALID_DATE.toString(),
                        messageSource.getMessage(MessageCode.INVALID_DATE.toString(), null,
                                LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
            }
            try {
                personalCourse.setEndDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(courseMemberDto.getEndDate()));
            } catch (ParseException e) {
                throw new SkillManagementException(MessageCode.INVALID_DATE.toString(),
                        messageSource.getMessage(MessageCode.INVALID_DATE.toString(), null,
                                LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
            }
            personalCourse.setTrainer(trainingCourse.getTrainer());
            personalCourse.setStatus(trainingCourse.getStatus());
            personalCourse.setCertificate(null);
            final PersonalCourse courseSave = personalCourseRepository.save(personalCourse);
            return PersonalCourseConverterUtil.convertToDetailDTO(courseSave);
        } else {
            throw new SkillManagementException(MessageCode.CANNOT_FIND_COURSE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_FIND_COURSE.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
        }
    }

    //	Upload Certificate for Personal Course
    @Override
    @Transactional
    public PersonalCourseDto uploadCertificate(String personalCourseId, byte[] certificate) {
        Optional<PersonalCourse> personalCourseEntity = personalCourseRepository.findById(personalCourseId);
        if (personalCourseEntity.isPresent()) {
            PersonalCourse personalCourse = personalCourseEntity.get();
            String cert = Base64.getEncoder().encodeToString(certificate);
            personalCourse.setCertificate(cert);
            return PersonalCourseConverterUtil.convertToDetailDTO(personalCourse);
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Unable to find Course!!", null);
        }
    }

    //	Delete Certificate for PersonalCourse
    @Override
    @Transactional
    public PersonalCourseDto deleteCertificate(String personalCourseId) {
        Optional<PersonalCourse> personalCourseEntity = personalCourseRepository.findById(personalCourseId);
        if (personalCourseEntity.isPresent()) {
            PersonalCourse personalCourse = personalCourseEntity.get();
            personalCourse.setCertificate(null);
            return PersonalCourseConverterUtil.convertToDetailDTO(personalCourse);
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Unable to find Course!!", null);
        }
    }

    @Override
    public String exportData(String personalId, List<PersonalProjectDto> projects, List<PersonalCourseDto> courses) {
        Optional<Personal> personalOptional = personalRepository.findById(personalId);
        if (!personalOptional.isPresent()) {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Cannot find personal", null);
        }
        PersonalDto personal = personalConverter.convertToPDFDTO(personalOptional.get());
        String fullName = personal.getName();
        String[] names = fullName.split(" ");
        String lastName = "";
        String firstName = "";
        if (names.length > 3) {
            lastName = String.valueOf(names[names.length - 2].toUpperCase().toCharArray()[0]);
        }
        if (names.length > 0) {
            firstName = String.valueOf(names[0].toUpperCase().toCharArray()[0]);
        }
        int boschExp = 0;
        try {
            boschExp = Integer.parseInt(personal.getExperiencedAtBosch());
        } catch (Exception e) {
            log.error("Err boschExp parse from String: {}", e.getMessage());
        }
        int nonBoschExp = 0;
        try {
            nonBoschExp = Integer.parseInt(personal.getExperiencedNonBosch());
        } catch (Exception e) {
            log.error("Err nonBoschExp parse from String: {}", e.getMessage());
        }
        int totalExp = 0;
        if (boschExp > 0) {
            totalExp += boschExp;
        }
        if (nonBoschExp > 0) {
            totalExp += nonBoschExp;
        }

        Context context = new Context();
        context.setVariable("personal", personal);
        context.setVariable("shortname", firstName + lastName);
        context.setVariable("avatar", personal.getPicture());
        List<String> circleColors = Arrays.asList("#cce2ef", "#f7cccd", "#d2eadd", "#feefd7", "#dcd3e5", "#f9d3ea");
        List<String> shortnameColors = Arrays.asList("#006ead", "#d50005", "#219557", "#fbb03b", "#50237f", "#e02595");
        Random ran = new Random();
        int x = ran.nextInt(6);
        context.setVariable("circleColor", circleColors.get(x));
        context.setVariable("shortnameColor", shortnameColors.get(x));
        context.setVariable("projects", projects);
        context.setVariable("courses", courses);
        if (boschExp > 0) {
            context.setVariable("boschExp", (boschExp == 1 ? boschExp + " year" : boschExp + " years"));
        } else {
            context.setVariable("boschExp", "0 years");
        }
        if (totalExp > 0) {
            context.setVariable("totalExp", (totalExp == 1 ? totalExp + " year" : totalExp + " years"));
        } else {
            context.setVariable("totalExp", "0 years");
        }
        context.setVariable("port", port);
        context.setVariable("levelPath", levelPath);
        context.setVariable("briefInfo", personal.getBriefInfo());
        String html = parseThymeleafTemplate("export-profile", context);
        String outputFolder = System.getProperty("user.home") + File.separator + "thymeleaf.pdf";
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(outputFolder);
        } catch (FileNotFoundException e) {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Cannot create PDF file", null);

        }
        HtmlConverter.convertToPdf(html, outputStream);

        return outputFolder;
    }


    private String parseThymeleafTemplate(String templateName, Context context) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine.process(templateName, context);
    }

    private PersonalProjectDto setCommonTask(PersonalProject personalProject) {
        PersonalProjectDto personalProjectDto = personalProjectConverter.convertToDetailDTO(personalProject);
        if (Objects.nonNull(personalProjectDto.getProjectType())
                && personalProjectDto.getProjectType().equals(Constants.BOSCH)) {
            if (Objects.nonNull(personalProject.getTask())) {
                List<CommonTask> commonTasks = commonTaskRepository.findByIdIn(
                        Arrays.asList(personalProject.getTask().split(",")));
                personalProjectDto.setTasks(commonTaskConverter.convertToDTOs(commonTasks));
            }

        } else {
            if (Objects.nonNull(personalProject.getTask())) {
                List<CommonTaskDto> commonTaskDtos = new ArrayList<>();
                String[] taskArr = personalProject.getTask().split(",");
                for (String task : taskArr) {
                    commonTaskDtos.add(CommonTaskDto
                            .builder()
                            .name(task)
                            .build());
                }
                personalProjectDto.setTasks(commonTaskDtos);
            }
        }
        return personalProjectDto;
    }

    @Override
    public PersonalSkillEvaluationDto findSkillsByPersonalId(String personalId) {
        Personal personal = personalRepository.findById(personalId).orElseThrow(
                () -> new EETResponseException(String.valueOf(NOT_FOUND.value()), "Associate not found", null)
        );
        return PersonalSkillEvaluationDto.builder()
        		.mainSkillCluster(personalConverter.getSkillGroupNames(personalConverter.getSkillGroupsFromPersonal(personal)))
        		.listSkill(personal.getPersonalSkills().stream().map(
                personalSkill -> {
                    Skill skill = personalSkill.getSkill();
                    List<SkillLevelDTO> skillLevelDTOS = skillExperienceLevelRepository.findDistinctBySkillId(skill.getId())
                            .stream().map(
                                    skillExperienceLevel -> SkillLevelDTO.builder()
                                            .description(skillExperienceLevel.getDescription())
                                            .level(skillExperienceLevel.getName())
                                            .build()
                            ).collect(Collectors.toList());
                    List<SkillLevelDTO> expectedSkillLevelDTOS = skillExperienceLevelRepository.findBySkillAndLevel(skill, personal.getLevel())
                            .stream().map(
                                    skillExperienceLevel -> SkillLevelDTO.builder()
                                            .description(skillExperienceLevel.getDescription())
                                            .level(skillExperienceLevel.getName())
                                            .build()
                            ).collect(Collectors.toList());
                    String expectedLevel = "";
                    if (!expectedSkillLevelDTOS.isEmpty()) {
                        String[] levelSplit = expectedSkillLevelDTOS.get(0).getLevel().split(" ");
                        if (levelSplit.length > 1) {
                            expectedLevel = levelSplit[1];
                        }
                    }
                    return SkillDescriptionDTO.builder()
                            .skillId(skill.getId())
                            .skillName(skill.getName())
                            .competency(skill.getSkillGroup().getName())
                            .level(Utility.floatToStringLevelFormat(personalSkill.getLevel()))
                            .expectedLevel(expectedLevel)
                            .experience(personalSkill.getExperience())
                            .skillLevelDTOS(skillLevelDTOS)
                            .build();
                }
        ).collect(Collectors.toList()))
        		.build();
    }

    @Override
    public PersonalSkillEvaluationDto findSkillsByPersonalIdV2(String personalId) {
        Personal personal = personalRepository.findById(personalId).orElseThrow(
                () -> new EETResponseException(String.valueOf(NOT_FOUND.value()),
                        MessageCode.SKM_USER_NOT_FOUND.toString(),
                        null)
        );
        List<SkillLevel> skillLevel = skillLevelRepository.findExpectedLevel(personalId, personal.getLevel().getId());
        System.out.println(skillLevel.size());

        HashMap<String, String> hmExpectedLevel = new HashMap<>();

        for (SkillLevel sl : skillLevel) {
            hmExpectedLevel.put(sl.getSkill().getId(), sl.getLevelLable());
        }

        return PersonalSkillEvaluationDto.builder()
                .mainSkillCluster(personalConverter.getSkillGroupNames(personalConverter.getSkillGroupsFromPersonal(personal)))
                .listSkill(mapAllSkillAndPersonalSkill(personal))
                .build();
    }

    private List<SkillDescriptionDTO> mapAllSkillAndPersonalSkill(Personal personal){
        List<SkillLevel> skillLevel = skillLevelRepository.findExpectedLevelByAssociateLevelId(personal.getLevel().getId());
        HashMap<String, String> hmExpectedLevel = new HashMap<>();
        for (SkillLevel sl : skillLevel) {
            hmExpectedLevel.put(sl.getSkill().getId(), sl.getLevelLable());
        }

        Set<PersonalSkill> personalSkills = personal.getPersonalSkills();
        Map<String, PersonalSkill> personalSkillsMapById = new HashMap<>();
        if (!CollectionUtils.isEmpty(personalSkills)) {
            personalSkillsMapById = personalSkills.stream()
                    .collect(Collectors.toMap(personalSkill ->
                                    personalSkill.getSkill().getId(),
                            personalSkill -> personalSkill));
        }

        List<SkillDescriptionDTO> skills = new ArrayList<>();
        List<Skill> allSkills = skillRepository.findAll();
        for(Skill skill: allSkills) {
            String skillId = skill.getId();

            List<SkillLevelDTO> skillLevelDTOS = skillExperienceLevelRepository.findDistinctBySkillId(skill.getId())
                    .stream().map(
                            skillExperienceLevel -> SkillLevelDTO.builder()
                                    .description(skillExperienceLevel.getDescription())
                                    .level(skillExperienceLevel.getName())
                                    .build()
                    ).collect(Collectors.toList());

            String expectedLevel = "";
            if (hmExpectedLevel.containsKey(skillId)) {
                String[] levelSplit = hmExpectedLevel.get(skillId).split(" ");
                expectedLevel = levelSplit[1];
            }
            String level = Constants.DEFAULT_SKILL_LEVEL;
            int experience = Constants.DEFAULT_EXPERIENCE;
            PersonalSkill personalSkill = personalSkillsMapById.get(skillId);
            if(Objects.nonNull(personalSkill)){
                level = Utility.floatToStringLevelFormat(personalSkill.getLevel());
                experience = personalSkill.getExperience();
            }
            SkillGroup skillGroup = skill.getSkillGroup();
            skills.add(SkillDescriptionDTO.builder()
                    .skillId(skill.getId())
                    .skillName(skill.getName())
                    .competency(skillGroup.getName())
                    .level(level)
                    .expectedLevel(expectedLevel)
                    .experience(experience)
                    .skillLevelDTOS(skillLevelDTOS)
                    .skillType(skillGroup.getSkillType().getName())
                    .build());
        }
        return skills;
    }
    
    @Override
    @Transactional
    public void addNewAssociate(AddSkillDto addSkillDto) throws LdapException {
        // Validation
        if (addSkillDto.getPersonalDto() == null ||
                StringUtils.isBlank(addSkillDto.getPersonalDto().getCode())) {
            throw new SkillManagementException(MessageCode.VALIDATION_ERRORS.toString(),
                    messageSource.getMessage(MessageCode.VALIDATION_ERRORS.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST);
        }

        User user = null;

        try {
            user = userService.findByNTId(addSkillDto.getPersonalDto().getCode());
        } catch (Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            if (e.getMessage().equalsIgnoreCase(messageSource.getMessage(
                    com.bosch.eet.skill.management.usermanagement.consts.MessageCode.USER_NOT_FOUND_MSG.toString(),
                    null,
                    LocaleContextHolder.getLocale()
            ))) { // user does not exist => create user from ldap info
                log.info("User not found in system, search in LDAP");
                LdapInfo ldapInfo = ldapService.getPrincipalInfo(addSkillDto.getPersonalDto().getCode()).orElseThrow(
                        () -> new LdapException(MessageCode.SKM_INVALID_LDAP_CHECKED.toString(),
                                messageSource.getMessage(MessageCode.SKM_INVALID_LDAP_CHECKED.toString(), null,
                                        LocaleContextHolder.getLocale()),
                                null)
                );

                user = userService.addUser(
                        UserDTO.builder()
                                .name(ldapInfo.getName())
                                .displayName(ldapInfo.getDisplayName())
                                .email(ldapInfo.getEmail())
                                .status(Status.ACTIVE.getLabel())
                                .type(UserType.PERSON.getLabel())
                                .build()
                );
            }
        }

        if (Status.INACTIVE.getLabel().equals(user.getStatus())) { // if user inactive => active
            user.setStatus(Status.ACTIVE.getLabel());
            user = userService.updateUser(user);
        }

//        boolean personalExist = personalRepository.existsById(user.getId());
        Personal savedPersonal = createAssociateAndSendMail(addSkillDto, user);
        if (savedPersonal == null) {
            throw new SkillManagementException(MessageCode.SAVE_PERSONAL_FAIL.toString(),
                    messageSource.getMessage(MessageCode.SAVE_PERSONAL_FAIL.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.EXPECTATION_FAILED);
        }

        // index to elastic search
        if (personalElasticRepository.existById(savedPersonal.getId())) {
            personalElasticRepository.updatePersonal(savedPersonal);
        } else {
            PersonalDocument document = personalConverter.convertToDocument(savedPersonal);
            personalElasticRepository.insertDataPersonal(document);
        }
    }

	private Personal createAssociateAndSendMail(AddSkillDto addSkillDto, User user) {
		String receiverMail = user.getEmail();
		addSkillDto.getPersonalDto().setId(user.getId());
		addSkillDto.getPersonalDto().setCode(user.getName());
		addSkillDto.getPersonalDto().setName(user.getDisplayName());
		addSkillDto.getPersonalDto().setSkillClusterId(addSkillDto.getSkillGroupIds());
		int expAtBosh = 0;
    	if (null != addSkillDto.getPersonalDto() && null != addSkillDto.getPersonalDto().getJoinDate()) {
    		expAtBosh = LocalDate.now().getYear() - addSkillDto.getPersonalDto().getJoinDate().getYear();
    		addSkillDto.getPersonalDto().setExperiencedAtBosch(String.valueOf(expAtBosh));
        }
		Personal newAssociate = personalConverter.mapAddSkillDtoToEntity(addSkillDto);
		newAssociate.setUpdated(false);
        Personal savedAssociate = null;
		try {
            savedAssociate = personalRepository.save(newAssociate);
		} catch (Exception e){
		   	 throw new SkillManagementException(messageSource.getMessage(
		             MessageCode.SKM_ASSOCIATE_ALREADY_EXIST.toString(),
		             null,
		             LocaleContextHolder.getLocale()),
		     MessageCode.SKM_ASSOCIATE_ALREADY_EXIST.toString(),
		     null,
		     NOT_FOUND);
		}

        List<String> skillGroupIds = addSkillDto.getSkillGroupIds();
        if(Objects.nonNull(skillGroupIds)) {
            savedAssociate.setPersonalSkillGroups(addPersonalSkillGroup(savedAssociate, skillGroupIds));
        }
		Optional<Group> groupOpt = groupRepository.findById(Constants.ASSOCIATE_GROUP_ID);
		UserGroup userGroup = UserGroup.builder()
				.user(user)
				.group(groupOpt.orElse(null))
				.build();
		userGroupRepository.save(userGroup);
		emailService.mailToUserAddedToSystem(receiverMail, Constants.WAM_SERVER);

        return savedAssociate;
	}
    
    @Override
    @Transactional
    public PersonalProjectDto updatePersonalProject(String personalProjectId, PersonalProjectDto personalProjectDto) {
        Optional<PersonalProject> personalProjectOpt = personalProjectRepository.findById(personalProjectId);
        if (personalProjectOpt.isPresent()) {
            PersonalProject personalProject = personalProjectOpt.get();
            Optional<ProjectRole> projectRoleEntity = projectRoleRepository.findById(personalProjectDto.getRoleId());
            projectRoleEntity.ifPresent(personalProject::setProjectRole);
            personalProjectConverter.mapDataToEntity(personalProjectDto, personalProject);
            
            PersonalProject savedPersonalProject = personalProjectRepository.save(personalProject);
            
            // update skill tags
            List<SkillTag> skillTagsModified = new ArrayList<>();
            List<ProjectSkillTag> projectSkillTags = new ArrayList<>();
            projectService.buildSkillTag(ProjectDto.builder().skillTags(personalProjectDto.getSkillTags()).build(),
                    personalProject.getProject(), skillTagsModified, projectSkillTags);
            if (!Objects.isNull(personalProject.getProject().getId())) {
                projectSkillTagRepository.deleteByProjectId(personalProject.getProject().getId());
            }
            skillTagRepository.saveAll(skillTagsModified);
            projectSkillTags= projectSkillTagRepository.saveAll(projectSkillTags);
            personalProject.getProject().setProjectSkillTags(new HashSet<>(projectSkillTags));
            
            projectRepository.save(savedPersonalProject.getProject());

            return personalProjectConverter.convertToDetailDTO(savedPersonalProject);
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Cannot find the project", null);
        }

    }
    
    @Override
    @Transactional
    public void editAssociateInfo(AddSkillDto addSkillDto) {
    	
    	Personal personal = personalConverter.mapAddSkillDtoToEntity(addSkillDto);
    	
    	try {
    		Personal savedPersonal = personalRepository.save(personal);
            List<String> skillGroupIds = addSkillDto.getSkillGroupIds();
            if(Objects.nonNull(skillGroupIds)) {
                personalSkillGroupRepository.deleteByPersonalId(savedPersonal.getId());
                savedPersonal.setPersonalSkillGroups(addPersonalSkillGroup(savedPersonal, skillGroupIds));
            }
            personalElasticRepository.updatePersonal(savedPersonal);
    	} catch (Exception e) {
    		log.error(e.getLocalizedMessage());
    	}
    }

    @Override
    public boolean checkIsExisted(String associateNTId){
        Optional<Personal> personal = personalRepository.findByPersonalCodeIgnoreCase(associateNTId);

        if(personal.isPresent()){
            if(personalSkillRepository.getCountByPersonalId(personal.get().getId()) == 0) {
				return false;
			} else {
				return true;
			}
        }
        return false;
    }
    @Override
    public void generateAssociateListExcel(HttpServletResponse response, List<String> personalStringList) throws IOException {
    	List<Personal> recordList = personalRepository.findAllById(personalStringList);
    	
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet;
        sheet = workbook.createSheet("Sheet1");
        
//        Sheet header
        Row headerRow = sheet.createRow(0);
        
        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeight(10);
        headerFont.setBold(true);;
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        
        for(int i = 0; i < excelHeaderTitleArray.length; i++) {
            createCell(headerRow, i, excelHeaderTitleArray[i], headerStyle);
        }

//        Write data
        int rowCount = 1;
        CellStyle dataStyle = workbook.createCellStyle();
        XSSFFont dataFont = workbook.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeight(10);
        dataStyle.setFont(dataFont);
        
        for (Personal record : recordList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            PersonalDto dto = personalConverter.convertToExcelDto(record);
            
            String joinDate = Objects.isNull(dto.getJoinDate()) ? null : dto.getJoinDate().format(Constants.SIMPLE_LOCAL_DATE_FORMAT);
            		
    		createCell(row, columnCount++, dto.getName(), dataStyle);
            createCell(row, columnCount++, dto.getCode(), dataStyle);
            createCell(row, columnCount++, dto.getDepartmentName(), dataStyle);
            createCell(row, columnCount++, dto.getGroup(), dataStyle);
            createCell(row, columnCount++, dto.getTeam(), dataStyle);
            createCell(row, columnCount++, dto.getGender(), dataStyle);
            createCell(row, columnCount++, dto.getEmail(), dataStyle);
            createCell(row, columnCount++, dto.getLevel(), dataStyle);
            createCell(row, columnCount++, dto.getTitle(), dataStyle);
            createCell(row, columnCount++, dto.getSupervisorName(), dataStyle);
            createCell(row, columnCount++, dto.getExperiencedAtBoschInt(), dataStyle);
            createCell(row, columnCount++, dto.getExperiencedNonBoschInt(), dataStyle);
            createCell(row, columnCount++, dto.getTotalExperiencedInt(), dataStyle);
            createCell(row, columnCount++, dto.getSkillGroup(), dataStyle);
            createCell(row, columnCount++, dto.getSkill(), dataStyle);
            createCell(row, columnCount++, joinDate, dataStyle);
            createCell(row, columnCount++, dto.getLocation(), dataStyle);
            createCell(row, columnCount++, dto.getBriefInfo(), dataStyle);
            createCell(row, columnCount++, dto.getSkillHighlights(), dataStyle);
        }
        
//        Column auto width
        for(int i = 0; i < excelHeaderTitleArray.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
//        Generate sheet
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
    }
    
    private void createCell(Row row, int columnIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }
    
    @Override
	public List<PersonalDto> findAssociateNotEvaluate(String personalId) 
    {
    	List<Personal> personals = personalRepository.findAssociatesNotEvaluateYet(personalId);
    	List<PersonalDto> personalDtos = personalConverter.convertToNotEvaluateDTOs(personals);
    	return personalDtos;
    }

    private List<PersonalSkillGroup> addPersonalSkillGroup(Personal personal, List<String> skillGroupIds){
        List<PersonalSkillGroup> personalSkillGroups = new ArrayList<>();
        skillGroupIds.forEach(skillGroupId -> {
            SkillGroup skillGroup = skillGroupRepository.findById(skillGroupId.trim()).orElseThrow(
                    () -> new SkillManagementException(MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(),
                            messageSource.getMessage(MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(), null,
                                    LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST)
            );

            personalSkillGroups.add(PersonalSkillGroup.builder()
                    .personal(personal)
                    .skillGroup(skillGroup)
                    .build());
        });
        return personalSkillGroupRepository.saveAll(personalSkillGroups);
    }
    
    @Override
    @Transactional
    public void deleteAssociate(String personalId) {
    	
    	Personal personal = personalRepository.findById(personalId).orElseThrow(
				() -> new SkillManagementException(MessageCode.ASSOCIATE_NOT_FOUND.toString(),
						messageSource.getMessage(MessageCode.ASSOCIATE_NOT_FOUND.toString(), null,
								LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST)
		);
    	try {
    		personal.setDeleted(true);
    		personalSpringElasticRepository.deleteById(personal.getId());
	    } catch (Exception e) {
			log.error(e.getMessage());
			throw new SkillManagementException(MessageCode.SYNC_ASSOCIATE_ELASTIC_FAIL.toString(),
	                messageSource.getMessage(MessageCode.SYNC_ASSOCIATE_ELASTIC_FAIL.toString(), null,
	                        LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
		}
    	
    }
}