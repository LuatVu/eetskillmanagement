package com.bosch.eet.skill.management.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LevelExpected {
	private String idLevel;
	private String nameLevel;
	private float value;
}
