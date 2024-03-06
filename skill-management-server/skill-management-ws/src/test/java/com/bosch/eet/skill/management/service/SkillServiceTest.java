package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.converter.PersonalConverter;
import com.bosch.eet.skill.management.converter.SkillConverter;
import com.bosch.eet.skill.management.converter.utils.PersonalSkillConverterUtil;
import com.bosch.eet.skill.management.converter.utils.SkillsHighlightConverterUtil;
import com.bosch.eet.skill.management.dto.AddSkillDto;
import com.bosch.eet.skill.management.dto.LevelExpected;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.PersonalSkillDto;
import com.bosch.eet.skill.management.dto.SkillCompetencyLeadDto;
import com.bosch.eet.skill.management.dto.SkillDto;
import com.bosch.eet.skill.management.dto.SkillExpectedLevelDto;
import com.bosch.eet.skill.management.dto.SkillExperienceLevelDTO;
import com.bosch.eet.skill.management.dto.SkillGroupLevelDto;
import com.bosch.eet.skill.management.dto.SkillHighlightDto;
import com.bosch.eet.skill.management.dto.SkillManagementDto;
import com.bosch.eet.skill.management.elasticsearch.document.PersonalDocument;
import com.bosch.eet.skill.management.elasticsearch.document.SkillDocument;
import com.bosch.eet.skill.management.elasticsearch.repo.PersonalSpringElasticRepository;
import com.bosch.eet.skill.management.elasticsearch.repo.SkillElasticRepository;
import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectSkillGroupSkill;
import com.bosch.eet.skill.management.entity.ProjectType;
import com.bosch.eet.skill.management.entity.RequestEvaluation;
import com.bosch.eet.skill.management.entity.RequestEvaluationDetail;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillCompetencyLead;
import com.bosch.eet.skill.management.entity.SkillExperienceLevel;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.entity.SkillHighlight;
import com.bosch.eet.skill.management.entity.SkillLevel;
import com.bosch.eet.skill.management.entity.SkillType;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.ResourceNotFoundException;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.repo.LevelRepository;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.PersonalSkillEvaluationRepository;
import com.bosch.eet.skill.management.repo.PersonalSkillRepository;
import com.bosch.eet.skill.management.repo.RequestEvaluationDetailRepository;
import com.bosch.eet.skill.management.repo.RequestEvaluationRepository;
import com.bosch.eet.skill.management.repo.SkillCompetencyLeadRepository;
import com.bosch.eet.skill.management.repo.SkillEvaluationRepository;
import com.bosch.eet.skill.management.repo.SkillExperienceLevelRepository;
import com.bosch.eet.skill.management.repo.SkillGroupRepository;
import com.bosch.eet.skill.management.repo.SkillLevelRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;
import com.bosch.eet.skill.management.repo.SkillsHighlightReposistory;
import com.bosch.eet.skill.management.service.impl.SkillServiceImpl;
import com.bosch.eet.skill.management.usermanagement.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SkillServiceTest {

    final String jsonResponse = "{\"id\":\"1\",\"name\":\"Test\",\"description\":\"This is a test\"}";

    @Mock
    private SkillService skillService;
    
    @InjectMocks
    @Spy    
    private SkillServiceImpl serviceImpl;
    
    @Mock
    private SkillRepository skillRepository;
    
    @Mock
    private LevelRepository levelRepository;
    
    @Mock
    private SkillLevelRepository skillLevelRepository;
    
    @Mock
    private SkillGroupRepository skillGroupRepository;

    @Mock
    private SkillExperienceLevelRepository skillExperienceLevelRepository;
    
    @Mock
    private SkillCompetencyLeadRepository skillCompetencyLeadRepository;

    @Mock
    private SkillEvaluationRepository skillEvaluationRepository;

    @Mock
    private RequestEvaluationDetailRepository requestEvaluationDetailRepository;

    @Mock
    private RequestEvaluationRepository requestEvaluationRepository;

    @Mock
    private PersonalSkillEvaluationRepository personalSkillEvaluationRepository;

    @Mock
    private SkillElasticRepository skillElasticRepository;

    @Mock
    private SkillsHighlightReposistory skillsHighlightReposistory;

    @Mock
    private PersonalSpringElasticRepository personalSpringElasticRepository;

    @Mock
    private SkillConverter skillConverter; 
    
    @Mock
    private SkillsHighlightConverterUtil skillsHighlightConverter;
    
    @Mock
    private PersonalRepository personalRepository;
    
    @Mock
    private SkillGroupService skillGroupService;

    @Mock
    private CompetencyLeadService competencyLeadService;
    
    @Mock
    private PersonalSkillRepository personalSkillRepository;

    @Mock
    private PersonalConverter personalConverter;
    
    @Mock
    private MessageSource messageSource;
    @Mock
    private MessageCode messageCode;


    //find
    @Test
    @DisplayName("Find all skills")
    @Transactional
    void getSkill_returnListOfSkill() {
        when(skillRepository.findAll()).thenReturn(
                Arrays.asList(
                        Skill.builder()
                        .id("1")
                        .name("a")
                        .status("a")
                        .description("a")
                        .sequence(1)
                        .skillGroup(SkillGroup.builder().id("123").name("test").build())
                        .build()
                )
        );

        List<String> actualResult = serviceImpl.findAllSkills();
        System.out.println(actualResult);
        assertThat(actualResult).isNotEmpty();
    }

    //find null
    @Test
    @DisplayName("Find empty skills")
    void getSkill_returnListOfEmptySkill() {
        when(skillRepository.findAll()).thenReturn(
                new ArrayList<>()
        );
        List<String> skills = skillService.findAllSkills();
        assertThat(skills).isEmpty();
    }

    @Test
    @DisplayName("Find skill experience level of specific skill happy case")
    void getSkillExperienceLevel_specificSkill() {

        // Dummy skill group
        SkillGroup skillGroup = SkillGroup.builder()
                .id("1")
                .name("Test group")
                .build();

        // Dummy skill
        Skill skill = Skill.builder()
                .id("1")
                .name("Test skill")
                .status("Active")
                .description("This is description")
                .sequence(1)
                .skillGroup(skillGroup)
                .build();

        // Dummy skill experience level
        List<SkillExperienceLevel> skillExperienceLevels = new ArrayList<>();
        SkillExperienceLevel skillExperienceLevel = SkillExperienceLevel.builder()
                .id("1")
                .name("Test")
                .description("This is a test")
                .skill(skill)
                .skillGroup(skillGroup)
                .build();
        skillExperienceLevels.add(skillExperienceLevel);

        when(skillExperienceLevelRepository.findNameAndDescriptionDistinctBySkillId(skill.getId()))
                .thenReturn(skillExperienceLevels);

        List<SkillExperienceLevelDTO> skillExperienceLevelDTOS = skillService.findAllSkillExperienceBySkillId(skill.getId());
        assertThat(skillExperienceLevelDTOS).isEqualTo(skillConverter.convertToDetailLevelDTOs(skillExperienceLevels));      

    }

    @Test
    @DisplayName("Find empty skill experience levels")
    void getSkillExperienceLevel_Empty() {

        // Dummy skill group
        SkillGroup skillGroup = SkillGroup.builder()
                .id("1")
                .name("Test group")
                .build();

        // Dummy skill
        Skill skill = Skill.builder()
                .id("1")
                .name("Test skill")
                .status("Active")
                .description("This is description")
                .sequence(1)
                .skillGroup(skillGroup)
                .build();

        // Dummy skill experience level
        List<SkillExperienceLevel> skillExperienceLevels = new ArrayList<>();

        when(skillExperienceLevelRepository.findNameAndDescriptionDistinctBySkillId(skill.getId()))
                .thenReturn(skillExperienceLevels);

        List<SkillExperienceLevelDTO> skillExperienceLevelDTOS = skillService.findAllSkillExperienceBySkillId(skill.getId());
        assertThat(skillExperienceLevelDTOS).isEmpty();

    }


    @Test
    @DisplayName("Find skill experience levels with empty skill")
    void getSkillExperienceLevels_emptySkill() {

        // Dummy skill group
        SkillGroup skillGroup = SkillGroup.builder()
                .id("1")
                .name("Test group")
                .build();

        // Dummy skill experience level
        SkillExperienceLevel skillExperienceLevel = SkillExperienceLevel.builder()
                .id("1")
                .name("Test")
                .description("This is a test")
                .skillGroup(skillGroup)
                .build();
        List<SkillExperienceLevel> skillExperienceLevels = new ArrayList<>();
        skillExperienceLevels.add(skillExperienceLevel);

        when(skillExperienceLevelRepository.findNameAndDescriptionDistinctBySkillId(""))
                .thenReturn(null);

        List<SkillExperienceLevelDTO> skillExperienceLevelDTOS = skillService.findAllSkillExperienceBySkillId("");

        assertThat(skillExperienceLevelDTOS).isEmpty();
    }

    @Test
    @DisplayName("Add skill for new associate")
    void addSkill_happyCase() {
        Level level = Level.builder()
                .id("1")
                .name(Constants.LEVEL_3)
                .build();

        User user = User.builder()
                .id("1")
                .name("Gia Huy")
                .build();

        PersonalDto personalDto = PersonalDto.builder()
                .id("1")
                .name(user.getName())
                .skillCluster(Arrays.asList("Java", "C#"))
                .level(level.getName())
                .build();

        Personal personal = Personal.builder()
                .id("1")
                .user(user)
                .mainSkillCluster("Java")
                .level(level)
                .build();

        SkillGroup skillGroup = SkillGroup.builder()
                .id("skill-group-1")
                .name("Java")
                .build();

        Skill skill = Skill.builder()
                .id("1")
                .name("Java")
                .skillGroup(skillGroup)
                .build();

        PersonalSkill personalSkill = PersonalSkill.builder()
                .id("1")
                .skill(skill)
                .personal(personal)
                .level(Constants.DEFAULT_FLOAT_SKILL_LEVEL)
                .experience(Constants.DEFAULT_EXPERIENCE)
                .build();

        PersonalSkillDto personalSkillDto = PersonalSkillDto.builder()
                .id("1")
                .personalId(personal.getId())
                .skillId(skill.getId())
                .level(Constants.LEVEL_3)
                .experience(Constants.DEFAULT_EXPERIENCE)
                .build();

        List<String> skillIds = Collections.singletonList(skill.getId());

        AddSkillDto addSkillDto = AddSkillDto.builder()
                .personalDto(personalDto)
                .skillGroupIds(Collections.singletonList(skillGroup.getId()))
                .build();

        when(personalRepository.findById(personalDto.getId())).thenReturn(Optional.of(personal));
        when(skillGroupRepository.findById(any())).thenReturn(Optional.of(skillGroup));
        when(skillRepository.findAllBySkillGroup(skillGroup)).thenReturn(Collections.singletonList(skill));
        when(skillGroupService.findSkillIdBySkillGroup(skillGroup.getId())).thenReturn(skillIds);
        when(skillRepository.findById(any())).thenReturn(Optional.of(skill));
        when(personalSkillRepository.save(personalSkill)).thenReturn(personalSkill);
		doReturn(Collections.singletonList(personalSkillDto)).when(serviceImpl).convertToDTOs(anyList());
		assertThat(Collections.singletonList(personalSkillDto)).isEqualTo(serviceImpl.addSkillsByListOfSkillGroups(addSkillDto));	
        assertThat(serviceImpl.addSkillsByListOfSkillGroups(addSkillDto)).hasSize(1);
    }

    @Test
    @DisplayName("Add skill for new associate - Throw skill not found")
    void addSkillForNewAssociateThrowSkillNotFound() {
        Level level = Level.builder()
                .id("1")
                .name(Constants.LEVEL_3)
                .build();

        User user = User.builder()
                .id("1")
                .name("Gia Huy")
                .build();

        PersonalDto personalDto = PersonalDto.builder()
                .id("1")
                .name(user.getName())
                .skillCluster(Arrays.asList("Java", "C#"))
                .level(level.getName())
                .build();

        Personal personal = Personal.builder()
                .id("1")
                .user(user)
                .mainSkillCluster("Java")
                .level(level)
                .build();

        AddSkillDto addSkillDto = AddSkillDto.builder()
                .personalDto(personalDto)
                .skillGroupIds(Collections.emptyList())
                .build();

        when(personalRepository.findById(personalDto.getId())).thenReturn(Optional.of(personal));

        assertThrows(SkillManagementException.class, () -> serviceImpl.addSkillsByListOfSkillGroups(addSkillDto));
    }

    @Test
    @DisplayName("Add skill for new associate null person case")
    void addSkill_nullPerson() {
        Level level = Level.builder()
                .id("1")
                .name(Constants.LEVEL_3)
                .build();

        User user = User.builder()
                .id("1")
                .name("Gia Huy")
                .build();

        PersonalDto personalDto = PersonalDto.builder()
                .id(StringUtils.EMPTY)
                .name(user.getName())
                .skillCluster(Arrays.asList("Java", "C#"))
                .level(level.getName())
                .build();

        AddSkillDto addSkillDto = AddSkillDto.builder()
                .personalDto(personalDto)
                .skillGroupIds(Collections.emptyList())
                .build();

        assertThrows(SkillManagementException.class, () -> serviceImpl.addSkillsByListOfSkillGroups(addSkillDto));
    }

    @Test
    @DisplayName("Add skill for new associate null skill case")
    void addSkill_nullSkill() {
    	
    	PersonalDto personalDto = PersonalDto.builder()
    			.id("test personal")
    			.name("test name")
    			.build();
    	
    	AddSkillDto addSkillDto = AddSkillDto.builder()
    			.personalDto(personalDto)
    			.skillGroupIds(Arrays.asList())
    			.build();
    	when(personalRepository.findById("associate_id"))
    			.thenReturn(Optional.of(Personal.builder().id("associate_id").build()));

    	assertThrows(SkillManagementException.class, () -> serviceImpl.addSkillsByListOfSkillGroups(addSkillDto));
    }

    @Test
    @DisplayName("Update skill happy case")
    void updateSkill() {
        Personal personal = Personal.builder()
                .id("1")
                .personalCode("ABC1HC")
                .build();

        SkillGroup skillGroup = SkillGroup.builder()
                .id("1")
                .name("Test group")
                .build();

        SkillGroup skillGroup1 = SkillGroup.builder()
                .id("2")
                .name("New group")
                .build();

        Skill skill = Skill.builder()
                .id("1")
                .name("Test skill")
                .skillGroup(skillGroup)
                .build();

        SkillCompetencyLead skillCompetencyLead = SkillCompetencyLead.builder()
                .id("1")
                .personal(personal)
                .skill(skill)
                .build();

        List<SkillExperienceLevelDTO> skillExperienceLevelDTOS = new ArrayList<>();
        SkillExperienceLevelDTO skillExperienceLevelDTO = SkillExperienceLevelDTO.builder()
                .id("1")
                .name("Level 0")
                .description("Test description")
                .build();
        skillExperienceLevelDTOS.add(skillExperienceLevelDTO);

        SkillExperienceLevel skillExperienceLevel = SkillExperienceLevel.builder()
                .id("2")
                .name("Level 2")
                .description("This is test description")
                .build();

        SkillManagementDto skillManagementDto = SkillManagementDto.builder()
                .name("Level 1")
                .skillExperienceLevels(skillExperienceLevelDTOS)
                .competencyLeads(Arrays.asList(personal.getId()))
                .skillGroup("New group")
                .isMandatory(true)
                .isRequired(false)
                .build();

        when(skillGroupRepository.findByName(skillManagementDto.getSkillGroup()))
                .thenReturn(Optional.of(skillGroup1));

        when(skillRepository.save(any()))
                .thenReturn(skill);

        when(skillExperienceLevelRepository.save(skillExperienceLevel))
                .thenReturn(skillExperienceLevel);

        when(personalRepository.findById(personal.getId()))
                .thenReturn(Optional.of(personal));

        when(skillCompetencyLeadRepository.save(skillCompetencyLead))
                .thenReturn(skillCompetencyLead);
        assertThat(serviceImpl.editNewSkill(skillManagementDto)).isTrue(); 
    }

    @Test
    @DisplayName("Update skill with skill group not found")
    void updateSkill_skillGroupNotFound() {
        Personal personal = Personal.builder()
                .id("1")
                .personalCode("ABC1HC")
                .build();

        List<SkillExperienceLevelDTO> skillExperienceLevelDTOS = new ArrayList<>();
        SkillExperienceLevelDTO skillExperienceLevelDTO = SkillExperienceLevelDTO.builder()
                .id("1")
                .name("Level 0")
                .description("Test description")
                .build();
        skillExperienceLevelDTOS.add(skillExperienceLevelDTO);

        SkillManagementDto skillManagementDto = SkillManagementDto.builder()
                .name("Level 1")
                .skillExperienceLevels(skillExperienceLevelDTOS)
                .competencyLeads(Arrays.asList(personal.getId()))
                .skillGroup("New group")
                .build();

        when(skillGroupRepository.findByName(skillManagementDto.getSkillGroup()))
                .thenReturn(Optional.empty());

        assertThrows(SkillManagementException.class,
                () -> serviceImpl.editNewSkill(skillManagementDto));
    }
    
    @Test
    @DisplayName("find skills highlight in happy case")
    public void findSkillsHightlight_HappyCase() {
    	String idPersonal ="1";
    	PersonalSkill ps1 = PersonalSkill.builder().personal(Personal.builder().id(idPersonal).build())
    			.skill(Skill.builder().id("1").build()).level(1).experience(1).build();
    	PersonalSkill ps2 = PersonalSkill.builder().personal(Personal.builder().id(idPersonal).build())
    			.skill(Skill.builder().id("2").build()).level(1).experience(1).build();
    	List<PersonalSkill> personalSkills = new ArrayList<PersonalSkill>();
    	personalSkills.add(ps1);
    	personalSkills.add(ps2);
		
    	assertThat(skillService.findSkillsHighlight(idPersonal)).isEqualTo(PersonalSkillConverterUtil.convertToDTOs2(personalSkills));   
    }
    
    @Test
    @DisplayName("find skills highlight (wrong id personal or not exist)")
    public void findSkillsHightlight_WrongIdPersonal() {
    	String idPersonal ="1";	
		when(personalSkillRepository.findSkillsHighlight(idPersonal)).thenReturn(new ArrayList<PersonalSkill>());
    	assertThat(skillService.findSkillsHighlight(idPersonal)).isEqualTo( new ArrayList<PersonalSkillDto>());   
    }
    @Test
    @DisplayName("find skills highlight (with null id personal)")
    public void findSkillsHightlight_NullIdPersonal() {
		when(personalSkillRepository.findSkillsHighlight(null)).thenReturn(new ArrayList<PersonalSkill>());
    	assertThat(skillService.findSkillsHighlight(null)).isEqualTo( new ArrayList<PersonalSkillDto>());  
    }
    
    @Test
    @DisplayName("find skills highlight in happy case")
    public void saveSkillsHightlight_HappyCase() {    	
    	List<String> personalSkillIds = new ArrayList<>();
    	personalSkillIds.add("1");
    	
    	List<PersonalSkillDto> personalSkillDtos = Arrays.asList(
    			PersonalSkillDto.builder()
    			.personalId("1")
    			.skillId("1").build()
        );
    	
    	when(skillService.saveSkillsHighlight("1", personalSkillIds)).thenReturn(personalSkillDtos);
    	assertThat(skillService.saveSkillsHighlight("1", personalSkillIds)).isEqualTo(personalSkillDtos);  
    	
    }
    
    @Test
    @DisplayName("find skills highlight (personal id wrong)")
    public void saveSkillsHightlight_PersonalIdWrong() {    	
    	List<String> personalSkillIds = new ArrayList<>();
    	personalSkillIds.add("1");
    	
    	when(skillService.saveSkillsHighlight("1", personalSkillIds)).thenThrow(SkillManagementException.class);

    	assertThrows(SkillManagementException.class,()-> skillService.saveSkillsHighlight("1", personalSkillIds));
    	
    }
    
    @Test
    @DisplayName("find skills highlight (skill id wrong)")
    public void saveSkillsHightlight_SkillIdWrong() {    	
    	List<String> personalSkillIds = new ArrayList<>();
    	personalSkillIds.add("1sdsdsa");
    	
    	when(skillService.saveSkillsHighlight("1", personalSkillIds)).thenReturn(new ArrayList<PersonalSkillDto>());
    	assertThat(new ArrayList<PersonalSkillDto>()).isEqualTo(skillService.saveSkillsHighlight("1", personalSkillIds));  
    }
    
    @Test
    @DisplayName("update skill level (happy case)")
    public void updateSkillLevel_HappyCase() {    	
    	List<String> idSkills = new ArrayList<>();
    	idSkills.add("1");
    	idSkills.add("2");
    	
    	Skill s1 = Skill.builder().id("1").name("skill 1").build();
    	Skill s2 = Skill.builder().id("2").name("skill 2").build();
    	List<Skill> skills = new ArrayList<>();
    	skills.add(s1);
    	skills.add(s2);
    	
    	when(skillRepository.findAllByIdIn(idSkills)).thenReturn(skills);
    	
    	Level l1 = Level.builder().id("1").name("Level 1").build();
    	Level l2 = Level.builder().id("2").name("Level 2").build();
    	List<Level> levels = new ArrayList<>();
    	levels.add(l1);
    	levels.add(l2);
    	
    	when(levelRepository.findAll()).thenReturn(levels);
    	
    	when(skillLevelRepository.findByLevelAndSkill(l2, s2)).thenReturn(Optional.empty());
    	
    	LevelExpected levelExpected1 = LevelExpected.builder().idLevel("1").nameLevel("Level 1").value(1).build();
    	LevelExpected levelExpected2 = LevelExpected.builder().idLevel("2").nameLevel("Level 2").value(2).build();
    	List<LevelExpected> levelExpecteds = new ArrayList<>();
    	levelExpecteds.add(levelExpected1);
    	levelExpecteds.add(levelExpected2);
    	
    	List<SkillExpectedLevelDto> skillExpectedLevelDtos = new ArrayList<>();
    	SkillExpectedLevelDto skillExpectedLevelDto1 =SkillExpectedLevelDto.builder()
    			.idSkill("1").nameSkill("skill 1").levelExpecteds(levelExpecteds).build();
    	SkillExpectedLevelDto skillExpectedLevelDto2 =SkillExpectedLevelDto.builder()
    			.idSkill("2").nameSkill("skill 2").levelExpecteds(levelExpecteds).build();
    	skillExpectedLevelDtos.add(skillExpectedLevelDto1);
    	skillExpectedLevelDtos.add(skillExpectedLevelDto2); 
    	
    	String result =serviceImpl.updateSkillLevel(skillExpectedLevelDtos);	
    	
    	assertThat("Done").isEqualTo(result);   
    }
    
    @Test
    @DisplayName("update skill level (none skill cluster)")
    public void updateSkillLevel_NoneSkillCluster() {    	
    	List<String> idSkills = new ArrayList<>();
    	idSkills.add("1");
    	idSkills.add("2");
    	
    	Skill s1 = Skill.builder().id("1").name("skill 1").build();
    	Skill s2 = Skill.builder().id("2").name("skill 2").build();
    	List<Skill> skills = new ArrayList<>();
    	skills.add(s1);
    	skills.add(s2);
    	
    	when(skillRepository.findAllByIdIn(idSkills)).thenReturn(skills);
    	
    	Level l1 = Level.builder().id("1").name("Level 1").build();
    	Level l2 = Level.builder().id("2").name("Level 2").build();
    	List<Level> levels = new ArrayList<>();
    	levels.add(l1);
    	levels.add(l2);
    	
    	when(levelRepository.findAll()).thenReturn(levels);
    	
    	when(skillLevelRepository.findByLevelAndSkill(l2, s2)).thenReturn(Optional.empty());
    	
    	LevelExpected levelExpected1 = LevelExpected.builder().idLevel("1").nameLevel("Level 1").value(1).build();
    	LevelExpected levelExpected2 = LevelExpected.builder().idLevel("2").nameLevel("Level 2").value(2).build();
    	List<LevelExpected> levelExpecteds = new ArrayList<>();
    	levelExpecteds.add(levelExpected1);
    	levelExpecteds.add(levelExpected2);
    	
    	List<SkillExpectedLevelDto> skillExpectedLevelDtos = new ArrayList<>();
    	String result = serviceImpl.updateSkillLevel(skillExpectedLevelDtos);	
    	assertThat("Done").isEqualTo(result);
    }

    @Test
    @DisplayName("Find all Skill level")
    void findAllSkillLevel() {
        SkillType skillType = SkillType.builder()
                .id("1")
                .name("Technical")
                .build();

        ProjectType projectType = ProjectType.builder()
                .id("1")
                .name(Constants.BOSCH)
                .build();

        Project project = Project.builder()
                .id("1")
                .name("Simple Project")
                .projectType(projectType)
                .build();

        Skill skill1 = Skill.builder()
                .id("1")
                .name("Java")
                .isMandatory(true)
                .isRequired(true)
                .build();

        Skill skill2 = Skill.builder()
                .id("2")
                .name("C#")
                .isMandatory(false)
                .isRequired(false)
                .build();

        List<Skill> skillList = Arrays.asList(skill1, skill2);
        List<String> skillIds = Arrays.asList(skill1.getId(), skill2.getId());

        SkillGroup skillGroup = SkillGroup.builder()
                .id("1")
                .name("Skill group")
                .skillType(skillType)
                .skills(skillList)
                .build();

        skill1.setSkillGroup(skillGroup);
        skill2.setSkillGroup(skillGroup);
        ProjectSkillGroupSkill projectSkillGroupSkill = ProjectSkillGroupSkill.builder()
                .skillGroup(skillGroup)
                .project(project)
                .id("1")
                .build();

        skillGroup.setProjectSkillGroupSkills(Collections.singletonList(projectSkillGroupSkill));

        Map<String, SkillGroupLevelDto> skillMap = new HashMap<>();
        when(skillRepository.findAllById(skillIds)).thenReturn(skillList);
        when(skillRepository.findAll()).thenReturn(skillList);
        when(skillRepository.findById(skill1.getId())).thenReturn(Optional.of(skill1));
        when(skillRepository.findById(skill2.getId())).thenReturn(Optional.of(skill2));
        for (String skillId : skillIds) {
            Optional<Skill> skillOpt = skillRepository.findById(skillId);
            if(!skillOpt.isPresent()) {
                throw new SkillManagementException(
                        messageSource.getMessage(MessageCode.SKILL_NOT_FOUND.toString(), null,
                                LocaleContextHolder.getLocale()),
                        MessageCode.SKILL_NOT_FOUND.toString(), null, NOT_FOUND);
            }
            Skill skill = skillOpt.get();
            SkillGroupLevelDto skillGroupAndLevelDto = SkillGroupLevelDto.builder()
                    .skillName(skill.getName())
                    .skillGroup(skill.getSkillGroup().getName())
                    .skillLevels(skillService.findAllSkillExperienceBySkillId(skillId))
                    .isMandatory(skill.getIsMandatory())
                    .isRequired(skill.getIsRequired())
                    .build();
            skillMap.put(skillId, skillGroupAndLevelDto);
        }
        assertThat(skillMap).isEqualTo(serviceImpl.findAllSkillLevel());
    }

    @Test
    @DisplayName("Find all skill level - skill not found")
    void findAllSkillLevelThrowSkillNotFound() {
        Skill skill1 = Skill.builder()
                .id("1")
                .name("Java")
                .build();

        Skill skill2 = Skill.builder()
                .id("2")
                .name("C#")
                .build();

        Skill skill3 = Skill.builder()
                .id("3")
                .name("DotNet")
                .build();

        List<Skill> skillList = Arrays.asList(skill1, skill2, skill3);

        when(skillRepository.findAll()).thenReturn(skillList);
        when(skillRepository.findById(any())).thenThrow(SkillManagementException.class);

        assertThrows(SkillManagementException.class, () -> serviceImpl.findAllSkillLevel());

    }

    @Test
    @DisplayName("Find by Id - Happy case")
    void findById() {
        Skill skill = Skill.builder()
                .id("1")
                .name("Java")
                .build();

        SkillDto skillDto = SkillDto.builder()
                .id("1")
                .name("Java")
                .build();

        SkillGroup skillGroup = SkillGroup.builder()
                .id("1")
                .name("Skill group")
                .skills(Collections.singletonList(skill))
                .build();

        skill.setSkillGroup(skillGroup);

        when(skillRepository.findById(skill.getId())).thenReturn(Optional.of(skill));
        when(skillConverter.convertToDTO(skill)).thenReturn(skillDto);
        assertThat(skillDto).isEqualTo(serviceImpl.findById(skill.getId()));  
        
    }

    @Test
    @DisplayName("Find by Id - Skill not found")
    void findByIdThrowSkillNotFound() {
        Skill skill = Skill.builder()
                .id("1")
                .name("Java")
                .build();

        SkillDto skillDto = SkillDto.builder()
                .id("1")
                .name("Java")
                .build();

        SkillGroup skillGroup = SkillGroup.builder()
                .id("1")
                .name("Skill group")
                .skills(Collections.singletonList(skill))
                .build();

        skill.setSkillGroup(skillGroup);
        skillDto.setSkillGroup(skillGroup.getName());

        when(skillRepository.findById(skill.getId())).thenReturn(Optional.empty());
        when(skillConverter.convertToDTO(skill)).thenReturn(skillDto);
        assertThrows(SkillManagementException.class, () -> serviceImpl.findById(skill.getId()));
    }

    @Test
    @DisplayName("find skill experience detail by skill Id")
    void findSkillExperienceDetailBySkillId() {
        Level level = Level.builder()
                .id("1")
                .name("L50")
                .build();

        Personal personal = Personal.builder()
                .id("GiaHuy")
                .personalNumber("1")
                .level(level)
                .build();

        SkillType skillType = SkillType.builder()
                .id("1")
                .name("Technical")
                .build();

        Skill skill = Skill.builder()
                .id("1")
                .name("Java")
                .isMandatory(true)
                .isRequired(true)
                .build();

        List<String> skillIdList = Collections.singletonList(skill.getId());
        List<String> skillNameList = Collections.singletonList(skill.getName());

        SkillGroup skillGroup = SkillGroup.builder()
                .id("1")
                .name("Skill group")
                .skillType(skillType)
                .skills(Collections.singletonList(skill))
                .build();

        skill.setSkillGroup(skillGroup);

        SkillCompetencyLeadDto skillCompetencyLeadDto = SkillCompetencyLeadDto.builder()
                .skillGroupId("1")
                .skillIds(skillIdList)
                .skillCluster("Java")
                .skillNames(skillNameList)
                .personalId(personal.getId())
                .build();

        SkillCompetencyLead skillCompetencyLead = SkillCompetencyLead.builder()
                .id("1")
                .skill(skill)
                .skillGroup(skillGroup)
                .personal(personal)
                .build();

        SkillExperienceLevel skillExperienceLevel = SkillExperienceLevel.builder()
                .id("1")
                .level(level)
                .skillGroup(skillGroup)
                .name("SkillExperienceLevel")
                .description("Dummy Description")
                .build();

        SkillExperienceLevelDTO skillExperienceLevelDTO = SkillExperienceLevelDTO.builder()
                .id("1")
                .name("SkillExperienceLevel")
                .description("Dummy Description")
                .build();

        List<SkillExperienceLevel> skillExperienceLevelList = Collections.singletonList(skillExperienceLevel);
        List<SkillExperienceLevelDTO> skillExperienceLevelDTOList = Collections.singletonList(skillExperienceLevelDTO);

        SkillGroupLevelDto skillGroupLevelDto = SkillGroupLevelDto.builder()
                .skillGroup(skillGroup.getName())
                .skillLevels(skillExperienceLevelDTOList)
                .skillName(skill.getName())
                .competencyLeads(Collections.singletonList(skillCompetencyLeadDto))
                .isMandatory(skill.getIsMandatory())
                .isRequired(skill.getIsRequired())
                .skillType(skillGroup.getSkillType().getName())
                .build();

        when(personalRepository.save(personal)).thenReturn(personal);
        when(personalRepository.findById(personal.getId())).thenReturn(Optional.of(personal));
        when(skillCompetencyLeadRepository.findBySkillIdOrderByPersonalAsc(skill.getId()))
                .thenReturn(Collections.singletonList(skillCompetencyLead));
        when(competencyLeadService.findCompetencyLeadBySkillId(skill.getId()))
                .thenReturn(Collections.singletonList(skillCompetencyLeadDto));
        
        when(skillRepository.findById(skill.getId())).thenReturn(Optional.of(skill));
        when(skillExperienceLevelRepository.findNameAndDescriptionDistinctBySkillId(skill.getId()))
                .thenReturn(skillExperienceLevelList);
        when(skillConverter.convertToDetailLevelDTOs(skillExperienceLevelList)).thenReturn(skillExperienceLevelDTOList);
        assertThat(skillGroupLevelDto).isEqualTo(serviceImpl.findSkillExperienceDetailBySkillId(skill.getId()));  

    }

    @Test
    @DisplayName("find skill experience detail by skill Id - skill not found")
    void findSkillExperienceDetailBySkillIdThrowSkillNotFound() {
        Skill skill = Skill.builder()
                .id("1")
                .name("Java")
                .isMandatory(true)
                .isRequired(true)
                .build();

        SkillDto skillDto = SkillDto.builder()
                .id("1")
                .name("Java")
                .build();

        when(skillRepository.findById(skill.getId())).thenReturn(Optional.empty());
        when(skillConverter.convertToDTO(skill)).thenReturn(skillDto);
        assertThrows(SkillManagementException.class, () -> serviceImpl.findSkillExperienceDetailBySkillId(skill.getId()));

    }

    @Test
    @DisplayName("Save Skill Management")
    void saveSkillManagement() {
        Level level = Level.builder()
                .id("1")
                .name("L50")
                .build();

        Personal personal = Personal.builder()
                .id("GiaHuy")
                .personalNumber("1")
                .level(level)
                .build();

        SkillType skillType = SkillType.builder()
                .id("1")
                .name("Technical")
                .build();

        SkillGroup skillGroup = SkillGroup.builder()
                .id("1")
                .name("Skill Group")
                .skillType(skillType)
                .build();

        Skill skill = Skill.builder()
                .name("Java")
                .skillGroup(skillGroup)
                .status("ACTIVE")
                .isMandatory(true)
                .isRequired(true)
                .build();

        SkillDto skillDto = SkillDto.builder()
                .id("1")
                .name("Java")
                .skillGroup(skillGroup.getName())
                .skillType(skillType.getName())
                .level(level.getName())
                .build();

        SkillExperienceLevel skillExperienceLevel = SkillExperienceLevel.builder()
                .id("1")
                .name("SkillExperienceLevel")
                .skillGroup(skillGroup)
                .level(level)
                .skill(skill)
                .build();

        SkillExperienceLevelDTO skillExperienceLevelDTO = SkillExperienceLevelDTO.builder()
                .id("1")
                .name("SkillExperienceLevel")
                .description("Dummy Description")
                .build();

        skillDto.setSkillExperienceLevel(Collections.singletonList(skillExperienceLevelDTO));

        SkillManagementDto skillManagementDto = SkillManagementDto.builder()
                .id("1")
                .name("Java")
                .skillType(skillType.getName())
                .skillGroup(skillGroup.getName())
                .skillExperienceLevels(Collections.singletonList(skillExperienceLevelDTO))
                .competencyLeads(Collections.singletonList(personal.getId()))
                .isMandatory(true)
                .isRequired(true)
                .build();

        SkillCompetencyLead skillCompetencyLead = SkillCompetencyLead.builder()
                .id("1")
                .skillGroup(skillGroup)
                .personal(personal)
                .skill(skill)
                .description("skillCompetencyLead")
                .build();

        when(skillConverter.convertToDTO(skill)).thenReturn(skillDto);
        when(skillRepository.findByName(skillManagementDto.getName())).thenReturn(Optional.empty());
        when(skillRepository.findById(skill.getId())).thenReturn(Optional.of(skill));
        when(skillRepository.findAll()).thenReturn(Collections.singletonList(skill));
        when(skillGroupRepository.findByName(skillManagementDto.getSkillGroup())).thenReturn(Optional.of(skillGroup));
        when(skillRepository.save(any())).thenReturn(skill);
        when(skillExperienceLevelRepository.save(skillExperienceLevel)).thenReturn(skillExperienceLevel);
        doNothing().when(skillCompetencyLeadRepository).deleteBySkillGroupIdAndWhereSkillIdIsNull(skillGroup.getId());
        when(personalRepository.findById(skillManagementDto.getCompetencyLeads().get(0))).thenReturn(Optional.of(personal));
        when(skillCompetencyLeadRepository.save(skillCompetencyLead)).thenReturn(skillCompetencyLead);
        doNothing().when(skillElasticRepository).update(skill);
        assertThat(skill).isEqualTo(serviceImpl.saveSkillManagement(skillManagementDto));  

    }

    @Test
    @DisplayName("Save skill management - skill name exist")
    void saveSkillManagementThrowSkillNameExist() {
        SkillExperienceLevelDTO skillExperienceLevelDTO = SkillExperienceLevelDTO.builder()
                .id("1")
                .name("SkillExperienceLevel")
                .description("Dummy Description")
                .build();

        SkillManagementDto skillManagementDto = SkillManagementDto.builder()
                .id("1")
                .name("Java")
                .skillType("Skill Type")
                .skillGroup("Skill Group")
                .skillExperienceLevels(Collections.singletonList(skillExperienceLevelDTO))
                .competencyLeads(Collections.singletonList("Personal-id"))
                .isMandatory(true)
                .isRequired(true)
                .build();

        when(skillRepository.findByName(skillManagementDto.getName())).thenThrow(SkillManagementException.class);
        assertThrows(SkillManagementException.class, () -> serviceImpl.saveSkillManagement(skillManagementDto));
    }

    @Test
    @DisplayName("Find all skill name")
    void findAllSkillName() {
        Skill skill1 = Skill.builder()
                .id("1")
                .name("Java")
                .build();


        Skill skill2 = Skill.builder()
                .id("1")
                .name("Java")
                .build();

        Skill skill3 = Skill.builder()
                .id("1")
                .name("Java")
                .build();

        List<Skill> skillList = Arrays.asList(skill1, skill2, skill3);
        List<String> skillNameList = new ArrayList<>(Arrays.asList(skill1.getName(), skill2.getName(), skill3.getName()));
        when(skillRepository.findAll()).thenReturn(skillList);
        assertThat(skillNameList).isEqualTo(serviceImpl.findAllSkillsName());  
    }

    @Test
    @DisplayName("Delete skill")
    void deleteSkillHappyCase() {
        Level level = Level.builder()
                .id("1")
                .name(Constants.LEVEL_3)
                .build();

        Personal personal = Personal.builder()
                .id("GiaHuy")
                .personalNumber("1")
                .level(level)
                .build();

        SkillType skillType = SkillType.builder()
                .id("1")
                .name("Technical")
                .build();

        Skill skill = Skill.builder()
                .id("1")
                .name("Java")
                .isMandatory(true)
                .isRequired(true)
                .build();

        SkillGroup skillGroup = SkillGroup.builder()
                .id("1")
                .name("Skill group")
                .skillType(skillType)
                .skills(Collections.singletonList(skill))
                .build();

        skill.setSkillGroup(skillGroup);

        SkillExperienceLevel skillExperienceLevel = SkillExperienceLevel.builder()
                .id("1")
                .level(level)
                .skillGroup(skillGroup)
                .name("SkillExperienceLevel")
                .description("Dummy Description")
                .build();

        SkillCompetencyLead skillCompetencyLead = SkillCompetencyLead.builder()
                .id("1")
                .skillGroup(skillGroup)
                .personal(personal)
                .skill(skill)
                .description("skillCompetencyLead")
                .build();

        SkillHighlight skillHighlight = SkillHighlight.builder()
                .id("1")
                .personal(personal)
                .build();

        PersonalSkill personalSkill = PersonalSkill.builder()
                .skillHighlights(Collections.singletonList(skillHighlight))
                .personal(personal)
                .skill(skill)
                .level(Constants.DEFAULT_FLOAT_SKILL_LEVEL)
                .experience(Constants.DEFAULT_EXPERIENCE)
                .build();

        RequestEvaluationDetail requestEvaluationDetail = RequestEvaluationDetail.builder()
                .id("1")
                .skill(skill)
                .status(Constants.STATUS)
                .approver(personal)
                .currentExp(Constants.DEFAULT_EXPERIENCE)
                .currentLevel(Constants.DEFAULT_FLOAT_SKILL_LEVEL)
                .build();

        List<RequestEvaluationDetail> requestEvaluationDetailList = Collections.singletonList(requestEvaluationDetail);

        RequestEvaluation requestEvaluation = RequestEvaluation.builder()
                .requestEvaluationDetails(requestEvaluationDetailList)
                .requester(personal)
                .id("1")
                .approver(personal)
                .status(Constants.STATUS)
                .build();

        requestEvaluationDetail.setRequestEvaluation(requestEvaluation);

        List<RequestEvaluation> requestEvaluationList = new ArrayList<>(Collections.singletonList(requestEvaluation));

        SkillLevel skillLevel = SkillLevel.builder()
                .level(level)
                .skill(skill)
                .skillGroup(skillGroup)
                .id("1")
                .build();

        PersonalDocument personalDocument = PersonalDocument.builder()
                .id("1")
                .personalCode("Gia-Huy")
                .personalId("Personal-id")
                .skills(Collections.singletonList("Java"))
                .level(Constants.LEVEL_3)
                .build();

        String skillId = "1"; //dummy skill Id use as a parameter

        when(skillExperienceLevelRepository.findAllBySkillId(skillId))
                .thenReturn(Collections.singletonList(skillExperienceLevel));

        doNothing().when(skillExperienceLevelRepository).deleteAll(Collections.singletonList(skillExperienceLevel));
        when(skillRepository.findById(skillId)).thenReturn(Optional.ofNullable(skill));
        when(skillCompetencyLeadRepository.findAllBySkillId(skillId))
                .thenReturn(Collections.singletonList(skillCompetencyLead));
        doNothing().when(skillCompetencyLeadRepository).deleteAll(Collections.singletonList(skillCompetencyLead));
        doNothing().when(skillCompetencyLeadRepository).updateSkillToNullBySkillId(skillId);
        when(skillsHighlightReposistory.findByPersonalSkillSkillId(skillId))
                .thenReturn(Collections.singletonList(skillHighlight));
        doNothing().when(skillsHighlightReposistory)
                .deleteAll(Collections.singletonList(skillHighlight));
        doNothing().when(personalSkillRepository)
                .deleteAll(Collections.singletonList(personalSkill));
        when(requestEvaluationDetailRepository.findBySkillId(skillId))
                .thenReturn(requestEvaluationDetailList);
        for (RequestEvaluationDetail requestEvaluationDetailTemp : requestEvaluationDetailList) {
            RequestEvaluation requestEvaluationTemp = requestEvaluationDetailTemp.getRequestEvaluation();
            boolean isRequestHasSingleDetail = requestEvaluationTemp.getRequestEvaluationDetails().size() == 1;
            if (!requestEvaluationDetailList.contains(requestEvaluation) && isRequestHasSingleDetail) {
                requestEvaluationList.add(requestEvaluation);
            }
        }
        doNothing().when(requestEvaluationDetailRepository).deleteBySkillId(skillId);
        doNothing().when(requestEvaluationRepository).deleteAll(requestEvaluationList);
        when(skillLevelRepository.findBySkillId(skillId))
                .thenReturn(Collections.singletonList(skillLevel));
        doNothing().when(skillLevelRepository)
                .deleteAll(Collections.singletonList(skillLevel));
        doNothing().when(skillRepository).deleteById(skillId);
        doNothing().when(skillElasticRepository).removeById(skillId);
        when(personalSkillRepository.findBySkillId(skillId))
                .thenReturn(Collections.singletonList(personalSkill));
        when(personalConverter.convertToDocument(personal))
                .thenReturn(personalDocument);
        when(personalSpringElasticRepository.saveAll(Collections.singletonList(personalDocument)))
                .thenReturn(Collections.singleton(personalDocument));

        assertThat(Constants.SUCCESS).isEqualTo(serviceImpl.deleteSkill(skillId));

    }

    @Test
    @DisplayName("Delete skill - throw skill not found")
    void deleteSkillThrowSkillNotFound() {
        Level level = Level.builder()
                .id("1")
                .name(Constants.LEVEL_3)
                .build();

        SkillType skillType = SkillType.builder()
                .id("1")
                .name("Technical")
                .build();

        Skill skill = Skill.builder()
                .id("1")
                .name("Java")
                .isMandatory(true)
                .isRequired(true)
                .build();

        SkillGroup skillGroup = SkillGroup.builder()
                .id("1")
                .name("Skill group")
                .skillType(skillType)
                .skills(Collections.singletonList(skill))
                .build();

        skill.setSkillGroup(skillGroup);

        SkillExperienceLevel skillExperienceLevel = SkillExperienceLevel.builder()
                .id("1")
                .level(level)
                .skillGroup(skillGroup)
                .name("SkillExperienceLevel")
                .description("Dummy Description")
                .build();

        String skillId = "1"; //dummy skill Id use as a parameter

        when(skillExperienceLevelRepository.findAllBySkillId(skillId))
                .thenReturn(Collections.singletonList(skillExperienceLevel));
        doNothing().when(skillExperienceLevelRepository)
                .deleteAll(Collections.singletonList(skillExperienceLevel));
        when(skillLevelRepository.findBySkillId(any()))
                .thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> serviceImpl.deleteSkill(skillId));

    }

    @Test
    @DisplayName("Find skill highlight")
    void findSkillsHighlightHappyCase() {
        Level level = Level.builder()
                .id("1")
                .name(Constants.LEVEL_3)
                .build();

        Personal personal = Personal.builder()
                .id("GiaHuy")
                .personalNumber("1")
                .level(level)
                .build();

        Skill skill = Skill.builder()
                .id("1")
                .name("Java")
                .isMandatory(true)
                .isRequired(true)
                .build();

        SkillHighlight skillHighlight = SkillHighlight.builder()
                .id("1")
                .personal(personal)
                .build();

        PersonalSkill personalSkill = PersonalSkill.builder()
                .skillHighlights(Collections.singletonList(skillHighlight))
                .personal(personal)
                .skill(skill)
                .level(Constants.DEFAULT_FLOAT_SKILL_LEVEL)
                .experience(Constants.DEFAULT_EXPERIENCE)
                .build();

        SkillHighlightDto skillHighlightDto = SkillHighlightDto.builder()
                .skillId(skill.getId())
                .skillName(skill.getName())
                .level(Constants.LEVEL_3)
                .experience(Constants.DEFAULT_EXPERIENCE)
                .build();

        String personalId = personal.getId();

        List<SkillHighlightDto> expectedResult = Collections.singletonList(skillHighlightDto);
        when(personalSkillRepository.findSkillsHighlight(personalId))
                .thenReturn(Collections.singletonList(personalSkill));
        when(skillsHighlightReposistory.findByPersonalId(personalId))
                .thenReturn(Collections.singletonList(skillHighlight));
        assertThat(expectedResult).isEqualTo(serviceImpl.findSkillsHighlight(personalId));
    }

    @Test
    @DisplayName("Find skill highlight - empty personal skill")
    void findSkillHighlightWithEmptyPersonalSkillThenReturnEmptyList() {
        when(personalSkillRepository.findSkillsHighlight(any()))
                .thenReturn(Collections.emptyList());
        assertThat(new ArrayList<SkillHighlightDto>()).isEqualTo(serviceImpl.findSkillsHighlight(StringUtils.EMPTY));
    }

    @Test
    @DisplayName("Save skill highlight")
    void saveSkillsHighlight() {
        Level level = Level.builder()
                .id("1")
                .name(Constants.LEVEL_3)
                .build();

        Personal personal = Personal.builder()
                .id("GiaHuy")
                .personalNumber("1")
                .level(level)
                .build();

        Skill skill = Skill.builder()
                .id("1")
                .name("Java")
                .isMandatory(true)
                .isRequired(true)
                .build();

        SkillHighlight skillHighlight = SkillHighlight.builder()
                .id("1")
                .personal(personal)
                .build();

        PersonalSkill personalSkill = PersonalSkill.builder()
                .skillHighlights(Collections.singletonList(skillHighlight))
                .personal(personal)
                .skill(skill)
                .level(Constants.DEFAULT_FLOAT_SKILL_LEVEL)
                .experience(Constants.DEFAULT_EXPERIENCE)
                .build();

        skillHighlight.setPersonalSkill(personalSkill);

        PersonalSkillDto personalSkillDto = PersonalSkillDto.builder()
                .id("1")
                .personalId(personal.getId())
                .skillId(skill.getId())
                .level(Constants.LEVEL_3)
                .experience(Constants.DEFAULT_EXPERIENCE)
                .build();

        //input parameters
        String personalId = personal.getId();
        List<String> personalSkillDtos = Arrays.asList("Java", "C#");
        List<SkillHighlight> skillHighlightList = Collections.singletonList(skillHighlight);

        when(personalRepository.findById(personalId)).thenReturn(Optional.of(personal));
        when(personalSkillRepository.findByPersonalIdAndSkillIdIn(personalId, personalSkillDtos))
                .thenReturn(Collections.singletonList(personalSkill));
        doNothing().when(skillsHighlightReposistory).deleteByPersonalId(personalId);
        when(skillsHighlightReposistory.saveAllAndFlush(skillHighlightList))
                .thenReturn(Collections.singletonList(skillHighlight));
        
        doReturn(Collections.singletonList(personalSkillDto)).when(serviceImpl).convertToDTOs2(anyList());
        assertThat(Collections.singletonList(personalSkillDto)).isEqualTo(serviceImpl.saveSkillsHighlight(personalId, personalSkillDtos));
    }

    @Test
    @DisplayName("Save skill highlight - throw personal ID not found")
    void saveSkillHighlightThrowPersonalIdNotFound() {
        String personalId = StringUtils.EMPTY;
        List<String> personalSkillDtos = Collections.emptyList();
        when(personalRepository.findById(any())).thenThrow(SkillManagementException.class);
        assertThrows(SkillManagementException.class, () -> serviceImpl.saveSkillsHighlight(
                personalId,
                personalSkillDtos
        ));
    }

    @Test
    @DisplayName("Save and sync Elastic Skill")
    void saveAndSyncElasticSkill() {
        Skill skill = Skill.builder()
                .id("1")
                .name("Java")
                .isMandatory(true)
                .isRequired(true)
                .build();

        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillElasticRepository.existById(skill.getId())).thenReturn(true);
        doNothing().when(skillElasticRepository).update(skill);
        assertThat(skill).isEqualTo(serviceImpl.saveAndSyncElasticSkill(skill));
        
    }

    @Test
    @DisplayName("Save and insert to Elastic Skill")
    void saveAndInsertElasticSkill() {
        Skill skill = Skill.builder()
                .id("1")
                .name("Java")
                .isMandatory(true)
                .isRequired(true)
                .build();

        SkillDocument skillDocument = SkillDocument.builder()
                .skillId(skill.getId())
                .skillType(Constants.TECHNICAL_SKILL_GROUP)
                .skillName("Java")
                .skillGroup("SkillGroup")
                .isMandatory(true)
                .isRequired(true)
                .build();

        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillElasticRepository.existById(any())).thenReturn(false);
        when(skillConverter.convertToDocument(skill)).thenReturn(skillDocument);
        doNothing().when(skillElasticRepository).insert(skillDocument);
        assertThat(skill).isEqualTo(serviceImpl.saveAndSyncElasticSkill(skill));
        }

    @Test
    @DisplayName("Find all by name")
    void findAllByName() {
        Skill skill = Skill.builder()
                .id("1")
                .name("Java")
                .isMandatory(true)
                .isRequired(true)
                .build();

        when(skillRepository.findAllByName(skill.getName()))
                .thenReturn(Collections.singletonList(skill));
        assertThat(Collections.singletonList(skill)).isEqualTo(serviceImpl.findAllByName(skill.getName()));
    }

}