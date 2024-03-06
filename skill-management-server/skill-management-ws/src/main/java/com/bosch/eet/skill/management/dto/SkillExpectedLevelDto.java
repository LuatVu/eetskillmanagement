package com.bosch.eet.skill.management.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillExpectedLevelDto {
	private String idSkill;
	private String nameSkill;
	private List<LevelExpected> levelExpecteds;
}
