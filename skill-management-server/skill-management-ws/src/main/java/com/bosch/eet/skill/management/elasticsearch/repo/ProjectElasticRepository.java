package com.bosch.eet.skill.management.elasticsearch.repo;

import java.io.IOException;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bosch.eet.skill.management.converter.utils.ProjectConverterUtil;
import com.bosch.eet.skill.management.elasticsearch.document.DepartmentProjectDocument;
import com.bosch.eet.skill.management.elasticsearch.document.PersonalDocument;
import com.bosch.eet.skill.management.entity.Project;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProjectElasticRepository {

    @Autowired
    private ProjectConverterUtil projectConverter;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Value("${elasticsearch.url}")
    private String elasticsearchUrl;

    @Value("${spring.profiles.active}")
    private String profile;

    public boolean existById(String id) {
        SearchRequest searchRequest = new SearchRequest("project_" + profile);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.matchQuery("_id", id));
        sourceBuilder.query(boolQuery);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            if (searchResponse == null ||
                    searchResponse.getHits() == null ||
                    searchResponse.getHits().getHits().length == 0) {
                return false;
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void insert(DepartmentProjectDocument projectDocument) {
        HttpEntity<DepartmentProjectDocument> skillRequest = new HttpEntity<>(new DepartmentProjectDocument(projectDocument.getId(),
                projectDocument.getProjectId(), projectDocument.getProjectName(),
                projectDocument.getProjectType(), projectDocument.getProjectManager(),
                projectDocument.getCustomerGB(), projectDocument.getGbUnit(),
                projectDocument.getTeam(), projectDocument.getStatus(),
                projectDocument.getStartDate(), projectDocument.getEndDate(),
                projectDocument.getTechnologyUsed(), projectDocument.isTopProject(),
                projectDocument.getTeamSize(), projectDocument.getObjective(),
                projectDocument.getDescription(), projectDocument.getHighlight(),
                projectDocument.getScopeId(), projectDocument.getScopeName()));
       restTemplate.exchange(elasticsearchUrl  + "project_" + profile +"/_doc",
                HttpMethod.POST, skillRequest, PersonalDocument.class);
    }

    public void removeById(String id) {
        String resourceUrl = elasticsearchUrl + "project_" + profile + "/_doc/" + id;

        restTemplate.exchange(resourceUrl, HttpMethod.DELETE, null, Void.class);
    }

    public void update(Project project) {
        DepartmentProjectDocument projectDocument = projectConverter.convertToDocument(project);
        HttpEntity<DepartmentProjectDocument> requestUpdate = new HttpEntity<>(projectDocument, PersonalElasticRepository.createJsonHeaders());
        String resourceUrl = elasticsearchUrl + "project_" + profile + "/_doc/" + projectDocument.getId();
        restTemplate.exchange(resourceUrl, HttpMethod.PUT, requestUpdate, Void.class);
    }
}