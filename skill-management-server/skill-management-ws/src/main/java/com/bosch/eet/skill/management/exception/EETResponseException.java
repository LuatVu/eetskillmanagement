/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */
 
package com.bosch.eet.skill.management.exception;

import org.springframework.http.HttpStatus;

import com.bosch.eet.skill.management.common.JsonUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author LUK1HC
 */

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class EETResponseException extends RuntimeException {


	private static final long serialVersionUID = 1L;

	private String code;
	private String message;
	private Throwable cause;
	private HttpStatus status;
	public EETResponseException(String code, String message, Throwable cause) {
		this.code = code;
		this.message = message;
		this.cause = cause;
	}

	@Override
	public String toString() {
		return String.format("SkillManagementException[code=%s, message='%s', cause='%s']", code, message, cause);
	}

	public String toJson() {
		return JsonUtils.convertToString(this);
	}
}
