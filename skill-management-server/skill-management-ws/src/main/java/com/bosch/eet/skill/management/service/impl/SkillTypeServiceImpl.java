package com.bosch.eet.skill.management.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.dto.SkillTypeDto;
import com.bosch.eet.skill.management.entity.SkillType;
import com.bosch.eet.skill.management.repo.SkillTypeRepository;
import com.bosch.eet.skill.management.service.SkillTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SkillTypeServiceImpl implements SkillTypeService {
	
	private final SkillTypeRepository skillTypeRepository;
	
	private final ModelMapper modelMapper;

	@Override
	public List<SkillTypeDto> findAllSkillType() {
		return skillTypeRepository.findAll().stream().map(item -> modelMapper.map(item, SkillTypeDto.class))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<SkillType> findAll() {
		return skillTypeRepository.findAll();
	}

}

