package com.bosch.eet.skill.management.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.converter.utils.LevelConverterUtil;
import com.bosch.eet.skill.management.dto.LevelDto;
import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.repo.LevelRepository;
import com.bosch.eet.skill.management.service.LevelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LevelServiceImpl implements LevelService {
	
	
    private final LevelRepository levelRepository;
    
    @Override
    public List<String> findAllLevels() {
        return levelRepository.findAll().stream().map(Level::getName).collect(Collectors.toList());
    }

    @Override
    public List<LevelDto> findAll() {
    	return LevelConverterUtil.convertToSimpleDTOs(levelRepository.findByOrderByName());
    }
    
}