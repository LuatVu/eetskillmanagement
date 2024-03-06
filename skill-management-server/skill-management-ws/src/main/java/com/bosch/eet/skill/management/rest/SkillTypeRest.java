/**
 * 
 */
package com.bosch.eet.skill.management.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.SkillTypeDto;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.service.SkillTypeService;

import lombok.extern.slf4j.Slf4j;

@RequestMapping
@RestController
@Slf4j
public class SkillTypeRest {

	@Autowired
	private SkillTypeService skillTypeService;

	@GetMapping(value = Routes.URI_REST_SKILL_TYPE)
	public GenericResponseDTO<List<SkillTypeDto>> getAllSkillTags() {
		return GenericResponseDTO.<List<SkillTypeDto>>builder().code(MessageCode.SUCCESS.toString())
				.data(skillTypeService.findAllSkillType()).build();
	}

}
