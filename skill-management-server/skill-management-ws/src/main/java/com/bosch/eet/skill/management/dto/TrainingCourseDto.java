package com.bosch.eet.skill.management.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingCourseDto implements Serializable {

	private String id;
	
    @JsonProperty("course_id")
    private String courseId;

    private String name;
    
    private String categoryName;
    
    private String category;
    
    private String trainer;

    @JsonProperty("course_type")
    private String courseType;
    
    private Integer duration;
    
    @JsonProperty("target_audience")
    private String targetAudience;

    private String date;
        
    private String status;
    
    private String description;
}
