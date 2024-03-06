package com.bosch.eet.skill.management.rest;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.JsonUtils;
import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.CustomerDetailDTO;
import com.bosch.eet.skill.management.dto.CustomerGbDto;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.exception.BadRequestException;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.ResourceNotFoundException;
import com.bosch.eet.skill.management.service.CustomerService;
import com.bosch.eet.skill.management.service.ProjectService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("dev")
class CustomerGBRestTests {

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private ProjectService projectService;

	@MockBean
	private CustomerService customerService;

	@Autowired
	private MessageSource messageSource;

	@Test
	@DisplayName("Query customergb from elasticsearch")
	@WithMockUser(username = "admin", authorities = {Constants.VIEW_ALL_PROJECTS})
	public void queryCustomerGb() throws Exception {
		Map<String, Integer> mapTag = new HashMap<>();
		mapTag.put("Tag", 1);
		List<ProjectDto> dtos = new ArrayList<>();
		dtos.add(ProjectDto.builder().id("1").name("abc").build());
		CustomerGbDto customerGbDto = CustomerGbDto.builder().gbName("tub").headCounts(1).vModelCount(1).GbInfo(mapTag)
				.projectDtoList(dtos).build();
		List<CustomerGbDto> customerGbDtos = new ArrayList<>();
		customerGbDtos.add(customerGbDto);
		when(projectService.findAllByCustomerGb()).thenReturn(customerGbDtos);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/elastic/search/customer_gb")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.content("{" + "\"query\":\"\"," + "\"size\":10000," + "\"from\":0" + "}")
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.data.numberOfResults").value("31"));

	}

	@Test
	@DisplayName("Query customergb from elasticsearch with filter")
	@WithMockUser(username = "admin", authorities = {Constants.VIEW_ALL_PROJECTS})
	public void queryCustomerGb_filter() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/elastic/search/customer_gb")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.content("{" + "\"query\":\"*xc\"," + "\"size\":10000," + "\"from\":0" + "}")
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.data.numberOfResults").value("2"));
	}

	@Test
	@DisplayName("Get customer detail")
	@WithMockUser(username = "admin", authorities = { Constants.VIEW_PROJECT_DETAIL })
	void getCustomerDetail() throws Exception {
		String id = "idtest";
		CustomerDetailDTO customerDetailDTO = CustomerDetailDTO.builder().id(id).name("test").hightlight("")
				.headCounts(98).vModelCount(51).corporation("").gbInfo(Collections.singletonMap("Maven", 13)).build();
		when(customerService.findById(id)).thenReturn(customerDetailDTO);

		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PROJECT_CUSTOMER_GB_DETAIL, id)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string(
						"{" + "\"data\":{" + "\"id\":\"idtest\"," + "\"name\":\"test\"," + "\"hightlight\":\"\","
								+ "\"corporation\":\"\"," + "\"head_counts\":98," + "\"v_model_count\":51,"
								+ "\"gb_info\":{" + "\"Maven\":13" + "}" + "}," + "\"code\":\"SUCCESS\"" + "}"));
	}

	@Test
	@DisplayName("Get customer detail - Resource can not found")
	@WithMockUser(username = "admin", authorities = { Constants.VIEW_PROJECT_DETAIL })
	void getCustomerDetail_ResourceCanNotFound() throws Exception {
		String id = "idtest";
		when(customerService.findById(id)).thenThrow(new ResourceNotFoundException(messageSource
				.getMessage(MessageCode.CUSTOMER_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale())));

		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PROJECT_CUSTOMER_GB_DETAIL, id)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(jsonPath("$.message", is("Customer not found")))
				.andExpect(jsonPath("$.code", is("404 NOT_FOUND")));
	}

	@Test
	@DisplayName("Get customer detail - Access denied")
	@WithMockUser(username = "admin", authorities = {})
	void getCustomerDetail_AccessDenied() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PROJECT_CUSTOMER_GB_DETAIL, "idtest")
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isForbidden()).andExpect(content().string(
						"{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
	}

	@Test
	@DisplayName("Create customer")
	@WithMockUser(username = "admin", authorities = { Constants.ADD_BOSCH_PROJECT })
	void createCustomer() throws Exception {
		String id = "idtest";
		CustomerDetailDTO customerDetailDTO = CustomerDetailDTO.builder().id(id).name("test").hightlight("Highlight")
				.corporation("Corporation").build();
		CustomerDetailDTO customerDetailDTO2 = customerDetailDTO;
		customerDetailDTO2.setId("idtest");

		when(customerService.create(customerDetailDTO)).thenReturn(customerDetailDTO2);

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_CUSTOMER_GB)
				.content(JsonUtils.convertToString(customerDetailDTO)).characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(content().string(
						"{\"data\":{\"id\":\"idtest\",\"name\":\"test\",\"hightlight\":\"Highlight\",\"corporation\":\"Corporation\"},\"code\":\"SUCCESS\"}"));
	}

	@Test
	@DisplayName("Create customer - Duplicated name")
	@WithMockUser(username = "admin", authorities = {Constants.ADD_BOSCH_PROJECT})
	void createCustomer_DuplicatedName() throws Exception {
		String id = "idtest";
		CustomerDetailDTO customerDetailDTO = CustomerDetailDTO.builder().id(id).name("test").hightlight("Highlight")
				.corporation("Corporation").build();

		when(customerService.create(customerDetailDTO)).thenThrow(new BadRequestException(messageSource
				.getMessage(MessageCode.CUSTOMER_DUPLICATED.toString(), null, LocaleContextHolder.getLocale())));

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_CUSTOMER_GB)
				.content(JsonUtils.convertToString(customerDetailDTO)).characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Customer duplicated")))
				.andExpect(jsonPath("$.code", is("400 BAD_REQUEST")));
	}

	@Test
	@DisplayName("Create customer - Access denied")
	@WithMockUser(username = "admin", authorities = {})
	void createCustomer_AccessDenied() throws Exception {
		String id = "idtest";
		CustomerDetailDTO customerDetailDTO = CustomerDetailDTO.builder().id(id).name("test").hightlight("Highlight")
				.corporation("Corporation").build();
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_CUSTOMER_GB)
				.content(JsonUtils.convertToString(customerDetailDTO)).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(content().string(
						"{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
	}

	@Test
	@DisplayName("Create customer - Customer name too long")
	@WithMockUser(username = "admin", authorities = { Constants.ADD_BOSCH_PROJECT })
	void createCustomer_CustomerNameTooLong() throws Exception {
		String id = "idtest";
		CustomerDetailDTO customerDetailDTO = CustomerDetailDTO.builder().id(id).name(
				"1U1OhUvT85saUwY0Ox2eMYKWlJ2LPBEgMHoZ5xg3Zr0aosBHveL4gNx9qzUi5Fl3yx17FbTQRjeeYjWR4kpkipnSGdfxHj6em3YgVbUq3ciQvu3hawVyLpQBName test")
				.hightlight("Highlight").corporation("Corporation").build();

		when(customerService.create(customerDetailDTO)).thenThrow(new BadRequestException(messageSource
				.getMessage(MessageCode.CUSTOMER_NAME_TOO_LONG.toString(), null, LocaleContextHolder.getLocale())));

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_CUSTOMER_GB)
				.content(JsonUtils.convertToString(customerDetailDTO)).characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Customer name too long")))
				.andExpect(jsonPath("$.code", is("400 BAD_REQUEST")));
	}

	@Test
	@DisplayName("Create customer - Customer highlight too long")
	@WithMockUser(username = "admin", authorities = { Constants.ADD_BOSCH_PROJECT })
	void createCustomer_CustomerHighlightTooLong() throws Exception {
		String id = "idtest";
		CustomerDetailDTO customerDetailDTO = CustomerDetailDTO.builder().id(id).name("test").hightlight(
				"mQKQIVlzqb9IbCWe9jFVPiAetv9xeUHdpYraZp6urAeHy6kCC15T8EOmoVUAgMjsQjVoO16Cdhyl0SFpbeDzblXVFmsTWOwiyAT9fX\"\r\n"
						+ "						+ \"kWowEJKXO6LORXd3x0gvYTPETbc2agqVX8X8FG1SoCoDY6McdwyTi3fkD3iqgC5ifWHPsGitVSTB7y1rmPTPCsiqxEMTA1fEHSlcP\"\r\n"
						+ "						+ \"46SIwTGdZvnvzUEDiTpvDE8Q4ZnAp6QbtFlEe09b6TGeuTUWOJ3Y3bkSR0CQOP6KtYBfGuDBHw8zlcqubYQfg5C6MxXC4OcN72TwJn\"\r\n"
						+ "						+ \"tWSv94m8AM80KpuJ5N0SRQ0xbUuHuMMfvAheGdah4gGo25zgGoEa6cPC8NSkdeSuuz1XB2Pu0rl7l5PUXzaDS4aEyRC6QPiqpc0wJtq\"\r\n"
						+ "						+ \"nMadYb9BYcijTmw5PWVzAYhroLG8LLhDbrgfjXQVb39k5MKX6a6Yk9RZP6o8loWRjZCpnLbWNuf3kaM9VX4Gjbl9YzLn\\r\\n\"")
				.corporation("Corporation").build();

		when(customerService.create(customerDetailDTO)).thenThrow(
				new BadRequestException(messageSource.getMessage(MessageCode.CUSTOMER_HIGHLIGHT_TOO_LONG.toString(),
						null, LocaleContextHolder.getLocale())));

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_CUSTOMER_GB)
				.content(JsonUtils.convertToString(customerDetailDTO)).characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Customer highlight too long")))
				.andExpect(jsonPath("$.code", is("400 BAD_REQUEST")));
	}

	@Test
	@DisplayName("Create customer - Customer corporation too long")
	@WithMockUser(username = "admin", authorities = { Constants.ADD_BOSCH_PROJECT })
	void createCustomer_CustomerCorporationTooLong() throws Exception {
		String id = "idtest";
		CustomerDetailDTO customerDetailDTO = CustomerDetailDTO.builder().id(id).name("test").hightlight("Highlight")
				.corporation(
						"e6E78rHnAREjZ8zRWDhYoO6nqsjIBWZg0LwOiR217CFPBThcpPMYUAO5Xt2xsQOodMQ6Uc9V39nJqezo2i6yIoMSW7\"\r\n"
								+ "						+ \"FL5WWOnbqNrp3rWeS7Lp5FAYdXIAumjRZE9jfTri7jaawJd0V3T081WXR09D1eR0aom8VoaxyzWj1kjb2tdFOT9Kh2mKql\"\r\n"
								+ "						+ \"01Ixv1WAfzSVV04ClCT4rjzWAqT6Lz9mKTKwaz3GZfOWcjwJlK6rdOONUJ0FPfOMnHkBunzms5IJi5RS2PLwjY44FgREjIz\"\r\n"
								+ "						+ \"9MdCiyb9d9tiOaVGfrj9ystdTYALGfpWAgmqddDoWWJ1KrcFcXVRiYxaz92ZE13OQMcfAfZp2b3TucjLXowOR2yDLJVgOZqk\"\r\n"
								+ "						+ \"ZTeNjlht6hc2YQsZH7raK60FUMYVlGKwDzNUxBbHPm3B2Zx2NGwXZzHJlTs5xLmKpHrogydsGyRweQDZbAOxNEUYxSNE43yOK\"\r\n"
								+ "						+ \"sUULUrZDmnvlEUGYsBMwi68ASBo2KXecIeaR45b37nmgyb6ok4KDvv63s24uy8oDrV2oiO3tjH9Lhp7As85mSe5OtjDHG5xrvs\"\r\n"
								+ "						+ \"32TtPKh0ruYtO4TzTYeIF1tlgVEoC08HV4ZdY906JZAoXAviDeBTTBg1sCrOyv2RW2v2OsPjRvfvGwe1Kz1NsXof9T2nxL8bcOo\"\r\n"
								+ "						+ \"10pEicvY6mVLzm3qIRzuxteogTey7atQtENFmi6vwTjhWRBsahbY9EfYVGPownp7dT8FdmJykzus84F50ul1BJkLq4cKMFcUyU13\"\r\n"
								+ "						+ \"I60EVBYtnLV5JafmHjLpNymZqVDh3nGjhxAGn9BuAsucfm0FQPVKS5KyoHab2f8GcMJnz6N1QXTIdvE9BDBFqyR7D535mtD3JCi8t\"\r\n"
								+ "						+ \"tUzaTvPzMAmengS6WHIn1hMgKrmzyfBZnBZRmh9pmpGufqT2x6af6wz5xSWvFvjDKzmtPGlqcCWDy9NIWH6N8fgZBPOmnBhjKgSKux\"\r\n"
								+ "						+ \"tmYLRVb01qkCY1JK6TcU5ZZC7KZl\\r\\n\"")
				.build();

		when(customerService.create(customerDetailDTO)).thenThrow(
				new BadRequestException(messageSource.getMessage(MessageCode.CUSTOMER_CORPORATION_TOO_LONG.toString(),
						null, LocaleContextHolder.getLocale())));

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_CUSTOMER_GB)
				.content(JsonUtils.convertToString(customerDetailDTO)).characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Customer corporation too long")))
				.andExpect(jsonPath("$.code", is("400 BAD_REQUEST")));
	}

	@Test
	@DisplayName("Update customer detail - Access denied")
	@WithMockUser(username = "admin", authorities = {})
	void updateCustomer_AccessDenied() throws Exception {
		String id = "idtest";
		CustomerDetailDTO customerDetailDTO = CustomerDetailDTO.builder().id(id).name("test").hightlight("Highlight")
				.corporation("Corporation").build();
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_CUSTOMER_GB_DETAIL, id)
				.content(JsonUtils.convertToString(customerDetailDTO)).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(content().string(
						"{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
	}

	@Test
	@DisplayName("Update customer")
	@WithMockUser(username = "admin", authorities = { Constants.EDIT_PROJECT })
	void updateCustomer() throws Exception {
		String id = "idtest";
		CustomerDetailDTO customerDetailDTO = CustomerDetailDTO.builder().id(id).name("test").hightlight("Highlight")
				.corporation("Corporation").build();

		when(customerService.update(customerDetailDTO)).thenReturn(customerDetailDTO);
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_CUSTOMER_GB_DETAIL, id)
				.content(JsonUtils.convertToString(customerDetailDTO)).characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(content().string(
						"{\"data\":{\"id\":\"idtest\",\"name\":\"test\",\"hightlight\":\"Highlight\",\"corporation\":\"Corporation\"},\"code\":\"SUCCESS\"}"));
	}

	@Test
	@DisplayName("Update customer detail - Customer not found")
	@WithMockUser(username = "admin", authorities = { Constants.EDIT_PROJECT })
	void updateCustomer_CustomerNotFound() throws Exception {
		String id = "idtest";
		CustomerDetailDTO customerDetailDTO = CustomerDetailDTO.builder().id(id).name("test").hightlight("Highlight")
				.corporation("Corporation").build();
		when(customerService.update(customerDetailDTO)).thenThrow(new ResourceNotFoundException(messageSource
				.getMessage(MessageCode.CUSTOMER_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale())));

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_CUSTOMER_GB_DETAIL, id)
				.content(JsonUtils.convertToString(customerDetailDTO)).characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(jsonPath("$.message", is("Customer not found")))
				.andExpect(jsonPath("$.code", is("404 NOT_FOUND")));
	}

	@Test
	@DisplayName("Update customer detail - Customer name duplicated")
	@WithMockUser(username = "admin", authorities = { Constants.EDIT_PROJECT })
	void updateCustomer_CustomerNameDuplicated() throws Exception {
		String id = "idtest";
		CustomerDetailDTO customerDetailDTO = CustomerDetailDTO.builder().id(id).name("test").hightlight("Highlight")
				.corporation("Corporation").build();
		when(customerService.update(customerDetailDTO)).thenThrow(new BadRequestException(messageSource
				.getMessage(MessageCode.CUSTOMER_DUPLICATED.toString(), null, LocaleContextHolder.getLocale())));

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_CUSTOMER_GB_DETAIL, id)
				.content(JsonUtils.convertToString(customerDetailDTO)).characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Customer duplicated")))
				.andExpect(jsonPath("$.code", is("400 BAD_REQUEST")));
	}
	
	@Test
	@DisplayName("Update customer - Customer name too long")
	@WithMockUser(username = "admin", authorities = { Constants.EDIT_PROJECT })
	void updateCustomer_CustomerNameTooLong() throws Exception {
		String id = "idtest";
		CustomerDetailDTO customerDetailDTO = CustomerDetailDTO.builder().id(id).name(
				"1U1OhUvT85saUwY0Ox2eMYKWlJ2LPBEgMHoZ5xg3Zr0aosBHveL4gNx9qzUi5Fl3yx17FbTQRjeeYjWR4kpkipnSGdfxHj6em3YgVbUq3ciQvu3hawVyLpQBName test")
				.hightlight("Highlight").corporation("Corporation").build();

		when(customerService.update(customerDetailDTO)).thenThrow(new BadRequestException(messageSource
				.getMessage(MessageCode.CUSTOMER_NAME_TOO_LONG.toString(), null, LocaleContextHolder.getLocale())));

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_CUSTOMER_GB_DETAIL, id)
				.content(JsonUtils.convertToString(customerDetailDTO)).characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Customer name too long")))
				.andExpect(jsonPath("$.code", is("400 BAD_REQUEST")));
	}

	@Test
	@DisplayName("Update customer - Customer highlight too long")
	@WithMockUser(username = "admin", authorities = { Constants.EDIT_PROJECT })
	void updateCustomer_CustomerHighlightTooLong() throws Exception {
		String id = "idtest";
		CustomerDetailDTO customerDetailDTO = CustomerDetailDTO.builder().id(id).name("test").hightlight(
				"mQKQIVlzqb9IbCWe9jFVPiAetv9xeUHdpYraZp6urAeHy6kCC15T8EOmoVUAgMjsQjVoO16Cdhyl0SFpbeDzblXVFmsTWOwiyAT9fX\"\r\n"
						+ "						+ \"kWowEJKXO6LORXd3x0gvYTPETbc2agqVX8X8FG1SoCoDY6McdwyTi3fkD3iqgC5ifWHPsGitVSTB7y1rmPTPCsiqxEMTA1fEHSlcP\"\r\n"
						+ "						+ \"46SIwTGdZvnvzUEDiTpvDE8Q4ZnAp6QbtFlEe09b6TGeuTUWOJ3Y3bkSR0CQOP6KtYBfGuDBHw8zlcqubYQfg5C6MxXC4OcN72TwJn\"\r\n"
						+ "						+ \"tWSv94m8AM80KpuJ5N0SRQ0xbUuHuMMfvAheGdah4gGo25zgGoEa6cPC8NSkdeSuuz1XB2Pu0rl7l5PUXzaDS4aEyRC6QPiqpc0wJtq\"\r\n"
						+ "						+ \"nMadYb9BYcijTmw5PWVzAYhroLG8LLhDbrgfjXQVb39k5MKX6a6Yk9RZP6o8loWRjZCpnLbWNuf3kaM9VX4Gjbl9YzLn\\r\\n\"")
				.corporation("Corporation").build();

		when(customerService.update(customerDetailDTO)).thenThrow(
				new BadRequestException(messageSource.getMessage(MessageCode.CUSTOMER_HIGHLIGHT_TOO_LONG.toString(),
						null, LocaleContextHolder.getLocale())));

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_CUSTOMER_GB_DETAIL, id)
				.content(JsonUtils.convertToString(customerDetailDTO)).characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Customer highlight too long")))
				.andExpect(jsonPath("$.code", is("400 BAD_REQUEST")));
	}

	@Test
	@DisplayName("Update customer - Customer corporation too long")
	@WithMockUser(username = "admin", authorities = { Constants.EDIT_PROJECT })
	void updateCustomer_CustomerCorporationTooLong() throws Exception {
		String id = "idtest";
		CustomerDetailDTO customerDetailDTO = CustomerDetailDTO.builder().id(id).name("test").hightlight("Highlight")
				.corporation(
						"e6E78rHnAREjZ8zRWDhYoO6nqsjIBWZg0LwOiR217CFPBThcpPMYUAO5Xt2xsQOodMQ6Uc9V39nJqezo2i6yIoMSW7\"\r\n"
								+ "						+ \"FL5WWOnbqNrp3rWeS7Lp5FAYdXIAumjRZE9jfTri7jaawJd0V3T081WXR09D1eR0aom8VoaxyzWj1kjb2tdFOT9Kh2mKql\"\r\n"
								+ "						+ \"01Ixv1WAfzSVV04ClCT4rjzWAqT6Lz9mKTKwaz3GZfOWcjwJlK6rdOONUJ0FPfOMnHkBunzms5IJi5RS2PLwjY44FgREjIz\"\r\n"
								+ "						+ \"9MdCiyb9d9tiOaVGfrj9ystdTYALGfpWAgmqddDoWWJ1KrcFcXVRiYxaz92ZE13OQMcfAfZp2b3TucjLXowOR2yDLJVgOZqk\"\r\n"
								+ "						+ \"ZTeNjlht6hc2YQsZH7raK60FUMYVlGKwDzNUxBbHPm3B2Zx2NGwXZzHJlTs5xLmKpHrogydsGyRweQDZbAOxNEUYxSNE43yOK\"\r\n"
								+ "						+ \"sUULUrZDmnvlEUGYsBMwi68ASBo2KXecIeaR45b37nmgyb6ok4KDvv63s24uy8oDrV2oiO3tjH9Lhp7As85mSe5OtjDHG5xrvs\"\r\n"
								+ "						+ \"32TtPKh0ruYtO4TzTYeIF1tlgVEoC08HV4ZdY906JZAoXAviDeBTTBg1sCrOyv2RW2v2OsPjRvfvGwe1Kz1NsXof9T2nxL8bcOo\"\r\n"
								+ "						+ \"10pEicvY6mVLzm3qIRzuxteogTey7atQtENFmi6vwTjhWRBsahbY9EfYVGPownp7dT8FdmJykzus84F50ul1BJkLq4cKMFcUyU13\"\r\n"
								+ "						+ \"I60EVBYtnLV5JafmHjLpNymZqVDh3nGjhxAGn9BuAsucfm0FQPVKS5KyoHab2f8GcMJnz6N1QXTIdvE9BDBFqyR7D535mtD3JCi8t\"\r\n"
								+ "						+ \"tUzaTvPzMAmengS6WHIn1hMgKrmzyfBZnBZRmh9pmpGufqT2x6af6wz5xSWvFvjDKzmtPGlqcCWDy9NIWH6N8fgZBPOmnBhjKgSKux\"\r\n"
								+ "						+ \"tmYLRVb01qkCY1JK6TcU5ZZC7KZl\\r\\n\"")
				.build();

		when(customerService.update(customerDetailDTO)).thenThrow(
				new BadRequestException(messageSource.getMessage(MessageCode.CUSTOMER_CORPORATION_TOO_LONG.toString(),
						null, LocaleContextHolder.getLocale())));

		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_CUSTOMER_GB_DETAIL, id)
				.content(JsonUtils.convertToString(customerDetailDTO)).characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Customer corporation too long")))
				.andExpect(jsonPath("$.code", is("400 BAD_REQUEST")));
	}

	@Test
	@DisplayName("Delete customer - Access denied")
	@WithMockUser(username = "admin", authorities = {})
	void deleteCustomer_AccessDenied() throws Exception {
		String id = "idtest";
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_CUSTOMER_GB_DEL, id)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isForbidden()).andExpect(content().string(
						"{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
	}
	
	@Test
	@DisplayName("Delete customer")
	@WithMockUser(username = "admin", authorities = { Constants.DELETE_PROJECT })
	void deleteCustomer() throws Exception {
		String id = "idtest";
		when(customerService.delete(id)).thenReturn(Constants.SUCCESS);
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_CUSTOMER_GB_DEL, id)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(jsonPath("$.data", is(Constants.SUCCESS)));
	}

	@Test
	@DisplayName("Delete customer - Customer not found")
	@WithMockUser(username = "admin", authorities = { Constants.DELETE_PROJECT })
	void deleteCustomer_CustomerNotFound() throws Exception {
		String id = "idtest";
		when(customerService.delete(id)).thenThrow(new ResourceNotFoundException(messageSource
				.getMessage(MessageCode.CUSTOMER_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale())));
		mockMvc.perform(MockMvcRequestBuilders.post(Routes.URI_REST_PROJECT_CUSTOMER_GB_DEL, id)
				.characterEncoding(StandardCharsets.UTF_8).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(jsonPath("$.message", is("Customer not found")))
				.andExpect(jsonPath("$.code", is("404 NOT_FOUND")));
	}

	@Test
	@DisplayName("Get all customer for filter")
	@WithMockUser(username = "admin", authorities = {Constants.VIEW_ALL_PROJECTS})
	void testGetAllCustomerGb() throws Exception {
		List<String> allCustomerGb = Arrays.asList("11", "Add elastichsearch", "BGSV", "chuyen sang long customer gb",
				"Cross GB", "dsdasdsa", "EET", "ETAS GmbH", "GmbH", "Long vua thay doi customer gb",
				"LongCustomerGbLongCustomerGbLongCustomerGbLongCust", "MS", "Non PS-EC", "PSEC", "QMM/AnP", "ted1t",
				"tedst", "test", "test bosch for demand", "Test Long 5/10 sang test", "Test Long 5/10ddddd",
				"test long customer gb", "test11", "test112", "test1SDSDADSA", "testdsadasdsad", "thuonggb",
				"ugn8hc test bosch for demand", "XC-AN", "XC-DX", "ZvM");
		when(customerService.getAllCustomerGbFilters()).thenReturn(allCustomerGb);
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PROJECT_FILTER_CUSTOMER_GB))
				.andExpect(status().isOk()).andExpect(jsonPath("$.data", is(allCustomerGb)));
	}
	
	@Test
	@DisplayName("Get all customer for filter")	
	@WithMockUser(username = "admin", authorities = {})
	void testGetAllCustomerGb_AccessDenied() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_PROJECT_FILTER_CUSTOMER_GB))
				.andExpect(MockMvcResultMatchers.status().isForbidden()).andExpect(content().string(
						"{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
	}
	
	@Test
	@DisplayName("Find projects by customer id and skill tag name")
	@WithMockUser(username = "admin", authorities = { Constants.VIEW_PROJECT_DETAIL })
	void findProjectsByCustomerIdAndSkillTagName() throws Exception {
		String customerId = "customerid";
		String skillTagName = "skilltagname";
		ProjectDto projectDto1 = ProjectDto.builder().id("id1").name("Name 1").build();
		ProjectDto projectDto2 = ProjectDto.builder().id("id2").name("Name 2").build();
		when(customerService.getProjectsByCustomerAndSkillTag(customerId, skillTagName))
				.thenReturn(Arrays.asList(projectDto1, projectDto2));
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_GET_PROJECTS_BY_SKILLTAG_AND_CUSTOMER)
				.param("customer_id", customerId).param("skill_tag", skillTagName)).andExpect(status().isOk())
				.andExpect(content().string("{" + "\"data\":[" + "{" + "\"name\":\"Name 1\","
						+ "\"project_id\":\"id1\"," + "\"top_project\":false" + "}," + "{" + "\"name\":\"Name 2\","
						+ "\"project_id\":\"id2\"," + "\"top_project\":false" + "}" + "]," + "\"code\":\"SUCCESS\""
						+ "}"));
	}

	@Test
	@DisplayName("Find projects by customer id and skill tag name - Access denied")
	@WithMockUser(username = "admin", authorities = {})
	void findProjectsByCustomerIdAndSkillTagName_AccessDenied() throws Exception {
		String customerId = "customerid";
		String skillTagName = "skilltagname";
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_GET_PROJECTS_BY_SKILLTAG_AND_CUSTOMER)
				.param("customer_id", customerId).param("skill_tag", skillTagName)).andExpect(status().isForbidden())
				.andExpect(content().string(
						"{" + "\"error\":\"access_denied\"," + "\"error_description\":\"Access is denied\"" + "}"));
	}
	
	@Test
	@DisplayName("Find projects by customer id and skill tag name - Missing customer id")
	@WithMockUser(username = "admin", authorities = {Constants.VIEW_PROJECT_DETAIL})
	void findProjectsByCustomerIdAndSkillTagName_MissingCustomerId() throws Exception {
		String skillTagName = "skilltagname";
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_GET_PROJECTS_BY_SKILLTAG_AND_CUSTOMER)
				.param("skill_tag", skillTagName)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", is("400 BAD_REQUEST"))).andExpect(jsonPath("$.message", is(
						"Required request parameter 'customer_id' for method parameter type String is not present")));
	}
	
	@Test
	@DisplayName("Find projects by customer id and skill tag name - Missing skill tag name")
	@WithMockUser(username = "admin", authorities = {Constants.VIEW_PROJECT_DETAIL})
	void findProjectsByCustomerIdAndSkillTagName_MissingSkillTagName() throws Exception {
		String customerId = "customerid";
		mockMvc.perform(MockMvcRequestBuilders.get(Routes.URI_REST_GET_PROJECTS_BY_SKILLTAG_AND_CUSTOMER)
				.param("customer_id", customerId)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", is("400 BAD_REQUEST"))).andExpect(jsonPath("$.message", is(
						"Required request parameter 'skill_tag' for method parameter type String is not present")));
	}
}
