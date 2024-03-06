/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.dto;

import java.io.Serializable;

import com.bosch.eet.skill.management.common.JsonUtils;

import lombok.Data;

/**
 * @author LUK1HC
 *
 */

@Data
public class AccessTokenRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String baseUrl;

	private String clientId;
	
	private String clientSecret;
	
	private String username;
	
	private String password;
	
	private String grantType;
	
	private String refreshToken;
	
	public String toJson() {
		return JsonUtils.convertToString(this);
	}
	
}
