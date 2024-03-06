package com.bosch.eet.skill.management.facade.impl;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.converter.utils.ProjectConverterUtil;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.elasticsearch.repo.ProjectSpringElasticRepository;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.ProjectFacade;
import com.bosch.eet.skill.management.service.CustomerService;
import com.bosch.eet.skill.management.service.ProjectService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectFacadeImpl implements ProjectFacade {
    private final ProjectService projectService;
    private final CustomerService customerService;
    private final ProjectConverterUtil projectConverter;
    private final ProjectSpringElasticRepository projectSpringElasticRepo;
    private final MessageSource messageSource;

    @Override
	@Transactional
	public List<ProjectDto> importProject(String ntid,MultipartFile file){
    	List<Project> newProjectList = new ArrayList<>();
		List<ProjectDto> dupplicatedList = projectService.importProject(ntid, file, newProjectList);

    	try {
	    	if(dupplicatedList.size() == 0) {
			    projectSpringElasticRepo.saveAll(newProjectList.stream().map(projectConverter::convertToDocument).collect(Collectors.toList()));
				customerService.syncCustomerGBToElastic();
	    	}
	    } catch (Exception e) {
			log.error(e.getMessage());
			throw new SkillManagementException(MessageCode.SYNC_PROJECT_ELASTIC_FAIL.toString(),
	                messageSource.getMessage(MessageCode.SYNC_PROJECT_ELASTIC_FAIL.toString(), null,
	                        LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
		}
    	return dupplicatedList;
    }

    @Override
    @Transactional
    public ProjectDto save(ProjectDto projectDto) {
    	ProjectDto resultProjectDto = projectService.save(projectDto);

    	try {
	    	projectSpringElasticRepo.save(projectConverter.convertDTOtoDocument(resultProjectDto));
	    	customerService.syncCustomerGBToElastic();
    	} catch (Exception e) {
    		log.error(e.getMessage());
    		throw new SkillManagementException(MessageCode.SYNC_PROJECT_ELASTIC_FAIL.toString(),
                    messageSource.getMessage(MessageCode.SYNC_PROJECT_ELASTIC_FAIL.toString(), null,
                            LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
    	}
    	return resultProjectDto;
    }

    @Override
	@Transactional
	public boolean deleteProject(ProjectDto projectDto) {
    	ProjectDto resultProjectDto;
		resultProjectDto = projectService.deleteProject(projectDto);

    	try {
	    	projectSpringElasticRepo.deleteById(resultProjectDto.getId());
	    	customerService.syncCustomerGBToElastic();
	    } catch (Exception e) {
			log.error(e.getMessage());
			throw new SkillManagementException(MessageCode.SYNC_PROJECT_ELASTIC_FAIL.toString(),
	                messageSource.getMessage(MessageCode.SYNC_PROJECT_ELASTIC_FAIL.toString(), null,
	                        LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
		}
    	return true;
    }

	@Override
	@Transactional
	public ProjectDto editProjectPortfolio(ProjectDto projectDto) {
		Project resultProject;
		resultProject = projectService.editPortfolio(projectDto);

		try {
			projectSpringElasticRepo.save(projectConverter.convertToDocument(resultProject));
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new SkillManagementException(MessageCode.SYNC_PROJECT_ELASTIC_FAIL.toString(),
					messageSource.getMessage(MessageCode.SYNC_PROJECT_ELASTIC_FAIL.toString(), null,
							LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
		}
		return projectConverter.convertToProjectPortfolioDto(resultProject);
	}
}
