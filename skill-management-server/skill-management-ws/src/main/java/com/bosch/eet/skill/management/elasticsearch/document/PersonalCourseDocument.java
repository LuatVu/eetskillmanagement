package com.bosch.eet.skill.management.elasticsearch.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalCourseDocument {
	
	private String id;
	
	private String courseName;
	
    private String status;

    private String certificate;

    private Integer duration;
    
}
