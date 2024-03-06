package com.bosch.eet.skill.management.service;

import java.util.List;
import java.util.Map;

import com.bosch.eet.skill.management.dto.RequestEvaluationDetailDto;
import com.bosch.eet.skill.management.dto.RequestEvaluationDto;
import com.bosch.eet.skill.management.dto.RequestEvaluationPendingDto;
import com.bosch.eet.skill.management.entity.RequestEvaluation;

import lombok.NonNull;

public interface RequestEvaluationService {
	
	List<RequestEvaluationDto> findAll(Map<String,String> q);
	
	List<RequestEvaluationDto> findRequestEvaluated(String nitd);

	RequestEvaluationDto findByID(String evaluationID);

	RequestEvaluationDto createRequest(RequestEvaluationDto request, String authName);

	RequestEvaluationDto forwardRequest(String requestId, List<String> competencyListIds);

	RequestEvaluationDto newFindByID(String evaluationID, Map<String, String> filterParams);

	RequestEvaluationDetailDto updateRequestDetail(String requestDetailId, RequestEvaluationDetailDto requestDetailDto);

	void updateRequest(RequestEvaluationDto requestDto, @NonNull String authName);

	void updateRequestStatus(RequestEvaluation request);
	
	RequestEvaluationPendingDto notificationRequestPending(String personalId);
	
	Map<String, Object> findHistorical(String requesterId, Integer page, Integer size, Map<String, String> query);
}
