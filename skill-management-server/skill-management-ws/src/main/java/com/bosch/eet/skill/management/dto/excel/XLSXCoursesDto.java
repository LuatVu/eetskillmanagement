package com.bosch.eet.skill.management.dto.excel;

import com.bosch.eet.skill.management.entity.Course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class XLSXCoursesDto {

    private Course course;

    private String trainer;

    private String date;

    private String category;

    private Integer duration;

    private String type;

    private String targetAudience;
    
    private String desription;
    
    private String status;
}
