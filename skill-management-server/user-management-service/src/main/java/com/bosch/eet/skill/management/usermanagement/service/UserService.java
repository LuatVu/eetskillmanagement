/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.usermanagement.service;

import java.util.List;
import java.util.Set;

import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.entity.User;

/**
 * @author LUK1HC
 *
 */
public interface UserService {

	List<UserDTO> findAll();
	
	String save(final UserDTO dto);
	
	UserDTO findById(final String id);
	
	void deleteById(final String id);
	
	UserDTO findActiveUserByName(final String name);
	
	List<PermissionDTO> getUserPermissions(final String id);
	
	List<UserDTO> findByConfigurationIdAndIsChecked(final String configurationId, final boolean isChecked);

	User findByNTId(String userID);

	User findByName(String name);

    List<UserDTO> findByIdIn(List<String> users);

	boolean existByName(String userName);

	User updateUser(User user);

	List<UserDTO> findUserByNameLike(String phrase);

	User addUser(UserDTO userDTO);

	List<UserDTO> findUserByIdAndActive(List<String> users);

    List<UserDTO> findUserByRoleName(String roleId);

	User findByDisplayName(String displayName);

	List<User> findByNameIn(Set<String> ntIds);
}
