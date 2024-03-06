package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.PersonalSkillDto;
import com.bosch.eet.skill.management.elasticsearch.document.PersonalSkillDocument;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.facade.util.Utility;

@Component
public final class PersonalSkillConverterUtil {

	private PersonalSkillConverterUtil() {
		// prevent instantiation
	}
	
    public static PersonalSkillDto convertToDTO(PersonalSkill personalSkill) {
        return PersonalSkillDto.builder()
                .id(personalSkill.getId())
                .level(Utility.floatToStringLevelFormat(personalSkill.getLevel()))
                .experience(personalSkill.getExperience())
                .build();
    }
    
    public static List<PersonalSkillDto> convertToDTOs(List<PersonalSkill> personalSkills) {
    	List<PersonalSkillDto> personalSkillDtos = new ArrayList<>();
        for (PersonalSkill personalSkill : personalSkills) {
            personalSkillDtos.add(convertToDTO(personalSkill));
        }
        return personalSkillDtos;
    }
    
    public static PersonalSkillDocument convertToDocument (PersonalSkill personalSkill) {
    	return PersonalSkillDocument.builder()
    			.id(personalSkill.getId())
    			.skillName(personalSkill.getSkill().getName())
    			.skillGroup(personalSkill.getSkill().getSkillGroup().getName())
    			.level(Utility.floatToStringLevelFormat(personalSkill.getLevel()))
    			.experience(personalSkill.getExperience())
    			.build();
    }
    
    public static List<PersonalSkillDocument> convertToListDocument (List<PersonalSkill> listPersonalSkill) {
    	List<PersonalSkillDocument> personalSkillDocuments = new ArrayList<>();
    	for(PersonalSkill personalSkill : listPersonalSkill) {
    		personalSkillDocuments.add(convertToDocument(personalSkill));
    	}
    	
    	return personalSkillDocuments;
    }
    
    public static PersonalSkillDto convertToDTO2(PersonalSkill personalSkill) {
        return PersonalSkillDto.builder()
                .skillId(personalSkill.getSkill().getId())
                .level(Utility.floatToStringLevelFormat(personalSkill.getLevel()))
                .experience(personalSkill.getExperience())
                .build();
    }
    
    public static List<PersonalSkillDto> convertToDTOs2(List<PersonalSkill> personalSkills) {
    	List<PersonalSkillDto> personalSkillDtos = new ArrayList<>();
        for (PersonalSkill personalSkill : personalSkills) {
            personalSkillDtos.add(convertToDTO2(personalSkill));
        }
        return personalSkillDtos;
    }
}
