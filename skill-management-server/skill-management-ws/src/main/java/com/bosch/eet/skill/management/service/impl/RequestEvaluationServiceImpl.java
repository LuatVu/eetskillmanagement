package com.bosch.eet.skill.management.service.impl;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.constant.EmailConstant;
import com.bosch.eet.skill.management.converter.RequestEvaluationConverter;
import com.bosch.eet.skill.management.converter.utils.RequestEvaluationDetailConverterUtil;
import com.bosch.eet.skill.management.dto.RequestEvaluationDetailDto;
import com.bosch.eet.skill.management.dto.RequestEvaluationDto;
import com.bosch.eet.skill.management.dto.RequestEvaluationHistoricalDto;
import com.bosch.eet.skill.management.dto.RequestEvaluationPendingDto;
import com.bosch.eet.skill.management.dto.SkillDescriptionDTO;
import com.bosch.eet.skill.management.dto.SkillDto;
import com.bosch.eet.skill.management.elasticsearch.repo.PersonalElasticRepository;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.RequestEvaluation;
import com.bosch.eet.skill.management.entity.RequestEvaluationDetail;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.util.Utility;
import com.bosch.eet.skill.management.mail.EmailService;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.PersonalSkillRepository;
import com.bosch.eet.skill.management.repo.RequestEvaluationDetailRepository;
import com.bosch.eet.skill.management.repo.RequestEvaluationRepository;
import com.bosch.eet.skill.management.service.CompetencyLeadService;
import com.bosch.eet.skill.management.service.RequestEvaluationService;
import com.bosch.eet.skill.management.specification.RequestEvaluationDetailSpecification;
import com.bosch.eet.skill.management.specification.RequestEvaluationSpecification;
import com.bosch.eet.skill.management.usermanagement.consts.MessageCode;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestEvaluationServiceImpl implements RequestEvaluationService {

    private final RequestEvaluationRepository requestEvaluationRepository;
    
    private final PersonalRepository personalRepository;
    
    private final PersonalElasticRepository personalElasticRepository;
    
    private final RequestEvaluationConverter requestEvaluationConverter;
    
    private final RequestEvaluationDetailRepository requestEvaluationDetailRepository;
    
    private final MessageSource messageSource; 
    
    private final EmailService emailService;
    
    private final CompetencyLeadService competencyLeadService;

	private final UserRepository userRepository;

	private final PersonalSkillRepository personalSkillRepository;

    Date updatedDate = new Date();
    Date approvedDate = new Date();
    Date nullDate = null;
    
    //Forward Request Detail
    @Override
    @Transactional
    public RequestEvaluationDto forwardRequest(String requestId, List<String> competencyListIds) {
        HashMap <String, String> forwardEmailAddressList = new HashMap<>();

        Optional<RequestEvaluation> requestOptional = requestEvaluationRepository.findById(requestId);
        if (requestOptional.isPresent()) {
            RequestEvaluation request = requestOptional.get();	
			String requesterName = request.getRequester().getUser().getDisplayName();
			String requesterMail = request.getRequester().getUser().getEmail();
        	forwardEmailAddressList.put(requesterName, requesterMail);
            List<RequestEvaluationDetail> listDetails = request.getRequestEvaluationDetails();
            for (RequestEvaluationDetail detail : listDetails) {
            	if(!detail.getStatus().equals(Constants.WAITING_FOR_APPROVAL)) {
            		continue;
            	}
                for (String competencyId : competencyListIds) {
                    Optional<Personal> newAprrover = personalRepository.findById(competencyId);
                	List<SkillDto> skills = competencyLeadService.findSkillsByCompetencyLeadId(competencyId);
		                if(newAprrover.isPresent()){
		                	for (SkillDto skill : skills) {
		                		if(skill.getId().equalsIgnoreCase(detail.getSkill().getId())) {
				                	Personal forwardedApprover = newAprrover.get();
				                	detail.setApprover(forwardedApprover);
				                	String approverName = forwardedApprover.getUser().getDisplayName();
				                	String approverMail = forwardedApprover.getUser().getEmail();
				                	forwardEmailAddressList.put(approverName, approverMail);
		                		 }
		                	}
		                } else {
		                	 throw new SkillManagementException(messageSource.getMessage(
		                             MessageCode.MISSING_APPROVER_ID.toString(),
		                             null,
		                             LocaleContextHolder.getLocale()),
		                     MessageCode.MISSING_APPROVER_ID.toString(),
		                     null,
		                     NOT_FOUND);
		                }
                    }
            }
            forwardEmailAddressList.forEach((personalName, personalMail)->{
            	if(!personalName.equalsIgnoreCase(requesterName)) {
	                emailService.mailToApproverAboutForwardRequest(personalName, requesterName, personalMail, Constants.MANAGE_PENDING_REQUEST_LINK);
            	} else {
            		List<String> personalNameOfMap = new ArrayList<>(forwardEmailAddressList.keySet());
            		personalNameOfMap.forEach(nameCompetencyLead->{
            			if(!nameCompetencyLead.equalsIgnoreCase(requesterName)) {
                			emailService.mailToRequesterAboutForwardRequest(nameCompetencyLead,
                					requesterName, personalMail, Constants.MY_REQUEST_LINK);
            			}
            		});
            	}
            });

            return requestEvaluationConverter.convertToDetailDTO(request);
        	} else {
           	 throw new SkillManagementException(messageSource.getMessage(
                     MessageCode.SKM_REQUEST_EVALUATION_NOT_FOUND.toString(),
                     null,
                     LocaleContextHolder.getLocale()),
             MessageCode.SKM_REQUEST_EVALUATION_NOT_FOUND.toString(),
             null,
             NOT_FOUND);
        }
    }
    
    @Override
    public List<RequestEvaluationDto> findAll(Map<String, String> filterParams) {
        Specification<RequestEvaluation> requestEvaluationSpecification = RequestEvaluationSpecification.search(filterParams);
        List<RequestEvaluation> requestEvaluations = requestEvaluationRepository.findAll(requestEvaluationSpecification);
        Map<String, RequestEvaluationDto> map = new HashMap<>();
        List<RequestEvaluationDto> result = new ArrayList<>();
        
        List<RequestEvaluation> temp = requestEvaluationRepository
        		.findAllByApproverIdAndStatus(filterParams.get(Constants.APPROVER_ID), Constants.WAITING_FOR_APPROVAL);
		if (CollectionUtils.isEmpty(requestEvaluations)) {
			buildRequestEvaluationDetail(filterParams, map, result, temp);
			return result;
		} else {
			//check for case exist request evaluation and request forward
			for (RequestEvaluation requestEvaluation : temp) {
				if(!requestEvaluations.contains(requestEvaluation)){
					requestEvaluations.add(requestEvaluation);
				}
			}
			buildRequestEvaluationDetail(filterParams, map, result, requestEvaluations);
			return result;
		}
		
    }

	private void buildRequestEvaluationDetail(Map<String, String> filterParams, Map<String, RequestEvaluationDto> map,
			List<RequestEvaluationDto> result, List<RequestEvaluation> temp) {
		for(RequestEvaluation requestEvaluation : temp) {
		  	if(filterParams.containsKey(Constants.REQUESTER_ID)) {
		  		map.put(requestEvaluation.getId() ,requestEvaluationConverter.convertToDTO(requestEvaluation,""));
		  	}else {
		    	if(requestEvaluation.getStatus().equalsIgnoreCase(Constants.WAITING_FOR_APPROVAL)) {
					map.put(requestEvaluation.getId(), requestEvaluationConverter.convertToDTO(requestEvaluation,
							filterParams.get(Constants.APPROVER_ID)));
		    	}
		  	}
		  }
		 map.forEach((key, value) -> {
		  	result.add(value);
		});
	}
    
    @Override
    public RequestEvaluationDto findByID(String evaluationID) {
        Optional<RequestEvaluation> requestEvaluationOpt = requestEvaluationRepository.findById(evaluationID);
        if(!requestEvaluationOpt.isPresent()){
            throw new SkillManagementException(MessageCode.SKM_REQUEST_EVALUATION_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_REQUEST_EVALUATION_NOT_FOUND.toString(),
                            null,
                            LocaleContextHolder.getLocale()),
                    null,
                    NOT_FOUND);
        }
        return requestEvaluationConverter.convertToDetailDTO(requestEvaluationOpt.get());
    }

	// create and edit request
    @Override
    @Transactional
    public RequestEvaluationDto createRequest(RequestEvaluationDto request, String authNTID) {
		// Case rejected: create a new request evaluation
		if (request.getStatus().equals(Constants.REJECTED) || request.getStatus().equals(Constants.APPROVED_OR_REJECTED)) {
			RequestEvaluation requestEvaluation = requestEvaluationConverter.mapDtoToEntity(request);
			RequestEvaluation requestEvaluationEntity = new RequestEvaluation();
			try {
				requestEvaluationEntity = requestEvaluationRepository.save(requestEvaluation);
				String approverName = requestEvaluation.getApprover().getUser().getDisplayName();
				String approverEmail = requestEvaluation.getApprover().getUser().getEmail();
				String requesterName = requestEvaluation.getRequester().getUser().getDisplayName();
            emailService.mailToApproverAboutNewRequest(approverName, requesterName, approverEmail, Constants.MANAGE_PENDING_REQUEST_LINK);
			} catch (Exception e){
				e.printStackTrace();
			}
			return requestEvaluationConverter.convertToDTO(requestEvaluationEntity,"");
		}
		// If it's not rejected, update the request evaluation
		else {
			RequestEvaluation requestEvaluation = requestEvaluationConverter.newMapDtoToEntity(request);

			// Create new request
			// Check if user has pending request
			if(requestEvaluation.getId() == null){
				Map<String,String> filterParams = new HashMap<>();
				filterParams.put("requester_id", authNTID);
				Specification<RequestEvaluation> requestEvaluationSpecification = RequestEvaluationSpecification.search(filterParams);
				List<RequestEvaluation> requestEvaluations = requestEvaluationRepository.findAll(requestEvaluationSpecification);
				for ( RequestEvaluation item: requestEvaluations ) {
					if (item.getStatus().equalsIgnoreCase(Constants.PENDING)){
						log.info(MessageCode.SKM_HAS_PENDING_REQUEST_EVALUATION.toString());
						throw new SkillManagementException(MessageCode.SKM_HAS_PENDING_REQUEST_EVALUATION.toString(),
								messageSource.getMessage(MessageCode.SKM_HAS_PENDING_REQUEST_EVALUATION.toString(),
									null,
									LocaleContextHolder.getLocale()),
								null,
								PRECONDITION_FAILED);
					}
				}
			}

			requestEvaluationRepository.save(requestEvaluation);
            if (requestEvaluation.getId() != null) {
                for (RequestEvaluationDetail requestEvaluationDetail : requestEvaluation.getRequestEvaluationDetails()) {
                    requestEvaluationDetailRepository.save(requestEvaluationDetail);
                }
            }
			
			return requestEvaluationConverter.convertToDTO(requestEvaluation,"");
		}
    }
    
    @Override
    public RequestEvaluationDto newFindByID(String evaluationID, Map<String, String> filterParams) {
        Optional<RequestEvaluation> requestEvaluationOpt = requestEvaluationRepository.findById(evaluationID);
        if(!requestEvaluationOpt.isPresent()){
            throw new SkillManagementException(MessageCode.SKM_REQUEST_EVALUATION_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_REQUEST_EVALUATION_NOT_FOUND.toString(),
                            null,
                            LocaleContextHolder.getLocale()),
                    null,
                    NOT_FOUND);
        }
        return requestEvaluationConverter.newConvertToDetailDTO(requestEvaluationOpt.get(), filterParams);
    }
    
    @Override
    @Transactional
    public void updateRequestStatus(RequestEvaluation request) {
    	List<RequestEvaluationDetail> listRequestDetail = request.getRequestEvaluationDetails();
    	
		if (listRequestDetail.stream().anyMatch(r -> r.getStatus().equalsIgnoreCase(Constants.WAITING_FOR_APPROVAL))) {
			request.setStatus(Constants.WAITING_FOR_APPROVAL);
		} else if (listRequestDetail.stream().allMatch(r -> r.getStatus().equalsIgnoreCase(Constants.APPROVED))) {
			request.setStatus(Constants.APPROVED);
			request.setApprovedDate(new Date());
		} else if (listRequestDetail.stream().allMatch(r -> r.getStatus().equalsIgnoreCase(Constants.REJECTED))) {
			request.setStatus(Constants.REJECTED);
			request.setApprovedDate(new Date());
		} else {
			request.setStatus(Constants.APPROVED_OR_REJECTED);
			request.setApprovedDate(new Date());
		}

    }
    
    @Override
    @Transactional
    public RequestEvaluationDetailDto updateRequestDetail(String requestDetailId, RequestEvaluationDetailDto requestDetailDto) {
    	Optional<RequestEvaluationDetail> detailOpt = requestEvaluationDetailRepository.findById(requestDetailId);
    	if(!detailOpt.isPresent()) {
            throw new SkillManagementException(MessageCode.SKM_REQUEST_EVALUATION_DETAIL_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_REQUEST_EVALUATION_DETAIL_NOT_FOUND.toString(),
                            null,
                            LocaleContextHolder.getLocale()),
                    null,
                    NOT_FOUND);
    	}
    	RequestEvaluationDetail detail = detailOpt.get();
    	RequestEvaluation request = detail.getRequestEvaluation();
    	
    	Personal requester = detail.getRequestEvaluation().getRequester();
    	if (requestDetailDto.getStatus().equalsIgnoreCase(Constants.APPROVED)) {
			detail.setStatus(requestDetailDto.getStatus());
			detail.setApproverComment(requestDetailDto.getApproverComment());
			detail.setCurrentExp(requestDetailDto.getSkillDescription().getExperience());
			detail.setCurrentLevel(Utility.validateSkillLevel(requestDetailDto.getSkillDescription().getLevel()));
			detail.setApprovedDate(approvedDate);
			detail.setUpdatedDate(updatedDate);

			Skill evaluatingSkill = detail.getSkill();
			Optional<PersonalSkill> personalSkillOpt = personalSkillRepository.findByPersonalIdAndSkillId(
					requester.getId(),
					evaluatingSkill.getId()
			);
			PersonalSkill personalSkill = personalSkillOpt.orElseGet(() ->
					PersonalSkill.builder()
							.personal(requester)
							.skill(evaluatingSkill)
							.build()
			);

			float level = detail.getCurrentLevel();
			int exp = detail.getCurrentExp();
			String personalSkillId = personalSkill.getId();
			if(level == Constants.DEFAULT_FLOAT_SKILL_LEVEL &&
					exp == Constants.DEFAULT_EXPERIENCE){
				if(Objects.nonNull(personalSkillId)) {
					personalSkillRepository.deleteById(personalSkillId);
				}
			} else {
				personalSkill.setLevel(level);
				personalSkill.setExperience(exp);
				personalSkillRepository.save(personalSkill);
			}

		} else {
			if(!Objects.isNull(requestDetailDto.getComment())) {
				detail.setComment(requestDetailDto.getComment());
			}
			detail.setApproverComment(requestDetailDto.getApproverComment());
			detail.setStatus(requestDetailDto.getStatus());
			detail.setApprovedDate(nullDate);
			detail.setUpdatedDate(updatedDate);
		}

		String statusAction;
		if(requestDetailDto.getStatus().equalsIgnoreCase(Constants.APPROVED)){
			statusAction = "approved";
		} else if (requestDetailDto.getStatus().equalsIgnoreCase(Constants.REJECTED)) {
			statusAction = "rejected";
		} else {
			statusAction = "updated";
		}
		String emailStatus = emailService.mailToRequesterAndApproverAboutRequest(
				request.getRequester().getUser().getName(),
				request.getRequester().getUser().getEmail(),
				statusAction,
				Constants.MY_REQUEST_LINK);
    	personalElasticRepository.updatePersonal(requester);
    	updateRequestStatus(request);
    	RequestEvaluationDetail saveDetail = requestEvaluationDetailRepository.save(detail);
		RequestEvaluationDetailDto requestEvaluationDetailDto = convertRequestEvaluationDetailDto(saveDetail);
		if(emailStatus != null) {
			requestEvaluationDetailDto.setSentEmail(emailStatus.equalsIgnoreCase(EmailConstant.SEND_SUCCESS));
		}
    	return requestEvaluationDetailDto;
    }

	public RequestEvaluationDetailDto convertRequestEvaluationDetailDto(RequestEvaluationDetail detail) {
		return RequestEvaluationDetailConverterUtil.convertDetailToDTO(detail);
	}
    
    @Override
	@Transactional
    public void updateRequest(RequestEvaluationDto requestDto, @NonNull String authName) {
		User authUser = userRepository.findByName(authName).orElse(null);
		if (authUser == null || StringUtils.isBlank(authUser.getId())) {
			            throw new SkillManagementException(messageSource.getMessage(
								com.bosch.eet.skill.management.usermanagement.consts.MessageCode.MISSING_APPROVER.toString(),
								null,
								LocaleContextHolder.getLocale()),
								com.bosch.eet.skill.management.usermanagement.consts.MessageCode.MISSING_APPROVER.toString(),
								null,
								HttpStatus.NOT_FOUND);
		}
		String personalId = authUser.getId();

    	requestEvaluationDetailRepository.updateStatusByRequestIdAndApproverId(requestDto.getStatus(), requestDto.getId(), personalId);
    	Optional<RequestEvaluation> requestOpt = requestEvaluationRepository.findById(requestDto.getId());
    	if(!requestOpt.isPresent()) {
            throw new SkillManagementException(MessageCode.SKM_REQUEST_EVALUATION_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_REQUEST_EVALUATION_NOT_FOUND.toString(),
                            null,
                            LocaleContextHolder.getLocale()),
                    null,
                    NOT_FOUND);
    	}
    	RequestEvaluation request = requestOpt.get();
		request.setComment(requestDto.getComment()); // set comment whatever status
		if(requestDto.getStatus().equalsIgnoreCase(Constants.APPROVED)) {
			request.setApprovedDate(approvedDate);
			approveRequestByDto(requestDto);
		}

		Personal requester = request.getRequester();
    	personalElasticRepository.updatePersonal(requester);
    	updateRequestStatus(request);

		String statusAction;
		if(requestDto.getStatus().equalsIgnoreCase(Constants.APPROVED)){
			statusAction = "approved";
		} else if (requestDto.getStatus().equalsIgnoreCase(Constants.REJECTED)) {
			statusAction = "rejected";
		} else {
			statusAction = "updated";
		}
		emailService.mailToRequesterAndApproverAboutRequest(
				request.getRequester().getUser().getName(),
				request.getRequester().getUser().getEmail(),
				statusAction,
				Constants.MY_REQUEST_LINK);
    }
    
    public void approveRequestByDto(RequestEvaluationDto requestDto) {
    	Optional<RequestEvaluation> requestOpt = requestEvaluationRepository.findById(requestDto.getId());
    	if(!requestOpt.isPresent()) {
            throw new SkillManagementException(MessageCode.SKM_REQUEST_EVALUATION_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_REQUEST_EVALUATION_NOT_FOUND.toString(),
                            null,
                            LocaleContextHolder.getLocale()),
                    null,
                    NOT_FOUND);
    	}
    	RequestEvaluation request = requestOpt.get();
    	Personal requester = request.getRequester();
    	List<SkillDescriptionDTO> listDetails = requestDto.getRequestEvaluationDetails();

		for (SkillDescriptionDTO requestDetailDto : listDetails) {
			RequestEvaluationDetail detail = null;
			List<RequestEvaluationDetail> details = request.getRequestEvaluationDetails();
			for(RequestEvaluationDetail eachDetail : details) {
				if(eachDetail.getId().equalsIgnoreCase(requestDetailDto.getId())) {
					detail = eachDetail;
					break;
				}
			}

			if(Objects.isNull(detail)){
				throw new SkillManagementException(MessageCode.SKM_REQUEST_EVALUATION_DETAIL_NOT_FOUND.toString(),
						messageSource.getMessage(MessageCode.SKM_REQUEST_EVALUATION_DETAIL_NOT_FOUND.toString(),
								null, LocaleContextHolder.getLocale()), null, BAD_REQUEST);
			}

			if(detail.getStatus().equalsIgnoreCase(Constants.REJECTED)) {
				continue;
			}

			detail.setCurrentExp(requestDetailDto.getExperience());
			detail.setCurrentLevel(Utility.validateSkillLevel(requestDetailDto.getLevel()));
			detail.setApprovedDate(approvedDate);
			detail.setUpdatedDate(updatedDate);

			Skill evaluatingSkill = detail.getSkill();
			Optional<PersonalSkill> personalSkillOpt = personalSkillRepository.findByPersonalIdAndSkillId(
					requester.getId(),
					evaluatingSkill.getId()
			);
			PersonalSkill personalSkill = personalSkillOpt.orElseGet(() ->
					PersonalSkill.builder()
							.personal(requester)
							.skill(evaluatingSkill)
							.build()
			);
			float level = detail.getCurrentLevel();
			int exp = detail.getCurrentExp();
			String personalSkillId = personalSkill.getId();
			if(level == Constants.DEFAULT_FLOAT_SKILL_LEVEL &&
					exp == Constants.DEFAULT_EXPERIENCE){
				if(Objects.nonNull(personalSkillId)) {
					personalSkillRepository.deleteById(personalSkillId);
				}
			} else {
				personalSkill.setLevel(level);
				personalSkill.setExperience(exp);
				personalSkillRepository.save(personalSkill);
			}
		}
    }
    
    public void approveRequestAndUpdateSkill(RequestEvaluation request) {
		Personal requester = request.getRequester();
		List<RequestEvaluationDetail> listRequestDetail = request.getRequestEvaluationDetails();
		for (RequestEvaluationDetail requestDetail : listRequestDetail) {
			Set<PersonalSkill> listPersonalSkill = requester.getPersonalSkills();
			for (PersonalSkill personalSkill : listPersonalSkill) {
				Skill skill = personalSkill.getSkill();
				String skillId = skill.getId();
				if (skillId.equalsIgnoreCase(requestDetail.getSkill().getId())) {
					personalSkill.setLevel(requestDetail.getCurrentLevel());
					personalSkill.setExperience((requestDetail.getCurrentExp()));

				}
			}
		}
	}

	@Override
	public RequestEvaluationPendingDto notificationRequestPending(String personalId) {
		RequestEvaluationPendingDto requestEvaluationPendingDto = requestEvaluationRepository
				.findRequestPendingByPersonalIdAndStatus(personalId, Constants.PENDING);
		// For case: if a request forward then it was not present in RequestEvaluationDetail 
		Long requestPending = requestEvaluationRepository.countRequestPendingByPersonalIdAndStatus(personalId,
				Constants.PENDING);
		requestEvaluationPendingDto.setCount(requestEvaluationPendingDto.getCount() + requestPending);
		return requestEvaluationPendingDto;
	}

	@Override
	public List<RequestEvaluationDto> findRequestEvaluated(String ntid) {
        List<RequestEvaluationDto> result = new ArrayList<>();
		List<RequestEvaluation> temp = requestEvaluationRepository
				.findAllRequestEvaluated(ntid, Constants.WAITING_FOR_APPROVAL);
		for (RequestEvaluation requestEvaluation : temp) {
			result.add(requestEvaluationConverter.convertToDTO(requestEvaluation, ntid));
		}
		return result;
	}
	
	@Override
	public Map<String, Object> findHistorical(String requesterId, Integer page, Integer size,
			Map<String, String> query) {
		Map<String, Object> response = new HashMap<>();
		List<RequestEvaluationHistoricalDto> evaluationHistoricalDtos = new ArrayList<>();
		Specification<RequestEvaluationDetail> specification = RequestEvaluationDetailSpecification.search(requesterId,
				query);
		Pageable paging = PageRequest.of(page < 0 ? Constants.DEFAULT_PAGE : page,
				size <= 0 ? Constants.DEFAULT_SIZE : size);
		Page<RequestEvaluationDetail> evaluationDetailPages = requestEvaluationDetailRepository.findAll(specification,
				paging);
		if (!evaluationDetailPages.hasContent()) {
			putToMapResponseHistoricalLevel(response, evaluationHistoricalDtos, evaluationDetailPages);
			return response;
		}
		List<RequestEvaluationDetail> evaluationDetails = evaluationDetailPages.getContent();
		int sizeResultQuery = evaluationDetails.size();
		for (int i = 0; i < sizeResultQuery; i++) {
			RequestEvaluationDetail requestEvaluationDetail = evaluationDetails.get(i);
			if (i > 0) {
				RequestEvaluationHistoricalDto preDto = evaluationHistoricalDtos.get(i - 1);
				updateLevelAndExpPreDTO(requestEvaluationDetail, preDto);
			}
			RequestEvaluationHistoricalDto dto = buildRequestEvaluationHistoricalDto(requestEvaluationDetail);
			if (sizeResultQuery - 1 == i) {
				checkIsAvailableOldRecord(dto, requestEvaluationDetail);
			}
			evaluationHistoricalDtos.add(dto);
		}
		putToMapResponseHistoricalLevel(response, evaluationHistoricalDtos, evaluationDetailPages);
		return response;
	}

	private RequestEvaluationHistoricalDto buildRequestEvaluationHistoricalDto(
			RequestEvaluationDetail requestEvaluationDetail) {
		String approverComment = requestEvaluationDetail.getApproverComment();
		String note = Objects.nonNull(approverComment) ? approverComment
				: requestEvaluationDetail.getRequestEvaluation().getComment();
		RequestEvaluationHistoricalDto dto = RequestEvaluationHistoricalDto.builder().note(note)
				.currentExp(requestEvaluationDetail.getCurrentExp())
				.currentLevel(requestEvaluationDetail.getCurrentLevel())
				.skillName(requestEvaluationDetail.getSkill().getName())
				.skillCluster(requestEvaluationDetail.getSkill().getSkillGroup().getName()).build();
		if (!Objects.isNull(requestEvaluationDetail.getCreatedDate())) {
			dto.setDate(requestEvaluationDetail.getCreatedDate().toString());
		}
		return dto;
	}

	private void putToMapResponseHistoricalLevel(Map<String, Object> response,
			List<RequestEvaluationHistoricalDto> evaluationHistoricalDtos,
			Page<RequestEvaluationDetail> evaluationDetailPages) {
		response.put(Constants.HISTORICAL_CHANGE_LEVEL, evaluationHistoricalDtos);
		response.put(Constants.TOTAL_PAGE, evaluationDetailPages.getTotalPages());
		response.put(Constants.TOTAL_ITEM, evaluationDetailPages.getTotalElements());
	}

	private void updateLevelAndExpPreDTO(RequestEvaluationDetail requestEvaluationDetail,
			RequestEvaluationHistoricalDto preDto) {
		String skillName = requestEvaluationDetail.getSkill().getName();
		preDto.setOldExp(skillName.equals(preDto.getSkillName()) ? requestEvaluationDetail.getCurrentExp() : 0);
		preDto.setOldLevel(skillName.equals(preDto.getSkillName()) ? requestEvaluationDetail.getCurrentLevel() : 0);
	}

	private void checkIsAvailableOldRecord(RequestEvaluationHistoricalDto dto,
			RequestEvaluationDetail requestEvaluationDetail) {
		dto.setOldExp(0);
		dto.setOldLevel(0);
		Pageable limit = PageRequest.of(0, 1);// get query with limit is 1
		List<RequestEvaluationDetail> requestEvaluationDetailOpt = requestEvaluationDetailRepository
				.findRequestEvaluatedDetailByRequesterAndSkillAndCreatedDate(
						requestEvaluationDetail.getRequestEvaluation().getRequester(),
						requestEvaluationDetail.getSkill(), requestEvaluationDetail.getCreatedDate(), limit);
		if (!requestEvaluationDetailOpt.isEmpty()) {
			RequestEvaluationDetail evaluationDetail = requestEvaluationDetailOpt.get(0);
			dto.setOldExp(evaluationDetail.getCurrentExp());
			dto.setOldLevel(evaluationDetail.getCurrentLevel());
		}
	}
}
