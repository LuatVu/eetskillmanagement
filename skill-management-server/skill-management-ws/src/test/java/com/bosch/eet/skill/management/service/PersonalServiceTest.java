package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.converter.PersonalConverter;
import com.bosch.eet.skill.management.converter.SkillConverter;
import com.bosch.eet.skill.management.dto.CourseDto;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.PersonalProjectDto;
import com.bosch.eet.skill.management.dto.SkillDto;
import com.bosch.eet.skill.management.elasticsearch.repo.ProjectElasticRepository;
import com.bosch.eet.skill.management.entity.Course;
import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalCourse;
import com.bosch.eet.skill.management.entity.PersonalProject;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.TrainingCourse;
import com.bosch.eet.skill.management.exception.EETResponseException;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.repo.CourseRepository;
import com.bosch.eet.skill.management.repo.DepartmentRepository;
import com.bosch.eet.skill.management.repo.LevelRepository;
import com.bosch.eet.skill.management.repo.PersonalCourseRepository;
import com.bosch.eet.skill.management.repo.PersonalProjectRepository;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.PersonalSkillRepository;
import com.bosch.eet.skill.management.repo.ProjectRepository;
import com.bosch.eet.skill.management.repo.ProjectSkillTagRepository;
import com.bosch.eet.skill.management.repo.SkillGroupRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;
import com.bosch.eet.skill.management.repo.TrainingCourseRepository;
import com.bosch.eet.skill.management.service.impl.PersonalServiceImpl;
import com.bosch.eet.skill.management.specification.PersonalSpecification;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@ExtendWith(MockitoExtension.class)
class PersonalServiceTest {

    @InjectMocks
    PersonalServiceImpl personalService;

    @Mock
    PersonalRepository personalRepository;

    @Mock
    SkillRepository skillRepository;

    @Mock
    SkillGroupRepository skillGroupRepository;

    @Mock
    PersonalConverter personalConverter;

    @Mock
    LevelRepository levelRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PersonalSkillRepository personalSkillRepository;

    @MockBean
    private SkillConverter skillConverter;


    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TrainingCourseRepository trainingCourseRepository;

    @Mock
    private PersonalCourseRepository personalCourseRepository;
    
    @Mock
    private PersonalProjectRepository personalProjectRepository;
    
    @Mock
    private MessageSource messageSource;
    
    @Mock
    private ProjectSkillTagRepository projectSkillTagRepository;
    
    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private ProjectElasticRepository projectElasticRepository;

    final String jsonResponse = "    {\n" +
            "        \"id\": \"cb43b124-e79c-40e5-91fb-a7b1c1a16328\",\n" +
            "        \"name\": \"Tran Minh Giang\",\n" +
            "        \"code\": \"TGI2HC\",\n" +
            "        \"skill\": \"Java,NodeJs\",\n" +
            "        \"level\": \"50\",\n" +
            "        \"team\": \"MS/EET23\",\n" +
            "        \"skill_group\": \"Backend,Frontend\",\n" +
            "        \"total_experienced\": \"4\"\n" +
            "    }";
    private static final String DEFAULT_PERSONAL_LEVEL = "f1f7a51a-5b92-414e-afd1-0bf59a827077";

    @Test
    @DisplayName("findAll happy case")
    void findAdd_whenPersonalRepoReturnPersonal_thenReturnPersonal() {
        Pageable pageable = PageRequest.of(0, 5);
        HashMap<String, String> map = new HashMap<>();
        Specification<Personal> specification = PersonalSpecification.search(map);

        List<Personal> dummyEntityList = new ArrayList<>();
        Personal dummyEntity = Personal.builder()
                .id("id")
                .personalNumber("personalNumber")
//                .manager("manager")
                .personalCode("personalCode")
                .level(Level.builder().build())
                .title("title")
                .build();
        dummyEntityList.add(dummyEntity);
        Page<Personal> page = new PageImpl<>(dummyEntityList);

        when(personalRepository.findAll(specification, pageable)).thenReturn(
                page
        );

        Mockito.mockStatic(PersonalSpecification.class);
        when(PersonalSpecification.search(map)).thenReturn(
                specification
        );

        List<PersonalDto> dummyList = new ArrayList<>();
        PersonalDto dummyPersonalDto = new Gson().fromJson(jsonResponse, PersonalDto.class);
        dummyList.add(dummyPersonalDto);
        Page<PersonalDto> pagePersonalDto = new PageImpl<>(dummyList);
        when(personalConverter.convertToSearchDTO(dummyEntity)).thenReturn(
                dummyPersonalDto
        );
        assertThat(personalService.findAll(pageable, map)).isEqualTo(pagePersonalDto);
    }
    
    @Test
    @DisplayName("Update personal skill")
    public void updatePersonalSkill_returnPersonal() {
        Personal personal = Personal.builder()
                .id("1")
                .personalNumber("123456")
                .build();
        SkillDto dummySkill = SkillDto.builder()
                .id("1")
                .name("Java")
                .competency("test")
                .level("2")
                .experienceNumber(7)
                .build();
        Skill skill = new Skill();

        when(skillRepository.findById(dummySkill.getId())).thenReturn(
                Optional.of(skill)
        );

        assertDoesNotThrow(() -> personalService.savePersonalSkill(personal, dummySkill));
    }
    
    @Test
    @DisplayName("Update personal course")
    public void updatePersonalCourse_returnPersonal() {
        Personal personal = Personal.builder()
                .id("1")
                .personalNumber("123456")
                .build();
        CourseDto dummyCourseDto = CourseDto.builder()
                .id("1")
                .name("This is a test")
                .courseType("Self-study")
                .startDate("2021-03-10")
                .endDate("2021-04-11")
                .status("Ongoing")
                .build();
        new Course();
        TrainingCourse trainingCourse = new TrainingCourse();
        List<CourseDto> courseDtos = new ArrayList<>();
        courseDtos.add(dummyCourseDto);
        List<PersonalCourse> personalCourses = new ArrayList<>();
        personalCourses.add(new PersonalCourse());

//        when(courseRepository.findById(dummyCourseDto.getId())).thenReturn(Optional.of(course));
        when(trainingCourseRepository.findById(dummyCourseDto.getId())).thenReturn(Optional.of(trainingCourse));
        when(personalCourseRepository.saveAll(anyList())).thenReturn(personalCourses);
        assertDoesNotThrow(() -> personalService.savePersonalCourses(personal, courseDtos));
    }



    @Test
    @DisplayName("findAll empty case")
    void findAll_whenPersonalRepoReturnEmpty_thenReturnEmpty() {
        Pageable pageable = PageRequest.of(1, 5);
        Map<String, String> filterParams = new HashMap<String, String>();
        filterParams.put("exp", "0");
        List<Personal> dummyEntityList = new ArrayList<>();
        Page<Personal> page = new PageImpl<>(dummyEntityList);
        when(personalRepository.findAll(pageable)).thenReturn(
                page
        );
        List<PersonalDto> dummyList = new ArrayList<>();
        Page<PersonalDto> pagePersonalDto = new PageImpl<>(dummyList);
        assertThat(personalService.findAll(pageable, filterParams)).isEqualTo(pagePersonalDto);
    }
    
    /**
     * @author DUP5HC
     */
//    Unit test for upload Avatar API
    @Test
    @DisplayName("save Associate avatar success")
    void uploadAvatar_returnPersonal() {
        PersonalDto personalDto = new PersonalDto();
        byte[] avatar = new byte[0]; 
        String personalPicture = Base64.getEncoder().encodeToString(avatar);
        Personal personal = new Personal();
        personal.setId("123");
        personal.setPicture(personalPicture);
        
        personalDto.setId("123");
        personalDto.setPicture(personalPicture);

        when(personalRepository.findById(personalDto.getId())).thenReturn(Optional.of(personal));
        when(personalConverter.convertToDTO(personal)).thenReturn(personalDto);
        PersonalDto actualResult = assertDoesNotThrow(()->personalService.saveImage(personalDto.getId(), avatar));
        
        assertThat(personalDto.getId()).isEqualTo(actualResult.getId());
        assertThat(personalDto.getPicture()).isEqualTo(actualResult.getPicture());
    }
    
    @Test
    @DisplayName("save Associate avatar fail")
    void uploadAvatar_associateNotFound() {
        PersonalDto personalDto = new PersonalDto();
        byte[] avatar = new byte[0]; 
        String personalPicture = Base64.getEncoder().encodeToString(avatar);
        Personal personal = new Personal();
        personal.setId("123");
        personal.setPicture(personalPicture);
        
        personalDto.setId("123");
        personalDto.setPicture(personalPicture);

        when(personalRepository.findById(personalDto.getId())).thenReturn(Optional.empty());
        assertThrows(EETResponseException.class, ()->personalService.saveImage(personalDto.getId(), avatar));
    }
    

    @Test
    @DisplayName("save personal with user")
    public void testSavePersonalWithUser_whenPersonalRepoDoesNotThrow_doesNotThrow(){
    	Level level = new Level();
    	level.setId(DEFAULT_PERSONAL_LEVEL);
    	level.setName("default_level");
    	
    	when(personalRepository.save(any())).thenReturn(Personal.builder().build());
        when(levelRepository.findLevelById(DEFAULT_PERSONAL_LEVEL)).thenReturn(Optional.of(level));
        
        assertDoesNotThrow(() -> personalService.savePersonalWithUser(PersonalDto.builder().levelId(DEFAULT_PERSONAL_LEVEL).build()));
    }

    @Test
    @DisplayName("save personal with user repo throw")
    public void testSavePersonalWithUser_whenPersonalRepoThrow_Throw(){

        // when(personalRepository.save(any())).thenThrow(SkillManagementException.class);

        assertThrows(SkillManagementException.class, () -> personalService.savePersonalWithUser(PersonalDto.builder().build()));
    }

    @Test
    public void savePersonalWithUser_levelNotFound_throwException() {

        when(levelRepository.findLevelById("f1f7a51a-5b92-414e-afd1-0bf59a827077")).thenThrow(
                SkillManagementException.class
        );

        assertThrows(SkillManagementException.class, () -> personalService.savePersonalWithUser(any()));
    }

    @Test
    public void savePersonalWithUser_saveSuccessfully() {

        when(levelRepository.findLevelById("f1f7a51a-5b92-414e-afd1-0bf59a827077")).thenReturn(
                Optional.ofNullable(
                        Level.builder()
                                .name("test")
                                .build()
                )
        );

        when(personalRepository.save(any())).thenReturn(
                Personal.builder()
                        .id("test")
                        .personalCode("test")
                        .title(StringUtils.EMPTY)
//                        .manager(StringUtils.EMPTY)
                        .build()
        );

        PersonalDto personalDto = PersonalDto.builder()
                .id("test")
                .code("test")
                .build();

        Personal personal = personalService.savePersonalWithUser(personalDto);
    	assertThat("test").isEqualTo(personal.getId());
    	assertThat("test").isEqualTo(personal.getPersonalCode());
    	assertThat(personal.getTitle()).isEmpty();
    	assertThat(personal.getManager()).isNull();
    }

    @Test
    public void addSkillsToPersonalWhenUserIsActivatedUserParam_addSuccessfully() {
    	
    	User user = User.builder().id("test").displayName("Test").build();

		when(personalRepository.findById("test")).thenReturn(Optional
				.ofNullable(Personal.builder().id("test").level(Level.builder().id("test").name("test").build()).build()));

		Personal personal = personalService.addSkillsToPersonalWhenUserIsActivated(user);
		assertThat("test").isEqualTo(personal.getId());
	}
    
    @Test
    @DisplayName("Find personal by id (happy case)")
    public void loadPersonalInfor_happycase() {
    	String idP = "test";
    	String ntid="nitd";
    	Personal personalDummy = Personal.builder().id(idP).personalCode(ntid)
    			.build();
    	PersonalDto personalDto = PersonalDto.builder()
                .id(idP)
                .build();
		Set<String> auth = new HashSet<>();
    	when(personalRepository.findById(idP)).thenReturn(Optional.ofNullable(personalDummy));
    	when(personalConverter.convertToDTOV2(personalDummy)).thenReturn(personalDto);
    	PersonalDto actualResult = assertDoesNotThrow(()-> personalService.findById(idP,ntid, auth ));
    	assertThat(personalDto.getId()).isEqualTo(actualResult.getId());
    }
    
    @Test
    @DisplayName("Find personal by id - permision for manager")
    void loadPersonalInfor_permissionForManager() {
    	String idP = "test";
    	String ntid="nitd";
    	Personal personalDummy = Personal.builder().id(idP).personalCode("ntidother")
    			.build();
    	PersonalDto personalDto = PersonalDto.builder()
                .id(idP)
                .build();
    	String[] permission = { Constants.VIEW_ASSOCIATE_INFO_PERMISSION };
		Set<String> auth = new HashSet<>(Arrays.asList(permission));
    	when(personalRepository.findById(idP)).thenReturn(Optional.ofNullable(personalDummy));
    	when(personalConverter.convertToDTOV2(personalDummy)).thenReturn(personalDto);
    	PersonalDto actualResult = assertDoesNotThrow(()-> personalService.findById(idP,ntid, auth ));
    	assertThat(personalDto.getId()).isEqualTo(actualResult.getId());
    }
    
    @Test
    @DisplayName("Find personal by id - access denied")
    void loadPersonalInfor_accessDenied() {
    	String idP = "test";
    	String ntid="nitd";
    	Personal personalDummy = Personal.builder().id(idP).personalCode("ntidother")
    			.build();
		Set<String> auth = new HashSet<>();
    	when(personalRepository.findById(idP)).thenReturn(Optional.ofNullable(personalDummy));
    	assertThrows(AccessDeniedException.class,
				() -> personalService.findById(idP,ntid, auth ));
    }
    
    @Test
    @DisplayName("Find personal by id - not found")
    public void loadPersonalInfor() {
    	String idP = "test";
    	String ntid="nitd";
    	String[] permission = { Constants.VIEW_ASSOCIATE_INFO_PERMISSION };
		Set<String> auth = new HashSet<>(Arrays.asList(permission));
    	when(personalRepository.findById(idP)).thenReturn(Optional.ofNullable(null));
    	assertThrows(EETResponseException.class, () -> personalService.findById(idP,ntid, auth ));
    }
    
    @Test
    @DisplayName("Delete personalBoschProject happy case")
    public void deletePersonalBoschProjectHappy() {
        PersonalProjectDto personalProjectDto = PersonalProjectDto.builder()
                .projectId("BoschProjectId")
                .projectType(Constants.BOSCH)
                .id("personalProjectId")
                .build();
        
        Set<String> authorities = new HashSet<>();
        authorities.add(Constants.EDIT_ASSOCIATE_INFO_PERMISSION);
        
        doNothing().when(personalProjectRepository).deleteById("personalProjectId");
        
        assertDoesNotThrow(()-> personalService.deletePersonalProject(personalProjectDto, authorities, "rrn5hc"));
    }
    
    @Test
    @DisplayName("Delete personalBoschProject by unauthorised person")
    public void deletePersonalBoschProjectUnauthorised() {
        PersonalProjectDto personalProjectDto = PersonalProjectDto.builder()
                .projectId("BoschProjectId")
                .projectType(Constants.BOSCH)
                .id("personalProjectId")
                .build();
        
        Set<String> authorities = new HashSet<>();
        
        when(messageSource.getMessage(MessageCode.NOT_AUTHORIZATION.toString(), null,
                LocaleContextHolder.getLocale())).thenReturn("You're not authorized");
        
        assertThrows(SkillManagementException.class,
                ()->personalService.deletePersonalProject(personalProjectDto, authorities, "rrn5hc"),
                "You're not authorized");
    }
    
    @Test
    @DisplayName("Delete personalNonBoschProject by the owner of project")
    public void deletePersonalNonBoschProjectByOwner() {
        PersonalProjectDto personalProjectDto = PersonalProjectDto.builder()
                .projectId("nonBoschProjectId")
                .projectType(Constants.NONBOSCH)
                .id("personalProjectId")
                .build();
        
        Personal personal = Personal.builder().personalCode("rrn5hc").build();
        
        PersonalProject personalProject = PersonalProject.builder().personal(personal).build();
        Optional<PersonalProject> personalProjectOptional = Optional.of(personalProject);
        
        Set<String> authorities = new HashSet<>();
        
        when(personalProjectRepository.findById("personalProjectId")).thenReturn(personalProjectOptional);
        doNothing().when(personalProjectRepository).deleteById("personalProjectId");
        doNothing().when(projectSkillTagRepository).deleteByProjectId("nonBoschProjectId");
        doNothing().when(projectRepository).deleteById("nonBoschProjectId");
        
        assertDoesNotThrow(() -> personalService.deletePersonalProject(personalProjectDto, authorities, "rrn5hc"));
    }

    @Test
    @DisplayName("Delete personalNonBoschProject by person with permission")
    public void deletePersonalNonBoschProjectByPersonWithPermission() {
        PersonalProjectDto personalProjectDto = PersonalProjectDto.builder()
                .projectId("nonBoschProjectId")
                .projectType(Constants.NONBOSCH)
                .id("personalProjectId")
                .build();
        
        Personal personal = Personal.builder().personalCode("rrn5hc").build();
        
        PersonalProject personalProject = PersonalProject.builder().personal(personal).build();
        Optional<PersonalProject> personalProjectOptional = Optional.of(personalProject);
        
        Set<String> authorities = new HashSet<>();
        authorities.add(Constants.EDIT_ASSOCIATE_INFO_PERMISSION);
        
        when(personalProjectRepository.findById("personalProjectId")).thenReturn(personalProjectOptional);
        doNothing().when(personalProjectRepository).deleteById("personalProjectId");
        doNothing().when(projectSkillTagRepository).deleteByProjectId("nonBoschProjectId");
        doNothing().when(projectRepository).deleteById("nonBoschProjectId");
        assertDoesNotThrow(()-> personalService.deletePersonalProject(personalProjectDto, authorities, "userWithPermission"));
    }
    
    @Test
    @DisplayName("Delete personalNonBoschProject by person with permission")
    public void deletePersonalNonBoschProjectUnauthorised() {
        PersonalProjectDto personalProjectDto = PersonalProjectDto.builder()
                .projectId("nonBoschProjectId")
                .projectType(Constants.NONBOSCH)
                .id("personalProjectId")
                .build();
        
        // Owner is another person
        Personal personal = Personal.builder().personalCode("rrn5hc").build();
        
        PersonalProject personalProject = PersonalProject.builder().personal(personal).build();
        Optional<PersonalProject> personalProjectOptional = Optional.of(personalProject);
        
        // No permission
        Set<String> authorities = new HashSet<>();
        
        when(personalProjectRepository.findById("personalProjectId")).thenReturn(personalProjectOptional);
        when(messageSource.getMessage(MessageCode.NOT_AUTHORIZATION.toString(), null,
                LocaleContextHolder.getLocale())).thenReturn("You're not authorized");
        
        assertThrows(SkillManagementException.class,
                ()-> personalService.deletePersonalProject(personalProjectDto, authorities, "userWithoutPermission"),
                "You're not authorized");
    }
}
