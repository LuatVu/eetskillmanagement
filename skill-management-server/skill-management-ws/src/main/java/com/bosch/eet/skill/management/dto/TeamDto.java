package com.bosch.eet.skill.management.dto;

import java.util.Objects;

import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamDto {

    private String id;

    private String name;

    private Integer associates;
    
    private String group;

    @JsonProperty("line_manager")
    private UserDTO lineManager;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamDto teamDto = (TeamDto) o;
        return Objects.equals(name, teamDto.name) &&
                Objects.equals(associates, teamDto.associates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, associates);
    }

    public TeamDto(String name, Integer associates) {
        this.name = name;
        this.associates = associates;
    }

}
