/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.facade;

import org.springframework.security.core.Authentication;

import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;

/**
 * @author LUK1HC
 *
 */
public interface AuthenticationFacade {
	
	Authentication getAuthentication();
	
	UserDTO getAuthenticationUser();
	
}
