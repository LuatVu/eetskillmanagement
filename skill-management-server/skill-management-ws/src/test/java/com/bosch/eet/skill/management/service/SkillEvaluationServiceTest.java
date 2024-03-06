package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.dto.UpdateSkillEvaluationDto;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillEvaluation;
import com.bosch.eet.skill.management.exception.EETResponseException;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.PersonalSkillEvaluationRepository;
import com.bosch.eet.skill.management.repo.SkillEvaluationRepository;
import com.bosch.eet.skill.management.service.impl.SkillEvaluationServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class SkillEvaluationServiceTest {

	@InjectMocks
	private SkillEvaluationServiceImpl skillEvaluationServiceImpl;

	@Mock
    private SkillEvaluationRepository skillEvaluationRepository;
    
	@Mock
    private PersonalRepository personalRepository;
	
	@Mock 
	private SkillEvaluationService skillEvaluationService;
	
	@Mock
	private PersonalSkillEvaluationRepository personalSkillEvaluationRepository;
    
	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");


    //update
    @Test
    @DisplayName("saveSkillEvaluation happy case")
    @Transactional
    void updateSaveSkillEvaluation_throwException() throws ParseException {
    
    	UpdateSkillEvaluationDto updateSkillEvaluationDto = UpdateSkillEvaluationDto.builder()
				.id("id-test")
				.name("name test")
				.approver("cb43b124-e79c-40e5-91fb-a7b1c1a16328")
				.status("pending test")
				.level("level test")
				.newLevel("new level test")
				.build();
    	
    	UpdateSkillEvaluationDto updateSkillDto = UpdateSkillEvaluationDto.builder()
				.id("id-test")
				.name("name test")
				.approver("cb43b124-e79c-40e5-91fb-a7b1c1a16328")
				.status("pending test")
				.level("level test")
				.newLevel("new level test")
				.build();
    	
    	Personal personalApr = Personal.builder()
    			.id("cb43b124-e79c-40e5-91fb-a7b1c1a16328")
    			.build();
    	
    	Set<PersonalSkill> personalSkills = new HashSet<PersonalSkill>();
    	personalSkills.add(PersonalSkill.builder()
    			.id("Skill-id")
    			.skill(Skill.builder().name("name test").build())    			
    			.build());
    	
    	Personal personalReq = Personal.builder()
    			.id("385edc75-b007-419f-b0cc-d9dff731e7a0")
    			.personalSkills(personalSkills)
    			.build();   
    	
    	SkillEvaluation skillEvaluation = SkillEvaluation.builder()
				.id("id-test")
				.approveDate(simpleDateFormat.parse("2022-12-09"))
				.approver(personalApr)
				.status("pending test")
				.personal(personalReq)
				.build();
    	
    	Optional<SkillEvaluation> skillEvaluationOpt = Optional.ofNullable(skillEvaluation);
    	
		when(skillEvaluationRepository.findById(updateSkillEvaluationDto.getId()))
			.thenReturn(skillEvaluationOpt);

		when(personalRepository.findById("cb43b124-e79c-40e5-91fb-a7b1c1a16328"))
			.thenReturn(Optional.ofNullable(personalApr));
		
		when(personalRepository.findById("385edc75-b007-419f-b0cc-d9dff731e7a0"))
		.thenReturn(Optional.ofNullable(personalReq));
		
		UpdateSkillEvaluationDto useDto = skillEvaluationServiceImpl.saveSkillEvaluation(updateSkillEvaluationDto);
		log.info(useDto.toString());
		assertThat(useDto).isEqualTo(updateSkillDto);
}


//update
@Test
@DisplayName("saveSkillEvaluation skill evaluation id null case")
@Transactional
void updateSaveSkillEvaluationThrow_throwException() throws ParseException {

	UpdateSkillEvaluationDto updateSkillEvaluationDto = UpdateSkillEvaluationDto.builder()
			.id("id-test")
			.name("name test")
			.approver("cb43b124-e79c-40e5-91fb-a7b1c1a16328")
			.status("pending test")
			.level("level test")
			.newLevel("new level test")
			.build();
	
	when(skillEvaluationRepository.findById(updateSkillEvaluationDto.getId()))
		.thenThrow(EETResponseException.class);
	
	assertThrows(EETResponseException.class, () -> skillEvaluationServiceImpl.saveSkillEvaluation(updateSkillEvaluationDto));
}

//update
@Test
@DisplayName("saveSkillEvaluation personal null case")
@Transactional
void updateSaveSkillEvaluationPersonalNull_throwException() throws ParseException {


	UpdateSkillEvaluationDto updateSkillEvaluationDto = UpdateSkillEvaluationDto.builder()
			.id("id-test")
			.name("name test")
			.approver("cb43b124-e79c-40e5-91fb-a7b1c1a16328")
			.status("pending test")
			.level("level test")
			.newLevel("new level test")
			.build();
	
	Personal personalApr = Personal.builder()
			.id("cb43b124-e79c-40e5-91fb-a7b1c1a16328")
			.build();
	
	Set<PersonalSkill> personalSkills = new HashSet<PersonalSkill>();
	personalSkills.add(PersonalSkill.builder()
			.id("Skill-id")
			.skill(Skill.builder().name("name test").build())    			
			.build());
	
	Personal personalReq = Personal.builder()
			.id("385edc75-b007-419f-b0cc-d9dff731e7a0")
			.personalSkills(personalSkills)
			.build();   
	
	SkillEvaluation skillEvaluation = SkillEvaluation.builder()
			.id("id-test")
			.approveDate(simpleDateFormat.parse("2022-12-09"))
			.approver(personalApr)
			.status("pending test")
			.personal(personalReq)
			.build();
	
	Optional<SkillEvaluation> skillEvaluationOpt = Optional.ofNullable(skillEvaluation);
	
	when(skillEvaluationRepository.findById(updateSkillEvaluationDto.getId()))
		.thenReturn(skillEvaluationOpt);

	when(personalRepository.findById("cb43b124-e79c-40e5-91fb-a7b1c1a16328"))
	.thenReturn(Optional.ofNullable(personalApr));
	//Expected personal optional get equal null --> throw
	assertThrows(NoSuchElementException.class, () -> skillEvaluationServiceImpl.saveSkillEvaluation(updateSkillEvaluationDto));
}

//update
@Test
@DisplayName("saveForwardedSkillEvaluation happy case")
@Transactional
void updateSaveForwardedSkillEvaluation_throwException() throws ParseException {


	UpdateSkillEvaluationDto updateSkillEvaluationDto = UpdateSkillEvaluationDto.builder()
			.id("id-test")
			.name("name test")
			.approver("cb43b124-e79c-40e5-91fb-a7b1c1a16328")
			.status("pending test")
			.level("level test")
			.newLevel("new level test")
			.skillCompetencyLeadId("0b43fe1c-63f2-11ed-8977-6c2408f06cc1")
			.build();
	
	UpdateSkillEvaluationDto updateSkillDto = UpdateSkillEvaluationDto.builder()
			.id("id-test")
			.name("name test")
			.approver("cb43b124-e79c-40e5-91fb-a7b1c1a16328")
			.status("pending test")
			.level("level test")
			.newLevel("new level test")
			.skillCompetencyLeadId("0b43fe1c-63f2-11ed-8977-6c2408f06cc1")
			.build();
	
	Personal personalApr = Personal.builder()
			.id("cb43b124-e79c-40e5-91fb-a7b1c1a16328")
			.build();
	
	Set<PersonalSkill> personalSkills = new HashSet<PersonalSkill>();
	personalSkills.add(PersonalSkill.builder()
			.id("Skill-id")
			.skill(Skill.builder().name("name test").build())    			
			.build());
	
	Personal personalReq = Personal.builder()
			.id("385edc75-b007-419f-b0cc-d9dff731e7a0")
			.personalSkills(personalSkills)
			.build();   
	
	Personal personalFwd = Personal.builder()
			.id("0b43fe1c-63f2-11ed-8977-6c2408f06cc1")
			.build();
	
	SkillEvaluation skillEvaluation = SkillEvaluation.builder()
			.id("id-test")
			.approveDate(simpleDateFormat.parse("2022-12-09"))
			.approver(personalApr)
			.status("pending test")
			.personal(personalReq)
			.build();
	
	Optional<SkillEvaluation> skillEvaluationOpt = Optional.ofNullable(skillEvaluation);
	
	when(skillEvaluationRepository.findById(updateSkillEvaluationDto.getId()))
		.thenReturn(skillEvaluationOpt);

	when(personalRepository.findById("0b43fe1c-63f2-11ed-8977-6c2408f06cc1"))
		.thenReturn(Optional.ofNullable(personalFwd));
	
	UpdateSkillEvaluationDto ufseDto = skillEvaluationServiceImpl.saveForwardedSkillEvaluation(updateSkillEvaluationDto);
	log.info(ufseDto.toString());
	assertThat(ufseDto).isEqualTo(updateSkillDto);
	log.info(skillEvaluation.getApprover().getId());
	assertThat(personalFwd).isEqualTo(skillEvaluation.getApprover());
}

//update
@Test
@DisplayName("saveForwardedSkillEvaluation null skill evaluation case")
@Transactional
void updateSaveForwardedSkillEvaluationNullSkillEvaluation_throwException() throws ParseException {

	UpdateSkillEvaluationDto updateSkillEvaluationDto = UpdateSkillEvaluationDto.builder()
			.id("id-test")
			.name("name test")
			.approver("cb43b124-e79c-40e5-91fb-a7b1c1a16328")
			.status("pending test")
			.level("level test")
			.newLevel("new level test")
			.skillCompetencyLeadId("0b43fe1c-63f2-11ed-8977-6c2408f06cc1")
			.build();
	

	when(skillEvaluationRepository.findById(updateSkillEvaluationDto.getId()))
	.thenThrow(EETResponseException.class);
	
	assertThrows(EETResponseException.class, () -> skillEvaluationServiceImpl.saveForwardedSkillEvaluation(updateSkillEvaluationDto));
}
}
