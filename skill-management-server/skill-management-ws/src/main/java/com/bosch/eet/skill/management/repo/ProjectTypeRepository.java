package com.bosch.eet.skill.management.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bosch.eet.skill.management.entity.ProjectType;

public interface ProjectTypeRepository extends JpaRepository<ProjectType, String>, JpaSpecificationExecutor {
    Optional<ProjectType> findByName (String name);
}
