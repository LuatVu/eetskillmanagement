/**
 * 
 */
package com.bosch.eet.skill.management.service.impl;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.converter.utils.ProjectConverterUtil;
import com.bosch.eet.skill.management.converter.utils.SkillTagConverterUtil;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.SkillTagDto;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectSkillTag;
import com.bosch.eet.skill.management.entity.SkillTag;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.repo.ProjectSkillTagRepository;
import com.bosch.eet.skill.management.repo.SkillTagRepository;
import com.bosch.eet.skill.management.service.SkillTagService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author VOU6HC
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class SkillTagServiceImpl implements SkillTagService {

	private final MessageSource messageSource;

	private final SkillTagRepository skillTagRepository;

	private final ProjectConverterUtil projectConverter;

	private final ProjectSkillTagRepository projectSkillTagRepository;
	

	@Override
	public List<SkillTagDto> findAllSkillTags() {
		return skillTagRepository.findAllDetailDtoCountProjectByType(Constants.BOSCH);
	}

	@Override
	public SkillTagDto findById(String skillTagId) {
		Optional<SkillTag> skillTagOpt = skillTagRepository.findById(skillTagId);
		if (skillTagOpt.isPresent()) {
			return convertToDetailDto(skillTagOpt.get());
		} else {
			throw new SkillManagementException(MessageCode.SKILL_TAG_NOT_FOUND.toString(), messageSource
					.getMessage(MessageCode.SKILL_TAG_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale()),
					null, NOT_FOUND);
		}
	}

	public SkillTagDto convertToDetailDto(SkillTag skillTag) {
		return SkillTagConverterUtil.convertToSkillTagDetailDTO(skillTag);
	}

	@Override
	public List<SkillTag> saveAll(Iterable<SkillTag> skillTags){
		return skillTagRepository.saveAll(skillTags);
	}


	@Override
	public List<SkillTag> findAll(){
		return skillTagRepository.findAll();
	}

	@Override
	public SkillTag findByNameIgnoreCase(String name){
		Optional<SkillTag> skillTagOpt = skillTagRepository.findByNameIgnoreCase(name);
		return skillTagOpt.orElse(null);
	}

	@Override
	public List<ProjectDto> findProjectsBySkillTagsId(String skillTagId) {
		List<Project> projects = new ArrayList<Project>();
		if (!skillTagRepository.existsById(skillTagId)) {
			throw new SkillManagementException(MessageCode.SKILL_TAG_NOT_FOUND.toString(), messageSource
					.getMessage(MessageCode.SKILL_TAG_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale()),
					null, NOT_FOUND);
		}

		projects = projectSkillTagRepository.findProjectBySkillTagId(skillTagId);
		return projectConverter.convertToListOfProjectContainSkillTag(projects);
	}

	@Override
	@Transactional
	public SkillTagDto addSkillTag(SkillTagDto skillTagRequestDto) {
		SkillTag skillTag = new SkillTag();
		if (skillTagRequestDto.getName().isEmpty()) {
			throw new SkillManagementException(
					MessageCode.NAME_IS_REQUIRED.toString(), messageSource
							.getMessage(MessageCode.NAME_IS_REQUIRED.toString(), null, LocaleContextHolder.getLocale()),
					null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// check if the skill tag is exists before adding a new one
		Optional<SkillTag> skillTagOpt = skillTagRepository.findByName(skillTagRequestDto.getName());
		if (skillTagOpt.isPresent()) {
			SkillTag _skillTag = skillTagOpt.get();
			if (_skillTag.getName().equals(skillTagRequestDto.getName())) {
				throw new SkillManagementException(MessageCode.SKILL_TAG_ALREADY_EXISTS.toString(),
						messageSource.getMessage(MessageCode.SKILL_TAG_ALREADY_EXISTS.toString(), null,
								LocaleContextHolder.getLocale()),
						null, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		skillTag.setName(skillTagRequestDto.getName());
		skillTagRepository.save(skillTag);
		return convertToSkillTagSimpleDto(skillTag);

	}

	@Override
	public SkillTagDto updateSkillTag(String skillTagId, SkillTagDto skillTagRequestDto) {
		SkillTag skillTag = getSkillTagEntity(skillTagRequestDto);
		skillTag = skillTagRepository.findById(skillTagId).orElseThrow(() -> new SkillManagementException(
				MessageCode.SKILL_TAG_NOT_FOUND.toString(), messageSource
						.getMessage(MessageCode.SKILL_TAG_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale()),
				null, NOT_FOUND));
		skillTag.setName(skillTagRequestDto.getName());
		skillTagRepository.save(skillTag);
		return convertToSkillTagSimpleDto(skillTag);
	}

	public SkillTagDto convertToSkillTagSimpleDto(SkillTag skillTag) {
		return SkillTagConverterUtil.convertToSimpleDTO(skillTag);
	}

	public SkillTag getSkillTagEntity (SkillTagDto skillTagRequestDto) {
		return SkillTagConverterUtil.mapDtoToEntity(skillTagRequestDto);
	}

	@Override
	@Transactional
	public boolean deleteSkillTag(SkillTagDto skillTagRequestDto) {
		Optional<SkillTag> skillTagOpt = skillTagRepository.findById(skillTagRequestDto.getId());
		if (!skillTagOpt.isPresent()) {
			throw new SkillManagementException(MessageCode.SKILL_TAG_NOT_FOUND.toString(), messageSource
					.getMessage(MessageCode.SKILL_TAG_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale()),
					null, NOT_FOUND);
		}
		SkillTag skillTag = skillTagOpt.get();
		skillTagRepository.deleteById(skillTag.getId());
		return true;

	}

	@Override
	@Transactional
	public String updateOrder(List<SkillTagDto> skillTagDtos) {
		List<String> nameTags = skillTagDtos.stream().map(item -> item.getName()).collect(Collectors.toList());
		List<SkillTag> skillTags = skillTagRepository.findByNameIn(nameTags);
		skillTags.forEach(tag -> {
			skillTagDtos.forEach(dto -> {
				if(tag.getName().equalsIgnoreCase(dto.getName())){
					tag.setIsMandatory(dto.getIsMandatory());
				}
			});
		});
		skillTagRepository.saveAll(skillTags);
		return Constants.DONE;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	/*
	 * Using for replace 2 skill tags, the first one is the old skill tag
	 * and will be replaced with the second one.
	 * This happened in 2 progresses:
	 * First, query to table project_skill_tags to fetch all records based on old skillTag Id
	 * then replace all records with the new skillTagId
	 * Second, delete the old skill tag in table skill_tag
	 * then replace the new skill tag with the old name
	 */
	public String replaceSkillTag(List<SkillTagDto> skillTagRequests) {

		List<String> idTags = skillTagRequests.stream().map(item -> item.getId()).collect(Collectors.toList());
		Optional<SkillTag> oldSkillTagOpt = skillTagRepository.findById(idTags.get(0));
		Optional<SkillTag> newSkillTagOpt = skillTagRepository.findById(idTags.get(1));
		// Old skill tag
		SkillTag oldSkillTag;
		if (oldSkillTagOpt.isPresent()) {
			oldSkillTag = oldSkillTagOpt.get();
		} else {
			throw new SkillManagementException(MessageCode.SKILL_TAG_NOT_FOUND.toString(), messageSource
					.getMessage(MessageCode.SKILL_TAG_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale()),
					null, NOT_FOUND);
		}

		// new skill tag
		SkillTag newSkillTag;
		if (newSkillTagOpt.isPresent()) {
			newSkillTag = newSkillTagOpt.get();
		} else {
			throw new SkillManagementException(MessageCode.SKILL_TAG_NOT_FOUND.toString(), messageSource
					.getMessage(MessageCode.SKILL_TAG_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale()),
					null, NOT_FOUND);
		}

		/*
		 * Use for replace old skillTagId with the new one in table project_skill_tags
		 */
		List<ProjectSkillTag> projectSkillTags = projectSkillTagRepository.findBySkillTagId(oldSkillTag.getId());
		if (!projectSkillTags.isEmpty()) {
			Set<String> projectUsingNewTagIdSet = new HashSet<>(projectSkillTagRepository.findProjectIdBySkillTagId(newSkillTag.getId()));
			
			for (ProjectSkillTag projectSkillTag : projectSkillTags) {
				if (projectUsingNewTagIdSet.contains(projectSkillTag.getProject().getId())) {
					projectSkillTagRepository.deleteById(projectSkillTag.getId());
				} else {
					projectSkillTagRepository.replaceSkillTag(projectSkillTag.getSkillTag().getId(), newSkillTag.getId());
				}
			}
		}

		/*
		 * Use for delete skill tag and update on table skill_tag
		 */
		String tempSkillTagName = oldSkillTag.getName();
		skillTagRepository.deleteById(oldSkillTag.getId());

		newSkillTag.setName(tempSkillTagName);
		skillTagRepository.saveAndFlush(newSkillTag);
		
		return Constants.DONE;
	}

}
