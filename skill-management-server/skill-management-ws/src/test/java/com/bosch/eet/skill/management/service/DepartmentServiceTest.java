package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.entity.Department;
import com.bosch.eet.skill.management.repo.DepartmentRepository;
import com.bosch.eet.skill.management.service.impl.DepartmentServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @Mock
    private DepartmentRepository departmentRepository;

    //find
	@Test
    @DisplayName("Find all skill department")
    @Transactional
    void getDepartment_returnListOfDepartment() {
		List<Department> departments = Arrays.asList(Department.builder().id("1").name("a").build());
		when(departmentRepository.findAll())
				.thenReturn(departments);
		assertThat( departmentService.findAllDepartment()).isNotEmpty();
    }
    
    //find null
    @Test
    @DisplayName("Find empty skill groups")
    void getDepartment_returnListOfEmptyDepartment() {
        when(departmentRepository.findAll()).thenReturn(Collections.emptyList());
        assertThat(departmentService.findAllDepartment()).isEmpty();

    }
}
