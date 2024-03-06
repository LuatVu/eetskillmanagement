package com.bosch.eet.skill.management.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalCourse;

public class PersonalCourseSpecification {
    private PersonalCourseSpecification() {
        throw new IllegalStateException("PersonalCourse class");
    }
    public static Specification<PersonalCourse> search(Map<String, String> query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<PersonalCourse, Personal> personalCoursePersonal = root.join("personal");
            String personalId = query.get("personal_id");
            String personalCourseId = query.get("personal_course_id");
            if (StringUtils.hasLength(personalId)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(personalCoursePersonal.get("id")), personalId.toLowerCase()));
            }
            if (StringUtils.hasLength(personalCourseId)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("id")), personalCourseId.toLowerCase()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    
}
