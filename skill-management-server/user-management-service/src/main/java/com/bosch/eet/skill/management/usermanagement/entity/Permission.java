/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.usermanagement.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author LUK1HC
 *
 */

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "`permission`")
public class Permission implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "code")
	private String code;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "`status`", insertable = false, updatable = true)
	private String status;
	
	@Column(name = "created_by", nullable = false, insertable = true, updatable = false)
	private String createdBy;

	@Column(name = "created_date", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdDate;

	@Column(name = "modified_by", nullable = true, insertable = false, updatable = true)
	private String modifiedBy;

	@Column(name = "modified_date", nullable = true, insertable = false, updatable = true)
	private LocalDateTime modifiedDate;
	
	//bi-directional many-to-one association to RolePermission
	@OneToMany(mappedBy = "permission")
	private List<RolePermission> rolePermissions;

	@ManyToOne
	@JoinColumn(name = "permission_category_id")
	private PermissionCategory permissionCategory;
	
}
