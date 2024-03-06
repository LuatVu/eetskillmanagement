package com.bosch.eet.skill.management.dto.excel;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class XLSXPersonalSkillClusterDTO {

    private String personalId;
    
    private List<String> skillCluster;

}
