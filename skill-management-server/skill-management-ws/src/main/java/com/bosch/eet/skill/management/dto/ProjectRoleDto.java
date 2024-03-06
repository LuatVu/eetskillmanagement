package com.bosch.eet.skill.management.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectRoleDto implements Serializable{
    private String id;
    @JsonProperty("name")
    private String name;
   
}
