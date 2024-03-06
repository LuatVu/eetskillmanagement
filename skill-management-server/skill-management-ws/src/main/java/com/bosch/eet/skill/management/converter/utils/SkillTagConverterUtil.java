/**
 * 
 */
package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.dto.SkillTagDto;
import com.bosch.eet.skill.management.entity.SkillTag;

import lombok.extern.slf4j.Slf4j;

/**
 * @author VOU6HC
 */

@Component
@Slf4j
public final class SkillTagConverterUtil {
	
	private SkillTagConverterUtil() {
		// prevent instantiation
	}
	
	public static SkillTagDto convertToSkillTagDetailDTO(SkillTag skillTag) {
        return SkillTagDto.builder()
                .id(skillTag.getId())
                .name(skillTag.getName())
                .order(skillTag.getOrder())
                .projectCount((int) skillTag.getProjectSkillTags().stream().filter(item ->
						item.getProject().getProjectType().getName().equalsIgnoreCase(Constants.BOSCH)).count())
                .build();
    }
	
	public static List<SkillTagDto> convertToListOfSkillTagDTO(List<SkillTag> skillTags) {
        List<SkillTagDto> skillTagDtoList = new ArrayList<>();
        StopWatch stopWatch = new StopWatch();
        log.info("Start converting skill tags to DTO");
        stopWatch.start();
        for (SkillTag st : skillTags) {
			skillTagDtoList.add(convertToSkillTagDetailDTO(st));
        }
        stopWatch.stop();
        log.info("Convert all skill tags to DTO took " + stopWatch.getTotalTimeMillis()
                + "ms ~= " + stopWatch.getTotalTimeSeconds() + "s ~=");
        return skillTagDtoList;
    }

	public static SkillTagDto convertToSimpleDTO(SkillTag skillTag){
		return SkillTagDto.builder()
				.id(skillTag.getId())
				.name(skillTag.getName())
				.build();
	}

	public static SkillTag mapDtoToEntity(SkillTagDto skillTagDto) {
		SkillTag skillTagEntity = new SkillTag();
		
		if (skillTagDto.getId() != null) {
			skillTagEntity.setId(skillTagDto.getId());
		}
		
		skillTagEntity.setName(skillTagDto.getName());
		return skillTagEntity;
		
	}

}
