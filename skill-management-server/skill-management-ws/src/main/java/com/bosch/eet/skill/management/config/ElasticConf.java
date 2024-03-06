/*
                * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author LUK1HC
 */

@Slf4j
@Configuration
@EnableElasticsearchRepositories(basePackages = {"com.bosch.eet.skill.management.elasticsearch.repo"})
@ComponentScan(basePackages = { "com.bosch.eet.skill.management.elasticsearch" })
@EnableScheduling
@ComponentScan(basePackages = { "com.bosch.eet.skill.management.elasticsearch" })
public class ElasticConf extends AbstractElasticsearchConfiguration {

	@Value("${elasticsearch.host}")
	private String elastisearchHost;
	
	@Value ("${elasticsearch.port}")
	private String elasticsearchPort;

	@Value("${spring.profiles.active}")
	private String profile;

	@Bean
	public String profile(){
		return profile;
	}

	@Override
	public RestHighLevelClient elasticsearchClient() {
		ClientConfiguration clientConfiguration = ClientConfiguration.builder()
				.connectedTo(String.format("%s:%s", elastisearchHost,elasticsearchPort))
				.build();
		return RestClients.create(clientConfiguration).rest();
	}
	
}
