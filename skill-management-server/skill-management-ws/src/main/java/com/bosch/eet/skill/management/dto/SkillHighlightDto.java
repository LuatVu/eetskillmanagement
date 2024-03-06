package com.bosch.eet.skill.management.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SkillHighlightDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("skill_id")
	private String skillId;
	
	private String skillName;
	
	private String level;
	
	private Integer experience;
	
	private boolean isSelected;
}
