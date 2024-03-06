package com.bosch.eet.skill.management.converter;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.dto.CommonTaskDto;
import com.bosch.eet.skill.management.dto.PersonalProjectDto;
import com.bosch.eet.skill.management.dto.ProjectMemberDto;
import com.bosch.eet.skill.management.dto.SkillTagDto;
import com.bosch.eet.skill.management.elasticsearch.document.PersonalProjectDocument;
import com.bosch.eet.skill.management.entity.CommonTask;
import com.bosch.eet.skill.management.entity.GbUnit;
import com.bosch.eet.skill.management.entity.PersonalProject;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectRole;
import com.bosch.eet.skill.management.entity.ProjectSkillTag;
import com.bosch.eet.skill.management.entity.ProjectType;
import com.bosch.eet.skill.management.exception.EETResponseException;
import com.bosch.eet.skill.management.repo.CommonTaskRepository;
import com.bosch.eet.skill.management.repo.GbUnitRepository;
import com.bosch.eet.skill.management.repo.ProjectRepository;
import com.bosch.eet.skill.management.specification.CommonTasksSpecification;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PersonalProjectConverter {
	
    @Autowired
    private CommonTaskRepository commonTaskRepository;
    
    @Autowired
    private GbUnitRepository gbUnitRepository;
    
    @Autowired
    private ProjectRepository projectRepository;

    public PersonalProjectDto convertToSearchDTO(PersonalProject personalProject) {
        PersonalProjectDto personalProjectDto = new PersonalProjectDto();
        personalProjectDto.setId(personalProject.getId());
        personalProjectDto.setProjectId(personalProject.getProject().getId());
        Project project = personalProject.getProject();
        ProjectRole projectRole = personalProject.getProjectRole();
        if (!Objects.isNull(project)) {
            personalProjectDto.setName(project.getName());
            personalProjectDto.setTeamSize(project.getTeamSize());
            if (!Objects.isNull(project.getStatus())) {
                personalProjectDto.setStatus(project.getStatus());
            }
            personalProjectDto.setStatus(Constants.DEFAULT_PROJECT_STATUS);
            if (!Objects.isNull(project.getStartDate())) {
                personalProjectDto.setStartDate(Constants.SIMPLE_DATE_FORMAT.format(project.getStartDate()));
            }
            if (!Objects.isNull(project.getEndDate())) {
                personalProjectDto.setEndDate(Constants.SIMPLE_DATE_FORMAT.format(project.getEndDate()));
            }
            ProjectType projectType = project.getProjectType();
            if (!Objects.isNull(projectType)) {
                personalProjectDto.setProjectType(projectType.getName());
            }
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Project not found", null);
        }
        if (!Objects.isNull(projectRole)) {
            personalProjectDto.setRoleId(projectRole.getId());
            personalProjectDto.setRoleName(projectRole.getName());
        }
        
        if (!Objects.isNull(project.getTechnologyUsed())) {
        	personalProjectDto.setTechnologyUsed(project.getTechnologyUsed());
        }
        return personalProjectDto;
    }

    public PersonalProjectDto convertToDetailDTO(PersonalProject personalProject) {
        PersonalProjectDto personalProjectDto = new PersonalProjectDto();
        personalProjectDto.setProjectId(personalProject.getProject().getId());
        personalProjectDto.setId(personalProject.getId());
//      Set CommonTask
		ProjectRole projectRole = personalProject.getProjectRole();
		if(projectRole!=null) {
			List<CommonTaskDto> commonTaskDtos = new ArrayList<>();
			List<CommonTask> commonTasks = projectRole.getCommonTask();
			for (CommonTask commonTask : commonTasks) {
				commonTaskDtos.add(CommonTaskDto
						.builder()
						.id(commonTask.getId())
						.name(commonTask.getName())
						.build());
			}
			personalProjectDto.setTasks(commonTaskDtos);
		}
		
        if (StringUtils.isNotBlank(personalProject.getAdditionalTask())) {
            personalProjectDto.setAdditionalTasks(personalProject.getAdditionalTask());
        }
        Project project = personalProject.getProject();
        if (!Objects.isNull(project.getTechnologyUsed())) {
        	personalProjectDto.setTechnologyUsed(project.getTechnologyUsed());
        }
        if (!Objects.isNull(project)) {
            personalProjectDto.setName(project.getName());
            personalProjectDto.setPmName(project.getLeader());
            if (!Objects.isNull(project.getStatus())) {
                personalProjectDto.setStatus(project.getStatus());
            }
            personalProjectDto.setChallenge(project.getChallenge());
            personalProjectDto.setObjective(project.getTargetObject());
            if (!Objects.isNull(project.getTeamSize())) {
            	personalProjectDto.setTeamSize(project.getTeamSize());
            }
            if (!Objects.isNull(project.getReferenceLink())) {
            	personalProjectDto.setReferenceLink(project.getReferenceLink());
            }
            if (!Objects.isNull(project.getDescription())) {
            	personalProjectDto.setDescription(project.getDescription());
            }
            if (!Objects.isNull(project.getGbUnit())) {
            	personalProjectDto.setGbUnit(project.getGbUnit().getName());
            }
            if (!Objects.isNull(project.getStartDate())) {
                personalProjectDto.setStartDate(Constants.SIMPLE_DATE_FORMAT.format(project.getStartDate()));
            }
            if (!Objects.isNull(project.getEndDate())) {
                personalProjectDto.setEndDate(Constants.SIMPLE_DATE_FORMAT.format(project.getEndDate()));
            }
            ProjectType projectType = project.getProjectType();
            if (!Objects.isNull(projectType)) {
                personalProjectDto.setProjectType(projectType.getName());
            }
            Set<ProjectSkillTag>projectSkillTagSet=project.getProjectSkillTags();
            if(!Objects.isNull(projectSkillTagSet)&&!projectSkillTagSet.isEmpty()){
                personalProjectDto.setSkillTags(projectSkillTagSet.stream()
                        .map(item->SkillTagDto.builder()
                                .id(item.getSkillTag().getId())
                                .name(item.getSkillTag().getName())
                                .build())
                        .collect(Collectors.toSet()));
            }
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Project not found", null);
        }
        if (!Objects.isNull(projectRole)) {
            personalProjectDto.setRoleId(projectRole.getId());
            personalProjectDto.setRoleName(projectRole.getName());
        }
        Date memberStartDate = personalProject.getStartDate();
        if (!Objects.isNull(memberStartDate)) {
            personalProjectDto.setMemberStartDate(Constants.SIMPLE_DATE_FORMAT.format(memberStartDate));
        }
        Date memberEndDate = personalProject.getEndDate();
        if (!Objects.isNull(memberEndDate)) {
            personalProjectDto.setMemberEndDate(Constants.SIMPLE_DATE_FORMAT.format(memberEndDate));
        }
        return personalProjectDto;
    }

    public void mapDataToEntity(PersonalProjectDto personalProjectDto, PersonalProject personalProject) {
        String projectType = personalProjectDto.getProjectType();
        if (projectType.equals(Constants.BOSCH)) {
            if(!Objects.isNull(personalProjectDto.getAdditionalTasks())) {
                personalProject.setAdditionalTask(personalProjectDto.getAdditionalTasks());
            }
            
            Optional<Project> prjEntity = projectRepository.findById(personalProjectDto.getProjectId());
            if (prjEntity.isPresent()) {
            	Project prj = prjEntity.get();
            	Optional<GbUnit> gbUnitEn = gbUnitRepository.findByName(personalProjectDto.getGbUnit());
            	gbUnitEn.ifPresent(prj::setGbUnit);
            }
        } else {
            if (!Objects.isNull(personalProject.getProject())) {
                personalProject.getProject().setName(personalProjectDto.getName());
                
                // validate dates
                try {
                    if (Objects.isNull(personalProjectDto.getStartDate())) {
						throw new EETResponseException(String.valueOf(INTERNAL_SERVER_ERROR.value()), "StartDate is required", null);
					}
                    Date startDate = Constants.SIMPLE_DATE_FORMAT.parse(personalProjectDto.getStartDate());
                    personalProject.getProject().setStartDate(startDate);
                    
                    if (!Objects.isNull(personalProjectDto.getEndDate())) {
                        Date endDate = Constants.SIMPLE_DATE_FORMAT.parse(personalProjectDto.getEndDate());
                        if(startDate.after(endDate) ||  Constants.SIMPLE_DATE_FORMAT.format(startDate).equals(Constants.SIMPLE_DATE_FORMAT.format(endDate))) {
                            throw new EETResponseException(String.valueOf(BAD_REQUEST.value()),
                                    "end date must after start date", null);
                        }
                        personalProject.getProject().setEndDate(endDate);
                    } else {
                        personalProject.getProject().setEndDate(null);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    throw new EETResponseException(String.valueOf(INTERNAL_SERVER_ERROR.value()), "Date is not valid", null);
                }
                
                personalProject.setAdditionalTask(personalProjectDto.getAdditionalTasks());
                if (!personalProjectDto.getStatus().isEmpty()) {
                    personalProject.getProject().setStatus(personalProjectDto.getStatus());
                }
                
                personalProject.getProject().setTeamSize(personalProjectDto.getTeamSize());
                personalProject.getProject().setLeader(personalProjectDto.getPmName());
                personalProject.getProject().setChallenge(personalProjectDto.getChallenge());
                personalProject.getProject().setTargetObject(personalProjectDto.getObjective());
                personalProject.getProject().setDescription(personalProjectDto.getDescription());
                personalProject.getProject().setReferenceLink(personalProjectDto.getReferenceLink());
                personalProject.getProject().setTechnologyUsed(personalProjectDto.getTechnologyUsed());

            } else {
                throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Project not found", null);
            }
        }
        log.info(personalProjectDto.getId());
    }
    
    public CommonTask findCommonTaskById(String commonTaskId) {
        HashMap<String, String> hmSearch = new HashMap<>();
        hmSearch.put("common_task_id", commonTaskId);
        Specification<CommonTask> specification = CommonTasksSpecification.search(hmSearch);
        Page<CommonTask> commonTasks = commonTaskRepository.findAll(specification, Pageable.unpaged());
        if (!commonTasks.getContent().isEmpty()) {
        	CommonTask ct = commonTasks.getContent().get(0);
            return ct;
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Cannot find the course", null);
        }
    }
    
    public CommonTask findCommonTaskByName(String commonTaskName) {
        HashMap<String, String> hmSearch = new HashMap<>();
        hmSearch.put("name", commonTaskName);
        Specification<CommonTask> specification = CommonTasksSpecification.search(hmSearch);
        Page<CommonTask> commonTasks = commonTaskRepository.findAll(specification, Pageable.unpaged());
        if (!commonTasks.getContent().isEmpty()) {
        	CommonTask ct = commonTasks.getContent().get(0);
            return ct;
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Cannot find the course", null);
        }
    }
    
    public PersonalProjectDocument convertToDocument (PersonalProject personalProject) {
    	
    	return PersonalProjectDocument.builder()
    			.id(personalProject.getId())
    			.projectName(personalProject.getProject().getName())
    			.tasks(personalProject.getTask())
    			.additionalTasks(personalProject.getAdditionalTask())
    			.projectRole(personalProject.getProjectRole().getName())
    			.build();
    }
    
    public List<PersonalProjectDocument> convertToListDocument (List<PersonalProject> personalProjectList) {
    	List<PersonalProjectDocument> listPersonalProjectDocuments = new ArrayList<>();
    	for (PersonalProject personalProject : personalProjectList) {
    		listPersonalProjectDocuments.add(convertToDocument(personalProject));
    	}
    	return listPersonalProjectDocuments;
    }

    public ProjectMemberDto convertPersonalProjectToProjectMemberDto(PersonalProject personalProject){
        ProjectMemberDto projectMemberDto = ProjectMemberDto.builder()
                .id(personalProject.getPersonal().getId())
                .startDateDate(personalProject.getStartDate())
                .endDateDate(personalProject.getEndDate())
        		.personalProjectId(personalProject.getId()).build();
        return projectMemberDto;
    }
}
