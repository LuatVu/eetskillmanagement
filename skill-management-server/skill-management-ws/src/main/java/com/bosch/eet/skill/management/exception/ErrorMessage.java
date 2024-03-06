/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.exception;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.bosch.eet.skill.management.common.JsonUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author LUK1HC
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor 
@AllArgsConstructor 
@Getter
@Setter
@Builder
@Data
public class ErrorMessage implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String path;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private LocalDateTime timestamp;
	
	private HttpStatus status;

	private String code;

	private String message;

	private String cause;

	public String toJson() {
		return JsonUtils.convertToString(this);
	}

}
