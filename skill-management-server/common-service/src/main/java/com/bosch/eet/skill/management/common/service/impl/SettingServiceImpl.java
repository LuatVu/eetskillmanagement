/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */
 
package com.bosch.eet.skill.management.common.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.common.entity.Setting;
import com.bosch.eet.skill.management.common.repo.SettingRepository;
import com.bosch.eet.skill.management.common.service.SettingService;

/**
 * @author LUK1HC
 *
 */

@Service
public class SettingServiceImpl implements SettingService {

	@Autowired
	private SettingRepository settingRepository;
	
	@Override
	public String findByKey(final String key) {
		final Optional<Setting> setting = settingRepository.findByKey(key);
		if (setting.isPresent()) {
			return setting.get().getValue();
		}
		else {
			return null;
		}
	}
	
}
