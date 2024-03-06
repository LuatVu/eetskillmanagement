package com.bosch.eet.skill.management.elasticsearch.repo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.converter.PersonalConverter;
import com.bosch.eet.skill.management.converter.SkillConverter;
import com.bosch.eet.skill.management.converter.TrainingCourseConverter;
import com.bosch.eet.skill.management.converter.utils.GbUnitConverterUtil;
import com.bosch.eet.skill.management.converter.utils.ProjectConverterUtil;
import com.bosch.eet.skill.management.dto.CustomerGbDto;
import com.bosch.eet.skill.management.elasticsearch.document.CustomerGBDocument;
import com.bosch.eet.skill.management.elasticsearch.document.DepartmentCourseDocument;
import com.bosch.eet.skill.management.elasticsearch.document.DepartmentProjectDocument;
import com.bosch.eet.skill.management.elasticsearch.document.PersonalDocument;
import com.bosch.eet.skill.management.elasticsearch.document.ResultQuery;
import com.bosch.eet.skill.management.elasticsearch.document.SkillDocument;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.TrainingCourse;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.ProjectRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;
import com.bosch.eet.skill.management.repo.TrainingCourseRepository;
import com.bosch.eet.skill.management.service.CustomerService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PersonalElasticRepository  {
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${elasticsearch.host}")
	private String elastisearchHost;
	
	@Value ("${elasticsearch.port}")
	private String elasticsearchPort;
	
	@Value ("${elasticsearch.url}")
	private String elasticsearchUrl;

	@Value ("${spring.profiles.active}")
	private String profile;
	
	@Autowired
	private RestHighLevelClient elasticSearchClient;	
	
	@Autowired
	private PersonalConverter personalConverter;
	
	@Autowired
	private ProjectConverterUtil projectConverter;
	
	@Autowired
	private SkillConverter skillConverter;
	
	@Autowired
	private TrainingCourseConverter trainingCourseConverter;
	
	@Autowired
	private PersonalRepository personalRepository;
	
	@Autowired
	private TrainingCourseRepository trainingCourseRepository;
	
	@Autowired
	private SkillRepository skillRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Autowired
	private SkillElasticRepository skillElasticRepository;

	@Autowired
	private CourseElasticRepository courseElasticRepository;

	@Autowired
	private ProjectElasticRepository projectElasticRepository;
	
	@Autowired
	private CustomerGBElasticRepository customerGBElasticRepository;
	
	@Autowired
	private CustomerService customerService;
	
	protected static HttpHeaders createJsonHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return httpHeaders;
	}
	
	
	public void insertDataPersonal(PersonalDocument personalDocument) {
			HttpEntity<PersonalDocument> personalRequest = new HttpEntity<>(new PersonalDocument(
					personalDocument.getId(), personalDocument.getPersonalId(), personalDocument.getPersonalCode(),
					personalDocument.getDisplayName(), personalDocument.getLevel(), personalDocument.getTeam(),
					personalDocument.getExperience(),personalDocument.getDepartment(),
					personalDocument.getGbUnit(), personalDocument.getLocation(), personalDocument.getSkills(),
					personalDocument.getSkillGroups()));
			restTemplate.exchange(elasticsearchUrl  + "personal_" + profile +"/_doc",
					HttpMethod.POST, personalRequest, PersonalDocument.class);
	}

	public void insertDataToIndex() {
		List<Personal> listPersonal = personalRepository.findAll();
		List<Skill> listSkills = skillRepository.findAll();
		List<Project> listProjects = projectRepository.findAll();
		List<TrainingCourse> listCourses = trainingCourseRepository.findAll();
		List<CustomerGbDto> customerGbDtos = customerService.findAll();
		
		for (Personal personal : listPersonal) {
			if(personal.getDeleted()){ continue;}
			PersonalDocument personalDocument = personalConverter.convertToDocument(personal);
			insertDataPersonal(personalDocument);
		}
		for (Skill skill : listSkills) {
			SkillDocument skillDocument = skillConverter.convertToDocument(skill);
			skillElasticRepository.insert(skillDocument);
		}
		for (Project project : listProjects) {
			DepartmentProjectDocument projectDocument = projectConverter.convertToDocument(project);
			projectElasticRepository.insert(projectDocument);
		}
		for (TrainingCourse trainingCourse : listCourses) {
			DepartmentCourseDocument courseDocument = trainingCourseConverter.convertToDocument(trainingCourse);
			courseElasticRepository.insert(courseDocument);
		}
		for (CustomerGbDto customerGbDto : customerGbDtos) {
			CustomerGBDocument customerGBDocument = GbUnitConverterUtil.convertToDocument(customerGbDto);
			customerGBElasticRepository.insert(customerGBDocument);
		}
	}
	
	public void managePipeline() {
		String managePipeline = "{\r\n"
				+ "  \"description\": \"updates _id with personal id at the time of ingestion\",\r\n"
				+ "  \"processors\": [\r\n"
				+ "    {\r\n"
				+ "      \"set\": {\r\n"
				+ "        \"field\": \"_id\",\r\n"
				+ "        \"value\": \"{{id}}\"\r\n"
				+ "      }\r\n"
				+ "    }\r\n"
				+ "  ]\r\n"
				+ "}";
		String resourceUrl = 
				elasticsearchUrl + "_ingest/pipeline/personalindex";
		HttpEntity<String> requestUpdate = new HttpEntity<>(managePipeline, createJsonHeaders());
		restTemplate.exchange(resourceUrl, HttpMethod.PUT, requestUpdate, Void.class);	
	}
	
	public void createIndex() {
				
		String resourcePersonalUrl = 
				elasticsearchUrl + "personal_" + profile;
		String resourceProjectUrl = 
				elasticsearchUrl + "project_" + profile;
		String resourceSkillUrl = 
				elasticsearchUrl + "skill_" + profile;
		String customerGBUrl = 
				elasticsearchUrl + "customer_gb_" + profile;


		HttpEntity<String> requestCreatePersonal = new HttpEntity<>(ElasticCommon.PERSONAL_BODY, createJsonHeaders());
		restTemplate.exchange(resourcePersonalUrl, HttpMethod.PUT, requestCreatePersonal, Void.class);
		HttpEntity<String> requestCreateProject = new HttpEntity<>(ElasticCommon.PROJECT_BODY, createJsonHeaders());
		restTemplate.exchange(resourceProjectUrl, HttpMethod.PUT, requestCreateProject, Void.class);
		HttpEntity<String> requestCustomerGB = new HttpEntity<>(ElasticCommon.CUSTOMER_GB_BODY, createJsonHeaders());
		restTemplate.exchange(customerGBUrl, HttpMethod.PUT, requestCustomerGB, Void.class);
		HttpEntity<String> requestSkill = new HttpEntity<>(ElasticCommon.SKILL_BODY, createJsonHeaders());
		restTemplate.exchange(resourceSkillUrl, HttpMethod.PUT, requestSkill, Void.class);

		managePipeline();
	}

	
	
	public void deleteIndex() {
		String resourcePersonalUrl = 
				elasticsearchUrl + "personal_" + profile;
		String resourceProjectUrl = 
				elasticsearchUrl + "project_" + profile;
		String resourceSkillUrl = 
				elasticsearchUrl + "skill_" + profile;
		String resourceCourseUrl = 
				elasticsearchUrl + "course_" + profile;
		String customerGBUrl = 
				elasticsearchUrl + "customer_gb_" + profile;

		restTemplate.exchange(resourcePersonalUrl, HttpMethod.DELETE, null, Void.class);	
		restTemplate.exchange(resourceProjectUrl, HttpMethod.DELETE, null, Void.class);	
		restTemplate.exchange(resourceSkillUrl, HttpMethod.DELETE, null, Void.class);	
		restTemplate.exchange(resourceCourseUrl, HttpMethod.DELETE, null, Void.class);	
		restTemplate.exchange(customerGBUrl, HttpMethod.DELETE, null, Void.class);
	}
	
	
	public void syncData() throws IOException {
		GetIndexRequest requestPersonal = new GetIndexRequest("personal_" + profile);
		boolean ifPersonalExist = elasticSearchClient.indices().exists(requestPersonal, RequestOptions.DEFAULT);
		if(ifPersonalExist) {
			updateData();
		} else {
			createIndex();
			insertDataToIndex();
		}
	}
	
	
	@Scheduled(fixedDelay = 604800)
	@Transactional
    public void sync() throws IOException {
        log.info("Start Syncing - {}", LocalDateTime.now());
        StopWatch watch = new StopWatch();
        watch.start();
        syncData();
        watch.stop();
        log.info(" End Syncing - {}", LocalDateTime.now());
        log.info("Sync data took " + watch.getTotalTimeMillis() + "ms ~= " + watch.getTotalTimeSeconds() + "s ~= ");
    }
	
	
	public void updateData() {
		List<Personal> listPersonal = personalRepository.findAll();
		for(Personal personal : listPersonal) {
			if(!personal.getUpdated()) {
				PersonalDocument personalDocument = personalConverter.convertToDocument(personal);
				HttpEntity<PersonalDocument> requestUpdate = new HttpEntity<>(new PersonalDocument(
						personalDocument.getId(), personalDocument.getPersonalId(),personalDocument.getPersonalCode(), 
						personalDocument.getDisplayName(), personalDocument.getLevel(), personalDocument.getTeam(),
						personalDocument.getExperience(),personalDocument.getDepartment(),
						personalDocument.getGbUnit(), personalDocument.getLocation(), personalDocument.getSkills(),
						personalDocument.getSkillGroups()), createJsonHeaders());
				String resourceUrl = 
						elasticsearchUrl + "personal_" + profile + "/_doc/" + personalDocument.getId();
				restTemplate.exchange(resourceUrl, HttpMethod.PUT, requestUpdate, Void.class);
				personal.setUpdated(true);
				personalRepository.save(personal);
			}
		}
	}
	
	public void updatePersonal(Personal personal) {
		PersonalDocument personalDocument = personalConverter.convertToDocument(personal);
		HttpEntity<PersonalDocument> requestUpdate = new HttpEntity<>(new PersonalDocument(
				personalDocument.getId(), personalDocument.getPersonalId(),personalDocument.getPersonalCode(), 
				personalDocument.getDisplayName(), personalDocument.getLevel(), personalDocument.getTeam(),
				personalDocument.getExperience(),personalDocument.getDepartment(),
				personalDocument.getGbUnit(), personalDocument.getLocation(), personalDocument.getSkills(),
				personalDocument.getSkillGroups()), createJsonHeaders());
		String resourceUrl = 
				elasticsearchUrl + "personal_" + profile + "/_doc/" + personalDocument.getId();
		restTemplate.exchange(resourceUrl, HttpMethod.PUT, requestUpdate, Void.class);
		personal.setUpdated(true);
	}

	public boolean existById(String id) {
		SearchRequest searchRequest = new SearchRequest("personal_" + profile);
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
	
	
    private ResultQuery executeHttpRequest(String body, String indexName) throws IOException{
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
    		String resourcePersonalUrl = elasticsearchUrl + indexName +"_"+ profile + "/_search";
            ResultQuery resultQuery = new ResultQuery();
            HttpPost httpPost = new HttpPost(resourcePersonalUrl);
            httpPost.setHeader(ElasticCommon.CONTENT_ACCEPT, ElasticCommon.APP_TYPE);
            httpPost.setHeader(ElasticCommon.CONTENT_TYPE, ElasticCommon.APP_TYPE);
            try {
                httpPost.setEntity(new StringEntity(body, ElasticCommon.ENCODING_UTF8));
                HttpResponse response = httpClient.execute(httpPost);
                String message = EntityUtils.toString(response.getEntity());
                JSONObject myObject = new JSONObject(message);
                if(myObject.getJSONObject(ElasticCommon.HITS)
                		.getJSONObject(ElasticCommon.TOTAL_HITS)
                        .getInt(ElasticCommon.VALUE) != 0){
                    resultQuery
                            .setElements(myObject
                                    .getJSONObject(ElasticCommon.HITS)
                                    .getJSONArray(ElasticCommon.HITS)
                                    .toString());
                    resultQuery
                            .setNumberOfResults(myObject.getJSONObject(ElasticCommon.HITS)
                                    .getJSONObject(ElasticCommon.TOTAL_HITS)
                                    .getInt(ElasticCommon.VALUE));
                    resultQuery.setTimeTook((float) ((double) myObject.getInt(ElasticCommon.TOOK) / ElasticCommon.TO_MS));
                } else {
                    resultQuery.setElements(null);
                    resultQuery.setNumberOfResults(0);
                    resultQuery.setTimeTook((float) ((double) myObject.getInt(ElasticCommon.TOOK) / ElasticCommon.TO_MS));
                }
            } catch (IOException | JSONException e) {
                log.error("Error while connecting to elastic engine --> {}", e.getMessage());
                resultQuery.setNumberOfResults(0);
            }

            return resultQuery;
        }
    }
	
	public ResultQuery searchFromQuery(String indexName, String query, Integer size, Integer from) throws IOException {
		String body = HelperFunctions.buildMultiIndexMatchBody(query, size, from);
		return executeHttpRequest(body, indexName);
	}
	
	public ResultQuery searchPersonalByNameOrNtid(String query, Integer size, Integer from) throws IOException {
		String body = HelperFunctions.buildSearchPersonalByNameOrNtidBody(query, size, from);
		return executeHttpRequest(body, Constants.PERSON_INDEX);
	}
	
	

	public void deleteCourseDocument(String id) {
		String resourceUrl =
				elasticsearchUrl + "course_" + profile + "/_doc/" + id;

		restTemplate.exchange(resourceUrl, HttpMethod.DELETE, null, Void.class);
	}
}
