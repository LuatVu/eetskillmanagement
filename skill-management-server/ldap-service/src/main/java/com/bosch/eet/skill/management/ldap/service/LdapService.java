/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.ldap.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.bosch.eet.skill.management.ldap.exception.LdapException;
import com.bosch.eet.skill.management.ldap.model.LdapInfo;

/**
*
* @author LUK1HC
*/

public interface LdapService {

	boolean authenticate(final String principal, final String credentials) throws LdapException;
	
	Optional<LdapInfo> getPrincipalInfo(final String principal) throws LdapException;
	
	Optional<LdapInfo> getPrincipalInfoByDisplayName(final String displayName);
	
	Optional<LdapInfo> getGroupInfo(final String group);
	
	List<LdapInfo> search(final String keyword);
	
	List<LdapInfo> searchGroup(final String keyword);
	
	Set<LdapInfo> getChildGroups(final String groupDisplayName);

	List<LdapInfo> getPrincipalInfos(List<String> principals);
}
