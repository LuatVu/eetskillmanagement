package com.bosch.eet.skill.management.converter;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.EvaluationStatus;
import com.bosch.eet.skill.management.converter.utils.SkillCompetencyLeadConverterUtil;
import com.bosch.eet.skill.management.dto.RequestEvaluationDto;
import com.bosch.eet.skill.management.dto.SkillCompetencyLeadDto;
import com.bosch.eet.skill.management.dto.SkillDescriptionDTO;
import com.bosch.eet.skill.management.dto.SkillLevelDTO;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.RequestEvaluation;
import com.bosch.eet.skill.management.entity.RequestEvaluationDetail;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillCompetencyLead;
import com.bosch.eet.skill.management.entity.SkillLevel;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.util.Utility;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.PersonalSkillRepository;
import com.bosch.eet.skill.management.repo.SkillCompetencyLeadRepository;
import com.bosch.eet.skill.management.repo.SkillExperienceLevelRepository;
import com.bosch.eet.skill.management.repo.SkillLevelRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;

@Component
public class RequestEvaluationConverter {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private PersonalRepository personalRepository;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private SkillExperienceLevelRepository skillExperienceLevelRepository;
    @Autowired
    private PersonalSkillRepository personalSkillRepository;
    
    @Autowired
    private SkillCompetencyLeadRepository skillCompetencyLeadRepository;
    
    @Autowired
    private SkillLevelRepository skillLevelRepository;
    
    @Autowired
    private MessageSource messageSource;

    public RequestEvaluationDto convertToDTO(RequestEvaluation requestEvaluation, String approverId) {
        boolean isApproverExist = !Objects.isNull(requestEvaluation.getApprover());
        boolean isRequesterExist = !Objects.isNull(requestEvaluation.getRequester());
        String createdDAte = !Objects.isNull(requestEvaluation.getCreatedDate())
                ? Constants.TIMESTAMP_DATE_FORMAT.format(requestEvaluation.getCreatedDate())
                : null;
        String updatedDate = !Objects.isNull(requestEvaluation.getUpdatedDate())
                ? Constants.TIMESTAMP_DATE_FORMAT.format(requestEvaluation.getUpdatedDate())
                : createdDAte;
        List<RequestEvaluationDetail> details =  requestEvaluation.getRequestEvaluationDetails().stream()
        	.filter(x -> x.getApprover().getPersonalCode().equalsIgnoreCase(approverId))
        	.filter(x -> x.getStatus().equals(Constants.PENDING)).collect(Collectors.toList());
        
        RequestEvaluationDto requestEvaluationDto = RequestEvaluationDto.builder()
                .id(requestEvaluation.getId() != null ? requestEvaluation.getId() : null)
                .requester(isRequesterExist
                        ? requestEvaluation.getRequester().getUser().getDisplayName()
                        : null)
                .approver(isApproverExist
                        ? requestEvaluation.getApprover().getUser().getDisplayName()
                        : null)
                .status(requestEvaluation.getStatus())
                .createdDate(createdDAte)
                .updateDate(updatedDate)
                .approvedDate(!Objects.isNull(requestEvaluation.getApprovedDate())
                        ? Constants.TIMESTAMP_DATE_FORMAT.format(requestEvaluation.getApprovedDate())
                        : null)
                .comment(requestEvaluation.getComment())
                .status(requestEvaluation.getStatus())
                .competencyLeadEvaluateAll(requestEvaluation.getApprover().getUser().getName().equalsIgnoreCase(approverId) ? 
                		"" :  CollectionUtils.isEmpty(details) ? "EvaluateAll" : "")
                .build();
        return requestEvaluationDto;
    }

    public RequestEvaluationDto convertToDetailDTO(RequestEvaluation requestEvaluation) {
        boolean isApproverExist = !Objects.isNull(requestEvaluation.getApprover());
        boolean isRequesterExist = !Objects.isNull(requestEvaluation.getRequester());
        String createdDAte = !Objects.isNull(requestEvaluation.getCreatedDate())
                ? simpleDateFormat.format(requestEvaluation.getCreatedDate())
                : null;
        String updatedDate = !Objects.isNull(requestEvaluation.getUpdatedDate())
                ? simpleDateFormat.format(requestEvaluation.getUpdatedDate())
                : createdDAte;
        RequestEvaluationDto requestEvaluationDto = RequestEvaluationDto.builder()
                .id(requestEvaluation.getId())
                .requester(isRequesterExist
                        ? requestEvaluation.getRequester().getUser().getDisplayName()
                        : null)
                .approver(isApproverExist
                        ? requestEvaluation.getApprover().getUser().getDisplayName()
                        : null)
                .approverId(isApproverExist
                        ? requestEvaluation.getApprover().getUser().getId()
                        : null)
                .status(requestEvaluation.getStatus())
                .createdDate(createdDAte)
                .updateDate(updatedDate)
                .approvedDate(!Objects.isNull(requestEvaluation.getApprovedDate())
                        ? simpleDateFormat.format(requestEvaluation.getApprovedDate())
                        : null)
                .comment(requestEvaluation.getComment())
                .status(requestEvaluation.getStatus())
                .build();
        
        List<RequestEvaluationDetail> requestEvaluationDetails = requestEvaluation.getRequestEvaluationDetails();
        List<SkillDescriptionDTO> requestEvaluationDetailDTOs = new ArrayList<>();
        if (requestEvaluationDetails != null
                && !requestEvaluationDetails.isEmpty()) {
            for (RequestEvaluationDetail requestEvaluationDetail : requestEvaluationDetails) {
                Boolean isForwarded = false;
                if(!requestEvaluationDetail.getApprover().getId().equalsIgnoreCase(requestEvaluation.getApprover().getId())) {
                	isForwarded = true;
                }

                Skill skill = requestEvaluationDetail.getSkill();
                if (Objects.isNull(skill)) {
                    continue;
                }
                List<SkillLevelDTO> expectedSkillLevelDTOS = skillExperienceLevelRepository.findBySkillAndLevel(skill, requestEvaluation.getApprover().getLevel())
                        .stream().map(
                                skillExperienceLevel -> SkillLevelDTO.builder()
                                        .description(skillExperienceLevel.getDescription())
                                        .level(skillExperienceLevel.getName())
                                        .build()
                        ).collect(Collectors.toList());
                String expectedLevel = "";
                if (!expectedSkillLevelDTOS.isEmpty()) {
                    String[] levelSplit = expectedSkillLevelDTOS.get(0).getLevel().split(" ");
                    if (levelSplit.length > 1) {
                        expectedLevel = levelSplit[1];
                    }
                }
                
                SkillCompetencyLeadDto sclDto = SkillCompetencyLeadDto.builder()
                		.personalId(requestEvaluationDetail.getApprover().getId())
                		.displayName(requestEvaluationDetail.getApprover().getUser().getDisplayName())
                		.build();
                
                requestEvaluationDetailDTOs.add(SkillDescriptionDTO
                        .builder()
                        .skillId(skill.getId())
                        .skillName(skill.getName())
                        .competency(Objects.nonNull(skill.getSkillGroup())
                                ? skill.getSkillGroup().getName()
                                : null)
                        .level(Utility.floatToStringLevelFormat(requestEvaluationDetail.getCurrentLevel()))
                        .skillCompetencyLeadDto(sclDto)
                        .expectedLevel(expectedLevel)
                        .experience(requestEvaluationDetail.getCurrentExp())
                        .comment(requestEvaluationDetail.getComment())
                        .approverComment(requestEvaluationDetail.getApproverComment())
                        .status(requestEvaluationDetail.getStatus())
                        .isForwarded(isForwarded)
                        .build());
            }
        }
        requestEvaluationDto.setRequestEvaluationDetails(requestEvaluationDetailDTOs);
        return requestEvaluationDto;
    }

    public List<RequestEvaluationDto> convertToDTOs(List<RequestEvaluation> requestEvaluationList) {
        List<RequestEvaluationDto> requestEvaluationDtos = new ArrayList<>();
        for (RequestEvaluation skill : requestEvaluationList) {
            requestEvaluationDtos.add(convertToDTO(skill, ""));
        }
        return requestEvaluationDtos;
    }

    public RequestEvaluation mapDtoToEntity(RequestEvaluationDto requestDto) {
        Optional<Personal> requesterOpt = personalRepository.findById(requestDto.getRequester());
        if (!requesterOpt.isPresent()) {
            throw new SkillManagementException(MessageCode.SKM_USER_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_USER_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
        }
        Optional<Personal> approverOpt = personalRepository.findById(requestDto.getApprover());
        if (!approverOpt.isPresent()) {
            throw new SkillManagementException(MessageCode.SKM_USER_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_USER_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
        }
        RequestEvaluation entity = RequestEvaluation
                .builder()
                .requester(requesterOpt.get())
                .approver(approverOpt.get())
                .comment(requestDto.getComment())
                .createdDate(Calendar.getInstance().getTime())
                .status(EvaluationStatus.WaitingForApproval.getLabel())
                .build();
        List<SkillDescriptionDTO> requestEvaluationDTODetails = requestDto.getRequestEvaluationDetails();
        List<RequestEvaluationDetail> requestEvaluations = new ArrayList<>();
        for (SkillDescriptionDTO requestEvaluationDetail : requestEvaluationDTODetails) {
            Optional<Skill> skillOpt = skillRepository.findById(requestEvaluationDetail.getSkillId());
            if (!skillOpt.isPresent()) {
                throw new SkillManagementException(MessageCode.SKILL_NOT_FOUND.toString(),
                        messageSource.getMessage(MessageCode.SKILL_NOT_FOUND.toString(), null,
                                LocaleContextHolder.getLocale()), null, NOT_FOUND);
            }
            float currentLevel;
            try {
                currentLevel = Utility.validateSkillLevel(requestEvaluationDetail.getLevel());
            } catch (Exception e) {
                e.printStackTrace();
                throw new SkillManagementException(MessageCode.SKILL_LEVEL_INVALID.toString(),
                        messageSource.getMessage(MessageCode.SKILL_LEVEL_INVALID.toString(), null,
                                LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
            }
            requestEvaluations.add(RequestEvaluationDetail
                    .builder()
                    .skill(skillOpt.get())
                    .approver(approverOpt.get())
                    .currentExp(requestEvaluationDetail.getExperience())
                    .currentLevel(currentLevel)
                    .comment(requestEvaluationDetail.getComment())
                    .createdDate(Calendar.getInstance().getTime())
                    .status(EvaluationStatus.WaitingForApproval.getLabel())
                    .requestEvaluation(entity)
                    .build()
            );
        }
        entity.setRequestEvaluationDetails(requestEvaluations);
        return entity;
    }

    public RequestEvaluation newMapDtoToEntity(RequestEvaluationDto requestDto) {
        Optional<Personal> requesterOpt = personalRepository.findById(requestDto.getRequester());
        if (!requesterOpt.isPresent()) {
            throw new SkillManagementException(MessageCode.SKM_USER_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_USER_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
        }
        Optional<Personal> approverOpt = personalRepository.findById(requestDto.getApprover());
        if (!approverOpt.isPresent()) {
            throw new SkillManagementException(MessageCode.SKM_USER_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_USER_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
        }
        RequestEvaluation entity = RequestEvaluation
                .builder()
                .id(requestDto.getId() != null ? requestDto.getId() : null)
                .requester(requesterOpt.get())
                .approver(approverOpt.get())
                .comment(requestDto.getComment())
                .updatedDate(Calendar.getInstance().getTime())
                .status(EvaluationStatus.WaitingForApproval.getLabel())
                .build();
        try {
        	if (StringUtils.isEmpty(requestDto.getCreatedDate())) {
        		entity.setCreatedDate(Constants.SIMPLE_DATE_FORMAT.parse(requestDto.getCreatedDate()));        		
        	} else {
        		entity.setCreatedDate(Calendar.getInstance().getTime());   
        	}
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.INVALID_DATE.toString(),
                    messageSource.getMessage(MessageCode.INVALID_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
        }
        List<SkillDescriptionDTO> requestEvaluationDTODetails = requestDto.getRequestEvaluationDetails();
        List<RequestEvaluationDetail> requestEvaluations = new ArrayList<>();
        for (SkillDescriptionDTO requestEvaluationDetail : requestEvaluationDTODetails) {
            Optional<Skill> skillOpt = skillRepository.findById(requestEvaluationDetail.getSkillId());
            if (!skillOpt.isPresent()) {
                throw new SkillManagementException(MessageCode.SKILL_NOT_FOUND.toString(),
                        messageSource.getMessage(MessageCode.SKILL_NOT_FOUND.toString(), null,
                                LocaleContextHolder.getLocale()), null, NOT_FOUND);
            }
            float currentLevel;
            try {
                currentLevel = Utility.validateSkillLevel(requestEvaluationDetail.getLevel());
            } catch (Exception e) {
                e.printStackTrace();
                throw new SkillManagementException(MessageCode.SKILL_LEVEL_INVALID.toString(),
                        messageSource.getMessage(MessageCode.SKILL_LEVEL_INVALID.toString(), null,
                                LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
            }
            requestEvaluations.add(RequestEvaluationDetail
                    .builder()
                    .id(requestEvaluationDetail.getId() != null ? requestEvaluationDetail.getId() : null)
                    .skill(skillOpt.get())
                    .approver(approverOpt.get())
                    .currentExp(requestEvaluationDetail.getExperience())
                    .currentLevel(currentLevel)
                    .comment(requestEvaluationDetail.getComment())
                    .createdDate(Calendar.getInstance().getTime())
                    .status(EvaluationStatus.WaitingForApproval.getLabel())
                    .requestEvaluation(entity)
                    .build()
            );
        }
        entity.setRequestEvaluationDetails(requestEvaluations);
        return entity;
    }
    
    public RequestEvaluationDto newConvertToDetailDTO(RequestEvaluation requestEvaluation, Map<String, String> filterParams) {
        boolean isApproverExist = !Objects.isNull(requestEvaluation.getApprover());
        boolean isRequesterExist = !Objects.isNull(requestEvaluation.getRequester());
        String createdDAte = !Objects.isNull(requestEvaluation.getCreatedDate())
                ? simpleDateFormat.format(requestEvaluation.getCreatedDate())
                : null;
        String updatedDate = !Objects.isNull(requestEvaluation.getUpdatedDate())
                ? simpleDateFormat.format(requestEvaluation.getUpdatedDate())
                : createdDAte;
        RequestEvaluationDto requestEvaluationDto = RequestEvaluationDto.builder()
                .id(requestEvaluation.getId())
                .requester(isRequesterExist
                        ? requestEvaluation.getRequester().getUser().getDisplayName()
                        : null)
                .approver(isApproverExist
                        ? requestEvaluation.getApprover().getUser().getDisplayName()
                        : null)
                .approverId(isApproverExist
                        ? requestEvaluation.getApprover().getUser().getId()
                        : null)
                .status(requestEvaluation.getStatus())
                .createdDate(createdDAte)
                .updateDate(updatedDate)
                .approvedDate(!Objects.isNull(requestEvaluation.getApprovedDate())
                        ? simpleDateFormat.format(requestEvaluation.getApprovedDate())
                        : null)
                .comment(requestEvaluation.getComment())
                .status(requestEvaluation.getStatus())
                .build();
        List<RequestEvaluationDetail> requestEvaluationDetails = requestEvaluation.getRequestEvaluationDetails();
        List<SkillDescriptionDTO> requestEvaluationDetailDTOs = new ArrayList<>();
        if (requestEvaluationDetails != null
                && !requestEvaluationDetails.isEmpty()) {
            for (RequestEvaluationDetail requestEvaluationDetail : requestEvaluationDetails) {
                Skill skill = requestEvaluationDetail.getSkill();
                if (Objects.isNull(skill)) {
                    continue;
                }
                
                Optional<SkillLevel> skillLevelOpt = skillLevelRepository.findBySkillIdAndLevelId(skill.getId(), requestEvaluation.getRequester().getLevel().getId());
                
                String expectedLevel = "";
                if (skillLevelOpt.isPresent()) {
                    String[] levelSplit = skillLevelOpt.get().getLevelLable().split(" ");
                    if (levelSplit.length > 1) {
                        expectedLevel = Utility.floatToStringLevelFormat(Float.parseFloat(levelSplit[1]));
                    }
                }
                
                List<SkillCompetencyLead> sclOpt = skillCompetencyLeadRepository
                		.findBySkillIdAndPersonalId(skill.getId(), requestEvaluationDetail.getApprover().getId());
                
                List<SkillCompetencyLead> competencyLeads = skillCompetencyLeadRepository.findBySkillIdOrderByPersonalAsc(skill.getId());
                
                String displayNameCompetencyLeads ="";
                if(sclOpt.size()==0) {
                	for (int i = 0; i < competencyLeads.size(); i++) {
                		displayNameCompetencyLeads+=competencyLeads.get(i).getPersonal().getUser().getDisplayName();
                		if(i < competencyLeads.size()-1) {
                			displayNameCompetencyLeads+=", ";
                		}
					}
                }
                
                SkillCompetencyLeadDto sclDto = SkillCompetencyLeadDto.builder()
                		.personalId(requestEvaluationDetail.getApprover().getId())
                		.displayName(displayNameCompetencyLeads)
                		.build();              

                List<SkillLevelDTO> skillLevelDTOS = skillExperienceLevelRepository.findDistinctBySkillId(skill.getId())
                        .stream().map(
                                skillExperienceLevel -> SkillLevelDTO.builder()
                                        .description(skillExperienceLevel.getDescription())
                                        .level(skillExperienceLevel.getName())
                                        .build()
                        ).collect(Collectors.toList());


                if(filterParams.get("user_id").equalsIgnoreCase(requestEvaluationDetail.getApprover().getId())
                		|| filterParams.get("user_id").equalsIgnoreCase(requestEvaluation.getApprover().getId())
                		|| filterParams.get("user_id").equalsIgnoreCase(requestEvaluation.getRequester().getId())) {
                    Optional<PersonalSkill> personalSkillOpt = personalSkillRepository
                            .findByPersonalIdAndSkillId(requestEvaluation.getRequester().getId(),
                                    skill.getId());
                    Boolean isForwarded = false;
                    if(!requestEvaluationDetail.getApprover().getId().equalsIgnoreCase(requestEvaluation.getApprover().getId())) {
                    	isForwarded = true;
                    }
	                requestEvaluationDetailDTOs.add(
	                		SkillDescriptionDTO
	                        .builder()
	                        .id(requestEvaluationDetail.getId())
	                        .skillId(skill.getId())
	                        .skillName(skill.getName())
	                        .competency(Objects.nonNull(skill.getSkillGroup())
	                                ? skill.getSkillGroup().getName()
	                                : null)
                            .oldLevel(personalSkillOpt.isPresent() ? Utility.floatToStringLevelFormat(personalSkillOpt.get().getLevel()) : Constants.DEFAULT_SKILL_LEVEL)
	                        .level(Utility.floatToStringLevelFormat(requestEvaluationDetail.getCurrentLevel()))
	                        .skillCompetencyLeadDto(!sclOpt.isEmpty() ? SkillCompetencyLeadConverterUtil.convertToDTO(sclOpt.get(0)) : sclDto )
	                        .expectedLevel(expectedLevel)
                            .oldExperience(personalSkillOpt.isPresent() ? personalSkillOpt.get().getExperience() : Constants.DEFAULT_EXPERIENCE)
	                        .experience(requestEvaluationDetail.getCurrentExp())
	                        .comment(requestEvaluationDetail.getComment())
	                        .approverComment(requestEvaluationDetail.getApproverComment())
	                        .status(requestEvaluationDetail.getStatus())
	                        .isForwarded(isForwarded)
                            .skillLevelDTOS(skillLevelDTOS)
                            .skillType(Objects.nonNull(skill.getSkillGroup())
	                                ? skill.getSkillGroup().getSkillType().getName()
	                                : null)
	                        .build());
                }
            }
        }
        requestEvaluationDto.setRequestEvaluationDetails(requestEvaluationDetailDTOs);
        return requestEvaluationDto;
    }


}
