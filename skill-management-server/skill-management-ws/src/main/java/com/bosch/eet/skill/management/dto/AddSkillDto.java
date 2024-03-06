package com.bosch.eet.skill.management.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddSkillDto {
    	
    private PersonalDto personalDto;
    
    @JsonProperty("skill_group_ids")
    private List<String> skillGroupIds;
    
}