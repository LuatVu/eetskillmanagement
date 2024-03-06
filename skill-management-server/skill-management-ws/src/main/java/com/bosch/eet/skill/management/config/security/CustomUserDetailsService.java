/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.config.security;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LUK1HC
 *
 */

@Slf4j
@Service(value = "customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserService userService;

	public UserDetails loadUserByUsername(final String userName) throws UsernameNotFoundException {
		log.info("Calling CustomUserDetailsService.loadUserByUsername");
		UserDTO user = new UserDTO();
		// Validate a user exists in database
		user = userService.findActiveUserByName(userName);
		if (user == null || StringUtils.isEmpty(user.getName())) {
			log.error("User Not Found with username: " + userName);
			throw new UsernameNotFoundException("User Not Found.");
		}

		// Password is empty because requirement is: Do not store user's password. 
		return new User(user.getName(), "", getAuthorities(user.getId()));
	}

	public Collection<? extends GrantedAuthority> getAuthorities(final String userId) {
		Collection<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();
		try {
			final List<PermissionDTO> permissionsDto = userService.getUserPermissions(userId);
			
			authorities = permissionsDto
					.stream()
					.map(permission -> new SimpleGrantedAuthority(permission.getCode()))
					.collect(Collectors.toList());
			
		} catch (Exception e) {
			log.error("Error getAuthorities, " + e.getMessage());
		}
		return authorities;
	}
	

}
