package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.converter.SkillConverter;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.SkillDto;
import com.bosch.eet.skill.management.dto.SkillGroupDto;
import com.bosch.eet.skill.management.entity.PersonalSkillGroup;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.entity.SkillType;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.repo.PersonalSkillGroupRepository;
import com.bosch.eet.skill.management.repo.SkillGroupRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;
import com.bosch.eet.skill.management.repo.SkillTypeRepository;
import com.bosch.eet.skill.management.service.impl.CompetencyLeadServiceImpl;
import com.bosch.eet.skill.management.service.impl.SkillGroupServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SkillGroupServiceTest {

    @InjectMocks
    private SkillGroupServiceImpl skillGroupService;

    @Mock
    private SkillGroupRepository skillGroupRepository;

	@Mock
	private PersonalSkillGroupRepository personalSkillGroupRepository;
    
    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillConverter skillConverter;
    
    @Mock
    private SkillTypeRepository skillTypeRepository;
    
    @Mock
    private MessageSource messageSourceMock;
    
    @Mock
    private CompetencyLeadServiceImpl competencyLeadServiceImpl;

    //find
    @Test
    @DisplayName("Find all skill groups")
    @Transactional
    void getSkillGroup_returnListOfSkillGroup() {
        when(skillGroupRepository.findAll()).thenReturn(
                Arrays.asList(
                        SkillGroup.builder()
                        .id("1")
                        .name("a")
                        .personalSkillGroups(Collections.emptyList())
                        .build()
                )
        );
        assertThat(skillGroupService.findAllSkillGroups()).isNotEmpty();    
    }

    //find null
    @Test
    @DisplayName("Find empty skill groups")
    void getSkillGroup_returnListOfEmptySkillGroup() {
        when(skillGroupRepository.findAll()).thenReturn(
                new ArrayList<>()
        );
        List<SkillGroupDto> skills = skillGroupService.findAllSkillGroups();
        assertThat(skills).isEmpty();
    }

	@Test
	@DisplayName("Create skill group")
	void createSkillGroup() {
		String skillTypeName = "Technical";

		SkillType skillType = SkillType.builder().id("sdsad").name(skillTypeName).build();

		SkillGroupDto skillGroupDto = SkillGroupDto.builder().id("1").name("a").skillType(skillTypeName).build();

		SkillGroup skillGroup = SkillGroup.builder().id("1").name("a").build();

		when(skillGroupRepository.findByName("a")).thenReturn(Optional.empty());

		when(skillTypeRepository.findByName(skillTypeName)).thenReturn(Optional.of(skillType));

		when(skillGroupRepository.save(skillGroup)).thenReturn(skillGroup);

		SkillGroupDto result = skillGroupService.addSkillGroup(skillGroupDto);
		assertThat(skillGroupDto).isEqualTo(result);
		
		}

    @Test
    @DisplayName("Create skill group duplicate case")
    void createSkillGroup_Duplicate() {
    	
        SkillGroupDto skillGroupDto = SkillGroupDto.builder()
                .id("1")
                .name("a")
                .build();

        SkillGroup skillGroup = SkillGroup.builder()
                .id("1")
                .name("a")
                .build();

        when(skillGroupRepository.findByName("a"))
                .thenReturn(Optional.of(skillGroup));

        assertThrows(SkillManagementException.class,
                () -> skillGroupService.addSkillGroup(skillGroupDto));
    }
    
	@Test
	@DisplayName("Create skill group - name skill group empy")
	void createSkillGroup_NameSkillGroupEmpty() {
		SkillGroupDto skillGroupDto = SkillGroupDto.builder().name("").build();
		 assertThrows(SkillManagementException.class,
	                () -> skillGroupService.addSkillGroup(skillGroupDto));
	}
	
	@Test
	@DisplayName("Create skill group - Skill type null")
	void createSkillGroup_SkillTypeNull() {
		String skillTypeName = "Technical";

		SkillGroupDto skillGroupDto = SkillGroupDto.builder().id("1").name("a").skillType(skillTypeName).build();

		SkillGroup skillGroup = SkillGroup.builder().id("1").name("sa").build();

		when(skillGroupRepository.findByName("a")).thenReturn(Optional.of(skillGroup));

		when(skillTypeRepository.findByName(skillTypeName)).thenReturn(Optional.ofNullable(null));
		
		assertThrows(SkillManagementException.class,
	                () -> skillGroupService.addSkillGroup(skillGroupDto));
	}

	@Test
	@DisplayName("Delete skill group")
	void deleteSkillGroup() {

		SkillGroup skillGroup = SkillGroup.builder()
				.id("1")
				.name("test skill group")
				.build();

		PersonalSkillGroup personalSkillGroup = PersonalSkillGroup.builder()
				.id("1")
				.skillGroup(skillGroup)
				.build();

		when(personalSkillGroupRepository.findBySkillGroupId(skillGroup.getId()))
				.thenReturn(Arrays.asList(personalSkillGroup));
		skillGroupService.deleteSkillGroup(skillGroup);


		Mockito.verify(personalSkillGroupRepository).deleteAll(Arrays.asList(personalSkillGroup));

	}

	@Test
	@DisplayName("Find by id")
	void findById() {
		String skillTypeName = "Technical";

		String idSKill = "idskill";

		String nameSKill = "nameskill";

		String nameSkillGroup = "nameskillgroup";

		SkillType skillType = SkillType.builder().id("sdsad").name(skillTypeName).build();

		Skill skill = Skill.builder().id(idSKill).build();

		SkillGroup skillGroup = SkillGroup.builder().id("1").name(nameSkillGroup)
				.skills(Collections.singletonList(skill)).skillType(skillType).build();

		skill.setSkillGroup(skillGroup);

		SkillDto skillDto = SkillDto.builder().id(idSKill).name(nameSKill).competency(nameSkillGroup).build();

		when(skillGroupRepository.findById("1")).thenReturn(Optional.ofNullable(skillGroup));

		when(skillConverter.convertToDTOs(Collections.singletonList(skill)))
				.thenReturn(Collections.singletonList(skillDto));

		SkillGroupDto skillGroupDto = SkillGroupDto.builder().id(skillGroup.getId()).name(skillGroup.getName())
				.skillType(skillTypeName).skillTypeId(skillType.getId()).skills(Collections.singletonList(skillDto))
				.build();

		when(competencyLeadServiceImpl.findCompetencyLeadBySkillGroupId(skillGroup.getId()))
				.thenReturn(Collections.singletonList(PersonalDto.builder().build()));
		assertThat(skillGroupDto).isEqualTo(skillGroupService.findById(skillGroup.getId()));
	}
	
	@Test
	@DisplayName("Find by id")
	void findById_NotFoundSkillGroup() {
		String skillTypeName = "Technical";

		String idSKill = "idskill";

		String nameSkillGroup = "nameskillgroup";

		SkillType skillType = SkillType.builder().id("sdsad").name(skillTypeName).build();

		Skill skill = Skill.builder().id(idSKill).build();

		SkillGroup skillGroup = SkillGroup.builder().id("1").name(nameSkillGroup)
				.skills(Collections.singletonList(skill)).skillType(skillType).build();

		skill.setSkillGroup(skillGroup);

		when(skillGroupRepository.findById("1")).thenReturn(Optional.ofNullable(null));

		assertThrows(SkillManagementException.class, () -> skillGroupService.findById(skillGroup.getId()));

	}
	
	@Test
	@DisplayName("Find skill id by skill group")
	void findSkillIdBySkillGroup() {
		String skillTypeName = "Technical";

		String idSKill = "idskill";

		String nameSkillGroup = "nameskillgroup";

		SkillType skillType = SkillType.builder().id("sdsad").name(skillTypeName).build();

		Skill skill = Skill.builder().id(idSKill).build();

		SkillGroup skillGroup = SkillGroup.builder().id("1").name(nameSkillGroup)
				.skills(Collections.singletonList(skill)).skillType(skillType).build();

		skill.setSkillGroup(skillGroup);

		when(skillGroupRepository.findById("1")).thenReturn(Optional.ofNullable(skillGroup));

		when(skillRepository.findAllBySkillGroup(skillGroup)).thenReturn(Collections.singletonList(skill));
		assertThat(Collections.singletonList(idSKill)).isEqualTo(skillGroupService.findSkillIdBySkillGroup(skillGroup.getId()));
	}

}
