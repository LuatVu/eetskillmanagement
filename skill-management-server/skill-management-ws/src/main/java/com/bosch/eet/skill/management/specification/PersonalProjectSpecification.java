package com.bosch.eet.skill.management.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalProject;

public class PersonalProjectSpecification {
    private PersonalProjectSpecification() {
        throw new IllegalStateException("ProjectSpecification class");
    }
    public static Specification<PersonalProject> search(Map<String, String> query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<PersonalProject, Personal> personalProjectPersonal = root.join("personal");
            String personalId = query.get("personal_id");
            String personalProjectId = query.get("personal_project_id");
            if (StringUtils.hasLength(personalId)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(personalProjectPersonal.get("id")), personalId.toLowerCase()));
            }
            if (StringUtils.hasLength(personalProjectId)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("id")), personalProjectId.toLowerCase()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
