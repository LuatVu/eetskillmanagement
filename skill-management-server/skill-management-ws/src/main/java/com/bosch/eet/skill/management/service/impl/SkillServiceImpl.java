package com.bosch.eet.skill.management.service.impl;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.converter.PersonalConverter;
import com.bosch.eet.skill.management.converter.SkillConverter;
import com.bosch.eet.skill.management.converter.utils.LevelExpectedConverterUtil;
import com.bosch.eet.skill.management.converter.utils.PersonalSkillConverterUtil;
import com.bosch.eet.skill.management.converter.utils.SkillsHighlightConverterUtil;
import com.bosch.eet.skill.management.dto.AddSkillDto;
import com.bosch.eet.skill.management.dto.LevelExpected;
import com.bosch.eet.skill.management.dto.PersonalSkillDto;
import com.bosch.eet.skill.management.dto.SkillCompetencyLeadDto;
import com.bosch.eet.skill.management.dto.SkillDto;
import com.bosch.eet.skill.management.dto.SkillExpectedLevelDto;
import com.bosch.eet.skill.management.dto.SkillExperienceLevelDTO;
import com.bosch.eet.skill.management.dto.SkillGroupLevelDto;
import com.bosch.eet.skill.management.dto.SkillHighlightDto;
import com.bosch.eet.skill.management.dto.SkillManagementDto;
import com.bosch.eet.skill.management.elasticsearch.document.PersonalDocument;
import com.bosch.eet.skill.management.elasticsearch.document.SkillDocument;
import com.bosch.eet.skill.management.elasticsearch.repo.PersonalSpringElasticRepository;
import com.bosch.eet.skill.management.elasticsearch.repo.SkillElasticRepository;
import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.RequestEvaluation;
import com.bosch.eet.skill.management.entity.RequestEvaluationDetail;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillCompetencyLead;
import com.bosch.eet.skill.management.entity.SkillExperienceLevel;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.entity.SkillHighlight;
import com.bosch.eet.skill.management.entity.SkillLevel;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.ResourceNotFoundException;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.repo.LevelRepository;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.PersonalSkillRepository;
import com.bosch.eet.skill.management.repo.RequestEvaluationDetailRepository;
import com.bosch.eet.skill.management.repo.RequestEvaluationRepository;
import com.bosch.eet.skill.management.repo.SkillCompetencyLeadRepository;
import com.bosch.eet.skill.management.repo.SkillExperienceLevelRepository;
import com.bosch.eet.skill.management.repo.SkillGroupRepository;
import com.bosch.eet.skill.management.repo.SkillLevelRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;
import com.bosch.eet.skill.management.repo.SkillsHighlightReposistory;
import com.bosch.eet.skill.management.service.CompetencyLeadService;
import com.bosch.eet.skill.management.service.SkillGroupService;
import com.bosch.eet.skill.management.service.SkillService;
import com.bosch.eet.skill.management.specification.SkillSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    
    private final SkillConverter skillConverter;
    
    private final LevelExpectedConverterUtil levelExpectedConverter;
    
    private final MessageSource messageSource;
    
    private final SkillExperienceLevelRepository skillExperienceLevelRepository;

    private final SkillGroupRepository skillGroupRepository;
    
    private final SkillCompetencyLeadRepository skillCompetencyLeadRepository;
    
    private final PersonalRepository personalRepository;
    
    private final PersonalSkillRepository personalSkillRepository;
    
    private final PersonalConverter personalConverter;

    private final RequestEvaluationDetailRepository requestEvaluationDetailRepository;

    private final RequestEvaluationRepository requestEvaluationRepository;
    
    private final SkillsHighlightReposistory skillsHighlightReposistory;
    
    private final SkillGroupService skillGroupService;
    
    private final CompetencyLeadService competencyLeadService;

	private final SkillElasticRepository skillElasticRepository;
	
	private final SkillLevelRepository skillLevelRepository;
	
	private final LevelRepository levelRepository;

	private final PersonalSpringElasticRepository personalSpringElasticRepository;

//	Get all skill id
    @Override
    public List<String> findAllSkills() {
        return skillRepository.findAll().stream().map(Skill::getId).collect(Collectors.toList());
    }
    
//	Get all skill name
    @Override
    public List<String> findAllSkillsName() {
        return skillRepository.findAll().stream().map(Skill::getName).collect(Collectors.toList());
    }
    
//	Get all skill Level
    @Override
    public Map<String, SkillGroupLevelDto> findAllSkillLevel() {
    	Map<String, SkillGroupLevelDto> skillMap = new HashMap<>();
    	List<String> skillIds = findAllSkills();
    	for (String skillId : skillIds) {
    		Optional<Skill> skillOpt = skillRepository.findById(skillId);
    		if(!skillOpt.isPresent()) {
				throw new SkillManagementException(
						messageSource.getMessage(MessageCode.SKILL_NOT_FOUND.toString(), null,
								LocaleContextHolder.getLocale()),
						MessageCode.SKILL_NOT_FOUND.toString(), null, NOT_FOUND);
    		}
    		Skill skill = skillOpt.get();
    		SkillGroupLevelDto skillGroupAndLevelDto = SkillGroupLevelDto.builder()
    				.skillName(skill.getName())
    				.skillGroup(skill.getSkillGroup().getName())
    				.skillLevels(findAllSkillExperienceBySkillId(skillId))
    				.isMandatory(skill.getIsMandatory())
    				.isRequired(skill.getIsRequired())
    				.build();
    		skillMap.put(skillId, skillGroupAndLevelDto);
    	}
    	
    	return skillMap;
    }
    

//  Get List of SkillLevel by SkillId
    @Override
    public List<SkillExperienceLevelDTO> findAllSkillExperienceBySkillId(String skillId) {
    	List<SkillExperienceLevel> skillExperienceLevels = skillExperienceLevelRepository
                .findNameAndDescriptionDistinctBySkillId(skillId);
        return skillConverter.convertToDetailLevelDTOs(skillExperienceLevels);
    }

    @Override
	public SkillDto findById(String skillId) {
    	Optional<Skill> skillOpt = skillRepository.findById(skillId);
    	if (skillOpt.isPresent()) {
    		Skill skill = skillOpt.get();
    		return SkillDto.builder()
					.id(skill.getId())
					.name(skill.getName())
					.competency(skill.getSkillGroup().getName())
					.build();
		} else {
    		throw new SkillManagementException(MessageCode.SKILL_NOT_FOUND.toString(),
					messageSource.getMessage(MessageCode.SKILL_NOT_FOUND.toString(), null,
							LocaleContextHolder.getLocale()), null, NOT_FOUND);
		}
	}
    
    @Override
    public SkillGroupLevelDto findSkillExperienceDetailBySkillId(String skillId) { 
    	List<SkillCompetencyLeadDto> competencyLeads = competencyLeadService.findCompetencyLeadBySkillId(skillId);
    	Optional<Skill> skillOpt = skillRepository.findById(skillId);
    	if(!skillOpt.isPresent()) {
			throw new SkillManagementException(
					messageSource.getMessage(MessageCode.SKILL_NOT_FOUND.toString(), null,
							LocaleContextHolder.getLocale()),
					MessageCode.SKILL_NOT_FOUND.toString(), null, NOT_FOUND);

    	}
    	Skill skill = skillOpt.get();
    	SkillGroup skillGroup = skill.getSkillGroup();
    	SkillGroupLevelDto skillDetail = SkillGroupLevelDto.builder()
    			.skillName(skill.getName())
    			.skillLevels(findAllSkillExperienceBySkillId(skillId))
    			.skillGroup(skillGroup.getName())
    			.competencyLeads(competencyLeads)
    			.isMandatory(skill.getIsMandatory())
    			.isRequired(skill.getIsRequired())
    			.skillType(skillGroup.getSkillType().getName())
				.build();
    	
    	return skillDetail;
    }
    
// Add List of PersonalSkillDto by personalId and List of skillGroupId
	@Override
	public List<PersonalSkillDto> addSkillsByListOfSkillGroups(AddSkillDto addSkillDto) {
		List<PersonalSkill> newPersonalSkills = new ArrayList<PersonalSkill>();
		if (!addSkillDto.getPersonalDto().getId().isEmpty()) {
			Optional<Personal> personOpt = personalRepository.findById(addSkillDto.getPersonalDto().getId());
			if (personOpt.isPresent()) {
				Personal person = personOpt.get();
				if (!addSkillDto.getSkillGroupIds().isEmpty()) {
					for (String skillGroupId : addSkillDto.getSkillGroupIds()) {
						List<String> skills = skillGroupService.findSkillIdBySkillGroup(skillGroupId);
						for (String skillId : skills) {
							Skill skill = skillRepository.findById(skillId).get();
							PersonalSkill newPersonalSkill = new PersonalSkill();
							newPersonalSkill.setPersonal(person);
							newPersonalSkill.setSkill(skill);
							newPersonalSkill.setExperience(Constants.DEFAULT_EXPERIENCE);
							newPersonalSkill.setLevel(Constants.DEFAULT_FLOAT_SKILL_LEVEL);
							final PersonalSkill savedPersonalSkill = personalSkillRepository
									.save(newPersonalSkill);
							newPersonalSkills.add(savedPersonalSkill);
						}
					}
				} else {
					log.info("throw skill not found");
					throw new SkillManagementException(
							messageSource.getMessage(MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(), null,
									LocaleContextHolder.getLocale()),
							MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(), null, NOT_FOUND);
				}

			} else {
				log.info("throw user not found");
				throw new SkillManagementException(
						
						messageSource.getMessage(MessageCode.SKM_USER_NOT_FOUND.toString(), null,
								LocaleContextHolder.getLocale()),
						MessageCode.SKM_USER_NOT_FOUND.toString(), null, NOT_FOUND);
			}
		} else {
			log.info("throw personal id not found");
			throw new SkillManagementException(
					messageSource.getMessage(MessageCode.PERSONAL_ID_NOT_EXIST.toString(), null,
							LocaleContextHolder.getLocale()),
					MessageCode.PERSONAL_ID_NOT_EXIST.toString(), null, NOT_FOUND);
		}
		return convertToDTOs(newPersonalSkills);
	}
	
	public List<PersonalSkillDto> convertToDTOs(List<PersonalSkill> newPersonalSkills) {
		return PersonalSkillConverterUtil.convertToDTOs(newPersonalSkills);
	}
	
	//Add new skill experience level and skill competency lead
	@Override
	@Transactional
	public Skill saveSkillManagement(SkillManagementDto skillManagementDto) {
		String skillName = skillManagementDto.getName();
		Optional<Skill> skill = skillRepository.findByName(skillName);
		if (skill.isPresent()) {
			throw new SkillManagementException(MessageCode.SKILL_NAME_ALREADY_EXIST.toString(),
					messageSource.getMessage(MessageCode.SKILL_NAME_ALREADY_EXIST.toString(), null,
							LocaleContextHolder.getLocale()),
					null, NOT_FOUND);
		}
		
		Optional<SkillGroup> skillGroup = skillGroupRepository.findByName(skillManagementDto.getSkillGroup());
		if (!skillGroup.isPresent()) {
			throw new SkillManagementException(MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(),
					messageSource.getMessage(MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(), null,
							LocaleContextHolder.getLocale()),
					null, NOT_FOUND);
		}		
		
		Skill newSkill = Skill.builder()
				.name(skillName)
				.skillGroup(skillGroup.get())
				.status("ACTIVE")
				.isMandatory(skillManagementDto.isMandatory())
				.isRequired(skillManagementDto.isRequired())
				.build();
		// save skill
		final Skill savedSkill = skillRepository.save(newSkill);
		List<SkillExperienceLevelDTO> selDtos = skillManagementDto.getSkillExperienceLevels();
		
		List<SkillExperienceLevel> savedSkillExperienceLevels = new ArrayList<SkillExperienceLevel>();
		for (SkillExperienceLevelDTO selDto : selDtos) {
			SkillExperienceLevel newSkillExperienceLevel = SkillExperienceLevel.builder()
					.name(selDto.getName())
					.skill(savedSkill)
					.skillGroup(skillGroup.get())
					.description(selDto.getDescription())
					.build();
			
			final SkillExperienceLevel savedSkillExperienceLevel = skillExperienceLevelRepository.save(newSkillExperienceLevel);
			savedSkillExperienceLevels.add(savedSkillExperienceLevel);
		}
		
		List<SkillCompetencyLead> savedSkillCompetencyLeads = new ArrayList<SkillCompetencyLead>();
		skillCompetencyLeadRepository.deleteBySkillGroupIdAndWhereSkillIdIsNull(skillGroup.get().getId());
		for (String competencyLeadId : skillManagementDto.getCompetencyLeads()) {
			Optional<Personal> competencyLead = personalRepository.findById(competencyLeadId);
			if (!competencyLead.isPresent()) {
				throw new SkillManagementException(MessageCode.COMPETENCY_LEAD_NOT_FOUND.toString(),
						messageSource.getMessage(MessageCode.COMPETENCY_LEAD_NOT_FOUND.toString(), null,
								LocaleContextHolder.getLocale()),
						null, NOT_FOUND);
			}
			
			SkillCompetencyLead skillCompetencyLead = SkillCompetencyLead.builder()
					.personal(competencyLead.get())
					.skill(savedSkill)
					.skillGroup(skillGroup.get())
					.build();
			
			try {
				final SkillCompetencyLead savedSkillCompetencyLead = skillCompetencyLeadRepository.save(skillCompetencyLead);
				savedSkillCompetencyLeads.add(savedSkillCompetencyLead);
			}
			catch (Exception e) {
				throw new SkillManagementException(MessageCode.SKM_ADD_SKILL_FAIL.toString(),
						messageSource.getMessage(MessageCode.SKM_ADD_SKILL_FAIL.toString(), null,
								LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
			}			
 		}

		// Sync Elastic
		try {
			skillElasticRepository.update(savedSkill);
		} catch (Exception e) {
			e.printStackTrace();
		}

 		return savedSkill;
	}
	

	@Override
	@Transactional
	public boolean editNewSkill(SkillManagementDto skillManagementDto) {
    	// Check if the skill group exists in the database.
		// If not, throw the exception informing the user that the given
		// skill group doesn't exist.
    	Optional<SkillGroup> skillGroupOpt = skillGroupRepository
				.findByName(skillManagementDto.getSkillGroup());
    	if (!skillGroupOpt.isPresent()) {
    		throw new SkillManagementException(MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(),
					messageSource.getMessage(MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(), null,
							LocaleContextHolder.getLocale()), null, NOT_FOUND);
		}
    	SkillGroup skillGroup = skillGroupOpt.get();

    	// Delete old records of the skill competency leads.
		List<SkillCompetencyLead> oldSkillCompetencyLeads = skillCompetencyLeadRepository
				.findAllBySkillId(skillManagementDto.getId());
		if (!oldSkillCompetencyLeads.isEmpty()) {
			try {
				skillCompetencyLeadRepository.deleteAll(oldSkillCompetencyLeads);
			} catch (Exception e) {
				throw new SkillManagementException(MessageCode.SKM_COMPETENCY_LEAD_DELETE_FAIL.toString(),
						messageSource.getMessage(MessageCode.SKM_COMPETENCY_LEAD_DELETE_FAIL.toString(), null,
								LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
			}
		}

    	// Delete old records of the skill experience levels.
		List<SkillExperienceLevel> oldSkillExperienceLevels = skillExperienceLevelRepository
				.findAllBySkillId(skillManagementDto.getId());
		if (!oldSkillExperienceLevels.isEmpty()) {
			try {
				skillExperienceLevelRepository.deleteAll(oldSkillExperienceLevels);
			} catch (Exception e) {
				throw new SkillManagementException(MessageCode.SKM_SKILL_EXPERIENCE_LEVEL_DELETE_FAIL.toString(),
						messageSource.getMessage(MessageCode.SKM_SKILL_EXPERIENCE_LEVEL_DELETE_FAIL.toString(), null,
								LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
			}
		}

		// Create the updated entity with the existing ID.
    	Skill updateSkill = Skill.builder()
				.id(skillManagementDto.getId())
				.name(skillManagementDto.getName())
				.status(Constants.ACTIVE)
				.skillGroup(skillGroup)
				.isMandatory(skillManagementDto.isMandatory())
				.isRequired(skillManagementDto.isRequired())
				.build();
    	
		// Save the updated entity.
    	Skill saveSkill = skillRepository.save(updateSkill);

    	// Check if skill experience levels are null.
		// If not, then update them
    	if (!skillManagementDto.getSkillExperienceLevels().isEmpty()) {
			for (SkillExperienceLevelDTO skillExperienceLevelDTO : skillManagementDto.getSkillExperienceLevels()) {
				SkillExperienceLevel skillExperienceLevel = SkillExperienceLevel.builder()
						.name(skillExperienceLevelDTO.getName())
						.skill(saveSkill)
						.skillGroup(skillGroup)
						.description(skillExperienceLevelDTO.getDescription())
						.build();

				try {
					skillExperienceLevelRepository.save(skillExperienceLevel);
				} catch (Exception e) {
					throw new SkillManagementException(MessageCode.SKM_SKILL_EXPERIENCE_LEVEL_SAVE_FAIL.toString(),
							messageSource.getMessage(MessageCode.SKM_SKILL_EXPERIENCE_LEVEL_SAVE_FAIL.toString(), null,
									LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
				}
			}
		}

    	// Check if the competency leads are null.
		// If not, then update them
    	if (!CollectionUtils.isEmpty(skillManagementDto.getCompetencyLeads())) {
			for (String competencyLeadId : skillManagementDto.getCompetencyLeads()) {
				Optional<Personal> competencyLeadOpt = personalRepository
						.findById(competencyLeadId);
				if (!competencyLeadOpt.isPresent()) {
					throw new SkillManagementException(MessageCode.COMPETENCY_LEAD_NOT_FOUND.toString(),
							messageSource.getMessage(MessageCode.COMPETENCY_LEAD_NOT_FOUND.toString(), null,
									LocaleContextHolder.getLocale()), null, NOT_FOUND);
				}
				Personal competencyLead = competencyLeadOpt.get();

				SkillCompetencyLead skillCompetencyLead = SkillCompetencyLead.builder()
						.personal(competencyLead)
						.skill(saveSkill)
						.skillGroup(saveSkill.getSkillGroup())
						.build();

				try {
					skillCompetencyLeadRepository.save(skillCompetencyLead);
				} catch (Exception e) {
					throw new SkillManagementException(MessageCode.SKM_COMPETENCY_LEAD_SAVE_FAIL.toString(),
							messageSource.getMessage(MessageCode.SKM_COMPETENCY_LEAD_SAVE_FAIL.toString(), null,
									LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
				}
			}
		}

		// Sync Elastic
		try {
			skillElasticRepository.update(saveSkill);
		} catch (Exception e) {
			e.printStackTrace();
		}

    	// Return true when the operation completed successfully.
		return true;
	}
	
	 /**
     * Delete skill
     *
     * @param String skillId
     * @return String
     * 
     */
	@Override
	@Transactional
	public String deleteSkill(String skillId) {
		List<SkillExperienceLevel> skillExperienceLevels = skillExperienceLevelRepository.findAllBySkillId(skillId);
		// Delete skill experience level
		skillExperienceLevelRepository.deleteAll(skillExperienceLevels);

		Optional<Skill> skillOpt = skillRepository.findById(skillId);
		Skill skill = skillOpt.orElseThrow(() -> new ResourceNotFoundException(messageSource
				.getMessage(MessageCode.SKILL_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale())));

		// Delete competency lead
		if(skill.getSkillGroup().getSkills().size() > 1) {
			List<SkillCompetencyLead> skillCompetencyLeads = skillCompetencyLeadRepository.findAllBySkillId(skillId);
			skillCompetencyLeadRepository.deleteAll(skillCompetencyLeads);
		} else {
			skillCompetencyLeadRepository.updateSkillToNullBySkillId(skillId);
		}

		// Delete skill highlight
		List<SkillHighlight> skillHighlights = skillsHighlightReposistory.findByPersonalSkillSkillId(skillId);
		skillsHighlightReposistory.deleteAll(skillHighlights);

		// Delete personal skill
		List<PersonalSkill> personalSkills = skill.getPersonalSkill();
		if (Objects.nonNull(personalSkills) && !personalSkills.isEmpty()) {
			personalSkillRepository.deleteAll(personalSkills);
		}
		// Delete request evaluation detail (including the request evaluation has only it)
		List<RequestEvaluationDetail> requestEvaluationDetails = requestEvaluationDetailRepository
					.findBySkillId(skillId);
		List<RequestEvaluation> requestEvaluations = new ArrayList<>();
		for (RequestEvaluationDetail requestEvaluationDetail : requestEvaluationDetails) {
			RequestEvaluation requestEvaluation = requestEvaluationDetail.getRequestEvaluation();
			boolean isRequestHasSingleDetail = requestEvaluation.getRequestEvaluationDetails().size() == 1;
			if (!requestEvaluations.contains(requestEvaluation) && isRequestHasSingleDetail) {
				requestEvaluations.add(requestEvaluation);
			}
		}
//		requestEvaluationDetailRepository.deleteAll(requestEvaluationDetails);
		requestEvaluationDetailRepository.deleteBySkillId(skillId);
		requestEvaluationRepository.deleteAll(requestEvaluations);
		List<SkillLevel> skillLevels = skillLevelRepository.findBySkillId(skillId);
		skillLevelRepository.deleteAll(skillLevels);
		skillRepository.deleteById(skillId);
		skillElasticRepository.removeById(skillId);

		List<PersonalDocument> personalDocuments = personalSkillRepository.findBySkillId(skillId)
				.stream().map(personalSkill -> personalConverter.convertToDocument(personalSkill.getPersonal()))
				.collect(Collectors.toList());
		personalSpringElasticRepository.saveAll(personalDocuments);

		return Constants.SUCCESS;
	}

	@Override
	public List<SkillHighlightDto> findSkillsHighlight(String personalId) {
		List<PersonalSkill> personalSkills = personalSkillRepository.findSkillsHighlight(personalId);
		if(CollectionUtils.isEmpty(personalSkills)) {
			return new ArrayList<SkillHighlightDto>();
		}
		List<SkillHighlight> skillHighlights = skillsHighlightReposistory.findByPersonalId(personalId);
		
		return SkillsHighlightConverterUtil.convertToDtos(personalSkills, skillHighlights);
	}

	@Override
	@Transactional
	public List<PersonalSkillDto> saveSkillsHighlight(String idPersonal, List<String> personalSkillDtos) {
		Optional<Personal> personalOpt = personalRepository.findById(idPersonal);
		if(!personalOpt.isPresent()) {
			throw new SkillManagementException(MessageCode.PERSONAL_ID_NOT_EXIST.toString(), 
					messageSource.getMessage(MessageCode.PERSONAL_ID_NOT_EXIST.toString(),
							null,LocaleContextHolder.getLocale())
								,null, HttpStatus.NOT_FOUND);
			
		}
		
		Personal personal = personalOpt.get();
		List<PersonalSkill> personalSkills = personalSkillRepository.findByPersonalIdAndSkillIdIn(idPersonal,personalSkillDtos);

		skillsHighlightReposistory.deleteByPersonalId(idPersonal);
		
		List<SkillHighlight> skillHighlights = SkillsHighlightConverterUtil.convert2SkillHighlights(personal, personalSkills);
		skillsHighlightReposistory.saveAllAndFlush(skillHighlights);
		
		return convertToDTOs2(personalSkills);
	}

	public List<PersonalSkillDto> convertToDTOs2(List<PersonalSkill> personalSkills) {
		return PersonalSkillConverterUtil.convertToDTOs2(personalSkills);
	}

	@Override
	public Skill saveAndSyncElasticSkill(Skill skill){
		Skill savedSkill = skillRepository.save(skill);

		// index to elastic search
		if (skillElasticRepository.existById(skill.getId())) {
			skillElasticRepository.update(skill);
		} else {
			SkillDocument skillDocument = skillConverter.convertToDocument(skill);
			skillElasticRepository.insert(skillDocument);
		}
		return savedSkill;
	}

	@Override
	public List<Skill> findAllByName(String name) {
		return skillRepository.findAllByName(name);
	}

	@Override
	public Map<String, Object> getSkillLevelExpected(Integer pageNumber, Integer size, List<String> skillCluster) {
		int DEFAUL_PAGE=0;
		Pageable paging = PageRequest.of(pageNumber < 0 ? DEFAUL_PAGE : pageNumber, size, Sort.by(Constants.NAME));
		Specification<Skill> specification = SkillSpecification.search(skillCluster);
		Page<Skill> skillContent = skillRepository.findAll(specification, paging);
		List<SkillExpectedLevelDto> skillExpectedLevel = new ArrayList<>();
		if(skillContent.hasContent()) {
			List<Skill> skills = skillContent.getContent();
			List<String> idSkills = skills.stream().map(item -> item.getId()).collect(Collectors.toList());
			List<SkillLevel> skillLevels = skillLevelRepository.findBySkillIdIn(idSkills);
			for (Skill skill : skills) {
				List<SkillLevel> skillLevelBySkill = skillLevels.stream().filter(item -> item.getSkill().equals(skill)).collect(Collectors.toList());
				List<LevelExpected> levelExpecteds = levelExpectedConverter.convertToDtos(skillLevelBySkill);
				SkillExpectedLevelDto skillExpectedLevelDto = SkillExpectedLevelDto.builder()
						.idSkill(skill.getId())
						.nameSkill(skill.getName())
						.levelExpecteds(levelExpecteds)
						.build();
				skillExpectedLevel.add(skillExpectedLevelDto);
			}
		}

		Map<String, Object> mapResponse = new HashMap<>();
		mapResponse.put(Constants.SKILLS, skillExpectedLevel);
		mapResponse.put(Constants.TOTAL_PAGE, skillExpectedLevel.size());
		mapResponse.put(Constants.TOTAL_ITEM, skillContent.getTotalElements());
		return mapResponse;
	}

	@Override
	@Transactional
	public String updateSkillLevel(List<SkillExpectedLevelDto> skillExpectedLevelDtos) {
		List<SkillLevel> skillLevelList = new ArrayList<>();
		List<String> idSkill = skillExpectedLevelDtos.stream().map(item -> item.getIdSkill())
				.collect(Collectors.toList());
		List<Skill> skills = skillRepository.findAllByIdIn(idSkill);
		List<Level> levels = levelRepository.findAll();
		Map<String, Level> mapLevel = new HashMap<>();
		Map<String, Skill> mapSkill = new HashMap<>();
		for (Level level : levels) {
			mapLevel.put(level.getId(), level);
		}

		for (Skill skill : skills) {
			mapSkill.put(skill.getId(), skill);
		}

		for (SkillExpectedLevelDto seld : skillExpectedLevelDtos) {
			for (LevelExpected levelExpected : seld.getLevelExpecteds()) {
				if (!Constants.EXPECTED_SKILL_LEVELS.contains(levelExpected.getValue())) {
					continue;
				}
				SkillLevel skillLevelTemp;
				Optional<SkillLevel> skillLevelOpt = skillLevelRepository
						.findByLevelAndSkill(mapLevel.get(levelExpected.getIdLevel()), mapSkill.get(seld.getIdSkill()));
				if (skillLevelOpt.isPresent()) {
					skillLevelTemp = skillLevelOpt.get();
					skillLevelTemp.setLevelLable(Constants.LEVEL_TEMPLATE + levelExpected.getValue());
				} else {
					skillLevelTemp = SkillLevel.builder()
							.skill(mapSkill.get(seld.getIdSkill()))
							.level(mapLevel.get(levelExpected.getIdLevel()))
							.skillGroup(mapSkill.get(seld.getIdSkill()).getSkillGroup())
							.levelLable(Constants.LEVEL_TEMPLATE + levelExpected.getValue())
							.build();
				}
				skillLevelList.add(skillLevelTemp);
			}
		}

		skillLevelRepository.saveAll(skillLevelList);
		return Constants.DONE;
	}

}