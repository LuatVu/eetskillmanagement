package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.bosch.eet.skill.management.converter.PersonalConverter;
import com.bosch.eet.skill.management.converter.SkillConverter;
import com.bosch.eet.skill.management.converter.utils.SkillCompetencyLeadConverterUtil;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.SkillCompetencyLeadDto;
import com.bosch.eet.skill.management.dto.SkillDto;
import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillCompetencyLead;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.SkillCompetencyLeadRepository;
import com.bosch.eet.skill.management.repo.SkillGroupRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;
import com.bosch.eet.skill.management.service.impl.CompetencyLeadServiceImpl;
import com.bosch.eet.skill.management.usermanagement.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CompetencyLeadServiceTest {

	@InjectMocks
	private CompetencyLeadServiceImpl competencyLeadServiceImpl;

	@Mock
	private SkillCompetencyLeadRepository skillCompetencyLeadRepository;

	@Mock
	private PersonalRepository personalRepository;

	@Mock
	private SkillRepository skillRepository;

	@Mock
	private SkillConverter skillConverter;
	
	@Mock
	private SkillCompetencyLeadConverterUtil skillCompetencyLeadConverter;
	
	@Mock
	private PersonalConverter personalConverter;
	
	@Mock
	private SkillGroupRepository skillGroupRepository;
	
	@Mock
	private MessageSource messageSource;

	// find
	@Test
	@DisplayName("Find skills of competency lead happy case")
	@Transactional
	public void getskills_returnListOfSkill() {
		String personalId = "admin";
		Personal dummyEntity = Personal.builder().id("admin").personalNumber("personalNumber")
				.personalCode("personalCode").level(Level.builder().build()).title("title").build();
		Skill dummySkill = Skill.builder().id("1").name("a").status("a").description("a").sequence(1)
				.skillGroup(SkillGroup.builder().id("123").name("test").build()).build();

		List<SkillCompetencyLead> scls = Arrays
				.asList(SkillCompetencyLead.builder().id("1").personal(dummyEntity).skill(dummySkill).build());

		List<Skill> skills = scls.stream().map(SkillCompetencyLead::getSkill).collect(Collectors.toList());

		when(skillCompetencyLeadRepository.findByPersonalId(personalId)).thenReturn(scls);

		when(skillConverter.convertToDTOs(skills))
				.thenReturn(Arrays.asList(SkillDto.builder().id("1").name("a").build()));

		List<SkillDto> skillOfComeptencyLeads = competencyLeadServiceImpl.findSkillsByCompetencyLeadId(personalId);
		System.out.println(skillOfComeptencyLeads);
		assertThat(skillOfComeptencyLeads).isNotEmpty();
	}

	// find
	@Test
	@DisplayName("Find competency lead - not found")
	@Transactional
	public void getskills_empty() {
		String personalId = "admin";
		when(skillCompetencyLeadRepository.findByPersonalId(personalId)).thenReturn(Collections.emptyList());
		assertThat(competencyLeadServiceImpl.findSkillsByCompetencyLeadId(personalId)).isEmpty();
	}

	// find
	@Test
	@DisplayName("Find skills of competency lead null skills case")
	@Transactional
	public void getskills_returnListOfNullSkill() {
		String personalId = "admin";
		Personal dummyEntity = Personal.builder().id("admin").personalNumber("personalNumber")
				.personalCode("personalCode").level(Level.builder().build()).title("title").build();

		List<SkillCompetencyLead> scls = Arrays
				.asList(SkillCompetencyLead.builder().id("1").personal(dummyEntity).build());

		when(skillCompetencyLeadRepository.findByPersonalId(personalId)).thenReturn(scls);

		when(competencyLeadServiceImpl.findSkillsByCompetencyLeadId(personalId))
				.thenThrow(SkillManagementException.class);

		assertThrows(SkillManagementException.class,
				() -> competencyLeadServiceImpl.findSkillsByCompetencyLeadId(personalId));
	}

	@Test
	@DisplayName("Save happy case")
	@Transactional
	void saveCompetencyLead() {
		List<Skill> skills = new ArrayList<>();

		SkillGroup skillGroup = SkillGroup.builder().id("skillGroupId").build();
		
		Skill skill = Skill.builder()
				.id("1")
				.skillGroup(skillGroup)
				.name("Test skill")
				.status("Active")
				.build();

		Personal personal = Personal.builder()
				.id("1")
				.build();
		

		skills.add(skill);

		List<String> skillIdList = new ArrayList<>();
		skillIdList.add("1");

		List<String> skillNameList = new ArrayList<>();
		skillNameList.add("Test skill");

		List<SkillCompetencyLeadDto> skillCompetencyLeadDtos = new ArrayList<>();

		SkillCompetencyLeadDto skillCompetencyLeadDto = SkillCompetencyLeadDto.builder()
				.personalId("1")
				.displayName("Test user")
				.skillIds(skillIdList)
				.skillNames(skillNameList)
				.skillGroupId(skillGroup.getId())
				.description("This is a test")
				.build();

		skillCompetencyLeadDtos.add(skillCompetencyLeadDto);

		List<SkillCompetencyLead> skillCompetencyLeads = new ArrayList<>();

		SkillCompetencyLead skillCompetencyLead = SkillCompetencyLead.builder()
				.id("1")
				.personal(personal)
				.skill(skill)
				.description(skillCompetencyLeadDto.getDescription())
				.build();

		skillCompetencyLeads.add(skillCompetencyLead);

		when(personalRepository.findById("1"))
				.thenReturn(Optional.of(personal));
		doNothing().when(skillCompetencyLeadRepository).deleteByPersonalId("1");
		when(skillRepository.findById("1"))
				.thenReturn(Optional.of(skill));
		when(skillCompetencyLeadRepository.saveAll(Mockito.any(List.class)))
				.thenReturn(skillCompetencyLeads);

		assertThat(competencyLeadServiceImpl.save(skillCompetencyLeadDtos)).isEqualTo(0);
	}
	
	@Test
	@DisplayName("Save happy case - Personal not exist")
	void saveCompetencyLead_PersonalNotExist() {
		List<Skill> skills = new ArrayList<>();

		SkillGroup skillGroup = SkillGroup.builder().id("skillGroupId").build();

		Skill skill = Skill.builder().id("1").skillGroup(skillGroup).name("Test skill").status("Active").build();

		Personal personal = Personal.builder().id("1").build();

		skills.add(skill);

		List<String> skillIdList = new ArrayList<>();
		skillIdList.add("1");

		List<String> skillNameList = new ArrayList<>();
		skillNameList.add("Test skill");

		List<SkillCompetencyLeadDto> skillCompetencyLeadDtos = new ArrayList<>();

		SkillCompetencyLeadDto skillCompetencyLeadDto = SkillCompetencyLeadDto.builder().personalId("1")
				.displayName("Test user").skillIds(skillIdList).skillNames(skillNameList)
				.skillGroupId(skillGroup.getId()).description("This is a test").build();

		skillCompetencyLeadDtos.add(skillCompetencyLeadDto);

		List<SkillCompetencyLead> skillCompetencyLeads = new ArrayList<>();

		SkillCompetencyLead skillCompetencyLead = SkillCompetencyLead.builder().id("1").personal(personal).skill(skill)
				.description(skillCompetencyLeadDto.getDescription()).build();

		skillCompetencyLeads.add(skillCompetencyLead);

		when(personalRepository.findById("1")).thenReturn(Optional.ofNullable(null));

		assertThrows(SkillManagementException.class,
				() -> competencyLeadServiceImpl.save(skillCompetencyLeadDtos));
	}
	
	@Test
	@DisplayName("Save happy case - skill not found")
	@Transactional
	void saveCompetencyLead_SkillNotFound() {
		List<Skill> skills = new ArrayList<>();

		SkillGroup skillGroup = SkillGroup.builder().id("skillGroupId").build();
		
		Skill skill = Skill.builder()
				.id("1")
				.skillGroup(skillGroup)
				.name("Test skill")
				.status("Active")
				.build();

		Personal personal = Personal.builder()
				.id("1")
				.build();
		

		skills.add(skill);

		List<String> skillIdList = new ArrayList<>();
		skillIdList.add("1");

		List<String> skillNameList = new ArrayList<>();
		skillNameList.add("Test skill");

		List<SkillCompetencyLeadDto> skillCompetencyLeadDtos = new ArrayList<>();

		SkillCompetencyLeadDto skillCompetencyLeadDto = SkillCompetencyLeadDto.builder()
				.personalId("1")
				.displayName("Test user")
				.skillIds(skillIdList)
				.skillNames(skillNameList)
				.skillGroupId(skillGroup.getId())
				.description("This is a test")
				.build();

		skillCompetencyLeadDtos.add(skillCompetencyLeadDto);

		List<SkillCompetencyLead> skillCompetencyLeads = new ArrayList<>();

		SkillCompetencyLead skillCompetencyLead = SkillCompetencyLead.builder()
				.id("1")
				.personal(personal)
				.skill(skill)
				.description(skillCompetencyLeadDto.getDescription())
				.build();

		skillCompetencyLeads.add(skillCompetencyLead);

		when(personalRepository.findById("1"))
				.thenReturn(Optional.of(personal));
		doNothing().when(skillCompetencyLeadRepository).deleteByPersonalId("1");
		when(skillRepository.findById("1"))
				.thenReturn(Optional.ofNullable(null));

		assertThrows(SkillManagementException.class,
				() -> competencyLeadServiceImpl.save(skillCompetencyLeadDtos));
	}

	@Test
	@DisplayName("Save empty case")
	@Transactional
	void saveCompetencyLead_Empty() {
		Personal personal = Personal.builder().build();
		List<SkillCompetencyLeadDto> skillCompetencyLeadDtos = new ArrayList<>();
		SkillCompetencyLeadDto skillCompetencyLeadDto = SkillCompetencyLeadDto.builder().build();
		skillCompetencyLeadDtos.add(skillCompetencyLeadDto);

		when(personalRepository.findById(personal.getId()))
				.thenReturn(Optional.of(personal));
		doNothing().when(skillCompetencyLeadRepository).deleteByPersonalId(personal.getId());

		assertThrows(Exception.class, () -> {competencyLeadServiceImpl.save(skillCompetencyLeadDtos);});
	}

	@Test
	@DisplayName("Save competency lead with empty personal case")
	@Transactional
	void saveCompetencyLead_EmptyPersonal() {
		when(personalRepository.findById("admin"))
				.thenThrow(SkillManagementException.class);

		assertThrows(SkillManagementException.class,
				() -> competencyLeadServiceImpl.save(Arrays.asList(SkillCompetencyLeadDto.builder().personalId("admin").build())));
	}

	@Test
	@DisplayName("Save competency lead with empty skill case")
	@Transactional
	void saveCompetencyLead_emptySkill() {
		Personal personal = Personal.builder()
				.id("1")
				.build();

		List<String> skillIdList = new ArrayList<>();

		Skill skill = Skill.builder().build();
		
		SkillGroup skillGroup = SkillGroup.builder().id("skillGroupId").build();

		List<SkillCompetencyLeadDto> skillCompetencyLeadDtos = new ArrayList<>();
		SkillCompetencyLeadDto skillCompetencyLeadDto = SkillCompetencyLeadDto.builder()
				.personalId("1")
				.skillIds(skillIdList)
				.skillGroupId("skillGroupId")
				.build();
		skillCompetencyLeadDtos.add(skillCompetencyLeadDto);
		
		SkillCompetencyLead skillCompetencyLead = SkillCompetencyLead.builder()
				.id("1")
				.personal(personal)
				.skillGroup(skillGroup)
				.build();
		List<SkillCompetencyLead> skillCompetencyLeads = Arrays.asList(skillCompetencyLead);
		
		when(personalRepository.findById("1"))
				.thenReturn(Optional.of(personal));
		doNothing().when(skillCompetencyLeadRepository).deleteByPersonalIdAndWhereSkillNotNull("1");
		when(skillRepository.findById(skill.getId()))
				.thenReturn(Optional.of(skill));
		when(skillGroupRepository.findById(skillGroup.getId()))
				.thenReturn(Optional.of(skillGroup));
		when(skillCompetencyLeadRepository.saveAll(Mockito.any(List.class)))
				.thenReturn(skillCompetencyLeads);
		assertThat(competencyLeadServiceImpl.save(skillCompetencyLeadDtos)).isEqualTo(0);
	}
	
	@Test
	@DisplayName("Find all competency leads")
	void findAllCompetencyLead() {
		Personal personal = Personal.builder().id("d").user(User.builder().displayName("Long").build()).build();
		SkillGroup skillGroup = SkillGroup.builder().build();
		List<SkillCompetencyLead> listCompe = Collections.singletonList(
				SkillCompetencyLead.builder().personal(personal).skillGroup(skillGroup).description("").build());
		List<SkillCompetencyLeadDto> listCompeDto = Collections.singletonList(
				SkillCompetencyLeadDto.builder().personalId("d").displayName("Long").description("").build());
		when(skillCompetencyLeadRepository.findNameList()).thenReturn(listCompe);
		assertThat(listCompeDto).isEqualTo(competencyLeadServiceImpl.findAllCompetencyLead());
	}

	@Test
	@DisplayName("Find competency lead by skill group id")
	void findCompetencyLeadBySkillGroupId() {
		Personal personal = Personal.builder().id("d").user(User.builder().displayName("Long").build())
				.personalCode("GLO7HC").build();
		String skillGroupId = "idskillgroup";
		SkillGroup skillGroup = SkillGroup.builder().id(skillGroupId).build();
		List<SkillCompetencyLead> listCompe = Collections.singletonList(
				SkillCompetencyLead.builder().personal(personal).skillGroup(skillGroup).description("").build());
		when(skillGroupRepository.findById(skillGroupId)).thenReturn(Optional.of(skillGroup));
		when(skillCompetencyLeadRepository.findBySkillGroupIdOrderByPersonalAsc(skillGroupId))
				.thenReturn(listCompe);
		List<PersonalDto> personalDtos = Collections
				.singletonList(PersonalDto.builder().id("d").name("Long").code("GLO7HC").build());
		List<Personal> competencyLeadList = new ArrayList<>();
		competencyLeadList.add(personal);
		when(personalConverter.convertToListMangerDto(competencyLeadList)).thenReturn(personalDtos);
		assertThat(personalDtos).isEqualTo(competencyLeadServiceImpl.findCompetencyLeadBySkillGroupId(skillGroupId));
	}
	
	@Test
	@DisplayName("Find competency lead by skill group id - Skill group not found")
	void findCompetencyLeadBySkillGroupId_SkillGroupNotFound() {
		String skillGroupId = "idskillgroup";
		when(skillGroupRepository.findById(skillGroupId)).thenReturn(Optional.ofNullable(null));
		assertThrows(SkillManagementException.class,
				() -> competencyLeadServiceImpl.findCompetencyLeadBySkillGroupId(skillGroupId));
	}
	
	@Test
	@DisplayName("Find competency lead by skill id")
	void findCompetencyLeadBySkillId() {
		String skillId = "idskillgroup";
		Personal personal = Personal.builder().id("d").user(User.builder().displayName("Long").build())
				.personalCode("GLO7HC").build();
		SkillCompetencyLead skillCompetencyLead = SkillCompetencyLead.builder().personal(personal).description("")
				.build();
		List<SkillCompetencyLead> listCompe = Collections.singletonList(skillCompetencyLead);
		when(skillCompetencyLeadRepository.findBySkillIdOrderByPersonalAsc(skillId)).thenReturn(listCompe);
		SkillCompetencyLeadDto skillCompetencyLeadDto = SkillCompetencyLeadDto.builder().personalId("d")
				.displayName("Long").description("").build();
		List<SkillCompetencyLeadDto> listCompeDto = Collections.singletonList(skillCompetencyLeadDto);
		assertThat(listCompeDto).isEqualTo(competencyLeadServiceImpl.findCompetencyLeadBySkillId(skillId));
	}
	
}
