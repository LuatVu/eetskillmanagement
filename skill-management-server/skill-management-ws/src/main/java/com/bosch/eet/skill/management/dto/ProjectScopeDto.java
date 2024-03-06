package com.bosch.eet.skill.management.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectScopeDto {

	String id;

	@NotBlank(message = "is mandatory")
	@Size(max = 255)
	String name;

	@NotBlank(message = "is mandatory")
	@Size(max = 45)
	String colour;

	@JsonProperty("hover_colour")
	@NotBlank(message = "is mandatory")
	@Size(max = 45)
	String hoverColour;

}
