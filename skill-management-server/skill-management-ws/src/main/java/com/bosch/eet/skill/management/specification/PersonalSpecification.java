package com.bosch.eet.skill.management.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.entity.Team;
import com.bosch.eet.skill.management.usermanagement.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersonalSpecification {
    private PersonalSpecification() {
        throw new IllegalStateException("PersonalSpecification class");
    }


    public static Specification<Personal> search(Map<String, String> query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<Personal, User> personalsUser = root.join("user", JoinType.INNER);
            Join<Personal, Team> personalTeam = root.join(Constants.TEAM);
            Join<Personal, PersonalSkill> personalpersonalSkills = null;
            Join<PersonalSkill, Skill> personalSkillSkill = null;
            Join<Skill, SkillGroup> skillSkillgroup = null;

            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasLength(query.get("name"))) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(personalsUser.get("displayName")), "%" + query.get("name").toLowerCase() + "%"));
            }
            String teams = query.get(Constants.TEAM);
            if (StringUtils.hasLength(teams)) {
                log.info(personalTeam.get(Constants.NAME).toString());
                predicates.add(criteriaBuilder.like(personalTeam.get(Constants.NAME), "%" + query.get(Constants.TEAM).toLowerCase() + "%"));
            }
            String skills = query.get("skill");
            if (StringUtils.hasLength(skills)) {
                personalpersonalSkills = root.join("personalSkills", JoinType.INNER);
                personalSkillSkill = personalpersonalSkills.join("skill", JoinType.INNER);
                CriteriaBuilder.In<String> inClause = criteriaBuilder.in(criteriaBuilder.lower(personalSkillSkill.get("name")));
                for (String skill : skills.split(",")) {
                    inClause.value(skill);
                }
                predicates.add(inClause);
            }
            String skillGroups = query.get("skill-group");
            if (StringUtils.hasLength(skillGroups)) {
                if(personalpersonalSkills == null){
                    personalpersonalSkills = root.join("personalSkills", JoinType.INNER);
                    personalSkillSkill = personalpersonalSkills.join("skill", JoinType.INNER);
                }
                skillSkillgroup = personalSkillSkill.join("skillGroup", JoinType.INNER);
                CriteriaBuilder.In<String> inClause = criteriaBuilder.in(criteriaBuilder.lower(skillSkillgroup.get("name")));
                for (String skillGroup : skillGroups.split(",")) {
                    inClause.value(skillGroup);
                }
                predicates.add(inClause);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
