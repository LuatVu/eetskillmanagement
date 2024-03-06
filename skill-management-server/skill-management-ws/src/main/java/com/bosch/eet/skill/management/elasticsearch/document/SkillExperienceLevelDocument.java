package com.bosch.eet.skill.management.elasticsearch.document;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillExperienceLevelDocument {
	
	private String id;
	
	private String name;
	
	private String skillId;
	
	private String level;
	
	private String description;
		
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SkillExperienceLevelDocument that = (SkillExperienceLevelDocument) o;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
