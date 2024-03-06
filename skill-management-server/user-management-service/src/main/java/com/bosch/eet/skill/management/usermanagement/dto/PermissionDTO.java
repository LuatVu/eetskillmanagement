/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.usermanagement.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.Size;

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
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;

	private String code;
	
    @Size(max = 120)
	private String name;

	private String permissionCategoriesId;

	private String status;
	
    @Size(max = 250)
	private String description;
	
	private String createdBy;

	private String categoryId;

	private LocalDateTime createdDate;

	private LocalDateTime modifiedDate;

	private String modifiedBy;

	private boolean belongsToRole;
	
	public String toJson() {
		return JsonUtils.convertToString(this);
	}
	
}
