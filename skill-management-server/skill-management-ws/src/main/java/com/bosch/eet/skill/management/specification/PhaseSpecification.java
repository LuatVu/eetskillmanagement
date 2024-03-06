package com.bosch.eet.skill.management.specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
//import org.springframework.util.StringUtils;

import com.bosch.eet.skill.management.entity.Phase;

public class PhaseSpecification {
    private PhaseSpecification() {
        throw new IllegalStateException("PhaseSpecification class");
    }

    public static Specification<Phase> search(Map<String, String> query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(query.get("id"))) {
                predicates.add(criteriaBuilder.equal(root.get("id"), query.get("id")));
            }

            if (StringUtils.isNotBlank(query.get("name"))) {
                predicates.add(criteriaBuilder.equal(root.get("name"), query.get("name")));
            }

            if (StringUtils.isNotBlank(query.get("list_descriptions"))) {
                String listDescriptionStr = query.get("list_descriptions");
                List<Predicate> orPredicates = new ArrayList<>();

                List<String> phaseStrList = handleSpecialPhase(listDescriptionStr);

                for (String description: phaseStrList) {
                    description = description.trim();
                    orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
                }
                predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static List<String> handleSpecialPhase(String listDescriptionStr){
        String specialPhase = "Product Management - Planning, Creation and Delivery";
        ArrayList<String> phaseStrList;

        int indexOfPhase = listDescriptionStr.indexOf(specialPhase);
        if(indexOfPhase != -1) {
            if (listDescriptionStr.length() == specialPhase.length()) {
                phaseStrList = new ArrayList<>();
                phaseStrList.add(specialPhase);
            } else {
                if (indexOfPhase > 0) {
                    listDescriptionStr = listDescriptionStr.substring(0, indexOfPhase - 1) + listDescriptionStr.substring(indexOfPhase + specialPhase.length());
                } else {
                    listDescriptionStr = listDescriptionStr.substring(indexOfPhase + specialPhase.length() + 1);
                }
                phaseStrList = new ArrayList<>(Arrays.asList(listDescriptionStr.split(",")));
                phaseStrList.add(specialPhase);
            }
        } else {
            phaseStrList = new ArrayList<>(Arrays.asList(listDescriptionStr.split(",")));
        }
        return phaseStrList;
    }

}
