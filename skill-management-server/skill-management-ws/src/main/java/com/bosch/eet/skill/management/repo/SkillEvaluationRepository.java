package com.bosch.eet.skill.management.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bosch.eet.skill.management.entity.SkillEvaluation;

public interface SkillEvaluationRepository extends JpaRepository<SkillEvaluation, String>, JpaSpecificationExecutor {

    boolean existsByPersonalIdAndSkillIdAndStatus(String personalId, String skillId, String status);
    List<SkillEvaluation> findAllByPersonalId(String personalId);
    List<SkillEvaluation> findAllBySkillId(String skillId);
}
