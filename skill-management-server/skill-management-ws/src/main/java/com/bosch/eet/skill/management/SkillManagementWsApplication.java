/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */
 
package com.bosch.eet.skill.management;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author LUK1HC
 */

@SpringBootApplication()
@EnableScheduling
public class SkillManagementWsApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SkillManagementWsApplication.class, args);
	}

}
