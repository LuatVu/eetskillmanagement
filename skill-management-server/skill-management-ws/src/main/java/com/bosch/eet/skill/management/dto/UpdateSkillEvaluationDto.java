package com.bosch.eet.skill.management.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateSkillEvaluationDto implements Serializable{
	
	private String id;
	
	private String approver;
	
	private String name;

    private String status;

    private String level;
    
    @JsonProperty("new_level")
    private String newLevel;
	
	@JsonProperty("skill_competency_lead_id")
	private String skillCompetencyLeadId;	
}
