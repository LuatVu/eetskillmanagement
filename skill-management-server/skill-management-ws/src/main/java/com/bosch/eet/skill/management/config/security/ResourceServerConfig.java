/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.Routes;

/**
 * @author LUK1HC
 *
 */

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Value(value = "{$spring.resource.is.stateless default true}")
	private String isStateless;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId(Constants.RESOURCE_ID);
		resources.stateless(Boolean.parseBoolean(isStateless));
	}

	/**
	 * Configure the access rules and paths that are protected by OAuth2 security.
	 * By default all resources not in "/oauth/**" are protected.
	 *
	 * The ResourceServerConfiguration class actually inherent from WebSecurityConfigurerAdapter. So there is also a config(HttpSecurity http) method.
	 * The rule defines in ResourceServerConfiguration will be a higher priority SecurityConfig.
	 *
	 * The defaults should work for many applications, but you might want to change at least the resource id.
	 * You should override configure(ResourceServerSecurityConfigurer resources) method.
	 * */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		// @formatter:off
        http
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    		.and()
            .authorizeRequests()
            // Protected URLs
            // Project Management
            .antMatchers(HttpMethod.POST, Routes.URI_REST_DELETE_PROJECT).hasAuthority(Constants.DELETE_PROJECT)
            .antMatchers(HttpMethod.GET, Routes.URI_REST_PHASE).hasAuthority(Constants.VIEW_ALL_PROJECTS)//get v-model
            .antMatchers(HttpMethod.GET, Routes.URI_REST_PROJECT_ID).hasAuthority(Constants.VIEW_ALL_PROJECTS)//get project detail
            .antMatchers(HttpMethod.GET, Routes.URI_REST_PROJECT_DROP_DOWN).hasAuthority(Constants.VIEW_ALL_PROJECTS)//get project drop down
            .antMatchers(HttpMethod.GET, Routes.URI_REST_PROJECT_FILTER).hasAuthority(Constants.VIEW_ALL_PROJECTS)//get project drop down
            .antMatchers(HttpMethod.POST, Routes.URI_REST_PROJECT).hasAuthority(Constants.ADD_BOSCH_PROJECT)//add project
            .antMatchers(HttpMethod.POST, Routes.URI_REST_UPDATE_PROJECT).hasAuthority(Constants.EDIT_PROJECT)//update project
            .antMatchers(HttpMethod.POST, Routes.URI_REST_IMPORT_PROJECT).hasAuthority(Constants.ADD_BOSCH_PROJECT)//import project
            .antMatchers(HttpMethod.POST, Routes.URI_REST_PROJECT_PORTFOLIO_DETAIL).hasAuthority(Constants.EDIT_PROJECT)
            .antMatchers(HttpMethod.POST, Routes.UIR_REST_PROJECT_PORTFOLIO).hasAuthority(Constants.EDIT_PROJECT)//edit project portfolio
            // Associate
			//Project Scope
			.antMatchers(HttpMethod.POST, Routes.URI_REST_PROJECT_SCOPE).hasAuthority(Constants.VIEW_SYSTEM)
			.antMatchers(HttpMethod.POST, Routes.URI_REST_PROJECT_SCOPE_UPDATE).hasAuthority(Constants.VIEW_SYSTEM)
			.antMatchers(HttpMethod.POST, Routes.URI_REST_PROJECT_SCOPE_DELETE).hasAuthority(Constants.VIEW_SYSTEM)
			//User Group
            .antMatchers(HttpMethod.GET, Routes.URI_REST_GROUP).hasAuthority(Constants.VIEW_USER_MANAGEMENT)//get all group
            .antMatchers(HttpMethod.GET, Routes.URI_REST_GROUP+Routes.URI_REST_ID).hasAuthority(Constants.VIEW_USER_MANAGEMENT)
            .antMatchers(HttpMethod.POST, Routes.URI_REST_GROUP).hasAuthority(Constants.USER_GROUP)//create a new group
            .antMatchers(HttpMethod.POST, Routes.URI_REST_GROUP+Routes.URI_REST_GROUP_DELETE).hasAuthority(Constants.USER_GROUP)//delete a group
            .antMatchers(HttpMethod.POST, Routes.URI_REST_GROUP+Routes.URI_REST_ID).hasAuthority(Constants.USER_GROUP)//update a group
            .antMatchers(HttpMethod.POST, Routes.URI_REST_GROUP+Routes.URI_REST_GROUP_ADD_USERS+Routes.URI_REST_ID).hasAuthority(Constants.USER_GROUP)//add a user to a group
            .antMatchers(HttpMethod.POST, Routes.URI_REST_GROUP+Routes.URI_REST_GROUP_DEL_USERS+Routes.URI_REST_ID).hasAuthority(Constants.USER_GROUP)//remove a user from a group
            .antMatchers(HttpMethod.POST, Routes.URI_REST_GROUP+Routes.URI_REST_GROUP_ADD_ROLES+Routes.URI_REST_ID).hasAuthority(Constants.USER_GROUP)//add a user to a group
            .antMatchers(HttpMethod.POST, Routes.URI_REST_GROUP+Routes.URI_REST_GROUP_DEL_ROLES+Routes.URI_REST_ID).hasAuthority(Constants.USER_GROUP)//remove a user from a group
            //Role
            .antMatchers(HttpMethod.GET, Routes.URI_REST_ROLE).hasAuthority(Constants.VIEW_USER_MANAGEMENT)
            .antMatchers(HttpMethod.GET, Routes.URI_REST_ROLE+Routes.URI_REST_ID).hasAuthority(Constants.VIEW_USER_MANAGEMENT)
            .antMatchers(HttpMethod.POST, Routes.URI_REST_ROLE).hasAuthority(Constants.CREATE_ROLE)
            .antMatchers(HttpMethod.POST, Routes.URI_REST_ROLE+Routes.URI_REST_ID).hasAuthority(Constants.EDIT_ROLE)
            .antMatchers(HttpMethod.POST, Routes.URI_REST_DELETE_ROLE).hasAuthority(Constants.DELETE_ROLE)
            //Permission
            .antMatchers(HttpMethod.GET, Routes.URI_REST_PERMISSION).hasAuthority(Constants.VIEW_USER_MANAGEMENT)
            .antMatchers(HttpMethod.POST, Routes.URI_REST_PERSON_EDIT).hasAuthority(Constants.EDIT_ASSOCIATE_INFO_PERMISSION)
            .antMatchers(HttpMethod.POST, Routes.URI_REST_PERSON).hasAuthority(Constants.EDIT_ASSOCIATE_INFO_PERMISSION)
            .antMatchers(HttpMethod.POST, Routes.URI_REST_PERSON_ID_PROJECT).hasAuthority(Constants.EDIT_ASSOCIATE_INFO_PERMISSION)
            .antMatchers(HttpMethod.POST, Routes.URI_REST_ELASTIC_QUERY).hasAnyAuthority(Constants.VIEW_ASSOCIATE_INFO_PERMISSION,
            		Constants.VIEW_ALL_PROJECTS, Constants.VIEW_DEPARTMENT_LEARNING, Constants.VIEW_SYSTEM)
            //Customer
            .antMatchers(HttpMethod.GET, Routes.URI_REST_PROJECT_CUSTOMER_GB_DETAIL).hasAuthority(Constants.VIEW_PROJECT_DETAIL)
            .antMatchers(HttpMethod.POST, Routes.URI_REST_PROJECT_CUSTOMER_GB).hasAuthority(Constants.ADD_BOSCH_PROJECT)
            .antMatchers(HttpMethod.POST, Routes.URI_REST_PROJECT_CUSTOMER_GB_DETAIL).hasAuthority(Constants.EDIT_PROJECT)
            .antMatchers(HttpMethod.POST, Routes.URI_REST_PROJECT_CUSTOMER_GB_DEL).hasAuthority(Constants.DELETE_PROJECT)
            .antMatchers(HttpMethod.GET, Routes.URI_REST_PROJECT_FILTER_CUSTOMER_GB).hasAuthority(Constants.VIEW_ALL_PROJECTS)
            .antMatchers(HttpMethod.POST, Routes.URI_REST_PERSON_LIST_EXPORT).hasAuthority(Constants.VIEW_ASSOCIATE_INFO_PERMISSION)
            .antMatchers(HttpMethod.GET, Routes.URI_REST_SKILL_TYPE).hasAnyAuthority(Constants.VIEW_EXPECTED_SKILL_LEVEL,
            		Constants.EDIT_EXPECTED_SKILL_LEVEL, Constants.VIEW_SKILL_EVALUATE)
            .antMatchers(HttpMethod.GET, Routes.URI_REST_GET_PROJECTS_BY_SKILLTAG_AND_CUSTOMER).hasAuthority(Constants.VIEW_PROJECT_DETAIL)
            //Report
            .antMatchers(HttpMethod.GET, Routes.URI_REST_REPORT_PROJECT).hasAuthority(Constants.VIEW_REPORT)
            //Skill
            .antMatchers(HttpMethod.POST, Routes.URI_REST_DELETE_SKILL).hasAuthority(Constants.VIEW_SYSTEM)
            .anyRequest().authenticated()
            .and()
            .logout().invalidateHttpSession(true).clearAuthentication(true).deleteCookies()
            .and()
    		.csrf().disable()
    		.cors()
    		.and()
    		.formLogin().loginProcessingUrl(Routes.URI_REST_LOGIN)
    		.and()
    		.formLogin().disable()
    		.httpBasic().disable()
            .exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
        // @formatter:on
	}



}
