package com.bosch.eet.skill.management.elasticsearch.repo;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.bosch.eet.skill.management.elasticsearch.document.DepartmentProjectDocument;

public interface ProjectSpringElasticRepository extends ElasticsearchRepository<DepartmentProjectDocument, String> {
}