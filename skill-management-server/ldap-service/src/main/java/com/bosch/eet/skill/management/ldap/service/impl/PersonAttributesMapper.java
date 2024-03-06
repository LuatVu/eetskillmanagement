/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.ldap.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ldap.core.AttributesMapper;

import com.bosch.eet.skill.management.ldap.common.Constants;
import com.bosch.eet.skill.management.ldap.model.LdapInfo;

/**
 * @author LUK1HC
 *
 */
public class PersonAttributesMapper implements AttributesMapper<LdapInfo> {

	@Override
	public LdapInfo mapFromAttributes(final Attributes attrs) throws NamingException {
		LdapInfo ldapInfo = new LdapInfo();
		final String userNtId = (String)attrs.get(Constants.CN).get();
		if(!StringUtils.isBlank(userNtId)) {
			ldapInfo.setUserId(userNtId);
			ldapInfo.setCn((String)attrs.get(Constants.CN).get());
			ldapInfo.setName(attrs.get(Constants.NAME) != null ? (String)attrs.get(Constants.NAME).get() : "");
			ldapInfo.setLastName(attrs.get(Constants.SN) != null ? (String)attrs.get(Constants.SN).get() : "");
			ldapInfo.setGivenName(attrs.get(Constants.GIVEN_NAME) != null ? (String)attrs.get(Constants.GIVEN_NAME).get() : "");
			ldapInfo.setDisplayName(attrs.get(Constants.DISPLAY_NAME) != null ? (String)attrs.get(Constants.DISPLAY_NAME).get() : "");
			ldapInfo.setEmail(attrs.get(Constants.MAIL) != null ? (String)attrs.get(Constants.MAIL).get() : "");
			ldapInfo.setGroup(attrs.get(Constants.GROUP) != null ? (String)attrs.get(Constants.GROUP).get() : "");
			ldapInfo.setCountry(attrs.get(Constants.CO) != null ? (String)attrs.get(Constants.CO).get() : "");
			ldapInfo.setDistinguishedName(attrs.get(Constants.DISTINGGUISHED_NAME) != null ? (String)attrs.get(Constants.DISTINGGUISHED_NAME).get() : "");
			ldapInfo.setObjectClass(attrs.get(Constants.OBJECT_CLASS) != null ? (String)attrs.get(Constants.OBJECT_CLASS).get(1) : "");
			ldapInfo.setCompany(attrs.get(Constants.COMPANY) != null ? (String)attrs.get(Constants.COMPANY).get() : "");
			ldapInfo.setOffice(attrs.get(Constants.OFFICE) != null ? (String)attrs.get(Constants.OFFICE).get() : "");
			ldapInfo.setPicture(attrs.get(Constants.PICTURE) != null ? (byte[])attrs.get(Constants.PICTURE).get() : null);
			if (attrs.get(Constants.MEMBER_OF) != null) {
				final NamingEnumeration<?> membersOf = attrs.get(Constants.MEMBER_OF).getAll();
				final Set<String> userMemberOf = retrievesEnumerationAttributes(membersOf);
				ldapInfo.setMemberOf(userMemberOf);
			}
			if (attrs.get(Constants.MEMBER) != null) {
				final NamingEnumeration<?> members = attrs.get(Constants.MEMBER).getAll();
				final Set<String> userMember = retrievesEnumerationAttributes(members);
				ldapInfo.setMember(userMember);
			}
			extractDomain(attrs, ldapInfo);
		}
		return ldapInfo;
	}
	private Set<String> retrievesEnumerationAttributes(final NamingEnumeration<?> object) {
		Set<String> results = new HashSet<>();
		while (object.hasMoreElements()) {
			final String dn = (String) object.nextElement();
			final List<String> dns = Arrays.asList(dn.split(Constants.COMMA));
			final List<String> cns = Arrays.asList(dns.get(0).split(Constants.EQUAL));
			results.add(cns.get(1));
		}
		return results;
	}

	private void extractDomain(final Attributes attrs, LdapInfo ldapInfo) throws NamingException {
		if (null != attrs.get(Constants.DISTINGGUISHED_NAME)) {
			// Extract Area/Domain
			final String distinguishedName = (String)attrs.get(Constants.DISTINGGUISHED_NAME).get(); // Ex: CN={user_nt_id},OU=L,OU=Useraccounts,OU=Hc,DC=APAC,DC=bosch,DC=com
			if (!StringUtils.isBlank(distinguishedName)) {
				final String[] arrDistinguishedName = distinguishedName.split(Constants.COMMA);
				if (arrDistinguishedName.length > 4) {
					final String dcArea = arrDistinguishedName[4];
					final String[] tmpDcArea = dcArea.split(Constants.EQUAL);
					if (tmpDcArea.length > 0) {
						ldapInfo.setArea(tmpDcArea[1]);
					}
				}
			}
		}
	}
	
}