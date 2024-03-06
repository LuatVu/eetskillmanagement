package com.bosch.eet.skill.management.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bosch.eet.skill.management.entity.PageConfig;

public interface PageConfigRepository extends JpaRepository<PageConfig, String> {

	Optional<PageConfig> findById(String id);
}
