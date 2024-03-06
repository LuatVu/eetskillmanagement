package com.bosch.eet.skill.management.elasticsearch.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultQuery {
    
    private Float timeTook;
    private Integer numberOfResults;
    private String elements;    
}