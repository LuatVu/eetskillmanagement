package com.bosch.eet.skill.management.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailDTO {
	
	String id;

    String name;
    
    String hightlight;
   
    String corporation;

    @JsonProperty("head_counts")
    Integer headCounts;

    @JsonProperty("v_model_count")
    Integer vModelCount;

    @JsonProperty("gb_info")
    Map<String, Integer> gbInfo;
    
}
