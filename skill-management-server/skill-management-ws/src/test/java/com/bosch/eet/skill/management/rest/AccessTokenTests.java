/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.common.CommonTestData;
import com.bosch.eet.skill.management.config.ConfigurationTests;
import com.bosch.eet.skill.management.dto.AccessTokenRequest;
import com.bosch.eet.skill.management.dto.AccessTokenResponse;
import com.bosch.eet.skill.management.facade.util.TokenRequester;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LUK1HC
 *
 */

@Slf4j
public class AccessTokenTests extends ConfigurationTests {

	private final String CLIENT_ID = "skm-ws";
	private final String CLIENT_SECRET = "Ld8ypvn#GVbTsG7e";
	
	private final String USERNAME = CommonTestData.USER_NT_ID_LUK1HC;
	private final String PASSWORD = "Lovelife287*";

	private final String GRANT_TYPE = "password";
	
	@Autowired
	private TokenRequester tokenRequester;
	
	@DisplayName("Test Get Token")
	@Test
	@Transactional	
	void testGetToken() throws Exception {
		
		AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
		accessTokenRequest.setBaseUrl(getBaseUrl());
		accessTokenRequest.setClientId(CLIENT_ID);
		accessTokenRequest.setClientSecret(CLIENT_SECRET);
		accessTokenRequest.setUsername(USERNAME);
		accessTokenRequest.setPassword(PASSWORD);
		accessTokenRequest.setGrantType(GRANT_TYPE);
		
		AccessTokenResponse accessTokenResponse = tokenRequester.requestAccessToken(accessTokenRequest);
		log.info(accessTokenResponse.toJson());
		assertTrue(true);
	}
	
	
	
}
