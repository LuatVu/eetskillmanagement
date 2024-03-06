package com.bosch.eet.skill.management.service.impl;

import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.entity.ProjectRole;
import com.bosch.eet.skill.management.repo.ProjectRoleRepository;
import com.bosch.eet.skill.management.service.ProjectRoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectRoleServiceImpl implements ProjectRoleService {
    private final ProjectRoleRepository projectRoleRepository;
    @Override
    public ProjectRole findById(String id) {
        return projectRoleRepository.findById(id).orElse(null);
    }
}
