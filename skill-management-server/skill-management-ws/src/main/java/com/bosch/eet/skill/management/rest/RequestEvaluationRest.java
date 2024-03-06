package com.bosch.eet.skill.management.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.RequestEvaluationDetailDto;
import com.bosch.eet.skill.management.dto.RequestEvaluationDto;
import com.bosch.eet.skill.management.dto.RequestEvaluationPendingDto;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.service.RequestEvaluationService;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author TGI2HC
 */

@RequestMapping
@RestController
@Slf4j
public class RequestEvaluationRest {
	
	@Autowired
	private RequestEvaluationService requestEvaluationService;

	@Autowired
	private UserRepository userRepository;
    
    @GetMapping(value = Routes.URI_REST_REQUEST_EVALUATION)
	public GenericResponseDTO<List<RequestEvaluationDto>> getRequests(@RequestParam(name = "requester", required = false) Boolean requester,
																	  Pageable pageable,
																	  Authentication auht) {
		String userNTID = auht.getName();
		Map<String,String> filterParams = new HashMap<>();
		if ((requester != null) && requester) {
			filterParams.put("requester_id", userNTID);
		} else {
			filterParams.put("approver_id", userNTID);
			//TODO: when findAll return null, check ntid if competency lead => check detail approver
		}
		return  GenericResponseDTO.<List<RequestEvaluationDto>>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(requestEvaluationService.findAll(filterParams))
                .build();
	}

	@GetMapping(value = Routes.URI_REST_REQUEST_EVALUATED)
	public GenericResponseDTO<List<RequestEvaluationDto>> getRequestsEvaluated(Authentication auht) {
		String userNTID = auht.getName();
		return  GenericResponseDTO.<List<RequestEvaluationDto>>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(requestEvaluationService.findRequestEvaluated(userNTID))
                .build();
	}
	
    @GetMapping(value = Routes.URI_REST_REQUEST_EVALUATION_EVALUATION_ID)
	public GenericResponseDTO<RequestEvaluationDto> getRequestByID(
			@PathVariable(name = "evaluation_id") String evaluationId,Authentication auht) {

    	String userId = userRepository.findByName(auht.getName()).get().getId();

		Map<String,String> filterParams = new HashMap<>();
		filterParams.put("user_id", userId);


		RequestEvaluationDto requestEvaluationDto = requestEvaluationService.newFindByID(evaluationId, filterParams);
		return  GenericResponseDTO.<RequestEvaluationDto>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(requestEvaluationDto)
                .build();
	}
	@PostMapping(value = Routes.URI_REST_REQUEST_EVALUATION)
	public GenericResponseDTO<RequestEvaluationDto> createRequest(@Valid @RequestBody RequestEvaluationDto body, Authentication auth) {
		log.info(body.toString());
		try {
			RequestEvaluationDto requestEvaluationDto = requestEvaluationService.createRequest(body, auth.getName());
			return GenericResponseDTO.<RequestEvaluationDto>builder()
					.code(MessageCode.SUCCESS.toString())
					.message("Evaluate successfully")
					.data(requestEvaluationDto)
					.build();
		}catch (Exception e) {
			return  GenericResponseDTO.<RequestEvaluationDto>builder()
					.code(MessageCode.REQUEST_EVALUATION_CREATE_FAIL.toString())
					.message("Request evaluation create fail")
					.build();
		}
	}
	
	//approve || reject all
    @PostMapping(value = Routes.URI_REST_REQUEST_EVALUATION_UPDATE)
	public GenericResponseDTO<String> updateRequest(@PathVariable(name = "id", required = true) String requestId,
																	  Authentication auth, @RequestBody RequestEvaluationDto requestDto) {
		try {
			requestEvaluationService.updateRequest(requestDto, auth.getName());
			return  GenericResponseDTO.<String>builder()
	                .code(MessageCode.SUCCESS.toString())
	                .data("Update request successfully")
	                .build();
		}catch (Exception e) {
			return  GenericResponseDTO.<String>builder()
	                .code(MessageCode.CANNOT_UPDATE_REQUEST_EVALUATION.toString())
	                .data(e.toString())
	                .build();
		}
		

	}

	
	//forward request
	@PostMapping(value = Routes.URI_REST_REQUEST_EVALUATION_FORWARD)
	public GenericResponseDTO<RequestEvaluationDto> forwardRequest(@PathVariable(name = "id")String requestId,
			@RequestBody List<String> listCompetencyLeadId){
		RequestEvaluationDto requestEvaluationDto = requestEvaluationService.forwardRequest(requestId, listCompetencyLeadId);
		return  GenericResponseDTO.<RequestEvaluationDto>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(requestEvaluationDto)
                .build();
	}
	
	@PostMapping(value = Routes.URI_REST_REQUEST_EVALUATION_DETAIL_UPDATE)
	public GenericResponseDTO<RequestEvaluationDetailDto> updateRequestDetail(@PathVariable(name = "detail_id")String requestDetailId, 
																		@RequestBody RequestEvaluationDetailDto detailDto) {
		return GenericResponseDTO.<RequestEvaluationDetailDto>builder()
				.code(MessageCode.SUCCESS.toString())
				.data(requestEvaluationService.updateRequestDetail(requestDetailId, detailDto))
				.build();
	}
	
	@GetMapping(value = Routes.URI_REST_REQUEST_EVALUATION_PENDING)
	public GenericResponseDTO<RequestEvaluationPendingDto> notificationRequestPending(@PathVariable String personalId){
		return GenericResponseDTO.<RequestEvaluationPendingDto>builder()
				.code(MessageCode.SUCCESS.toString())
				.data(requestEvaluationService.notificationRequestPending(personalId)).build();
	}

	@GetMapping(value = Routes.URI_REST_HISTORICAL_LEVEL)
	public GenericResponseDTO<Map<String, Object>> getHistoricalLevel(@PathVariable String personalId,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size,
			@RequestParam(required = false) Map<String, String> query) {
		return GenericResponseDTO.<Map<String, Object>>builder().code(MessageCode.SUCCESS.toString())
				.data(requestEvaluationService.findHistorical(personalId, page, size, query)).build();
	}
}
