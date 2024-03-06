package com.bosch.eet.skill.management.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalSkillEvaluationDto {
    
	private List<String> mainSkillCluster;
	
	private List<SkillDescriptionDTO> listSkill;
}