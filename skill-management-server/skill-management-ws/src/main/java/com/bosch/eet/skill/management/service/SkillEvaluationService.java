package com.bosch.eet.skill.management.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bosch.eet.skill.management.dto.SkillEvaluationDto;
import com.bosch.eet.skill.management.dto.UpdateSkillEvaluationDto;

public interface SkillEvaluationService {
    Page<SkillEvaluationDto> findAll(Pageable pageable, Map<String,String> q);
    SkillEvaluationDto findById(String evaluationId);
    UpdateSkillEvaluationDto saveForwardedSkillEvaluation(UpdateSkillEvaluationDto updateSkillEvaluationDto);
	UpdateSkillEvaluationDto saveSkillEvaluation(UpdateSkillEvaluationDto updateSkillEvaluation);
    List<SkillEvaluationDto> findByPersonalId(String personalId);
    SkillEvaluationDto saveNewSkillEvaluation(SkillEvaluationDto skillEvaluationDto);
}
