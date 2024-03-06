/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;

import com.bosch.eet.skill.management.SkillManagementWsApplication;

/**
 *
 * @author LUK1HC
 */

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = SkillManagementWsApplication.class)
public abstract class ConfigurationTests {

	@LocalServerPort
	protected int randomServerPort;
	
	@Autowired
	protected TestRestTemplate restTemplate;
	
	public HttpHeaders createBasicHeaders() {
		return new HttpHeaders() {
			{
				set("Content-Type", "application/json");
			}
		};
	}

	public HttpHeaders createOAuth2Headers() {
		return new HttpHeaders() {
			{
				set("Content-Type", "application/json");
			}
		};
	}

	public HttpHeaders createHeaders(final String ivUser, final String token) {
		return new HttpHeaders() {
			{
				String authHeader = "Bearer  " + token;
				set("Content-Type", "application/json");
				set("Authorization", authHeader);
				set("iv-user", ivUser);				
			}
		};
	}
	
	public String getBaseUrl() {
		return "http://localhost:" + randomServerPort + "/skm-ws";
	}
	
}
