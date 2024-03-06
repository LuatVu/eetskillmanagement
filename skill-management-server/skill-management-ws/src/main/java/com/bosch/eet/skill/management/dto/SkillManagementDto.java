package com.bosch.eet.skill.management.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillManagementDto implements Serializable {

    private String id;

    @NotEmpty(message = "is mandatory")
    @Size(max = 120)
    private String name;

    @JsonProperty("skill_group")
    private String skillGroup;

    @JsonProperty("skill_experience_levels")
    private List<SkillExperienceLevelDTO> skillExperienceLevels;

    @JsonProperty("competency_leads")
    private List<String> competencyLeads;

    @JsonProperty("is_mandatory")
    private boolean isMandatory;

    @JsonProperty("is_required")
    private boolean isRequired;
    
    private String skillType;
}
