package com.bosch.eet.skill.management.rest;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.ProjectScopeDto;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.service.ProjectScopeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProjectScopeRest {
	private final ProjectScopeService projectScopeService;
	
	@GetMapping(value = Routes.URI_REST_PROJECT_SCOPE)
	public GenericResponseDTO<List<ProjectScopeDto>> getAllProjectScope() {
		return GenericResponseDTO.<List<ProjectScopeDto>>builder().code(MessageCode.SUCCESS.toString())
				.data(projectScopeService.findAll())
				.timestamps(new Date())
				.build();
	}

	@GetMapping(value = Routes.URI_REST_PROJECT_SCOPE_ID)
	public GenericResponseDTO<ProjectScopeDto> findProjectScopeById(@PathVariable(name = "id") String projectScopeId) {
		return GenericResponseDTO.<ProjectScopeDto>builder().code(MessageCode.SUCCESS.toString())
				.data(projectScopeService.findById(projectScopeId))
				.timestamps(new Date())
				.build();
	}

	@PostMapping(value = Routes.URI_REST_PROJECT_SCOPE)
	public GenericResponseDTO<ProjectScopeDto> createProjectScope(@RequestBody @Valid ProjectScopeDto projectScopeDto) {
		return GenericResponseDTO.<ProjectScopeDto>builder().code(MessageCode.SUCCESS.toString())
				.data(projectScopeService.createNewProjectScope(projectScopeDto))
				.timestamps(new Date())
				.build();
	}

	@PostMapping(value = Routes.URI_REST_PROJECT_SCOPE_UPDATE)
	public GenericResponseDTO<ProjectScopeDto> editScope(@RequestBody @Valid ProjectScopeDto projectScopeDto) {
		return GenericResponseDTO.<ProjectScopeDto>builder().code(MessageCode.SUCCESS.toString())
				.data(projectScopeService.updateProjectScope(projectScopeDto))
				.timestamps(new Date())
				.build();
	}

	@PostMapping(value = Routes.URI_REST_PROJECT_SCOPE_DELETE)
	public GenericResponseDTO<String> deleteScope(@PathVariable(name = "id") String projectScopeId) {
		projectScopeService.deleteProjectScope(projectScopeId);
		return GenericResponseDTO.<String>builder().code(MessageCode.SUCCESS.toString())
				.data(Constants.SUCCESS)
				.timestamps(new Date())
				.build();
	}
}
