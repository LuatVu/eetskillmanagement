package com.bosch.eet.skill.management.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bosch.eet.skill.management.entity.CommonTask;
import com.bosch.eet.skill.management.entity.ProjectRole;

public class CommonTasksSpecification {
	 private CommonTasksSpecification() {
	        throw new IllegalStateException("Common Tasks class");
	    }
	    public static Specification<CommonTask> search(Map<String, String> query) {
	        return (root, criteriaQuery, criteriaBuilder) -> {
	            List<Predicate> predicates = new ArrayList<>();
	            Join<CommonTask, ProjectRole> CommontasksProjectRole = root.join("projectRole");
	            String projectRoleId = query.get("project_role_id");
	            String commonTasks = query.get("common_task_id");
	            if (StringUtils.hasLength(projectRoleId)) {
	                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(CommontasksProjectRole.get("id")), projectRoleId.toLowerCase()));
	            }
	            if (StringUtils.hasLength(commonTasks)) {
	                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("id")), commonTasks.toLowerCase()));
	            }
	            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	        };
	    }
}
