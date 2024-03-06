package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bosch.eet.skill.management.entity.ProjectSkillGroupSkill;

public interface ProjectSkillGroupSkillRepository extends JpaRepository<ProjectSkillGroupSkill, String> {
    @Query("select psgs.skill.id from ProjectSkillGroupSkill as psgs where psgs.project.id=:projectId")
    Optional<List<String>> findSkillIdByProjectId(String projectId);
}
