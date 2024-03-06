package com.bosch.eet.skill.management.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentGroupDto {

    private String id;

    private String name;
    
    private List<TeamDto> listTeams;

}
