package com.bosch.eet.skill.management.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bosch.eet.skill.management.entity.GbUnit;

public interface GBRepository extends JpaRepository<GbUnit, String>, JpaSpecificationExecutor {
}
