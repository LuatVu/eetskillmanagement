package com.bosch.eet.skill.management.converter.utils;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.text.SimpleDateFormat;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.RequestEvaluationDetailDto;
import com.bosch.eet.skill.management.dto.SkillDescriptionDTO;
import com.bosch.eet.skill.management.entity.RequestEvaluationDetail;
import com.bosch.eet.skill.management.exception.EETResponseException;
import com.bosch.eet.skill.management.facade.util.Utility;

@Component
public final class RequestEvaluationDetailConverterUtil {

	private RequestEvaluationDetailConverterUtil() {
		// prevent instantiation
	}
	

    public static RequestEvaluationDetailDto convertToDTO(RequestEvaluationDetail requestEvaluationDetail) {
        if (Objects.isNull(requestEvaluationDetail.getApprover())) {
			throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Approver not found", null);
		}
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        boolean isApproverExist = !Objects.isNull(requestEvaluationDetail.getApprover());
        RequestEvaluationDetailDto requestEvaluationDetailDto = RequestEvaluationDetailDto.builder()
                .id(requestEvaluationDetail.getId())
                .approver(isApproverExist
                        ? requestEvaluationDetail.getApprover().getUser().getDisplayName()
                        : null)
                .status(requestEvaluationDetail.getStatus())
                .createdDate(!Objects.isNull(requestEvaluationDetail.getCreatedDate())
                        ? simpleDateFormat.format(requestEvaluationDetail.getCreatedDate())
                        : null)
                .updateDate(!Objects.isNull(requestEvaluationDetail.getUpdatedDate())
                        ? simpleDateFormat.format(requestEvaluationDetail.getUpdatedDate())
                        : null)
                .approvedDate(!Objects.isNull(requestEvaluationDetail.getApprovedDate())
                        ? simpleDateFormat.format(requestEvaluationDetail.getApprovedDate())
                        : null)
                .comment(requestEvaluationDetail.getComment())
                .build();
        return requestEvaluationDetailDto;
    }
    
    public static RequestEvaluationDetailDto convertDetailToDTO(RequestEvaluationDetail requestEvaluationDetail) {
    	
        if (Objects.isNull(requestEvaluationDetail.getApprover())) {
			throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Approver not found", null);
		}
        boolean isApproverExist = !Objects.isNull(requestEvaluationDetail.getApprover());
        SkillDescriptionDTO skillDescription = SkillDescriptionDTO.builder()
								        .skillId(requestEvaluationDetail.getSkill().getId())
								        .skillName(requestEvaluationDetail.getSkill().getName())
								        .competency(Objects.nonNull(requestEvaluationDetail.getSkill().getSkillGroup())
								                ? requestEvaluationDetail.getSkill().getSkillGroup().getName()
								                : null)
								        .level(Utility.floatToStringLevelFormat(requestEvaluationDetail.getCurrentLevel()))
								        .experience(requestEvaluationDetail.getCurrentExp())
								        .comment(requestEvaluationDetail.getComment())
								        .status(requestEvaluationDetail.getStatus())
								        .build();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        RequestEvaluationDetailDto requestEvaluationDetailDto = RequestEvaluationDetailDto.builder()
                .id(requestEvaluationDetail.getId())
                .approver(isApproverExist
                        ? requestEvaluationDetail.getApprover().getUser().getDisplayName()
                        : null)
                .status(requestEvaluationDetail.getStatus())
                .createdDate(!Objects.isNull(requestEvaluationDetail.getCreatedDate())
                        ? simpleDateFormat.format(requestEvaluationDetail.getCreatedDate())
                        : null)
                .updateDate(!Objects.isNull(requestEvaluationDetail.getUpdatedDate())
                        ? simpleDateFormat.format(requestEvaluationDetail.getUpdatedDate())
                        : null)
                .approvedDate(!Objects.isNull(requestEvaluationDetail.getApprovedDate())
                        ? simpleDateFormat.format(requestEvaluationDetail.getApprovedDate())
                        : null)
                .skillDescription(skillDescription)
                .comment(requestEvaluationDetail.getComment())
                .build();
        return requestEvaluationDetailDto;
    }
}
