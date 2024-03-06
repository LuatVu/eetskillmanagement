/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.usermanagement.dto;

import java.io.Serializable;
import java.util.List;

import com.bosch.eet.skill.management.common.JsonUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LUK1HC
 *
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String name;
	
	private String displayName;
	
	private String email;
	
	private String status;
	
	private String createdBy;

	private String type;

	private boolean isDistributionList;

	private List<RoleDTO> roles;

	private List<PermissionDTO> permissions;
	
	public String toJson() {
		return JsonUtils.convertToString(this);
	}
}
