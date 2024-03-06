package com.bosch.eet.skill.management.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.entity.GbUnit;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectType;

public class ProjectSpecification {
    private ProjectSpecification() {
        throw new IllegalStateException("ProjectSpecification class");
    }
    public static Specification<Project> search(Map<String, String> query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            String projectName = query.get(Constants.NAME);
            
            Join<Project, ProjectType> projectProjectType = null;
            String projectType = query.get(Constants.PROJECT_TYPE);
            
            String projectStatus = query.get(Constants.STATUS);
            
            Join<Project, GbUnit> projectGbUnit = null;
            String gbUnit = query.get(Constants.GB);
            
            if (StringUtils.hasLength(projectType)) {
            	projectProjectType = root.join(Constants.PROJECT_TYPE_JOIN);
                CriteriaBuilder.In<String> inClause = criteriaBuilder.in(criteriaBuilder.lower(projectProjectType.get(Constants.NAME)));
                for (String strProjectType : projectType.split(",")) {
                    inClause.value(strProjectType);
                }
                predicates.add(inClause);
            }

            if (StringUtils.hasLength(projectStatus)) {
                CriteriaBuilder.In<String> inClause = criteriaBuilder.in(criteriaBuilder.lower(root.get(Constants.STATUS)));
                for (String strProjectStatus : projectStatus.split(",")) {
                    inClause.value(strProjectStatus);
                }
                predicates.add(inClause);
            }

            if (StringUtils.hasLength(gbUnit)) {
            	projectGbUnit = root.join(Constants.GB_UNIT);
                CriteriaBuilder.In<String> inClause = criteriaBuilder.in(criteriaBuilder.lower(projectGbUnit.get(Constants.NAME)));
                for (String strGbUnit : gbUnit.split(",")) {
                    inClause.value(strGbUnit);
                }
                predicates.add(inClause);
            }

            if (StringUtils.hasLength(projectName)) {
                predicates.add(criteriaBuilder.like(root.get(Constants.NAME), "%" + query.get(Constants.NAME).toLowerCase() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
