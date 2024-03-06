package com.bosch.eet.skill.management.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PhaseDto {
	private String id;
	private String name;
	private String description;
	private List<ProjectDto> projects;
}
