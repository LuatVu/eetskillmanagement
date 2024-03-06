package com.bosch.eet.skill.management.usermanagement.service;

import java.util.List;

import com.bosch.eet.skill.management.usermanagement.dto.ConfigurationCheckDto;
import com.bosch.eet.skill.management.usermanagement.dto.ConfigurationDto;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementException;

public interface ConfigurationService {

	List<ConfigurationDto> findAllByUserId(String userId) throws UserManagementException;
	
	void check(ConfigurationCheckDto dto) throws UserManagementException;
}
