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
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bosch.eet.skill.management.usermanagement.entity.User;

/**
 * @author LUK1HC
 *
 */

public interface UserRepository extends JpaRepository<User, String> {

	boolean existsByName(String userName);

	List<User> findAllByStatus(final String status);
	
	Optional<User> findByNameAndStatus(final String name, final String status);

	Optional<User> findByName(final String name);

    List<User> findByIdIn(List<String> ids);

	List<User> findAllByNameLike(String phrase);

	Optional<User> findByDisplayName(final String displayName);

	List<User> findByNameIn(Set<String> ntIds);

//	@Query("select count(u) from User u" +
//			" join Personal p on " +
//			" where u.displayName not like 'MS/EET%'" +
//			" and u.displayName not like 'System%'")
//	Integer countAssociates(String team);

//	Integer countByDisplayNameStartsWith(final String name);
}
