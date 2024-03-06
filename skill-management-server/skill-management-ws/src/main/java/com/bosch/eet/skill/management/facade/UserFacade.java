/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.facade;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import com.bosch.eet.skill.management.dto.LoginRequest;
import com.bosch.eet.skill.management.dto.LoginResponse;
import com.bosch.eet.skill.management.dto.response.SearchUserResponseDTO;
import com.bosch.eet.skill.management.dto.response.UserSession;
import com.bosch.eet.skill.management.ldap.exception.LdapException;
import com.bosch.eet.skill.management.ldap.model.LdapInfo;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementException;

/**
 * @author LUK1HC
 *
 */
public interface UserFacade {

	LoginResponse login(final String clientId, final String clientSecret, LoginRequest request) throws UserManagementException, LdapException;
	LoginResponse loginSSO(HttpServletRequest request, Map<String, String> parameters) throws UserManagementException, LdapException;

	boolean validateAccessToken(final String accessToken);

	LdapInfo findByUserIDInLDAP(String userID);

	User findByUserIDInDB(String userID);

	String sendRequestAccessMail(LdapInfo ldapInfo, String reason) throws UserManagementException, MessagingException;

	List<LdapInfo> searchUser(String QueryParams);

	List<LdapInfo> searchDistributionList(String QueryParams);

	SearchUserResponseDTO searchUserAndDisList(String searchPhrase) throws LdapException;

    void requestAccess(String username, String reason) throws LdapException;
    User addUserAndPersonal(String username);

	List<UserDTO> findPersonalByRole(String roleId);
	
	UserSession refreshToken(String clientId, String clientSecret, String userName,
			String refreshToken) throws UserManagementException;
}
