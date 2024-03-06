package com.bosch.eet.skill.management.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportDto {

    private Integer associates;

    private Integer internals;
    
    private Integer externals;
    
    @JsonProperty("fixedTerms")
    private Integer fixedTerms;

    @JsonProperty("associates_by_team")
    private List<TeamDto> associatesByTeam;

    @JsonProperty("associates_by_skill_cluster")
    private List<SkillGroupDto> associatesBySkillCluster;

    @JsonProperty("associates_by_levels")
    private List<LevelDto> associatesByLevels;

    @JsonProperty("associates_by_skills")
    private List<SkillDto> associatesBySkills;

    @JsonProperty("associates_by_gb")
    private List<GbUnitDto> associatesByGb;

    private Integer projects;

    @JsonProperty("projects_by_status")
    private List<ProjectDto> projectsByStatus;

    @JsonProperty("projects_by_gb")
    private List<GbUnitDto> projectsByGb;

    @JsonProperty("projects_by_skill_tags")
    private List<GroupProjectBySkillTag> projectBySkillTags;

}
