package com.bosch.eet.skill.management.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bosch.eet.skill.management.entity.Phase;

public interface PhaseRepository extends JpaRepository<Phase, String>, JpaSpecificationExecutor<Phase> {
}
