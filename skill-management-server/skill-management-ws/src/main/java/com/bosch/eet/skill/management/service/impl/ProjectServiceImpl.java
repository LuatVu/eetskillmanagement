package com.bosch.eet.skill.management.service.impl;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.ExcelFileHandler;
import com.bosch.eet.skill.management.common.ObjectMapperUtils;
import com.bosch.eet.skill.management.converter.PersonalProjectConverter;
import com.bosch.eet.skill.management.converter.utils.ProjectConverterUtil;
import com.bosch.eet.skill.management.converter.utils.ProjectTypeConverterUtil;
import com.bosch.eet.skill.management.dto.CustomerGbDto;
import com.bosch.eet.skill.management.dto.FileStorageDTO;
import com.bosch.eet.skill.management.dto.PhaseDto;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.ProjectMemberDto;
import com.bosch.eet.skill.management.dto.ProjectTypeDto;
import com.bosch.eet.skill.management.dto.SimplePersonalProjectDto;
import com.bosch.eet.skill.management.dto.SkillTagDto;
import com.bosch.eet.skill.management.dto.excel.XLSXProjectDto;
import com.bosch.eet.skill.management.elasticsearch.document.DepartmentProjectDocument;
import com.bosch.eet.skill.management.elasticsearch.repo.ProjectElasticRepository;
import com.bosch.eet.skill.management.entity.Customer;
import com.bosch.eet.skill.management.entity.FileStorage;
import com.bosch.eet.skill.management.entity.GbUnit;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalProject;
import com.bosch.eet.skill.management.entity.Phase;
import com.bosch.eet.skill.management.entity.PhaseProject;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectRole;
import com.bosch.eet.skill.management.entity.ProjectScope;
import com.bosch.eet.skill.management.entity.ProjectSkillTag;
import com.bosch.eet.skill.management.entity.ProjectType;
import com.bosch.eet.skill.management.entity.SkillTag;
import com.bosch.eet.skill.management.enums.LayoutProjectPortfolio;
import com.bosch.eet.skill.management.exception.BadRequestException;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.ResourceNotFoundException;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.util.Utility;
import com.bosch.eet.skill.management.repo.CustomerRepository;
import com.bosch.eet.skill.management.repo.FileStorageRepository;
import com.bosch.eet.skill.management.repo.GbUnitRepository;
import com.bosch.eet.skill.management.repo.PersonalProjectRepository;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.PhaseProjectRepository;
import com.bosch.eet.skill.management.repo.PhaseRepository;
import com.bosch.eet.skill.management.repo.ProjectRepository;
import com.bosch.eet.skill.management.repo.ProjectRoleRepository;
import com.bosch.eet.skill.management.repo.ProjectScopeRepository;
import com.bosch.eet.skill.management.repo.ProjectSkillTagRepository;
import com.bosch.eet.skill.management.repo.ProjectTypeRepository;
import com.bosch.eet.skill.management.repo.SkillTagRepository;
import com.bosch.eet.skill.management.service.ObjectStorageService;
import com.bosch.eet.skill.management.service.ProjectService;
import com.bosch.eet.skill.management.specification.ProjectSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final GbUnitRepository gbUnitRepository;

    private final ProjectRepository projectRepository;
    private final ProjectTypeRepository projectTypeRepository;
    private final PersonalProjectRepository personalProjectRepository;
    private final PersonalRepository personalRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final ProjectScopeRepository projectScopeRepository;
    private final FileStorageRepository fileStorageRepository;
    
    private final ProjectConverterUtil projectConverter;
    private final PhaseProjectRepository phaseProjectRepository;
    private final PhaseRepository phaseRepository;
    private final ProjectSkillTagRepository projectSkillTagRepository;
    private final CustomerRepository customerRepository;
    private final PersonalProjectConverter personalProjectConverter;
    private final MessageSource messageSource;
    
    private final ObjectStorageService objectStorageService;

    @Value ("${elasticsearch.url}")
	private String elasticsearchUrl;

    @Autowired
    private ProjectElasticRepository projectElasticRepository;

    private final SkillTagRepository skillTagRepository;
	@Value ("${spring.profiles.active}")
	private String profile;
	
	@Value("${project.path}")
	private String projectStorageDir;

    @SuppressWarnings("deprecation")
    @Override
    public Page<ProjectDto> findAll(Pageable pageable, Map<String, String> q) {
        Page<Project> projects = null;
        List<ProjectDto> projectDtos = new ArrayList<>();
        Specification<Project> projectSpecification = ProjectSpecification.search(q);
        String yearString = q.get("year");
        if (StringUtils.isNotBlank(yearString)) {
            String[] yearList = yearString.split(",");
            projects = projectRepository.findAll(projectSpecification, pageable);
            for (String year : yearList) {
                for (Project project : projects) {
                    if (Integer.parseInt(project.getStartDate().toString().substring(0, 4)) == Integer.parseInt(year)) {
                        projectDtos.add(projectConverter.convertToSearchProjectDTO(project));
                    }

                }
            }
        } else {
            projects = projectRepository.findAll(projectSpecification, pageable);
            projectDtos = projects.map(projectConverter::convertToSearchProjectDTO).getContent();
        }
        log.info(projectDtos.toString());
        return new PageImpl<>(projectDtos, pageable, projectDtos.size());
    }

    @Override
    public List<ProjectDto> findAllForDropdown(Map<String, String> filterParams) {
        Specification<Project> projectSpecification = ProjectSpecification.search(filterParams);
        List<Project> projects = projectRepository.findAll(projectSpecification);
        List<ProjectDto> projectDtos = projects.stream()
                .map(project -> ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .projectType(project.getProjectType().getName())
                .build()
                ).sorted(Comparator.comparing(ProjectDto::getName)).collect(Collectors.toList());
        log.info(projectDtos.toString());
        return projectDtos;
    }

    @Override
    public List<ProjectTypeDto> findAll() {
        return projectRepository.findAllProjectIdName("Bosch")
                .stream()
                .map(o -> new ProjectTypeDto(o[0].toString(), o[1].toString()))
                .collect(Collectors.toList());
    }

    @Override
    public ProjectDto findById(String projectId) {
        Optional<Project> projectEntity = projectRepository.findById(projectId);
        if (projectEntity.isPresent()) {
            Project project = projectEntity.get();
            return projectConverter.convertToProjectDetailDTO(project);
        } else {
            throw new SkillManagementException(MessageCode.RESOURCE_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.RESOURCE_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public ProjectDto save(ProjectDto projectDto) {
    	Project resultProject;
        if (StringUtils.isBlank(projectDto.getId())) { // if DTO does not have id => create
        	resultProject = addProject(projectDto);
        } else {
        	resultProject = updateProject(projectDto);
        }
        return projectConverter.convertToProjectDetailDTO(resultProject);
    }

    private Project addProject(ProjectDto projectDto) {
    	projectDto.setName(projectDto.getName().trim());
        // Check name exist
        if (StringUtils.isBlank(projectDto.getName())) {
            throw new SkillManagementException(MessageCode.VALIDATION_ERRORS.toString(),
                    messageSource.getMessage(MessageCode.VALIDATION_ERRORS.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST);
        }
        Project existProject = projectRepository.findByName(projectDto.getName()).orElse(null);
        if (existProject != null) {
            throw new SkillManagementException(MessageCode.PROJECT_NAME_EXIST.toString(),
                    messageSource.getMessage(MessageCode.PROJECT_NAME_EXIST.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.EXPECTATION_FAILED);
        }

        Project project = findAndSetProjectRelations(projectDto, projectConverter.mapDtoToEntity(projectDto));
        Project saveProject = projectRepository.save(project);

        List<SkillTag> skillTagsModified = new ArrayList<>();
        List<ProjectSkillTag> projectSkillTags = new ArrayList<>();
        buildSkillTag(projectDto, saveProject, skillTagsModified, projectSkillTags);

        skillTagRepository.saveAll(skillTagsModified);
        projectSkillTags = projectSkillTagRepository.saveAll(projectSkillTags);
        saveProject.setProjectSkillTags(new HashSet<>(projectSkillTags));

        //add member
        List<ProjectMemberDto> memberDtoList = projectDto.getMembers();
        if (CollectionUtils.isNotEmpty(memberDtoList)) {
        	validateProjectMemberList(memberDtoList, saveProject.getStartDate(), saveProject.getEndDate());
            for (ProjectMemberDto member : memberDtoList) {
                savePersonalProject(null, saveProject, member);
            }
        }

        //add phase
        if (CollectionUtils.isNotEmpty(projectDto.getPhaseDtoSet())) {
            List<PhaseProject> phaseProjects = new ArrayList<>();
            for (PhaseDto phaseDto : projectDto.getPhaseDtoSet()) {
                phaseProjects.add(savePhaseProject(null, saveProject, phaseDto));
            }
            saveProject.setPhaseProjects(phaseProjects);
        }

        //sync customer projects
        Customer customer = saveProject.getCustomer();
        List<Project> customerProjects = customer.getProjects();
        customerProjects.add(saveProject);
        customer.setProjects(customerProjects);

        return saveProject;
    }

    private Project updateProject(ProjectDto projectDto) {
    	projectDto.setName(projectDto.getName().trim());
        // Check exist
        if (StringUtils.isBlank(projectDto.getId())) {
            throw new SkillManagementException(MessageCode.VALIDATION_ERRORS.toString(),
                    messageSource.getMessage(MessageCode.VALIDATION_ERRORS.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST);
        }
        Project project = projectRepository.findById(projectDto.getId()).orElse(null);
        if (project == null) {
            throw new SkillManagementException(MessageCode.PROJECT_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.PROJECT_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.EXPECTATION_FAILED);
        }

        // Check name exist
        if (StringUtils.isBlank(projectDto.getName())) {
            throw new SkillManagementException(MessageCode.VALIDATION_ERRORS.toString(),
                    messageSource.getMessage(MessageCode.VALIDATION_ERRORS.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST);
        }
        Project existProject = projectRepository.findByName(projectDto.getName()).orElse(null);
        if (existProject != null && !existProject.getId().equals(project.getId())) {
            throw new SkillManagementException(MessageCode.PROJECT_NAME_EXIST.toString(),
                    messageSource.getMessage(MessageCode.PROJECT_NAME_EXIST.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.EXPECTATION_FAILED);
        }

        Map<String, String> oldPhaseIdMap = project.getPhaseProjects().stream()
                .collect(Collectors.toMap(phaseProject -> phaseProject.getPhase().getId(), PhaseProject::getId));

        project = findAndSetProjectRelations(projectDto, projectConverter.mapDtoToEntity(projectDto));
        Project saveProject = projectRepository.save(project);

        //add member
        personalProjectRepository.deleteByProjectId(project.getId());
        List<ProjectMemberDto> memberDtoList = projectDto.getMembers();
        if (CollectionUtils.isNotEmpty(memberDtoList)) {
        	validateProjectMemberList(memberDtoList , saveProject.getStartDate(), saveProject.getEndDate());
            for (ProjectMemberDto member : memberDtoList) {
                savePersonalProject(null, saveProject, member);
            }
        }

        //update phase
        List<PhaseProject> phaseProjects = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(projectDto.getPhaseDtoSet())) {
            Map<String, PhaseDto> phaseDtoMap =
                    projectDto.getPhaseDtoSet().stream().collect(Collectors.toMap(PhaseDto::getId, item -> item));
            for (String oldPhaseId : oldPhaseIdMap.keySet()) {
                if (phaseDtoMap.containsKey(oldPhaseId)) {
                    phaseDtoMap.remove(oldPhaseId);
                } else { //delete old phase that not contain anymore
                    phaseProjectRepository.deleteById(oldPhaseIdMap.get(oldPhaseId));
                }
            }

            phaseProjects =  phaseProjectRepository.findByProjectId(saveProject.getId());

            // add new phases
            for (Map.Entry<String, PhaseDto> entry : phaseDtoMap.entrySet()) {
                String phaseId = entry.getKey();
                PhaseDto phaseDto = phaseDtoMap.get(phaseId);

                phaseProjects.add(savePhaseProject(null, saveProject, phaseDto));
            }
        } else { // Delete phases
            if (!oldPhaseIdMap.isEmpty()) {
                phaseProjectRepository.deleteAllById(oldPhaseIdMap.values());
            }
        }
        saveProject.setPhaseProjects(phaseProjects);

        List<SkillTag> skillTagsModified = new ArrayList<>();
        List<ProjectSkillTag> projectSkillTags = new ArrayList<>();
        buildSkillTag(projectDto, saveProject, skillTagsModified, projectSkillTags);
        if (!Objects.isNull(projectDto.getId())) {
            projectSkillTagRepository.deleteByProjectId(saveProject.getId());
        }
        skillTagRepository.saveAll(skillTagsModified);
        projectSkillTags = projectSkillTagRepository.saveAll(projectSkillTags);
        saveProject.setProjectSkillTags(new HashSet<>(projectSkillTags));
        return saveProject;
    }

    private Project findAndSetProjectRelations(ProjectDto projectDto, Project project){
        //set Project Type
        ProjectType projectType = projectTypeRepository.findByName(projectDto.getProjectType()).orElse(null);
        if (projectType == null) {
            throw new SkillManagementException(MessageCode.VALIDATION_ERRORS.toString(),
                    messageSource.getMessage(MessageCode.VALIDATION_ERRORS.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST);
        }
        project.setProjectType(projectType);

        // Set project role
        if (StringUtils.isNotBlank(projectDto.getGbUnit())) {
            GbUnit projectRole = gbUnitRepository.findByName(projectDto.getGbUnit()).orElse(null);
            if (projectRole == null) {
                throw new SkillManagementException(MessageCode.VALIDATION_ERRORS.toString(),
                        messageSource.getMessage(MessageCode.VALIDATION_ERRORS.toString(), null,
                                LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST);
            }
            project.setGbUnit(projectRole);
        }

        // Set project scope
        String projectScopeId = projectDto.getProjectScopeId();
        if(StringUtils.isNotBlank(projectScopeId)) {
            ProjectScope projectScope = projectScopeRepository.findById(projectScopeId).orElse(null);
            if (projectScope == null) {
                throw new SkillManagementException(MessageCode.VALIDATION_ERRORS.toString(),
                        messageSource.getMessage(MessageCode.VALIDATION_ERRORS.toString(), null,
                                LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST);
            }
            project.setProjectScope(projectScope);
        }

        return project;
    }

    @Override
	public void buildSkillTag(ProjectDto projectDto, Project saveProject, List<SkillTag> skillTagsModified,
			List<ProjectSkillTag> projectSkillTags) {
		long totalTag = skillTagRepository.count();
		if(CollectionUtils.isNotEmpty(projectDto.getSkillTags())) {
        	List<String> nameSkillTags = projectDto.getSkillTags().stream().map(item->item.getName()).collect(Collectors.toList());
        	List<SkillTag> skillTags = skillTagRepository.findByNameIn(nameSkillTags);
        	Map<String, SkillTag> mapSkillTag = new HashMap<>();
        	for (SkillTag skillTag : skillTags) {
				mapSkillTag.put(skillTag.getName().toUpperCase(), skillTag);
			}
        	for (SkillTagDto skillTagDto:projectDto.getSkillTags()) {
        		SkillTag skillTag = mapSkillTag.get(skillTagDto.getName().toUpperCase()) != null ?
        				mapSkillTag.get(skillTagDto.getName().toUpperCase()) : SkillTag.builder().name(skillTagDto.getName()).order(totalTag++).build();
				projectSkillTags.add(ProjectSkillTag.builder()
						.skillTag(skillTag)
						.project(saveProject)
						.build());
				skillTagsModified.add(skillTag);
        	}
        }
    }

    private void savePersonalProject(String id, Project saveProject, ProjectMemberDto member) {
        PersonalProject personalProject = PersonalProject.builder().id(id)
        		.additionalTask(member.getAdditionalTask())
                .startDate(member.getStartDateDate())
        		.endDate(member.getEndDateDate()).build();
        
        //set personal
        Optional<Personal> personalEntity = personalRepository.findById(member.getId());
        if (!personalEntity.isPresent()) {
            throw new SkillManagementException(MessageCode.PERSONAL_ID_NOT_EXIST.toString(),
                    messageSource.getMessage(MessageCode.PERSONAL_ID_NOT_EXIST.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
        }
        personalProject.setPersonal(personalEntity.get());
        //set project
        personalProject.setProject(saveProject);
        //set project role
        Optional<ProjectRole> projectRoleEntity = projectRoleRepository.findById(member.getRoleId());
        if (!projectRoleEntity.isPresent()) {
            throw new SkillManagementException(MessageCode.PROJECT_ROLE_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.PROJECT_ROLE_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
        }
        personalProject.setProjectRole(projectRoleEntity.get());
        try {
            personalProjectRepository.save(personalProject);
        } catch (Exception e) {
            throw new SkillManagementException(MessageCode.SAVING_PROJECT_FAIL.toString(),
                    messageSource.getMessage(MessageCode.SAVING_PROJECT_FAIL.toString(), null,
                            LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ProjectDto deleteProject(ProjectDto projectDto) {
        Optional<Project> projectOpt = projectRepository
                .findById(projectDto.getId());
        if (!projectOpt.isPresent()) {
            throw new SkillManagementException(MessageCode.PROJECT_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.PROJECT_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
        }
        Project project = projectOpt.get();

        if (!project.getProjectType().getName().equals(Constants.BOSCH) &&
                !project.getProjectType().getName().equals(Constants.NONBOSCH)) {
            throw new SkillManagementException(MessageCode.NO_MATCHING_PROJECT_TYPE_FOUND.toString(),
                    messageSource.getMessage(MessageCode.NO_MATCHING_PROJECT_TYPE_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
        }

        for (PersonalProject personalProject : project.getPersonalProject()) {
            try {
                personalProjectRepository.delete(personalProject);
            } catch (Exception e) {
                throw new SkillManagementException(MessageCode.ERROR_DELETING_PERSONAL_PROJECT.toString(),
                        messageSource.getMessage(MessageCode.ERROR_DELETING_PERSONAL_PROJECT.toString(), null,
                                LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
            }
        }
        
		try {
			List<SkillTag> skillTagsByProjectDeleted = projectSkillTagRepository.deleteByProject(project).stream().map(item -> item.getSkillTag())
					.collect(Collectors.toList());
			List<SkillTag> skillTagInProjectSkillTag = projectSkillTagRepository.findAllSkillTags();
			List<SkillTag> skillTagsNotUsed = skillTagsByProjectDeleted.stream()
					.filter(item -> !skillTagInProjectSkillTag.contains(item))
					.collect(Collectors.toList());
			if(!skillTagsNotUsed.isEmpty()) {
				List<SkillTag> skillTags = reorderSkillTagAfterDeleteProject(skillTagsNotUsed);
				skillTagRepository.saveAll(skillTags);
			}
		} catch (Exception e) {
			throw new SkillManagementException(MessageCode.ERROR_DELETING_PROJECT_SKILL_TAG.toString(),
					messageSource.getMessage(MessageCode.ERROR_DELETING_PROJECT_SKILL_TAG.toString(), null,
							LocaleContextHolder.getLocale()),
					null, INTERNAL_SERVER_ERROR);
		}
		
		try {
			phaseProjectRepository.deleteByProject(project);
		} catch (Exception e) {
			throw new SkillManagementException(MessageCode.ERROR_DELETING_PHASE_PROJECT.toString(),
					messageSource.getMessage(MessageCode.ERROR_DELETING_PHASE_PROJECT.toString(), null,
							LocaleContextHolder.getLocale()),
					null, INTERNAL_SERVER_ERROR);
		}

        Customer customer = project.getCustomer();
        if(customer != null) {
        	 customer.setProjects(customer.getProjects().stream()
                     .filter(item -> !item.getId().equalsIgnoreCase(project.getId())).collect(Collectors.toList()));
        }
       

        try {
            projectRepository.deleteById(project.getId());
        } catch (Exception e) {
            throw new SkillManagementException(MessageCode.ERROR_DELETING_PROJECT.toString(),
                    messageSource.getMessage(MessageCode.ERROR_DELETING_PROJECT.toString(), null,
                            LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
        }
		deleteFilePortfolio(project.getId());
        return projectDto;
    }
    
    /**
     * Delete folder storage file project portfolio
     *
     * @param projectId
     * @void
     */
	public void deleteFilePortfolio(String projectId) {
		String suffixNameFile = projectId + Constants.DOT + Constants.TXT_EXT;
		String pathFolder = projectStorageDir + Constants.SLASH + projectId;

		objectStorageService.deleteFileOrFolder(pathFolder);
		List<FileStorage> fileStorages = fileStorageRepository.findByNameEndingWith(suffixNameFile);
		for (FileStorage fileStorage : fileStorages) {
			fileStorage.setDeleted(true);
		}
		fileStorageRepository.saveAll(fileStorages);
	}
    
    /**
     * Reorder skill tags  when some skill tags are not used in any project, move skill tags are
     * not used to latest order
     *
     * @param skillTagsNotUsed
     * @return List<SkillTag>
     */
    public List<SkillTag> reorderSkillTagAfterDeleteProject(List<SkillTag> skillTagsNotUsed) {
    	List<SkillTag> skillTags = skillTagRepository.findAllByOrderByOrder();
    	List<SkillTag> result = new ArrayList<>(); 
    	long order=0;
    	long orderReverse=skillTags.size();
    	for (SkillTag skillTag : skillTags) {
			if(skillTagsNotUsed.contains(skillTag)) {
				skillTag.setOrder(--orderReverse);
			}else {
				skillTag.setOrder(order++);
			}
			result.add(skillTag);
		}
    	return result;
    }

    @Override
    public HashMap<String, Object> getFilter() {
        List<String> customerGbList = customerRepository.findAll()
                .stream().map(Customer::getName).collect(Collectors.toList());
        List<String> projectStatusList = projectRepository.findProjectStatusList();
        List<ProjectType> projectTypes = projectTypeRepository.findAll();
        List<ProjectTypeDto> projectTypeDto = ProjectTypeConverterUtil.convertToDTOs(projectTypes);
        HashMap<String, Object> result = new HashMap<>();
        result.put("customer_gb_filter", customerGbList);
        result.put("project_status_filter", projectStatusList);
        result.put("project_type_filter", projectTypeDto);
        return result;
    }
    
    @Override
    @Transactional
    public List<ProjectDto> importProject(String ntid, MultipartFile file, List<Project> savedProjectList) {
        try {
            if(!personalRepository.findByPersonalCodeIgnoreCase(ntid).isPresent()){
                throw new SkillManagementException(MessageCode.PERSONAL_ID_NOT_EXIST.toString(),
                        messageSource.getMessage(MessageCode.PERSONAL_ID_NOT_EXIST.toString(), null,
                                LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
            }
            
            List<XLSXProjectDto> xlsxProjectDtos = ExcelFileHandler.convertXLSXFileToProjectDto(ntid,file);
            List<Project> projectList = projectConverter.mapXLSXDto2Projects(xlsxProjectDtos);

            List<PhaseProject> allPhaseProjects = new ArrayList<>();
            List<PersonalProject> personalProjects = new ArrayList<>();
            List<ProjectSkillTag> projectSkillTagListToSave = new ArrayList<>();
            
            List<ProjectDto> existedProjectList = new ArrayList<>();
            boolean isContainExisted = false;
            
            for (Project project: projectList) {
                // Check project name existence
                if(!Objects.isNull(project.getStatus()) && project.getStatus().equalsIgnoreCase(Constants.PROJECT_EXISTED)){
                    existedProjectList.add(ProjectDto.builder().id(project.getId()).name(project.getName()).build());
                    isContainExisted = true;
                    continue;
                }
                
                if(isContainExisted) {
					continue;
				}
                
                // Add phase projects
                if (CollectionUtils.isNotEmpty(project.getPhases())) {
                    List<PhaseProject> phaseProjects = new ArrayList<>();
                    project.getPhases().forEach(ph -> {
                        PhaseProject phaseProject = PhaseProject.builder()
                                .phase(ph)
                                .project(project)
                                .build();
                        allPhaseProjects.add(phaseProject);
                        phaseProjects.add(phaseProject);
                    });
                    project.setPhaseProjects(phaseProjects);
                }

                // Add personal project
                if (CollectionUtils.isNotEmpty(project.getPersonalProject())) {
                    project.getPersonalProject().forEach(personalProject -> {
                        personalProject.setProject(project);
                        personalProjects.add(personalProject);
                    });
                }

                // Add project_skill_tags
                if (CollectionUtils.isNotEmpty(project.getProjectSkillTags())) {
                    project.getProjectSkillTags().forEach(projectSkillTag -> {
                        projectSkillTag.setProject(project);
                        projectSkillTagListToSave.add(projectSkillTag);
                    });
                }
            }

            if(!isContainExisted) {
                
                // Save/Update projects
                projectRepository.saveAll(projectList);
                
                phaseProjectRepository.saveAll(allPhaseProjects);
                personalProjectRepository.saveAll(personalProjects);
                projectSkillTagRepository.saveAll(projectSkillTagListToSave);
                projectRepository.saveAll(projectList);
                
                savedProjectList.addAll(projectList);  
            }
            return existedProjectList;
		} catch (Exception e) {
            log.error(e.getMessage());
            throw new SkillManagementException(MessageCode.IMPORT_FAIL.toString(),
                    messageSource.getMessage(MessageCode.IMPORT_FAIL.toString(), null,
                            LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
		}
	}

    @Override
    public Project saveAndSyncElasticProject(Project project) {
        Project savedProject = projectRepository.save(project);

        // index to elastic search
        if (projectElasticRepository.existById(project.getId())) {
            projectElasticRepository.update(project);
        } else {
            DepartmentProjectDocument projectDocument = projectConverter.convertToDocument(project);
            projectElasticRepository.insert(projectDocument);
        }
        return savedProject;
    }

    @Override
    @Transactional
    public List<CustomerGbDto> findAllByCustomerGb(){
        List<Project> projectList = projectRepository.findAllHavingCustomerGb();
        HashMap<String, List<ProjectDto>> projectDtoHashMap = new HashMap<>();
        HashMap<String, Integer> customerGbHCMap = new HashMap<>();
        HashMap<String, Integer> customerGbVModelMap = new HashMap<>();
        HashMap<String, HashMap<String, Integer>> customerGbSkillTagMap = new HashMap<>();
        List<CustomerGbDto> CustomerGbDtoList = new ArrayList<>();

        for (Project project : projectList) {
            if (StringUtils.isBlank(project.getCustomerGb())) {
				continue;
			}

            String customerGb = project.getCustomerGb();

            List<ProjectDto> projectDtoList = projectDtoHashMap.get(customerGb);
            if (projectDtoList == null) { // not added CustomerGb
                // Project
                projectDtoList = new ArrayList<>();
                projectDtoList.add(ProjectDto.builder().id(project.getId()).name(project.getName()).build());
                projectDtoHashMap.put(customerGb, projectDtoList);

                // Head count
                int teamSize;
                try{
                    teamSize = Integer.parseInt(project.getTeamSize());
                } catch (NumberFormatException e){
                    teamSize = 0;
                }
                customerGbHCMap.put(customerGb, teamSize);
                
                // VModel count
                int vModelCount = 0;
                if (project.getPhaseProjects() != null && !project.getPhaseProjects().isEmpty()) {
                    vModelCount = 1;
                }
                customerGbVModelMap.put(customerGb, vModelCount);

                // Skill tag
                HashMap<String, Integer> SkillTagCountMap = new HashMap<>();
                Set<ProjectSkillTag> projectSkillTagList = project.getProjectSkillTags();
                if (projectSkillTagList != null) {
                    projectSkillTagList.forEach(projectSkillTag ->
                            SkillTagCountMap.put(projectSkillTag.getSkillTag().getName(), 1));
                }
                customerGbSkillTagMap.put(customerGb, SkillTagCountMap);

            } else { // Already added CustomerGb
                // Project
                projectDtoList.add(ProjectDto.builder().id(project.getId()).name(project.getName()).build());

                // Head count
                int teamSize;
                try{
                    teamSize = Integer.parseInt(project.getTeamSize());
                } catch (NumberFormatException e){
                    teamSize = 0;
                }
                customerGbHCMap.replace(customerGb, customerGbHCMap.get(customerGb)+teamSize);
                
                // VModel count
                if (project.getPhaseProjects() != null && !project.getPhaseProjects().isEmpty()) {
                    customerGbVModelMap.replace(customerGb, customerGbVModelMap.get(customerGb) + 1);
                }

                // Skill tag
                Set<ProjectSkillTag> projectSkillTagList = project.getProjectSkillTags();
                if (projectSkillTagList != null) {
                    HashMap<String, Integer> SkillTagCountMap = customerGbSkillTagMap.get(customerGb);
                    projectSkillTagList.forEach(projectSkillTag -> {
                        String tagName = projectSkillTag.getSkillTag().getName();
                        if (SkillTagCountMap.containsKey(tagName)) {
							SkillTagCountMap.replace(tagName, SkillTagCountMap.get(tagName) + 1);
						} else {
							SkillTagCountMap.put(tagName, 1);
						}
                    });
                }

            }
        }

        projectDtoHashMap.forEach((key, value) -> CustomerGbDtoList.add(CustomerGbDto.builder()
                .projectDtoList(value)
                .gbName(key)
                .headCounts(customerGbHCMap.get(key))
                .vModelCount(customerGbVModelMap.get(key))
                .GbInfo(customerGbSkillTagMap.get(key))
                .build()));

        return CustomerGbDtoList;
    }

    private PhaseProject savePhaseProject(String id, Project saveProject, PhaseDto phaseDto) {
        PhaseProject phaseProject = new PhaseProject();
        phaseProject.setId(id);

        Optional<Phase> phaseOptional = phaseRepository.findById(phaseDto.getId());
        if(!phaseOptional.isPresent()){
            throw new SkillManagementException(MessageCode.PHASE_ID_NOT_EXIST.toString(),
                messageSource.getMessage(MessageCode.PHASE_ID_NOT_EXIST.toString(), null,
                        LocaleContextHolder.getLocale()), null, BAD_REQUEST);
        }

        phaseProject.setPhase(phaseOptional.get());
        phaseProject.setProject(saveProject);

        return phaseProjectRepository.save(phaseProject);
    }

	@Override
	public String updateAdditionalTask(String ntid, SimplePersonalProjectDto simplePersonalProjectDto,
			Set<String> auth) {
		Optional<PersonalProject> personalProjectOpt = personalProjectRepository
				.findById(simplePersonalProjectDto.getId());
		if (personalProjectOpt.isPresent()) {
			PersonalProject personalProject = personalProjectOpt.get();
			boolean isPersonalProject = personalProject.getPersonal().getPersonalCode().equals(ntid);
			if (isPersonalProject || auth.contains(Constants.EDIT_ASSOCIATE_INFO_PERMISSION)) {
				buildPersonalProject(ntid,simplePersonalProjectDto, personalProject);
				return Constants.SUCCESS;
			}
			throw new AccessDeniedException(messageSource.getMessage(MessageCode.ACCESS_DENIED.toString(), null,
					LocaleContextHolder.getLocale()));
		}
		throw new ResourceNotFoundException(messageSource.getMessage(MessageCode.PERSONAL_PROJECT_NOT_FOUND.toString(),
				null, LocaleContextHolder.getLocale()));
	}

	private void buildPersonalProject(String ntid, SimplePersonalProjectDto simplePersonalProjectDto,
			PersonalProject personalProject) {
        Project project = personalProject.getProject();
		// check if project is Non-Bosch then can not assign to another NonBosch project
		if (project.getProjectType().getName().equals(Constants.NONBOSCH)
				&& !project.getId().equals(simplePersonalProjectDto.getProjectId())) {
			throw new BadRequestException(messageSource.getMessage(
					MessageCode.CANNOT_ASSIGN_ANOTHER_NON_BOSCH.toString(), null, LocaleContextHolder.getLocale()));
		}
		
		if (!project.getId().equals(simplePersonalProjectDto.getProjectId())) {
			Optional<Project> projectOpt = projectRepository.findById(simplePersonalProjectDto.getProjectId());
			personalProject.setProject(projectOpt.orElseThrow(() -> new ResourceNotFoundException(messageSource
					.getMessage(MessageCode.PROJECT_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale()))));
		}
		if (!personalProject.getProjectRole().getId().equals(simplePersonalProjectDto.getRoleId())) {
			Optional<ProjectRole> projectRoleOpt = projectRoleRepository.findById(simplePersonalProjectDto.getRoleId());
			personalProject.setProjectRole(
					projectRoleOpt.orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage(
							MessageCode.PROJECT_ROLE_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale()))));
		}
		personalProject.setAdditionalTask(simplePersonalProjectDto.getAdditionalTasks());
		
		//validate start_date/ end_date
		List<PersonalProject> personalProjects = personalProjectRepository.findByPersonalIdAndProjectId(personalProject.getPersonal().getId(), project.getId());
		List<ProjectMemberDto> memberDtoList = personalProjects.stream()
			    .map(personalProjectConverter::convertPersonalProjectToProjectMemberDto)
			    .filter(item -> (item.getStartDateDate() != null && item.getPersonalProjectId() != simplePersonalProjectDto.getId()))
			    .collect(Collectors.toList());

        memberDtoList.add(ProjectMemberDto.builder().startDate(simplePersonalProjectDto.getMemberStartDate())
                 .endDate(simplePersonalProjectDto.getMemberEndDate()).id(personalProject.getPersonal().getId()).build());
                
        validateProjectMemberList(memberDtoList, project.getStartDate(), project.getEndDate());

		personalProject.setStartDate(Utility.parseSimpleDateFormat(simplePersonalProjectDto.getMemberStartDate()));
        String endDateStr = simplePersonalProjectDto.getMemberEndDate();
        if(StringUtils.isNotBlank(endDateStr)){
            personalProject.setEndDate(Utility.parseSimpleDateFormat(endDateStr));
        }
		personalProjectRepository.save(personalProject);
	}

    @Override
	public void validateProjectMemberList(List<ProjectMemberDto> memberSet, Date projectStartDate, Date projectEndDate) {
		for (ProjectMemberDto member : memberSet) {
			String startDateStr = member.getStartDate();
			if (StringUtils.isNotBlank(startDateStr)) {
                Date startDate = Utility.parseSimpleDateFormat(startDateStr);
                if(startDate.compareTo(projectStartDate) < 0 ||
                        (Objects.nonNull(projectEndDate) && startDate.compareTo(projectEndDate) > 0)){
                    throw new BadRequestException(messageSource.getMessage(MessageCode.INVALID_DATE.toString(), null,
                            LocaleContextHolder.getLocale()));
                }
                member.setStartDateDate(startDate);
			} else{
                throw new BadRequestException(messageSource.getMessage(MessageCode.MEMBER_START_DATE_IS_REQUIRED.toString(),
                        null,
                        LocaleContextHolder.getLocale()));
            }

			String endDateStr = member.getEndDate();
			if (StringUtils.isNotBlank(endDateStr)) {
                Date endDate = Utility.parseSimpleDateFormat(endDateStr);
                if(endDate.compareTo(projectStartDate) < 0 ||
                        (Objects.nonNull(projectEndDate) && endDate.compareTo(projectEndDate) > 0)){
                    throw new BadRequestException(messageSource.getMessage(MessageCode.INVALID_DATE.toString(), null,
                            LocaleContextHolder.getLocale()));
                }

                // validate: startDate < endDate
                if(member.getStartDateDate().compareTo(endDate) > 0) {
                    throw new BadRequestException(messageSource.getMessage(MessageCode.INVALID_DATE.toString(), null,
                            LocaleContextHolder.getLocale()));
                }
                member.setEndDateDate(endDate);
            }
		}

		Set<String> personalIdSet = memberSet.stream().map(ProjectMemberDto::getId).collect(Collectors.toSet());
		for (String personalId : personalIdSet) {
			List<ProjectMemberDto> memberDtoListOfPersonal = memberSet.stream()
					.filter(dto -> dto.getId().equalsIgnoreCase(personalId)).collect(Collectors.toList());
			
			int dtoListSize = memberDtoListOfPersonal.size();			
			memberDtoListOfPersonal.sort(Comparator.comparing(ProjectMemberDto::getStartDateDate));
			
			for (int i = 1; i < dtoListSize; i++) {
				Date firstEndDate = memberDtoListOfPersonal.get(i-1).getEndDateDate();
				Date secondStartDate = memberDtoListOfPersonal.get(i).getStartDateDate();
				// Check null endDate but not last record
				if(firstEndDate == null) {
                    throw new BadRequestException(messageSource.getMessage(MessageCode.PROJECT_MEMBER_PERIOD_OVERLAPPED.toString(), null,
                            LocaleContextHolder.getLocale()));
				}
				
				if(firstEndDate.compareTo(secondStartDate) > 0) {
                    throw new BadRequestException(messageSource.getMessage(MessageCode.PROJECT_MEMBER_PERIOD_OVERLAPPED.toString(), null,
                            LocaleContextHolder.getLocale()));
				}
			}
		}
	}

	 /**
     * Update information for project portfolio
     *
     * @param projectId
     * @param file
     * @param layoutValue
     * @return FileStorageDto
     */
	@Override
	@Transactional
	public FileStorageDTO updateInfoPortfolio(String projectId, MultipartFile file, String layoutValue) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException(messageSource
						.getMessage(MessageCode.PROJECT_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale())));
		if (project.getProjectType().getName().equals(Constants.NONBOSCH)) {
			throw new BadRequestException(messageSource.getMessage(
					MessageCode.CANNOT_ADD_PORTFOLIO_FOR_NON_BOSCH.toString(), null, LocaleContextHolder.getLocale()));
		}
		layoutValue=layoutValue.trim();
		if (!LayoutProjectPortfolio.exist(layoutValue)) {
			throw new BadRequestException(messageSource.getMessage(MessageCode.INVALID_LAYOUT.toString(), null,
					LocaleContextHolder.getLocale()));
		}

		String filePath = projectStorageDir + Constants.SLASH + projectId;
		String extention = FilenameUtils.getExtension(file.getOriginalFilename());
		String nameFile = layoutValue + Constants.UNDERSCORE + projectId + Constants.DOT + extention;
		try {
			objectStorageService.putObject(file, nameFile, filePath);
		} catch (IOException e) {
			throw new SkillManagementException(MessageCode.ERROR_UPLOAD_FILE.toString(), messageSource
					.getMessage(MessageCode.ERROR_UPLOAD_FILE.toString(), null, LocaleContextHolder.getLocale()), null,
					INTERNAL_SERVER_ERROR);
		}
		FileStorage fileStorage = fileStorageRepository.findByName(nameFile)
				.orElse(FileStorage.builder().name(nameFile).extension(extention).uri(filePath).build());
		fileStorage.setSize(file.getSize());
		fileStorageRepository.save(fileStorage);
		FileStorageDTO fileStorageDTO = ObjectMapperUtils.map(fileStorage, FileStorageDTO.class);
		fileStorageDTO.setUri(null);
		return fileStorageDTO;
	}

	 /**
     * Get information portfolio 
     *
     * @param projectId
     * @param layoutValue
     * @return String
     */
	@Override
	public String getInforPortfolio(String projectId, String layoutValue) {
		layoutValue=layoutValue.trim();
		if (!LayoutProjectPortfolio.exist(layoutValue)) {
			throw new BadRequestException(messageSource.getMessage(MessageCode.INVALID_LAYOUT.toString(), null,
					LocaleContextHolder.getLocale()));
		}
		String nameFile = layoutValue + Constants.UNDERSCORE + projectId + Constants.DOT + Constants.TXT_EXT;
		FileStorage fileStorage = fileStorageRepository.findByName(nameFile).orElse(null);
		if (Objects.isNull(fileStorage)) {
			return StringUtils.EMPTY;
		}
		StringBuilder content = new StringBuilder();
		BufferedReader reader;
		try {
			reader = new BufferedReader(
					new FileReader(fileStorage.getUri() + Constants.SLASH + fileStorage.getName()));
			String line;
			while ((line=reader.readLine()) != null ) {
				content.append(line);
			}
			reader.close();
			return content.toString();
		} catch (FileNotFoundException e) {
			throw new ResourceNotFoundException(messageSource.getMessage(MessageCode.PROJECT_NOT_FOUND.toString(), null,
					LocaleContextHolder.getLocale()));
		} catch (IOException e) {
			throw new SkillManagementException(MessageCode.READ_FILE_ERROR.toString(), messageSource
					.getMessage(MessageCode.READ_FILE_ERROR.toString(), null, LocaleContextHolder.getLocale()), null,
					INTERNAL_SERVER_ERROR);
		} 
	}
	
    @Override
    public ProjectDto getPortfolio(String projectId){
        Optional<Project> projectEntity = projectRepository.findById(projectId);
        if (projectEntity.isPresent()) {
            Project project = projectEntity.get();
            return projectConverter.convertToProjectPortfolioDto(project);
        } else {
            throw new SkillManagementException(MessageCode.PROJECT_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.PROJECT_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, BAD_REQUEST);
        }
    }

    @Override
    public Project editPortfolio(ProjectDto projectDto) {
        String projectId = projectDto.getId();
        // Check name exist
        if (StringUtils.isBlank(projectId)) {
            throw new BadRequestException(messageSource.getMessage(MessageCode.VALIDATION_ERRORS.toString(), null,
                    LocaleContextHolder.getLocale()));
        }

        Optional<Project> projectEntity = projectRepository.findById(projectId);
        if (!projectEntity.isPresent()) {
            throw new SkillManagementException(MessageCode.PROJECT_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.PROJECT_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, BAD_REQUEST);
        }

        Project project = projectEntity.get();
        project.setHightlight(projectDto.getHighlight());
        return project;
    }
}