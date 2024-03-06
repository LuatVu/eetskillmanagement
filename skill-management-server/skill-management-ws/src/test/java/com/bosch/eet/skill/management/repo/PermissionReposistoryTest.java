package com.bosch.eet.skill.management.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.bosch.eet.skill.management.usermanagement.entity.Permission;
import com.bosch.eet.skill.management.usermanagement.repo.PermissionRepository;

@DataJpaTest
@ActiveProfiles("dev")
public class PermissionReposistoryTest {

	@Autowired
	private PermissionRepository permissionRepository;

	@Test
	void testFindPermissionById() {
		// query in list empty
		List<Permission> permission = permissionRepository.findPermissionsById(Collections.emptyList());
		assertThat(permission).isEmpty();
		// query in list non empty and id exists
		List<Permission> permission1 = permissionRepository
				.findPermissionsById(Arrays.asList("005be29f-d1c9-11ec-81c0-38f3ab0673e4"));
		assertThat(permission1).isNotEmpty();
		// query in list non empty and id not exists
		List<Permission> permission2 = permissionRepository
				.findPermissionsById(Arrays.asList("005be29f-d1c9-11ec-81c0-idnotexits"));
		assertThat(permission2).isEmpty();
	}

	@Test
	void testFindAllByRole() {
		List<String> roleIds = Collections.singletonList("rladmin3-d1c0-11ec-81c0-38f3ab0673e4");
		List<Permission> permissions = permissionRepository.findAllByRoles(roleIds);
		assertThat(permissions).isNotEmpty();
		List<Permission> permissions2 = permissionRepository.findAllByRoles(Collections.singletonList("abc"));
		assertThat(permissions2).isEmpty();
	}

}
