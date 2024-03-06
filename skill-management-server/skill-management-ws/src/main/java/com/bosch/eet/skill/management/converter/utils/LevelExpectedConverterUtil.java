package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.dto.LevelExpected;
import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.entity.SkillLevel;
import com.bosch.eet.skill.management.facade.util.Utility;
import com.bosch.eet.skill.management.repo.LevelRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public final class LevelExpectedConverterUtil {

	private LevelExpectedConverterUtil() {
		// prevent instantiation
	}
	
	@Autowired
	private  LevelRepository levelRepository;

	public  List<LevelExpected> convertToDtos(List<SkillLevel> skillExpecteds) {
		Map<Level, SkillLevel> mapLevelSkillLevel = new HashMap<>();
		for (SkillLevel sl : skillExpecteds) {
			mapLevelSkillLevel.put(sl.getLevel(), sl);
		}
		List<LevelExpected> levelExpecteds = new ArrayList<>();
		List<Level> levels = levelRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
		for (Level level : levels) {
			if (mapLevelSkillLevel.containsKey(level)) {
				levelExpecteds.add(convertToDto(mapLevelSkillLevel.get(level)));
			} else {
				LevelExpected levelExpected = LevelExpected.builder().idLevel(level.getId()).nameLevel(level.getName())
						.value(0).build();
				levelExpecteds.add(levelExpected);
			}
		}
		return levelExpecteds;
	}

	public static LevelExpected convertToDto(SkillLevel skillLevel) {
		return LevelExpected.builder().idLevel(skillLevel.getLevel().getId()).nameLevel(skillLevel.getLevel().getName())
				.value(Utility.parseFloat(skillLevel.getLevelLable().split(Constants.WHITE_SPACE)[1])).build();
	}
}
