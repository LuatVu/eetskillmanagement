package com.bosch.eet.skill.management.service.impl;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.converter.PersonalConverter;
import com.bosch.eet.skill.management.converter.SkillConverter;
import com.bosch.eet.skill.management.converter.utils.SkillCompetencyLeadConverterUtil;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.SkillCompetencyLeadDto;
import com.bosch.eet.skill.management.dto.SkillDto;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillCompetencyLead;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.SkillCompetencyLeadRepository;
import com.bosch.eet.skill.management.repo.SkillGroupRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;
import com.bosch.eet.skill.management.service.CompetencyLeadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompetencyLeadServiceImpl implements CompetencyLeadService {

	private final MessageSource messageSource;

    private final SkillCompetencyLeadRepository skillCompetencyLeadRepository;
    private final PersonalRepository personalRepository;
    private final SkillGroupRepository skillGroupRepository;
    private final SkillRepository skillRepository;
    private final SkillConverter skillConverter;
    private final PersonalConverter personalConverter;
    


    @Override
    @Transactional
    public int save(List<SkillCompetencyLeadDto> skillCompetencyLeadDtos) {
        int rs = -1;
        for (SkillCompetencyLeadDto skillCompetencyLeadDto : skillCompetencyLeadDtos) {
            Optional<Personal> personalOptional = personalRepository.findById(skillCompetencyLeadDto.getPersonalId());
            if (!personalOptional.isPresent()) {
                throw new SkillManagementException(MessageCode.PERSONAL_ID_NOT_EXIST.toString(),
						messageSource.getMessage(MessageCode.PERSONAL_ID_NOT_EXIST.toString(), null,
								LocaleContextHolder.getLocale()), null, NOT_FOUND);
            }
            Personal personal = personalOptional.get();
            skillCompetencyLeadRepository.deleteByPersonalIdAndWhereSkillNotNull(skillCompetencyLeadDto.getPersonalId());
            Set<String> skillIds = new HashSet<>(skillCompetencyLeadDto.getSkillIds());
            List<SkillCompetencyLead> skillCompetencyLeads = new ArrayList<>();
            boolean newSkillGroupHaveSkill = false;
            
            if (Objects.nonNull(skillIds) && !skillIds.isEmpty()) {
                for (String skillId : skillIds) {
                    Optional<Skill> skillOptional = skillRepository.findById(skillId);
                    if (skillOptional.isPresent()) {
                    	Skill skill = skillOptional.get();
                    	skillCompetencyLeads.add(SkillCompetencyLead.builder()
                                .personal(personal)
                                .skill(skill)
                                .skillGroup(skill.getSkillGroup())
								.description(skillCompetencyLeadDto.getDescription())
                                .build());
                    	if(skill.getSkillGroup().getId().equalsIgnoreCase(skillCompetencyLeadDto.getSkillGroupId())) {
                    		newSkillGroupHaveSkill = true;
                    	}
                    } else {
                        log.error("CompetencyLeadServiceImpl save skill {} not found", skillId);
                        throw new SkillManagementException(MessageCode.COMPETENCY_LEAD_SKILL_NOT_FOUND.toString(),
								messageSource.getMessage(MessageCode.COMPETENCY_LEAD_SKILL_NOT_FOUND.toString(), null,
										LocaleContextHolder.getLocale()), null, NOT_FOUND);
                    }
                }
            }

            if(!newSkillGroupHaveSkill){
            	Optional<SkillGroup> skillGroupOptional = skillGroupRepository.findById(skillCompetencyLeadDto.getSkillGroupId());
            	if(skillGroupOptional.isPresent()) {
            		skillCompetencyLeads.add(SkillCompetencyLead.builder()
                            .personal(personal)
                            .skillGroup(skillGroupOptional.get())
							.description(skillCompetencyLeadDto.getDescription())
                            .build());
            	} else {
            		throw new SkillManagementException(MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(),
							messageSource.getMessage(MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(), null,
									LocaleContextHolder.getLocale()), null, NOT_FOUND);
            	}
            }

            List<SkillCompetencyLead> saved = skillCompetencyLeadRepository.saveAll(skillCompetencyLeads);
            rs += saved.size();
            log.info("Save success {}/{} for personalId: {}", saved.size(), ObjectUtils.isEmpty(skillIds.size())?0:skillIds.size(), personal.getId());
        }

        return rs;
    }
    
    @Override
    public List<SkillCompetencyLeadDto> findAllCompetencyLead() {
        List<SkillCompetencyLead> listCompe = skillCompetencyLeadRepository.findNameList();
        return SkillCompetencyLeadConverterUtil.convertToDTOs(listCompe);
    }  

	@Override
	@Transactional
	public List<SkillDto> findSkillsByCompetencyLeadId(String competencyLeadId) {
		List<SkillCompetencyLead> skillsByCompetencyLeadID = skillCompetencyLeadRepository.findByPersonalId(competencyLeadId);
		List<Skill> skillsOfCompetencyLead = new ArrayList<Skill>();
		if (!skillsByCompetencyLeadID.isEmpty()) {
			for (SkillCompetencyLead skillCompetencyLead : skillsByCompetencyLeadID) {
				Skill skill = skillCompetencyLead.getSkill();
				if (skill!=null) {
					skillsOfCompetencyLead.add(skill);
				}
			}
			return skillConverter.convertToDTOs(skillsOfCompetencyLead);
		} else {
			return new ArrayList<SkillDto>();
		}
}
	
	@Override
	public List<SkillCompetencyLeadDto> findCompetencyLeadBySkillId(String skillId){
		List<SkillCompetencyLeadDto> competencyList = new ArrayList<>();
		List<SkillCompetencyLead> skillCompetencyLeads = skillCompetencyLeadRepository.findBySkillIdOrderByPersonalAsc(skillId);
		for(SkillCompetencyLead sCL	: skillCompetencyLeads) {
			competencyList.add(SkillCompetencyLeadConverterUtil.convertToDTO(sCL));
		}
		return competencyList;
	}
	
	@Override
	public List<SkillCompetencyLeadDto> findAll() {
		List<SkillCompetencyLead> listCompe = skillCompetencyLeadRepository.findAll();
		HashMap<String, SkillCompetencyLeadDto> hashMap = new HashMap<>();
		for (SkillCompetencyLead scl : listCompe) {
			String competencyLeadId = scl.getPersonal().getId();
			Skill skill = scl.getSkill();
			if(Objects.isNull(skill)){
				continue;
			}
			if (!hashMap.containsKey(competencyLeadId)) {
				List<String> skillIdList = new ArrayList<>(Arrays.asList(skill.getId()));
				List<String> skillNameList = new ArrayList<>(Arrays.asList(skill.getName()));
				SkillCompetencyLeadDto sclDto = SkillCompetencyLeadDto.builder()
						.personalId(scl.getPersonal().getId())
						.displayName(scl.getPersonal().getUser().getDisplayName())
						.skillIds(skillIdList)
						.skillNames(skillNameList)
						.description(scl.getDescription())
						.skillCluster(scl.getSkill().getSkillGroup().getName())
						.build();
				hashMap.put(competencyLeadId, sclDto);
			} else {
				List<String> skillIdList = hashMap.get(competencyLeadId).getSkillIds();	
				skillIdList.add(skill.getId());
				
				List<String> skillNameList = hashMap.get(competencyLeadId).getSkillNames();
				skillNameList.add(scl.getSkill().getName());
				hashMap.get(competencyLeadId).setSkillCluster(skill.getSkillGroup().getName());
			}			
		}

		List<SkillCompetencyLeadDto> result = hashMap.values().stream().collect(Collectors.toList());
		return result; 
	}
	
	@Override
	@Transactional
	public List<PersonalDto> findCompetencyLeadBySkillGroupId(String skillGroupId) {
		Optional<SkillGroup> skillGroup = skillGroupRepository.findById(skillGroupId);
		if (!skillGroup.isPresent()) {
			throw new SkillManagementException(MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(),
					messageSource.getMessage(MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(), null,
							LocaleContextHolder.getLocale()),
					null, NOT_FOUND);
		}	
		List<Personal> competencyLeadList = new ArrayList<>();
		List<SkillCompetencyLead> skillCompetencyLeads = skillCompetencyLeadRepository.findBySkillGroupIdOrderByPersonalAsc(skillGroupId);
		for (SkillCompetencyLead scl : skillCompetencyLeads) {
			Personal compeLead = scl.getPersonal();
			if (!competencyLeadList.contains(compeLead))  {
				competencyLeadList.add(compeLead);
			}
		}
		return personalConverter.convertToListMangerDto(competencyLeadList);
		
	}
	
	@Override
	@Transactional
	public void deleteCompetencyLead(SkillCompetencyLeadDto skillCompetencyLeadDto) {
		List<SkillCompetencyLead> skillsByCompetencyLeadID = skillCompetencyLeadRepository.findByPersonalId(skillCompetencyLeadDto.getPersonalId());
		if (skillsByCompetencyLeadID != null) {
				skillCompetencyLeadRepository.deleteByPersonalIdAndSkillGroupId(skillCompetencyLeadDto.getPersonalId(), skillCompetencyLeadDto.getSkillGroupId());
			}
			
	}
}