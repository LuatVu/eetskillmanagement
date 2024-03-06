package com.bosch.eet.skill.management.usermanagement.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bosch.eet.skill.management.usermanagement.entity.UserToolRole;

public interface UserToolRoleRepository extends JpaRepository<UserToolRole, String>{

	void deleteByUserId(final String userId);
}
