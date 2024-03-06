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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.ldap.service.LdapService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LUK1HC
 *
 */

@Slf4j
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private LdapService ldapService;
	
	@Autowired
	private MessageSource messageSource;
	
	@SuppressWarnings("unchecked")
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		log.info("Calling CustomAuthenticationProvider.authenticate().");
		Collection<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();
		final String userName = authentication.getName();
		final String password = authentication.getCredentials().toString();
		
		try {
			// Validate a user exists in database
			UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
			if (userDetails != null) {
				// Validate a user by LDAP System
				boolean result = ldapService.authenticate(userName, password);
				if (!result) {
					final String message = messageSource.getMessage(MessageCode.NOT_AUTHORIZATION.toString(), null, LocaleContextHolder.getLocale());
					throw new BadCredentialsException(message);
				}
				
				// Get user's permissions
				authorities = (Collection<GrantedAuthority>) userDetails.getAuthorities();
			}
		} catch (Exception e) {
			log.error("BadCredentialsException. Details: " + e.getMessage());
			throw new BadCredentialsException(e.getMessage(), e);
		}
		return new UsernamePasswordAuthenticationToken(userName, password, authorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
	
}
