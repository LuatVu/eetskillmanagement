package com.bosch.eet.skill.management.facade;

import java.util.List;

import com.bosch.eet.skill.management.dto.SkillTagDto;



public interface SkillTagFacade {
	
	String replaceSkillTag(List<SkillTagDto> skillTagRequests);

}
