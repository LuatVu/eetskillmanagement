/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.facade.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.facade.AuthenticationFacade;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LUK1HC
 *
 */

@Slf4j
@Component
public class AuthenticationFacadeImpl implements AuthenticationFacade {

	@Autowired
	private UserService userService;
	
	@Override
	public Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	@Override
	public UserDTO getAuthenticationUser() {
		UserDTO userDto = null;
		final Authentication auth = getAuthentication();
		userDto = userService.findActiveUserByName(auth.getName());

		return userDto;
	}
	
}
