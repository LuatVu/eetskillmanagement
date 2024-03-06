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

import com.bosch.eet.skill.management.converter.utils.GbUnitConverterUtil;
import com.bosch.eet.skill.management.dto.CustomerGbDto;
import com.bosch.eet.skill.management.elasticsearch.document.CustomerGBDocument;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomerGBElasticRepository {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    
    @Value("${elasticsearch.url}")
    private String elasticsearchUrl;

    @Value("${spring.profiles.active}")
    private String profile;
    
    public boolean existById(String id) {
        SearchRequest searchRequest = new SearchRequest("customer_gb_" + profile);
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

	public void insert(CustomerGBDocument customerGBDocument) {
		HttpEntity<CustomerGBDocument> customerGBRequest = new HttpEntity<>(new CustomerGBDocument(
				customerGBDocument.getId(), customerGBDocument.getName(), customerGBDocument.getNumOfProject(),
				customerGBDocument.getNumOfHC(), customerGBDocument.getToolAccross(), customerGBDocument.getProjects(),
				customerGBDocument.getProjectSkillTagSimpleDocuments()));
		restTemplate.exchange(
				elasticsearchUrl + "customer_gb_" + profile + "/_doc", HttpMethod.POST, customerGBRequest,
				CustomerGBDocument.class);
	}

    public void removeById(String customerGBESId) {
        DeleteRequest request = new DeleteRequest("customer_gb_" + profile).id(customerGBESId);
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

    public void update(CustomerGbDto customerGbDto) {
        CustomerGBDocument customerGBDocument = GbUnitConverterUtil.convertToDocument(customerGbDto);
        HttpEntity<CustomerGBDocument> requestUpdate = new HttpEntity<>(customerGBDocument, PersonalElasticRepository.createJsonHeaders());
        String resourceUrl =
                elasticsearchUrl + "customer_gb_" + profile + "/_doc/" + customerGBDocument.getName();
        restTemplate.exchange(resourceUrl, HttpMethod.PUT, requestUpdate, Void.class);
    }
}