package com.bosch.eet.skill.management.elasticsearch.document;

import java.util.List;

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
@Document(indexName = "customer_#{@profile}")
public class CustomerGBDocument {

	@Field
    private String id;
	
    @Field
    private String name;

    @Field
    private Integer numOfProject;

    @Field
    private Integer numOfHC;
    
    @Field
    private Integer toolAccross;

    @Field
    private  List<DepartmentProjectSimpleDocument> projects;

    @Field
    private  List<ProjectSkillTagSimpleDocument> projectSkillTagSimpleDocuments;
}