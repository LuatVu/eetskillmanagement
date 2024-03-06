package com.bosch.eet.skill.management.service.impl;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.common.SkillEvaluationStatus;
import com.bosch.eet.skill.management.converter.utils.PersonalSkillEvaluationConverterUtil;
import com.bosch.eet.skill.management.converter.utils.SkillEvaluationConverterUtil;
import com.bosch.eet.skill.management.dto.SkillEvaluationDto;
import com.bosch.eet.skill.management.dto.UpdateSkillEvaluationDto;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.PersonalSkillEvaluation;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillEvaluation;
import com.bosch.eet.skill.management.exception.EETResponseException;
import com.bosch.eet.skill.management.facade.util.Utility;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.PersonalSkillEvaluationRepository;
import com.bosch.eet.skill.management.repo.SkillEvaluationRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;
import com.bosch.eet.skill.management.service.SkillEvaluationService;
import com.bosch.eet.skill.management.specification.PersonalSkillEvaluationSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SkillEvaluationServiceImpl implements SkillEvaluationService {

    private final PersonalSkillEvaluationRepository personalSkillEvaluationRepository;

    private final SkillEvaluationRepository skillEvaluationRepository;

    private final PersonalRepository personalRepository;

    private final SkillRepository skillRepository;

    @Override
    public Page<SkillEvaluationDto> findAll(Pageable pageable, Map<String, String> q) {
        Specification<PersonalSkillEvaluation> projectSpecification = PersonalSkillEvaluationSpecification.search(q);
        Page<PersonalSkillEvaluation> personalSkillEvaluations = personalSkillEvaluationRepository.findAll(projectSpecification, pageable);
        return personalSkillEvaluations.map(PersonalSkillEvaluationConverterUtil::convertToDTO);
    }

    @Override
    public SkillEvaluationDto findById(String evaluationId) {
        Optional<SkillEvaluation> skillEvaluationOpt = skillEvaluationRepository.findById(evaluationId);
        if(skillEvaluationOpt.isPresent()){
            SkillEvaluation skillEvaluation = skillEvaluationOpt.get();
            Personal personal = skillEvaluation.getPersonal();
            if(!Objects.isNull(personal)){
                Set<PersonalSkill> personalSkills = personal.getPersonalSkills();
                if(!Objects.isNull(personalSkills) && !personalSkills.isEmpty()){
                    List<PersonalSkill> filterPersonalSkills = personalSkills
                            .stream()
                            .filter(x -> x.getSkill().getId().equals(skillEvaluation.getSkill().getId()))
                            .collect(Collectors.toList());
                    if(!filterPersonalSkills.isEmpty()){
                        PersonalSkill skill = filterPersonalSkills.get(0);
                        SkillEvaluationDto skillEvaluationDto = SkillEvaluationConverterUtil.convertToDTO(skillEvaluation);
                        skillEvaluationDto.setOldLevel(String.format("Level %s",skill.getLevel()));
                        skillEvaluationDto.setOldExperience(String.valueOf(skill.getExperience()));
                        return skillEvaluationDto;
                    } else {
                        throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Skill evaluation is not valid. Current skill not found.", null);
                    }
                } else {
                    throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Skill evaluation is not valid. Current skill not found.", null);
                }
            } else {
                throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Personal request not found", null);
            }
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Skill evaluation request not found", null);
        }
    }
    
@Override
@Transactional
public UpdateSkillEvaluationDto saveSkillEvaluation(UpdateSkillEvaluationDto updateskillEvaluationDto) {
    // Find the skill evaluation by ID
    Optional<SkillEvaluation> skillEvaluationOpt = skillEvaluationRepository
            .findById(updateskillEvaluationDto.getId());
    if (skillEvaluationOpt.isPresent()) {
        SkillEvaluation skillEvaluation = skillEvaluationOpt.get();
        // Update the status of the skill evaluation from DTO
        skillEvaluation.setStatus(updateskillEvaluationDto.getStatus());
        String approverId = updateskillEvaluationDto.getApprover();
        Optional<Personal> approverOpt = personalRepository.findById(approverId);
        if (approverOpt.isPresent()) {
            skillEvaluation.setApprover(approverOpt.get());
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "This manager doesn't exist!", null);
        }
        
        // Update level of personal skill
        String requesterId = skillEvaluation.getPersonal().getId();
        Optional<Personal> requesterOpt = personalRepository.findById(requesterId);
        Set<PersonalSkill> personalSkillsSet = new HashSet<>(requesterOpt.get().getPersonalSkills());
        List<PersonalSkill> personalSkills = new ArrayList<>(personalSkillsSet);
        if (personalSkills != null && !personalSkills.isEmpty()) {
            for (int i = 0; i < personalSkills.size(); i++) {
                PersonalSkill personalSkill = personalSkills.get(i);
                if (personalSkill.getSkill().getName().equals(updateskillEvaluationDto.getName())) {
                    personalSkill.setLevel(Utility.validateSkillLevel(updateskillEvaluationDto.getNewLevel()));
                }
            }
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()),
                    "This skill evaluation ID doesn't exist!", null);
        }
    }

    return updateskillEvaluationDto;
}

@Override
	@Transactional
	public UpdateSkillEvaluationDto saveForwardedSkillEvaluation(UpdateSkillEvaluationDto updateSkillEvaluationDto) {
// 			Find the skill evaluation by ID
		Optional<SkillEvaluation> skillEvaluationOpt = skillEvaluationRepository
				.findById(updateSkillEvaluationDto.getId());
		if (skillEvaluationOpt.isPresent()) {
			SkillEvaluation skillEvaluation = skillEvaluationOpt.get();
//			Update the status of the skill evaluation from DTO
			Optional<Personal> forwardedOpt = personalRepository
					.findById(updateSkillEvaluationDto.getSkillCompetencyLeadId());
			Personal forwardedPerson = forwardedOpt.get();
			if (forwardedOpt.isPresent()) {
				PersonalSkillEvaluation personalSkillEvaluation = new PersonalSkillEvaluation();
				personalSkillEvaluation.setPersonal(forwardedPerson);
				personalSkillEvaluation.setSkillEvaluation(skillEvaluation);
				personalSkillEvaluationRepository.save(personalSkillEvaluation);
				skillEvaluation.setApprover(forwardedPerson);
			} else {
				throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "This competency lead doesn't exist!",
						null);
			}
		} else {
			throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "This skill evaluation ID doesn't exist!",
					null);
		}
		return updateSkillEvaluationDto;
	}

    @Override
    public List<SkillEvaluationDto> findByPersonalId(String personalId) {
        List<SkillEvaluation> skillEvaluations = skillEvaluationRepository.findAllByPersonalId(personalId);
        return skillEvaluations.stream().map(SkillEvaluationConverterUtil::convertToDTO).collect(Collectors.toList());
    }
    @Override
    public SkillEvaluationDto saveNewSkillEvaluation(SkillEvaluationDto skillEvaluationDto) {

        Skill skill = skillRepository.findById(skillEvaluationDto.getName()).orElseThrow(
                () -> new EETResponseException(String.valueOf(NOT_FOUND.value()), "Skill not exist", null)
        );

        Personal requester = personalRepository.findByPersonalCodeIgnoreCase(skillEvaluationDto.getRequester()).orElseThrow(
                () -> new EETResponseException(String.valueOf(NOT_FOUND.value()), "This requester doesn't exist!", null)
        );

        if(skillEvaluationRepository.existsByPersonalIdAndSkillIdAndStatus(requester.getId(), skill.getId(), SkillEvaluationStatus.APPROVAL_PENDING.getValue())){
            throw new EETResponseException(String.valueOf(INTERNAL_SERVER_ERROR.value()), "Evaluation existed", null);
        }

        String managerId = Optional.ofNullable(requester.getManager()).orElseThrow(
                () -> new EETResponseException(String.valueOf(NOT_FOUND.value()), "This manager doesn't exist!", null)
        ).getId();

        Personal manager = personalRepository.findById(managerId).orElseThrow(
                () -> new EETResponseException(String.valueOf(NOT_FOUND.value()), "This manager doesn't exist!", null)
        );

        int currentLevel = 0;
        try {
            currentLevel = Integer.parseInt(skillEvaluationDto.getOldLevel());
        } catch (Exception e){
            log.error(e.getMessage());
        }

        int newLevel = 0;
        try {
            newLevel = Integer.parseInt(skillEvaluationDto.getNewLevel());
        } catch (Exception e){
            log.error(e.getMessage());
        }

        int experience = 0;
        try {
            experience = Integer.parseInt(skillEvaluationDto.getExperience());
        } catch (Exception e){
            log.error(e.getMessage());
        }

        SkillEvaluation skillEvaluation = SkillEvaluation.builder()
                .currentLevel(currentLevel)
                .targetLevel(newLevel)
                .date(new Date())
                .status(SkillEvaluationStatus.APPROVAL_PENDING.getValue())
                .experience(experience)
                .skill(skill)
                .personal(requester)
                .build();

        PersonalSkillEvaluation pse = PersonalSkillEvaluation.builder()
                .skillEvaluation(skillEvaluation)
                .personal(manager)
                .build();

        skillEvaluation.setPersonalSkillEvaluations(new ArrayList<>(Collections.singletonList(pse)));
        return SkillEvaluationConverterUtil.convertToDTO(skillEvaluationRepository.save(skillEvaluation));
    }

}
