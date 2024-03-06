package com.bosch.eet.skill.management.converter.utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.dto.CommonTaskDto;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.ProjectMemberDto;
import com.bosch.eet.skill.management.dto.SkillTagDto;
import com.bosch.eet.skill.management.dto.excel.XLSXProjectDto;
import com.bosch.eet.skill.management.elasticsearch.document.DepartmentProjectDocument;
import com.bosch.eet.skill.management.entity.CommonTask;
import com.bosch.eet.skill.management.entity.Customer;
import com.bosch.eet.skill.management.entity.Department;
import com.bosch.eet.skill.management.entity.GbUnit;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalProject;
import com.bosch.eet.skill.management.entity.Phase;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectRole;
import com.bosch.eet.skill.management.entity.ProjectScope;
import com.bosch.eet.skill.management.entity.ProjectSkillTag;
import com.bosch.eet.skill.management.entity.ProjectType;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillTag;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.ResourceNotFoundException;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.UserFacade;
import com.bosch.eet.skill.management.ldap.exception.LdapException;
import com.bosch.eet.skill.management.ldap.model.LdapInfo;
import com.bosch.eet.skill.management.ldap.service.LdapService;
import com.bosch.eet.skill.management.repo.CustomerRepository;
import com.bosch.eet.skill.management.repo.ProjectRepository;
import com.bosch.eet.skill.management.repo.SkillTagRepository;
import com.bosch.eet.skill.management.service.GbUnitService;
import com.bosch.eet.skill.management.service.PersonalService;
import com.bosch.eet.skill.management.service.PhaseService;
import com.bosch.eet.skill.management.service.ProjectRoleService;
import com.bosch.eet.skill.management.service.ProjectTypeService;
import com.bosch.eet.skill.management.service.SkillTagService;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public final class ProjectConverterUtil {

	private ProjectConverterUtil() {
		// prevent instantiation
	}
	
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PhaseService phaseService;

    @Autowired
    private ProjectTypeService projectTypeService;

    @Autowired
    private GbUnitService gbUnitService;

    @Autowired
    private ProjectRoleService projectRoleService;

    @Autowired
    private PersonalService personalService;

    @Autowired
    private LdapService ldapService;

    @Autowired
    private SkillTagService skillTagService;

    @Autowired
    private UserFacade userFacade;
    
    @Autowired
    private SkillTagRepository skillTagRepository;

    @Autowired
    private CustomerRepository customerRepository;


    public ProjectDto convertToSearchProjectDTO(Project project) {
        ProjectDto projectDto = ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .pmName(project.getLeader())
                .status(project.getStatus())
                .build();
        if(CollectionUtils.isNotEmpty(project.getProjectSkillTags())) {
        	projectDto.setSkillTags(project.getProjectSkillTags().stream()
                    .map(item -> SkillTagConverterUtil.convertToSimpleDTO(item.getSkillTag()))
                    .collect(Collectors.toSet()));
        }
        if (!Objects.isNull(projectDto.getGbUnit())) {
            projectDto.setGbUnit(project.getGbUnit().getName());
        }
        if (!Objects.isNull(project.getDepartment())) {
            projectDto.setDepartment(project.getDepartment().getId());
        }
        if (!Objects.isNull(project.getProjectType())) {
            projectDto.setProjectType(project.getProjectType().getName());
            projectDto.setProjectTypeId(project.getProjectType().getId());
        }
        if (!Objects.isNull(project.getStartDate())) {
            projectDto.setStartDate(Constants.SIMPLE_DATE_FORMAT.format(project.getStartDate()));
        } else {
            throw new SkillManagementException(MessageCode.INVALID_DATE.toString(),
                    messageSource.getMessage(MessageCode.INVALID_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (!Objects.isNull(project.getEndDate())) {
            projectDto.setEndDate(Constants.SIMPLE_DATE_FORMAT.format(project.getEndDate()));
        }
        if (!Objects.isNull(project.getTechnologyUsed())) {
            projectDto.setTechnologyUsed(project.getTechnologyUsed());
        }
        return projectDto;
    }
    
// This method is using for return all projects that contain the skill tag
    public List<ProjectDto> convertToListOfProjectContainSkillTag(List<Project> projects) {
    	Set<ProjectDto> projectDtoSet = new HashSet<>();
    	for (Project prj : projects) {
    		projectDtoSet.add(convertToProjectDetailDTO(prj));   	
    	}
		return projectDtoSet.stream().collect(Collectors.toList());    	
    	
    }
    
    public ProjectDto convertToProjectDetailDTO(Project project) {
        ProjectDto projectDto = ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .pmName(project.getLeader())
                .status(project.getStatus())
                .objective(project.getTargetObject())
                .challenge(project.getChallenge())
                .teamSize(project.getTeamSize())
                .description(project.getDescription())
                .referenceLink(project.getReferenceLink())
                .isTopProject(project.isTopProject())
                .stakeholder(project.getStackHolder())
                .highlight(project.getHightlight())
                .problemStatement(project.getProblemStatement())
                .solution(project.getSolution())
                .benefits(project.getBenefits())
                .build();

        if(CollectionUtils.isNotEmpty(project.getPhaseProjects())) {
        	projectDto.setPhaseDtoSet(project.getPhaseProjects().stream().map(PhaseConverterUtil::convertToDtoWithoutProject).collect(Collectors.toSet()));
        }
        if(CollectionUtils.isNotEmpty(project.getProjectSkillTags())) { 
        	projectDto.setSkillTags(project.getProjectSkillTags().stream()
                    .map(item -> SkillTagConverterUtil.convertToSimpleDTO(item.getSkillTag()))
                    .collect(Collectors.toSet()));
        }
        if (!Objects.isNull(project.getGbUnit())) {
            projectDto.setGbUnit(project.getGbUnit().getName());
        }
        if (!Objects.isNull(project.getProjectType())) {
            projectDto.setProjectType(project.getProjectType().getName());
            projectDto.setProjectTypeId(project.getProjectType().getId());
        }
        if (!Objects.isNull(project.getDepartment())) {
            projectDto.setDepartment(project.getDepartment().getId());
        }
        if (!Objects.isNull(project.getStartDate())) {
            projectDto.setStartDate(Constants.SIMPLE_DATE_FORMAT.format(project.getStartDate()));
        } else {
            throw new SkillManagementException(MessageCode.INVALID_DATE.toString(),
                    messageSource.getMessage(MessageCode.INVALID_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (!Objects.isNull(project.getEndDate())) {
            projectDto.setEndDate(Constants.SIMPLE_DATE_FORMAT.format(project.getEndDate()));
        }

        if (!Objects.isNull(project.getTechnologyUsed())) {
            projectDto.setTechnologyUsed(project.getTechnologyUsed());
        }
        
        if(CollectionUtils.isNotEmpty(project.getProjectSkillTags())) {
        	projectDto.setSkillTags(project.getProjectSkillTags().stream()
                    .map(item -> SkillTagConverterUtil.convertToSimpleDTO(item.getSkillTag()))
                    .collect(Collectors.toSet()));
        }
        if (!Objects.isNull(project.getCustomer())) {
            projectDto.setCustomerGb(project.getCustomer().getName());
        }

        ProjectScope projectScope = project.getProjectScope();
        if (!Objects.isNull(projectScope)) {
            projectDto.setProjectScopeName(projectScope.getName());
            projectDto.setProjectScopeId(projectScope.getId());
        }

        List<PersonalProject> personalProjects = project.getPersonalProject();
        List<ProjectMemberDto> memberDtos = new ArrayList<>();
        if (personalProjects != null && !personalProjects.isEmpty()) {
            for (PersonalProject personalProject : personalProjects) {
            	List<CommonTaskDto> commonTaskDtos = new ArrayList<>();
            	List<CommonTask> commonTasks = personalProject.getProjectRole().getCommonTask();
            	for (CommonTask commonTask : commonTasks) {
            		 commonTaskDtos.add(CommonTaskDto.builder()
                           .id(commonTask.getId())
                           .name(commonTask.getName())
                           .build());
				}

                memberDtos.add(ProjectMemberDto.builder()
                        .id(personalProject.getPersonal().getId())
                        .personalProjectId(personalProject.getId())
                        .role(personalProject.getProjectRole().getName())
                        .roleId(personalProject.getProjectRole().getId())
                        .name(personalProject.getPersonal().getUser() != null?
                                personalProject.getPersonal().getUser().getDisplayName():
                                userService.findById(personalProject.getPersonal().getId()).getDisplayName())
                        .commonTasks(commonTaskDtos)
                        .additionalTask(personalProject.getAdditionalTask())
                        .startDate(personalProject.getStartDate() != null ?
                                Constants.SIMPLE_DATE_FORMAT.format(personalProject.getStartDate()) : null)
                		.endDate(personalProject.getEndDate() != null ?
                                Constants.SIMPLE_DATE_FORMAT.format(personalProject.getEndDate()) : null)
                        .build());
            }
            projectDto.setMembers(memberDtos);
        }
        
        if(project.getProjectType().getName().equals(Constants.NONBOSCH)){
            if(!project.getPersonalProject().isEmpty()) {
                projectDto.setPersonalProjectId(project.getPersonalProject().get(0).getId());
            }else{
                throw new SkillManagementException(MessageCode.PROJECT_NOT_FOUND.toString(),
                        messageSource.getMessage(MessageCode.PROJECT_NOT_FOUND.toString(), null,
                                LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return projectDto;
    }

    public ProjectDto convertToSearchProjectDTOSimple(Project project) {
        ProjectDto projectDto = ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .pmName(project.getLeader())
                .status(project.getStatus())
                .build();
        if (!Objects.isNull(project.getGbUnit())) {
            projectDto.setGbUnit(project.getGbUnit().getName());
        }
        if (!Objects.isNull(project.getCustomerGb())) {
            projectDto.setGbUnit(project.getCustomerGb());
        }
        if (!Objects.isNull(project.getDepartment())) {
            projectDto.setDepartment(project.getDepartment().getId());
        }
        if (!Objects.isNull(project.getProjectType())) {
            projectDto.setProjectType(project.getProjectType().getName());
            projectDto.setProjectTypeId(project.getProjectType().getId());
        }
        if (!Objects.isNull(project.getStartDate())) {
            projectDto.setStartDate(Constants.SIMPLE_DATE_FORMAT.format(project.getStartDate()));
        }
        if (!Objects.isNull(project.getEndDate())) {
            projectDto.setEndDate(Constants.SIMPLE_DATE_FORMAT.format(project.getEndDate()));
        }
        if (!Objects.isNull(project.getTechnologyUsed())) {
            projectDto.setTechnologyUsed(project.getTechnologyUsed());
        }
        return projectDto;
    }

    public Project mapDtoToEntity(ProjectDto projectDto) {
        Project entity = new Project();
        if (projectDto.getId() != null) {
            entity.setId(projectDto.getId());
        }
        entity.setName(projectDto.getName());
        if (!Objects.isNull(projectDto.getStartDate())) {
            try {
                entity.setStartDate(Constants.SIMPLE_DATE_FORMAT.parse(projectDto.getStartDate()));
            } catch (ParseException e) {
                throw new SkillManagementException(MessageCode.INVALID_DATE.toString(),
                        messageSource.getMessage(MessageCode.INVALID_DATE.toString(), null,
                                LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        if (!Objects.isNull(projectDto.getEndDate())) {
            try {
                entity.setEndDate(Constants.SIMPLE_DATE_FORMAT.parse(projectDto.getEndDate()));
            } catch (ParseException e) {
                throw new SkillManagementException(MessageCode.INVALID_DATE.toString(),
                        messageSource.getMessage(MessageCode.INVALID_DATE.toString(), null,
                                LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        entity.setLeader(projectDto.getPmName());
        entity.setTeamSize(projectDto.getTeamSize());
        entity.setChallenge(projectDto.getChallenge());
        entity.setDescription(projectDto.getDescription());
        entity.setStatus(projectDto.getStatus());
        entity.setTopProject(projectDto.isTopProject());
        entity.setCustomerGb(projectDto.getCustomerGb());
        entity.setStackHolder(projectDto.getStakeholder());
        if(projectDto.getTechnologyUsed() != null) {
        	entity.setTechnologyUsed(projectDto.getTechnologyUsed());
        }

        if (projectDto.getDepartment() != null) {
            Optional<Project> project = projectRepository.findById(projectDto.getId());
            Department dept = project.get().getDepartment();
            entity.setDepartment(dept);
        }
        
		if (!Objects.isNull(projectDto.getCustomerGb())) {
			Optional<Customer> customerOpt = customerRepository.findByName(projectDto.getCustomerGb());
			Customer customer = customerOpt.orElseThrow(() -> new ResourceNotFoundException(messageSource
					.getMessage(MessageCode.CUSTOMER_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale())));
			entity.setCustomer(customer);
		}
//        entity.setGbUnit(projectDto.getGbUnit());
        entity.setTargetObject(projectDto.getObjective());
        entity.setReferenceLink(projectDto.getReferenceLink());
        entity.setCreatedBy(projectDto.getCreatedBy());
        entity.setHightlight(projectDto.getHighlight());
        entity.setProblemStatement(projectDto.getProblemStatement());
        entity.setSolution(projectDto.getSolution());
        entity.setBenefits(projectDto.getBenefits());
        return entity;
    }

    public DepartmentProjectDocument convertToDocument(Project project) {
        DepartmentProjectDocument projectDocument = DepartmentProjectDocument.builder()
                .id(project.getId())
                .projectId(project.getId())
                .projectName(project.getName())
                .projectType(project.getProjectType().getName())
                .projectManager(project.getLeader())
                .status(project.getStatus())
                .topProject(project.isTopProject())
                .objective(project.getTargetObject())
                .description(project.getDescription())
                .teamSize(project.getTeamSize())
                .highlight(project.getHightlight())
                .build();

        if(project.getStartDate() != null) {
            projectDocument.setStartDate(Constants.SIMPLE_DATE_FORMAT.format(project.getStartDate()));
        }

        if(project.getEndDate() != null) {
            projectDocument.setEndDate(Constants.SIMPLE_DATE_FORMAT.format(project.getEndDate()));
        }

        if (project.getDepartment() != null) {
            projectDocument.setTeam(project.getDepartment().getName());
        }

        Customer customer = project.getCustomer();
        if (Objects.nonNull(customer)) {
            projectDocument.setCustomerGB(customer.getName());
        }

        if (project.getGbUnit() != null) {
            projectDocument.setGbUnit(project.getGbUnit().getName());
        }

        if(project.getProjectSkillTags() != null && !project.getProjectSkillTags().isEmpty()){
            List<String> skillTags = project.getProjectSkillTags().stream()
            .filter(e-> e.getSkillTag().getIsMandatory() == true)
            .map(projectSkillTag -> projectSkillTag.getSkillTag().getName())
    		.collect(Collectors.toList());
            projectDocument.setTechnologyUsed(skillTags);
        }

        ProjectScope projectScope = project.getProjectScope();
        if(!Objects.isNull(projectScope)){
            projectDocument.setScopeId(projectScope.getId());
            projectDocument.setScopeName(projectScope.getName());
        }

        return projectDocument;

    }

    public List<DepartmentProjectDocument> convertToListDocument(List<Project> projects) {
        List<DepartmentProjectDocument> listProject = new ArrayList<>();
        for (Project project : projects) {
            listProject.add(convertToDocument(project));
        }
        return listProject;
    }

    public DepartmentProjectDocument convertDTOtoDocument (ProjectDto projectDto) {
    	DepartmentProjectDocument projectDocument = DepartmentProjectDocument.builder()
    			.id(projectDto.getId())
    			.projectId(projectDto.getId())
    			.projectName(projectDto.getName())
    			.projectType(projectDto.getProjectType())
    			.projectManager(projectDto.getPmName())
    			.status(projectDto.getStatus())
                .topProject(projectDto.isTopProject())
                .customerGB(projectDto.getCustomerGb())
                .gbUnit(projectDto.getGbUnit())
                .description(projectDto.getDescription())
                .objective(projectDto.getObjective())
                .teamSize(projectDto.getTeamSize())
                .highlight(projectDto.getHighlight())
                .scopeId(projectDto.getProjectScopeId())
                .scopeName(projectDto.getProjectScopeName())
    			.build();
    			
    	try {
            Date startDate = Constants.SIMPLE_DATE_FORMAT.parse(projectDto.getStartDate());
        	projectDocument.setStartDate(Constants.SIMPLE_DATE_FORMAT.format(startDate));
		} catch (ParseException e) {
			log.error(e.toString());
            throw new SkillManagementException(MessageCode.INVALID_DATE.toString(),
                    messageSource.getMessage(MessageCode.INVALID_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (StringUtils.isNotBlank(projectDto.getEndDate())) {
            try {
                Date endDate = Constants.SIMPLE_DATE_FORMAT.parse(projectDto.getEndDate());
                projectDocument.setEndDate(Constants.SIMPLE_DATE_FORMAT.format(endDate));
            } catch (ParseException e) {
                log.error(e.toString());
                throw new SkillManagementException(MessageCode.INVALID_DATE.toString(),
                        messageSource.getMessage(MessageCode.INVALID_DATE.toString(), null,
                                LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        if(projectDto.getSkillTags() != null && !projectDto.getSkillTags().isEmpty()){
        	List<String> skillTags=  projectDto.getSkillTags().stream()
                    .filter(e-> e.getIsMandatory() == true)
                    .map(SkillTagDto::getName)
            		.collect(Collectors.toList());
            projectDocument.setTechnologyUsed(skillTags);
        }

        return projectDocument;
    }

    public List<Project> mapXLSXDto2Projects(List<XLSXProjectDto> xlsxs) {
        List<Project> projects = new ArrayList<Project>();
        List<ProjectType> projectTypes = projectTypeService.findAll();
        ProjectType projectBoschForDefault = projectTypes.stream().filter(p -> Constants.BOSCH.equals(p.getName())).findFirst().orElse(null);
        if (projectBoschForDefault == null) {
            throw new SkillManagementException(MessageCode.NO_PROJECT_TYPE_FOUND.toString(),
                    messageSource.getMessage(MessageCode.NO_PROJECT_TYPE_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<GbUnit> gbUnits = gbUnitService.findAll();
        ProjectRole projectRole = projectRoleService.findById(Constants.PROJECT_ROLE_ID_DEFAULT);
        Map<String, Personal> cachedPersonalsByNTID = new HashMap<>();
        Map<String, Skill> cachedSkillsByName = new HashMap<>();

        List<SkillTag> newSkillTagToSaveList = new ArrayList<>();
        Map<String, SkillTag> cachedSkillTagByLowerCaseName = new HashMap<>();
        skillTagService.findAll().forEach(skillTag ->
                cachedSkillTagByLowerCaseName.put(skillTag.getName().toLowerCase(), skillTag));
        long[] totalTagWrapper = {skillTagRepository.count()};
        Map<String, Customer> cachedCustomerByLowerCaseName = new HashMap<>();
        customerRepository.findAll().forEach(customer ->
                cachedCustomerByLowerCaseName.put(customer.getName().toLowerCase(), customer));

        boolean isContainExisted = false;
        
        for (XLSXProjectDto xlsx : xlsxs) {
            Project project;
            
            // Handle existed project name is system
            List<Project> projectByNameList = projectRepository.findByNameIgnoreCase(xlsx.getProjectName());
            if(!projectByNameList.isEmpty()){
                project = Project.builder()
                        .name(xlsx.getProjectName())
                        .id(projectByNameList.get(0).getId())
                        .status(Constants.PROJECT_EXISTED)
                        .build();
                isContainExisted = true;
                projects.add(project);
            } else if(!isContainExisted){
                project = mapXLSXD2ProjectV2(
                        xlsx,
                        projectTypes,
                        projectBoschForDefault,
                        gbUnits,
                        projectRole,
                        cachedPersonalsByNTID,
                        cachedSkillsByName,
                        newSkillTagToSaveList,
                        cachedSkillTagByLowerCaseName,
                        totalTagWrapper,
                        cachedCustomerByLowerCaseName
                );
                projects.add(project);
            }
        }
        
        if(!isContainExisted) {
            skillTagService.saveAll(newSkillTagToSaveList);
        }
        return projects;
    }

    @Deprecated
    public Project mapXLSXD2Project(XLSXProjectDto xlsx) {
        String teamSize = xlsx.getTeamSize() != null ? Double.toString(xlsx.getTeamSize()) : null;
        Date startDate = null;
        if (xlsx.getStartDate() == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            startDate = cal.getTime();
        } else {
            startDate = xlsx.getStartDate();
        }
        Project project = Project.builder()
                .name(xlsx.getProjectName())
                .description(xlsx.getProjectDescription())
                .startDate(startDate)
                .endDate(xlsx.getEndDate())
                .teamSize(teamSize)
                .challenge(xlsx.getChallenge())
                .status(xlsx.getStatus())
                .createdBy(xlsx.getCreateBy())
                .build();
        return project;
    }

    public Project mapXLSXD2ProjectV2(XLSXProjectDto xlsx,
                                      List<ProjectType> projectTypes,
                                      ProjectType projectBosch,
                                      List<GbUnit> gbUnits,
                                      ProjectRole projectRole,
                                      Map<String, Personal> cachedPersonalsByNTID,
                                      Map<String, Skill> cachedSkillsByName,
                                      List<SkillTag> newSkillTagToSaveList,
                                      Map<String, SkillTag> cachedSkillTagByLowerCaseName,
                                      long[] totalTag,
                                      Map<String, Customer> cachedCustomerByLowerCaseName) {
        Project project = Project.builder()
                .name(xlsx.getProjectName())
                .description(xlsx.getProjectDescription())
                .endDate(xlsx.getEndDate())
                .challenge(xlsx.getChallenge())
                .status(xlsx.getStatus())
                .createdBy(xlsx.getCreateBy())
                .technologyUsed(xlsx.getTechnologiesUsed())
                .stackHolder(xlsx.getStackHolder())
                .customerGb(xlsx.getCustomerGb())
                .targetObject(xlsx.getProjectObjective())
                .leader(xlsx.getLeader())
                .referenceLink(xlsx.getDocumentLink())
                .build();

        // Start date
        Date startDate = null;
        if (xlsx.getStartDate() == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            startDate = cal.getTime();
        } else {
            startDate = xlsx.getStartDate();
        }
        project.setStartDate(startDate);

        // Project Type
        int indexProjectType = IntStream.range(0, projectTypes.size() - 1)
                .filter(i -> projectTypes.get(i).getName().equals(xlsx.getProjectType()))
                .findFirst()
                .orElse(-1);
        if (indexProjectType != -1) {
            project.setProjectType(projectTypes.get(indexProjectType));
        } else {
            project.setProjectType(projectBosch);
        }

        // Gb Unit
        project.setGbUnit(gbUnits.stream().filter(gb -> gb.getName().equals(xlsx.getGbUnitGroup())).findFirst().orElse(null));

        // V Model Phase
        if (StringUtils.isNotBlank(xlsx.getVModelPhases())) {
            List<Phase> phases = phaseService.findAllByListDescriptionLike(Arrays.asList(xlsx.getVModelPhases().split(",")));
            if (CollectionUtils.isNotEmpty(phases)) {
                project.setPhases(phases);
            }
        }

        // Personal project
        AtomicInteger teamSizeCount = new AtomicInteger();
        List<PersonalProject> personalProjects = new ArrayList<>();
        if (StringUtils.isNotBlank(xlsx.getMember())) {
            Arrays.stream(xlsx.getMember().split(",")).forEach(memberStr -> {
                memberStr = memberStr.trim();
                Personal personal = null;
                if (cachedPersonalsByNTID.get(memberStr) != null) { // Get from cached first
                    personal = cachedPersonalsByNTID.get(memberStr);
                } else {
                    User user = userService.findByName(memberStr);
                    if (user == null) { // If searching by name return null => Search by display name
                        user = userService.findByDisplayName(memberStr);
                    }

                    if (user == null) { // If searching by display name return null => Add user
                        user = userFacade.addUserAndPersonal(memberStr);
                    }

                    if (user != null) {
                        personal = personalService.findEntityById(user.getId());

                        if(personal == null){ // If user is existed but there was no personal => Add personal
                            personal = personalService.addRawPersonalWithoutPermission(Personal.builder()
                                    .id(user.getId())
                                    .title("")
                                    .personalCode(user.getName())
                                    .build());
                        }
                    }
                }

                if (personal != null) {
                    personalProjects.add(PersonalProject.builder()
                            .personal(personal)
                            .projectRole(projectRole)
                            .build());
                    cachedPersonalsByNTID.put(memberStr, personal);
                    teamSizeCount.getAndIncrement();
                }
            });
            if (CollectionUtils.isNotEmpty(personalProjects)) {
                project.setPersonalProject(personalProjects);
            }
        }

        // Team size
//        String teamSize = xlsx.getTeamSize() != null ? Double.toString(xlsx.getTeamSize()) : null;
        project.setTeamSize(Integer.toString(teamSizeCount.get()));


        // Skill tags
        Set<ProjectSkillTag> projectSkillTagSet = new HashSet<>();
        if (StringUtils.isNotBlank(xlsx.getTechnologiesUsed())) {

            // Handle inconsistency of data
            String techUsed = xlsx.getTechnologiesUsed().replace("\n", ",");
            techUsed = techUsed.replace("\u00a0", "");

            String[] skillTagNameList = techUsed.split(",");


            Set<String> skillTagNameSet = new HashSet<>();
            for(String skillTagName: skillTagNameList){
                //Check validation of tag name and duplication skill tag in project
                if(StringUtils.isBlank(skillTagName) || skillTagNameSet.contains(skillTagName.toLowerCase())) {
					continue;
				}

                skillTagName = skillTagName.trim();
                skillTagNameSet.add(skillTagName.toLowerCase());

                // check existence of skill tag before create new
                SkillTag skillTag = cachedSkillTagByLowerCaseName.get(skillTagName.toLowerCase());
                if(skillTag == null){
                    skillTag = SkillTag.builder().name(skillTagName).order(totalTag[0]++).build();
                    newSkillTagToSaveList.add(skillTag);
                    cachedSkillTagByLowerCaseName.put(skillTag.getName().toLowerCase(), skillTag);
                }
                projectSkillTagSet.add(ProjectSkillTag.builder().skillTag(skillTag).project(project).build());
            }
            if(!projectSkillTagSet.isEmpty()){
                project.setProjectSkillTags(projectSkillTagSet);
            }
        }

        // leader
        try {
        	if(StringUtils.isNotBlank(xlsx.getLeader())) {
        		Optional<LdapInfo> infoOptional = ldapService.getPrincipalInfo(xlsx.getLeader());
    			if(infoOptional.isPresent()) {
    				project.setLeader(infoOptional.get().getDisplayName());
    			}
        	}
		} catch (LdapException e) {
			e.printStackTrace();
			log.error(e.toString());
		}

        // Customer
        String customerGbString = xlsx.getCustomerGb();
        if(StringUtils.isNotBlank(customerGbString)){
            Customer customer = cachedCustomerByLowerCaseName.get(customerGbString.toLowerCase());
            if(Objects.isNull(customer)){
                customer = customerRepository.save(Customer.builder().name(customerGbString).build());
                cachedCustomerByLowerCaseName.put(customerGbString.toLowerCase(), customer);
            }
            project.setCustomer(customer);
            List<Project> customerProjects = getCustomerProjects(customer);
            customerProjects.add(project);
            customer.setProjects(customerProjects);
        }
        return project;
    }

    private static List<Project> getCustomerProjects(Customer customer) {
        List<Project> customerProjects = customer.getProjects();
        if(Objects.nonNull(customerProjects)) {
			return customerProjects;
		}
        return new ArrayList<>();
    }

    public ProjectDto convertToProjectPortfolioDto(Project project){
        ProjectDto projectDto = ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .teamSize(project.getTeamSize())
                .highlight(project.getHightlight())
                .build();

        ProjectScope projectScope = project.getProjectScope();
        if(!Objects.isNull(projectScope)){
            projectDto.setProjectScopeId(projectScope.getId());
            projectDto.setProjectScopeName(projectScope.getName());
        }

        Customer customer = project.getCustomer();
        if (!Objects.isNull(customer)) {
            projectDto.setCustomerGb(customer.getName());
        }

        Set<ProjectSkillTag> projectSkillTags = project.getProjectSkillTags();
        if(CollectionUtils.isNotEmpty(projectSkillTags)) {
            projectDto.setSkillTags(projectSkillTags.stream()
                    .map(item -> SkillTagConverterUtil.convertToSimpleDTO(item.getSkillTag()))
                    .collect(Collectors.toSet()));
        }

        return projectDto;
    }
}
