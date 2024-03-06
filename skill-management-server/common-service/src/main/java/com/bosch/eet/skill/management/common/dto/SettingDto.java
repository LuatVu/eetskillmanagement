/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */
 
package com.bosch.eet.skill.management.common.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * @author LUK1HC
 *
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SettingDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String key;

	private String value;
	
}
