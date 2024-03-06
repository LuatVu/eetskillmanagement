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
@Document(indexName = "skill_#{@profile}")
public class SkillDocument {

	@Field
    private String id;

    @Field
    private String skillId;

    @Field
    private String skillName;

    @Field
    private String skillGroup;

    @Field
    private List<SkillExperienceLevelDocument> skillLevels;
    
    @Field
    private boolean isRequired;
    
    @Field
    private boolean isMandatory;

    @Field
    private String skillType;
}