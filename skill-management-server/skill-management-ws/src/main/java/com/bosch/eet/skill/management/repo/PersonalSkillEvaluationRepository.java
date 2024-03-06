package com.bosch.eet.skill.management.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bosch.eet.skill.management.entity.PersonalSkillEvaluation;

public interface PersonalSkillEvaluationRepository extends JpaRepository<PersonalSkillEvaluation, String>, JpaSpecificationExecutor {
    Optional<PersonalSkillEvaluation> findBySkillEvaluationId(String skillEvaluationId);
}
