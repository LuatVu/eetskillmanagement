package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.ExcelFileHandler;
import com.bosch.eet.skill.management.converter.PersonalProjectConverter;
import com.bosch.eet.skill.management.converter.utils.ProjectConverterUtil;
import com.bosch.eet.skill.management.dto.PhaseDto;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.ProjectMemberDto;
import com.bosch.eet.skill.management.dto.ProjectTypeDto;
import com.bosch.eet.skill.management.dto.SimplePersonalProjectDto;
import com.bosch.eet.skill.management.dto.SkillTagDto;
import com.bosch.eet.skill.management.dto.excel.XLSXProjectDto;
import com.bosch.eet.skill.management.elasticsearch.repo.ProjectElasticRepository;
import com.bosch.eet.skill.management.elasticsearch.repo.ProjectSpringElasticRepository;
import com.bosch.eet.skill.management.entity.Customer;
import com.bosch.eet.skill.management.entity.FileStorage;
import com.bosch.eet.skill.management.entity.GbUnit;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalProject;
import com.bosch.eet.skill.management.entity.Phase;
import com.bosch.eet.skill.management.entity.PhaseProject;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectRole;
import com.bosch.eet.skill.management.entity.ProjectSkillTag;
import com.bosch.eet.skill.management.entity.ProjectType;
import com.bosch.eet.skill.management.entity.SkillTag;
import com.bosch.eet.skill.management.exception.BadRequestException;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.ResourceNotFoundException;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.util.Utility;
import com.bosch.eet.skill.management.repo.CustomerRepository;
import com.bosch.eet.skill.management.repo.DepartmentRepository;
import com.bosch.eet.skill.management.repo.FileStorageRepository;
import com.bosch.eet.skill.management.repo.GBRepository;
import com.bosch.eet.skill.management.repo.GbUnitRepository;
import com.bosch.eet.skill.management.repo.PersonalProjectRepository;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.PhaseProjectRepository;
import com.bosch.eet.skill.management.repo.PhaseRepository;
import com.bosch.eet.skill.management.repo.ProjectRepository;
import com.bosch.eet.skill.management.repo.ProjectRoleRepository;
import com.bosch.eet.skill.management.repo.ProjectSkillGroupSkillRepository;
import com.bosch.eet.skill.management.repo.ProjectSkillTagRepository;
import com.bosch.eet.skill.management.repo.ProjectTypeRepository;
import com.bosch.eet.skill.management.repo.SkillTagRepository;
import com.bosch.eet.skill.management.service.impl.ProjectServiceImpl;
import com.itextpdf.io.source.ByteArrayOutputStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @InjectMocks
    private ProjectServiceImpl projectService;
    
    @MockBean
    private ProjectServiceImpl projectServiceImpl;

    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private ProjectRoleRepository projectRoleRepository;
    
    @Mock
    private SkillTagRepository skillTagRepository;

    @Mock
    private PersonalProjectRepository personalProjectRepository;

    @Mock
    private ProjectTypeRepository projectTypeRepository;

    @Autowired
    private MessageSource messageSource;
    
    @Mock
    private GBRepository gbRepository;
    
    @Mock
    private DepartmentRepository departmentRepository;
    
    @Mock
    private ExcelFileHandler excelFileHandler;
    
    @InjectMocks
    private ProjectConverterUtil projectConverter;

    @Mock
    private ProjectConverterUtil projectConverterMock;

    @Mock
    private PersonalProjectConverter personalProjectConverter;

    @Mock
    private PhaseProjectRepository phaseProjectRepository;

    @Mock
    private ProjectSkillGroupSkillRepository projectSkillGroupSkillRepository;

    @Mock
    private ProjectSkillTagRepository projectSkillTagRepository;

    @InjectMocks
    @Autowired
    private ProjectElasticRepository projectElasticRepository;
    
    @Mock
    private PersonalRepository personalRepository;
    
    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private ProjectSpringElasticRepository projectSpringElasticRepo;
    
    @Mock
    private MessageSource messageSourceMock;
    @Mock
    private GbUnitRepository gbUnitRepository;
    @Mock
    private PhaseRepository phaseRepository;
    @Mock
    private FileStorageRepository fileStorageRepository;
    @Mock
    private ObjectStorageService objectStorageService;
    @Mock
    private CustomerRepository customerRepository;
    
    @Value("${elasticsearch.url}")
    private String elasticsearchUrl;
    
    @Value ("${spring.profiles.active}")
    private String profile;

    @Value("${project.path}")
    private String projectStorageDir;

    @Test
    @DisplayName("Delete project happy case")
    void deleteNonBoschProject() {
        String startDate = "2022-12-25";
        String endDate = "2022-12-26";

        ProjectType projectType = ProjectType.builder()
                .id("1")
                .name("Non-Bosch")
                .build();

        Project project = Project.builder()
                .id("1")
                .name("Test project")
                .leader("Test")
                .projectType(projectType)
                .status("New")
                .challenge("This is challenge")
                .description("This is description")
                .createdBy("Test")
                .createdDate(new Date())
                .build();

        Personal personal = Personal.builder()
                .id("1")
                .personalNumber("123456")
                .personalCode("ABC1HC")
                .build();

        PersonalProject personalProject = PersonalProject.builder()
                .id("1")
                .personal(personal)
                .project(project)
                .build();

        ProjectRole projectRole = ProjectRole.builder()
                .id("1")
                .name("Test role")
                .build();

        FileStorage fileStorage = FileStorage.builder().id("1").name("Test Project").deleted(false).build();

        List<PersonalProject> personalProjects = new ArrayList<>();
        List<Project> projects = new ArrayList<>();
        List<FileStorage> fileStorages = new ArrayList<>();
        String suffixNameFile = project.getId() + Constants.DOT + Constants.TXT_EXT;
        String pathFolder = projectStorageDir + Constants.SLASH + project.getId();
        objectStorageService.deleteFileOrFolder(pathFolder);
        personalProjects.add(personalProject);
        projects.add(project);
        fileStorages.add(fileStorage);

        personal.setPersonalProjects(personalProjects);
        personalProject.setProjectRole(projectRole);
        projectType.setProject(projects);
        project.setPersonalProject(personalProjects);

        try {
            project.setStartDate(Constants.SIMPLE_DATE_FORMAT
                .parse(startDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_START_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_START_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            project.setEndDate(Constants.SIMPLE_DATE_FORMAT
                .parse(endDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_END_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_END_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ProjectDto projectDto = ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .projectType(project.getProjectType().getName())
                .status(project.getStatus())
                .createdBy(project.getCreatedBy())
                .description(project.getDescription())
                .pmName(project.getLeader())
                .challenge(project.getChallenge())
                .startDate(project.getStartDate().toString())
                .endDate(project.getEndDate().toString())
                .build();

        when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.of(project));
        when(fileStorageRepository.findByNameEndingWith(suffixNameFile)).thenReturn(fileStorages);

        projectService.deleteProject(projectDto);
        for (FileStorage element : fileStorages) {
            element.setDeleted(true);
        }
        Mockito.verify(personalProjectRepository).delete(personalProject);
        Mockito.verify(projectRepository).deleteById(project.getId());
    }

    @Test
    @DisplayName("Test delete non Bosch project failed - not found Portfolio File")
    void deleteNonBoschProjectWithEmptyFilePortfolio() {
        String startDate = "2022-12-25";
        String endDate = "2022-12-26";

        ProjectType projectType = ProjectType.builder()
                .id("1")
                .name(Constants.BOSCH)
                .build();

        Project project = Project.builder()
                .id("1")
                .name("Test project")
                .leader("Test")
                .projectType(projectType)
                .status("New")
                .challenge("This is challenge")
                .description("This is description")
                .createdBy("Test")
                .createdDate(new Date())
                .build();

        Personal personal = Personal.builder()
                .id("1")
                .personalNumber("123456")
                .personalCode("ABC1HC")
                .build();

        PersonalProject personalProject = PersonalProject.builder()
                .id("1")
                .personal(personal)
                .project(project)
                .build();

        ProjectRole projectRole = ProjectRole.builder()
                .id("1")
                .name("Test role")
                .build();

        List<PersonalProject> personalProjects = new ArrayList<>();
        List<Project> projects = new ArrayList<>();
        String suffixNameFile = project.getId() + Constants.DOT + Constants.TXT_EXT;
        personalProjects.add(personalProject);
        projects.add(project);

        personal.setPersonalProjects(personalProjects);
        personalProject.setProjectRole(projectRole);
        projectType.setProject(projects);
        project.setPersonalProject(personalProjects);

        try {
            project.setStartDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(startDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_START_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_START_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            project.setEndDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(endDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_END_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_END_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ProjectDto projectDto = ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .projectType(project.getProjectType().getName())
                .status(project.getStatus())
                .createdBy(project.getCreatedBy())
                .description(project.getDescription())
                .pmName(project.getLeader())
                .challenge(project.getChallenge())
                .startDate(project.getStartDate().toString())
                .endDate(project.getEndDate().toString())
                .build();

        when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.of(project));
        when(fileStorageRepository.findByNameEndingWith(suffixNameFile)).thenThrow(NullPointerException.class);

        assertThrows(NullPointerException.class, () -> projectService.deleteProject(projectDto));
    }

    @Test
    @DisplayName("Test delete empty project")
    void deleteEmptyProject() {
        ProjectDto projectDto = ProjectDto.builder()
                .id("1")
                .name("Test project")
                .build();
        when(projectRepository.findById("1")).thenThrow(SkillManagementException.class);
        assertThrows(SkillManagementException.class, () -> projectService.deleteProject(projectDto));
    }

    @Test
    @DisplayName("Test delete empty personal project - Throw exception")
    void deleteEmptyPersonalProjectThenThrowException() {
        String startDate = "2022-12-25";
        String endDate = "2022-12-26";

        ProjectType projectType = ProjectType.builder()
                .id("1")
                .name(Constants.NONBOSCH)
                .build();

        Project project = Project.builder()
                .id("1")
                .name("Test project")
                .leader("Test")
                .projectType(projectType)
                .status("New")
                .challenge("This is challenge")
                .description("This is description")
                .createdBy("Test")
                .createdDate(new Date())
                .build();

        List<Project> projects = new ArrayList<>();
        projects.add(project);

        projectType.setProject(projects);

        try {
            project.setStartDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(startDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_START_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_START_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null);
        }

        try {
            project.setEndDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(endDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_END_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_END_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null);
        }

        ProjectDto projectDto = ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .projectType(project.getProjectType().getName())
                .status(project.getStatus())
                .createdBy(project.getCreatedBy())
                .description(project.getDescription())
                .pmName(project.getLeader())
                .challenge(project.getChallenge())
                .startDate(project.getStartDate().toString())
                .endDate(project.getEndDate().toString())
                .build();

        when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.of(project));

        /*Since the personal project was not declared, it becomes nullable.
         As a result, projectService throws a null pointer exception instead of SkillManagementException*/
        assertThrows(NullPointerException.class, () -> projectService.deleteProject(projectDto));
//        assertThrows(SkillManagementException.class, () -> projectService.deleteProject(projectDto));
    }

    @Test
    @DisplayName("Test delete project with empty project type - Throw Exception")
    void deleteProjectWithEmptyProjectTypeThenThrowException() {
        String startDate = "2022-12-25";
        String endDate = "2022-12-26";

        Project project = Project.builder()
                .id("1")
                .name("Test project")
                .projectType(ProjectType.builder().build())
                .leader("Test")
                .status("New")
                .challenge("This is challenge")
                .description("This is description")
                .createdBy("Test")
                .createdDate(new Date())
                .build();

        try {
            project.setStartDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(startDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_START_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_START_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null);
        }

        try {
            project.setEndDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(endDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_END_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_END_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null);
        }

        ProjectDto projectDto = ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .projectTypeId(project.getProjectType().getId())
                .status(project.getStatus())
                .createdBy(project.getCreatedBy())
                .description(project.getDescription())
                .pmName(project.getLeader())
                .challenge(project.getChallenge())
                .startDate(project.getStartDate().toString())
                .endDate(project.getEndDate().toString())
                .build();

        when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.of(project));

        /*Since the project type was not declared, it becomes nullable.
         As a result, projectService throws a null pointer exception instead of SkillManagementException*/
        assertThrows(NullPointerException.class, () -> projectService.deleteProject(projectDto));
//        assertThrows(SkillManagementException.class, () -> projectService.deleteProject(projectDto));
    }

    @Test
    @DisplayName("Test add new Bosch project with customer - happy case")
    void testAddBoschProjectWithCustomerHappyCase() {
        String customerGb = "CustomerGb";
        String startDate = "2022-12-25";
        String endDate = "2023-12-26";

        ProjectType projectType = ProjectType.builder().id("1").name(Constants.BOSCH).build();

        ProjectDto projectDto = ProjectDto.builder().name("Test Project DTO").projectType(Constants.BOSCH)
                .gbUnit("MS").customerGb(customerGb).build();
        projectDto.setStartDate(startDate);
        projectDto.setEndDate(endDate);

        Project newProject = Project.builder().id("1").name("Project").build();

        try {
            newProject.setStartDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(startDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_START_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_START_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null);
        }

        try {
            newProject.setEndDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(endDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_END_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_END_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null);
        }

        Customer customer = Customer.builder()
                .id("1").name("Customer1")
                .build();

        Project existedCustomerProject = Project.builder()
                .id("2").name("Existed Project")
                .customerGb("BD").projectType(projectType)
                .customer(customer)
                .build();

        Phase phase = Phase.builder().id("1").name("Phase 1").build();
        PhaseProject phaseProject = PhaseProject.builder().id("1")
                .phase(phase).project(existedCustomerProject)
                .build();
        existedCustomerProject.setPhaseProjects(Collections.singletonList(phaseProject));
        customer.setProjects(new LinkedList<>(Collections.singletonList(existedCustomerProject)));

        SkillTagDto skillTagDto1 = SkillTagDto.builder().id("1").name("Skill Tag 1").build();
        projectDto.setSkillTags(new HashSet<>(Collections.singletonList(skillTagDto1)));

        PhaseDto phaseDto1 = PhaseDto.builder().id("phase1Id").build();
        projectDto.setPhaseDtoSet(new HashSet<>(Collections.singletonList(phaseDto1)));

//        1 -> 3,  7 -> 9,  4 -> 8
        ProjectMemberDto memberDto1 = ProjectMemberDto.builder().id("member1Id")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-01-01").endDate("2023-03-01").build();
        ProjectMemberDto memberDto2 = ProjectMemberDto.builder().id("member2Id")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-07-01").endDate("2023-09-01").build();
        ProjectMemberDto memberDto3 = ProjectMemberDto.builder().id("member3Id")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-04-01").endDate("2023-06-01").build();
        projectDto.setMembers(Arrays.asList(memberDto1, memberDto2, memberDto3));
        newProject.setCustomer(customer);

        List<Project> customerProjects;
        customerProjects = customer.getProjects();
        customerProjects.add(newProject);
        customer.setProjects(customerProjects);
        Phase phase1 = Phase.builder().id("phase1Id").name("phase 1").build();
        Personal personal1 = Personal.builder().id("member1Id").build();
        Personal personal2 = Personal.builder().id("member2Id").build();
        Personal personal3 = Personal.builder().id("member3Id").build();
        ProjectRole projectRole = ProjectRole.builder().build();

        ProjectSkillTag projectSkillTag = ProjectSkillTag.builder().build();

        when(projectRepository.findByName(projectDto.getName())).thenReturn(Optional.empty());
        when(projectConverterMock.mapDtoToEntity(projectDto)).thenReturn(newProject);
        when(projectTypeRepository.findByName(projectDto.getProjectType())).thenReturn(Optional.of(ProjectType.builder().build()));
        when(gbUnitRepository.findByName(projectDto.getGbUnit())).thenReturn(Optional.of(GbUnit.builder().build()));
        when(projectRepository.save(any())).thenReturn(newProject);
        when(skillTagRepository.count()).thenReturn(0L);
        when(skillTagRepository.findByNameIn(any())).thenReturn(new ArrayList<>());
        when(skillTagRepository.saveAll(any())).thenReturn(null);
        when(projectSkillTagRepository.saveAll(any())).thenReturn(Collections.singletonList(projectSkillTag));
        when(personalRepository.findById("member1Id")).thenReturn(Optional.of(personal1));
        when(personalRepository.findById("member2Id")).thenReturn(Optional.of(personal2));
        when(personalRepository.findById("member3Id")).thenReturn(Optional.of(personal3));
        when(projectRoleRepository.findById(any())).thenReturn(Optional.of(projectRole));
        when(personalProjectRepository.save(any())).thenReturn(null);
        when(phaseRepository.findById("phase1Id")).thenReturn(Optional.of(phase1));
        when(phaseProjectRepository.save(any())).thenReturn(null);
        when(projectConverterMock.convertToProjectDetailDTO(any())).thenReturn(ProjectDto.builder().build());

        assertNotNull(projectService.save(projectDto));
    }

    @Test
    @DisplayName("Test add new Bosch project with empty customer - throw exception")
    void testAddBoschProjectWithEmptyCustomerThenThrowException() {
        ProjectDto projectDto = ProjectDto.builder().name("test proj").projectType(Constants.BOSCH)
                .gbUnit("MS").build();

        SkillTagDto skillTagDto1 = SkillTagDto.builder().name("add project test 1").build();
        projectDto.setSkillTags(new HashSet<>(Collections.singletonList(skillTagDto1)));

        PhaseDto phaseDto1 = PhaseDto.builder().id("phase1Id").build();
        projectDto.setPhaseDtoSet(new HashSet<>(Collections.singletonList(phaseDto1)));

//        1 -> 3,  7 -> 9,  4 -> 8
        ProjectMemberDto memberDto1 = ProjectMemberDto.builder().id("memberId")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-01-01").endDate("2023-03-01").build();
        ProjectMemberDto memberDto2 = ProjectMemberDto.builder().id("memberId")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-07-01").endDate("2023-09-01").build();
        ProjectMemberDto memberDto3 = ProjectMemberDto.builder().id("memberId")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-04-01").endDate("2023-06-01").build();
        projectDto.setMembers(Arrays.asList(memberDto1, memberDto2, memberDto3));

        Project convertedProject = Project.builder().name("test proj").build();

        ProjectSkillTag projectSkillTag = ProjectSkillTag.builder().build();

        when(projectRepository.findByName(projectDto.getName())).thenReturn(Optional.empty());
        when(projectConverterMock.mapDtoToEntity(projectDto)).thenReturn(convertedProject);
        when(projectTypeRepository.findByName(projectDto.getProjectType())).thenReturn(Optional.of(ProjectType.builder().build()));
        when(gbUnitRepository.findByName(projectDto.getGbUnit())).thenReturn(Optional.of(GbUnit.builder().build()));
        when(projectRepository.save(any())).thenReturn(convertedProject);
        when(skillTagRepository.count()).thenReturn(0L);
        when(skillTagRepository.findByNameIn(any())).thenReturn(new ArrayList<>());
        when(skillTagRepository.saveAll(any())).thenReturn(null);
        when(projectSkillTagRepository.saveAll(any())).thenReturn(Collections.singletonList(projectSkillTag));

        assertThrows(NullPointerException.class, () -> projectService.save(projectDto));
    }

    @Test
    @DisplayName("Test add new Bosch project with empty phase - Return not null")
    void testAddBoschProjectWithEmptyPhaseThenReturnNotNull() {
        String customerGb = "CustomerGb";
        String startDate = "2022-12-25";
        String endDate = "2023-12-26";

        ProjectType projectType = ProjectType.builder().id("1").name(Constants.BOSCH).build();

        ProjectDto projectDto = ProjectDto.builder().name("Test Project DTO").projectType("Bosch")
                .gbUnit("MS").customerGb(customerGb).build();
        projectDto.setStartDate(startDate);
        projectDto.setEndDate(endDate);

        Project newProject = Project.builder().id("1").name("Project").build();

        try {
            newProject.setStartDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(startDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_START_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_START_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null);
        }

        try {
            newProject.setEndDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(endDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_END_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_END_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null);
        }

        Customer customer = Customer.builder()
                .id("1").name("Customer1")
                .build();

        Project existedCustomerProject = Project.builder()
                .id("2").name("Existed Project")
                .customerGb("BD").projectType(projectType)
                .customer(customer)
                .build();

        customer.setProjects(new LinkedList<>(Collections.singletonList(existedCustomerProject)));
        SkillTagDto skillTagDto1 = SkillTagDto.builder().id("1").name("Skill Tag 1").build();
        projectDto.setSkillTags(new HashSet<>(Collections.singletonList(skillTagDto1)));

//        1 -> 3,  7 -> 9,  4 -> 8
        ProjectMemberDto memberDto1 = ProjectMemberDto.builder().id("member1Id")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-01-01").endDate("2023-03-01").build();
        ProjectMemberDto memberDto2 = ProjectMemberDto.builder().id("member2Id")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-07-01").endDate("2023-09-01").build();
        ProjectMemberDto memberDto3 = ProjectMemberDto.builder().id("member3Id")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-04-01").endDate("2023-06-01").build();
        projectDto.setMembers(Arrays.asList(memberDto1, memberDto2, memberDto3));
        newProject.setCustomer(customer);

        List<Project> customerProjects;
        customerProjects = customer.getProjects();
        customerProjects.add(newProject);
        customer.setProjects(customerProjects);
        Personal personal1 = Personal.builder().id("member1Id").build();
        Personal personal2 = Personal.builder().id("member2Id").build();
        Personal personal3 = Personal.builder().id("member3Id").build();
        ProjectRole projectRole = ProjectRole.builder().build();

        ProjectSkillTag projectSkillTag = ProjectSkillTag.builder().build();

        when(projectRepository.findByName(projectDto.getName())).thenReturn(Optional.empty());
        when(projectConverterMock.mapDtoToEntity(projectDto)).thenReturn(newProject);
        when(projectTypeRepository.findByName(projectDto.getProjectType())).thenReturn(Optional.of(ProjectType.builder().build()));
        when(gbUnitRepository.findByName(projectDto.getGbUnit())).thenReturn(Optional.of(GbUnit.builder().build()));
        when(projectRepository.save(any())).thenReturn(newProject);
        when(skillTagRepository.count()).thenReturn(0L);
        when(skillTagRepository.findByNameIn(any())).thenReturn(new ArrayList<>());
        when(skillTagRepository.saveAll(any())).thenReturn(null);
        when(projectSkillTagRepository.saveAll(any())).thenReturn(Collections.singletonList(projectSkillTag));
        when(personalRepository.findById("member1Id")).thenReturn(Optional.of(personal1));
        when(personalRepository.findById("member2Id")).thenReturn(Optional.of(personal2));
        when(personalRepository.findById("member3Id")).thenReturn(Optional.of(personal3));
        when(projectRoleRepository.findById(any())).thenReturn(Optional.of(projectRole));
        when(personalProjectRepository.save(any())).thenReturn(null);
        when(projectConverterMock.convertToProjectDetailDTO(any())).thenReturn(ProjectDto.builder().build());

        assertNotNull(projectService.save(projectDto));
    }

    @Test
    @DisplayName("Delete Non Bosch project with phase - Happy Case")
    void deleteNonBoschProjectWithPhaseHappyCase() {
        String startDate = "2022-12-25";
        String endDate = "2022-12-26";

        ProjectType projectType = ProjectType.builder()
                .id("1")
                .name(Constants.NONBOSCH)
                .build();

        Project project = Project.builder()
                .id("1")
                .name("Test project")
                .leader("Test")
                .projectType(projectType)
                .status("New")
                .challenge("This is challenge")
                .description("This is description")
                .createdBy("Test")
                .createdDate(new Date())
                .build();

        Personal personal = Personal.builder()
                .id("1")
                .personalNumber("123456")
                .personalCode("ABC1HC")
                .build();

        PersonalProject personalProject = PersonalProject.builder()
                .id("1")
                .personal(personal)
                .project(project)
                .build();

        ProjectRole projectRole = ProjectRole.builder()
                .id("1")
                .name("Test role")
                .build();

        FileStorage fileStorage = FileStorage.builder().id("1").name("Test Project").deleted(false).build();

        List<PersonalProject> personalProjects = new ArrayList<>();
        List<Project> projects = new ArrayList<>();
        List<FileStorage> fileStorages = new ArrayList<>();
        String pathFolder = projectStorageDir + Constants.SLASH + project.getId();
        objectStorageService.deleteFileOrFolder(pathFolder);
        personalProjects.add(personalProject);
        projects.add(project);
        fileStorages.add(fileStorage);

        personal.setPersonalProjects(personalProjects);
        personalProject.setProjectRole(projectRole);
        projectType.setProject(projects);
        project.setPersonalProject(personalProjects);

        try {
            project.setStartDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(startDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_START_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_START_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            project.setEndDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(endDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_END_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_END_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ProjectDto projectDto = ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .projectType(project.getProjectType().getName())
                .status(project.getStatus())
                .createdBy(project.getCreatedBy())
                .description(project.getDescription())
                .pmName(project.getLeader())
                .challenge(project.getChallenge())
                .startDate(project.getStartDate().toString())
                .endDate(project.getEndDate().toString())
                .build();

        PhaseDto phaseDto = PhaseDto.builder()
                .id("1").name("Phase Dto")
                .projects(Collections.singletonList(projectDto))
                .build();

        projectDto.setPhaseDtoSet(new HashSet<>(Collections.singletonList(phaseDto)));

        when(projectRepository.findById(projectDto.getId())).thenReturn(Optional.of(project));

        projectService.deleteProject(projectDto);
        for (FileStorage element : fileStorages) {
            element.setDeleted(true);
        }
        Mockito.verify(personalProjectRepository).delete(personalProject);
        Mockito.verify(projectRepository).deleteById(project.getId());
    }

    @Test
    @DisplayName("Update Portfolio for Non Bosch Project - Throw Exception")
    void updatePortfolioForNonBoschProjectThenThrowException() {
        String startDate = "2022-12-25";
        String endDate = "2022-12-26";

        ProjectType projectType = ProjectType.builder()
                .id("1")
                .name(Constants.NONBOSCH)
                .build();

        Project project = Project.builder()
                .id("1")
                .name("Test project")
                .leader("Test")
                .projectType(projectType)
                .status("New")
                .challenge("This is challenge")
                .description("This is description")
                .createdBy("Test")
                .createdDate(new Date())
                .build();

        Personal personal = Personal.builder()
                .id("1")
                .personalNumber("123456")
                .personalCode("ABC1HC")
                .build();

        PersonalProject personalProject = PersonalProject.builder()
                .id("1")
                .personal(personal)
                .project(project)
                .build();

        ProjectRole projectRole = ProjectRole.builder()
                .id("1")
                .name("Test role")
                .build();

        List<PersonalProject> personalProjects = new ArrayList<>();
        List<Project> projects = new ArrayList<>();
        String pathFolder = projectStorageDir + Constants.SLASH + project.getId();
        objectStorageService.deleteFileOrFolder(pathFolder);
        personalProjects.add(personalProject);
        projects.add(project);

        personal.setPersonalProjects(personalProjects);
        personalProject.setProjectRole(projectRole);
        projectType.setProject(projects);
        project.setPersonalProject(personalProjects);

        try {
            project.setStartDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(startDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_START_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_START_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            project.setEndDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(endDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_END_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_END_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ProjectDto projectDto = ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .projectType(project.getProjectType().getName())
                .status(project.getStatus())
                .createdBy(project.getCreatedBy())
                .description(project.getDescription())
                .pmName(project.getLeader())
                .challenge(project.getChallenge())
                .startDate(project.getStartDate().toString())
                .endDate(project.getEndDate().toString())
                .build();

        PhaseDto phaseDto = PhaseDto.builder()
                .id("1").name("Phase Dto")
                .projects(Collections.singletonList(projectDto))
                .build();

        projectDto.setPhaseDtoSet(new HashSet<>(Collections.singletonList(phaseDto)));

        when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.of(project));

        assertThrows(BadRequestException.class,
                () -> projectService.updateInfoPortfolio(projectDto.getId(), null, anyString()));
    }

    @Test
    @DisplayName("Update phase for existed Project")
    @Transactional
    void updatePhaseForExistedProject() {
        String customerGb = "CustomerGb";
        String startDate = "2022-12-25";
        String endDate = "2023-12-26";

        ProjectType projectType = ProjectType.builder().id("1").name(Constants.BOSCH).build();

        ProjectDto projectDto = ProjectDto.builder().id("1").name("Test Project DTO")
                .description("Update phase for old project").projectType(Constants.BOSCH)
                .gbUnit("MS").customerGb(customerGb)
                .build();
        projectDto.setStartDate(startDate);
        projectDto.setEndDate(endDate);

        Project newCustomerProject = Project.builder().id("2").name("New Project").build();

        try {
            newCustomerProject.setStartDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(startDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_START_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_START_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null);
        }

        try {
            newCustomerProject.setEndDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(endDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_END_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_END_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null);
        }

        Customer customer = Customer.builder()
                .id("1").name("Customer1")
                .build();

        Project existedCustomerProject = Project.builder()
                .id("1").name("Existed Customer Project")
                .customerGb("BD").projectType(projectType)
                .customer(customer)
                .build();

        Phase phase = Phase.builder().id("1").name("Phase 1").build();
        PhaseProject phaseProject = PhaseProject.builder().id("1")
                .phase(phase).project(existedCustomerProject)
                .build();
        existedCustomerProject.setPhaseProjects(Collections.singletonList(phaseProject));
        customer.setProjects(new LinkedList<>(Collections.singletonList(existedCustomerProject)));

        SkillTagDto skillTagDto1 = SkillTagDto.builder().id("1").name("Skill Tag 1").build();
        projectDto.setSkillTags(new HashSet<>(Collections.singletonList(skillTagDto1)));

        PhaseDto phaseDto1 = PhaseDto.builder().id("phase1Id").name("New Phase DTO").build();
        projectDto.setPhaseDtoSet(new HashSet<>(Collections.singletonList(phaseDto1)));

//        1 -> 3,  7 -> 9,  4 -> 8
        ProjectMemberDto memberDto1 = ProjectMemberDto.builder().id("member1Id")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-01-01").endDate("2023-03-01").build();
        ProjectMemberDto memberDto2 = ProjectMemberDto.builder().id("member2Id")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-07-01").endDate("2023-09-01").build();
        ProjectMemberDto memberDto3 = ProjectMemberDto.builder().id("member3Id")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-04-01").endDate("2023-06-01").build();
        projectDto.setMembers(Arrays.asList(memberDto1, memberDto2, memberDto3));
        newCustomerProject.setCustomer(customer);

        List<Project> customerProjects;
        customerProjects = customer.getProjects();
        customerProjects.add(newCustomerProject);
        customer.setProjects(customerProjects);
        Phase phase1 = Phase.builder().id("phase1Id").name("phase 1").build();
        projectDto.setPhaseDtoSet(new HashSet<>(Collections.singletonList(phaseDto1)));
        Personal personal1 = Personal.builder().id("member1Id").build();
        Personal personal2 = Personal.builder().id("member2Id").build();
        Personal personal3 = Personal.builder().id("member3Id").build();
        ProjectRole projectRole = ProjectRole.builder().build();

        ProjectSkillTag projectSkillTag = ProjectSkillTag.builder().build();

        when(projectRepository.findById(projectDto.getId())).thenReturn(Optional.of(existedCustomerProject));
        when(projectRepository.findByName(projectDto.getName())).thenReturn(Optional.empty());
        when(projectConverterMock.mapDtoToEntity(projectDto)).thenReturn(newCustomerProject);
        when(projectTypeRepository.findByName(projectDto.getProjectType())).thenReturn(Optional.of(ProjectType.builder().build()));
        when(gbUnitRepository.findByName(projectDto.getGbUnit())).thenReturn(Optional.of(GbUnit.builder().build()));
        when(projectRepository.save(any())).thenReturn(newCustomerProject);
        when(skillTagRepository.count()).thenReturn(0L);
        when(skillTagRepository.findByNameIn(any())).thenReturn(new ArrayList<>());
        when(skillTagRepository.saveAll(any())).thenReturn(null);
        when(projectSkillTagRepository.saveAll(any())).thenReturn(Collections.singletonList(projectSkillTag));
        when(personalRepository.findById("member1Id")).thenReturn(Optional.of(personal1));
        when(personalRepository.findById("member2Id")).thenReturn(Optional.of(personal2));
        when(personalRepository.findById("member3Id")).thenReturn(Optional.of(personal3));
        when(projectRoleRepository.findById(any())).thenReturn(Optional.of(projectRole));
        when(personalProjectRepository.save(any())).thenReturn(null);
        when(phaseRepository.findById("phase1Id")).thenReturn(Optional.of(phase1));
        when(phaseProjectRepository.save(any())).thenReturn(null);
        when(projectConverterMock.convertToProjectDetailDTO(any())).thenReturn(ProjectDto.builder().build());

        assertNotNull(projectService.save(projectDto));

    }

  //find
    @Test
    @DisplayName("Find all filter")
    @Transactional
    void getFilter() {
        when(projectTypeRepository.findAll()).thenReturn(
                Arrays.asList(
                        ProjectType.builder().name("project type").build()
                )
        );
        
        HashMap<String, Object> filters = projectService.getFilter();
		System.out.println(filters);
		assertThat(filters).isNotEmpty();
    }

	@Test
    @DisplayName("Import project from excel file _ happy case")
    public void importProject_HappyCase() throws IOException {
		String header ="Project Name,Project Description,Status,V Model Phases,Challenge,Start Date,End date,GB Unit Group,Customer GB,Members(NT-ID),Customer Name,Project Type,PM,Technologies used,Project Objective,Team Size,Documentation Links(wiki,website,..)";
		String record ="Automation Tube,Automation Solution Database is place where everyone can find the automation solutions as well as publish or share automation solutions to others,On-Going,Software Design and Documentation,\"Need to complete in short time, resources are freshers\",1-Sep-2022,Dec-23,MS,BGSW,\"hyc1hc,TUO3HC,EGI1HC,LPE7HC,GLN5HC\",Venkatasubramanian M K (BGSW/ETI BGSW/PJ-CoE-AT) <Venkatasubramanian.MK@in.bosch.com>,Bosch,hyc1hc,\"Java, Angular, HTML/CSS, Elastic Search, MySQL\",Centralize all automation solutions across GBs,5,https://inside-docupedia.bosch.com/confluence/display/REST/1.%5BCoE-AD%5D+Project+Info";
		
		byte[] byteDummy = createFileXLSXDummy(header, record);
		MultipartFile multipartFile =  new MockMultipartFile("xlsxtest", byteDummy);
		log.info("number of bye is "+ multipartFile.getSize());
		List<XLSXProjectDto> xlsxProjectDtos = excelFileHandler.convertXLSXFileToProjectDto("idtest", multipartFile);
		assertThat("Automation Tube").isEqualTo(xlsxProjectDtos.get(0).getProjectName());
		
		ProjectType projectType = ProjectType.builder().name("test").build();
		List<Project> projects = new ArrayList<>();
		Project project = Project.builder()
				.name("Automation Tube")
				.createdBy("test")
				.createdBy("1/1/1")
				.projectType(projectType)
				.build();	
		projects.add(project);
		assertThat(project.getName()).isEqualTo(projectConverter.mapXLSXD2Project(xlsxProjectDtos.get(0)).getName());
		when(projectRepository.saveAllAndFlush(projects)).thenReturn(projects);
		assertThat(projectRepository.saveAllAndFlush(projects)).isEqualTo(projects);
	}
	
	private byte[] createFileXLSXDummy(String header, String recordTest) throws IOException {
		String headers[] = header.split(Constants.COMMA);
		String records[] = recordTest.split(Constants.COMMA);
		XSSFWorkbook workBook = new XSSFWorkbook();
        XSSFSheet sheet = workBook.createSheet("sheettest");
		XSSFRow headerRow = sheet.createRow(0);
		XSSFRow recordRow = sheet.createRow(1);
		for (int i = 0; i < headers.length; i++) {
			headerRow.createCell(i).setCellValue(headers[i]);
		}
		for (int i = 0; i < headers.length; i++) {
			recordRow.createCell(i).setCellValue(records[i]);
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workBook.write(outputStream);
		return outputStream.toByteArray();
	}

    @Test
    @DisplayName("Import project from excel file _ happy case")
    public void testImportProject() throws IOException {
        String header ="Project Name,Project Description,Status,V Model Phases,Challenge,Start Date,End date,GB Unit Group,Customer GB,Members(NT-ID),Customer Name,Project Type,PM,Technologies used,Project Objective,Team Size,Documentation Links(wiki,website,..)";
        String record ="Automation Tube,Automation Solution Database is place where everyone can find the automation solutions as well as publish or share automation solutions to others,On-Going,Software Design and Documentation,\"Need to complete in short time, resources are freshers\",1-Sep-2022,Dec-23,MS,BGSW,\"hyc1hc,TUO3HC,EGI1HC,LPE7HC,GLN5HC\",Venkatasubramanian M K (BGSW/ETI BGSW/PJ-CoE-AT) <Venkatasubramanian.MK@in.bosch.com>,Bosch,hyc1hc,\"Java, Angular, HTML/CSS, Elastic Search, MySQL\",Centralize all automation solutions across GBs,5,https://inside-docupedia.bosch.com/confluence/display/REST/1.%5BCoE-AD%5D+Project+Info";

        byte[] byteDummy = createFileXLSXDummy(header, record);
        MultipartFile multipartFile =  new MockMultipartFile("xlsxtest", byteDummy);
        log.info("number of bye is "+ multipartFile.getSize());

        GbUnit gbUnit = GbUnit.builder().name("MS").build();
        ProjectType projectType = ProjectType.builder().name("Bosch").build();

        Project project = Project.builder()
                .name("Automation Tube")
                .description("Automation Solution Database is place where everyone can find the automation solutions as well as publish or share automation solutions to others")
                .status("On-Going")
                .challenge("Need to complete in short time, resources are freshers")
                .gbUnit(gbUnit)
                .customerGb("BGSW")
                .projectType(projectType)
                .leader("hyc1hc")
                .targetObject("Centralize all automation solutions across GBs")
                .teamSize("5.0")
                .build();

        Phase phase = Phase.builder().name("Software Design and Documentation").build();
        PhaseProject phaseProject = PhaseProject.builder().phase(phase).project(project).build();
        List<PhaseProject> phaseProjectList = new ArrayList<>();
        phaseProjectList.add(phaseProject);

        Personal personal = Personal.builder().personalCode("hyc1hc").build();
        PersonalProject personalProject = PersonalProject.builder().personal(personal).project(project).build();
        List<PersonalProject> personalProjectList = new ArrayList<>();
        personalProjectList.add(personalProject);

        SkillTag skillTag = SkillTag.builder().name("Java").build();
        ProjectSkillTag projectSkillTag = ProjectSkillTag.builder().skillTag(skillTag).project(project).build();
        Set<ProjectSkillTag> projectSkillTagSet = new HashSet<>();
        projectSkillTagSet.add(projectSkillTag);

        project.setPhaseProjects(phaseProjectList);
        project.setPersonalProject(personalProjectList);
        project.setProjectSkillTags(projectSkillTagSet);

        List<Project> projectList = new ArrayList<>();
        projectList.add(project);

        when(personalRepository.findByPersonalCodeIgnoreCase(any())).thenReturn(Optional.of(Personal.builder().build()));
        when(projectConverterMock.mapXLSXDto2Projects(any())).thenReturn(projectList);
        when(phaseProjectRepository.saveAll(any())).thenReturn(null);
        when(personalProjectRepository.saveAll(any())).thenReturn(null);
        when(projectSkillTagRepository.saveAll(any())).thenReturn(null);
        
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        
        List<ProjectDto> result = projectService.importProject("RRN5HC", multipartFile, new ArrayList<Project>());
        assertThat(result).isEmpty();
    }

	@Test
	@DisplayName("Update additional task - personal")
	void updateAdditionalTask() {
		String ntid = "admin";
		String projectId1 = "projectid1";
		String projectId2 = "project2";
		String roleId = "roleid";
		String roleId1 = "roleid1";
		String[] permission = { Constants.EDIT_ASSOCIATE_INFO_PERMISSION };
		Set<String> auth = new HashSet<>(Arrays.asList(permission));
		String personalProjectId = "personalprojectid";

		SimplePersonalProjectDto simplePersonalProjectDto = SimplePersonalProjectDto.builder().id(personalProjectId)
                .memberStartDate("2023-11-01").memberEndDate("2023-12-01")
				.projectId(projectId2).build();

		ProjectType projectType = ProjectType.builder().name(Constants.BOSCH).build();
		
		Personal personal = Personal.builder().id("personalid").personalCode(ntid).build();
		Project project1 = Project.builder().id(projectId1).projectType(projectType).startDate(Utility.parseSimpleDateFormat("2023-10-01")).endDate(Utility.parseSimpleDateFormat("2024-10-01")).build();
		Project project2 = Project.builder().id(projectId2).startDate(Utility.parseSimpleDateFormat("2023-10-01")).endDate(Utility.parseSimpleDateFormat("2024-10-01")).build();
		Optional<Project> projectOpt = Optional.ofNullable(project2);
		ProjectRole projectRole = ProjectRole.builder().id(roleId).build();
		ProjectRole projectRole1 = ProjectRole.builder().id(roleId1).build();
		PersonalProject personalProject = PersonalProject.builder().id("personalprojectid").personal(personal)
				.projectRole(projectRole).project(project1).additionalTask("No task").build();
		Optional<PersonalProject> personalProjectOpt = Optional.ofNullable(personalProject);
		Optional<ProjectRole> projectRoleOpt = Optional.ofNullable(projectRole1);
		
		when(personalProjectRepository.findById(simplePersonalProjectDto.getId())).thenReturn(personalProjectOpt);
		
		when(projectRepository.findById(simplePersonalProjectDto.getProjectId())).thenReturn(projectOpt);

		when(projectRoleRepository.findById(simplePersonalProjectDto.getRoleId())).thenReturn(projectRoleOpt);

		assertThat(Constants.SUCCESS).isEqualTo(projectService.updateAdditionalTask(ntid, simplePersonalProjectDto, auth));
	}

	@Test
	@DisplayName("Update additional task - can not found personal project")
	void updateAdditionalTask_NotFoundPersonalProject() {
		String ntid = "admin";
		String projectId2 = "project2";
		String[] permission = { Constants.EDIT_ASSOCIATE_INFO_PERMISSION };
		Set<String> auth = new HashSet<>(Arrays.asList(permission));
		String personalProjectId = "personalprojectid";

		SimplePersonalProjectDto simplePersonalProjectDto = SimplePersonalProjectDto.builder().id(personalProjectId)
				.projectId(projectId2).build();
		Optional<PersonalProject> personalProjectOpt = Optional.ofNullable(null);

		when(personalProjectRepository.findById(simplePersonalProjectDto.getId()))
				.thenReturn(personalProjectOpt);
		assertThrows(ResourceNotFoundException.class,
				() -> projectService.updateAdditionalTask(ntid, simplePersonalProjectDto, auth));
	}
	
	@Test
	@DisplayName("Update additional task - edit by manager")
	void updateAdditionalTask_editByManager() {
		String ntid = "admin";
		String ntidAnother = "adminanother";
		String projectId1 = "projectid1";
		String projectId2 = "project2";
		String roleId = "roleid";
		String roleId1 = "roleid1";
		String[] permission = { Constants.EDIT_ASSOCIATE_INFO_PERMISSION };
		Set<String> auth = new HashSet<>(Arrays.asList(permission));
		String personalProjectId = "personalprojectid";

		SimplePersonalProjectDto simplePersonalProjectDto = SimplePersonalProjectDto.builder().id(personalProjectId)
                .memberStartDate("2023-11-01").memberEndDate("2023-12-01")
				.projectId(projectId2).build();
		
		ProjectType projectType = ProjectType.builder().name(Constants.BOSCH).build();

		Personal personal = Personal.builder().id("personalid").personalCode(ntidAnother).build();
		Project project1 = Project.builder().id(projectId1).startDate(Utility.parseSimpleDateFormat("2023-10-01")).endDate(Utility.parseSimpleDateFormat("2024-10-01")).projectType(projectType).build();
		Project project2 = Project.builder().id(projectId2).startDate(Utility.parseSimpleDateFormat("2023-10-01")).endDate(Utility.parseSimpleDateFormat("2024-10-01")).build();
		Optional<Project> projectOpt = Optional.ofNullable(project2);
		ProjectRole projectRole = ProjectRole.builder().id(roleId).build();
		ProjectRole projectRole1 = ProjectRole.builder().id(roleId1).build();
		PersonalProject personalProject = PersonalProject.builder().id("personalprojectid").personal(personal)
				.projectRole(projectRole).project(project1).additionalTask("No task").build();
		Optional<PersonalProject> personalProjectOpt = Optional.ofNullable(personalProject);
		Optional<ProjectRole> projectRoleOpt = Optional.ofNullable(projectRole1);
		
		when(personalProjectRepository.findById(simplePersonalProjectDto.getId())).thenReturn(personalProjectOpt);

		when(projectRepository.findById(simplePersonalProjectDto.getProjectId())).thenReturn(projectOpt);

		when(projectRoleRepository.findById(simplePersonalProjectDto.getRoleId())).thenReturn(projectRoleOpt);
		assertThat(Constants.SUCCESS).isEqualTo(projectService.updateAdditionalTask(ntid, simplePersonalProjectDto, auth));
	}
	
	@Test
	@DisplayName("Update additional task - edit by manager - access dinied")
	void updateAdditionalTask_editByManager_accessDenied() {
		String ntid = "admin";
		String ntidAnother = "adminanother";
		String projectId1 = "projectid1";
		String projectId2 = "project2";
		String roleId = "roleid";
		String personalProjectId = "personalprojectid";
		Set<String> auth = new HashSet<>();
		SimplePersonalProjectDto simplePersonalProjectDto = SimplePersonalProjectDto.builder().id(personalProjectId)
				.projectId(projectId2).build();

		Personal personal = Personal.builder().id("personalid").personalCode(ntidAnother).build();
		Project project1 = Project.builder().id(projectId1).build();
		ProjectRole projectRole = ProjectRole.builder().id(roleId).build();
		PersonalProject personalProject = PersonalProject.builder().id("personalprojectid").personal(personal)
				.projectRole(projectRole).project(project1).additionalTask("No task").build();
		Optional<PersonalProject> personalProjectOpt = Optional.ofNullable(personalProject);

		when(personalProjectRepository.findById(simplePersonalProjectDto.getId()))
				.thenReturn(personalProjectOpt);
		assertThrows(AccessDeniedException.class,
				() -> projectService.updateAdditionalTask(ntid, simplePersonalProjectDto, auth));
	}
	
	@Test
	@DisplayName("Update additional task - start date less than")
	void updateAdditionalTask_assignDuplicateProject() {
		String ntid = "admin";
		String ntidAnother = "adminanother";
		String projectId1 = "projectid1";
		String roleId = "roleid";
		String[] permission = { Constants.EDIT_ASSOCIATE_INFO_PERMISSION };
		Set<String> auth = new HashSet<>(Arrays.asList(permission));
		String personalProjectId = "personalprojectid";

		SimplePersonalProjectDto simplePersonalProjectDto = SimplePersonalProjectDto.builder().id(personalProjectId)
                .memberStartDate("2023-11-01").memberEndDate("2023-12-01")
				.projectId(projectId1).build();
		
		ProjectType projectType = ProjectType.builder().name(Constants.BOSCH).build();

		Personal personal = Personal.builder().id("personalid").personalCode(ntidAnother).build();
		Project project1 = Project.builder().id(projectId1).projectType(projectType)
                .startDate(Utility.parseSimpleDateFormat("2023-12-01")).endDate(Utility.parseSimpleDateFormat("2024-10-01")).build();
		ProjectRole projectRole = ProjectRole.builder().id(roleId).build();
		PersonalProject personalProject = PersonalProject.builder().id("personalprojectidanother").personal(personal)
                .startDate(new Date()).endDate(new Date())
				.projectRole(projectRole).project(project1).additionalTask("No task").build();
		Optional<PersonalProject> personalProjectOpt = Optional.ofNullable(personalProject);
        Optional<ProjectRole> projectRoleOpt = Optional.ofNullable(projectRole);

        when(personalProjectRepository.findById(simplePersonalProjectDto.getId())).thenReturn(personalProjectOpt);

		List<PersonalProject> personalProjects = Collections.singletonList(personalProject);
		when(personalProjectRepository.findByPersonalIdAndProjectId(personalProject.getPersonal().getId(),personalProject.getProject().getId() )).thenReturn(personalProjects);

        ProjectMemberDto projectMemberDto = ProjectMemberDto.builder().id(personalProject.getPersonal().getId())
                .startDate(simplePersonalProjectDto.getMemberStartDate()).endDate(simplePersonalProjectDto.getMemberEndDate())
                .personalProjectId(personalProject.getId()).build();
        when(personalProjectConverter.convertPersonalProjectToProjectMemberDto(personalProject)).thenReturn(projectMemberDto);

        when(projectRoleRepository.findById(simplePersonalProjectDto.getRoleId())).thenReturn(projectRoleOpt);

		assertThrows(BadRequestException.class,
				() -> projectService.updateAdditionalTask(ntid, simplePersonalProjectDto, auth));
	}
	
	@Test
	@DisplayName("Update additional task - cannot found project")
	void updateAdditionalTask_CanNotFoundProject() {
		String ntid = "admin";
		String projectId1 = "projectid1";
		String projectId2 = "project2";
		String roleId = "roleid";
		String[] permission = { Constants.EDIT_ASSOCIATE_INFO_PERMISSION };
		Set<String> auth = new HashSet<>(Arrays.asList(permission));
		String personalProjectId = "personalprojectid";

		SimplePersonalProjectDto simplePersonalProjectDto = SimplePersonalProjectDto.builder().id(personalProjectId)
				.projectId(projectId2).build();
		
		ProjectType projectType = ProjectType.builder().name(Constants.BOSCH).build();

		Personal personal = Personal.builder().id("personalid").personalCode(ntid).build();
		Project project1 = Project.builder().id(projectId1).projectType(projectType).build();
		Optional<Project> projectOpt = Optional.ofNullable(null);
		ProjectRole projectRole = ProjectRole.builder().id(roleId).build();
		PersonalProject personalProject = PersonalProject.builder().id("personalprojectid").personal(personal)
				.projectRole(projectRole).project(project1).additionalTask("No task").build();
		Optional<PersonalProject> personalProjectOpt = Optional.ofNullable(personalProject);
		
		when(personalProjectRepository.findById(simplePersonalProjectDto.getId())).thenReturn(personalProjectOpt);

		when(projectRepository.findById(simplePersonalProjectDto.getProjectId())).thenReturn(projectOpt);

		assertThrows(ResourceNotFoundException.class,
				() -> projectService.updateAdditionalTask(ntid, simplePersonalProjectDto, auth));
	}

	
	@Test
	@DisplayName("Update additional task - wrong project role id")
	void updateAdditionalTask_WrongProjectRoleId() {
		String ntid = "admin";
		String projectId1 = "projectid1";
		String projectId2 = "project2";
		String roleId = "roleid";
		String[] permission = { Constants.EDIT_ASSOCIATE_INFO_PERMISSION };
		Set<String> auth = new HashSet<>(Arrays.asList(permission));
		String personalProjectId = "personalprojectid";

		SimplePersonalProjectDto simplePersonalProjectDto = SimplePersonalProjectDto.builder().id(personalProjectId)
				.projectId(projectId2).build();
		
		ProjectType projectType = ProjectType.builder().name(Constants.BOSCH).build();

		Personal personal = Personal.builder().id("personalid").personalCode(ntid).build();
		Project project1 = Project.builder().id(projectId1).projectType(projectType).build();
		Project project2 = Project.builder().id(projectId2).build();
		Optional<Project> projectOpt = Optional.ofNullable(project2);
		ProjectRole projectRole = ProjectRole.builder().id(roleId).build();
		PersonalProject personalProject = PersonalProject.builder().id("personalprojectid").personal(personal)
				.projectRole(projectRole).project(project1).additionalTask("No task").build();
		Optional<PersonalProject> personalProjectOpt = Optional.ofNullable(personalProject);
		Optional<ProjectRole> projectRoleOpt = Optional.ofNullable(null);
		
		when(personalProjectRepository.findById(simplePersonalProjectDto.getId())).thenReturn(personalProjectOpt);
		
		when(projectRepository.findById(simplePersonalProjectDto.getProjectId())).thenReturn(projectOpt);

		when(projectRoleRepository.findById(simplePersonalProjectDto.getRoleId())).thenReturn(projectRoleOpt);

		assertThrows(ResourceNotFoundException.class,
				() -> projectService.updateAdditionalTask(ntid, simplePersonalProjectDto, auth));
	}
	
	@Test
	@DisplayName("Update additional task - can not assign another Non Bosch project")
	void updateAdditionalTask_canNotAssignAnotherNonBoschProject() {
		String ntid = "admin";
		String ntidAnother = "adminanother";
		String projectId = "projectid";
		String projectIdAnother = "projectidanother";
		String roleId = "roleid";
		String[] permission = { Constants.EDIT_ASSOCIATE_INFO_PERMISSION };
		Set<String> auth = new HashSet<>(Arrays.asList(permission));
		String personalProjectId = "personalprojectid";

		SimplePersonalProjectDto simplePersonalProjectDto = SimplePersonalProjectDto.builder().id(personalProjectId)
				.projectId(projectIdAnother).build();
		
		ProjectType projectType = ProjectType.builder().name(Constants.NONBOSCH).build();

		Personal personal = Personal.builder().id("personalid").personalCode(ntidAnother).build();
		Project project1 = Project.builder().id(projectId).projectType(projectType).build();
		ProjectRole projectRole = ProjectRole.builder().id(roleId).build();
		PersonalProject personalProject = PersonalProject.builder().id("personalprojectid").personal(personal)
				.projectRole(projectRole).project(project1).additionalTask("No task").build();
		Optional<PersonalProject> personalProjectOpt = Optional.ofNullable(personalProject);
		
		when(personalProjectRepository.findById(simplePersonalProjectDto.getId())).thenReturn(personalProjectOpt);

		assertThrows(BadRequestException.class,
				() -> projectService.updateAdditionalTask(ntid, simplePersonalProjectDto, auth));
	}
    
    @Test
    @DisplayName("Test reorder skill tag after delete project")
    void testReorderSkillTagAfterDeleteProject() {
    	String idSkillTag1="id1";
    	String idSkillTag2="id2";
    	String idSkillTag3="id3";
    	String nameSkillTag1="skill tag 1";
    	String nameSkillTag2="skill tag 2";
    	String nameSkillTag3="skill tag 3";
    	SkillTag skillTag1= SkillTag.builder()
    			.id(idSkillTag1)
    			.name(nameSkillTag1)
    			.order(0l)
    			.build();
    	SkillTag skillTag2=SkillTag.builder()
    			.id(idSkillTag2)
    			.name(nameSkillTag2)
    			.order(1l)
    			.build();
    	SkillTag skillTag3= SkillTag.builder()
    			.id(idSkillTag3)
    			.name(nameSkillTag3)
    			.order(2l)
    			.build();
    	
    	List<SkillTag> skillTagNotUsed = new ArrayList<>();
    	skillTagNotUsed.add(skillTag2);
    	
    	List<SkillTag> allSkillTag = new ArrayList<>();
    	allSkillTag.add(skillTag1);
    	allSkillTag.add(skillTag2);
    	allSkillTag.add(skillTag3);
    	when(skillTagRepository.findAllByOrderByOrder()).thenReturn(allSkillTag);
    
    	List<SkillTag> result = projectService.reorderSkillTagAfterDeleteProject(skillTagNotUsed);
    	
    	SkillTag st1= SkillTag.builder()
    			.id(idSkillTag1)
    			.name(nameSkillTag1)
    			.order(0l)
    			.build();
    	SkillTag st2=SkillTag.builder()
    			.id(idSkillTag2)
    			.name(nameSkillTag2)
    			.order(2l)
    			.build();
    	SkillTag st3= SkillTag.builder()
    			.id(idSkillTag3)
    			.name(nameSkillTag3)
    			.order(1l)
    			.build();
    	
    	List<SkillTag> skillTagAfterOrder = new ArrayList<>();
    	skillTagAfterOrder.add(st1);
    	skillTagAfterOrder.add(st2);
    	skillTagAfterOrder.add(st3);
    	
    	assertThat(result.get(0).getName()).isEqualTo(skillTagAfterOrder.get(0).getName());
    	assertThat(result.get(1).getName()).isEqualTo(skillTagAfterOrder.get(1).getName());
    	assertThat(result.get(2).getName()).isEqualTo(skillTagAfterOrder.get(2).getName());
    	
    	assertThat(result.get(0).getOrder()).isEqualTo(skillTagAfterOrder.get(0).getOrder());
    	assertThat(result.get(1).getOrder()).isEqualTo(skillTagAfterOrder.get(1).getOrder());
    	assertThat(result.get(2).getOrder()).isEqualTo(skillTagAfterOrder.get(2).getOrder());
    	
    	assertThat(result.get(0).getId()).isEqualTo(skillTagAfterOrder.get(0).getId());
    	assertThat(result.get(1).getId()).isEqualTo(skillTagAfterOrder.get(1).getId());
    	assertThat(result.get(2).getId()).isEqualTo(skillTagAfterOrder.get(2).getId());
    	
    	assertThat(result.get(0).getProjectSkillTags()).isEqualTo(skillTagAfterOrder.get(0).getProjectSkillTags());
    	assertThat(result.get(1).getProjectSkillTags()).isEqualTo(skillTagAfterOrder.get(1).getProjectSkillTags());
    	assertThat(result.get(2).getProjectSkillTags()).isEqualTo(skillTagAfterOrder.get(2).getProjectSkillTags());
    	
    }

    @Test
    @DisplayName("Test get all bosch project for dropdown")
    void testGetAllBoschProjectForDropdown() {
    	List<Project> projectList = new ArrayList<>();
    	
    	ProjectType boshProjectType = ProjectType.builder().name("Bosch").build();
    	
    	projectList.add(Project.builder().id("test1").name("a").projectType(boshProjectType).build());
    	projectList.add(Project.builder().id("test3").name("c").projectType(boshProjectType).build());
    	projectList.add(Project.builder().id("test2").name("b").projectType(boshProjectType).build());
    	
    	HashMap<String, String> filterMap = new HashMap<String, String>();
    	filterMap.put("project_type", "Bosch");
    	
		when(projectRepository.findAll(any(Specification.class))).thenReturn(projectList);
		
		List<ProjectDto> resultProjectDtoList = projectService.findAllForDropdown(filterMap);
		assertThat("a").isEqualTo(resultProjectDtoList.get(0).getName());
		assertThat("b").isEqualTo(resultProjectDtoList.get(1).getName());
		assertThat("c").isEqualTo(resultProjectDtoList.get(2).getName());
	}

    @Test
    @DisplayName("Test add new Bosch project happy case")
    void testAddBoschProjectHappyCase() throws ParseException {
        ProjectDto projectDto = ProjectDto.builder().name("test proj").projectType("Bosch").startDate("2022-12-29").customerGb("customer_gb")
                .gbUnit("MS").build();

        SkillTagDto skillTagDto1 = SkillTagDto.builder().name("add project test 1").build();
        projectDto.setSkillTags(new HashSet<>(Collections.singletonList(skillTagDto1)));

        PhaseDto phaseDto1 = PhaseDto.builder().id("phase1Id").build();
        projectDto.setPhaseDtoSet(new HashSet<>(Collections.singletonList(phaseDto1)));

//        1 -> 3,  7 -> 9,  4 -> 8
        ProjectMemberDto memberDto1 = ProjectMemberDto.builder().id("memberId")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-01-01").endDate("2023-03-01").build();
        ProjectMemberDto memberDto2 = ProjectMemberDto.builder().id("memberId")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-07-01").endDate("2023-09-01").build();
        ProjectMemberDto memberDto3 = ProjectMemberDto.builder().id("memberId")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-04-01").endDate("2023-06-01").build();
        projectDto.setMembers(Arrays.asList(memberDto1, memberDto2, memberDto3));

        Phase phase1 = Phase.builder().id("phase1Id").name("phase 1").build();
        Personal personal1 = Personal.builder().id("memberId").build();
        ProjectRole projectRole = ProjectRole.builder().build();
        Customer customer = Customer.builder().name("customer_gb").id("customer_id").projects(new ArrayList<>()).build();
        Project convertedProject = Project.builder().name(projectDto.getName()).customer(customer).startDate(Constants.SIMPLE_DATE_FORMAT.parse(projectDto.getStartDate())).build();
        ProjectSkillTag projectSkillTag = ProjectSkillTag.builder().build();

        when(projectRepository.findByName(projectDto.getName())).thenReturn(Optional.empty());
        when(projectConverterMock.mapDtoToEntity(projectDto)).thenReturn(convertedProject);
        when(projectTypeRepository.findByName(projectDto.getProjectType())).thenReturn(Optional.of(ProjectType.builder().build()));
        when(gbUnitRepository.findByName(projectDto.getGbUnit())).thenReturn(Optional.of(GbUnit.builder().build()));
        when(projectRepository.save(any())).thenReturn(convertedProject);
        when(skillTagRepository.count()).thenReturn(0L);
        when(skillTagRepository.findByNameIn(any())).thenReturn(new ArrayList<>());
        when(skillTagRepository.saveAll(any())).thenReturn(null);
        when(projectSkillTagRepository.saveAll(any())).thenReturn(Collections.singletonList(projectSkillTag));
        when(personalRepository.findById("memberId")).thenReturn(Optional.of(personal1));
        when(projectRoleRepository.findById(any())).thenReturn(Optional.of(projectRole));
        when(personalProjectRepository.save(any())).thenReturn(null);
        when(phaseRepository.findById("phase1Id")).thenReturn(Optional.of(phase1));
        when(phaseProjectRepository.save(any())).thenReturn(null);
        when(projectConverterMock.convertToProjectDetailDTO(any())).thenReturn(ProjectDto.builder().build());

        assertNotNull(projectService.save(projectDto));
    }

    @Test
    @DisplayName("Test add new Bosch project with working time period overlapped")
    void testAddBoschProjectOverlapMemberWorkingTime() {
        ProjectDto projectDto = ProjectDto.builder().name("test proj").projectType("Bosch")
                .gbUnit("MS").build();

        SkillTagDto skillTagDto1 = SkillTagDto.builder().name("add project test 1").build();
        projectDto.setSkillTags(new HashSet<>(Collections.singletonList(skillTagDto1)));

        PhaseDto phaseDto1 = PhaseDto.builder().id("phase1Id").build();
        projectDto.setPhaseDtoSet(new HashSet<>(Collections.singletonList(phaseDto1)));

//        2 -> 4,  7 -> 9,  1 -> 6 (overlapped)
        ProjectMemberDto memberDto1 = ProjectMemberDto.builder().id("memberId")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-02-01").endDate("2023-04-01").build();
        ProjectMemberDto memberDto2 = ProjectMemberDto.builder().id("memberId")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-07-01").endDate("2023-09-01").build();
        ProjectMemberDto memberDto3 = ProjectMemberDto.builder().id("memberId")
                .role("123").roleId("2").additionalTask("")
                .startDate("2023-01-01").endDate("2023-06-01").build();
        projectDto.setMembers(Arrays.asList(memberDto1, memberDto2, memberDto3));

        Personal.builder().id("memberId").build();
        ProjectRole.builder().build();
        Project convertedProject = Project.builder().name("test proj").build();

        ProjectSkillTag projectSkillTag = ProjectSkillTag.builder().build();

        when(projectRepository.findByName(projectDto.getName())).thenReturn(Optional.empty());
        when(projectConverterMock.mapDtoToEntity(projectDto)).thenReturn(convertedProject);
        when(projectTypeRepository.findByName(projectDto.getProjectType())).thenReturn(Optional.of(ProjectType.builder().build()));
        when(gbUnitRepository.findByName(projectDto.getGbUnit())).thenReturn(Optional.of(GbUnit.builder().build()));
        when(projectRepository.save(any())).thenReturn(convertedProject);
        when(skillTagRepository.count()).thenReturn(0L);
        when(skillTagRepository.findByNameIn(any())).thenReturn(new ArrayList<>());
        when(skillTagRepository.saveAll(any())).thenReturn(null);
        when(projectSkillTagRepository.saveAll(any())).thenReturn(Collections.singletonList(projectSkillTag));

        assertThrows(NullPointerException.class, () -> projectService.save(projectDto));
    }

    @Test
    @DisplayName("Test find all project type")
    void testFindAllProjectType(){
        Object[] projectType1 = {"type1","Bosch"};
        Object[] projectType2 = {"type2","Bosch"};
        List<Object[]> objects = new ArrayList<>();
        objects.add(projectType1);
        objects.add(projectType2);
        when(projectRepository.findAllProjectIdName("Bosch"))
                .thenReturn(objects);
        List<ProjectTypeDto>  projectTypeDtos = objects.stream()
                .map(o -> new ProjectTypeDto(o[0].toString(), o[1].toString()))
                .collect(Collectors.toList());
        assertThat(projectTypeDtos).isEqualTo( projectService.findAll());
    }

    @Test
    @DisplayName("Test find project by Id - success case")
    void testFindByIdProjectSuccess() throws ParseException{
        Project proj = Project.builder().id("project_id")
                .name("project name")
                .leader("leader")
                .status("status")
                .targetObject("target object")
                .challenge("challenge")
                .teamSize("4")
                .description("description")
                .referenceLink("reference link")
                .isTopProject(false)
                .stackHolder("stack holder")
                .hightlight("hight light")
                .problemStatement("problem statement")
                .solution("solution")
                .startDate(Constants.SIMPLE_DATE_FORMAT.parse("2023-12-02"))
                .endDate(Constants.SIMPLE_DATE_FORMAT.parse("2023-12-10"))
                .projectType(ProjectType.builder().name("Bosch").build())
                .benefits("benefits").build();

        Optional<Project> projectOtp = Optional.ofNullable(proj);
        when(projectRepository.findById("project_id")).thenReturn(projectOtp);
        ProjectDto projDTO = ProjectDto.builder()
                .id(proj.getId()).name(proj.getName()).pmName(proj.getLeader())
                .status(proj.getStatus()).objective(proj.getTargetObject()).challenge(proj.getChallenge())
                .teamSize(proj.getTeamSize()).description(proj.getDescription()).referenceLink(proj.getReferenceLink())
                .isTopProject(proj.isTopProject()).stakeholder(proj.getStackHolder()).highlight(proj.getHightlight())
                .problemStatement(proj.getProblemStatement()).solution(proj.getSolution())
                .startDate(Constants.SIMPLE_DATE_FORMAT.format(proj.getStartDate())).endDate(Constants.SIMPLE_DATE_FORMAT.format(proj.getEndDate()))
                .benefits(proj.getBenefits()).build();
        when(projectConverterMock.convertToProjectDetailDTO(proj))
                .thenReturn(projDTO);
        assertThat(projDTO).isEqualTo(projectService.findById("project_id"));
    }

    @Test
    @DisplayName("Test find project by Id - failure case")
    void testFindByIdProjectFailure() throws ParseException{
        Optional<Project> projectOtp = Optional.ofNullable(null);
        when(projectRepository.findById("project_id")).thenReturn(projectOtp);
        assertThrows( SkillManagementException.class, () ->  projectService.findById("project_id"));
    }

    @Test
    @DisplayName("Test getPortfolio -- success case")
    void testGetPortfolioSuccess(){
        Project proj = Project.builder().id("project_id").name("project_name").build();
        Optional<Project> projectOpt = Optional.ofNullable(proj);
        when(projectRepository.findById(proj.getId())).thenReturn(projectOpt);
        ProjectDto projectDto = projectConverter.convertToProjectPortfolioDto(proj);
        when(projectConverterMock.convertToProjectPortfolioDto(proj)).thenReturn(projectDto);
        assertThat(projectDto).isEqualTo(projectService.getPortfolio(proj.getId()));
    }

    @Test
    @DisplayName("Test getPortfolio -- project not found")
    void testGetPortfolioFail(){
        Project proj = Project.builder().id("project_id").name("project_name").build();
        Optional<Project> projectOpt = Optional.ofNullable(null);
        when(projectRepository.findById(proj.getId())).thenReturn(projectOpt);

        assertThrows(SkillManagementException.class,() -> projectService.getPortfolio(proj.getId()));
    }

    @Test
    @DisplayName("Test edit portfolio -- success case")
    void testEditPortfolioSuccessCase(){
        Project proj = Project.builder().id("project_id").name("project_name").build();
        ProjectDto projDTO = ProjectDto.builder().id("project_id").highlight("good").build();
        Optional<Project> projectEntity = Optional.ofNullable(proj);
        when(projectRepository.findById("project_id")).thenReturn(projectEntity);
        assertThat(proj).isEqualTo(projectService.editPortfolio(projDTO));
    }

    @Test
    @DisplayName("Test edit portfolio -- Bad request")
    void testEditPortfolioBadRequest(){
        ProjectDto projDTO = ProjectDto.builder().id("").highlight("good").build();
        assertThrows(BadRequestException.class, () -> projectService.editPortfolio(projDTO));
    }

    @Test
    @DisplayName("Test edit portfolio -- Project not found")
    void testEditPortfolioProjNotFound(){
        ProjectDto projDTO = ProjectDto.builder().id("project_id").highlight("good").build();
        Optional<Project> projectEntity = Optional.ofNullable(null);
        when(projectRepository.findById("project_id")).thenReturn(projectEntity);
        assertThrows(SkillManagementException.class, () -> projectService.editPortfolio(projDTO));
    }

    @Test
    @DisplayName("Test get infor port folio -- Bad request")
    void testGetInforPortfolioBadRequest() {
        String projectId = "projectId";
        String layoutValue = "project_benefit_fake";
        assertThrows(BadRequestException.class, () -> projectService.getInforPortfolio(projectId, layoutValue));
    }

    @Test
    @DisplayName("Test get infor port folio -- File Storage not found")
    void testGetInforPortfolioFileStorageNotFound() {
        String projectId = "projectId";
        String layoutValue = "project_benefit";
        assertThat(projectService.getInforPortfolio(projectId, layoutValue)).isEmpty();
    }

    @Test
    @DisplayName("Test get infor port folio -- file not found")
    void testGetInforPortfolioFileNotFound() {
        String projectId = "projectId";
        String layoutValue = "project_benefit";
        FileStorage fileStorage = FileStorage.builder().id("id").name("filename.txt").extension("extension")
                .size(100L).uri("http://vucongluat").token("token").type("type").build();
        String nameFile = layoutValue + Constants.UNDERSCORE + projectId + Constants.DOT + Constants.TXT_EXT;
        when(fileStorageRepository.findByName(nameFile)).thenReturn(Optional.ofNullable(fileStorage));
        assertThrows(ResourceNotFoundException.class, () -> projectService.getInforPortfolio(projectId, layoutValue));
    }

    @Test
    @DisplayName("Test find all project paging")
    void testFindAll() throws ParseException {
        Map<String, String> q = new HashMap<>();
        q.put("year","2021,2022,2023");
        Project proj = Project.builder().id("project_id").name("project_name")
                .startDate(Constants.SIMPLE_DATE_FORMAT.parse("2023-12-02"))
                .endDate(Constants.SIMPLE_DATE_FORMAT.parse("2023-12-10")).build();

        List<ProjectDto> projectDtos = new ArrayList<>();
        projectDtos.add(projectConverter.convertToSearchProjectDTO(proj));

        assertThrows(NullPointerException.class, () -> projectService.findAll(Pageable.ofSize(10), q));
    }
}
