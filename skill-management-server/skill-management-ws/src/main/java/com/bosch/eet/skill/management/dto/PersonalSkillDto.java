package com.bosch.eet.skill.management.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
@Data
@Builder
public class PersonalSkillDto implements Serializable{

	private static final long serialVersionUID = 1L;

	private String id;
	
	@JsonProperty("personal_id")
	private String personalId;
	
	@JsonProperty("skill_id")
	private String skillId;
	
	private String level;
	
	private Integer experience;
}