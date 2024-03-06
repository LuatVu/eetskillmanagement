package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.LevelDto;
import com.bosch.eet.skill.management.entity.Level;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public final class LevelConverterUtil {

	private LevelConverterUtil() {
		// prevent instantiation
	}
    public static LevelDto convertToDTO(Level level) {
        return LevelDto.builder()
                .id(level.getId())
                .name(level.getName())
                .description(level.getDescription())
                .build();
	}

    public static List<LevelDto> convertToDTOs(List<Level> levels) {
        List<LevelDto> levelDtos = new ArrayList<>();
        for (Level level : levels) {
        	levelDtos.add(convertToDTO(level));
        }
        return levelDtos;
    }
    
    public static LevelDto convertToSimpleDTO(Level level) {
        return LevelDto.builder()
                .id(level.getId())
                .name(level.getName())
                .build();
	}
    
    public static List<LevelDto> convertToSimpleDTOs(List<Level> levels) {
        List<LevelDto> levelDtos = new ArrayList<>();
        for (Level level : levels) {
        	levelDtos.add(convertToSimpleDTO(level));
        }
        return levelDtos;
    }
}
