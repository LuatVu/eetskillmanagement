package com.bosch.eet.skill.management.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bosch.eet.skill.management.entity.DepartmentGroup;

public interface DepartmentGroupRepository extends JpaRepository<DepartmentGroup, String>, JpaSpecificationExecutor {
}
