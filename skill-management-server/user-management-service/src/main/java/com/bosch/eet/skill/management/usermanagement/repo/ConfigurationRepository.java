package com.bosch.eet.skill.management.usermanagement.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bosch.eet.skill.management.usermanagement.entity.Configuration;

public interface ConfigurationRepository extends JpaRepository<Configuration, String>{
	
	@Query(value = "SELECT * FROM configuration c LEFT JOIN (SELECT * FROM user_has_configuration WHERE user_id = :userId) uhc "
			+ "ON uhc.configuration_id = c.id", nativeQuery = true)
	List<Configuration> findAllByUserId(@Param("userId") final String userId);
}
