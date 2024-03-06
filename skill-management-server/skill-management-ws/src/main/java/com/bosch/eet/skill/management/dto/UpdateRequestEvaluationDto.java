package com.bosch.eet.skill.management.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateRequestEvaluationDto implements Serializable{
	
	@JsonProperty("approver_id")
	private String approver;

    private String status;
    
    private String comment;
    
    private List<SkillDescriptionDTO> listDtos;	
}
