package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.bosch.eet.skill.management.dto.ProjectTypeDto;
import com.bosch.eet.skill.management.dto.SkillClusterDTO;
import com.bosch.eet.skill.management.dto.SkillDto;
import com.bosch.eet.skill.management.dto.SkillGroupDto;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillGroup;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public final class SkillGroupConverterUtil {

	private SkillGroupConverterUtil() {
		// prevent instantiation
	}
	
    public static SkillGroupDto convertToDTO(SkillGroup skillGroup) {
        SkillGroupDto skillGroupDto = SkillGroupDto.builder()
                .id(skillGroup.getId())
                .name(skillGroup.getName())
                .build();
        if (!Objects.isNull(skillGroup.getSkillType())) {
        	skillGroupDto.setSkillType(skillGroup.getSkillType().getName());
        	skillGroupDto.setSkillTypeId(skillGroup.getSkillType().getId());
        }

        List<SkillDto> skillDtos = new ArrayList<>();
        if (!skillGroup.getSkills().isEmpty()) {
            for (Skill skill : skillGroup.getSkills()) {
                if (skillGroup.getSkills() != null) {
                    skillDtos.add(SkillDto.builder()
                            .id(skill.getId())
                            .name(skill.getName())
                            .build());
                }
            }
        }

        skillGroupDto.setSkills(skillDtos);
        return skillGroupDto;
    }

    public static List<SkillGroupDto> convertToDTOs(List<SkillGroup> skillGroups) {
        Set<SkillGroupDto> skillGroupDtoSet = new HashSet<>();
        for (SkillGroup sg : skillGroups) {
            skillGroupDtoSet.add(convertToDTO(sg));
        }
        return skillGroupDtoSet.stream().collect(Collectors.toList());
    }

    public static SkillGroupDto convertToSimpleDto(SkillGroup skillGroup) {
    	SkillGroupDto skillGroupDto =  SkillGroupDto.builder()
                .id(skillGroup.getId())
                .name(skillGroup.getName())
                .numberAssociate(skillGroup.getPersonalSkillGroups().size())
                .build();
    	if (!Objects.isNull(skillGroup.getSkillType())) {
        	skillGroupDto.setSkillType(skillGroup.getSkillType().getName());
        	skillGroupDto.setSkillTypeId(skillGroup.getSkillType().getId());
        }
    	
    	return skillGroupDto;
    }

    public static List<SkillGroupDto> convertToSimpleDTOs(List<SkillGroup> skillGroups) {
        Set<SkillGroupDto> skillGroupDtoSet = new HashSet<>();
        StopWatch stopWatch = new StopWatch();
        log.info("Start converting skill groups to DTO");
        stopWatch.start();
        for (SkillGroup sg : skillGroups) {
            skillGroupDtoSet.add(convertToSimpleDto(sg));
        }
        stopWatch.stop();
        log.info("Convert all skill groups to DTO took " + stopWatch.getTotalTimeMillis()
                + "ms ~= " + stopWatch.getTotalTimeSeconds() + "s ~=");
        return skillGroupDtoSet.stream().collect(Collectors.toList());
    }

    public static List<SkillClusterDTO> mapToSkillClusterDtos(List<SkillGroup> skillGroups) {
        final ModelMapper modelMapper = new ModelMapper();
        return skillGroups.stream()
                .map(skg -> {
                    SkillClusterDTO dto = modelMapper.map(skg, SkillClusterDTO.class);
                    dto.getSkills().addAll(skg.getSkills().stream()
                            .map(i -> ProjectTypeDto.builder()
                                    .id(i.getId())
                                    .name(i.getName())
                                    .build())
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
