/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.usermanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.common.CommonTestData;
import com.bosch.eet.skill.management.common.JsonUtils;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LUK1HC
 *
 */

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserServiceTests {

	@Autowired
	private UserService service;

	@Test
	@Transactional
	void testSave() throws Exception {
		final UserDTO dummy = buildDummyData();
		log.info("Request: " + JsonUtils.convertToString(dummy));
		final String id = service.save(dummy);
		log.info(id);
		assertNotNull(id);
	}

	@Test
	@Transactional
	void testFindById() throws Exception {
		// Save
		final UserDTO dummy = buildDummyData();
		final String id = service.save(dummy);
		log.info(id);
		assertNotNull(id);

		// FindById
		UserDTO result = service.findById(id);
		log.info(result.toString());
		assertNotNull(result);
		assertThat(id).isEqualTo( result.getId());
	}

	@Test
	@Transactional
	void testUpdate() throws Exception {
		// Save
		final UserDTO dummy = buildDummyData();
		final String id = service.save(dummy);
		log.info(id);
		assertNotNull(id);

		// Update
		dummy.setId(id);
		String updatedValue = dummy.getName() + "_Updated";
		dummy.setName(updatedValue);
		service.save(dummy);

		// FindById
		UserDTO result = service.findById(id);
		log.info(result.toString());
		assertNotNull(result);
		Assertions.assertEquals(updatedValue, result.getName());
	}

	@Test
	@Transactional
	void testDeleteById() throws Exception {
		// Save
		final UserDTO dummy = buildDummyData();
		final String id = service.save(dummy);
		log.info(id);
		assertNotNull(id);

		// Delete
		service.deleteById(id);

		// FindById
		UserDTO result = service.findById(id);
		Assertions.assertNull(result.getId());
	}

	@Test
	@Transactional
	void testFindAll() throws Exception {
		// Save
		final UserDTO dummy = buildDummyData();
		final String id = service.save(dummy);
		log.info(id);
		assertNotNull(id);

		// FindAll
		List<UserDTO> response = service.findAll();
		log.info(JsonUtils.convertToString(response));
		assertThat(response).isNotEmpty();

	}

	@Test
	@Transactional
	void testFindByName() throws Exception {
		// Save
		final UserDTO dummy = buildDummyData();
		final String id = service.save(dummy);
		log.info(id);
		assertNotNull(id);

		// FindById
		UserDTO result = service.findActiveUserByName(dummy.getName());
		log.info(result.toString());
		assertNotNull(result);
		Assertions.assertEquals(id, result.getId());
	}

	@Test
	@Transactional
	void testGetUserPermissions() throws Exception {
		List<PermissionDTO> response = service.getUserPermissions(CommonTestData.USER_ID_LUK1HC);
		log.info(JsonUtils.convertToString(response));
		assertThat(response).isNotEmpty();
	}

	private UserDTO buildDummyData() {
		final String NAME = "Dummy Test_" + new Random().nextInt(Integer.MAX_VALUE);
		UserDTO dummy = new UserDTO();
		dummy.setName(NAME);
		dummy.setDisplayName(NAME);
		dummy.setEmail(NAME);
		dummy.setCreatedBy(CommonTestData.USER_ID_SYSTEM_TEST);
		return dummy;
	}
}
