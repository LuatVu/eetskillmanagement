package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.dto.CustomerDetailDTO;
import com.bosch.eet.skill.management.dto.CustomerGbDto;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.entity.Customer;
import com.bosch.eet.skill.management.entity.Phase;
import com.bosch.eet.skill.management.entity.PhaseProject;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectSkillTag;
import com.bosch.eet.skill.management.entity.ProjectType;
import com.bosch.eet.skill.management.entity.SkillTag;
import com.bosch.eet.skill.management.exception.BadRequestException;
import com.bosch.eet.skill.management.exception.ResourceNotFoundException;
import com.bosch.eet.skill.management.repo.CustomerRepository;
import com.bosch.eet.skill.management.repo.PhaseProjectRepository;
import com.bosch.eet.skill.management.repo.PhaseRepository;
import com.bosch.eet.skill.management.repo.ProjectRepository;
import com.bosch.eet.skill.management.repo.ProjectSkillTagRepository;
import com.bosch.eet.skill.management.repo.SkillTagRepository;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class CustomerServiceTestWithoutMock {

	private static final String DATA_JPA_TEST_4 = "Data jpa test 4";

	private static final String DATA_JPA_TEST_3 = "Data jpa test 3";

	private static final String DATA_JPA_TEST_2 = "Data jpa test 2";

	private static final String DATA_JPA_TEST_1 = "Data jpa test 1";

	private Customer customer1;

	private Customer customer2;

	private Customer customer3;

	private Customer customer4;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private SkillTagRepository skillTagRepository;

	@Autowired
	private ProjectSkillTagRepository projectSkillTagRepository;

	@Autowired
	private PhaseRepository phaseRepository;

	@Autowired
	private PhaseProjectRepository phaseProjectRepository;

	@BeforeEach
	void prepareData() throws ParseException {
		customer1 = Customer.builder().name(DATA_JPA_TEST_1).hightlight("Highlight").corporation("EDL, DEL").build();
		customer2 = Customer.builder().name(DATA_JPA_TEST_2).build();
		customer3 = Customer.builder().name(DATA_JPA_TEST_3).build();
		customer4 = Customer.builder().name(DATA_JPA_TEST_4).build();
		List<Customer> customers = new ArrayList<>();
		customers.add(customer1);
		customers.add(customer2);
		customers.add(customer3);
		customers.add(customer4);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date = format.parse("2023/02/02");
		ProjectType projectType = ProjectType.builder().id("b8987dfa-f6e2-99d3-f511-3df3286bd7c0").name("Bosch")
				.build();
		SkillTag skillTag1 = SkillTag.builder().name("Name skill tag").order(Long.MAX_VALUE).build();
		SkillTag skillTag2 = SkillTag.builder().name("Name skill tag 2").order(Long.MAX_VALUE - 1).build();
		List<SkillTag> skillTags = new ArrayList<>(Arrays.asList(skillTag1, skillTag2));
		Phase phase = phaseRepository.findById("2f9675a5-01e6-b521-1a4c-d9a12a84a15e").get();
		Project project = Project.builder().name("Data jpa project").startDate(date).projectType(projectType)
				.teamSize("2").createdBy("GLO7HC").createdDate(date).customer(customer1).phaseProjects(null)
				.hightlight("").problemStatement("").solution("").benefits("").build();
		ProjectSkillTag projectSkillTag1 = ProjectSkillTag.builder().project(project).skillTag(skillTag1).build();
		ProjectSkillTag projectSkillTag2 = ProjectSkillTag.builder().project(project).skillTag(skillTag2).build();
		List<ProjectSkillTag> projectSkillTags = new ArrayList<>(Arrays.asList(projectSkillTag1, projectSkillTag2));
		project.setProjectSkillTags(new HashSet<>(projectSkillTags));
		customer1.setProjects(Collections.singletonList(project));
		customerRepository.saveAllAndFlush(customers);
		projectRepository.saveAndFlush(project);
		skillTagRepository.saveAllAndFlush(skillTags);
		projectSkillTagRepository.saveAllAndFlush(projectSkillTags);
		PhaseProject phaseProject = PhaseProject.builder().project(project).phase(phase).build();
		phase.setPhaseProjects(Collections.singletonList(phaseProject));
		project.setPhaseProjects(Collections.singletonList(phaseProject));
		phaseProjectRepository.saveAndFlush(phaseProject);
		customerService.syncCustomerGBToElastic();
	}

	@Test
	@DisplayName("Find all customer gb")
	void testFindAll() {
		List<CustomerGbDto> customerDTOs = customerService.findAll();
		assertThat(customerDTOs).hasSizeGreaterThanOrEqualTo(4);
		List<String> customerNames = customerDTOs.stream().map(item -> item.getGbName()).collect(Collectors.toList());
		assertThat(customerNames).contains(DATA_JPA_TEST_1, DATA_JPA_TEST_2, DATA_JPA_TEST_3, DATA_JPA_TEST_4);
	}

	@Test
	@DisplayName("Find customer by id")
	void testFindById() {
		CustomerDetailDTO customerDetailDTO = customerService.findById(customer1.getId());
		assertThat(DATA_JPA_TEST_1).isEqualTo(customerDetailDTO.getName());
		assertThat("EDL, DEL").isEqualTo(customerDetailDTO.getCorporation()); 
		assertThat("Highlight").isEqualTo(customerDetailDTO.getHightlight());
		assertThat(customerDetailDTO.getGbInfo().keySet().size()).isEqualTo(2);  
		assertThat(customerDetailDTO.getGbInfo().get("Name skill tag")).isEqualTo(1);  
		assertThat(customerDetailDTO.getHeadCounts()).isEqualTo(2);  
		assertThat(customerDetailDTO.getVModelCount()).isEqualTo(1); 
	}

	@Test
	@DisplayName("Find customer by id - Resource not found")
	void testFindById_ResourceNotFound() {
		assertThrows(ResourceNotFoundException.class, () -> customerService.findById("idnotexist"));
	}

	@Test
	@DisplayName("Create customer")
	void testCreate() throws IOException {
		CustomerDetailDTO customerDetailDto = CustomerDetailDTO.builder().name("Name test").hightlight("")
				.corporation("DEL, DDE").build();
		CustomerDetailDTO customerDetailDTO1 = customerService.create(customerDetailDto);
		assertNotEquals(null, customerDetailDTO1.getId());
		assertThat(customerDetailDto.getName()).isEqualTo(customerDetailDTO1.getCorporation());
		assertThat(customerDetailDto.getCorporation()).isEqualTo(customerDetailDTO1.getCorporation());
		assertThat(customerDetailDto.getHightlight()).isEqualTo(customerDetailDTO1.getHightlight());
	}

	@Test
	@DisplayName("Create customer - Name duplicated")
	void testCreate_NameDuplicated() {
		CustomerDetailDTO customerDetailDto = CustomerDetailDTO.builder().name(DATA_JPA_TEST_1).hightlight("")
				.corporation("DEL, DDE").build();
		Exception exception = assertThrows(BadRequestException.class, () -> customerService.create(customerDetailDto));
		assertThat("Customer duplicated").isEqualTo(exception.getMessage());
	}

	@Test
	@DisplayName("Create customer - Name too long")
	void testCreate_NameTooLong() {
		CustomerDetailDTO customerDetailDto = CustomerDetailDTO.builder().name(
				"1U1OhUvT85saUwY0Ox2eMYKWlJ2LPBEgMHoZ5xg3Zr0aosBHveL4gNx9qzUi5Fl3yx17FbTQRjeeYjWR4kpkipnSGdfxHj6em3YgVbUq3ciQvu3hawVyLpQBName test")
				.hightlight("").corporation("DEL, DDE").build();
		Exception exception = assertThrows(BadRequestException.class, () -> customerService.create(customerDetailDto));
		assertThat("Customer name too long").isEqualTo(exception.getMessage());
	}

	@Test
	@DisplayName("Create customer - Highlight too long")
	void testCreate_HighlightTooLong() {
		CustomerDetailDTO customerDetailDto = CustomerDetailDTO.builder().name("Name test").hightlight(
				"mQKQIVlzqb9IbCWe9jFVPiAetv9xeUHdpYraZp6urAeHy6kCC15T8EOmoVUAgMjsQjVoO16Cdhyl0SFpbeDzblXVFmsTWOwiyAT9fX"
						+ "kWowEJKXO6LORXd3x0gvYTPETbc2agqVX8X8FG1SoCoDY6McdwyTi3fkD3iqgC5ifWHPsGitVSTB7y1rmPTPCsiqxEMTA1fEHSlcP"
						+ "46SIwTGdZvnvzUEDiTpvDE8Q4ZnAp6QbtFlEe09b6TGeuTUWOJ3Y3bkSR0CQOP6KtYBfGuDBHw8zlcqubYQfg5C6MxXC4OcN72TwJn"
						+ "tWSv94m8AM80KpuJ5N0SRQ0xbUuHuMMfvAheGdah4gGo25zgGoEa6cPC8NSkdeSuuz1XB2Pu0rl7l5PUXzaDS4aEyRC6QPiqpc0wJtq"
						+ "nMadYb9BYcijTmw5PWVzAYhroLG8LLhDbrgfjXQVb39k5MKX6a6Yk9RZP6o8loWRjZCpnLbWNuf3kaM9VX4Gjbl9YzLn\r\n"
						+ "")
				.corporation("DEL, DDE").build();
		Exception exception = assertThrows(BadRequestException.class, () -> customerService.create(customerDetailDto));
		assertThat("Customer highlight too long").isEqualTo(exception.getMessage());
	}

	@Test
	@DisplayName("Create customer - Corporation too long")
	void testCreate_CorporationTooLong() {
		CustomerDetailDTO customerDetailDto = CustomerDetailDTO.builder().name("Name test").hightlight("Hightlight")
				.corporation(
						"e6E78rHnAREjZ8zRWDhYoO6nqsjIBWZg0LwOiR217CFPBThcpPMYUAO5Xt2xsQOodMQ6Uc9V39nJqezo2i6yIoMSW7"
								+ "FL5WWOnbqNrp3rWeS7Lp5FAYdXIAumjRZE9jfTri7jaawJd0V3T081WXR09D1eR0aom8VoaxyzWj1kjb2tdFOT9Kh2mKql"
								+ "01Ixv1WAfzSVV04ClCT4rjzWAqT6Lz9mKTKwaz3GZfOWcjwJlK6rdOONUJ0FPfOMnHkBunzms5IJi5RS2PLwjY44FgREjIz"
								+ "9MdCiyb9d9tiOaVGfrj9ystdTYALGfpWAgmqddDoWWJ1KrcFcXVRiYxaz92ZE13OQMcfAfZp2b3TucjLXowOR2yDLJVgOZqk"
								+ "ZTeNjlht6hc2YQsZH7raK60FUMYVlGKwDzNUxBbHPm3B2Zx2NGwXZzHJlTs5xLmKpHrogydsGyRweQDZbAOxNEUYxSNE43yOK"
								+ "sUULUrZDmnvlEUGYsBMwi68ASBo2KXecIeaR45b37nmgyb6ok4KDvv63s24uy8oDrV2oiO3tjH9Lhp7As85mSe5OtjDHG5xrvs"
								+ "32TtPKh0ruYtO4TzTYeIF1tlgVEoC08HV4ZdY906JZAoXAviDeBTTBg1sCrOyv2RW2v2OsPjRvfvGwe1Kz1NsXof9T2nxL8bcOo"
								+ "10pEicvY6mVLzm3qIRzuxteogTey7atQtENFmi6vwTjhWRBsahbY9EfYVGPownp7dT8FdmJykzus84F50ul1BJkLq4cKMFcUyU13"
								+ "I60EVBYtnLV5JafmHjLpNymZqVDh3nGjhxAGn9BuAsucfm0FQPVKS5KyoHab2f8GcMJnz6N1QXTIdvE9BDBFqyR7D535mtD3JCi8t"
								+ "tUzaTvPzMAmengS6WHIn1hMgKrmzyfBZnBZRmh9pmpGufqT2x6af6wz5xSWvFvjDKzmtPGlqcCWDy9NIWH6N8fgZBPOmnBhjKgSKux"
								+ "tmYLRVb01qkCY1JK6TcU5ZZC7KZl\r\n" + "")
				.build();
		Exception exception = assertThrows(BadRequestException.class, () -> customerService.create(customerDetailDto));
		assertThat("Customer corporation too long").isEqualTo(exception.getMessage());
	}

	@Test
	@DisplayName("Update customer")
	void testUpdate() throws IOException {
		CustomerDetailDTO customerDetailDto = CustomerDetailDTO.builder().id(customer2.getId())
				.name("Name customer new").hightlight("Highlight added").corporation("DEL, DDE").build();
		assertThat(customer2.getId()).isEqualTo(customerDetailDto.getId());
		assertNotEquals(customer2.getName(), customerDetailDto.getName());
		assertNotEquals(customer2.getCorporation(), customerDetailDto.getCorporation());
		assertNotEquals(customer2.getHightlight(), customerDetailDto.getHightlight());
		customerService.update(customerDetailDto);
		Customer customerSaved = customerRepository.findById(customer2.getId()).get();
		assertThat(customerDetailDto.getId()).isEqualTo(customerSaved.getId());
		assertThat(customerDetailDto.getName()).isEqualTo(customerSaved.getName());
		assertThat(customerDetailDto.getCorporation()).isEqualTo(customerSaved.getCorporation());
		assertThat(customerDetailDto.getHightlight()).isEqualTo(customerSaved.getHightlight());

	}

	@Test
	@DisplayName("Update customer - Name duplicated")
	void testUpdate_NameDuplicated() {
		CustomerDetailDTO customerDetailDto = CustomerDetailDTO.builder().id(customer2.getId()).name(DATA_JPA_TEST_1)
				.hightlight("Highlight added").corporation("DEL, DDE").build();
		Exception exception = assertThrows(BadRequestException.class, () -> customerService.update(customerDetailDto));
		assertThat("Customer duplicated").isEqualTo(exception.getMessage());
	}

	@Test
	@DisplayName("Update customer - Can not find customer")
	void testUpdate_CannotFindCustomer() {
		CustomerDetailDTO customerDetailDto = CustomerDetailDTO.builder().id("idnotexists").name(DATA_JPA_TEST_1)
				.hightlight("Highlight added").corporation("DEL, DDE").build();
		Exception exception = assertThrows(ResourceNotFoundException.class,
				() -> customerService.update(customerDetailDto));
		assertThat("Customer not found").isEqualTo(exception.getMessage());
	}

	@Test
	@DisplayName("Update customer - Name too long")
	void testUpdate_NameTooLong() {
		CustomerDetailDTO customerDetailDto = CustomerDetailDTO.builder().id(customer2.getId()).name(
				"1U1OhUvT85saUwY0Ox2eMYKWlJ2LPBEgMHoZ5xg3Zr0aosBHveL4gNx9qzUi5Fl3yx17FbTQRjeeYjWR4kpkipnSGdfxHj6em3YgVbUq3ciQvu3hawVyLpQBName test")
				.hightlight("").corporation("DEL, DDE").build();
		Exception exception = assertThrows(BadRequestException.class, () -> customerService.update(customerDetailDto));
		assertThat("Customer name too long").isEqualTo(exception.getMessage());

	}

	@Test
	@DisplayName("Update customer - Highlight too long")
	void testUpdate_HighlightTooLong() {
		CustomerDetailDTO customerDetailDto = CustomerDetailDTO.builder().id(customer2.getId()).name("Name test")
				.hightlight(
						"mQKQIVlzqb9IbCWe9jFVPiAetv9xeUHdpYraZp6urAeHy6kCC15T8EOmoVUAgMjsQjVoO16Cdhyl0SFpbeDzblXVFmsTWOwiyAT9fX"
								+ "kWowEJKXO6LORXd3x0gvYTPETbc2agqVX8X8FG1SoCoDY6McdwyTi3fkD3iqgC5ifWHPsGitVSTB7y1rmPTPCsiqxEMTA1fEHSlcP"
								+ "46SIwTGdZvnvzUEDiTpvDE8Q4ZnAp6QbtFlEe09b6TGeuTUWOJ3Y3bkSR0CQOP6KtYBfGuDBHw8zlcqubYQfg5C6MxXC4OcN72TwJn"
								+ "tWSv94m8AM80KpuJ5N0SRQ0xbUuHuMMfvAheGdah4gGo25zgGoEa6cPC8NSkdeSuuz1XB2Pu0rl7l5PUXzaDS4aEyRC6QPiqpc0wJtq"
								+ "nMadYb9BYcijTmw5PWVzAYhroLG8LLhDbrgfjXQVb39k5MKX6a6Yk9RZP6o8loWRjZCpnLbWNuf3kaM9VX4Gjbl9YzLn\r\n"
								+ "")
				.corporation("DEL, DDE").build();
		Exception exception = assertThrows(BadRequestException.class, () -> customerService.update(customerDetailDto));
		assertThat("Customer highlight too long").isEqualTo(exception.getMessage());
	}

	@Test
	@DisplayName("Update customer - Corporation too long")
	void testUpdate_CorporationTooLong() {
		CustomerDetailDTO customerDetailDto = CustomerDetailDTO.builder().id(customer2.getId()).name("Name test")
				.hightlight("Hightlight")
				.corporation(
						"e6E78rHnAREjZ8zRWDhYoO6nqsjIBWZg0LwOiR217CFPBThcpPMYUAO5Xt2xsQOodMQ6Uc9V39nJqezo2i6yIoMSW7"
								+ "FL5WWOnbqNrp3rWeS7Lp5FAYdXIAumjRZE9jfTri7jaawJd0V3T081WXR09D1eR0aom8VoaxyzWj1kjb2tdFOT9Kh2mKql"
								+ "01Ixv1WAfzSVV04ClCT4rjzWAqT6Lz9mKTKwaz3GZfOWcjwJlK6rdOONUJ0FPfOMnHkBunzms5IJi5RS2PLwjY44FgREjIz"
								+ "9MdCiyb9d9tiOaVGfrj9ystdTYALGfpWAgmqddDoWWJ1KrcFcXVRiYxaz92ZE13OQMcfAfZp2b3TucjLXowOR2yDLJVgOZqk"
								+ "ZTeNjlht6hc2YQsZH7raK60FUMYVlGKwDzNUxBbHPm3B2Zx2NGwXZzHJlTs5xLmKpHrogydsGyRweQDZbAOxNEUYxSNE43yOK"
								+ "sUULUrZDmnvlEUGYsBMwi68ASBo2KXecIeaR45b37nmgyb6ok4KDvv63s24uy8oDrV2oiO3tjH9Lhp7As85mSe5OtjDHG5xrvs"
								+ "32TtPKh0ruYtO4TzTYeIF1tlgVEoC08HV4ZdY906JZAoXAviDeBTTBg1sCrOyv2RW2v2OsPjRvfvGwe1Kz1NsXof9T2nxL8bcOo"
								+ "10pEicvY6mVLzm3qIRzuxteogTey7atQtENFmi6vwTjhWRBsahbY9EfYVGPownp7dT8FdmJykzus84F50ul1BJkLq4cKMFcUyU13"
								+ "I60EVBYtnLV5JafmHjLpNymZqVDh3nGjhxAGn9BuAsucfm0FQPVKS5KyoHab2f8GcMJnz6N1QXTIdvE9BDBFqyR7D535mtD3JCi8t"
								+ "tUzaTvPzMAmengS6WHIn1hMgKrmzyfBZnBZRmh9pmpGufqT2x6af6wz5xSWvFvjDKzmtPGlqcCWDy9NIWH6N8fgZBPOmnBhjKgSKux"
								+ "tmYLRVb01qkCY1JK6TcU5ZZC7KZl\r\n" + "")
				.build();
		Exception exception = assertThrows(BadRequestException.class, () -> customerService.update(customerDetailDto));
		assertThat("Customer corporation too long").isEqualTo(exception.getMessage());
	}

	@Test
	@DisplayName("Delete customer")
	void testDelete() throws IOException {
		String customerId = customer1.getId();
		String result = customerService.delete(customerId);
		assertThat(Constants.SUCCESS).isEqualTo(result);
	}

	@Test
	@DisplayName("Delete customer - Customer not found")
	void testDelete_CustomerNotFoud() throws IOException {
		String customerId = "id";
		Exception exception = assertThrows(ResourceNotFoundException.class, () -> customerService.delete(customerId));
		assertThat("Customer not found").isEqualTo(exception.getMessage());
	}

	@Test
	@DisplayName("Fill all name customer for filter")
	void findAllCustomerForFilter() {
		List<String> customers = customerService.getAllCustomerGbFilters();
		assertThat(customers).hasSizeGreaterThanOrEqualTo(4);
		assertThat(customers).contains(customer1.getName(), customer2.getName(), customer3.getName(),
				customer4.getName(), "idwrong");
	}

	@Test
	@DisplayName("Find projects by customer id and skill tag name")
	void findProjectsByCustomerIdAndSkillTagName() {
		List<ProjectDto> projectDtos = customerService.getProjectsByCustomerAndSkillTag(customer1.getId(),
				"Name skill tag");
		assertThat(projectDtos).hasSize(1);
		assertThat("Data jpa project").isEqualTo( projectDtos.get(0).getName());
		projectDtos = projectSkillTagRepository.findProjectsByCustomerIdAndSkillTagName(customer1.getId(),
				"Name skill tag 2");
		assertThat(projectDtos).hasSize(1);
		assertThat("Data jpa project").isEqualTo( projectDtos.get(0).getName());
	}
}
