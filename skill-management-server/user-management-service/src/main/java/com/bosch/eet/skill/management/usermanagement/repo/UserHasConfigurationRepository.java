package com.bosch.eet.skill.management.usermanagement.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bosch.eet.skill.management.usermanagement.entity.UserHasConfiguration;

public interface UserHasConfigurationRepository extends JpaRepository<UserHasConfiguration, String>{

	UserHasConfiguration findByConfigurationIdAndUserId(final String configurationId, final String userId);
	
	List<UserHasConfiguration> findByConfigurationIdAndIsChecked(final String configurationId, final boolean isChecked);
}
