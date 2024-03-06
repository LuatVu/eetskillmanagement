package com.bosch.eet.skill.management.elasticsearch.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalProjectDocument {
	
	private String id;
	
	private String projectName;
	
	private String tasks;
	
	private String additionalTasks;
	
	private String projectRole;
		
}
