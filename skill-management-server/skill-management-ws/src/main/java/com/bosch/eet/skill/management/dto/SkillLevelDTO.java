package com.bosch.eet.skill.management.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillLevelDTO {

    private String level;

    private String description;
}
