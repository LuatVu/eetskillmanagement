package com.bosch.eet.skill.management.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.RequestEvaluation;
import com.bosch.eet.skill.management.entity.RequestEvaluationDetail;
import com.bosch.eet.skill.management.usermanagement.entity.User;

public class RequestEvaluationSpecification {
    private RequestEvaluationSpecification() {
        throw new IllegalStateException("RequestEvaluationSpecification class");
    }
    public static Specification<RequestEvaluation> search(Map<String, String> query) {
    	return (root, criteriaQuery, criteriaBuilder) -> {
    		List<Predicate> predicates = new ArrayList<>();
    		Join<RequestEvaluation, Personal> requesterRequestEvaluationUser = root.join("requester");
    		Join<Personal, User> requestsUser = requesterRequestEvaluationUser.join("user");
    		
    		Join<RequestEvaluation, RequestEvaluationDetail> reqDetail = root.join("requestEvaluationDetails");
    		Join<RequestEvaluationDetail, User> approversCompetencyLead = reqDetail.join("approver");
    		
    		String requester = query.get(Constants.REQUESTER_ID);
    		String approver = query.get(Constants.APPROVER_ID);
    		
    		if(StringUtils.hasLength(requester)) {
    			predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(requestsUser.get("name")), requester.toUpperCase() ));
    		}
    		
			if (StringUtils.hasLength(approver)) {
    			predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(approversCompetencyLead.get("personalCode")), approver.toUpperCase() ));
			}
    		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    	};
	} 
}
