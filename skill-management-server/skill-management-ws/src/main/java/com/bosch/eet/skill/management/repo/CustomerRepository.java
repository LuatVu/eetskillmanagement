package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bosch.eet.skill.management.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {
	
	Optional<Customer> findByName(String name);
	
	List<Customer> findAllByOrderByName();
	
}
