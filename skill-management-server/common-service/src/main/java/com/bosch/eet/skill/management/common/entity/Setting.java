/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */
 
package com.bosch.eet.skill.management.common.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author LUK1HC
 */

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Entity
@Table(name="`setting`")
public class Setting implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Setter(AccessLevel.NONE)
	@Id
	@Column(name = "`key`")
	private String key;

	@Setter(AccessLevel.NONE)
	@Column(name = "`value`")
	private String value;
	
	
}
