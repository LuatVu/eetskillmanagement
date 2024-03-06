package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bosch.eet.skill.management.entity.ProjectRole;
import com.bosch.eet.skill.management.repo.ProjectRoleRepository;
import com.bosch.eet.skill.management.service.impl.ProjectRoleServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class ProjectRoleServiceTest {

	@InjectMocks
	private ProjectRoleServiceImpl projectRoleServiceImpl;

	@Mock
	private ProjectRoleRepository projectRoleRepository;

	@Test
	@DisplayName("Find by id")
	void findById() {
		String id = "abc";
		ProjectRole projectRole = ProjectRole.builder().id(id).name("name").build();
		when(projectRoleRepository.findById(id)).thenReturn(Optional.of(projectRole));
		assertThat(projectRole).isEqualTo(projectRoleServiceImpl.findById(id));
	}

	@Test
	@DisplayName("Find by id - not found")
	void findById_NotFound() {
		String id = "abc";
		when(projectRoleRepository.findById(id)).thenReturn(Optional.ofNullable(null));
		assertThat(projectRoleServiceImpl.findById(id)).isNull();
	}
}
