package com.bosch.eet.skill.management.rest;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.FileStorageDTO;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.SimplePersonalProjectDto;
import com.bosch.eet.skill.management.dto.response.ImportProjectResponseDTO;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.ProjectFacade;
import com.bosch.eet.skill.management.service.ProjectService;

import io.github.jhipster.web.util.PaginationUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author TGI2HC
 */

@RestController
@Slf4j
public class ProjectRest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private ProjectFacade projectFacade;

    @GetMapping(value = Routes.URI_REST_PROJECT)
    public ResponseEntity<List<ProjectDto>> getProjects(@RequestParam Map<String, String> filterParams,
                                                        Pageable pageable, Authentication auht) {
        Page<ProjectDto> projectsDto = projectService.findAll(pageable, filterParams);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), projectsDto);
        return ResponseEntity.ok().headers(headers).body(projectsDto.getContent());
    }
    
    @GetMapping(value = Routes.URI_REST_PROJECT_DROP_DOWN)
    public ResponseEntity<List<ProjectDto>> getProjectsForDropdown(@RequestParam Map<String, String> filterParams,
                                                        Authentication auth) {
        List<ProjectDto> projectsDto = projectService.findAllForDropdown(filterParams);
        return ResponseEntity.ok().body(projectsDto);
    }

    @GetMapping(value = Routes.URI_REST_PROJECT_ID)
    public GenericResponseDTO<ProjectDto> getProject(@PathVariable(name = "project_id") String projectId) {
        return GenericResponseDTO.<ProjectDto>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(projectService.findById(projectId))
                .timestamps(new Date())
                .build();
    }

    @PostMapping(value = Routes.URI_REST_UPDATE_PROJECT)
    public GenericResponseDTO<ProjectDto> updateProject(@PathVariable(name = "project_id") String projectId,
                                                        @RequestBody @Valid ProjectDto projectDto,
                                                        Authentication auht) {
        if (Objects.isNull(projectId)) {
            throw new SkillManagementException(MessageCode.PROJECT_ID_REQUIRED.toString(),
                    messageSource.getMessage(MessageCode.PROJECT_ID_REQUIRED.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
        }
        projectDto.setId(projectId);
        projectDto.setCreatedBy(auht.getName());
        return GenericResponseDTO.<ProjectDto>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(projectFacade.save(projectDto))
                .timestamps(new Date())
                .build();
    }

    @PostMapping(value = Routes.URI_REST_PROJECT)
    public GenericResponseDTO<ProjectDto> createProject(@RequestBody @Valid ProjectDto projectDto, Authentication auht) {
        projectDto.setCreatedBy(auht.getName());
        return GenericResponseDTO.<ProjectDto>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(projectFacade.save(projectDto))
                .timestamps(new Date())
                .build();
    }

    @PostMapping(value = Routes.URI_REST_DELETE_PROJECT)
    public GenericResponseDTO<ProjectDto> deleteProject(@PathVariable(name = "project_id") String projectId) {
        ProjectDto findProjectDto = projectService.findById(projectId);
        if (!Objects.isNull(findProjectDto)) {
            boolean result = projectFacade.deleteProject(findProjectDto);
            if (result == true) {
                return GenericResponseDTO.<ProjectDto>builder()
                        .code(MessageCode.SUCCESS.toString())
                        .timestamps(new Date())
                        .build();
            } else {
                throw new SkillManagementException(MessageCode.ERROR_DELETING_PROJECT.toString(),
                        messageSource.getMessage(MessageCode.ERROR_DELETING_PROJECT.toString(), null,
                                LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new SkillManagementException(MessageCode.PROJECT_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.PROJECT_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
        }
    }
    
    @GetMapping(value = Routes.URI_REST_PROJECT_FILTER)
    public GenericResponseDTO<HashMap<String, Object>> getProjectFilter() {
        return GenericResponseDTO.<HashMap<String, Object>>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(projectService.getFilter())
                .timestamps(new Date())
                .build();
    }
    @PostMapping(value=Routes.URI_REST_IMPORT_PROJECT)
    public GenericResponseDTO<ImportProjectResponseDTO> importProject(@PathVariable String ntid,@RequestParam(name = "file") MultipartFile file){
        
        ImportProjectResponseDTO responseDTO = ImportProjectResponseDTO.builder()
                .existedProjectList(projectFacade.importProject(ntid,file))
                .build();
        
        if(responseDTO.getExistedProjectList().isEmpty()) {
            return GenericResponseDTO.<ImportProjectResponseDTO>builder()
                    .code(MessageCode.SUCCESS.toString())
                    .data(responseDTO).build();
        } else {
            return GenericResponseDTO.<ImportProjectResponseDTO>builder()
                    .code(MessageCode.IMPORT_FAIL.toString())
                    .data(responseDTO).build();
        }
    }

	@PostMapping(value = Routes.URI_REST_EDIT_ADDITIONAL_TASK)
	public GenericResponseDTO<String> updateAdditionalTask(
			@RequestBody @Valid SimplePersonalProjectDto simplePersonalProjectDto, Authentication auth) {
		String nttid = auth.getName();
		Set<String> authSet = new HashSet<>(
				auth.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList()));
		return GenericResponseDTO.<String>builder().code(MessageCode.SUCCESS.toString())
				.data(projectService.updateAdditionalTask(nttid, simplePersonalProjectDto, authSet))
				.timestamps(new Date()).build();
	}
	
	@PostMapping(value = Routes.URI_REST_PROJECT_PORTFOLIO_DETAIL)
	public GenericResponseDTO<FileStorageDTO> updateInfoPortfolio(@RequestParam(name = "file") MultipartFile file,
			@PathVariable("project_id") String projectId, @PathVariable("layout") String layout) {
		return GenericResponseDTO.<FileStorageDTO>builder().code(MessageCode.SUCCESS.toString())
				.data(projectService.updateInfoPortfolio(projectId, file, layout)).build();
	}
	
	@GetMapping(value = Routes.URI_REST_PROJECT_PORTFOLIO_DETAIL)
	public GenericResponseDTO<String> getInfoPortfolio(@PathVariable("project_id") String projectId,
			@PathVariable("layout") String layout) {
		return GenericResponseDTO.<String>builder().code(MessageCode.SUCCESS.toString())
				.data(projectService.getInforPortfolio(projectId, layout)).build();
	}

	@GetMapping(value = Routes.UIR_REST_PROJECT_PORTFOLIO)
	public GenericResponseDTO<ProjectDto> getProjectPortfolio(@PathVariable(name = "project_id") String projectId) {
		return GenericResponseDTO.<ProjectDto>builder()
                .code(MessageCode.SUCCESS.toString())
				.data(projectService.getPortfolio(projectId))
                .build();
	}

	@PostMapping(value = Routes.UIR_REST_PROJECT_PORTFOLIO)
	public GenericResponseDTO<ProjectDto> editProjectPortfolio(@RequestBody ProjectDto projectDto) {
		return GenericResponseDTO.<ProjectDto>builder()
                .code(MessageCode.SUCCESS.toString())
				.data(projectFacade.editProjectPortfolio(projectDto))
                .build();
	}
}
