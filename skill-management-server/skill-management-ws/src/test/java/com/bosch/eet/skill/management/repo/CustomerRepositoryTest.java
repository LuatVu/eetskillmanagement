package com.bosch.eet.skill.management.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.bosch.eet.skill.management.entity.Customer;

@DataJpaTest
@ActiveProfiles("dev")
public class CustomerRepositoryTest {
	
	@Autowired
	private CustomerRepository customerRepository;

	@Test
	@DisplayName("Find by name")
	void findByName() throws ParseException {
		Customer customer = Customer.builder().name("Data jpa test").build();
		customerRepository.saveAndFlush(customer);
		Optional<Customer> customerOpt1 = customerRepository.findByName(customer.getName());
		assertThat(customerOpt1).isPresent();
		Optional<Customer> customerOpt2 = customerRepository.findByName("Data jpa test 2");
		assertThat(customerOpt2).isNotPresent();
	}
}
