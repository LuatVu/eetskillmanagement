package com.bosch.eet.skill.management.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillDescriptionDTO {
    private String id;

    @JsonProperty("skill_id")
    private String skillId;

    @JsonProperty("skill")
    private String skillName;

    @JsonProperty("old_level")
    private String oldLevel;

    @JsonProperty("current_level")
    private String level;

    @JsonProperty("expected_level")
    private String expectedLevel;
    
    @JsonProperty("evaluated_level")
    private Integer evaluatedLevel;
    
    @JsonProperty("evaluated_exp")
    private Integer evaluatedExp;
    
    @JsonProperty("evaluated_comment")
    private String evaluatedComment;

    @JsonProperty("skill_group")
    private String competency;
    
    @JsonProperty("competency_lead")
    private SkillCompetencyLeadDto skillCompetencyLeadDto;

    private String comment;
    
    @JsonProperty("approver_comment")
    private String approverComment;

    @JsonProperty("old_experience")
    private Integer oldExperience;

    @JsonProperty("current_experience")
    private Integer experience;

    private String status;
    
    @JsonProperty("is_forwarded")
    private Boolean isForwarded;

    @JsonProperty("skill_description")
    private List<SkillLevelDTO> skillLevelDTOS;
    
    @JsonProperty("skill_type")
    private String skillType;
}
