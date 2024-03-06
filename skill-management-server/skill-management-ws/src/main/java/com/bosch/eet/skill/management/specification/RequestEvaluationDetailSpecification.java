package com.bosch.eet.skill.management.specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.entity.RequestEvaluationDetail;

public class RequestEvaluationDetailSpecification {
	private RequestEvaluationDetailSpecification() {
		throw new IllegalStateException("RequestEvaluationDetailSpecification class");
	}

	public static Specification<RequestEvaluationDetail> search(String personalId, Map<String, String> query) {
		return (root, criteriaQuery, criteriaBuilder) -> {
			String skillCusterString = query.get("skillCluster");
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(criteriaBuilder.equal(root.get("requestEvaluation").get("requester").get("id"), personalId));
			predicates.add(criteriaBuilder.equal(root.get("status"), Constants.APPROVED));
			if (StringUtils.hasText(skillCusterString)) {
				List<String> skillClusters = Arrays.asList(skillCusterString.split(Constants.COMMA));
				skillClusters = skillClusters.stream().map(String::trim).collect(Collectors.toList());
				predicates.add(root.get("skill").get("skillGroup").get("name").in(skillClusters));
			}
			criteriaQuery.orderBy(criteriaBuilder.asc(root.get("skill")),
					criteriaBuilder.desc(root.get("createdDate")));
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
	
}
