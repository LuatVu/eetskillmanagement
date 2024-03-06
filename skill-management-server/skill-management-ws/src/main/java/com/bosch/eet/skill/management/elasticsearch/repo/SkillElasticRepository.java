package com.bosch.eet.skill.management.elasticsearch.repo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.converter.SkillConverter;
import com.bosch.eet.skill.management.elasticsearch.document.ResultQuery;
import com.bosch.eet.skill.management.elasticsearch.document.SkillDocument;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.repo.SkillRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SkillElasticRepository {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private PersonalElasticRepository personalElasticRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SkillConverter skillConverter;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Value("${elasticsearch.url}")
    private String elasticsearchUrl;

    @Value("${spring.profiles.active}")
    private String profile;

    public void syncData() throws IOException {
        int size = 100;
        List<Skill> allSkillsDB = new ArrayList<>();
        for (int page = 0; ; page++) {
            List<Skill> pagSkillsDB = skillRepository.findAll(PageRequest.of(page, size)).toList();
            allSkillsDB.addAll(pagSkillsDB);

            if (pagSkillsDB.size() < size) {
                break;
            }
        }

        List<SkillDocument> allSkillsES = new ArrayList<>();
        for (int page = 0; ; page++) {
            ResultQuery pagSkillsESResQuery = personalElasticRepository.searchFromQuery(
                    Constants.SKILL_INDEX,
                    "",
                    size, page*size);

            if (pagSkillsESResQuery.getNumberOfResults() == 0) {
                break;
            }

            List<Map<String, Object>> allSkillsESMap = objectMapper.readValue(pagSkillsESResQuery.getElements(), new TypeReference<List<Map<String, Object>>>() {
            });
            List<SkillDocument> pagSkillsES = new ArrayList<>();
            for (Map<String, Object> skillESMap: allSkillsESMap) {
                SkillDocument skillDocument = objectMapper.readValue(objectMapper.writeValueAsBytes(skillESMap.get("_source")), SkillDocument.class);
                pagSkillsES.add(skillDocument);
            }
            allSkillsES.addAll(pagSkillsES);

            if (pagSkillsES.size() < size) {
                break;
            }
        }

        Map<String, Boolean> updatedSkills = new HashMap<>();
        for (SkillDocument skillES : allSkillsES) {
            String skillESId = skillES.getSkillId();
            Skill skillESExistInDB = skillRepository.findById(skillESId).orElse(null);
            if (skillESExistInDB != null) { // Update skill which exists in DB
                if (!skillESExistInDB.getEsUpdated()) {
                    update(skillESExistInDB);
                }
                updatedSkills.put(skillESId, true);
            } else { // Remove skill which does not exist in DB
                removeById(skillESId);
            }
        }

        // Insert all skills which do not exist on elastic
        for (Skill skillDB : allSkillsDB) {
            if (updatedSkills.get(skillDB.getId()) == null) {
                SkillDocument skillDocument = skillConverter.convertToDocument(skillDB);
                this.insert(skillDocument);
            }
        }
    }

    public boolean existById(String id) {
        SearchRequest searchRequest = new SearchRequest("skill_" + profile);
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

    public void insert(SkillDocument skillDocument) {
        HttpEntity<SkillDocument> skillRequest = new HttpEntity<>(new SkillDocument(skillDocument.getSkillId(),
                skillDocument.getSkillId(), skillDocument.getSkillName(),
                skillDocument.getSkillGroup(), skillDocument.getSkillLevels(),
                skillDocument.isRequired(), skillDocument.isMandatory(),
        		skillDocument.getSkillType()));
        restTemplate.exchange(elasticsearchUrl  + "skill_" + profile +"/_doc",
                HttpMethod.POST, skillRequest, SkillDocument.class);
    }

    public void removeById(String skillESId) {
        DeleteRequest request = new DeleteRequest("skill_" + profile).id(skillESId);
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

    public void update(Skill skill) {
        SkillDocument skillDocument = skillConverter.convertToDocument(skill);
        HttpEntity<SkillDocument> requestUpdate = new HttpEntity<>(skillDocument, PersonalElasticRepository.createJsonHeaders());
        String resourceUrl =
                elasticsearchUrl + "skill_" + profile + "/_doc/" + skillDocument.getId();
        restTemplate.exchange(resourceUrl, HttpMethod.PUT, requestUpdate, Void.class);
        skill.setEsUpdated(true);
        skillRepository.save(skill);
    }
}
