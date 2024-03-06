package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.SkillCompetencyLeadDto;
import com.bosch.eet.skill.management.entity.SkillCompetencyLead;

@Component
public final class SkillCompetencyLeadConverterUtil {

	private SkillCompetencyLeadConverterUtil() {
		// prevent instantiation
	}
	
    public static SkillCompetencyLeadDto convertToDTO(SkillCompetencyLead skillCompetencyLead) {
        return SkillCompetencyLeadDto.builder()
        		.personalId(skillCompetencyLead.getPersonal().getId())
        		.displayName(skillCompetencyLead.getPersonal().getUser().getDisplayName())
                .description(skillCompetencyLead.getDescription())
                .build();
    }
    
    public static List<SkillCompetencyLeadDto> convertToDTOs(List<SkillCompetencyLead> skillCompetencyLeads) {
        List<SkillCompetencyLeadDto> skillCompetencyLeadDtos = new ArrayList<>();
        for (SkillCompetencyLead sCL : skillCompetencyLeads) {
        	skillCompetencyLeadDtos.add(convertToDTO(sCL));
        }
        return skillCompetencyLeadDtos;
    }
}
