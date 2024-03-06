/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.cache.AppCacheManager;
import com.bosch.eet.skill.management.common.CommonTestData;
import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.PasswordHelper;
import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.config.ConfigurationTests;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.LoginRequest;
import com.bosch.eet.skill.management.dto.LoginResponse;
import com.bosch.eet.skill.management.dto.register.RequestAccessDTO;
import com.bosch.eet.skill.management.exception.ErrorMessage;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LUK1HC
 *
 */

@Slf4j
class UserRestTests extends ConfigurationTests {

	private final String CLIENT_ID = "skm-ws";
	private final String CLIENT_SECRET = "Ld8ypvn#GVbTsG7e";
			
	private final String USERNAME = CommonTestData.USER_ID_SYSTEM_TEST;
	private final String PASSWORD = PasswordHelper.decrypt(CommonTestData.USER_ID_SYSTEM_TEST);
	
	@Autowired
	private AppCacheManager cacheManager;
	
	@DisplayName("Test login integration")
	@Test
	@Transactional
	void testLogin() throws Exception {

		// Set Header
		final HttpHeaders headers = buildHeader();
		
		// Login
		final String url = getBaseUrl() + Routes.URI_REST_LOGIN;
		final LoginRequest dummyData = buildDummyData();
		log.info("LoginRequest:" + dummyData.toJson());
		final HttpEntity<LoginRequest> request = new HttpEntity<>(dummyData, headers);
		ResponseEntity<LoginResponse> response = restTemplate.exchange(url, HttpMethod.POST, request, LoginResponse.class);
		log.info("LoginResponse:" + response.getBody().toJson());
		assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode()); 
		assertNotNull(response.getBody());
		assertNotNull(response.getBody().getId());
	}
	
	@DisplayName("Test login with invalid client-id")
	@Test
	@Transactional
	void testLoginWithInvalidClientId() throws Exception {

		// Set Header
		final HttpHeaders headers = createBasicHeaders();
		headers.set("client-id", "invalid");
		headers.set("client-secret", CLIENT_SECRET);
		
		// Login
		final String url = getBaseUrl() + Routes.URI_REST_LOGIN;
		final LoginRequest dummyData = buildDummyData();
		log.info("LoginRequest:" + dummyData.toJson());
		final HttpEntity<LoginRequest> request = new HttpEntity<>(dummyData, headers);
		ResponseEntity<LoginResponse> response = restTemplate.exchange(url, HttpMethod.POST, request, LoginResponse.class);
		assertThat((HttpStatus.INTERNAL_SERVER_ERROR)).isEqualTo(response.getStatusCode());       
	}

	@DisplayName("Test login with invalid client-secret")
	@Test
	@Transactional
	void testLoginWithInvalidClientSecret() throws Exception {

		// Set Header
		final HttpHeaders headers = createBasicHeaders();
		headers.set("client-id", CLIENT_ID);
		headers.set("client-secret", "invalid");
		
		// Login
		final String url = getBaseUrl() + Routes.URI_REST_LOGIN;
		final LoginRequest dummyData = buildDummyData();
		log.info("LoginRequest:" + dummyData.toJson());
		final HttpEntity<LoginRequest> request = new HttpEntity<>(dummyData, headers);
		ResponseEntity<LoginResponse> response = restTemplate.exchange(url, HttpMethod.POST, request, LoginResponse.class);
		assertThat((HttpStatus.INTERNAL_SERVER_ERROR)).isEqualTo(response.getStatusCode());   
	}
	
	@DisplayName("Test login integration with empty username")
	@Test
	@Transactional
	void testLoginWithEmptyUserName() throws Exception {

		// Set Header
		final HttpHeaders headers = buildHeader();
		
		// Login
		final String url = getBaseUrl() + Routes.URI_REST_LOGIN;
		LoginRequest dummyData = buildDummyData();
		dummyData.setUsername("");
		log.info("LoginRequest:" + dummyData.toJson());
		final HttpEntity<LoginRequest> request = new HttpEntity<>(dummyData, headers);
		ResponseEntity<SkillManagementException> response = restTemplate.exchange(url, HttpMethod.POST, request, SkillManagementException.class);
		assertThat((HttpStatus.INTERNAL_SERVER_ERROR)).isEqualTo(response.getStatusCode());   
		assertThat((MessageCode.INVALID_USERNAME_PASSWORD.name())).isEqualTo(response.getBody().getCode());   
	}

	@DisplayName("Test login integration with empty password")
	@Test
	@Transactional
	void testLoginWithEmptyPassword() throws Exception {

		// Set Header
		final HttpHeaders headers = buildHeader();
		
		// Login
		final String url = getBaseUrl() + Routes.URI_REST_LOGIN;
		LoginRequest dummyData = buildDummyData();
		dummyData.setPassword("");
		log.info("LoginRequest:" + dummyData.toJson());
		final HttpEntity<LoginRequest> request = new HttpEntity<>(dummyData, headers);
		ResponseEntity<SkillManagementException> response = restTemplate.exchange(url, HttpMethod.POST, request, SkillManagementException.class);
		assertThat((HttpStatus.INTERNAL_SERVER_ERROR)).isEqualTo(response.getStatusCode());   
		assertThat((MessageCode.INVALID_USERNAME_PASSWORD.name())).isEqualTo(response.getBody().getCode());   
	}

	@DisplayName("Test login integration with username is not exist")
	@Test
	@Transactional
	void testLoginWithUsernameIsNotExist() throws Exception {

		// Set Header
		final HttpHeaders headers = buildHeader();
		
		// Login
		final String url = getBaseUrl() + Routes.URI_REST_LOGIN;
		LoginRequest dummyData = buildDummyData();
		dummyData.setUsername("NotExist");
		log.info("LoginRequest:" + dummyData.toJson());
		final HttpEntity<LoginRequest> request = new HttpEntity<>(dummyData, headers);
		ResponseEntity<SkillManagementException> response = restTemplate.exchange(url, HttpMethod.POST, request, SkillManagementException.class);
		assertThat((HttpStatus.INTERNAL_SERVER_ERROR)).isEqualTo(response.getStatusCode());   
		assertThat((MessageCode.NOT_EXISTED_USERNAME.name())).isEqualTo(response.getBody().getCode());   
	}

	@DisplayName("Test login integration with invalid password")
	@Test
	@Transactional
	void testLoginWithInvalidpassword() throws Exception {

		// Set Header
		final HttpHeaders headers = buildHeader();
		
		// Login
		final String url = getBaseUrl() + Routes.URI_REST_LOGIN;
		LoginRequest dummyData = buildDummyData();
		dummyData.setPassword("Invalid");
		log.info("LoginRequest:" + dummyData.toJson());
		final HttpEntity<LoginRequest> request = new HttpEntity<>(dummyData, headers);
		ResponseEntity<SkillManagementException> response = restTemplate.exchange(url, HttpMethod.POST, request, SkillManagementException.class);
		assertThat((HttpStatus.INTERNAL_SERVER_ERROR)).isEqualTo(response.getStatusCode());   
		assertThat((MessageCode.INVALID_USERNAME_PASSWORD.name())).isEqualTo(response.getBody().getCode());   
	}

	@DisplayName("Test login with cached")
	@Test
	@Transactional
	void testLoginWithCached() throws Exception {

		// Set Header
		final HttpHeaders headers = buildHeader();
		
		// Login
		final String url = getBaseUrl() + Routes.URI_REST_LOGIN;
		final LoginRequest dummyData = buildDummyData();
		log.info("LoginRequest:" + dummyData.toJson());
		final HttpEntity<LoginRequest> request = new HttpEntity<>(dummyData, headers);
		ResponseEntity<LoginResponse> response = restTemplate.exchange(url, HttpMethod.POST, request, LoginResponse.class);
		log.info("LoginResponse:" + response.getBody().toJson());
		assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
		assertNotNull(response.getBody());
		assertNotNull(response.getBody().getId());
		
		// Verify cache
		Cache cache = cacheManager.getCache(Constants.CREDENTIALS_CACHE_NAME);
		@SuppressWarnings("unchecked")
		List<String> value = (List<String>) cache.get(USERNAME).get();
		assertThat(PASSWORD).isEqualTo(PasswordHelper.decrypt(value.get(0)));
		log.info(PasswordHelper.decrypt(value.get(0)));
		
	}
	
	private LoginRequest buildDummyData() {
		LoginRequest request = new LoginRequest();
		request.setUsername(USERNAME);
		request.setPassword(PASSWORD);
		return request;
	}

	private HttpHeaders buildHeader() {
		final HttpHeaders headers = createBasicHeaders();
		headers.set("client-id", CLIENT_ID);
		headers.set("client-secret", CLIENT_SECRET);
		
		return headers;
	}

	@DisplayName("Test request access with username is not exist in LDAP")
	@Test
	@Transactional
	void testUserRequestAccess_whenUserNotFoundInLDAP_returnInternalServerError(){
		// Request Access
		final String url = getBaseUrl() + Routes.URI_REST_REQUEST_ACCESS;
		final RequestAccessDTO dummyData = RequestAccessDTO.builder()
				.username(CommonTestData.USER_NT_USER_NOT_IN_LDAP)
				.reason("test")
				.build();
		log.info("RequestAccessDTO:" + dummyData.toJson());
		final HttpEntity<RequestAccessDTO> request = new HttpEntity<>(dummyData);
		ResponseEntity<ErrorMessage> response = restTemplate.exchange(url, HttpMethod.POST, request, ErrorMessage.class);
		log.info("ErrorMessage:" + Objects.requireNonNull(response.getBody()).toJson());

		assertThat(HttpStatus.INTERNAL_SERVER_ERROR).isEqualTo(response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@DisplayName("Test request access happy case")
	@Test
	@Transactional
	void testUserRequestAccess_happyCase(){
		// Request Access
		final String url = getBaseUrl() + Routes.URI_REST_REQUEST_ACCESS;
		final RequestAccessDTO dummyData = RequestAccessDTO.builder()
				.username(CommonTestData.USER_NT_ID_MAC9HC)
				.reason("test")
				.build();
		log.info("RequestAccessDTO:" + dummyData.toJson());
		final HttpEntity<RequestAccessDTO> request = new HttpEntity<>(dummyData);
		ResponseEntity<GenericResponseDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, GenericResponseDTO.class);
		log.info("GenericResponseDTO:" + Objects.requireNonNull(response.getBody()).toJson());

		assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
		assertNotNull(response.getBody());
		assertThat(MessageCode.SEND_SUCCESS.toString()).isEqualTo(response.getBody().getCode());
	}

	@DisplayName("Test request access happy case")
	@Test
	@Transactional
	void testUserRequestAccess_whenSaveToDBFail_returnInternalServerError(){
		// Request Access
		final String url = getBaseUrl() + Routes.URI_REST_REQUEST_ACCESS;
		final RequestAccessDTO dummyData = RequestAccessDTO.builder()
				.username(CommonTestData.USER_NT_ID_LUK1HC)
				.reason("test")
				.build();
		log.info("RequestAccessDTO:" + dummyData.toJson());
		final HttpEntity<RequestAccessDTO> request = new HttpEntity<>(dummyData);
		ResponseEntity<GenericResponseDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, GenericResponseDTO.class);
		log.info("GenericResponseDTO:" + Objects.requireNonNull(response.getBody()).toJson());

		assertThat(HttpStatus.INTERNAL_SERVER_ERROR).isEqualTo(response.getStatusCode());
		assertNotNull(response.getBody());
	}
}
