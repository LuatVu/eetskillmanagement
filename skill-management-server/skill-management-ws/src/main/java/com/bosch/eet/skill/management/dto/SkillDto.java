package com.bosch.eet.skill.management.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto implements Serializable{
    private static final long serialVersionUID = 1L;

    @JsonProperty("skill_id")
    private String id;

    private String name;

    private String competency;
    
    @JsonProperty("current_level")
    private String level;

    private Integer associates;

    @JsonProperty("skill_group")
    private String skillGroup;

    @JsonProperty("experience_number")
    private Integer experienceNumber;
    
    @JsonProperty("expected_level")
    private String expectedLevel;
    
    @JsonProperty("skill_type")
    private String skillType;
    
    @JsonProperty("skill_experience_level")
    private List<SkillExperienceLevelDTO> skillExperienceLevel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillDto skillDto = (SkillDto) o;
        return Objects.equals(name, skillDto.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public SkillDto(String name, Integer associates) {
        this.name = name;
        this.associates = associates;
    }

}
