package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.dto.RequestEvaluationDto;
import com.bosch.eet.skill.management.dto.RequestEvaluationHistoricalDto;
import com.bosch.eet.skill.management.dto.RequestEvaluationPendingDto;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.RequestEvaluation;
import com.bosch.eet.skill.management.entity.RequestEvaluationDetail;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.RequestEvaluationDetailRepository;
import com.bosch.eet.skill.management.repo.RequestEvaluationRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;

@SpringBootTest
@ActiveProfiles("dev")
public class RequestEvaluationServiceTestWithoutMock {

	@Autowired
	private RequestEvaluationService requestEvaluationService;

	@Autowired
	private SkillRepository skillRepository;

	@Autowired
	private PersonalRepository personalRepository;

	@Autowired
	private RequestEvaluationRepository requestEvaluationRepository;

	@Autowired
	private RequestEvaluationDetailRepository requestEvaluationDetailRepository;
	
	private Personal personal;
	
	private Personal approverForward;
	
	private RequestEvaluation requestEvaluation;
	
	private RequestEvaluation requestEvaluation2;
	
	private RequestEvaluationDetail requestEvaluationDetail;
	
	private RequestEvaluationDetail requestEvaluationDetail2;
	
	private RequestEvaluation requestEvaluation3;
	
	private RequestEvaluation requestEvaluation4;
	
	private RequestEvaluationDetail requestEvaluationDetail3;
	
	private RequestEvaluationDetail requestEvaluationDetail4;
	
	private RequestEvaluation re1;

	private RequestEvaluation re2;

	private RequestEvaluation re3;

	private RequestEvaluationDetail red1;

	private RequestEvaluationDetail red2;

	private RequestEvaluationDetail red3;
	
	private Skill skill1;
	
	private Skill skill2;
	
	@BeforeEach
	void prepare() throws ParseException {
		String dateStr1 = "2023/02/02";
		String dateStr2 = "2022/02/02";
		String dateStr3 = "2021/02/02";
		String dateStr4 = "2022/02/02";
		Pageable limit = PageRequest.of(0, 2);// limit
		List<Skill> skills = skillRepository.findAll(limit).getContent();
		List<Personal> personals = personalRepository.findAll(limit).getContent();
		skill1 = skills.get(0);// skill request 1
		skill2 = skills.get(1);// skill request 2
		personal = personals.get(0);// requester and approver
		approverForward = personals.get(1);// requester and approver

		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date1 = format.parse(dateStr1);
		Date date2 = format.parse(dateStr2);
		Date date3 = format.parse(dateStr3);
		Date date4 = format.parse(dateStr4);

		requestEvaluation = RequestEvaluation.builder().requester(personal).approver(personal).status("APPROVED")
				.build();
		requestEvaluationDetail = RequestEvaluationDetail.builder().skill(skill1).status("APPROVED").approver(personal)
				.currentExp(1).currentLevel(1).createdDate(date1).build();
		requestEvaluation.setRequestEvaluationDetails(Collections.singletonList(requestEvaluationDetail));
		requestEvaluationDetail.setRequestEvaluation(requestEvaluation);
		requestEvaluation2 = RequestEvaluation.builder().requester(personal).approver(personal).status("APPROVED")
				.build();
		requestEvaluationDetail2 = RequestEvaluationDetail.builder().skill(skill1).status("APPROVED").approver(personal)
				.currentExp(4).currentLevel(4).createdDate(date2).build();
		requestEvaluation2.setRequestEvaluationDetails(Collections.singletonList(requestEvaluationDetail2));
		requestEvaluationDetail2.setRequestEvaluation(requestEvaluation2);

		requestEvaluation3 = RequestEvaluation.builder().requester(personal).approver(personal).status("APPROVED")
				.build();
		requestEvaluationDetail3 = RequestEvaluationDetail.builder().skill(skill2).status("APPROVED").approver(personal)
				.currentExp(1).currentLevel(1).createdDate(date3).build();
		requestEvaluation3.setRequestEvaluationDetails(Collections.singletonList(requestEvaluationDetail3));
		requestEvaluationDetail3.setRequestEvaluation(requestEvaluation3);
		requestEvaluation4 = RequestEvaluation.builder().requester(personal).approver(personal).status("APPROVED")
				.build();
		requestEvaluationDetail4 = RequestEvaluationDetail.builder().skill(skill2).status("APPROVED").approver(personal)
				.currentExp(4).currentLevel(4).createdDate(date4).build();
		requestEvaluation4.setRequestEvaluationDetails(Collections.singletonList(requestEvaluationDetail4));
		requestEvaluationDetail4.setRequestEvaluation(requestEvaluation4);

		requestEvaluationRepository.saveAllAndFlush(
				Arrays.asList(requestEvaluation, requestEvaluation2, requestEvaluation3, requestEvaluation4));
		requestEvaluationDetailRepository.saveAllAndFlush(Arrays.asList(requestEvaluationDetail,
				requestEvaluationDetail2, requestEvaluationDetail3, requestEvaluationDetail4));
		
		re1 = RequestEvaluation.builder().requester(personal).approver(personal).status(Constants.WAITING_FOR_APPROVAL)
				.build();
		re2 = RequestEvaluation.builder().requester(personal).approver(personal).status(Constants.WAITING_FOR_APPROVAL)
				.build();
		re3 = RequestEvaluation.builder().requester(personal).approver(personal).status(Constants.APPROVED)
				.build();
		red1 = RequestEvaluationDetail.builder().skill(skill1).status(Constants.WAITING_FOR_APPROVAL)
				.approver(personal).currentExp(1).currentLevel(1).createdDate(date1).build();
		red2 = RequestEvaluationDetail.builder().skill(skill1).status(Constants.WAITING_FOR_APPROVAL)
				.approver(personal).currentExp(4).currentLevel(4).createdDate(date2).build();
		red3 = RequestEvaluationDetail.builder().skill(skill1).status(Constants.APPROVED)
				.approver(personal).currentExp(4).currentLevel(4).createdDate(date2).build();
		List<RequestEvaluation> requestEvaluations = new ArrayList<>(Arrays.asList(re1, re2, re3));
		List<RequestEvaluationDetail> requestEvaluationDetails = new ArrayList<>(Arrays.asList(red1, red2, red3));

		re1.setRequestEvaluationDetails(Collections.singletonList(red1));
		red1.setRequestEvaluation(re1);
		re2.setRequestEvaluationDetails(Collections.singletonList(red2));
		red2.setRequestEvaluation(re2);
		re3.setRequestEvaluationDetails(Collections.singletonList(red3));
		red3.setRequestEvaluation(re3);
		
		requestEvaluationRepository.saveAllAndFlush(requestEvaluations);
		requestEvaluationDetailRepository.saveAllAndFlush(requestEvaluationDetails);
	}

	@Test
	@Transactional
	@DisplayName("Find historical change level happy case")
	void findHistorycalChangeLevel() {
		Map<String, String> query = new HashMap<>();
		Map<String, Object> result = requestEvaluationService.findHistorical(personal.getId(), 0, Integer.MAX_VALUE,
				query);
		List<RequestEvaluationHistoricalDto> evaluationHistoricalDtos = (List<RequestEvaluationHistoricalDto>) result
				.get(Constants.HISTORICAL_CHANGE_LEVEL);
		Integer totalPage = (Integer) result.get(Constants.TOTAL_PAGE);
		Long totalItem = (Long) result.get(Constants.TOTAL_ITEM);
		assertThat(evaluationHistoricalDtos).hasSizeGreaterThanOrEqualTo(4);
		assertThat(totalPage).isGreaterThanOrEqualTo(1);
		assertThat(totalItem).isGreaterThanOrEqualTo(4);
		assertThat(evaluationHistoricalDtos.stream()
				.anyMatch(item -> item.getSkillName().equals(requestEvaluationDetail.getSkill().getName()))).isTrue();
		assertThat(evaluationHistoricalDtos.stream()
				.anyMatch(item -> item.getSkillName().equals(requestEvaluationDetail2.getSkill().getName()))).isTrue();
		assertThat(evaluationHistoricalDtos.stream().anyMatch(item -> item.getSkillName().equals("Skill name fake"))).isFalse();
		
		Map<String, List<RequestEvaluationHistoricalDto>> map = evaluationHistoricalDtos.stream()
				.collect(Collectors.groupingBy(s -> s.getSkillName()));
		for (String skillName : map.keySet()) {
			List<RequestEvaluationHistoricalDto> rehds = map.get(skillName);
			for (int i = 0; i < rehds.size() - 1; i++) {
				assertSame(rehds.get(i).getOldLevel(), rehds.get(i + 1).getCurrentLevel());// old level and old
																							// experience is current
																							// level and current
																							// experience
			}
			assertThat(rehds.get(rehds.size() - 1).getOldLevel()).isEqualTo(0);// historical change level oldest have old level
																		// and old experience is 0
			assertThat(rehds.get(rehds.size() - 1).getOldExp()).isEqualTo(0);// historical change level oldest have old level
																		// and old experience is 0
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	@Transactional
	@DisplayName("Find historical change level - filter by skill cluster")
	void findHistorycalChangeLevel_filterBySkillCluster() {
		Map<String, String> query = new HashMap<>();
		query.put("skillCluster", "Skill cluster not exists");
		Map<String, Object> result = requestEvaluationService.findHistorical(personal.getId(), 0, 2, query);

		List<RequestEvaluationHistoricalDto> evaluationHistoricalDtos = (List<RequestEvaluationHistoricalDto>) result
				.get(Constants.HISTORICAL_CHANGE_LEVEL);
		Integer totalPage = (Integer) result.get(Constants.TOTAL_PAGE);
		Long totalItem = (Long) result.get(Constants.TOTAL_ITEM);
		assertThat(evaluationHistoricalDtos).isEmpty();
		assertThat(totalPage).isEqualTo(0);
		assertThat(totalItem).isEqualTo(0);

		query.put("skillCluster", skill1.getSkillGroup().getName());
		result = requestEvaluationService.findHistorical(personal.getId(), 0, Integer.MAX_VALUE, query);
		evaluationHistoricalDtos = (List<RequestEvaluationHistoricalDto>) result.get(Constants.HISTORICAL_CHANGE_LEVEL);
		totalPage = (Integer) result.get(Constants.TOTAL_PAGE);
		totalItem = (Long) result.get(Constants.TOTAL_ITEM);
		assertThat(evaluationHistoricalDtos).hasSizeGreaterThanOrEqualTo(2);
		assertThat(totalPage).isEqualTo(1);
		assertThat(totalItem).isGreaterThanOrEqualTo(2);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@Transactional
	@DisplayName("Find historical change level - break list but still have old record")
	void findHistorycalChangeLevel_BreakListBusStillHaveOldRecord() {
		Map<String, String> query = new HashMap<>();
		Map<String, Object> result = requestEvaluationService.findHistorical(personal.getId(), 0, 1,
				query);
		List<RequestEvaluationHistoricalDto> evaluationHistoricalDtos = (List<RequestEvaluationHistoricalDto>) result
				.get(Constants.HISTORICAL_CHANGE_LEVEL);
		Integer totalPage = (Integer) result.get(Constants.TOTAL_PAGE);
		Long totalItem = (Long) result.get(Constants.TOTAL_ITEM);
		assertThat(evaluationHistoricalDtos).hasSize(1);
		assertThat(totalPage).isEqualTo(5);
		assertThat(totalItem).isEqualTo(5);
		RequestEvaluationHistoricalDto requestEvaluationHistoricalDto = evaluationHistoricalDtos.get(0);
		assertThat(requestEvaluationHistoricalDto.getCurrentLevel()).isEqualTo(1);
		assertThat(requestEvaluationHistoricalDto.getCurrentExp()).isEqualTo(1);
		assertThat(requestEvaluationHistoricalDto.getOldExp()).isEqualTo(4);
		assertThat(requestEvaluationHistoricalDto.getOldLevel()).isEqualTo(4);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@Transactional
	@DisplayName("Find historical change level - page and size have value negative -1")
	void findHistorycalChangeLevel_PageAndSizeHaveValuesIsNegative1() {
		Map<String, String> query = new HashMap<>();
		Map<String, Object> result = requestEvaluationService.findHistorical(personal.getId(), -1, -1,
				query);
		List<RequestEvaluationHistoricalDto> evaluationHistoricalDtos = (List<RequestEvaluationHistoricalDto>) result
				.get(Constants.HISTORICAL_CHANGE_LEVEL);
		Integer totalPage = (Integer) result.get(Constants.TOTAL_PAGE);
		Long totalItem = (Long) result.get(Constants.TOTAL_ITEM);
		assertThat(evaluationHistoricalDtos).hasSize(1);
		assertThat(totalPage).isEqualTo(5);
		assertThat(totalItem).isEqualTo(5);
		RequestEvaluationHistoricalDto requestEvaluationHistoricalDto = evaluationHistoricalDtos.get(0);
		assertThat(requestEvaluationHistoricalDto.getCurrentLevel()).isEqualTo(1);
		assertThat(requestEvaluationHistoricalDto.getCurrentExp()).isEqualTo(1);
		assertThat(requestEvaluationHistoricalDto.getOldExp()).isEqualTo(4);
		assertThat(requestEvaluationHistoricalDto.getOldLevel()).isEqualTo(4);
	}
	
	@Test
	@Transactional
	@DisplayName("Find all request for approver - all request forwarded")
	void findAllForApprover_AllRequestForwarded() throws ParseException {
		Pageable limit = PageRequest.of(0, 2);
		Personal personalFoward = personalRepository.findAll(limit).getContent().get(1);// The approver was be forwarded
		red1.setApprover(personalFoward);
		red2.setApprover(personalFoward);
		requestEvaluationDetailRepository.saveAllAndFlush(Arrays.asList(red1, red2));
		Map<String, String> map = Collections.singletonMap(Constants.APPROVER_ID, personal.getPersonalCode());
		List<RequestEvaluationDto> dtos = requestEvaluationService.findAll(map);
		assertThat(dtos).hasSizeGreaterThanOrEqualTo(2);
	}
	
	@Test
	@Transactional
	@DisplayName("Find all request for approver - request have forwarded and requested")
	void findAllForApprover_BothFowardedAndRequested() throws ParseException {
		Pageable limit = PageRequest.of(0, 2);
		Personal personalFoward = personalRepository.findAll(limit).getContent().get(1);// The approver was be forwarded
		red1.setApprover(personalFoward);//forward request
		requestEvaluationDetailRepository.saveAndFlush(red1);
		Map<String, String> map = Collections.singletonMap(Constants.APPROVER_ID, personal.getPersonalCode());
		List<RequestEvaluationDto> dtos = requestEvaluationService.findAll(map);
		assertThat(dtos).hasSizeGreaterThanOrEqualTo(2);
		List<String> requestIds = dtos.stream().map(RequestEvaluationDto::getId).collect(Collectors.toList());
		assertThat(requestIds).contains(re1.getId(),re2.getId());
		assertThat(requestIds).doesNotContain(re3.getId());
	}
	
	@Test
	@Transactional
	@DisplayName("Notification request pending")
	void notificationRequestPending() {
		String personalId = personal.getId();
		red1.setApprover(approverForward);// forward request
		requestEvaluationDetailRepository.saveAndFlush(red1);
		RequestEvaluationPendingDto dto = requestEvaluationService.notificationRequestPending(personalId);
		//Test approver have request was forwarded to another approver and have request not forwarded
		assertThat(personal.getUser().getDisplayName()).isEqualTo(dto.getDisplayName());
		assertThat(personal.getUser().getEmail()).isEqualTo( dto.getMail());
		assertThat(dto.getCount()).isGreaterThanOrEqualTo(2);
		String personalApproverForwardedId = approverForward.getId();
		////Test approver had forwarded from another approver
		RequestEvaluationPendingDto dto2 = requestEvaluationService
				.notificationRequestPending(personalApproverForwardedId);
		assertThat(approverForward.getUser().getDisplayName()).isEqualTo(dto2.getDisplayName());
		assertThat(approverForward.getUser().getEmail()).isEqualTo(dto2.getMail());
		assertThat(dto2.getCount()).isGreaterThanOrEqualTo(2);
	}
}
