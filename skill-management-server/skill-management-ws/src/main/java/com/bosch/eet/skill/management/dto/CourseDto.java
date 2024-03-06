package com.bosch.eet.skill.management.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseDto implements Serializable {

    @JsonProperty("course_id")
    private String id;

    private String name;

    @JsonProperty("course_type")
    private String courseType;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;
        
    private String status;
    
    private String category;

    private String certificate;

    private String description;
}
