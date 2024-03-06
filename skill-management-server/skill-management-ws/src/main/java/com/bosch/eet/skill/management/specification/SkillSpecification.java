package com.bosch.eet.skill.management.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.entity.SkillLevel;


public class SkillSpecification {
	 private SkillSpecification() {
	        throw new IllegalStateException("SkillSpecification class");
	 }
	 
	  public static Specification<Skill> search(List<String> skillCluster) {
	        return (root, criteriaQuery, criteriaBuilder) -> {
	            List<Predicate> predicates = new ArrayList<>();
	            Join<SkillLevel, SkillGroup> skillLevellSkillGroup = root.join(Constants.SKILL_GROUP, JoinType.INNER);
				if (!CollectionUtils.isEmpty(skillCluster)) {
					CriteriaBuilder.In<String> inClause = criteriaBuilder
							.in(criteriaBuilder.lower(skillLevellSkillGroup.get(Constants.NAME)));
					for (String sc : skillCluster) {
						inClause.value(sc);
					}
					 predicates.add(inClause);
				}
	            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	        };
	    }
}
