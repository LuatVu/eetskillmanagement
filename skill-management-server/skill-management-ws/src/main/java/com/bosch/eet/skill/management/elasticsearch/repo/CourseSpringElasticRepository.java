package com.bosch.eet.skill.management.elasticsearch.repo;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.bosch.eet.skill.management.elasticsearch.document.DepartmentCourseDocument;

public interface CourseSpringElasticRepository extends ElasticsearchRepository<DepartmentCourseDocument, String> {
}