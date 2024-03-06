package com.bosch.eet.skill.management.elasticsearch.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalSkillDocument {
	
	private String id;
	
	private String skillName;
	
	private String skillGroup;
	
	private String level;
	
	private Integer experience;	
}
