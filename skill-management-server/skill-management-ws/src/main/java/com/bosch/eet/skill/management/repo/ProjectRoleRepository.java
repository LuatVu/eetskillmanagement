package com.bosch.eet.skill.management.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bosch.eet.skill.management.entity.ProjectRole;

public interface ProjectRoleRepository extends JpaRepository<ProjectRole, String>, JpaSpecificationExecutor {
    Optional<ProjectRole> findById(String name);
    Optional<ProjectRole> findByName(String name);
}
