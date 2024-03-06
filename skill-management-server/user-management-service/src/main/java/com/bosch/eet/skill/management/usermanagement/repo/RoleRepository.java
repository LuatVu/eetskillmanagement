/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.usermanagement.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bosch.eet.skill.management.usermanagement.entity.Role;

/**
 * @author LUK1HC
 *
 */

public interface RoleRepository extends JpaRepository<Role, String> {

	List<Role> findAllByStatus(final String status);
	
	@Query("SELECT r FROM Role r JOIN UserRole ur On r.id = ur.role.id WHERE ur.user.id = :userId AND r.status = :status")
	List<Role> findAllByUserId(@Param("userId") final String userId, @Param("status") final String status);
	
	List<Role> findByIdNotInAndStatus(final List<String> ids, final String status);

    List<Role> findByIdIn(List<String> roleIds);

    Optional<Role> findByNameAndStatus(String name, String active);

	Optional<Role> findByNameIgnoreCase(String groupName);

	Optional<Role> findByName(String groupName);
}
