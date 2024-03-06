package com.bosch.eet.skill.management.elasticsearch.repo;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.bosch.eet.skill.management.elasticsearch.document.PersonalDocument;

public interface PersonalSpringElasticRepository extends ElasticsearchRepository<PersonalDocument, String>{
}