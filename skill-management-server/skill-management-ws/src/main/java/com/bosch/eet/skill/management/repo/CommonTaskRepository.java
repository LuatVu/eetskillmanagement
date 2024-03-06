package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bosch.eet.skill.management.entity.CommonTask;

public interface CommonTaskRepository extends JpaRepository<CommonTask, String>, JpaSpecificationExecutor {
    List<CommonTask> findByIdIn(List<String> ids);
    
	List<CommonTask> findByProjectRoleId(String roleProjectId);
	
	Optional<CommonTask> findByName(String commonTaskName);
	
	List<CommonTask> findAll(); 
}
