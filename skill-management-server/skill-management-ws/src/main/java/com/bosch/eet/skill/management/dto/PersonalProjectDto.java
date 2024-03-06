package com.bosch.eet.skill.management.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalProjectDto implements Serializable{

    private String id;

    @NotBlank(message = "is mandatory")
    @Size(max = 120)
    private String name;

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("role_id")
    @NotBlank(message = "is mandatory")
    private String roleId;
    
    @JsonProperty("role_name")
    private String roleName;

    @NotBlank(message = "is mandatory")
    private String status;

    @JsonProperty("team_size")
    @NotBlank(message = "is mandatory")
    @Pattern(regexp = "^[1-9][\\d]{0,1}$", message = "must be unsigned number and smaller than 100")
    private String teamSize;

    @JsonProperty("project_type")
    @NotBlank(message = "is mandatory")
    private String projectType;

    @JsonProperty("start_date")
    @NotBlank(message = "is mandatory")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;

    private List<CommonTaskDto> tasks;

    @JsonProperty("additional_tasks")
    @NotBlank(message = "is mandatory for non-bosch project")
    @Size(max = 250)
    private String additionalTasks;

    @JsonProperty("pm_name")
    @NotBlank(message = "is mandatory")
    @Size(max = 250)
    private String pmName;

    @JsonProperty("gb_unit")
    private String gbUnit;

    @JsonProperty("reference_link")
    private String referenceLink;

    @JsonProperty("project_type_id")
    private String projectTypeId;

    @Size(max = 250)
    private String challenge;

    @NotBlank(message = "is mandatory")
    @Size(max = 65535)
    private String objective;

    @NotBlank(message = "is mandatory")
    @Size(max = 65535)
    private String description;

    private String department;
    
    @JsonProperty("technology_used")
    private String technologyUsed;
    
    @JsonProperty("skill_tags")
    @NotEmpty(message = "is mandatory")
    @Valid
    private Set<SkillTagDto> skillTags;

    private String createdBy;

    @JsonProperty("member_start_date")
    private String memberStartDate;

    @JsonProperty("member_end_date")
    private String memberEndDate;
}
