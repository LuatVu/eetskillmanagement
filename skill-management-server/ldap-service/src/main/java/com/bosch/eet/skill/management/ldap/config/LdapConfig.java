/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.ldap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * @author LUK1HC
 *
 */

@Configuration
@ComponentScan({"com.bosch.eet.skill.management.ldap"})
@PropertySource(value = "classpath:application-ldap.properties")
public class LdapConfig {

	@Value("${ldap.url}")
	private String ldapUrl;
	
	@Value("${ldap.username}")
	private String ldapUserName;

	@Value("${ldap.password}")
	private String ldapPassword;

	@Value("${ldap.base}")
	private String ldapBase;

	@Bean
	public LdapContextSource contextSource() {
		LdapContextSource contextSource = new LdapContextSource();
		contextSource.setUrl(ldapUrl);
		contextSource.setUserDn(ldapUserName);
		contextSource.setPassword(ldapPassword);
		contextSource.setBase(ldapBase);
		return contextSource;
	}
	
	@Bean
	public LdapTemplate ldapTemplate() {
		return new LdapTemplate(contextSource());
	}
	
}