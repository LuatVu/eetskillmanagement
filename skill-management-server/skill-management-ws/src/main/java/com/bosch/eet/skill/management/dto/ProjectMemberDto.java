package com.bosch.eet.skill.management.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectMemberDto implements Serializable {
    @NotBlank(message = " is mandatory")
    private String id;
    
    private String name;
    private String role;
    
    @JsonProperty("role_id")
    @NotBlank(message = " is mandatory")
    private String roleId;
    
    @JsonProperty("personal_project_id")
    private String personalProjectId;
    
    @JsonProperty("common_task")
    private List<CommonTaskDto> commonTasks;
    
    @JsonProperty("additional_task")
    @Size(max = 250)
    private String additionalTask;

    @JsonProperty("start_date")
    @NotBlank(message = "is mandatory")
    private String startDate;
    
    @JsonProperty("end_date")
    private String endDate;
    
    private Date startDateDate;

    private Date endDateDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectMemberDto that = (ProjectMemberDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
