package com.bosch.eet.skill.management.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bosch.eet.skill.management.entity.ProjectScope;

public interface ProjectScopeRepository extends JpaRepository<ProjectScope, String>, JpaSpecificationExecutor {
    Optional<ProjectScope> findByNameIgnoreCase(String name);
}
