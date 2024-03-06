package com.bosch.eet.skill.management.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.bosch.eet.skill.management.usermanagement.entity.Role;
import com.bosch.eet.skill.management.usermanagement.repo.RoleRepository;

@DataJpaTest
@ActiveProfiles("dev")
public class RoleReposistoryTest {
	
	@Autowired
	private RoleRepository roleReposistory;

	@Test
	void testFindByNameIgnoreCase() {
		Optional<Role> groupOpt1 = roleReposistory.findByNameIgnoreCase("Associate");
		assertThat(groupOpt1).isPresent();
		Optional<Role> groupOpt2 = roleReposistory.findByNameIgnoreCase("Associatessss");
		assertThat(groupOpt2).isNotPresent();
	}

	@Test
	void testFindAllByStatus() {
		List<Role> roles = roleReposistory.findAllByStatus("Active");
		assertThat(roles).isNotEmpty();
		List<Role> roles2 = roleReposistory.findAllByStatus("Inactive");
		assertThat(roles2).isNotEmpty();
	}
	
	@Test
	void testFindByNameIgnorecase() {
		Optional<Role> roleOpt1 = roleReposistory.findByNameIgnoreCase("Associate");
		assertThat(roleOpt1).isPresent();
		Optional<Role> roleOpt2 = roleReposistory.findByNameIgnoreCase("Associates");
		assertThat(roleOpt2).isNotPresent();
	}
}
