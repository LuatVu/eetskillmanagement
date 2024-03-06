package com.bosch.eet.skill.management.facade.impl;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.converter.utils.ProjectConverterUtil;
import com.bosch.eet.skill.management.dto.PersonalProjectDto;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.elasticsearch.repo.ProjectSpringElasticRepository;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.PersonalFacade;
import com.bosch.eet.skill.management.service.PersonalService;
import com.bosch.eet.skill.management.service.ProjectService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalFacadeImpl implements PersonalFacade {
    private final ProjectService projectService;
    private final PersonalService personalService;
    private final ProjectConverterUtil projectConverter;
    private final ProjectSpringElasticRepository projectSpringElasticRepo;
    private final MessageSource messageSource;
   
// Add non-Bosch project - assign Bosch project
    @Override
    @Transactional
    public PersonalProjectDto addNonBoschProject(String personalId, PersonalProjectDto personalProjectDto) {
    	PersonalProjectDto resultDto = personalService.addPersonalProject(personalId, personalProjectDto);
    	
    	try {
	    	ProjectDto newProjectDto = projectService.findById(resultDto.getProjectId());
	    	projectSpringElasticRepo.save(projectConverter.convertDTOtoDocument(newProjectDto));
	    } catch (Exception e) {
			log.error(e.getMessage());
			throw new SkillManagementException(MessageCode.SYNC_PROJECT_ELASTIC_FAIL.toString(),
	                messageSource.getMessage(MessageCode.SYNC_PROJECT_ELASTIC_FAIL.toString(), null,
	                        LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
		}
    	return resultDto;
    }
    
    @Override
	@Transactional
    public void deletePersonalProject(PersonalProjectDto personalProjectDto, Set<String> authorities, String authNTID) {
		personalService.deletePersonalProject(personalProjectDto, authorities, authNTID);;

    	try {
	    	String projectType = personalProjectDto.getProjectType();
	    	if(StringUtils.isNotBlank(projectType) && projectType.equalsIgnoreCase(Constants.NONBOSCH)) {
	    		projectSpringElasticRepo.deleteById(personalProjectDto.getProjectId());
	    	}
	    } catch (Exception e) {
			log.error(e.getMessage());
			throw new SkillManagementException(MessageCode.SYNC_PROJECT_ELASTIC_FAIL.toString(),
	                messageSource.getMessage(MessageCode.SYNC_PROJECT_ELASTIC_FAIL.toString(), null,
	                        LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
		}
    }
    
    @Override
	@Transactional
    public PersonalProjectDto updatePersonalProject(String personalProjectId, PersonalProjectDto personalProjectDto) {
    	PersonalProjectDto resultDto = personalService.updatePersonalProject(personalProjectId, personalProjectDto);
    	
    	try {
	    	ProjectDto updatedProjectDto = projectService.findById(resultDto.getProjectId());
	    	projectSpringElasticRepo.save(projectConverter.convertDTOtoDocument(updatedProjectDto));
	    } catch (Exception e) {
			log.error(e.getMessage());
			throw new SkillManagementException(MessageCode.SYNC_PROJECT_ELASTIC_FAIL.toString(),
	                messageSource.getMessage(MessageCode.SYNC_PROJECT_ELASTIC_FAIL.toString(), null,
	                        LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
		}
    	return resultDto;
    }

}
