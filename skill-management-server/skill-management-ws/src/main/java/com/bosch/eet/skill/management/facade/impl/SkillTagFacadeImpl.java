package com.bosch.eet.skill.management.facade.impl;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.converter.utils.ProjectConverterUtil;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.SkillTagDto;
import com.bosch.eet.skill.management.elasticsearch.document.DepartmentProjectDocument;
import com.bosch.eet.skill.management.elasticsearch.repo.ProjectSpringElasticRepository;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.SkillTagFacade;
import com.bosch.eet.skill.management.service.CustomerService;
import com.bosch.eet.skill.management.service.ProjectService;
import com.bosch.eet.skill.management.service.SkillTagService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillTagFacadeImpl implements SkillTagFacade {
    private final ProjectService projectService;
    private final CustomerService customerService;
    private final ProjectConverterUtil projectConverter;
    private final ProjectSpringElasticRepository projectSpringElasticRepo;
    private final SkillTagService skillTagService;
    private final MessageSource messageSource;
   
    @Override
	public String replaceSkillTag(List<SkillTagDto> skillTagRequests) {
    	String resultMsg;
    	try {
    		resultMsg = skillTagService.replaceSkillTag(skillTagRequests);
    	} catch (SkillManagementException skillManagementException) {
    		throw skillManagementException;
    	} catch (Exception e) {
    		log.error(e.getMessage());
    		throw new SkillManagementException(MessageCode.SAVING_PROJECT_FAIL.toString(),
                    messageSource.getMessage(MessageCode.SAVING_PROJECT_FAIL.toString(), null,
                            LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
    	}
    	
		List<ProjectDto> dbProjectList = projectService.findAll(Pageable.unpaged(), new HashMap<String, String>()).getContent();
		List<DepartmentProjectDocument> documentList = dbProjectList.stream().map(projectConverter::convertDTOtoDocument).collect(Collectors.toList());
		projectSpringElasticRepo.saveAll(documentList);
		customerService.syncCustomerGBToElastic();
    	return resultMsg;
    }

}
