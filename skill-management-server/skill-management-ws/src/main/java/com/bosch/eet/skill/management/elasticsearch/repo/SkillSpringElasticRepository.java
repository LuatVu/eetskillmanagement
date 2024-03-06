package com.bosch.eet.skill.management.elasticsearch.repo;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.bosch.eet.skill.management.elasticsearch.document.SkillDocument;

public interface SkillSpringElasticRepository extends ElasticsearchRepository<SkillDocument, String> {
}
