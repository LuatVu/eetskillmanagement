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
public class PersonalCourseDto implements Serializable {
	
    private String id;

    @JsonProperty("display_name")
    private String displayName;
    
    @JsonProperty("personal_course_name")
    private String personalCourseName;

    @JsonProperty("personal_id")
    private String personalId;
    
    @JsonProperty("course_id")
    private String courseId;
    
    @JsonProperty("course_type")
    private String courseType;
    
    @JsonProperty("category_name")
    private String categoryName;
    
    @JsonProperty("start_date")
    private String startDate;
    
    @JsonProperty("end_date")
    private String endDate;
    
    private String status;
    
    private String certificate;
    
    private String trainer;
    
    private Integer duration;
	
}
