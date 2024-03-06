package com.bosch.eet.skill.management.facade.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.converter.PersonalConverter;
import com.bosch.eet.skill.management.elasticsearch.document.PersonalDocument;
import com.bosch.eet.skill.management.elasticsearch.repo.PersonalSpringElasticRepository;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.SkillFacade;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.SkillCompetencyLeadRepository;
import com.bosch.eet.skill.management.repo.SkillGroupRepository;
import com.bosch.eet.skill.management.service.SkillGroupService;
import com.bosch.eet.skill.management.service.SkillService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillFacadeImpl implements SkillFacade {
    private final SkillService skillService;
	private final SkillGroupService skillGroupService;

	private final SkillGroupRepository skillGroupRepository;
	private final SkillCompetencyLeadRepository skillCompetencyLeadRepository;
	private final PersonalRepository personalRepository;
	private final PersonalSpringElasticRepository personalSpringElasticRepository;

	private final PersonalConverter personalConverter;
	private final MessageSource messageSource;

	@Override
	@Transactional
	public boolean deleteSkillGroup(String skillGroupId){
		Optional<SkillGroup> skillGroupOpt = skillGroupRepository.findById(skillGroupId);
		if (!skillGroupOpt.isPresent()) {
			throw new SkillManagementException(MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(),
					messageSource.getMessage(MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(), null,
							LocaleContextHolder.getLocale()), null, HttpStatus.NOT_FOUND);
		}
		SkillGroup skillGroup = skillGroupOpt.get();
		skillGroup.getSkills().forEach(skill -> skillService.deleteSkill(skill.getId()));

		skillCompetencyLeadRepository.deleteBySkillGroupId(skillGroup.getId());
		skillGroupService.deleteSkillGroup(skillGroup);

		personalSpringElasticRepository.deleteAll();
		List<PersonalDocument> personalDocuments = personalRepository.findAll()
				.stream().map(personalConverter::convertToDocument).collect(Collectors.toList());
		personalSpringElasticRepository.saveAll(personalDocuments);

		return true;
	}
}
