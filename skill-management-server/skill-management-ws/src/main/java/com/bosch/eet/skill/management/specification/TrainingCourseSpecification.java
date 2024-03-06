package com.bosch.eet.skill.management.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.entity.Course;
import com.bosch.eet.skill.management.entity.TrainingCourse;

public class TrainingCourseSpecification {
    private TrainingCourseSpecification() {
        throw new IllegalStateException("TrainingCourse class");
    }
    public static Specification<TrainingCourse> search(Map<String, String> query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<TrainingCourse, Course> trainingCoursetraining = root.join(Constants.COURSE);
            String courseId = query.get(Constants.COURSE_ID);
            String trainingCourseId = query.get(Constants.TRAINING_COURSE_ID);
            String learningType = query.get(Constants.COURSE_TYPE);
            String status = query.get(Constants.STATUS);
            if (StringUtils.hasLength(courseId)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(trainingCoursetraining.get(Constants.ID)), courseId.toLowerCase()));
            }
            if (StringUtils.hasLength(trainingCourseId)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get(Constants.ID)), trainingCourseId.toLowerCase()));
            }
            if (StringUtils.hasLength(learningType)) {
                CriteriaBuilder.In<String> inClause = criteriaBuilder.in(criteriaBuilder.lower(trainingCoursetraining.get(Constants.COURSE_TYPE_JOIN)));
                for (String type : learningType.split(",")) {
                    inClause.value(type);
                }
                predicates.add(inClause);
            }
            if (StringUtils.hasLength(status)) {
                CriteriaBuilder.In<String> inClause = criteriaBuilder.in(criteriaBuilder.lower(root.get(Constants.STATUS)));
                for (String subStatus : status.split(",")) {
                    inClause.value(subStatus);
                }
                predicates.add(inClause);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    
}
