/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.facade.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bosch.eet.skill.management.cache.AppCacheManager;
import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.PasswordHelper;
import com.bosch.eet.skill.management.common.Status;
import com.bosch.eet.skill.management.common.SymbolConstants;
import com.bosch.eet.skill.management.common.UserType;
import com.bosch.eet.skill.management.dto.AccessTokenRequest;
import com.bosch.eet.skill.management.dto.AccessTokenResponse;
import com.bosch.eet.skill.management.dto.LoginRequest;
import com.bosch.eet.skill.management.dto.LoginResponse;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.response.SearchUserResponseDTO;
import com.bosch.eet.skill.management.dto.response.UserSession;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.UserFacade;
import com.bosch.eet.skill.management.facade.util.TokenRequester;
import com.bosch.eet.skill.management.ldap.exception.LdapException;
import com.bosch.eet.skill.management.ldap.model.LdapInfo;
import com.bosch.eet.skill.management.ldap.service.LdapService;
import com.bosch.eet.skill.management.mail.EmailService;
import com.bosch.eet.skill.management.service.PersonalService;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementBusinessException;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementException;
import com.bosch.eet.skill.management.usermanagement.service.UserService;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LUK1HC
 */

@Slf4j
@Service
public class UserFacadeImpl implements UserFacade {

    @Autowired
    private TokenRequester tokenRequester;

    @Autowired
    private UserService userService;

    @Autowired
    private LdapService ldapService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AppCacheManager cacheManager;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PersonalService personalService;

    @Value("${spring.profiles.active}")
    private String profile;

    @Override
    public LoginResponse login(final String clientId, final String clientSecret, final LoginRequest request) throws UserManagementException, LdapException {

        LoginResponse response = new LoginResponse();

        String userName = request.getUsername();

        // Process prefix domain if a user inputs. Example: apac\<user_nt_id> | fe\<user_nt_id>
        if (userName.contains(SymbolConstants.BACKSLASH)) {
            final String[] parts = userName.split(Pattern.quote(SymbolConstants.BACKSLASH));
            userName = parts[1];
        }

        // Validate if a user exists in database ?
        UserDTO userDto = userService.findActiveUserByName(userName);
        final String userId = userDto.getId();
        if (StringUtils.isNotEmpty(userId)) {
            // Get user information from LDAP system
            Optional<LdapInfo> optLdapInfo = ldapService.getPrincipalInfo(userName);
            if (optLdapInfo.isPresent()) {
                response.setId(userId);
                response.setName(userName);
                LdapInfo ldapInfo = optLdapInfo.get();
                response.setDisplayName(ldapInfo.getDisplayName());
                response.setEmail(ldapInfo.getEmail());
            }

            // Get user's permissions
            final List<PermissionDTO> permissionsDto = userService.getUserPermissions(userId);
            if (!CollectionUtils.isEmpty(permissionsDto)) {
                response.setPermissions(permissionsDto);
            } else {
                final String code = MessageCode.NOT_AUTHORIZATION.name();
                final String message = messageSource.getMessage(MessageCode.NOT_AUTHORIZATION.toString(), null, LocaleContextHolder.getLocale());
                throw new SkillManagementException(code, message, null, HttpStatus.UNAUTHORIZED);
            }
        } else {
            final String code = MessageCode.NOT_EXISTED_USERNAME.name();
            final String message = messageSource.getMessage(MessageCode.NOT_EXISTED_USERNAME.toString(), null, LocaleContextHolder.getLocale());
            throw new SkillManagementException(code, message, null, HttpStatus.UNAUTHORIZED);
        }

        // Get Access Token
        final AccessTokenRequest accessTokenRequest = buildAccessTokenRequest(clientId, clientSecret, userName, request.getPassword());
        AccessTokenResponse accessTokenResponse = tokenRequester.requestAccessToken(accessTokenRequest);
        UserSession userSession = new UserSession();
		userSession.setAccessToken(accessTokenResponse.getAccessToken());
		userSession.setRefreshToken(accessTokenResponse.getRefreshToken());
		userSession.setTokenType(accessTokenResponse.getTokenType());
		userSession.setExpiresIn(accessTokenResponse.getExpiresIn());
		response.setUserSession(userSession);
        response.setToken(accessTokenResponse);

        // Cache credentials
//		cacheCredentials(request);

        return response;
    }
    public LoginResponse loginSSO(final HttpServletRequest request, Map<String, String> parameters) throws UserManagementException, LdapException {
        LoginResponse response = new LoginResponse();
        String clientId = request.getHeader(Constants.HEADER_ATTRIBUTE_CLIENT_ID);
        String clientSecret = request.getHeader(Constants.HEADER_ATTRIBUTE_CLIENT_SECRET);
        String x_forwarded_for = request.getHeader(Constants.HEADER_ATTRIBUTE_X_FORWARDED_FOR);
        String iv_user = request.getHeader(Constants.HEADER_ATTRIBUTE_IV_USER);
        if (iv_user == null || iv_user.isEmpty()) {
            final String message = messageSource.getMessage(MessageCode.NOT_EXISTED_USERNAME.toString(), null, LocaleContextHolder.getLocale());
            throw new SkillManagementException(MessageCode.NOT_EXISTED_USERNAME.toString(), message, null);
        } else {
            log.info("X Forwarded For : {}", x_forwarded_for);
            if (x_forwarded_for.contains(Constants.WAM_IP_ADDR)) {
                String ipAddress = request.getRemoteAddr();
				log.info("IP Address: {}", ipAddress);
				// Validate if a user exists in database ?
				UserDTO userDto = userService.findActiveUserByName(iv_user);
				final String userId = userDto.getId();
				if (StringUtils.isNotEmpty(userId)) {
					// Get user information from LDAP system
					Optional<LdapInfo> optLdapInfo = ldapService.getPrincipalInfo(iv_user);
					if (optLdapInfo.isPresent()) {
						response.setId(userId);
						response.setName(iv_user);
						LdapInfo ldapInfo = optLdapInfo.get();
						response.setDisplayName(ldapInfo.getDisplayName());
						response.setEmail(ldapInfo.getEmail());
					}

					// Get user's permissions
					final List<PermissionDTO> permissionsDto = userService.getUserPermissions(userId);
					if (!CollectionUtils.isEmpty(permissionsDto)) {
						response.setPermissions(permissionsDto);
					} else {
						final String code = MessageCode.NOT_AUTHORIZATION.name();
						final String message = messageSource.getMessage(MessageCode.NOT_AUTHORIZATION.toString(), null,
								LocaleContextHolder.getLocale());
						throw new SkillManagementException(code, message, null, HttpStatus.UNAUTHORIZED);
					}
				} else {
					final String code = MessageCode.NOT_EXISTED_USERNAME.name();
					final String message = messageSource.getMessage(MessageCode.NOT_EXISTED_USERNAME.toString(), null,
							LocaleContextHolder.getLocale());
					throw new SkillManagementException(code, message, null, HttpStatus.UNAUTHORIZED);
				}

				// Get Access Token
				final AccessTokenRequest accessTokenRequest = buildSSOAccessTokenRequest(clientId, clientSecret,
						iv_user);
				AccessTokenResponse accessTokenResponse = tokenRequester.requestAccessToken(accessTokenRequest);
				response.setToken(accessTokenResponse);
				return response;
            }
        }
        return null;
    }

    private AccessTokenRequest buildAccessTokenRequest(final String clientId, final String clientSecret, final String username, final String password) {

        final String GRANT_TYPE = Constants.GRANT_TYPE_PASS;
        final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
        accessTokenRequest.setBaseUrl(baseUrl);
        accessTokenRequest.setClientId(clientId);
        accessTokenRequest.setClientSecret(clientSecret);
        accessTokenRequest.setUsername(username);
        accessTokenRequest.setPassword(password);
        accessTokenRequest.setGrantType(GRANT_TYPE);

        return accessTokenRequest;
    }

    private AccessTokenRequest buildSSOAccessTokenRequest(final String clientId, final String clientSecret, final String iv_user) {

        final String GRANT_TYPE = Constants.GRANT_TYPE_PASS;
        final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
        accessTokenRequest.setBaseUrl(baseUrl);
        accessTokenRequest.setClientId(clientId);
        accessTokenRequest.setClientSecret(clientSecret);
        accessTokenRequest.setUsername(iv_user);
        accessTokenRequest.setGrantType(GRANT_TYPE);
        return accessTokenRequest;
    }

    private LoginResponse loginWithoutSecurityOAuth2(final LoginRequest request) throws UserManagementException, LdapException {

        LoginResponse response = new LoginResponse();

        String userName = request.getUsername();

        // Process prefix domain if a user inputs. Example: apac\<user_nt_id> | fe\<user_nt_id>
        if (userName.contains(SymbolConstants.BACKSLASH)) {
            final String[] parts = userName.split(Pattern.quote(SymbolConstants.BACKSLASH));
            userName = parts[1];
        }

        // Validate if a user exists in database ?
        UserDTO userDto = userService.findActiveUserByName(userName);
        final String userId = userDto.getId();
        if (StringUtils.isNotEmpty(userId)) {
            // Validate a user by LDAP System
            boolean result = ldapService.authenticate(userName, request.getPassword());
            if (result) {
                // Get user information from LDAP system
                Optional<LdapInfo> optLdapInfo = ldapService.getPrincipalInfo(userName);
                if (optLdapInfo.isPresent()) {
                    response.setId(userDto.getId());
                    response.setName(userName);
                    LdapInfo ldapInfo = optLdapInfo.get();
                    response.setDisplayName(ldapInfo.getDisplayName());
                    response.setEmail(ldapInfo.getEmail());
                }

                // Get Token
                // response.setToken(UUID.randomUUID().toString());

                // Get user's permissions
                final List<PermissionDTO> permissionsDto = userService.getUserPermissions(userId);
                if (!CollectionUtils.isEmpty(permissionsDto)) {
                    response.setPermissions(permissionsDto);
                } else {
                    final String code = MessageCode.NOT_AUTHORIZATION.name();
                    final String message = messageSource.getMessage(MessageCode.NOT_AUTHORIZATION.toString(), null, LocaleContextHolder.getLocale());
                    throw new SkillManagementException(code, message, null, HttpStatus.UNAUTHORIZED);
                }
            } else {
                final String code = MessageCode.INVALID_USERNAME_PASSWORD.name();
                final String message = messageSource.getMessage(MessageCode.INVALID_USERNAME_PASSWORD.toString(), null, LocaleContextHolder.getLocale());
                throw new SkillManagementException(code, message, null, HttpStatus.UNAUTHORIZED);
            }
        } else {
            final String code = MessageCode.NOT_EXISTED_USERNAME.name();
            final String message = messageSource.getMessage(MessageCode.NOT_EXISTED_USERNAME.toString(), null, LocaleContextHolder.getLocale());
            throw new SkillManagementException(code, message, null, HttpStatus.UNAUTHORIZED);
        }

        // Cache credentials
//		cacheCredentials(request);

        return response;
    }

    public void cacheCredentials(final LoginRequest request) {
        // decrypt before cache
        final String passwd = PasswordHelper.encrypt(request.getPassword());
        List<String> ls = new ArrayList<String>();
        ls.add(passwd);

        Cache cache = cacheManager.getCache(Constants.CREDENTIALS_CACHE_NAME);
        cache.put(request.getUsername().toLowerCase(), ls);
    }

    @Override
    public boolean validateAccessToken(final String accessToken) {
        final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return tokenRequester.validateAccessToken(baseUrl, accessToken);
    }

    @Override
    public LdapInfo findByUserIDInLDAP(String userID) {
        try {
            LdapInfo userInfo = ldapService.getPrincipalInfo(userID).orElse(null);
            return userInfo;

        } catch (LdapException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public User findByUserIDInDB(String userID) {
        return userService.findByNTId(userID);
    }

    @Override
    public String sendRequestAccessMail(LdapInfo ldapInfo, String reason) throws MessagingException, UserManagementException {
        return emailService.mailToRequestAccess(ldapInfo.getEmail(), ldapInfo.getUserId(), reason);
    }

    @Override
    public List<LdapInfo> searchUser(String queryParams) {
        return ldapService.search(queryParams);
    }

    @Override
    public List<LdapInfo> searchDistributionList(String QueryParams) {
        return ldapService.searchGroup(QueryParams);
    }

    @Override
    public SearchUserResponseDTO searchUserAndDisList(String searchPhrase) {
        //find in db first
        List<UserDTO> userByNameLike = userService.findUserByNameLike(searchPhrase);

        //find in ldap
        List<LdapInfo> ldapUserNameLike = ldapService.search(searchPhrase);

        return SearchUserResponseDTO.builder()
                .userDTOList(userByNameLike)
                .ldapInfos(ldapUserNameLike)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void requestAccess(String username, String reason) throws LdapException {

        try {
            User userFound = userService.findByNTId(username);

            if (Status.INACTIVE.getLabel().equals(userFound.getStatus())) {
                throw new SkillManagementException(MessageCode.SKM_USER_WAITED_TO_APPROVAL.toString(),
                        messageSource.getMessage(MessageCode.SKM_USER_WAITED_TO_APPROVAL.toString(), null,
                                LocaleContextHolder.getLocale()),
                        null);
            } else throw new SkillManagementException(MessageCode.SKM_USER_EXISTED_ALREADY.toString(),
                    messageSource.getMessage(MessageCode.SKM_USER_EXISTED_ALREADY.toString(), null,
                            LocaleContextHolder.getLocale()),
                    null);
        } catch (UserManagementBusinessException exception) {
            log.info("User not found in system, search in LDAP");

            LdapInfo ldapInfo = ldapService.getPrincipalInfo(username).orElseThrow(
                    () -> new LdapException(MessageCode.SKM_INVALID_LDAP_CHECKED.toString(),
                            messageSource.getMessage(MessageCode.SKM_INVALID_LDAP_CHECKED.toString(), null,
                                    LocaleContextHolder.getLocale()),
                            null)
            );

            User user = userService.addUser(
                    UserDTO.builder()
                            .name(ldapInfo.getName())
                            .displayName(ldapInfo.getDisplayName())
                            .email(ldapInfo.getEmail())
                            .status(Status.INACTIVE.getLabel())
                            .type(UserType.PERSON.getLabel())
                            .build()
            );

            //add personal with id = user.id
            personalService.savePersonalWithUser(
                    PersonalDto.builder()
                            .id(user.getId())
                            .code(ldapInfo.getUserId())
                            .build()
            );

            try {
                sendRequestAccessMail(ldapInfo, reason);
            } catch (MessagingException | UserManagementException e) {
                log.error("Send mail fail : {}", e.getMessage());
            }
        }
    }

    @Override
    public List<UserDTO> findPersonalByRole(String roleId) {
        return userService.findUserByRoleName(roleId);
    }

    @Override
    public User addUserAndPersonal(String username) {
        User user = userService.findByName(username);
        if (user == null) {
            log.info("User not found in system, search in LDAP");

            LdapInfo ldapInfo = null;
            try {
                ldapInfo = ldapService.getPrincipalInfo(username).orElse(null);
            } catch (LdapException e) {
                throw new RuntimeException(e);
            }

            if (ldapInfo != null) {
                user = userService.addUser(
                        UserDTO.builder()
                                .name(ldapInfo.getName())
                                .displayName(ldapInfo.getDisplayName())
                                .email(ldapInfo.getEmail())
                                .status(Status.INACTIVE.getLabel())
                                .type(UserType.PERSON.getLabel())
                                .build()
                );
            }
        }

        if (user != null) {
            Personal personal = personalService.findEntityById(user.getId());
            if (personal == null) {
                //add personal with id = user.id
                personalService.addRawPersonalWithoutPermission(Personal.builder()
                                .id(user.getId())
                                .title("")
                                .personalCode(user.getName())
                        .build());
            }
        }

        return user;
    }
    
    private AccessTokenRequest buildRefreshTokenRequest(final String clientId, final String clientSecret,
			final String username, final String refreshToken) {

        final String GRANT_TYPE = Constants.GRANT_TYPE_REFRESH;
        final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
        accessTokenRequest.setUsername(username);
		accessTokenRequest.setBaseUrl(baseUrl);
		accessTokenRequest.setClientId(clientId);
		accessTokenRequest.setClientSecret(clientSecret);
		accessTokenRequest.setRefreshToken(refreshToken);
		accessTokenRequest.setGrantType(GRANT_TYPE);
		return accessTokenRequest;
	}
    
    @Override
	public UserSession refreshToken(final String clientId, final String clientSecret, String userName, String refreshToken) throws UserManagementException{
		final AccessTokenRequest refreshTokenRequest = buildRefreshTokenRequest(clientId, clientSecret, userName, refreshToken);
		AccessTokenResponse accessTokenResponse = tokenRequester.requestAccessToken(refreshTokenRequest);
		UserSession userSession = new UserSession();
		userSession.setAccessToken(accessTokenResponse.getAccessToken());
		userSession.setRefreshToken(accessTokenResponse.getRefreshToken());
		userSession.setTokenType(accessTokenResponse.getTokenType());
		userSession.setExpiresIn(accessTokenResponse.getExpiresIn());
		return userSession;
	}
}
