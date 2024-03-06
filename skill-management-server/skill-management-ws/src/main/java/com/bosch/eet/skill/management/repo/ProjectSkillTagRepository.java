package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectSkillTag;
import com.bosch.eet.skill.management.entity.SkillTag;

public interface ProjectSkillTagRepository extends JpaRepository<ProjectSkillTag, String>{
	
	@Query("select pst.skillTag from ProjectSkillTag pst where pst.project.id=:projectId")
	List<SkillTag> findSkillTagsByProjectsId (String projectId);
	
	void deleteByProjectId(String projectId);
	
	Optional<List<ProjectSkillTag>> findByProjectId (String projectId);

	@Query("select pst.project from ProjectSkillTag pst where pst.skillTag.id=:skillTagId")
	List<Project> findProjectBySkillTagId(String skillTagId);

	@Query("select pst from ProjectSkillTag pst where pst.skillTag.id=:skillTagId")
	List<ProjectSkillTag> findBySkillTagId(String skillTagId);

	@Modifying
	@Query("update ProjectSkillTag pst set pst.skillTag.id=:newSkillTagId where pst.skillTag.id=:skillTagWillBeReplacedId")
	void replaceSkillTag(String skillTagWillBeReplacedId, String newSkillTagId);
	
	List<ProjectSkillTag> deleteByProject(Project project);
	
	@Query("select pst.project.id from ProjectSkillTag pst where pst.skillTag.id=:skillTagId")
	List<String> findProjectIdBySkillTagId(String skillTagId);
	
	@Query("select distinct pst.skillTag from ProjectSkillTag pst")
	List<SkillTag> findAllSkillTags();
	
	@Query("select new com.bosch.eet.skill.management.dto.ProjectDto(pst.project.id, pst.project.name) "
			+ "from ProjectSkillTag pst where pst.project.customer.id=:customerId "
			+ "and pst.skillTag.name=:skillTagName")
	List<ProjectDto> findProjectsByCustomerIdAndSkillTagName(String customerId, String skillTagName);
}
