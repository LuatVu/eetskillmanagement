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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@Builder(toBuilder = true)
@Entity
@Table(name = "`role`")
public class Role implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "name")
	private String name;
	
	@Column(name="display_name")
	private String displayName;

	@Column(name = "description")
	private String description;

	@Column(name = "status", insertable = false)
	private String status;
	
	@Column(name = "created_by", nullable = false, updatable = false)
	private String createdBy;

	@Column(name = "created_date", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdDate;

	@Column(name = "modified_by", insertable = false)
	private String modifiedBy;

	@Column(name = "modified_date", insertable = false)
	private LocalDateTime modifiedDate;

	@Column(name = "priority")
	private Integer priority;

	//bi-directional many-to-one association to UserRole
	@OneToMany(mappedBy = "role")
	private List<UserRole> userRoles;
	
	//bi-directional many-to-one association to RolePermission
	@OneToMany(mappedBy = "role", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private List<RolePermission> rolePermissions;

	@OneToMany(mappedBy = "role")
	private List<GroupRole> groupRoles;
}
