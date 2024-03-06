package com.bosch.eet.skill.management.service.impl;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.dto.ProjectScopeDto;
import com.bosch.eet.skill.management.entity.ProjectScope;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.repo.ProjectRepository;
import com.bosch.eet.skill.management.repo.ProjectScopeRepository;
import com.bosch.eet.skill.management.service.ProjectScopeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectScopeServiceImpl implements ProjectScopeService {

	private final ProjectScopeRepository projectScopeRepository;
	private final ProjectRepository projectRepository;
	private final MessageSource messageSource;
	private final ModelMapper modelMapper;


	/**
	 * Get all projectscope
	 *
	 * @return
	 */
	@Override
	public List<ProjectScopeDto> findAll() {
		List<ProjectScope> projectScopes = projectScopeRepository.findAll();
		return projectScopes.stream()
				.map(item -> modelMapper.map(item, ProjectScopeDto.class)).collect(Collectors.toList());
	}

	/**
	 * Get a projectScope by ID
	 *
	 * @param projectScopeId
	 * @return
	 */
	@Override
	public ProjectScopeDto findById(String projectScopeId) {
		ProjectScope projectScope = projectScopeRepository.findById(projectScopeId).orElseThrow(
			() -> new SkillManagementException(MessageCode.PROJECT_SCOPE_NOT_FOUND.toString(),
				messageSource.getMessage(MessageCode.PROJECT_SCOPE_NOT_FOUND.toString(), null,
						LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST)
		);
		return modelMapper.map(projectScope, ProjectScopeDto.class);
	}

	/**
	 * Create new projectScope
	 *
	 * @param projectScopeDto
	 * @return
	 */
	@Override
	@Transactional
	public ProjectScopeDto createNewProjectScope(ProjectScopeDto projectScopeDto) {
		String scopeName = projectScopeDto.getName().trim();
		Optional<ProjectScope> projectScopeOptional = projectScopeRepository.findByNameIgnoreCase(scopeName);
		if(projectScopeOptional.isPresent()){
			throw new SkillManagementException(MessageCode.PROJECT_SCOPE_NAME_ALREADY_EXIST.toString(),
					messageSource.getMessage(MessageCode.PROJECT_SCOPE_NAME_ALREADY_EXIST.toString(), null,
							LocaleContextHolder.getLocale()), null, NOT_FOUND);
		}

		ProjectScope projectScope = ProjectScope.builder()
				.name(scopeName)
				.colour(projectScopeDto.getColour().trim())
				.hoverColour(projectScopeDto.getHoverColour().trim())
				.build();

		return modelMapper.map(projectScopeRepository.save(projectScope), ProjectScopeDto.class);
	}

	/**
	 * Update projectScope
	 *
	 * @param projectScopeDto
	 * @return
	 */
	@Override
	@Transactional
	public ProjectScopeDto updateProjectScope(ProjectScopeDto projectScopeDto) {
		ProjectScope projectScope = projectScopeRepository.findById(projectScopeDto.getId()).orElseThrow(
				() -> new SkillManagementException(MessageCode.PROJECT_SCOPE_NOT_FOUND.toString(),
						messageSource.getMessage(MessageCode.PROJECT_SCOPE_NOT_FOUND.toString(), null,
								LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST)
		);

		projectScope.setName(projectScopeDto.getName().trim());
		projectScope.setColour(projectScopeDto.getColour().trim());
		projectScope.setHoverColour(projectScopeDto.getHoverColour().trim());

		return modelMapper.map(projectScopeRepository.save(projectScope), ProjectScopeDto.class);
	}

	/**
	 * Delete projectScope
	 * - Set null to projects that use the projectScope
	 *
	 * @param projectScopeId
	 */
	@Override
	@Transactional
	public void deleteProjectScope(String projectScopeId) {
		projectScopeRepository.findById(projectScopeId).orElseThrow(
				() -> new SkillManagementException(MessageCode.PROJECT_SCOPE_NOT_FOUND.toString(),
						messageSource.getMessage(MessageCode.PROJECT_SCOPE_NOT_FOUND.toString(), null,
								LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST)
		);
		projectRepository.deleteProjectScopeFromProject(projectScopeId);
		projectScopeRepository.deleteById(projectScopeId);
	}
}
