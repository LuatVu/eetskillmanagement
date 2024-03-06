package com.bosch.eet.skill.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.converter.utils.DepartmentConverterUtil;
import com.bosch.eet.skill.management.dto.DepartmentDto;
import com.bosch.eet.skill.management.repo.DepartmentRepository;
import com.bosch.eet.skill.management.service.DepartmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    
    @Override
    public List<DepartmentDto> findAllDepartment() {
        return DepartmentConverterUtil.convertToDtos(departmentRepository.findAll());
        }

    
}