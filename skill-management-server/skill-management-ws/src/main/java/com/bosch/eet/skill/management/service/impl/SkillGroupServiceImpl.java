package com.bosch.eet.skill.management.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import com.bosch.eet.skill.management.converter.SkillConverter;
import com.bosch.eet.skill.management.converter.utils.SkillGroupConverterUtil;
import com.bosch.eet.skill.management.dto.SkillClusterDTO;
import com.bosch.eet.skill.management.dto.SkillGroupDto;
import com.bosch.eet.skill.management.entity.PersonalSkillGroup;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.entity.SkillType;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.repo.PersonalSkillGroupRepository;
import com.bosch.eet.skill.management.repo.SkillGroupRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;
import com.bosch.eet.skill.management.repo.SkillTypeRepository;
import com.bosch.eet.skill.management.service.SkillGroupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author DUP5HC
 * @author TAY3HC
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class SkillGroupServiceImpl implements SkillGroupService {

    private final MessageSource messageSource;

    private final CompetencyLeadServiceImpl competencyLeadService;

    private final SkillGroupRepository skillGroupRepository;
    private final SkillRepository skillRepository;
    private final SkillTypeRepository skillTypeRepository;
    private final PersonalSkillGroupRepository personalSkillGroupRepository;

    private final SkillConverter skillConverter;

    @Override
    public List<SkillGroupDto> findAllSkillGroups() {
        StopWatch stopWatch = new StopWatch();
        log.info("Start finding all skill groups");
        stopWatch.start();
        List<SkillGroup> skillGroups = skillGroupRepository.findAll();
        stopWatch.stop();
        log.info("Find all skill groups took " + stopWatch.getTotalTimeMillis() + "ms ~= "
                + stopWatch.getTotalTimeSeconds() + "s ~=");
        return SkillGroupConverterUtil.convertToSimpleDTOs(skillGroups);
    }


    @Override
    public SkillGroupDto findById(String skillGroupId) {
        Optional<SkillGroup> skillGroupOpt = skillGroupRepository.findById(skillGroupId);
        if (skillGroupOpt.isPresent()) {
            SkillGroup skillGroup = skillGroupOpt.get();
            return SkillGroupDto.builder()
                    .id(skillGroup.getId())
                    .name(skillGroup.getName())
                    .skillType(skillGroup.getSkillType().getName())
                    .skillTypeId(skillGroup.getSkillType().getId())
                    .skills(skillConverter.convertToDTOs(skillGroup.getSkills()))
                    .competencyLeads(competencyLeadService.findCompetencyLeadBySkillGroupId(skillGroupId))
                    .build();
        } else {
            throw new SkillManagementException(MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public List<String> findSkillIdBySkillGroup(String skillGroupId) {
        Optional<SkillGroup> skillGroup = skillGroupRepository.findById(skillGroupId);
        log.info(skillGroup.toString());
        List<Skill> skills = skillRepository.findAllBySkillGroup(skillGroup.get());
        return skills.stream().map(Skill::getId).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SkillGroupDto addSkillGroup(SkillGroupDto skillGroupDto) {
        SkillGroup skillGroup = new SkillGroup();
        if (skillGroupDto.getName().isEmpty()) {
            throw new SkillManagementException(MessageCode.NAME_IS_REQUIRED.toString(),
                    messageSource.getMessage(MessageCode.NAME_IS_REQUIRED.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Check if the skill group exists before adding a new one.
        Optional<SkillGroup> skillGroupOpt = skillGroupRepository
                .findByName(skillGroupDto.getName());
        if (skillGroupOpt.isPresent()) {
            SkillGroup skillGroup1 = skillGroupOpt.get();
            if (skillGroup1.getName().equals(skillGroupDto.getName())) {
                throw new SkillManagementException(MessageCode.SKM_SKILL_GROUP_ALREADY_EXISTS.toString(),
                        messageSource.getMessage(MessageCode.SKM_SKILL_GROUP_ALREADY_EXISTS.toString(), null,
                                LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        skillGroup.setName(skillGroupDto.getName());
        
        //set Skill Type
        SkillType skillType = skillTypeRepository.findByName(skillGroupDto.getSkillType()).orElse(null);
        if (skillType == null) {
            throw new SkillManagementException(MessageCode.VALIDATION_ERRORS.toString(),
                    messageSource.getMessage(MessageCode.VALIDATION_ERRORS.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST);
        }
        skillGroup.setSkillType(skillType);
        skillGroupRepository.save(skillGroup);
        return skillGroupDto;
    }

    @Override
    public boolean deleteSkillGroup(SkillGroup skillGroup) {
        List<PersonalSkillGroup> personalSkillGroups = personalSkillGroupRepository.findBySkillGroupId(skillGroup.getId());
        personalSkillGroupRepository.deleteAll(personalSkillGroups);
        skillGroupRepository.delete(skillGroup);
        return true;
    }

    @Override
    public List<SkillClusterDTO> findAllSkillGroupsWithSkills() {
        return SkillGroupConverterUtil.mapToSkillClusterDtos(skillGroupRepository.findAll());
    }

}