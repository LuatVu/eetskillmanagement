package com.bosch.eet.skill.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto {
    
    private String id;
    
    @JsonProperty("department_name")
    private String name;
    
    @JsonProperty("GB_unit")
    private String gbUnit;
    
    
    
}