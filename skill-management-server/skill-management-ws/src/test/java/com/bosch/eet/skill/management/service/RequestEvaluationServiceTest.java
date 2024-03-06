package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.constant.EmailConstant;
import com.bosch.eet.skill.management.converter.RequestEvaluationConverter;
import com.bosch.eet.skill.management.converter.utils.RequestEvaluationDetailConverterUtil;
import com.bosch.eet.skill.management.dto.RequestEvaluationDetailDto;
import com.bosch.eet.skill.management.dto.RequestEvaluationDto;
import com.bosch.eet.skill.management.dto.RequestEvaluationPendingDto;
import com.bosch.eet.skill.management.dto.SkillDescriptionDTO;
import com.bosch.eet.skill.management.elasticsearch.repo.PersonalElasticRepository;
import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.RequestEvaluation;
import com.bosch.eet.skill.management.entity.RequestEvaluationDetail;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.util.Utility;
import com.bosch.eet.skill.management.mail.EmailServiceImpl;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.RequestEvaluationDetailRepository;
import com.bosch.eet.skill.management.repo.RequestEvaluationRepository;
import com.bosch.eet.skill.management.service.impl.RequestEvaluationServiceImpl;
import com.bosch.eet.skill.management.specification.RequestEvaluationSpecification;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class RequestEvaluationServiceTest {

    @Spy
	@InjectMocks
	private RequestEvaluationServiceImpl requestEvaluationServiceImpl;
    
    @Mock
	private RequestEvaluationService requestEvaluationService;

    @Mock
	private EmailServiceImpl emailServiceImpl;

    @Mock
	private RequestEvaluationRepository requestEvaluationRepository;

    @Mock
	private RequestEvaluationDetailRepository requestEvaluationDetailRepository;

    @Mock
	private UserRepository userRepository;

    @Mock
	private PersonalElasticRepository personalElasticRepository;

	@Mock
	private PersonalRepository personalRepository;

	@Mock
	private RequestEvaluationConverter requestEvaluationConverter;

	@Mock
	private RequestEvaluationDetailConverterUtil requestEvaluationDetailConverter;

	@Mock
	private MessageSource messageSource;

	private final int PAGE = 0;
	private final int SIZE = 5;
	private final Pageable pageable = PageRequest.of(PAGE, SIZE);

	private final String jsonResponse = "{\"id\":\"1\",\"requester\":\"requester\",\"approver\":\"approver\",\"status\":\"APPROVAL_PENDING\",\"request_evaluation_details\":[{\"id\":\"1\",\"current_level\":\"2\",\"experience\":2,\"comment\":\"This is comment\",\"status\":\"APPROVAL_PENDING\"}],\"comment\":\"This is comment\",\"created_date\":\"2022-12-25\",\"updated_date\":\"2022-12-26\"}";

    @Test
    @DisplayName("Find all request evaluation happy case")
    void findAllRequestEvaluation() {
        Map<String, String> map = new HashMap<>();
        map.put("requester_id", "requester");
        Specification<RequestEvaluation> specification = RequestEvaluationSpecification.search(map);

        log.info(specification.toString());
        String createdDate = "2022-12-25";
        String updatedDate = "2022-12-26";

        // Dummy request evaluation entity list
        List<RequestEvaluation> requestEvaluations = new ArrayList<>();

        // Dummy requester
        Personal requester = Personal.builder()
                .id("requester")
                .personalNumber("personalNumber")
                .personalCode("personalCode")
                .level(Level.builder().build())
                .title("Title")
                .build();

        // Dummy approver
        Personal approver = Personal.builder()
                .id("approver")
                .personalNumber("12345678")
                .personalCode("ABC1HC")
                .level(Level.builder().build())
                .title("Title")
                .build();

        // Dummy request evaluation entity
        RequestEvaluation requestEvaluation = RequestEvaluation.builder()
                .id("id")
                .approver(approver)
                .requester(requester)
                .comment("This is comment")
                .status("APPROVAL_PENDING")
                .build();

        try {
            requestEvaluation.setCreatedDate(Constants.SIMPLE_DATE_FORMAT
                .parse(createdDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_CREATED_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_CREATED_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            requestEvaluation.setUpdatedDate(Constants.SIMPLE_DATE_FORMAT
                .parse(updatedDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_UPDATED_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_UPDATED_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Dummy skill
        Skill skill = Skill.builder()
                .id("1")
                .name("Test skill")
                .description("This is description")
                .sequence(2)
                .status("Active")
                .build();

        // Dummy request evaluation detail
        RequestEvaluationDetail requestEvaluationDetail = RequestEvaluationDetail.builder()
                .id("1")
                .skill(skill)
                .approver(approver)
                .currentLevel(2)
                .currentExp(2)
                .comment("This is comment")
                .status("APPROVAL_PENDING")
                .requestEvaluation(requestEvaluation)
                .build();

        try {
            requestEvaluationDetail.setCreatedDate(Constants.SIMPLE_DATE_FORMAT
                .parse(createdDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_CREATED_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_CREATED_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            requestEvaluationDetail.setUpdatedDate(Constants.SIMPLE_DATE_FORMAT
                .parse(updatedDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_UPDATED_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_UPDATED_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<RequestEvaluationDetail> requestEvaluationDetails = new ArrayList<>();
        requestEvaluationDetails.add(requestEvaluationDetail);

        requestEvaluation.setRequestEvaluationDetails(requestEvaluationDetails);

        requestEvaluations.add(requestEvaluation);
        Page<RequestEvaluation> pageRequestEvaluations = new PageImpl<>(requestEvaluations);
        log.info(pageRequestEvaluations.getContent().toString());
        RequestEvaluationDto requestEvaluationDto = RequestEvaluationDto.builder()
                .id(requestEvaluation.getId())
                .approver(requestEvaluation.getApprover().toString())
                .requester(requestEvaluation.getRequester().toString())
                .comment(requestEvaluation.getComment())
                .status(requestEvaluation.getStatus())
                .createdDate(createdDate)
                .updateDate(updatedDate)
                .build();

        SkillDescriptionDTO skillDescriptionDTO = SkillDescriptionDTO.builder()
                .id(requestEvaluationDetail.getId())
                .skillId(requestEvaluationDetail.getSkill().getId())
                .skillName(requestEvaluationDetail.getSkill().getName())
                .level(Utility.floatToStringLevelFormat(requestEvaluationDetail.getCurrentLevel()))
                .comment(requestEvaluationDetail.getComment())
                .experience(requestEvaluationDetail.getCurrentExp())
                .status(requestEvaluationDetail.getStatus())
                .build();

        List<SkillDescriptionDTO> skillDescriptionDTOS = new ArrayList<>();
        skillDescriptionDTOS.add(skillDescriptionDTO);

        requestEvaluationDto.setRequestEvaluationDetails(skillDescriptionDTOS);

        List<RequestEvaluationDto> requestEvaluationDtos = new ArrayList<>();
        requestEvaluationDtos.add(requestEvaluationDto);

        when(requestEvaluationRepository.findAll(specification, pageable))
                .thenReturn(pageRequestEvaluations);

        Mockito.mockStatic(RequestEvaluationSpecification.class);
        when(RequestEvaluationSpecification.search(map))
                .thenReturn(specification);
        log.info(specification.toString());

        List<RequestEvaluationDto> requestEvaluationDtoList = new ArrayList<>();
        RequestEvaluationDto requestEvaluationDto1 = new Gson().fromJson(jsonResponse, RequestEvaluationDto.class);
        requestEvaluationDtoList.add(requestEvaluationDto1);
        Page<RequestEvaluationDto> pageRequestEvaluationDtos1 = new PageImpl<>(requestEvaluationDtoList);
        when(requestEvaluationConverter.convertToDTO(requestEvaluation,""))
                .thenReturn(requestEvaluationDto1);
        log.info(pageRequestEvaluationDtos1.getContent().toString());
        assertThat(requestEvaluationServiceImpl.findAll(map)).isEqualTo(pageRequestEvaluationDtos1);
    }

    @Test
    @DisplayName("Find all evaluation request empty case")
    void findAllEvaluationRequest_Empty() {
        Map<String, String> map = new HashMap<>();
        map.put("requester_id", "requester");
        Specification<RequestEvaluation> specification = RequestEvaluationSpecification.search(map);

        List<RequestEvaluation> requestEvaluations = new ArrayList<>();
        Page<RequestEvaluation> pageRequestEvaluations = new PageImpl<>(requestEvaluations);
        when(requestEvaluationRepository.findAll(specification, pageable))
                .thenReturn(pageRequestEvaluations);
        Mockito.mockStatic(RequestEvaluationSpecification.class);
        when(RequestEvaluationSpecification.search(map))
                .thenReturn(specification);
        assertThat(requestEvaluationServiceImpl.findAll(map)).isEmpty();
    }

    @Test
    @DisplayName("Find request evaluation detail happy case")
    void findRequestEvaluationDetail() {
        String createdDate = "2022-12-25";
        String updatedDate = "2022-12-26";

        // Dummy request evaluation entity list
        List<RequestEvaluation> requestEvaluations = new ArrayList<>();

        // Dummy requester
        Personal requester = Personal.builder()
                .id("requester")
                .personalNumber("personalNumber")
                .personalCode("personalCode")
                .level(Level.builder().build())
                .title("Title")
                .build();

        // Dummy approver
        Personal approver = Personal.builder()
                .id("approver")
                .personalNumber("12345678")
                .personalCode("ABC1HC")
                .level(Level.builder().build())
                .title("Title")
                .build();

        // Dummy request evaluation entity
        RequestEvaluation requestEvaluation = RequestEvaluation.builder()
                .id("id")
                .approver(approver)
                .requester(requester)
                .comment("This is comment")
                .status("APPROVAL_PENDING")
                .build();

        try {
            requestEvaluation.setCreatedDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(createdDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_CREATED_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_CREATED_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            requestEvaluation.setUpdatedDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(updatedDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_UPDATED_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_UPDATED_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Dummy skill
        Skill skill = Skill.builder()
                .id("1")
                .name("Test skill")
                .description("This is description")
                .sequence(2)
                .status("Active")
                .build();

        // Dummy request evaluation detail
        RequestEvaluationDetail requestEvaluationDetail = RequestEvaluationDetail.builder()
                .id("1")
                .skill(skill)
                .approver(approver)
                .currentLevel(2)
                .currentExp(2)
                .comment("This is comment")
                .status("APPROVAL_PENDING")
                .requestEvaluation(requestEvaluation)
                .build();

        try {
            requestEvaluationDetail.setCreatedDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(createdDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_CREATED_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_CREATED_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            requestEvaluationDetail.setUpdatedDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(updatedDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_UPDATED_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_UPDATED_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<RequestEvaluationDetail> requestEvaluationDetails = new ArrayList<>();
        requestEvaluationDetails.add(requestEvaluationDetail);

        requestEvaluation.setRequestEvaluationDetails(requestEvaluationDetails);

        requestEvaluations.add(requestEvaluation);

        when(requestEvaluationRepository.findById("1"))
                .thenReturn(Optional.of(requestEvaluation));

        RequestEvaluationDto requestEvaluationDto = RequestEvaluationDto.builder()
                .id(requestEvaluation.getId())
                .approver(requestEvaluation.getApprover().toString())
                .requester(requestEvaluation.getRequester().toString())
                .comment(requestEvaluation.getComment())
                .status(requestEvaluation.getStatus())
                .createdDate(createdDate)
                .updateDate(updatedDate)
                .build();

        SkillDescriptionDTO skillDescriptionDTO = SkillDescriptionDTO.builder()
                .id(requestEvaluationDetail.getId())
                .skillId(requestEvaluationDetail.getSkill().getId())
                .skillName(requestEvaluationDetail.getSkill().getName())
                .level(Utility.floatToStringLevelFormat(requestEvaluationDetail.getCurrentLevel()))
                .comment(requestEvaluationDetail.getComment())
                .experience(requestEvaluationDetail.getCurrentExp())
                .status(requestEvaluationDetail.getStatus())
                .build();

        List<SkillDescriptionDTO> skillDescriptionDTOS = new ArrayList<>();
        skillDescriptionDTOS.add(skillDescriptionDTO);

        when(requestEvaluationConverter.convertToDetailDTO(requestEvaluation))
                .thenReturn(requestEvaluationDto);

        RequestEvaluationDto result = requestEvaluationServiceImpl.findByID("1");
        assertThat(result).isEqualTo(requestEvaluationDto);
    }

    @Test
    @DisplayName("Find request evaluation detail throwing exception")
    void findRequestEvaluationDetail_ThrowingException() {
        Mockito.doThrow(SkillManagementException.class).when(requestEvaluationRepository).findById("12");
        assertThrows(SkillManagementException.class, () -> requestEvaluationServiceImpl.findByID("12"));
    }

    
    @Test
    @DisplayName("Update request happy case")
    void updateRequestDetail_Success() {
        String createdDate = "2022-12-25";
        String updatedDate = "2022-12-26";
        
        Personal approver = Personal.builder()
                .id("approverId")
                .personalNumber("12345678")
                .personalCode("ABC1HC")
                .level(Level.builder().build())
                .title("Title")
                .build();
        
        Personal requester = Personal.builder()
                .id("requesterId")
                .personalNumber("personalNumber")
                .personalCode("personalCode")
                .level(Level.builder().build())
                .title("Title")
                .build();
    	
    	Skill skill1 = Skill.builder()
    			.id("skill 1")
    			.build();
    	
    	SkillDescriptionDTO skillDetail1 = SkillDescriptionDTO.builder()
    			.id("skill 1")
    			.level("2")
    			.experience(2)
    			.skillId("skill 1")
    			.build();
    	
    	RequestEvaluationDetailDto requestEvaluationDetail1 = RequestEvaluationDetailDto.builder()
    			.id("test request detail 1")
    			.skillDescription(skillDetail1)
    			.status(Constants.PENDING)
    			.approver("approverId")
    			.requester("requesterId")
    			.createdDate(createdDate)
    			.updateDate(updatedDate)
    			.comment("comment 1")
    			.build();
    	
    	RequestEvaluationDetail requestEvaluationDetail = RequestEvaluationDetail.builder()
    			.id("test request detail 1")
    			.skill(skill1)
    			.status(Constants.PENDING)
    			.approver(approver)
    			.comment("comment 1")
    			.build();

        try {
        	requestEvaluationDetail.setCreatedDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(createdDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_CREATED_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_CREATED_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
        	requestEvaluationDetail.setUpdatedDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(updatedDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_UPDATED_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_UPDATED_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    	List<RequestEvaluationDetailDto> listDetailDto = new ArrayList<>();
    	listDetailDto.add(requestEvaluationDetail1);
    	
    	List<RequestEvaluationDetail> listDetail = new ArrayList<>();
    	listDetail.add(requestEvaluationDetail);
    	
    	List<SkillDescriptionDTO> listSkillDescription = new ArrayList<>();
    	listSkillDescription.add(skillDetail1);

    	
    	
    	RequestEvaluation requestEvaluation = RequestEvaluation.builder()
    			.id("test request")
    			.requestEvaluationDetails(listDetail)
    			.approver(approver)
    			.requester(requester)
    			.comment("test comment 1")
    			.build();
    	
        try {
        	requestEvaluation.setCreatedDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(createdDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_CREATED_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_CREATED_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
        	requestEvaluation.setUpdatedDate(Constants.SIMPLE_DATE_FORMAT
                    .parse(updatedDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_UPDATED_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_UPDATED_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    	
        when(requestEvaluationRepository.findById("test request"))
        .thenReturn(Optional.of(requestEvaluation));
    }

    
    @Test
	@DisplayName("Notification request pending for mananger _ happy case")
    public void notificationRequestPending_HappyCase() throws Exception {
    	String personalId = "idtest";
    	RequestEvaluationPendingDto repd = RequestEvaluationPendingDto.builder()
				.displayName("Unknow")
				.mail("404@gmail.com")
				.count(10l).build();
    	when(requestEvaluationRepository.findRequestPendingByPersonalIdAndStatus(personalId, Constants.PENDING)).thenReturn(repd);
    	
    	assertThat(repd).isEqualTo(requestEvaluationServiceImpl.notificationRequestPending(personalId));
    }
    
    @Test
	@DisplayName("Notification request pending for mananger _ wrong personal id")
    public void notificationRequestPending_WrongPersonalId() throws Exception {
    	String personalId = "idtest";
    	RequestEvaluationPendingDto repd = RequestEvaluationPendingDto.builder()
				.count(0l).build();
    	when(requestEvaluationRepository.findRequestPendingByPersonalIdAndStatus(personalId, Constants.PENDING)).thenReturn(repd);
    	assertThat(repd).isEqualTo(requestEvaluationServiceImpl.notificationRequestPending(personalId));
    }


    @Test
    @DisplayName("Approve or reject request evaluation detail")
    void approveOrRejectEvaluationDetail() throws ParseException {
        User user = User.builder()
                .id("requesterId")
                .email("testMail")
                .name("testName")
                .build();

        Personal approver = Personal.builder()
                .id("approverId")
                .personalNumber("12345678")
                .personalCode("ABC1HC")
                .level(Level.builder().build())
                .title("Title")
                .build();

        Personal requester = Personal.builder()
                .id("requesterId")
                .personalNumber("personalNumber")
                .personalCode("personalCode")
                .level(Level.builder().build())
                .title("Title")
                .user(user)
                .build();

        Skill skill1 = Skill.builder()
                .id("skill 1")
                .build();

        PersonalSkill personalSkill = PersonalSkill.builder()
                .id("skill 1")
                .skill(skill1)
                .build();

        SkillDescriptionDTO skillDetail1 = SkillDescriptionDTO.builder()
                .id("skill 1")
                .level("2")
                .experience(2)
                .skillId("skill 1")
                .build();

        Set<PersonalSkill> personalSkills = new HashSet<>();
        personalSkills.add(personalSkill);

        requester.setPersonalSkills(personalSkills);

        RequestEvaluationDetailDto requestEvaluationDetailDto = RequestEvaluationDetailDto.builder()
                .id("test request detail 1")
                .skillDescription(skillDetail1)
                .status(Constants.APPROVED)
                .approver("approverId")
                .requester("requesterId")
                .comment("comment 1")
                .build();

        RequestEvaluationDetail requestEvaluationDetail = RequestEvaluationDetail.builder()
                .id("test request detail 1")
                .skill(skill1)
                .status(Constants.APPROVED)
                .approver(approver)
                .comment("comment 1")
                .build();

        List<RequestEvaluationDetail> listDetail = new ArrayList<>();
        listDetail.add(requestEvaluationDetail);

        RequestEvaluation requestEvaluation = RequestEvaluation.builder()
                .id("test request")
                .requestEvaluationDetails(listDetail)
                .approver(approver)
                .requester(requester)
                .comment("test comment 1")
                .build();

        requestEvaluationDetail.setRequestEvaluation(requestEvaluation);

        when(requestEvaluationDetailRepository.findById(requestEvaluationDetail.getId())).thenReturn(Optional.of(requestEvaluationDetail));
        when(requestEvaluationDetailRepository.save(requestEvaluationDetail)).thenReturn(requestEvaluationDetail);
        when(requestEvaluationServiceImpl.convertRequestEvaluationDetailDto(requestEvaluationDetail)).thenReturn(requestEvaluationDetailDto);
        Mockito.lenient().when(emailServiceImpl.mailToRequesterAndApproverAboutRequest(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(EmailConstant.SEND_SUCCESS);

        RequestEvaluationDetailDto actualResult = requestEvaluationServiceImpl.updateRequestDetail(requestEvaluationDetail.getId(), requestEvaluationDetailDto);
        assertThat(requestEvaluationDetailDto).isEqualTo(actualResult);
        assertThat(actualResult.getSentEmail()).isTrue();  
        PersonalSkill[] personalSkillArr = requester.getPersonalSkills().toArray(new PersonalSkill[0]);
        assertThat(personalSkillArr[0].getLevel()).isEqualTo("2");
        assertThat(personalSkillArr[0].getExperience()).isEqualTo(2);
    }

    @Test
    @DisplayName("Approve or reject request evaluation")
    void approveOrRejectEvaluation() throws ParseException {
        User user = User.builder()
                .id("requesterId")
                .email("testMail")
                .name("testName")
                .build();

        User approverUser = User.builder()
                .id("approverId")
                .email("testMail")
                .name("approverName")
                .build();

        Personal approver = Personal.builder()
                .id("approverId")
                .personalNumber("12345678")
                .personalCode("ABC1HC")
                .level(Level.builder().build())
                .title("Title")
                .build();

        Personal requester = Personal.builder()
                .id("requesterId")
                .personalNumber("personalNumber")
                .personalCode("personalCode")
                .level(Level.builder().build())
                .title("Title")
                .user(user)
                .build();

        Skill skill1 = Skill.builder()
                .id("skill 1")
                .build();

        Skill skill2 = Skill.builder()
                .id("skill 2")
                .build();

        PersonalSkill personalSkil1 = PersonalSkill.builder()
                .id("skill 1")
                .skill(skill1)
                .build();

        PersonalSkill personalSkil2 = PersonalSkill.builder()
                .id("skill 2")
                .skill(skill2)
                .build();

        Set<PersonalSkill> personalSkills = new HashSet<>();
        personalSkills.add(personalSkil1);
        personalSkills.add(personalSkil2);

        requester.setPersonalSkills(personalSkills);

        SkillDescriptionDTO skillDetail1 = SkillDescriptionDTO.builder()
                .id("skill 1")
                .level("2")
                .experience(2)
                .skillId("skill 1")
                .build();

        SkillDescriptionDTO skillDetail2 = SkillDescriptionDTO.builder()
                .id("skill 2")
                .level("1")
                .experience(1)
                .skillId("skill 2")
                .build();


        RequestEvaluationDetail requestEvaluationDetail1 = RequestEvaluationDetail.builder()
                .id("test request detail 1")
                .skill(skill1)
                .status(Constants.PENDING)
                .approver(approver)
                .build();

        RequestEvaluationDetail requestEvaluationDetail2 = RequestEvaluationDetail.builder()
                .id("test request detail 2")
                .skill(skill2)
                .status(Constants.PENDING)
                .approver(approver)
                .build();

//        List<RequestEvaluationDetailDto> listDetailDto = new ArrayList<>();
//        listDetailDto.add(requestEvaluationDetailDto1);
//        listDetailDto.add(requestEvaluationDetailDto2);

        List<RequestEvaluationDetail> listDetail = new ArrayList<>();
        listDetail.add(requestEvaluationDetail1);
        listDetail.add(requestEvaluationDetail2);

        List<SkillDescriptionDTO> listSkillDescription = new ArrayList<>();
        listSkillDescription.add(skillDetail1);
        listSkillDescription.add(skillDetail2);

        RequestEvaluationDto requestEvaluationDto = RequestEvaluationDto.builder()
                .id("testRequest")
                .approver(approver.getId())
                .comment("hehe")
                .status(Constants.APPROVED)
                .requestEvaluationDetails(listSkillDescription)
                .build();

        RequestEvaluation requestEvaluation = RequestEvaluation.builder()
                .id("testRequest")
                .requestEvaluationDetails(listDetail)
                .approver(approver)
                .requester(requester)
                .status(Constants.APPROVED)
                .comment("test comment 1")
                .build();

        requestEvaluationDetail1.setRequestEvaluation(requestEvaluation);
        requestEvaluationDetail2.setRequestEvaluation(requestEvaluation);

        when(userRepository.findByName("approverName")).thenReturn(Optional.ofNullable(approverUser));
        when(requestEvaluationRepository.findById(requestEvaluationDto.getId())).thenReturn(Optional.ofNullable(requestEvaluation));

        requestEvaluationServiceImpl.updateRequest(requestEvaluationDto, "approverName");

        PersonalSkill[] personalSkillArr = requester.getPersonalSkills().toArray(new PersonalSkill[0]);
        for (PersonalSkill personalSkill: personalSkillArr) {
            if (personalSkill.getId().equalsIgnoreCase("skill 1")){
                assertThat(personalSkill.getLevel()).isEqualTo("2");
                assertThat(personalSkill.getExperience()).isEqualTo(2);

            } else {
            	  assertThat(personalSkill.getLevel()).isEqualTo("1");
                  assertThat(personalSkill.getExperience()).isEqualTo(1);
            }
        }
    }
    @Test
    @DisplayName("Find all requested")
    void findAllRequestEvaluated() {

        String createdDate = "2022-12-25";
        String updatedDate = "2022-12-26";
        String approvedDate="2023-07-04";

        // Dummy request evaluation entity list
        List<RequestEvaluation> requestEvaluations = new ArrayList<>();

        // Dummy requester
        Personal requester = Personal.builder()
                .id("requester")
                .personalNumber("personalNumber")
                .personalCode("personalCode")
                .level(Level.builder().build())
                .title("Title")
                .build();

        // Dummy approver
        Personal approver = Personal.builder()
                .id("approver")
                .personalNumber("12345678")
                .personalCode("ABC1HC")
                .level(Level.builder().build())
                .title("Title")
                .build();


        // Dummy request evaluation entity
        RequestEvaluation requestEvaluation = RequestEvaluation.builder()
                .id("id")
                .approver(approver)
                .requester(requester)
                .comment("This is comment")
                .status("APPROVED")
                .build();

        try {
            requestEvaluation.setCreatedDate(Constants.SIMPLE_DATE_FORMAT
                .parse(createdDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_CREATED_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_CREATED_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            requestEvaluation.setUpdatedDate(Constants.SIMPLE_DATE_FORMAT
                .parse(updatedDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_UPDATED_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_UPDATED_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        try {
            requestEvaluation.setApprovedDate(Constants.SIMPLE_DATE_FORMAT
                .parse(approvedDate));
        } catch (ParseException e) {
            throw new SkillManagementException(MessageCode.CANNOT_PARSE_UPDATED_DATE.toString(),
                    messageSource.getMessage(MessageCode.CANNOT_PARSE_UPDATED_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        requestEvaluations.add(requestEvaluation);
        
        RequestEvaluationDto requestEvaluationDto = RequestEvaluationDto.builder()
                .id(requestEvaluation.getId())
                .approver("haha")
                .requester("hihi")
                .comment(requestEvaluation.getComment())
                .status(requestEvaluation.getStatus())
                .createdDate(createdDate)
                .updateDate(updatedDate)
                .approvedDate(approvedDate)
                .competencyLeadEvaluateAll("")
                .build();

        List<RequestEvaluationDto> requestEvaluationDtos = new ArrayList<>();
        requestEvaluationDtos.add(requestEvaluationDto);

        when(requestEvaluationRepository.findAllRequestEvaluated("approver", Constants.WAITING_FOR_APPROVAL))
                .thenReturn(requestEvaluations);

        String jsonRes="{"
		+"\"id\":\"id\","
		+"\"requester\":\"hihi\","
		+"\"approver\":\"haha\","
		+"\"status\":\"APPROVED\","
		+"\"comment\":\"This is comment\","
		+"\"competencyLeadEvaluateAll\":\"\","
		+"\"createdDate\":\"2022-12-25\","
		+"\"updateDate\":\"2022-12-26\","
		+"\"approvedDate\":\"2023-07-04\""
		+"}";

        List<RequestEvaluationDto> requestEvaluationDtoList = new ArrayList<>();
        RequestEvaluationDto requestEvaluationDto1 = new Gson().fromJson(jsonRes, RequestEvaluationDto.class);
        requestEvaluationDtoList.add(requestEvaluationDto1);
         
        when(requestEvaluationConverter.convertToDTO(requestEvaluation,"approver"))
                .thenReturn(requestEvaluationDto1);
        assertThat(requestEvaluationServiceImpl.findRequestEvaluated("approver")).isEqualTo(requestEvaluationDtos);
    }
}
