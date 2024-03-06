/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */
 
package com.bosch.eet.skill.management.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Routes;

/**
 *
 * @author LUK1HC
 */

@RestController
public class HomeRest {

	@GetMapping(Routes.URI_REST_HOME)
	public String home() {
		return "Welcome to Skill Management REST Service.";
	}
	
}
