package com.bosch.eet.skill.management.rest;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.SkillEvaluationDto;
import com.bosch.eet.skill.management.dto.UpdateSkillEvaluationDto;
import com.bosch.eet.skill.management.exception.EETResponseException;
import com.bosch.eet.skill.management.service.SkillEvaluationService;

import io.github.jhipster.web.util.PaginationUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author TGI2HC
 */

@RequestMapping
@RestController
@Slf4j
public class SkillEvaluationRest {
	
	@Autowired
	private SkillEvaluationService skillEvaluationService;

	@GetMapping(value = Routes.URI_REST_SKILL_EVALUATION)
	public ResponseEntity<List<SkillEvaluationDto>> getSkillEvaluation(@RequestParam Map<String, String> filterParams,
			Pageable pageable, Authentication auht) {
		String requester = auht.getName();
		// find role by ntid	
		String getRequest = (filterParams.get("request"));
		if ((getRequest != null) && (Boolean. parseBoolean(getRequest))) {
			// role manager
			filterParams.put("requester", requester);
		} else {
			// role personal
			filterParams.put("approver", requester);
		}
		
		Page<SkillEvaluationDto> skillEvaluationDtos = skillEvaluationService.findAll(pageable, filterParams);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), skillEvaluationDtos);
		return ResponseEntity.ok().headers(headers).body(skillEvaluationDtos.getContent());
	}

	@GetMapping(value = Routes.URI_REST_SKILL_EVALUATION_PERSONAL_ID)
	public ResponseEntity<List<SkillEvaluationDto>> getSkillEvaluationByPersonalId(
			@PathVariable(name = "personal_id") String personalId){
		return ResponseEntity.ok().body(skillEvaluationService.findByPersonalId(personalId));
	}
 
	@GetMapping(value = Routes.URI_REST_SKILL_EVALUATION_ID)
	public ResponseEntity<SkillEvaluationDto> getProject(@PathVariable(name = "evaluate_id") String evaluation_id) {
		SkillEvaluationDto skillEvaluationDto = skillEvaluationService.findById(evaluation_id);
		return ResponseEntity.ok().body(skillEvaluationDto);
	}
	
	@PutMapping(value = Routes.URI_REST_SKILL_EVALUATION_ID)
	public ResponseEntity<UpdateSkillEvaluationDto> updateSkillEvaluation(
			@PathVariable(name = "evaluate_id") String evaluationId, @RequestBody UpdateSkillEvaluationDto updateSkillEvaluation) {
		SkillEvaluationDto skillEvaluationDto = skillEvaluationService.findById(evaluationId);
		if (!skillEvaluationDto.getId().isEmpty()) {
			updateSkillEvaluation.setId(evaluationId);
			UpdateSkillEvaluationDto skillUpdate = skillEvaluationService.saveSkillEvaluation(updateSkillEvaluation);
			 if (updateSkillEvaluation.getSkillCompetencyLeadId() != null) {
	                skillUpdate = skillEvaluationService.saveForwardedSkillEvaluation(skillUpdate);
	                return ResponseEntity.ok().body(skillUpdate);
	            }
			return ResponseEntity.ok().body(skillUpdate);
		} else {
			throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Skill evaluation ID can't be null!",
					null);
		}
	}

	@PostMapping(value = Routes.URI_REST_SKILL_EVALUATION)
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<List<SkillEvaluationDto>> createSkillEvaluation(@RequestBody List<SkillEvaluationDto> skillEvaluationDTOs){
		List<SkillEvaluationDto> evaluations = skillEvaluationDTOs.stream().map(
				skillEvaluationDto -> skillEvaluationService.saveNewSkillEvaluation(skillEvaluationDto)
		).collect(Collectors.toList());
		return ResponseEntity.ok(evaluations);
	}
}
