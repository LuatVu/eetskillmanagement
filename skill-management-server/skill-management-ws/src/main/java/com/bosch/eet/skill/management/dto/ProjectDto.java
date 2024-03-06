package com.bosch.eet.skill.management.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
public class ProjectDto implements Serializable{

    @JsonProperty("project_id")
    private String id;

    @NotBlank(message = "is mandatory")
    @Size(max = 120)
    private String name;

    private String role;

    @JsonProperty("start_date")
    @NotBlank(message = "is mandatory")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;

    private List<String> tasks;

    @JsonProperty("additional_tasks")
    private List<String> additionalTasks;

    @JsonProperty("pm_name")
    @NotBlank(message = "is mandatory")
    private String pmName;

    @NotBlank(message = "is mandatory")
    private String status;

    @JsonProperty("team_size")
    @NotBlank(message = "is mandatory")
    @Pattern(regexp = "^[1-9][\\d]{0,1}$", message = "must be unsigned number and smaller than 100")
    private String teamSize;

    @JsonProperty("gb_unit")
    @NotBlank(message = "is mandatory")
    private String gbUnit;
    

    @JsonProperty("customer_gb")
    @NotBlank(message = "is mandatory")
    @Size(max = 120)
    private String customerGb;

    @JsonProperty("reference_link")
    @Size(max = 255)
    private String referenceLink;

    @JsonProperty("project_type")
    @NotBlank(message = "is mandatory")
    private String projectType;

    @JsonProperty("project_type_id")
    @NotBlank(message = "is mandatory")
    private String projectTypeId;

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

    private String createdBy;

    @NotEmpty(message = "is mandatory")
    @Valid
    private List<ProjectMemberDto> members;

    @JsonProperty("project_phases")
    @NotEmpty(message = "is mandatory")
    @NotNull(message = "phase may not be null")
    private Set<PhaseDto> phaseDtoSet;

    private Integer projects;

    @JsonProperty("top_project")
    private boolean isTopProject;
    
    @JsonProperty("skill_tags")
    @NotEmpty(message = "is mandatory")
    @Valid
    private Set<SkillTagDto> skillTags;

    @NotBlank(message = "is mandatory")
    @Size(max = 120)
    private String stakeholder;

    @Size(max = 1000)
    private String highlight;

    @JsonProperty("problem_statement")
    @Size(max = 65535)
    private String problemStatement;

    @Size(max = 65535)
    private String solution;

    @Size(max = 65535)
    private String benefits;

    @JsonProperty("project_scope_id")
    @NotBlank(message = "is mandatory")
    private String projectScopeId;

    @JsonProperty("project_scope_name")
    private String projectScopeName;

    // For non-bosch project
    @JsonProperty("personal_project_id")
    private String personalProjectId;

	public ProjectDto(String id, @NotBlank(message = "is mandatory") @Size(max = 120) String name) {
		super();
		this.id = id;
		this.name = name;
	}

}
