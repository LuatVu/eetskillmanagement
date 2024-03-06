package com.bosch.eet.skill.management.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.bosch.eet.skill.management.entity.Department;
@Repository
public interface DepartmentRepository extends JpaRepository<Department, String>, JpaSpecificationExecutor {

	Optional<Department> findByName(String name);
	
}
