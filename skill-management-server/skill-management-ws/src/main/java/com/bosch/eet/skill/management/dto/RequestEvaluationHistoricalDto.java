package com.bosch.eet.skill.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestEvaluationHistoricalDto {
	@JsonProperty("skill_name")
	private String skillName;
	@JsonProperty("skill_cluster")
	private String skillCluster;
	private String date;
	@JsonProperty("new_level")
	private float currentLevel;
	@JsonProperty("old_level")
	private float oldLevel;
	@JsonProperty("old_exp")
	private Integer oldExp;
	@JsonProperty("new_exp")
	private Integer currentExp;
	private String note;
}
