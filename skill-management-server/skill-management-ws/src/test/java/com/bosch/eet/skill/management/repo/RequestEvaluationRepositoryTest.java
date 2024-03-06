package com.bosch.eet.skill.management.repo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.dto.RequestEvaluationPendingDto;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.RequestEvaluation;
import com.bosch.eet.skill.management.entity.RequestEvaluationDetail;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.specification.RequestEvaluationDetailSpecification;

@DataJpaTest
@ActiveProfiles("dev")
@Transactional
public class RequestEvaluationRepositoryTest {
	
	@Autowired
	private RequestEvaluationDetailRepository requestEvaluationDetailRepository;
	
	@Autowired
	private SkillRepository skillRepository;
	
	@Autowired
	private PersonalRepository personalRepository;
	
	@Autowired
	private RequestEvaluationRepository requestEvaluationRepository;
	
	private Skill skill;
	
	private Skill skill2;
	
	private Skill skill3;
	
	private Personal personal;
	
	private RequestEvaluation re1;
	
	private RequestEvaluation re2;
	
	private RequestEvaluation re3;

	private RequestEvaluationDetail red1;
	
	private RequestEvaluationDetail red2;
	
	private RequestEvaluationDetail red3;
	
	private Date date1;
	
	private Date date2;
	
	@BeforeEach
	void prepare() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		date1 = format.parse("2023/02/02");
		date2 = format.parse("2022/02/02");
		
		Pageable limit = PageRequest.of(0, 1);// limit
		List<Skill> skills = skillRepository.findAll();
		skill = skills.get(0);
		skill2 = skills.get(1);
		skill3 = skills.get(2);
		personal = personalRepository.findAll(limit).getContent().get(0);// requester and approver

		re1 = RequestEvaluation.builder().requester(personal).approver(personal).status(Constants.WAITING_FOR_APPROVAL)
				.build();
		re2 = RequestEvaluation.builder().requester(personal).approver(personal).status(Constants.WAITING_FOR_APPROVAL)
				.build();
		re3 = RequestEvaluation.builder().requester(personal).approver(personal).status(Constants.APPROVED)
				.build();
		red1 = RequestEvaluationDetail.builder().skill(skill).status(Constants.WAITING_FOR_APPROVAL)
				.approver(personal).currentExp(1).currentLevel(1).createdDate(date1).build();
		red2 = RequestEvaluationDetail.builder().skill(skill).status(Constants.WAITING_FOR_APPROVAL)
				.approver(personal).currentExp(4).currentLevel(4).createdDate(date2).build();
		red3 = RequestEvaluationDetail.builder().skill(skill).status(Constants.APPROVED)
				.approver(personal).currentExp(4).currentLevel(4).createdDate(date2).build();
		List<RequestEvaluation> requestEvaluations = new ArrayList<>(Arrays.asList(re1, re2, re3));
		List<RequestEvaluationDetail> requestEvaluationDetails = new ArrayList<>(Arrays.asList(red1, red2, red3));

		requestEvaluationRepository.saveAllAndFlush(requestEvaluations);
		requestEvaluationDetailRepository.saveAllAndFlush(requestEvaluationDetails);

		re1.setRequestEvaluationDetails(Collections.singletonList(red1));
		red1.setRequestEvaluation(re1);
		re2.setRequestEvaluationDetails(Collections.singletonList(red2));
		red2.setRequestEvaluation(re2);
	}

	@Test
	void testFindRequestEvaluatedDetailByRequesterAndSkillAndCreatedDate() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date1 = format.parse("2023/02/02");
		Date date2 = format.parse("2022/02/02");
		
		Pageable limit = PageRequest.of(0, 1);// limit
		// prepare data
		Skill skill = skillRepository.findAll(limit).getContent().get(0);// skill request
		Personal personal = personalRepository.findAll(limit).getContent().get(0);// requester and approver

		RequestEvaluation requestEvaluation = RequestEvaluation.builder().requester(personal).approver(personal).status(Constants.APPROVED)
				.build();
		RequestEvaluationDetail requestEvaluationDetail = RequestEvaluationDetail.builder().skill(skill).status(Constants.APPROVED)
				.approver(personal).currentExp(1).currentLevel(1).createdDate(date1).build();

		requestEvaluation.setRequestEvaluationDetails(Collections.singletonList(requestEvaluationDetail));
		requestEvaluationDetail.setRequestEvaluation(requestEvaluation);
		
		RequestEvaluation requestEvaluation2 = RequestEvaluation.builder().requester(personal).approver(personal).status(Constants.APPROVED)
				.build();
		RequestEvaluationDetail requestEvaluationDetail2 = RequestEvaluationDetail.builder().skill(skill).status(Constants.APPROVED)
				.approver(personal).currentExp(4).currentLevel(4).createdDate(date2).build();

		requestEvaluation2.setRequestEvaluationDetails(Collections.singletonList(requestEvaluationDetail2));
		requestEvaluationDetail2.setRequestEvaluation(requestEvaluation2);
		
		requestEvaluationRepository.saveAndFlush(requestEvaluation);
		
		requestEvaluationDetailRepository.saveAndFlush(requestEvaluationDetail);
		
		List<RequestEvaluationDetail> requestEvaluationDetails1 = requestEvaluationDetailRepository
				.findRequestEvaluatedDetailByRequesterAndSkillAndCreatedDate(personal, skill,
						requestEvaluationDetail.getCreatedDate(), limit);
		assertThat(requestEvaluationDetails1).isEmpty();
		requestEvaluationRepository.saveAndFlush(requestEvaluation2);
		requestEvaluationDetailRepository.saveAndFlush(requestEvaluationDetail2);
		
		List<RequestEvaluationDetail> requestEvaluationDetails2 = requestEvaluationDetailRepository
				.findRequestEvaluatedDetailByRequesterAndSkillAndCreatedDate(personal, skill,
						requestEvaluationDetail.getCreatedDate(), limit);
		//test
		assertThat(requestEvaluationDetail2).isEqualTo(requestEvaluationDetails2.get(0));
	}
	
	@Test
	void testFindRequestEvaluatedDetailByRequesterAndSkillAndCreatedDate_statusPending() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date1 = format.parse("2023/02/02");
		Date date2 = format.parse("2022/02/02");
		
		Pageable limit = PageRequest.of(0, 1);// limit
		// prepare data
		Skill skill = skillRepository.findAll(limit).getContent().get(0);// skill request
		Personal personal = personalRepository.findAll(limit).getContent().get(0);// requester and approver

		RequestEvaluation requestEvaluation = RequestEvaluation.builder().requester(personal).approver(personal).status(Constants.WAITING_FOR_APPROVAL)
				.build();
		RequestEvaluationDetail requestEvaluationDetail = RequestEvaluationDetail.builder().skill(skill).status(Constants.WAITING_FOR_APPROVAL)
				.approver(personal).currentExp(1).currentLevel(1).createdDate(date1).build();

		requestEvaluation.setRequestEvaluationDetails(Collections.singletonList(requestEvaluationDetail));
		requestEvaluationDetail.setRequestEvaluation(requestEvaluation);
		
		RequestEvaluation requestEvaluation2 = RequestEvaluation.builder().requester(personal).approver(personal).status(Constants.WAITING_FOR_APPROVAL)
				.build();
		RequestEvaluationDetail requestEvaluationDetail2 = RequestEvaluationDetail.builder().skill(skill).status(Constants.WAITING_FOR_APPROVAL)
				.approver(personal).currentExp(4).currentLevel(4).createdDate(date2).build();

		requestEvaluation2.setRequestEvaluationDetails(Collections.singletonList(requestEvaluationDetail2));
		requestEvaluationDetail2.setRequestEvaluation(requestEvaluation2);
		
		requestEvaluationRepository.saveAndFlush(requestEvaluation);
		requestEvaluationDetailRepository.saveAndFlush(requestEvaluationDetail);
		requestEvaluationRepository.saveAndFlush(requestEvaluation2);
		requestEvaluationDetailRepository.saveAndFlush(requestEvaluationDetail2);
		
		List<RequestEvaluationDetail> requestEvaluationDetails = requestEvaluationDetailRepository
				.findRequestEvaluatedDetailByRequesterAndSkillAndCreatedDate(personal, skill,
						requestEvaluationDetail.getCreatedDate(), limit);
		assertThat(requestEvaluationDetails).isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testFindAllHistoricalChangeLevelWithSpecification() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date1 = format.parse("2023/02/02");
		Date date2 = format.parse("2022/02/02");
		
		Pageable limit = PageRequest.of(0, 1);// limit
		// prepare data
		Skill skill = skillRepository.findAll(limit).getContent().get(0);// skill request
		Personal personal = personalRepository.findAll(limit).getContent().get(0);// requester and approver
		RequestEvaluation requestEvaluation = RequestEvaluation.builder().requester(personal).approver(personal).status(Constants.APPROVED)
				.build();
		RequestEvaluationDetail requestEvaluationDetail = RequestEvaluationDetail.builder().skill(skill).status(Constants.APPROVED)
				.approver(personal).currentExp(1).currentLevel(1).createdDate(date1).build();
		requestEvaluation.setRequestEvaluationDetails(Collections.singletonList(requestEvaluationDetail));
		requestEvaluationDetail.setRequestEvaluation(requestEvaluation);
		RequestEvaluation requestEvaluation2 = RequestEvaluation.builder().requester(personal).approver(personal).status(Constants.APPROVED)
				.build();
		RequestEvaluationDetail requestEvaluationDetail2 = RequestEvaluationDetail.builder().skill(skill).status(Constants.APPROVED)
				.approver(personal).currentExp(4).currentLevel(4).createdDate(date2).build();
		requestEvaluation2.setRequestEvaluationDetails(Collections.singletonList(requestEvaluationDetail2));
		requestEvaluationDetail2.setRequestEvaluation(requestEvaluation2);
		
		requestEvaluationRepository.saveAndFlush(requestEvaluation);
		requestEvaluationRepository.saveAndFlush(requestEvaluation2);
		requestEvaluationDetailRepository.saveAndFlush(requestEvaluationDetail);
		requestEvaluationDetailRepository.saveAndFlush(requestEvaluationDetail2);

		Map<String, String> query = new HashMap<>();
		query.put("skillCluster", skill.getSkillGroup().getName());
		Pageable paging = PageRequest.of(0,2);
		Specification<RequestEvaluationDetail> specification = RequestEvaluationDetailSpecification.search(personal.getId(),
				query);
		Page<RequestEvaluationDetail> evaluationDetailPages = requestEvaluationDetailRepository.findAll(specification,
				paging);
		assertThat(evaluationDetailPages.hasContent()).isTrue();
		assertThat(evaluationDetailPages.getContent()).hasSizeGreaterThanOrEqualTo(2);
		
		query.put("skillCluster", "fakedate");
		evaluationDetailPages = requestEvaluationDetailRepository.findAll(specification,
				paging);
		assertThat(evaluationDetailPages.getContent()).hasSizeLessThanOrEqualTo(2);
	}
	
	@Test
	@DisplayName("Find all by approver id and status")
	void findAllByApproverIdAndStatus() {
		String ntid = personal.getPersonalCode();
		List<RequestEvaluation> evaluations = requestEvaluationRepository
				.findAllByApproverIdAndStatus(ntid, Constants.WAITING_FOR_APPROVAL);
		assertThat(evaluations).hasSizeGreaterThanOrEqualTo(2);
		assertThat(evaluations).contains(re1,re2);	
		assertThat(evaluations).doesNotContain(re3);	
		boolean isContainNtidOther = evaluations.stream()
				.anyMatch(item -> !item.getRequester().getPersonalCode().equals(ntid));
		assertThat(isContainNtidOther).isFalse();
	}
	
	@Test
	@DisplayName("Count request pending by personal id and status")
	void countRequestPendingByPersonalIdAndStatus() {
		String personalId = personal.getId();
		Long numOfRequest = requestEvaluationRepository.countRequestPendingByPersonalIdAndStatus(personalId,
				Constants.PENDING);
		assertThat(numOfRequest).isEqualTo(0);
		Personal approverForwarded = personalRepository.findAll().get(1);
		RequestEvaluation requestEvaluation = RequestEvaluation.builder().requester(personal).approver(personal)
				.status(Constants.WAITING_FOR_APPROVAL).build();
		RequestEvaluationDetail requestEvaluationDetail = RequestEvaluationDetail.builder().skill(skill)
				.status(Constants.WAITING_FOR_APPROVAL).approver(approverForwarded).currentExp(1).currentLevel(1)
				.createdDate(date1).build();
		requestEvaluationRepository.saveAndFlush(requestEvaluation);
		requestEvaluationDetailRepository.saveAndFlush(requestEvaluationDetail);
		requestEvaluation.setRequestEvaluationDetails(Collections.singletonList(requestEvaluationDetail));
		requestEvaluationDetail.setRequestEvaluation(requestEvaluation);
		numOfRequest = requestEvaluationRepository.countRequestPendingByPersonalIdAndStatus(personalId,
				Constants.PENDING);
		assertThat(numOfRequest).isEqualTo(1);
	}
	
	@Test
	@DisplayName("Find request pending by personal id and status")
	void findRequestPendingByPersonalIdAndStatus() {
		RequestEvaluationPendingDto evaluationPendingDto = RequestEvaluationPendingDto.builder()
				.displayName(personal.getUser().getDisplayName()).mail(personal.getUser().getEmail()).count(2l)
				.build();
		RequestEvaluationPendingDto dtoQuery = requestEvaluationRepository
				.findRequestPendingByPersonalIdAndStatus(personal.getId(), Constants.WAITING_FOR_APPROVAL);
		assertThat(evaluationPendingDto).isEqualTo(dtoQuery);
		dtoQuery = requestEvaluationRepository
				.findRequestPendingByPersonalIdAndStatus("ntidnotexist", Constants.WAITING_FOR_APPROVAL);
		assertNotEquals(evaluationPendingDto, dtoQuery);
	}
}
