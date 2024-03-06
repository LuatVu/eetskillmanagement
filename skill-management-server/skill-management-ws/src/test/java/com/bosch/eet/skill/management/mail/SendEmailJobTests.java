/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.mail;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LUK1HC
 *
 */

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SendEmailJobTests {

	@Autowired
	private SendEmailJob sendEmailJob;
	
	@Test
	void testSendEmailRQONEConnectionError() throws Exception {
		final String errorCode = "ERROR_CODE_TEST";
		final String errorMessage = "ERROR_MESSAGE_TEST";
		final String details = "ErrorDetailsTest";
		
		sendEmailJob.sendEmailRQONEConnectionError(errorCode, errorMessage, details);
		assertTrue(true);
	}
	
}
