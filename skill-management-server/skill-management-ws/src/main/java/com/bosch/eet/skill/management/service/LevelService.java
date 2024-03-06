package com.bosch.eet.skill.management.service;

import java.util.List;

import com.bosch.eet.skill.management.dto.LevelDto;


public interface LevelService {

	List<String> findAllLevels();

	List<LevelDto> findAll();

}
