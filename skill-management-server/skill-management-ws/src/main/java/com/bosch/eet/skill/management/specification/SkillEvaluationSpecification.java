package com.bosch.eet.skill.management.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.SkillEvaluation;
import com.bosch.eet.skill.management.usermanagement.entity.User;

public class SkillEvaluationSpecification {
    private SkillEvaluationSpecification() {
        throw new IllegalStateException("ProjectSpecification class");
    }

    public static Specification<SkillEvaluation> search(Map<String, String> query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<SkillEvaluation, Personal> personalSkillEvaluationsUser = root.join("personal");
            Join<Personal, User> personalsUser = personalSkillEvaluationsUser.join("user");
            Join<SkillEvaluation, Personal> approverSkillEvaluationsUser = root.join("approver");
            Join<Personal, User> approversUser = approverSkillEvaluationsUser.join("user");
            String requester = query.get("requester");
            String approver = query.get("approver");
            if (StringUtils.hasLength(requester)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(personalsUser.get("name")), requester.toLowerCase()));
            }
            if (StringUtils.hasLength(approver)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(approversUser.get("name")), approver.toLowerCase()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
