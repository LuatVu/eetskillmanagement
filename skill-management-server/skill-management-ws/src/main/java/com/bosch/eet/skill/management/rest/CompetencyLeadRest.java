package com.bosch.eet.skill.management.rest;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.SkillCompetencyLeadDto;
import com.bosch.eet.skill.management.dto.SkillDto;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.service.CompetencyLeadService;

/**
 * @author TGI2HC
 */

@RestController
public class CompetencyLeadRest {

    @Autowired
    private CompetencyLeadService competencyLeadService;

    @Autowired
    private MessageSource messageSource;

    @PostMapping(value = Routes.URI_REST_COMPETENCY_LEAD)
    public GenericResponseDTO<String> createSkillForCompetencyLead(@RequestBody List<SkillCompetencyLeadDto> skillCompetencyLeadDtos) {
        if (Objects.isNull(skillCompetencyLeadDtos)) {
            throw new SkillManagementException(MessageCode.COMPETENCY_LEAD_REQUIRED.toString(),
                    messageSource.getMessage(MessageCode.COMPETENCY_LEAD_REQUIRED.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
        }
        int rs = competencyLeadService.save(skillCompetencyLeadDtos);
        if (rs == -1) {
            throw new SkillManagementException(MessageCode.COMPETENCY_LEAD_SAVE_SKILL_FAIL.toString(),
                    messageSource.getMessage(MessageCode.COMPETENCY_LEAD_SAVE_SKILL_FAIL.toString(), null,
                            LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
        }
        return GenericResponseDTO.<String>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .build();
    }
    
    @PostMapping(value = Routes.URI_REST_COMPETENCY_LEAD_DELETE)
    public GenericResponseDTO<String> deleteCompetencyLead(@RequestBody SkillCompetencyLeadDto skillCompetencyLeadDtos) {
    	if (Objects.isNull(skillCompetencyLeadDtos)) {
    		throw new SkillManagementException(MessageCode.COMPETENCY_LEAD_REQUIRED.toString(),
    				messageSource.getMessage(MessageCode.COMPETENCY_LEAD_REQUIRED.toString(), null,
    						LocaleContextHolder.getLocale()), null, NOT_FOUND);
    	}
    	competencyLeadService.deleteCompetencyLead(skillCompetencyLeadDtos);
    	return GenericResponseDTO.<String>builder()
    			.code(MessageCode.SUCCESS.toString())
    			.timestamps(new Date())
    			.build();
    }
    
    @GetMapping(value = Routes.URI_REST_COMPETENCY_LEAD)
    public GenericResponseDTO<List<SkillCompetencyLeadDto>> getAll() {
        return GenericResponseDTO.<List<SkillCompetencyLeadDto>>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(competencyLeadService.findAll())
                .build();
    }
    
//	Get Skills By CompetencyLead    
    @GetMapping(value = Routes.URI_REST_COMPETENCY_LEAD_ID)
    public GenericResponseDTO<List<SkillDto>> getSkillsByCompetencyLead(@PathVariable(name = "id") String competencyLeadId,
                                                                             @RequestParam(name = "search", required = false) String search,
                                                                             Pageable pageable) {
    	return GenericResponseDTO.<List<SkillDto>>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(competencyLeadService.findSkillsByCompetencyLeadId(competencyLeadId))
                .build();
    }
    
//	Get Competency lead list By skill group id    
    @GetMapping(value = Routes.URI_REST_COMPETENCY_LEAD_SKILL_GROUP_ID)
    public GenericResponseDTO<List<PersonalDto>> getCompetencyLeadBySkillGroupId(@RequestParam(name = "skill-group-id", required = false) String skillGroupId) {
    	return GenericResponseDTO.<List<PersonalDto>>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(competencyLeadService.findCompetencyLeadBySkillGroupId(skillGroupId))
                .build();
    }
    
}
