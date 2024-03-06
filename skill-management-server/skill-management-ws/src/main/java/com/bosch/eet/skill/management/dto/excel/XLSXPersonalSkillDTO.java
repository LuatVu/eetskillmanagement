package com.bosch.eet.skill.management.dto.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class XLSXPersonalSkillDTO {

    private String personalId;

    private String skillName;

    private String level;
}
