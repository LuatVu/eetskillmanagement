package com.bosch.eet.skill.management.rest;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.SkillGroupDto;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.SkillFacade;
import com.bosch.eet.skill.management.service.SkillGroupService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DUP5HC
 * @author TAY3HC
 */

@RequestMapping
@RestController
@Slf4j
public class SkillGroupRest {
	
	@Autowired
	private SkillGroupService skillGroupService;

	@Autowired
	private SkillFacade skillFacade;

	@Autowired
	private MessageSource messageSource;
	
	@GetMapping(value = Routes.URI_REST_SKILL_GROUP)
	public GenericResponseDTO<List<SkillGroupDto>> getListSkillGroup() {
		return GenericResponseDTO.<List<SkillGroupDto>>builder()
				.code(MessageCode.SUCCESS.toString())
				.data(skillGroupService.findAllSkillGroups())
				.timestamps(new Date())
				.build();
	}

	@GetMapping(value = Routes.URI_REST_SKILL_GROUP_ID)
	public GenericResponseDTO<SkillGroupDto> getSkillGroup(@PathVariable(name = "id") String skillGroupId) {
		return GenericResponseDTO.<SkillGroupDto>builder()
				.code(MessageCode.SUCCESS.toString())
				.data(skillGroupService.findById(skillGroupId))
				.timestamps(new Date())
				.build();
	}

	@PostMapping(value = Routes.URI_REST_SKILL_GROUP)
	public GenericResponseDTO<SkillGroupDto> createSkillGroup(@RequestBody @Valid SkillGroupDto skillGroupDto) {
		return GenericResponseDTO.<SkillGroupDto>builder()
				.code(MessageCode.SUCCESS.toString())
				.data(skillGroupService.addSkillGroup(skillGroupDto))
				.timestamps(new Date())
				.build();
	}

	@PostMapping(value = Routes.URI_REST_DELETE_SKILL_GROUP)
	public GenericResponseDTO<SkillGroupDto> deleteSkillGroup(@PathVariable(name = "id") String skillGroupId) {
		if (StringUtils.isNotBlank(skillGroupId)) {
			skillFacade.deleteSkillGroup(skillGroupId);
			return GenericResponseDTO.<SkillGroupDto>builder()
					.code(MessageCode.SUCCESS.toString())
					.timestamps(new Date())
					.build();
		} else {
			throw new SkillManagementException(MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(),
					messageSource.getMessage(MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(), null,
							LocaleContextHolder.getLocale()), null, HttpStatus.NOT_FOUND);
		}
	}

}