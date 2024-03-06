package com.bosch.eet.skill.management.dto;

import java.util.Objects;

import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillExperienceLevelDTO {

	private String id;
	
	private String name;
	
	@Size(max = 65535)
    private String description;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SkillExperienceLevelDTO that = (SkillExperienceLevelDTO) o;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
