package com.bosch.eet.skill.management.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommonTaskDto implements Serializable{
    private String id;
    @JsonProperty("name")
    @NotBlank(message = "is mandatory")
    @Size(max = 45, message = "max size is 45")
    private String name;
    @JsonProperty("project_role_id")
    private String project_role_id;
}
