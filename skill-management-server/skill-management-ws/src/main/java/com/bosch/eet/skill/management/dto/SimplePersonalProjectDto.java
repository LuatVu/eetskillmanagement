package com.bosch.eet.skill.management.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
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
public class SimplePersonalProjectDto implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

    @JsonProperty("project_id")
    @NotBlank(message = "is mandatory")
    private String projectId;

    @JsonProperty("role_id")
    @NotBlank(message = "is mandatory")
    private String roleId;

    @JsonProperty("additional_tasks")
    @Size(max = 250)
    private String additionalTasks;
    
    @JsonProperty("member_start_date")
    @NotBlank(message = "is mandatory")
    private String memberStartDate;
    
    @JsonProperty("member_end_date")
    private String memberEndDate;

}
