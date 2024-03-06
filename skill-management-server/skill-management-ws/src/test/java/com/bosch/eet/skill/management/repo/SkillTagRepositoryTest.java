package com.bosch.eet.skill.management.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.dto.SkillTagDto;
import com.bosch.eet.skill.management.entity.Customer;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectSkillTag;
import com.bosch.eet.skill.management.entity.ProjectType;
import com.bosch.eet.skill.management.entity.SkillTag;

@DataJpaTest
@ActiveProfiles("dev")
public class SkillTagRepositoryTest {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private SkillTagRepository skillTagRepository;
	
	@Autowired
	private ProjectSkillTagRepository projectSkillTagRepository;

	private SkillTag skillTag1;
	
	private SkillTag skillTag2;
	
	private Project project;
	
	@BeforeEach
	void prepare() throws ParseException {
		projectSkillTagRepository.deleteAll();
		skillTagRepository.deleteAll();
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date = format.parse("2023/02/02");
		ProjectType projectType = ProjectType.builder().id("b8987dfa-f6e2-99d3-f511-3df3286bd7c0").name("Bosch")
				.build();
		Customer customer = Customer.builder().name("Data jpa test").build();
		customerRepository.saveAndFlush(customer);
		project = Project.builder().name("Data jpa project").startDate(date).projectType(projectType)
				.teamSize("2").createdBy("GLO7HC").createdDate(date).customer(customer).phaseProjects(null)
				.hightlight("").problemStatement("").solution("").benefits("").build();
		projectRepository.saveAndFlush(project);
		skillTag1 = SkillTag.builder().name("Name skill tag").order(Long.MAX_VALUE).build();
		skillTag2 = SkillTag.builder().name("Name skill tag #").order(Long.MAX_VALUE - 1).build();
		ProjectSkillTag projectSkillTag1 = ProjectSkillTag.builder().project(project).skillTag(skillTag1).build();
		ProjectSkillTag projectSkillTag2 = ProjectSkillTag.builder().project(project).skillTag(skillTag2).build();
		skillTagRepository.saveAllAndFlush(Arrays.asList(skillTag1, skillTag2));
		projectSkillTagRepository.saveAllAndFlush(Arrays.asList(projectSkillTag1, projectSkillTag2));
	}
	
	@Test
	@DisplayName("Find projects by customer id and skill tag name")
	void findAllDetailDtoCountProjectByType() {
		List<SkillTagDto> skillTagDtos = skillTagRepository.findAllDetailDtoCountProjectByType(Constants.BOSCH);
		assertThat(skillTagDtos).isNotEmpty();
		assertThat(skillTagDtos.get(0).getProjectCount()).isEqualTo(1);
		
	}
}
