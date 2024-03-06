/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.usermanagement.repo;

import org.springframework.data.repository.CrudRepository;

import com.bosch.eet.skill.management.usermanagement.entity.UserRole;

/**
 * @author LUK1HC
 *
 */

public interface UserRoleRepository extends CrudRepository<UserRole, String> {

}
