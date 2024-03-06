package com.bosch.eet.skill.management.converter.utils;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.text.SimpleDateFormat;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.SkillEvaluationDto;
import com.bosch.eet.skill.management.entity.SkillEvaluation;
import com.bosch.eet.skill.management.exception.EETResponseException;

@Component
public final class SkillEvaluationConverterUtil {

	private SkillEvaluationConverterUtil() {
		// prevent instantiation
	}
	

    public static SkillEvaluationDto convertToDTO(SkillEvaluation skillEvaluation) {
        if (Objects.isNull(skillEvaluation.getSkill())) {
			throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Skill not found", null);
		}
        if (Objects.isNull(skillEvaluation.getPersonal())) {
			throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Requester not found", null);
		}
        boolean isApproverExist = !Objects.isNull(skillEvaluation.getApprover());
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SkillEvaluationDto skillEvaluationDto = SkillEvaluationDto.builder()
                .id(skillEvaluation.getId())
                .name(skillEvaluation.getSkill().getName())
                .requester(skillEvaluation.getPersonal().getUser().getDisplayName())
                .approver(isApproverExist
                        ? skillEvaluation.getApprover().getUser().getDisplayName()
                        : null)
                .status(skillEvaluation.getStatus())
                .level(String.format("lv%s->lv%s",
                        skillEvaluation.getCurrentLevel(),
                        skillEvaluation.getTargetLevel()))
                .newLevel(String.format("Level %s",skillEvaluation.getTargetLevel()))
                .requestDate(simpleDateFormat.format(skillEvaluation.getDate()))
                .approverDate(!Objects.isNull(skillEvaluation.getApproveDate())
                        ? simpleDateFormat.format(skillEvaluation.getApproveDate())
                        : null)
                .oldLevel(String.valueOf(skillEvaluation.getCurrentLevel()))
                .newLevel(String.valueOf(skillEvaluation.getTargetLevel()))
                .build();
        if (!skillEvaluation.getSkill().getSkillGroup().getName().isEmpty()) {
        	skillEvaluationDto.setCompetency(skillEvaluation.getSkill().getSkillGroup().getName());
        }
        if (!Objects.isNull(skillEvaluation.getExperience())) {
        	skillEvaluationDto.setNewExperience(String.format("Level %s", skillEvaluation.getExperience()));
        	skillEvaluationDto.setExperience(String.format("%s", skillEvaluation.getExperience()));
        } else {
        	throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Cannot get skill experience!", null);
        }
        return skillEvaluationDto;
    }

    public static SkillEvaluation mapDtoToEntity(SkillEvaluationDto skillEvaluationDto) {
        SkillEvaluation entity = new SkillEvaluation();

        return entity;
    }

}
