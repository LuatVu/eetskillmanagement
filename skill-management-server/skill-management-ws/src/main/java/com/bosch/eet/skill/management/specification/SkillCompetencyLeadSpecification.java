package com.bosch.eet.skill.management.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillCompetencyLead;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SkillCompetencyLeadSpecification {
    private SkillCompetencyLeadSpecification() {
        throw new IllegalStateException("SkillCompetencyLeadSpecification class");
    }    
    public static Specification<SkillCompetencyLead> search(Map<String, String> query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<SkillCompetencyLead, Skill> skillCompetencyLeadSkill = root.join("skill");
            String skillCompetencyLeadId = query.get(Constants.SKILL_COMPETENCY_LEAD);
            String skillId = query.get(Constants.SKILL_ID);
            if (StringUtils.hasLength(skillCompetencyLeadId)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(skillCompetencyLeadSkill.get("id")), skillCompetencyLeadId.toLowerCase()));
            }
            if (StringUtils.hasLength(skillId)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("id")), skillId.toLowerCase()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
