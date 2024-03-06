/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.dto;

import java.io.Serializable;
import java.util.List;

import com.bosch.eet.skill.management.common.JsonUtils;
import com.bosch.eet.skill.management.dto.response.UserSession;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;

import lombok.Data;

/**
 * @author LUK1HC
 *
 */

@Data
public class LoginResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	private String displayName;
	private String email;
	private AccessTokenResponse token;
	private List<PermissionDTO> permissions;
	private UserSession userSession;
	
	public String toJson() {
		return JsonUtils.convertToString(this);
	}
	
}
