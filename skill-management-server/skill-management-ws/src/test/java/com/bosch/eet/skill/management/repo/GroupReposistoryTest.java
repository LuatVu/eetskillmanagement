package com.bosch.eet.skill.management.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.bosch.eet.skill.management.usermanagement.entity.Group;
import com.bosch.eet.skill.management.usermanagement.repo.GroupRepository;

@DataJpaTest
@ActiveProfiles("dev")
public class GroupReposistoryTest {

	@Autowired
	private GroupRepository groupRepository;

	@Test
	void testFindByName() {
		Optional<Group> groupOpt1 = groupRepository.findByName("Associate");
		assertThat(groupOpt1).isPresent();
		Optional<Group> groupOpt2 = groupRepository.findByName("Associatessss");
		assertThat(groupOpt2).isNotPresent();
	}

	@Test
	void testFindByStatus() {
		List<Group> groups = groupRepository.findAllByStatus("Active");
		assertThat(groups).isNotEmpty();
		List<Group> groups1 = groupRepository.findAllByStatus("wrongstatus");
		 assertThat(groups1).isEmpty();
		
	}

	@Test
	void testFindByIdAndStatus() {
		Optional<Group> groupOpt1 = groupRepository.findByIdAndStatus("26aa55a5-a1ff-4625-ba88-5198c7b74dcb", "Active");
		assertThat(groupOpt1).isPresent();
		Optional<Group> groupOpt2 = groupRepository.findByIdAndStatus("26aa55a5-a1ff-4625-ba88-wronggroup", "Active");
		assertThat(groupOpt2).isNotPresent();
	}

	@Test
	void testGroupByStartPrefix() {
		List<Group> groups1 = groupRepository.findByNameStartingWith("Ma");
		assertThat(groups1).isNotEmpty();
		List<Group> groups2 = groupRepository.findByNameStartingWith("wrong");
		assertThat(groups2).isEmpty();
	}
	
	@Test
	void testExistByName() {
		assertThat(groupRepository.existsGroupByName("Associate")).isTrue();
		assertThat( groupRepository.existsGroupByName("Ma")).isFalse();
	}
	
}
