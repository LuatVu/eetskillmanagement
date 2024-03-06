/**
 * 
 */
package com.bosch.eet.skill.management.rest;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.SkillTagDto;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.facade.SkillTagFacade;
import com.bosch.eet.skill.management.service.ProjectSkillTagService;
import com.bosch.eet.skill.management.service.SkillTagService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author VOU6HC
 */

@RequestMapping
@RestController
@Slf4j
public class SkillTagRest {

	@Autowired
	private SkillTagService skillTagService;

	@Autowired
	private SkillTagFacade skillTagFacade;
	
	@Autowired
	private ProjectSkillTagService projectSkillTagService;

	@GetMapping(value = Routes.URI_REST_SKILL_TAG)
	public GenericResponseDTO<List<SkillTagDto>> getAllSkillTags() {
		return GenericResponseDTO.<List<SkillTagDto>>builder().code(MessageCode.SUCCESS.toString())
				.data(skillTagService.findAllSkillTags()).timestamps(new Date()).build();
	}

	@GetMapping(value = Routes.URI_REST_PROJECT_ID_SKILL_TAG)
	public GenericResponseDTO<List<SkillTagDto>> getSkillTagsByProjectId(@PathVariable(value = "project_id") String projectId){
		return GenericResponseDTO.<List<SkillTagDto>>builder()
				.code(MessageCode.SUCCESS.toString())
				.data(projectSkillTagService.findSkillTagsByProjectsId(projectId))
				.timestamps(new Date())
				.build();
		
	}

	@GetMapping(value = Routes.URI_REST_SKILL_TAG_ID)
	public GenericResponseDTO<SkillTagDto> getSkillTagsById(@PathVariable(value = "id") String skillTagId) {
		return GenericResponseDTO.<SkillTagDto>builder().code(MessageCode.SUCCESS.toString())
				.data(skillTagService.findById(skillTagId)).timestamps(new Date()).build();

	}

	@GetMapping(value = Routes.URI_REST_SKILL_TAG_PROJECT)
	public GenericResponseDTO<List<ProjectDto>> getAllProjectsBySkillTagId(
			@PathVariable(value = "id") String skillTagId) {
		return GenericResponseDTO.<List<ProjectDto>>builder().code(MessageCode.SUCCESS.toString())
				.data(skillTagService.findProjectsBySkillTagsId(skillTagId)).timestamps(new Date()).build();
	}

	@PostMapping(value = Routes.URI_REST_SKILL_TAG)
	public GenericResponseDTO<SkillTagDto> addSkillTags(@RequestBody SkillTagDto skillTagRequest) {
		return GenericResponseDTO.<SkillTagDto>builder().code(MessageCode.SUCCESS.toString())
				.data(skillTagService.addSkillTag(skillTagRequest)).timestamps(new Date()).build();
	}

	@PostMapping(value = Routes.URI_REST_SKILL_TAG_ID)
	public GenericResponseDTO<SkillTagDto> updateSkillTag(@PathVariable(value = "id") String skillTagId,
			@RequestBody SkillTagDto skillTagRequest) {
		return GenericResponseDTO.<SkillTagDto>builder().code(MessageCode.UPDATE_INDEX_SUCCESS.toString())
				.data(skillTagService.updateSkillTag(skillTagId, skillTagRequest)).timestamps(new Date()).build();

	}
	
	@PostMapping(value = Routes.URI_REST_SKILL_TAG_UPDATE_ORDER)
	public GenericResponseDTO<String> updateOrder(@RequestBody List<SkillTagDto> skillTagDtos){
		return GenericResponseDTO.<String>builder().code(MessageCode.UPDATE_INDEX_SUCCESS.toString())
				.data(skillTagService.updateOrder(skillTagDtos)).timestamps(new Date()).build();
	}
	
	/*
	 * This API using for replace between 2 skill tags, first skill tag is the old one
	 * and will be replaced with the second one*/
	@PostMapping(value = Routes.URI_REST_SKILL_TAG_REPLACE)
	public GenericResponseDTO<String> replaceSkillTag(@RequestBody List<SkillTagDto> skillTagRequests){
		return GenericResponseDTO.<String>builder().code(MessageCode.UPDATE_INDEX_SUCCESS.toString())
				.data(skillTagFacade.replaceSkillTag(skillTagRequests)).timestamps(new Date()).build();
	}
	
}
