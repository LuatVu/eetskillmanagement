package com.bosch.eet.skill.management.rest;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.CommonTaskDto;
import com.bosch.eet.skill.management.dto.DepartmentGroupDto;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.ProjectRoleDto;
import com.bosch.eet.skill.management.dto.SkillCompetencyLeadDto;
import com.bosch.eet.skill.management.dto.TeamDto;
import com.bosch.eet.skill.management.dto.VModelDto;
import com.bosch.eet.skill.management.elasticsearch.document.QueryBuilder;
import com.bosch.eet.skill.management.elasticsearch.document.ResultQuery;
import com.bosch.eet.skill.management.elasticsearch.repo.PersonalElasticRepository;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.exception.EETResponseException;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.ldap.exception.LdapException;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.service.CommonService;
import com.bosch.eet.skill.management.service.PersonalService;

import io.github.jhipster.web.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author TGI2HC
 */

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommonRest {

    private final CommonService commonService;

    private final MessageSource messageSource;
    
    private final PersonalElasticRepository personalElasticRepository;
    
    private final PersonalService personalService;
    
    private final PersonalRepository personalRepository;
    
  
    @GetMapping(value = Routes.URI_REST_PROJECT_ROLE)
    public ResponseEntity<List<ProjectRoleDto>> getAllProjectRole(@RequestParam Map<String, String> filterParams) {
        Page<ProjectRoleDto> projectRoleDto = commonService.findAllRoles(Pageable.unpaged(), filterParams);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), projectRoleDto);
        return ResponseEntity.ok().headers(headers).body(projectRoleDto.getContent());
    }

    @GetMapping(value = Routes.URI_REST_COMMONTASKS)
    public ResponseEntity<List<CommonTaskDto>> getCommonTasksByProjectRoleId(@PathVariable(name = "id") String projectRoleId,
                                                                             @RequestParam(name = "search", required = false) String search,
                                                                             Pageable pageable) {
        Page<CommonTaskDto> commontask = commonService.findByProjectRoleId(projectRoleId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), commontask);
        return ResponseEntity.ok().headers(headers).body(commontask.getContent());
    }

    @GetMapping(value = Routes.URI_REST_GB)
    public ResponseEntity<List<String>> getGBs() {
        List<String> gBs = commonService.findAllGB();
        return ResponseEntity.ok().body(gBs);
    }

    @PostMapping(value = Routes.URI_REST_UPLOAD_SKILL)
    public ResponseEntity<String> uploadSkills(@RequestParam("file") MultipartFile file) {
        boolean rs = commonService.uploadSkill(file);
        if (!rs) {
            throw new EETResponseException(String.valueOf(INTERNAL_SERVER_ERROR.value()), "Upload skill failed", null);
        }
        return ResponseEntity.ok().body("Done");

    }

    @PostMapping(value = Routes.URI_REST_COMMON_ADD_PERSONAL)
    public GenericResponseDTO<String> addPersonalInfos(
            @RequestParam("file") MultipartFile file, @RequestParam("departmentName") String departmentName, Authentication auth) {

        commonService.addPersonalInfosFromExcel(file, auth.getName(), departmentName);

        return GenericResponseDTO.<String>builder()
                .timestamps(new Date())
                .code(MessageCode.SUCCESS.toString())
                .data(
                        messageSource.getMessage(
                                MessageCode.SUCCESS.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        )
                )
                .build();
    }

    @PostMapping(value = Routes.URI_REST_COMMON_ADD_PERSONAL_SKILL)
    public GenericResponseDTO<String> addPersonalSkillInfos(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "ntid", required = false) String ntid,
            Authentication auth
    ) {

        commonService.addPersonalSkillInfosFromExcel(file, auth.getName(), ntid);

        return GenericResponseDTO.<String>builder()
                .timestamps(new Date())
                .code(MessageCode.SUCCESS.toString())
                .data(
                        messageSource.getMessage(
                                MessageCode.SUCCESS.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        )
                )
                .build();
    }
    
    @GetMapping(value = Routes.URI_REST_COMMONTASK)
    public GenericResponseDTO<List<CommonTaskDto>> getAllCommonTask() {
        return GenericResponseDTO.<List<CommonTaskDto>>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(commonService.findAllCommonTask())
                .build();
    }
    
    @PostMapping(value = Routes.URI_REST_PROJECT_ROLE_DELETE)
    public GenericResponseDTO<List<ProjectRoleDto>> deleteProjectRole(@RequestBody List<ProjectRoleDto> projectRoleDtos) {
    	try {
    		commonService.deleteProjectRole(projectRoleDtos);
        	return GenericResponseDTO.<List<ProjectRoleDto>>builder()
                    .code(MessageCode.SUCCESS.toString())
                    .timestamps(new Date())
                    .build();

    	} catch (Exception e) {
        	return GenericResponseDTO.<List<ProjectRoleDto>>builder()
                    .code(MessageCode.PROJECT_ROLE_IS_IN_USE.toString())
                    .timestamps(new Date())
                    .build();
    	}
    }
    
    @PostMapping(value = Routes.URI_REST_COMMONTASK_DELETE)
    public GenericResponseDTO<List<CommonTaskDto>> deleteCommonTask(@RequestBody List<CommonTaskDto> commonTaskDtos) {
    	return GenericResponseDTO.<List<CommonTaskDto>>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(commonService.deleteCommonTask(commonTaskDtos))
                .build();
    	
    }
    
    @PostMapping(value = Routes.URI_REST_PROJECT_ROLE)
	public GenericResponseDTO<ProjectRoleDto> addNewProjectRole(
					@RequestBody ProjectRoleDto projectRoleDto) {
        try {
            final ProjectRoleDto newProjectRole =
                    commonService.addNewProjectRole(projectRoleDto);
            return GenericResponseDTO.<ProjectRoleDto>builder()
                    .code(MessageCode.SUCCESS.toString())
                    .data(newProjectRole)
                    .build();
        } catch (Exception e) {
            log.error(messageSource.getMessage(MessageCode.PROJECT_NAME_ALREADY_EXIST.toString(),
                    null, LocaleContextHolder.getLocale()));
            return GenericResponseDTO.<ProjectRoleDto>builder()
                    .code(MessageCode.PROJECT_NAME_ALREADY_EXIST.toString())
                    .build();
        }
	}
    
    @PostMapping(value = Routes.URI_REST_COMMONTASK)
	public GenericResponseDTO<CommonTaskDto> addNewCommonTaskToProjectRole(
			@RequestBody @Valid CommonTaskDto commonTaskDto) {
            return GenericResponseDTO.<CommonTaskDto>builder()
                    .code(MessageCode.SUCCESS.toString())
                    .data(commonService.addNewCommonTask(commonTaskDto))
                    .build();
	}
    
    @GetMapping(value = Routes.URI_REST_MANAGER)
    public GenericResponseDTO<List<PersonalDto>> findAllManager() {
    	return GenericResponseDTO.<List<PersonalDto>>builder()
    			.code(MessageCode.SUCCESS.toString())
    			.data(commonService.findAllPersonalManagerRole())
    			.build();
    }
    @GetMapping(value =Routes.URI_REST_LINE_MANAGER)
    public GenericResponseDTO<PersonalDto> findLineManager(@PathVariable String idPersonal){
    	return GenericResponseDTO.<PersonalDto>builder()
    			.code(MessageCode.SUCCESS.toString())
    			.data(commonService.findLineManager(idPersonal))
    			.build();
    }
    
//	Insert data to elastic search
    @PostMapping(value = Routes.URI_REST_ELASTIC_IMPORT)
    public GenericResponseDTO<String> importDocument() {
    	personalElasticRepository.insertDataToIndex();
    	return GenericResponseDTO.<String>builder()
    			.code(MessageCode.ADD_DATA_INDEX_SUCCESS.toString())
    			.build();
    }
    
//	Create index to elastic search
    @PutMapping(value = Routes.URI_REST_ELASTIC_IMPORT)
    public GenericResponseDTO<String> createIndex() {
    	personalElasticRepository.createIndex();
    	return GenericResponseDTO.<String>builder()
    			.code(MessageCode.CREATE_INDEX_SUCCESS.toString())
    			.build();
    }
   
//	Delete index to elastic search    
    @PostMapping(value =  Routes.URI_REST_ELASTIC_DEL)
    public GenericResponseDTO<String> deleteIndex() {
    	personalElasticRepository.deleteIndex();
    	return GenericResponseDTO.<String>builder()
    			.code(MessageCode.DELETE_INDEX_SUCCESS.toString())
    			.build();
    }
    
//	Sync index's data to elastic search    
    @GetMapping(value = Routes.URI_REST_ELASTIC_IMPORT)
    public GenericResponseDTO<String> syncIndex() throws IOException {
    	personalElasticRepository.syncData();
    	return GenericResponseDTO.<String>builder()
    			.code(MessageCode.SYNC_DATA_SUCCESS.toString())
    			.build();
    }
    
    @PutMapping(value = Routes.URI_REST_ELASTIC_UPDATE)
    public GenericResponseDTO<String> updateData() {
    	personalElasticRepository.updateData();
    	return GenericResponseDTO.<String>builder()
    			.code(MessageCode.UPDATE_INDEX_SUCCESS.toString())
    			.build();
    }
    
    @PostMapping(value = Routes.URI_REST_ELASTIC_QUERY)
    public GenericResponseDTO<ResultQuery> getAllPersonalRecord(@PathVariable(name = "index_name") String indexName,
    		@RequestBody QueryBuilder queryBuilder) throws IOException {
    	return GenericResponseDTO.<ResultQuery>builder()
    			.code(MessageCode.SUCCESS.toString())
    			.data(personalElasticRepository.searchFromQuery(
    					indexName, queryBuilder.getQuery(),
    					queryBuilder.getSize(), queryBuilder.getFrom()))
    			.build();
    	
    }
    
    @PostMapping(value = Routes.URI_REST_ELASTIC_QUERY_PERSONAL_NAME_NTID)
    public GenericResponseDTO<ResultQuery> findPersonalByNameOrNtid(@RequestBody QueryBuilder queryBuilder)
            throws IOException {
    	return GenericResponseDTO.<ResultQuery>builder()
    			.code(MessageCode.SUCCESS.toString())
    			.data(personalElasticRepository.searchPersonalByNameOrNtid(
    					queryBuilder.getQuery(), queryBuilder.getSize(), queryBuilder.getFrom()))
    			.build();
    	
    }
    
    @GetMapping(value = Routes.URI_REST_TEAM)
    public GenericResponseDTO<List<TeamDto>> getAllTeams() {
    	return GenericResponseDTO.<List<TeamDto>>builder()
    			.code(MessageCode.SUCCESS.toString())
    			.data(commonService.findAllTeam())
    			.build();
    }
    
    @PostMapping(value = Routes.URI_REST_INSERT_SKILL)
    public GenericResponseDTO<String> insertSkill() {
    	List<Personal> listPersonal = personalRepository.findAll();
    	for(Personal personal : listPersonal) {
    		personalService.addSkillToPersonal(personal);
    	}
    	return GenericResponseDTO.<String>builder()
    			.code(MessageCode.SUCCESS.toString())
    			.data("Add skill to list personal success")
    			.build();
    }
    @PostMapping(value = Routes.URI_REST_COMMON_ADD_PERSONAL_SKILL_CLUSTER)
    public GenericResponseDTO<String> addPersonalSkillCluster(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "ntid", required = false) String ntid,
            Authentication auth
    ) {

        commonService.addSkillCluster(file, auth.getName(), ntid);

        return GenericResponseDTO.<String>builder()
                .timestamps(new Date())
                .code(MessageCode.SUCCESS.toString())
                .data(
                        messageSource.getMessage(
                                MessageCode.SUCCESS.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        )
                )
                .build();
    }
    
    @GetMapping(value = Routes.URI_REST_DEPARTMENT_GROUP)
    public GenericResponseDTO<List<DepartmentGroupDto>> getAllDepartmentGroup() {
    	return GenericResponseDTO.<List<DepartmentGroupDto>>builder()
    			.code(MessageCode.SUCCESS.toString())
    			.data(commonService.findAllDepartmentGroup())
    			.build();
    }
    
    @GetMapping(value = Routes.URI_REST_TEAM_ID)
    public GenericResponseDTO<DepartmentGroupDto> getDepartmentGroupByTeamId(@PathVariable (name = "id") String id) {
    	return GenericResponseDTO.<DepartmentGroupDto>builder()
    			.code(MessageCode.SUCCESS.toString())
    			.data(commonService.findByTeamId(id))
    			.build();
    }
    
    @GetMapping(value = Routes.URI_REST_PHASE)
    public GenericResponseDTO<VModelDto> getVModel(){
    	return GenericResponseDTO.<VModelDto>builder().code(MessageCode.SUCCESS.toString())
    			.data(commonService.getVModel())
    			.build();
    }
    

    @PutMapping(value = Routes.URI_REST_PERSONAL_UPDATE_EMAIL)
    public GenericResponseDTO<String> updateEmail(){
       	try {
			return GenericResponseDTO.<String>builder()
					.code(MessageCode.SUCCESS.toString())
					.data(commonService.updateEmail()).build();
		} catch (LdapException e) {
			return null;
		}
    }

    @PostMapping(value = Routes.URI_REST_PERSON_UPDATE_SKILL_GROUP_DATA)
    public GenericResponseDTO<String> updatePersonalSkillGroup(){
        return GenericResponseDTO.<String>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(commonService.updatePersonalSkillGroup()).build();
    }

    @GetMapping(value = Routes.URI_REST_COMPETENCY_LEAD_BY_EVALUATION)
    public GenericResponseDTO<List<SkillCompetencyLeadDto>> getCompetencyLeadByRequest(@PathVariable(name = "id") String requestId) {
        return GenericResponseDTO.<List<SkillCompetencyLeadDto>>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(commonService.findCompetencyLeadByRequest(requestId))
                .build();
    }
}
