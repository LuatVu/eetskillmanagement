package com.bosch.eet.skill.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.entity.ProjectType;
import com.bosch.eet.skill.management.repo.ProjectTypeRepository;
import com.bosch.eet.skill.management.service.ProjectTypeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectTypeServiceImpl implements ProjectTypeService {
    private final ProjectTypeRepository projectTypeRepository;

    @Override
    public List<ProjectType> findAll() {
        return projectTypeRepository.findAll();
    }
}
