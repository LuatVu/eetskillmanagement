/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */
 
package com.bosch.eet.skill.management.ldap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.bosch.eet.skill.management.ldap.common.Constants;
import com.bosch.eet.skill.management.ldap.exception.LdapException;
import com.bosch.eet.skill.management.ldap.model.LdapInfo;
import com.bosch.eet.skill.management.ldap.service.LdapService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LUK1HC
 *
 */

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class LdapServiceTests {

	@Value("${ldap.username}")
	private String userNtId;
	
	@Value("${ldap.password}")
	private String password;
	
	@Autowired
	private LdapService ldapService;
	
	private final String DISPLAY_GROUP_NAME = "MS/EET12 Internal Employees";
	
	private final String INVALID = "Invalid";
	
	@Test	
	void testAuthenticate() throws Exception {
		assertThat(ldapService.authenticate(userNtId, password)).isTrue();
	}

	@Test
	void testAuthenticateWithInvalidDomain() throws LdapException {
		if (userNtId.contains(Constants.BACKSLASH)) {
			String area = userNtId.split(Pattern.quote(Constants.BACKSLASH))[0];
			userNtId = userNtId.replace(area, "invalid");
		} 
		
		LdapException exception = assertThrows(LdapException.class, () -> {
			ldapService.authenticate(userNtId, password);
	    });
		assertThat(exception.getMessage()).contains("Invalid username or password.");
	}
	
	@Test
	void testAuthenticateWithInvalidUserNtId() throws LdapException {
		LdapException exception = assertThrows(LdapException.class, () -> {
			ldapService.authenticate(INVALID, password);
	    });
		assertThat(exception.getMessage()).contains("Invalid username or password.");
	}

	@Test
	void testAuthenticateWithInvalidPassword() throws LdapException {
		LdapException exception = assertThrows(LdapException.class, () -> {
			ldapService.authenticate(userNtId, INVALID);
	    });
		assertThat(exception.getMessage()).contains("Invalid username or password.");
	}

	@Test	
	void testGetPrincipalInfo() throws Exception {
		if (userNtId.contains(Constants.BACKSLASH)) {
			userNtId = userNtId.split(Pattern.quote(Constants.BACKSLASH))[1];
		} 
		
		Optional<LdapInfo> result = ldapService.getPrincipalInfo(userNtId);
		assertThat(result).isPresent();
	}

	@Test
	void testGetGroupInfo() throws Exception {
		Optional<LdapInfo> result = ldapService.getGroupInfo(DISPLAY_GROUP_NAME);
		assertThat(result).isPresent();
	}

	@Test
	void testGetInvalidGroup() throws Exception {
		Optional<LdapInfo> result = ldapService.getGroupInfo(INVALID);
		assertThat(result).isNotPresent();
	}

	@Test
	void testSearch() throws Exception {
		List<LdapInfo> results = ldapService.search("luk1");
		assertThat(results).isNotEmpty();
		for (LdapInfo result : results) {
			log.info(result.toString());
		}
	}

	@Test
	void testSearchEmpty() throws Exception {
		List<LdapInfo> results = ldapService.search(INVALID);
		assertThat(results).isEmpty();
	}

	@Test
	void testSearchGroup() throws Exception {
		List<LdapInfo> results = ldapService.searchGroup(DISPLAY_GROUP_NAME);
		assertThat(results).isNotEmpty();
	}

	@Test
	void testSearchGroupEmpty() throws Exception {
		List<LdapInfo> results = ldapService.searchGroup(INVALID);
		assertThat(results).isEmpty();
	}

}
