// SkillConverter.java
package com.bosch.eet.skill.management.converter; 

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.dto.SkillDto;
import com.bosch.eet.skill.management.dto.SkillExperienceLevelDTO;
import com.bosch.eet.skill.management.dto.SkillManagementDto;
import com.bosch.eet.skill.management.elasticsearch.document.SkillDocument;
import com.bosch.eet.skill.management.elasticsearch.document.SkillExperienceLevelDocument;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillExperienceLevel;
import com.bosch.eet.skill.management.repo.LevelRepository;
import com.bosch.eet.skill.management.repo.SkillExperienceLevelRepository;

import lombok.extern.slf4j.Slf4j; 

@Component
@Slf4j
public class SkillConverter {
	
	@Autowired
	public SkillExperienceLevelRepository skillExperienceLevelRepository;

	@Autowired
	public LevelRepository levelRepository;
	
    public SkillExperienceLevelDTO convertToDetailLevelDTO(SkillExperienceLevel skillExperienceLevel) {
        SkillExperienceLevelDTO skillExpLevelDTO = new SkillExperienceLevelDTO();
        skillExpLevelDTO.setName(skillExperienceLevel.getName());
        skillExpLevelDTO.setDescription(skillExperienceLevel.getDescription());
        return skillExpLevelDTO;
    }

    public List<SkillExperienceLevelDTO> convertToDetailLevelDTOs(List<SkillExperienceLevel> skillExperienceLevels) {
        Set<SkillExperienceLevelDTO> skillExperienceLevelDTOSet = new HashSet<>();
        for (int i = 0; i < skillExperienceLevels.size(); i++) {
            skillExperienceLevelDTOSet.add(convertToDetailLevelDTO(skillExperienceLevels.get(i)));
        }
        return skillExperienceLevelDTOSet.stream().collect(Collectors.toList());
    }
    
    public SkillDto convertToDTO(Skill skill) {
        return SkillDto.builder()
                .id(skill.getId())
                .name(skill.getName())
                .competency(skill.getSkillGroup().getName())
                .build();
    } 

    public List<SkillDto> convertToDTOs(List<Skill> skills) {
        Set<SkillDto> skillDtos = new HashSet<>();
        for (Skill skill : skills) {
            skillDtos.add(convertToDTO(skill));
        }
        return skillDtos.stream().collect(Collectors.toList());
    }
    
    public SkillDto convertToPersonalSkillDto(Skill skill) {
    	return SkillDto.builder()
    			.id(skill.getId())
    			.name(skill.getName())
    			.competency(skill.getSkillGroup().getName())
    			.level(Constants.DEFAULT_SKILL_LEVEL)
    			.experienceNumber(Constants.DEFAULT_EXPERIENCE)
    			.build();
    }
    
    public SkillExperienceLevelDocument convertToSkillExpDocument (SkillExperienceLevel skillExperienceLevel) {
    	SkillExperienceLevelDocument skillExpDocument = new SkillExperienceLevelDocument();
    	if(skillExperienceLevel.getLevel() != null) {
    		skillExpDocument.setLevel(skillExperienceLevel.getLevel().getName());
    	}
    	skillExpDocument.setId(skillExperienceLevel.getId());
    	skillExpDocument.setSkillId(skillExperienceLevel.getSkill().getId());
    	skillExpDocument.setName(skillExperienceLevel.getName());
    	skillExpDocument.setDescription(skillExperienceLevel.getDescription());
    	
    	return skillExpDocument;
    	
    }
    
    public List<SkillExperienceLevelDocument> convertToListSkillExpDocument(List<SkillExperienceLevel> skillExperienceLevels) {
        Set<SkillExperienceLevelDocument> skillExperienceLevelDocumentSet = new HashSet<>();
        for (int i = 0; i < skillExperienceLevels.size(); i++) {
        	skillExperienceLevelDocumentSet.add(convertToSkillExpDocument(skillExperienceLevels.get(i)));
        }
        return skillExperienceLevelDocumentSet.stream().collect(Collectors.toList());
    }
    
    public SkillExperienceLevelDocument convertDTOToSkillExpDocument (SkillExperienceLevelDTO skillExperienceLevelDto) {
    	SkillExperienceLevelDocument skillExpDocument = new SkillExperienceLevelDocument();
    	skillExpDocument.setId(skillExperienceLevelDto.getId());
    	skillExpDocument.setName(skillExperienceLevelDto.getName());
    	skillExpDocument.setDescription(skillExperienceLevelDto.getDescription());
    	
    	return skillExpDocument;
    }
    
    public List<SkillExperienceLevelDocument> convertDTOToListSkillExpDocument (List<SkillExperienceLevelDTO> listSkillExpDto) {
        Set<SkillExperienceLevelDocument> skillExperienceLevelDocumentSet = new HashSet<>();
        for (int i = 0; i < listSkillExpDto.size(); i++) {
        	skillExperienceLevelDocumentSet.add(convertDTOToSkillExpDocument(listSkillExpDto.get(i)));
        }
        return skillExperienceLevelDocumentSet.stream().collect(Collectors.toList());
    }

    
    public SkillDocument convertToDocument (Skill skill) {
    	
    	List<SkillExperienceLevel> skillLevels = skillExperienceLevelRepository.findAllBySkillId(skill.getId());
    	List<SkillExperienceLevelDocument> skillLevelsDocument = convertToListSkillExpDocument(skillLevels);
    	
    	SkillDocument document = SkillDocument.builder()
    			.id(skill.getId())
    			.skillId(skill.getId())
    			.skillName(skill.getName())
    			.skillGroup(skill.getSkillGroup().getName())
    			.skillLevels(skillLevelsDocument)
    			.skillType(skill.getSkillGroup().getSkillType().getName())
    			.isMandatory(skill.getIsMandatory())
    			.isRequired(skill.getIsRequired())
    			.build();
    	
    	return document;
    }
    
    public List<SkillDocument> convertToListDocument (List<Skill> skills) {
    	List<SkillDocument> listDocuments = new ArrayList<>();
    	for(Skill skill : skills) {
    		listDocuments.add(convertToDocument(skill));
    	}
    	return listDocuments;
    }
    
    public SkillDocument convertDTOToDocument(SkillManagementDto skillDto) {
    	List<SkillExperienceLevelDTO> listSkills= skillDto.getSkillExperienceLevels();
    	List<SkillExperienceLevelDocument> listExpDocuments = convertDTOToListSkillExpDocument(listSkills);
    	
    	SkillDocument document = SkillDocument.builder()
    			.id(skillDto.getId())
    			.skillId(skillDto.getId())
    			.skillName(skillDto.getName())
    			.skillGroup(skillDto.getSkillGroup())
    			.skillLevels(listExpDocuments)
    			.isMandatory(skillDto.isMandatory())
    			.isRequired(skillDto.isRequired())
    			.skillType(skillDto.getSkillType())
    			.isMandatory(skillDto.isMandatory())
    			.isRequired(skillDto.isRequired())
    			.build();
    	    	
    	return document;
    }

}
