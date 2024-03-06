package com.bosch.eet.skill.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LevelDto {
    
    private String id;
    private String name;
    private String description;
    private Integer associates;

    public LevelDto(String name, Integer associates) {
        this.name = name;
        this.associates = associates;
    }

}