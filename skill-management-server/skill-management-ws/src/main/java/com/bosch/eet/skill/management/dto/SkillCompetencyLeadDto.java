package com.bosch.eet.skill.management.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillCompetencyLeadDto implements Serializable {
    @JsonProperty("personal_id")
    private String personalId;
    
    @JsonProperty("display_name")
    private String displayName;
    
    @JsonProperty("skill_ids")
    private List<String> skillIds;
    
    @JsonProperty("skill_names")
    private List<String> skillNames;
    
    @JsonProperty("skill_cluster")
    private String skillCluster;
    
    private String description;
    
    @JsonProperty("skill_group_id")
    private String skillGroupId;
}
