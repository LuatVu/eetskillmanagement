package com.bosch.eet.skill.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GbUnitDto {

    private String id;

    private String name;

    private Integer associates;

    private Integer projects;

    public GbUnitDto(String name, Integer associates, Integer projects) {
        this.name = name;
        this.associates = associates;
        this.projects = projects;
    }
}
