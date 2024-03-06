package com.bosch.eet.skill.management.elasticsearch.repo;

import java.io.IOException;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
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

import com.bosch.eet.skill.management.converter.TrainingCourseConverter;
import com.bosch.eet.skill.management.elasticsearch.document.DepartmentCourseDocument;
import com.bosch.eet.skill.management.elasticsearch.document.PersonalDocument;
import com.bosch.eet.skill.management.entity.TrainingCourse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CourseElasticRepository {

    @Autowired
    private TrainingCourseConverter trainingCourseConverter;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Value("${elasticsearch.url}")
    private String elasticsearchUrl;

    @Value("${spring.profiles.active}")
    private String profile;

    public boolean existById(String id) {
        SearchRequest searchRequest = new SearchRequest("course_" + profile);
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

    public void insert(DepartmentCourseDocument courseDocument) {
        HttpEntity<DepartmentCourseDocument> courseRequest = new HttpEntity<>(new DepartmentCourseDocument(courseDocument.getId(),
                courseDocument.getCourseId(), courseDocument.getCourseName(),
                courseDocument.getCourseType(), courseDocument.getTrainer(),
                courseDocument.getStartDate(), courseDocument.getEffort(),
                courseDocument.getTargetAudience(), courseDocument.getStatus()));
         restTemplate.exchange(elasticsearchUrl  + "course_" + profile +"/_doc",
                HttpMethod.POST, courseRequest, PersonalDocument.class);
    }

    public void removeById(String courseESId) {
        DeleteRequest request = new DeleteRequest("course_" + profile).id(courseESId);
        try {
            DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
            if (response.getResult() == DeleteResponse.Result.NOT_FOUND) {
                System.out.println("Document not found");
            } else {
                System.out.println("Document successfully deleted");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(TrainingCourse trainingCourse) {
        DepartmentCourseDocument trainingCourseDocument = trainingCourseConverter.convertToDocument(trainingCourse);
        HttpEntity<DepartmentCourseDocument> requestUpdate = new HttpEntity<>(trainingCourseDocument, PersonalElasticRepository.createJsonHeaders());
        String resourceUrl =
                elasticsearchUrl + "course_" + profile + "/_doc/" + trainingCourseDocument.getId();
        restTemplate.exchange(resourceUrl, HttpMethod.PUT, requestUpdate, Void.class);
    }
}