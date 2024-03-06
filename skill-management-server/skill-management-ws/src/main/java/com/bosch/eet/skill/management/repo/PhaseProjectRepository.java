package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bosch.eet.skill.management.entity.PhaseProject;
import com.bosch.eet.skill.management.entity.Project;

public interface PhaseProjectRepository extends JpaRepository<PhaseProject, String> {

    @Query("select pp.phase.id from PhaseProject as pp where pp.project.id=:projectId")
    Optional<List<String>> findPhaseIdByProjectId(String projectId);
    
    List<PhaseProject> deleteByProject(Project project);

    List<PhaseProject> findByProjectId(String projectId);
}
