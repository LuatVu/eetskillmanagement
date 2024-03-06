package com.bosch.eet.skill.management.elasticsearch.document;

import java.util.List;

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
@Document(indexName = "project_#{@profile}")
public class DepartmentProjectDocument {

    @Id
    private String id;

    @Field
    private String projectId;

    @Field
    private String projectName;

    @Field
    private String projectType;

    @Field
    private String projectManager;

    @Field
    private String customerGB;

    @Field
    private String gbUnit;

    @Field
    private String team;

    @Field
    private String status;

    @Field
    private String startDate;

    @Field
    private String endDate;

    @Field
    private List<String> technologyUsed;

    @Field
    private boolean topProject;
    
    @Field
    private String teamSize;
    
    @Field
    private String objective;
    
    @Field
    private String description;

    @Field
    private String highlight;

    @Field
    private String scopeId;

    @Field
    private String scopeName;

}