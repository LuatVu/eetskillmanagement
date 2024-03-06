package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.SkillHighlightDto;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.SkillHighlight;
import com.bosch.eet.skill.management.facade.util.Utility;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public final class SkillsHighlightConverterUtil {
	
	private SkillsHighlightConverterUtil() {
		// prevent instantiation
	}
	
	public static List<SkillHighlight> convert2SkillHighlights(Personal personal, List<PersonalSkill> personalSkills) {
		return personalSkills.stream().map(item -> convert2SkillHighlight(personal, item)).collect(Collectors.toList());
	}

	public static SkillHighlight convert2SkillHighlight(Personal personal, PersonalSkill personalSkill) {
		return SkillHighlight.builder().personal(personal).personalSkill(personalSkill).build();
	}
	
	public static List<SkillHighlightDto> convertToDtos(List<PersonalSkill> personalSkills, List<SkillHighlight> skillHighlights){
		List<SkillHighlightDto> highlightDtos = new ArrayList<>();
		
		for (PersonalSkill personalSkill : personalSkills) {
			boolean isSelected =false;
			for (SkillHighlight skillHighlight : skillHighlights) {
				if(skillHighlight.getPersonalSkill().getSkill().getId().equals(personalSkill.getSkill().getId())) {
					isSelected=true;
					break;
				}
			}
			SkillHighlightDto skillHighlightDto=SkillHighlightDto.builder()
					.isSelected(isSelected)
					.experience(personalSkill.getExperience())
					.level(Utility.floatToStringLevelFormat(personalSkill.getLevel()))
					.skillId(personalSkill.getSkill().getId())
					.skillName(personalSkill.getSkill().getName())
					.build();
			highlightDtos.add(skillHighlightDto);
		}
		
		return highlightDtos;
	}

}
