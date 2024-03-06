package com.bosch.eet.skill.management.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillGroupLevelDto {
    
	@JsonProperty("skill_name")
	private String skillName;
	
	@JsonProperty("skill_group")
	private String skillGroup;
	
	@JsonProperty("skill_levels")
	private List<SkillExperienceLevelDTO> skillLevels;
	
	@JsonProperty("skill_competency_leads")
	private List<SkillCompetencyLeadDto> competencyLeads;
	
    @JsonProperty("is_mandatory")
    private boolean isMandatory;

    @JsonProperty("is_required")
    private boolean isRequired;
    
    @JsonProperty("skill_type")
    private String skillType;
	
}