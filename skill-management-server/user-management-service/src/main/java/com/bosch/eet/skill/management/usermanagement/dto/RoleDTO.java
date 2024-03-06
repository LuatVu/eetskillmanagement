/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.usermanagement.dto;

import com.bosch.eet.skill.management.common.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author LUK1HC
 *
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String name;

	private String status;
	
	private String displayName;

	private String description;
	
	private String createdBy;

	private LocalDateTime createdDate;
	
	@Singular
	private List<String> permissionIds;

	private List<PermissionCategoryDTO> permissionCategories;

	private Integer priority;

	public String toJson() {
		return JsonUtils.convertToString(this);
	}
	
}
