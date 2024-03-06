/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.ldap.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author LUK1HC
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class LdapException extends Exception {

	private static final long serialVersionUID = -2898932882471608558L;
	
	@Getter
	protected String code;
	
	@Getter
	protected String message;
	
	@Getter
	protected Throwable cause;
	
}
