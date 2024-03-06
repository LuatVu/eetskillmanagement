/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.rest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.AddSkillDto;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.PersonalCourseDto;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.PersonalProjectDto;
import com.bosch.eet.skill.management.dto.PersonalSkillEvaluationDto;
import com.bosch.eet.skill.management.dto.TotalPersonDTO;
import com.bosch.eet.skill.management.dto.UpdateDto;
import com.bosch.eet.skill.management.exception.EETResponseException;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.PersonalFacade;
import com.bosch.eet.skill.management.ldap.exception.LdapException;
import com.bosch.eet.skill.management.repo.PersonalCourseRepository;
import com.bosch.eet.skill.management.service.PersonalService;
import com.bosch.eet.skill.management.usermanagement.consts.MessageCode;

import io.github.jhipster.web.util.PaginationUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author TGI2HC
 * @author DUP5HC
 */
@RequestMapping
@RestController
@Slf4j
public class PersonRest {
    @Autowired
    private PersonalService personalService;
    @Autowired
    private PersonalFacade personalFacade;
    @Autowired
    private PersonalCourseRepository personalCourseReposity;
    @Autowired
    private MessageSource messageSource;

    @GetMapping(value = Routes.URI_REST_PERSON)
    public ResponseEntity<TotalPersonDTO> getPersons(@Param("team") String team,
                                                     @Param("skill") String skill,
                                                     @Param("skill-group") String skillGroup,
                                                     @Param("exp") String exp,
                                                     Pageable pageable) {
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("team", team);
        filterParams.put("skill", skill);
        filterParams.put("skill-group", skillGroup);
        filterParams.put("exp", exp);
        Page<PersonalDto> personalsDto = personalService.findAll(pageable, filterParams);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), personalsDto);
        TotalPersonDTO totalPerson = TotalPersonDTO.builder()
                .listPersonal(personalsDto.getContent())
                .total(personalsDto.getTotalElements())
                .build();
        return ResponseEntity.ok().headers(headers).body(totalPerson);
    }

    @GetMapping(value = Routes.URI_REST_PERSON_FILTER)
    public ResponseEntity<HashMap<String, Object>> getPersonFilter() {
        HashMap<String, Object> filterJson = personalService.getFilter();
        return ResponseEntity.ok().body(filterJson);
    }

    @GetMapping(value = Routes.URI_REST_PERSON_ID)
    public ResponseEntity<PersonalDto> getPerson(@PathVariable(name = "associate_id") String associateId, Authentication auth) {
    	String ntid = auth.getName();
		Set<String> authSet = new HashSet<>(
				auth.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList()));
        PersonalDto personalDto = personalService.findById(associateId, ntid, authSet);
        return ResponseEntity.ok().body(personalDto);
    }

    @GetMapping(value = Routes.URI_REST_ASSOCIATE_ID)
    public GenericResponseDTO<AddSkillDto> getAssociateInfo(@PathVariable(name = "associate_id") String associateId) {
        return GenericResponseDTO.<AddSkillDto>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(AddSkillDto.builder()
                        .personalDto(personalService.findByIdForEdit(associateId))
                        .skillGroupIds(personalService.findByIdForEdit(associateId).getSkillClusterId())
                        .build())
                .build();
    }

    @GetMapping(value = Routes.URI_REST_PERSON_CHECKEXIST)
    public GenericResponseDTO<Boolean> checkExistence(@PathVariable(name = "associate_id") String associateNTId) {
        return GenericResponseDTO.<Boolean>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(personalService.checkIsExisted(associateNTId))
                .build();
    }

    @PostMapping(value = Routes.URI_REST_PERSON_UPDATE)
    public ResponseEntity<PersonalDto> update(@PathVariable(name = "associate_id") String associateId,
                                              @RequestBody @Valid PersonalDto personalDto) {
        personalDto.setId(associateId);
        personalService.save(personalDto);
        return ResponseEntity.ok().body(personalDto);
    }

    @GetMapping(value = Routes.URI_REST_PERSON_ID_PROJECT)
    public ResponseEntity<List<PersonalProjectDto>> getPersonProjects(@PathVariable(name = "associate_id") String associateId,
                                                                      @RequestParam(name = "search", required = false) String search,
                                                                      @PageableDefault(value = Constants.PAGEABLE_DEFAULT) Pageable pageable) {
        Page<PersonalProjectDto> projects = personalService.findProjectsByPersonalId(associateId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), projects);
        return ResponseEntity.ok().headers(headers).body(projects.getContent());
    }

    @GetMapping(value = Routes.URI_REST_PERSON_ID_PROJECT_ID)
    public ResponseEntity<PersonalProjectDto> getPersonProject(@PathVariable(name = "associate_id") String associateId,
                                                               @PathVariable(name = "id") String projectId) {
        PersonalProjectDto personalProjectById = personalService.findPersonalProjectById(associateId, projectId);
        return ResponseEntity.ok().body(personalProjectById);

    }

    @PostMapping(value = Routes.URI_REST_PERSON_ID_PROJECT)
    public ResponseEntity<PersonalProjectDto> assignBoschProject(@PathVariable(name = "associate_id") String associateId,
                                                                 @RequestBody PersonalProjectDto personalProject, Authentication auth) {
        String requester = auth.getName();
        if(StringUtils.isBlank(personalProject.getProjectId()) ||
                StringUtils.isBlank(personalProject.getRoleId()) ||
                StringUtils.isBlank(associateId)){
            throw new SkillManagementException(com.bosch.eet.skill.management.exception.MessageCode.MANDATORY_FIELD_MISSING.toString(),
                    messageSource.getMessage(com.bosch.eet.skill.management.exception.MessageCode.MANDATORY_FIELD_MISSING.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST);
        }
        
        try {
            personalProject.setCreatedBy(requester);
            PersonalProjectDto personalProjectById = personalService.addPersonalProject(associateId, personalProject);
            return ResponseEntity.ok(personalProjectById);
        }
        catch(SkillManagementException | EETResponseException handledException) {
        	throw handledException;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            throw new SkillManagementException(com.bosch.eet.skill.management.exception.MessageCode.SAVING_PROJECT_FAIL.toString(),
                    messageSource.getMessage(com.bosch.eet.skill.management.exception.MessageCode.SAVING_PROJECT_FAIL.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(value = Routes.URI_REST_PERSON_ID_NON_BOSCH_PROJECT)
    public ResponseEntity<PersonalProjectDto> addNonBoschProject(@PathVariable(name = "associate_id") String associateId,
                                                                 @RequestBody @Valid PersonalProjectDto personalProject, Authentication auth) {
        String requester = auth.getName();
        try {
            personalProject.setCreatedBy(requester);
            PersonalProjectDto personalProjectById = personalFacade.addNonBoschProject(associateId, personalProject);
            return ResponseEntity.ok(personalProjectById);
        } catch (SkillManagementException e) {
        	log.error(e.getMessage());
        	throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new SkillManagementException(com.bosch.eet.skill.management.exception.MessageCode.SAVING_PROJECT_FAIL.toString(),
                    messageSource.getMessage(com.bosch.eet.skill.management.exception.MessageCode.SAVING_PROJECT_FAIL.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = Routes.URI_REST_PERSONAL_PROJECT_UPDATE)
    public GenericResponseDTO<PersonalProjectDto> updateProject(@PathVariable(name = "id") String id,
                                                                @RequestBody @Valid PersonalProjectDto personalProjectDto) {
        return GenericResponseDTO.<PersonalProjectDto>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(personalFacade.updatePersonalProject(id, personalProjectDto))
                .build();
    }

    @PostMapping(value = Routes.URI_REST_PERSONAL_PROJECT_DELETE)
    public GenericResponseDTO<String> deletePersonalProject(@PathVariable(name = "associate_id") String associateId,
                                                            @PathVariable(name = "id") String personalProjectId,
                                                            Authentication auth) {
        PersonalProjectDto findPersonalProjectDto = personalService.findPersonalProjectById(associateId, personalProjectId);
        if (Objects.isNull(findPersonalProjectDto)) {
            throw new EETResponseException(String.valueOf(BAD_REQUEST.value()), "This project doesn't exist!", null);
        }
        Set<String> authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
		personalFacade.deletePersonalProject(findPersonalProjectDto, authorities, auth.getName());
        return GenericResponseDTO.<String>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(MessageCode.SUCCESS.toString())
                .build();
    }

    /**
     * @author DUP5HC
     */

//  Personal avatar size can only be under 16MB
    @PostMapping(value = Routes.URI_REST_PERSON_ID_AVATAR)
    public ResponseEntity<PersonalDto> uploadAvatar(@PathVariable(name = "associate_id") String associateId,
                                                    @RequestParam("file") MultipartFile file) {
        final PersonalDto avatar;
        try {
            avatar = personalService.saveImage(associateId, file.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "File size exceeded our limit! Try again", null);
        }
        return ResponseEntity.ok().body(avatar);
    }

    //  Get All Personal Courses by PersonalId
    @GetMapping(value = Routes.URI_REST_PERSON_ID_COURSE)
    public ResponseEntity<List<PersonalCourseDto>> getCourseByAssociateId(@PathVariable(name = "associate_id") String associateId,
                                                                          @RequestParam(name = "search", required = false) String search,
                                                                          Pageable pageable) {
        Page<PersonalCourseDto> courses = personalService.findCoursesByPersonalId(associateId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), courses);
        return ResponseEntity.ok().headers(headers).body(courses.getContent());
    }

    //  Get Course by PersonalId and CourseId
    @GetMapping(value = Routes.URI_REST_PERSON_ID_COURSE_ID)
    public ResponseEntity<PersonalCourseDto> getPersonCourse(@PathVariable(name = "associate_id") String associateId,
                                                             @PathVariable(name = "id") String courseId) {
        PersonalCourseDto personalCourseDto = personalService.findPersonalCourseById(associateId, courseId);

        return ResponseEntity.ok().body(personalCourseDto);
    }

    //    	Add new Personal Course
    @PostMapping(value = Routes.URI_REST_PERSON_ID_COURSE)
    public GenericResponseDTO<PersonalDto> addNewCourse(@PathVariable(name = "associate_id") String associateId,
                                                        @RequestBody List<String> courseId) {
        final PersonalDto newPersonalCourses = personalService.addCoursesByListOfTrainingCourse(associateId, courseId);
        return GenericResponseDTO.<PersonalDto>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(newPersonalCourses)
                .build();
    }

    //	Update Course StartDate and EndDate
    @PostMapping(value = Routes.URI_REST_PERSONAL_COURSE_UPDATE)
    public ResponseEntity<PersonalCourseDto> updateDate(@PathVariable(name = "associate_id") String associateId,
                                                        @PathVariable(name = "id") String courseId,
                                                        @RequestBody UpdateDto updateDto) {

        PersonalCourseDto searchPersonalCourseDto = personalService.findPersonalCourseById(associateId, courseId);
        if (Objects.isNull(searchPersonalCourseDto)) {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "This associate doesn't have any current course!", null);
        }
        if (searchPersonalCourseDto.getStatus().contains("Done")) {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Course already done, cannot change date!", null);
        }
        try {
            searchPersonalCourseDto = personalService.updateCourse(updateDto, courseId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Try again", null);
        }
        return ResponseEntity.ok().body(searchPersonalCourseDto);
    }

    //	Remove Course from Personal
    @PostMapping(value = Routes.URI_REST_PERSONAL_COURSE_DELETE)
    public void deleteCourse(@PathVariable(name = "associate_id") String associateId,
                             @PathVariable(name = "id") String courseId) {
        PersonalCourseDto searchPersonalCourseDto = personalService.findPersonalCourseById(associateId, courseId);
        if (Objects.isNull(searchPersonalCourseDto)) {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "This course doesn't exist", null);
        }
        personalCourseReposity.deleteById(courseId);


    }

    //	Upload Personal Course Certificate
    @PostMapping(value = Routes.URI_REST_PERSONAL_COURSE_CERTIFICATE)
    public ResponseEntity<PersonalCourseDto> uploadCertificate(@PathVariable(name = "id") String personalCourseId,
                                                               @RequestParam("file") MultipartFile file) {
        final PersonalCourseDto certificate;
        try {
            certificate = personalService.uploadCertificate(personalCourseId, file.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "File size exceeded our limit! Try again", null);
        }
        return ResponseEntity.ok().body(certificate);
    }

    //	Delete Personal Course Certificate
    @PostMapping(value = Routes.URI_REST_PERSONAL_COURSE_CERTIFICATE_DELETE)
    public ResponseEntity<PersonalCourseDto> deleteCertificate(@PathVariable(name = "id") String personalCourseId) {
        final PersonalCourseDto removeCertificate;
        removeCertificate = personalService.deleteCertificate(personalCourseId);
        return ResponseEntity.ok().body(removeCertificate);
    }


    @GetMapping(value = Routes.URI_REST_PERSON_ID_EXPORT)
    public ResponseEntity<InputStreamResource> exportData(@PathVariable(name = "associate_id") String associateId) {

        Page<PersonalProjectDto> projects = personalService.findProjectsDetailByPersonalId(associateId, Pageable.unpaged());
        Page<PersonalCourseDto> courses = personalService.findCoursesByPersonalId(associateId, Pageable.unpaged());

        // Set the input stream
        String outputPFDPath = personalService.exportData(associateId, projects.getContent(), courses.getContent());
        // asume that it was a PDF file
        File file = new File(outputPFDPath);
        InputStream inputstream = null;
        try {
            inputstream = Files.newInputStream(file.toPath());
        } catch (IOException e) {
            throw new EETResponseException(String.valueOf(INTERNAL_SERVER_ERROR.value()), "PDF is not exist", null);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        InputStreamResource inputStreamResource = new InputStreamResource(inputstream);
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentType(MediaType.valueOf("application/pdf"));
        // just in case you need to support browsers
        responseHeaders.put("Content-Disposition", Collections.singletonList("attachment; filename=export.pdf"));
        return new ResponseEntity<>(inputStreamResource,
                responseHeaders,
                HttpStatus.OK);
    }

    @GetMapping(value = Routes.URI_REST_PERSON_ID_SKILL)
    public ResponseEntity<GenericResponseDTO<PersonalSkillEvaluationDto>> getSkillsByPersonalId(
            @PathVariable(name = "associate_id") String personalId) {
        PersonalSkillEvaluationDto skillsByPersonalId = personalService.findSkillsByPersonalIdV2(personalId);
        GenericResponseDTO<PersonalSkillEvaluationDto> response = new GenericResponseDTO<>();
        response.setData(skillsByPersonalId);
        response.setCode(MessageCode.SUCCESS.toString());
        response.setTimestamps(new Date());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = Routes.URI_REST_PERSON)
    public GenericResponseDTO<String> addAssociateAndAssignSkill(
            @RequestBody AddSkillDto addSkillDto) throws LdapException {
        log.info(addSkillDto.toString());
        GenericResponseDTO<String> response = new GenericResponseDTO<>();
        personalService.addNewAssociate(addSkillDto);
        response.setCode(MessageCode.ADD_ASSOCIATE_SUCCESSFUL.toString());
        response.setTimestamps(new Date());
        return response;
    }

    @PostMapping(value = Routes.URI_REST_PERSON_EDIT)
    public GenericResponseDTO<String> updatePersonalInfo(@PathVariable(name = "associate_id") String personalId,
                                                         @RequestBody AddSkillDto addSkillDto) {
        personalService.editAssociateInfo(addSkillDto);
        return GenericResponseDTO.<String>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(addSkillDto.toString())
                .build();
    }
    //list associate not evaluate yet
    @GetMapping(value = Routes.URI_REST_PERSON_NOT_EVALUATE)
    public GenericResponseDTO<List<PersonalDto>> getAssociateNotEvaluateYet(
    		@PathVariable(name = "manager_id") String managerId){
    	List<PersonalDto> personals = personalService.findAssociateNotEvaluate(managerId);
	    return GenericResponseDTO.<List<PersonalDto>>builder()
	    		.code(MessageCode.SUCCESS.toString())
	    		.data(personals)
	    		.build();
    }

    @PostMapping(value = Routes.URI_REST_PERSON_LIST_EXPORT)
    public void exportAssociateList(HttpServletResponse response, @RequestBody List<String> personalStringList) {
        response.setContentType("application/octet-stream");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=associateList.xlsx";
        response.setHeader(headerKey, headerValue);
        
        try {
            personalService.generateAssociateListExcel(response, personalStringList);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
    @PostMapping(value = Routes.URI_REST_PERSON_DELETE)
    public GenericResponseDTO<String> deleteAssociate(@PathVariable(name = "associate_id") String personalId) {
    	if (StringUtils.isBlank(personalId)) {
            throw new SkillManagementException(MessageCode.ASSOCIATE_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.ASSOCIATE_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
        }
        personalService.deleteAssociate(personalId);
        return GenericResponseDTO.<String>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(Constants.SUCCESS)
                .build();
    }
}
