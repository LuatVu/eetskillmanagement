/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.rest;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.LoginRequest;
import com.bosch.eet.skill.management.dto.LoginResponse;
import com.bosch.eet.skill.management.dto.RefreshTokenRequest;
import com.bosch.eet.skill.management.dto.register.RequestAccessDTO;
import com.bosch.eet.skill.management.dto.response.SearchUserResponseDTO;
import com.bosch.eet.skill.management.dto.response.UserSession;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.UserFacade;
import com.bosch.eet.skill.management.ldap.exception.LdapException;
import com.bosch.eet.skill.management.ldap.model.LdapInfo;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LUK1HC
 */

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class UserRest {

    private final UserFacade userFacade;

    private final MessageSource messageSource;

    private final ConsumerTokenServices tokenServices;
    
	@Autowired
	private Environment env;

    @PostMapping(value = Routes.URI_REST_LOGIN)
    public GenericResponseDTO<LoginResponse> login(
            HttpServletRequest requestHeader,
            @RequestHeader("client-id") final String clientId,
            @RequestHeader("client-secret") final String clientSecret,
            @RequestBody LoginRequest request) throws UserManagementException, LdapException, SkillManagementException {

        StopWatch watch = new StopWatch();
        watch.start();
        log.info("iv-user: {}", requestHeader.getHeader("iv-user"));
        // Validate iv_user
        if (StringUtils.isEmpty(clientId) || StringUtils.isEmpty(clientSecret)) {
            final String code = MessageCode.INVALID_CLIENT_ID_SECRET.name();
            final String message = messageSource.getMessage(MessageCode.INVALID_CLIENT_ID_SECRET.toString(), null, LocaleContextHolder.getLocale());
            throw new SkillManagementException(code, message, null, HttpStatus.UNAUTHORIZED);
        }

        // Validate input
        if (StringUtils.isBlank(request.getUsername()) || StringUtils.isBlank(request.getPassword())) {
            final String code = MessageCode.INVALID_USERNAME_PASSWORD.name();
            final String message = messageSource.getMessage(MessageCode.INVALID_USERNAME_PASSWORD.toString(), null, LocaleContextHolder.getLocale());
            throw new SkillManagementException(code, message, null, HttpStatus.UNAUTHORIZED);
        }

        LoginResponse response = userFacade.login(clientId, clientSecret, request);

        watch.stop();
//        log.info("LoginResponse:" + response.toJson());
        log.info("Login API took " + watch.getTotalTimeMillis() + "ms ~= " + watch.getTotalTimeSeconds() + "s ~= ");
        return GenericResponseDTO.<LoginResponse>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(response)
                .build();
    }

    @GetMapping(value = Routes.URI_REST_LOGIN_SSO)
    public GenericResponseDTO<LoginResponse> loginSSO(HttpServletRequest request, @RequestParam Map<String, String> parameters) throws UserManagementException, LdapException {
        StopWatch watch = new StopWatch();
        watch.start();
        log.info("iv-user: {}", request.getHeader(Constants.HEADER_ATTRIBUTE_IV_USER));
        log.info("clientId: {}", request.getHeader(Constants.HEADER_ATTRIBUTE_CLIENT_ID));
        log.info("clientSecret:  {}", request.getHeader(Constants.HEADER_ATTRIBUTE_CLIENT_SECRET));
        log.info("x_forwarded_for:  {}", request.getHeader(Constants.HEADER_ATTRIBUTE_X_FORWARDED_FOR));
        LoginResponse response = userFacade.loginSSO(request, parameters);
        watch.stop();
//        log.info("LoginResponse:" + response.toJson());
        log.info("Login API loginSSO took " + watch.getTotalTimeMillis() + "ms ~= " + watch.getTotalTimeSeconds() + "s ~= ");
        return GenericResponseDTO.<LoginResponse>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(response)
                .build();
    }

    @GetMapping(value = Routes.URI_REST_VALIDATE_TOKEN)
    public Map<String, String> validateAccessToken(final HttpServletRequest request) {
        boolean result = false;
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.contains("Bearer")) {
            final String tokenId = authorization.replace("Bearer", "").trim();
            result = userFacade.validateAccessToken(tokenId);
        }
        return Collections.singletonMap("status", String.valueOf(result));
    }

    @GetMapping(value = Routes.URI_REST_LOGOUT)
    public Map<String, String> logout(final HttpServletRequest request) throws SkillManagementException {
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.contains("Bearer")) {
            final String tokenId = authorization.replace("Bearer", "").trim();
            tokenServices.revokeToken(tokenId);
        }
        return Collections.singletonMap("status", "Success");
    }

    @PostMapping(Routes.URI_REST_REQUEST_ACCESS)
    public GenericResponseDTO<String> userRequestAccess(@RequestBody final RequestAccessDTO request)
            throws LdapException, SkillManagementException, NoSuchMessageException {

        GenericResponseDTO<String> response = new GenericResponseDTO<>();
        log.info("AccessRequest: " + request.toString());

        userFacade.requestAccess(request.getUsername(), request.getReason());
        response.setCode(MessageCode.SEND_SUCCESS.toString());
        response.setTimestamps(new Date());
        return response;
    }

    @GetMapping(Routes.URI_SEARCH_USER)
    public GenericResponseDTO<List<LdapInfo>> searchUser(@RequestParam String queryParams) {
        return GenericResponseDTO.<List<LdapInfo>>builder()
                .data(userFacade.searchUser(queryParams))
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .build();
    }

    @GetMapping(Routes.URI_SEARCH_DISTRIBUTION)
    public GenericResponseDTO<List<LdapInfo>> searchDistribution(@RequestParam String queryParams) {
        return GenericResponseDTO.<List<LdapInfo>>builder()
                .data(userFacade.searchDistributionList(queryParams))
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .build();
    }

    @GetMapping(Routes.URI_SEARCH)
    public GenericResponseDTO<SearchUserResponseDTO> search(@RequestParam String searchPhrase) throws LdapException {
        return GenericResponseDTO.<SearchUserResponseDTO>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(userFacade.searchUserAndDisList(searchPhrase))
                .build();
    }

    @GetMapping(value = Routes.URI_SEARCH_USER_BY_ROLE)
    public GenericResponseDTO<List<UserDTO>> getPersonalByRoleId(@RequestParam String roleName){
        return GenericResponseDTO.<List<UserDTO>>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(userFacade.findPersonalByRole(roleName))
                .build();
    }
    
	@PostMapping(value = Routes.URI_REST_REFRESH_TOKEN)
	public GenericResponseDTO<UserSession> refreshToken(@RequestBody RefreshTokenRequest request) throws UserManagementException {
		final String clientId = env.getProperty("client.id");
		final String clientSecret = env.getProperty("client.secret");
		return GenericResponseDTO.<UserSession>builder()
				.code(MessageCode.SUCCESS.toString())
				.data(userFacade.refreshToken(clientId, clientSecret, request.getUsername(), request.getRefreshToken()))
				.build();
	}
}
