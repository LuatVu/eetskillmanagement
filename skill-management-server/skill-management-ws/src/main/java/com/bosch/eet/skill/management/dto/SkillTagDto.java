package com.bosch.eet.skill.management.dto;


import java.util.Set;

import javax.validation.constraints.Size;

import com.bosch.eet.skill.management.entity.Project;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Getter
@Setter
public class SkillTagDto {
	
	private String id;

	@Size(max = 100)
	private String name;
	
	@Singular
	private Set<Project> projects;
	private Long order; 
	private Integer projectCount;
	@JsonProperty("is_mandatory")
	private Boolean isMandatory;

	public SkillTagDto(String id, String name, Long order, Boolean isMandatory, Integer projectCount) {
		this.id = id;
		this.name = name;
		this.order = order;
		this.projectCount = projectCount;
		this.isMandatory = isMandatory;
	}
}
