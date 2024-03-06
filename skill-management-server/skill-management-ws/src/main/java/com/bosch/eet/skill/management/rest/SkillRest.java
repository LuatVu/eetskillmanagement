package com.bosch.eet.skill.management.rest;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.AddSkillDto;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.PersonalSkillDto;
import com.bosch.eet.skill.management.dto.SkillExpectedLevelDto;
import com.bosch.eet.skill.management.dto.SkillGroupLevelDto;
import com.bosch.eet.skill.management.dto.SkillHighlightDto;
import com.bosch.eet.skill.management.dto.SkillManagementDto;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.service.SkillService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DUP5HC
 */

@RequestMapping
@RestController
@Slf4j
public class SkillRest {
	
	@Autowired
	private SkillService skillService;
	
	@Autowired
	private MessageSource messageSource;
	
	@GetMapping(value = Routes.URI_REST_SKILL)
	public GenericResponseDTO<List<String>> getListSkill() {
		return GenericResponseDTO.<List<String>>builder()
				.code(MessageCode.SUCCESS.toString())
				.data(skillService.findAllSkillsName())
				.build();
	}
	
	@GetMapping(value = Routes.URI_REST_SKILL_EXPERIENCE)
    public GenericResponseDTO<Map<String, SkillGroupLevelDto>> getListSkillExperience() {
		return GenericResponseDTO.<Map<String, SkillGroupLevelDto>>builder()
				.code(MessageCode.SUCCESS.toString())
				.data(skillService.findAllSkillLevel())
				.build();
	}
	
	@GetMapping(value = Routes.URI_REST_SKILL_EXPERIENCE_ID)
    public GenericResponseDTO<SkillGroupLevelDto> getSkillLevelBySkillId(@PathVariable(name = "id") String skillId) {
		return GenericResponseDTO.<SkillGroupLevelDto>builder()
				.code(MessageCode.SUCCESS.toString())
				.data(skillService.findSkillExperienceDetailBySkillId(skillId))
				.build();
	}	
	
//	Add new Personal Skill List
	@PostMapping(value = Routes.URI_REST_SKILL_UPLOAD)
	public GenericResponseDTO<List<PersonalSkillDto>> addNewSkills(
					@RequestBody AddSkillDto addSkillDto) {
		final List<PersonalSkillDto> newPersonalSkills = 
				skillService.addSkillsByListOfSkillGroups(addSkillDto);
		return GenericResponseDTO.<List<PersonalSkillDto>>builder()
				.code(MessageCode.SUCCESS.toString())
				.data(newPersonalSkills)
				.build();
	}

	// Edit skill
	@PostMapping(value = Routes.URI_REST_EDIT_SKILL)
	public GenericResponseDTO<String> editSkill(@PathVariable(name = "id") String skillId,
												@RequestBody @Valid SkillManagementDto skillManagementDto) {
        try {
            skillService.editNewSkill(skillManagementDto);
            return GenericResponseDTO.<String>builder()
                    .code(MessageCode.SUCCESS.toString())
                    .data("Skill updated successfully!")
                    .build();
        } catch (Exception e) {
            log.error(messageSource.getMessage(MessageCode.SKM_UPDATE_SKILL_FAIL.toString(),
                    null, LocaleContextHolder.getLocale()));
            return GenericResponseDTO.<String>builder()
                    .code(MessageCode.SKM_UPDATE_SKILL_FAIL.toString())
                    .build();
        }
	}

	// Delete skill
	@PostMapping(value = Routes.URI_REST_DELETE_SKILL)
	public GenericResponseDTO<String> deleteSkill(@PathVariable(name = "id") String skillId) {
		return GenericResponseDTO.<String>builder().code(MessageCode.SUCCESS.toString())
				.data(skillService.deleteSkill(skillId)).build();
	}
	
	// Add new skill management
	@PostMapping(value = Routes.URI_REST_SKILL)
	public GenericResponseDTO<String> addSkillManagement (
			@RequestBody @Valid SkillManagementDto skillManagementDto) {
		try {
			Skill skill = skillService.saveSkillManagement(skillManagementDto);
			return GenericResponseDTO.<String>builder()
					.code(MessageCode.SUCCESS.toString())
					.data(skill.getId())
					.build();
		} catch (Exception e) {
			log.error(e.toString());
			return GenericResponseDTO.<String>builder()
					.code(MessageCode.SKM_SKILL_ALREADY_EXISTS.toString())
					.build();
		}
	}
	
	//Get all skills with experience > 0
	@GetMapping(value = Routes.URI_REST_SKILL_HIGHLIGHT)
	public GenericResponseDTO<List<SkillHighlightDto>> findSkillsHighlight(@PathVariable String idPersonal){
		return GenericResponseDTO.<List<SkillHighlightDto>>builder()
				.code(MessageCode.SUCCESS.toString())
				.data(skillService.findSkillsHighlight(idPersonal))
				.build();
	}
	
	//save skill highlight
	@PostMapping(value = Routes.URI_REST_SAVE_SKILL_HIGHLIGHT)
	public GenericResponseDTO<List<PersonalSkillDto>> saveSkillsHighlight(@PathVariable String idPersonal, @RequestBody List<String> personalSkillDtos){
		return GenericResponseDTO.<List<PersonalSkillDto>>builder()
				.code(MessageCode.SUCCESS.toString())
				.data(skillService.saveSkillsHighlight(idPersonal, personalSkillDtos))
				.build();
	}
	
	//get list skill level
	@GetMapping(value = Routes.URI_REST_SKILL_EXPECTED)
	public GenericResponseDTO<Map<String, Object>> getSkillLevelExpected(@RequestParam(defaultValue = "0") Integer page
			, @RequestParam(defaultValue = "10") Integer size, @RequestParam(required = false) List<String> skillCluster){
		return GenericResponseDTO.<Map<String, Object>>builder().
				code(MessageCode.SUCCESS.toString())
				.data(skillService.getSkillLevelExpected(page, size, skillCluster))
				.build();
	}
	@PutMapping(value =Routes.URI_REST_SKILL_EXPECTED_UPDATE)
	public GenericResponseDTO<String> updateSkillLevel(@RequestBody List<SkillExpectedLevelDto> skillExpectedLevelDtos){
		return GenericResponseDTO.<String>builder()
				.code(MessageCode.SUCCESS.toString())
				.data(skillService.updateSkillLevel(skillExpectedLevelDtos))
				.build();
	}
}


