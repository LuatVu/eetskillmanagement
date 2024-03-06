package com.bosch.eet.skill.management.elasticsearch.document;

import java.util.Date;

import javax.persistence.Id;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "course_#{@profile}")
public class DepartmentCourseDocument {

    @Id
    private String id;

    @Field
    private String courseId;

    @Field
    private String courseName;

    @Field
    private String courseType;

    @Field
    private String trainer;

    @Field
    private Date startDate;

    @Field
    private Integer effort;

    @Field
    private String targetAudience;

    @Field
    private String status;
}