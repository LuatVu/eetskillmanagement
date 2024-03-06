package com.bosch.eet.skill.management.repo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;

@DataJpaTest
@ActiveProfiles("dev")
public class UserReposistoryTest {
	
	@Autowired
	private UserRepository userRepository;
	
	@Test
	void testExistsByName() {
		boolean exists = userRepository.existsByName("glo7hc");
		assertThat(exists).isTrue();
		boolean exists2 = userRepository.existsByName("GLO7HC");
		assertThat(exists2).isTrue();
		boolean notExists = userRepository.existsByName("WRO8hc");
		assertThat(notExists).isFalse();
	}
}
