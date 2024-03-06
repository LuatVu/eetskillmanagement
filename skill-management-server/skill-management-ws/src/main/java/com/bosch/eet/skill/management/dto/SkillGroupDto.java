package com.bosch.eet.skill.management.dto;

import java.util.List;
import java.util.Objects;

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
public class SkillGroupDto {

    private String id;
    
    @NotBlank(message = "is mandatory")
    @Size(max = 45)
    private String name;

    private Integer associates;

    @JsonProperty("skills")
    private List<SkillDto> skills;

    @JsonProperty("competency_leads")
    private List<PersonalDto> competencyLeads;
    
	@JsonProperty("skill_type")
	private String skillType;

	@JsonProperty("skill_type_id")
	@NotBlank(message = "is mandatory")
	private String skillTypeId;

	@JsonProperty("number_associate")
	private int numberAssociate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillGroupDto that = (SkillGroupDto) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public SkillGroupDto(String name, Integer associates) {
        this.name = name;
        this.associates = associates;
    }

}
