package com.bosch.eet.skill.management.facade;

import java.util.Set;

import com.bosch.eet.skill.management.dto.PersonalProjectDto;



public interface PersonalFacade {
	
    PersonalProjectDto addNonBoschProject(String personalId, PersonalProjectDto personalProjectDto);
    
    void deletePersonalProject(PersonalProjectDto personalProjectDto, Set<String> authorities, String authNTID);

    PersonalProjectDto updatePersonalProject(String personalProjectId, PersonalProjectDto personalProjectDto);

}
