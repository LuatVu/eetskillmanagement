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
@Document(indexName = "personal_#{@profile}")
public class PersonalDocument {

    @Id
    private String id;

    @Field
    private String personalId;

    @Field
    private String personalCode;

    @Field
    private String displayName;

    @Field
    private String level;

    @Field
    private String team;

    @Field
    private Integer experience;

    @Field
    private String department;

    @Field
    private String gbUnit;

    @Field
    private String location;

    @Field
    private List<String> skills;

    @Field
    private List<String> skillGroups;
}