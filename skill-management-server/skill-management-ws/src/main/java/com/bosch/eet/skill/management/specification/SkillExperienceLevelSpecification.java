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
import com.bosch.eet.skill.management.entity.SkillExperienceLevel;
import com.bosch.eet.skill.management.entity.SkillGroup;

public class SkillExperienceLevelSpecification {
    private SkillExperienceLevelSpecification() {
        throw new IllegalStateException("SkillCompetencyLeadSpecification class");
    }    
    public static Specification<SkillExperienceLevel> search(Map<String, String> query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<SkillExperienceLevel, Skill> skillExperienceLevelSkill = root.join("skill");
            Join<SkillGroup, SkillExperienceLevel> skillGroupSkillExperienceLevelJoin = root.join("skillGroup");
            String skillId = query.get(Constants.SKILL_ID);
            String skillExperienceLevelId = query.get(Constants.SKILL_EXP_LEVEL_ID);
            String skillGroupId = query.get(Constants.SKILL_GROUP_ID);
            if (StringUtils.hasLength(skillId)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(skillExperienceLevelSkill.get("id")), skillId.toLowerCase()));
            }
            if (StringUtils.hasLength(skillExperienceLevelId)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("id")), skillExperienceLevelId.toLowerCase()));
            }
            if (StringUtils.hasLength(skillGroupId)) {
                predicates.add(criteriaBuilder.equal(skillGroupSkillExperienceLevelJoin.get("id"), skillGroupId.toLowerCase()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
