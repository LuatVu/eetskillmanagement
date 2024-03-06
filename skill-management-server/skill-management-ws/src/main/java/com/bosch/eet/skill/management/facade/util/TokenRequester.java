/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.facade.util;

import java.nio.charset.StandardCharsets;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.dto.AccessTokenRequest;
import com.bosch.eet.skill.management.dto.AccessTokenResponse;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LUK1HC
 *
 */

@Slf4j
@Component
public class TokenRequester {

	@Value("${server.port}")
	private String port;
	@Value("${server.servlet.context-path}")
	private String contextPath;
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MessageSource messageSource;

	public AccessTokenResponse requestAccessToken(AccessTokenRequest accessTokenRequest) {
		
		// Set Header
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", createAuthHeaderString(accessTokenRequest.getClientId(), accessTokenRequest.getClientSecret()));
        
        // Set a map of the key/value pairs that we want to supply in the body of the request
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", accessTokenRequest.getClientId()); 
        map.add("client_secret", accessTokenRequest.getClientSecret()); 
        map.add("username", accessTokenRequest.getUsername());
        map.add("password", accessTokenRequest.getPassword());
        map.add("grant_type", accessTokenRequest.getGrantType());
        map.add("refresh_token", accessTokenRequest.getRefreshToken());
        
        // Create an HttpEntity object, wrapping the body and headers of the request
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        try {
        	// Execute the request, as a POST, and expecting a TokenResponse object in return
			log.info("Url: {}", "http://localhost:" + port + "/" + contextPath + "/oauth/token");
			ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(
					"http://localhost:" + port + contextPath + "/oauth/token",
					HttpMethod.POST,
					entity,
					AccessTokenResponse.class);
			return response.getBody();
		} catch (Exception e) {
			log.error(e.toString());
			if (e.getMessage() != null && e.getMessage().contains("invalid_grant")) {
				final String code = MessageCode.INVALID_USERNAME_PASSWORD.name();
				final String message = messageSource.getMessage(MessageCode.INVALID_USERNAME_PASSWORD.toString(), null, LocaleContextHolder.getLocale());
				throw new SkillManagementException(code, message, null, HttpStatus.UNAUTHORIZED);
			}
			final String code = MessageCode.INTERNAL_SERVER_ERROR.name();
			final String message = messageSource.getMessage(MessageCode.INTERNAL_SERVER_ERROR.toString(), null, LocaleContextHolder.getLocale());
			throw new SkillManagementException(code, message, e.getCause(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	public AccessTokenResponse requestSSOAccessToken(AccessTokenRequest accessTokenRequest) {
		// Set Header
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Authorization", createAuthHeaderString(accessTokenRequest.getClientId(), accessTokenRequest.getClientSecret()));

		// Set a map of the key/value pairs that we want to supply in the body of the request
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("username", accessTokenRequest.getUsername());
		map.add("grant_type", accessTokenRequest.getGrantType());

		// Create an HttpEntity object, wrapping the body and headers of the request
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

		try {
			// Execute the request, as a POST, and expecting a TokenResponse object in return
			log.info("Url: {}", "http://localhost:" + port + "/" + contextPath + "/oauth/token");
			ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(
					"http://localhost:" + port + contextPath + "/oauth/token",
					HttpMethod.POST,
					entity,
					AccessTokenResponse.class);
			return response.getBody();
		} catch (Exception e) {
			log.error(e.toString());
			if (e.getMessage() != null && e.getMessage().contains("invalid_grant")) {
				final String code = MessageCode.INVALID_USERNAME_PASSWORD.name();
				final String message = messageSource.getMessage(MessageCode.INVALID_USERNAME_PASSWORD.toString(), null, LocaleContextHolder.getLocale());
				throw new SkillManagementException(code, message, null, HttpStatus.UNAUTHORIZED);
			}
			final String code = MessageCode.INTERNAL_SERVER_ERROR.name();
			final String message = messageSource.getMessage(MessageCode.INTERNAL_SERVER_ERROR.toString(), null, LocaleContextHolder.getLocale());
			throw new SkillManagementException(code, message, e.getCause(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	private AccessTokenResponse parseToken(OAuth2AccessToken oAuth2Token) {
		AccessTokenResponse output = new AccessTokenResponse();
		output.setAccessToken(oAuth2Token.getValue());
		output.setRefreshToken(oAuth2Token.getRefreshToken().getValue());
		output.setScope(oAuth2Token.getScope().toString());
		output.setTokenType(oAuth2Token.getTokenType());
		return output;
	}
	
	private String createAuthHeaderString(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);
        return authHeader;
    }
	
	public boolean validateAccessToken(final String baseUrl, final String accessToken) {
		boolean result = false;
		try {
			final String url = baseUrl + "/oauth/check_token?token=" + accessToken;
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

			if (response.getStatusCode().compareTo(HttpStatus.OK) == 0) {
				String body = response.getBody();
				if (body != null && body.contains(Constants.RESOURCE_ID)) {
					result = true;
				}
			} 
		} catch (Exception e) {
			log.error("Error validateAccessToken, detail: " + e.toString());
		}
		return result;
	}
	
}
