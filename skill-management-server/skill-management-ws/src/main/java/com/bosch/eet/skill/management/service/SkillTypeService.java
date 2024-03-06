/**
 * @author VOU6HC
 */

package com.bosch.eet.skill.management.service;

import java.util.List;

import com.bosch.eet.skill.management.dto.SkillTypeDto;
import com.bosch.eet.skill.management.entity.SkillType;

public interface SkillTypeService {

	List<SkillTypeDto> findAllSkillType();
	
	List<SkillType> findAll();
}
