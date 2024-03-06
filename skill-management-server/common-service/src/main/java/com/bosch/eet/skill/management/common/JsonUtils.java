/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.common;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author LUK1HC
 */

@Slf4j
public final class JsonUtils {

	private JsonUtils() {}
	
	public static ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper(); 
		objectMapper.findAndRegisterModules();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		return objectMapper;
	}
	
	public static String convertToString(Object o) {
		String result = "";
		final ObjectMapper objectMapper = getObjectMapper();
		try {
			result = objectMapper.writeValueAsString(o); 
		} catch (Exception e) {
			log.error("Error convertToString " + o.getClass().getName() + " object to JSON, " +  e);
		}
		return result; 
	}
	
}
