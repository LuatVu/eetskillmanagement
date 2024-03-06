package com.bosch.eet.skill.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RequestEvaluationPendingDto {
	
	private String displayName;
	private String mail;
	private Long count;
	
}
