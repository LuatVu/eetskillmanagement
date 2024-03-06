/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.ldap.model;

import java.io.Serializable;
import java.util.Set;

import lombok.Data;

/**
*
* @author LUK1HC
*/

@Data
public class LdapInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userId;
	private String cn;
	private String name;
	private String lastName;
	private String givenName;
	private String displayName;
	private String email;
	private String group;
	private String department;
	private String businessUnit;
	private String area;
	private String country;
	private String distinguishedName;
	private String objectClass;
	private String referenceId;
	private String company;
	private String office;
	private byte[] picture;
	private Set<String> memberOf;
	private Set<String> member;
	
}
