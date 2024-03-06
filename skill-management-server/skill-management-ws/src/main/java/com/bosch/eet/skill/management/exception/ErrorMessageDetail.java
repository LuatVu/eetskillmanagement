/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.exception;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author LUK1HC
 */

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Builder
public class ErrorMessageDetail implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String object;
	private String field;
	private Object rejectedValue;
	private String message;
	
	public ErrorMessageDetail(final String object, final String message) {
		this.object = object;
		this.message = message;
	}
}
