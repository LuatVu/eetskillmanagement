/**
 * @author VOU6HC
 */
package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.List;

import com.bosch.eet.skill.management.dto.SkillTypeDto;
import com.bosch.eet.skill.management.entity.SkillType;

public final class SkillTypeConverterUtil {
	
	private SkillTypeConverterUtil() {
		// prevent instantiation
	}
	
	public static SkillTypeDto convertToSkillTypeDetailDTO(SkillType skillType) {
        return SkillTypeDto.builder()
                .id(skillType.getId())
                .name(skillType.getName())
                .build();
	}
	
    public static List<SkillTypeDto> convertToListOfSkillTypeDTO(List<SkillType> skillTypes) {
        List<SkillTypeDto> skillTypeDtoList = new ArrayList<>();
        for (SkillType skillType : skillTypes) {
        	skillTypeDtoList.add(convertToSkillTypeDetailDTO(skillType));
        }
        return skillTypeDtoList;
    }

}
