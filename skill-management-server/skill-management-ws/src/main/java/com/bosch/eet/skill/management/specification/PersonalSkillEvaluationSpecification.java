package com.bosch.eet.skill.management.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalSkillEvaluation;
import com.bosch.eet.skill.management.entity.SkillEvaluation;
import com.bosch.eet.skill.management.usermanagement.entity.User;

public class PersonalSkillEvaluationSpecification {
    private PersonalSkillEvaluationSpecification() {
        throw new IllegalStateException("ProjectSpecification class");
    }

    public static Specification<PersonalSkillEvaluation> search(Map<String, String> query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<PersonalSkillEvaluation, Personal> personalSkillEvaluationsUser = root.join("personal");
            Join<Personal, User> personalsUser = personalSkillEvaluationsUser.join("user");
            Join<PersonalSkillEvaluation, SkillEvaluation> personalSkillEvaluationSkillEvaluation = root.join("skillEvaluation");
            Join<SkillEvaluation, Personal> skillEvaluationPersonalJoin = personalSkillEvaluationSkillEvaluation.join("personal");
            Join<Personal, User> skillEvaluationPersonalUser = skillEvaluationPersonalJoin.join("user");
            String requester = query.get("requester");
            String approver = query.get("approver");
            if (StringUtils.hasLength(requester)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(skillEvaluationPersonalUser.get("name")), requester.toLowerCase()));
            }
            if (StringUtils.hasLength(approver)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(personalsUser.get("name")), approver.toLowerCase()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
