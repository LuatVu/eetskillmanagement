package com.bosch.eet.skill.management.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.LevelDto;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.service.LevelService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DUP5HC
 */

@RequestMapping
@RestController
@Slf4j
public class LevelRest {
	
	@Autowired
	private LevelService levelService;
	
	@GetMapping(value = Routes.URI_REST_LEVEL)
	public GenericResponseDTO<List<LevelDto>> findAllLevel() {
		return GenericResponseDTO.<List<LevelDto>>builder()
				.code(MessageCode.SUCCESS.toString())
				.data(levelService.findAll())
				.build();
	}
	 
}


