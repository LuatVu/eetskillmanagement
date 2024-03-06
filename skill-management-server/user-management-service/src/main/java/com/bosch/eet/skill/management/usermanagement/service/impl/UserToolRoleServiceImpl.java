package com.bosch.eet.skill.management.usermanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.usermanagement.repo.UserToolRoleRepository;
import com.bosch.eet.skill.management.usermanagement.service.UserToolRoleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserToolRoleServiceImpl implements UserToolRoleService{
	
	@Autowired
	private UserToolRoleRepository repo;
	
	@Override
	@Transactional
	public void deleteByUserId(final String userId) {
		repo.deleteByUserId(userId);
	}
}
