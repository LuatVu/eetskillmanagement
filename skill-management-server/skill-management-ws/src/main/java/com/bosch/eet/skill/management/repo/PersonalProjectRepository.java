package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bosch.eet.skill.management.entity.PersonalProject;

public interface PersonalProjectRepository extends JpaRepository<PersonalProject, String>, JpaSpecificationExecutor {
    //    @Query("SELECT e FROM PersonalProject e  INNER JOIN Personal b on e.personal.id = b.id WHERE b.id IN (:ids)")
    List<PersonalProject> findByPersonalIdAndProjectId(String ids, String projectId);
    Optional<List<PersonalProject>> findByProjectId(String projectId);
    Page<PersonalProject> findAll(Specification specification, Pageable pageable);
    List<PersonalProject> findByProjectRoleId(String projectRoleId);
    List<PersonalProject> findByPersonalId(String personalId);
	
	Optional<PersonalProject> findByIdNotAndPersonalIdAndProjectId(String id, String personalId, String projectId);
	void deleteByProjectId(String projectId);
}
