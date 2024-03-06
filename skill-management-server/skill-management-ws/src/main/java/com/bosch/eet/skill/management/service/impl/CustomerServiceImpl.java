package com.bosch.eet.skill.management.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.converter.utils.CustomerConverterUtil;
import com.bosch.eet.skill.management.converter.utils.GbUnitConverterUtil;
import com.bosch.eet.skill.management.converter.utils.ProjectConverterUtil;
import com.bosch.eet.skill.management.dto.CustomerDetailDTO;
import com.bosch.eet.skill.management.dto.CustomerGbDto;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.elasticsearch.document.CustomerGBDocument;
import com.bosch.eet.skill.management.elasticsearch.repo.CustomerGBElasticRepository;
import com.bosch.eet.skill.management.elasticsearch.repo.ElasticCommon;
import com.bosch.eet.skill.management.elasticsearch.repo.ProjectSpringElasticRepository;
import com.bosch.eet.skill.management.entity.Customer;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.exception.BadRequestException;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.ResourceNotFoundException;
import com.bosch.eet.skill.management.repo.CustomerRepository;
import com.bosch.eet.skill.management.repo.ProjectSkillTagRepository;
import com.bosch.eet.skill.management.service.CustomerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository customerRepository;
	
	private final ProjectSkillTagRepository projectSkillTagRepository;

	private final ProjectConverterUtil projectConverter;

	@Value("${elasticsearch.url}")
	private String elasticsearchUrl;

	@Value("${spring.profiles.active}")
	private String profile;

	private final RestTemplate restTemplate;

	private final CustomerGBElasticRepository customerGBElasticRepository;

	private final ProjectSpringElasticRepository projectSpringElasticRepo;

	private final MessageSource messageSource;

	private final ModelMapper modelMapper;
	
	

	@Override
	public List<CustomerGbDto> findAll() {
		List<Customer> customers = customerRepository.findAll();
		return CustomerConverterUtil.convertToDtos(customers);
	}

	@Override
	@Transactional
	public void syncCustomerGBToElastic() {
		String customerGBUrl = elasticsearchUrl + Constants.CUSTOMER_GB + profile;
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		// delete index
		restTemplate.exchange(customerGBUrl, HttpMethod.DELETE, null, Void.class);
		// create new index
		HttpEntity<String> requestCustomerGB = new HttpEntity<>(ElasticCommon.CUSTOMER_GB_BODY, httpHeaders);
		restTemplate.exchange(customerGBUrl, HttpMethod.PUT, requestCustomerGB, Void.class);
		List<CustomerGbDto> customerGbDtos = findAll();
		for (CustomerGbDto customerGbDto : customerGbDtos) {
			CustomerGBDocument customerGBDocument = GbUnitConverterUtil.convertToDocument(customerGbDto);
			customerGBElasticRepository.insert(customerGBDocument);
		}
	}

	@Override
	public CustomerDetailDTO findById(String id) {
		Optional<Customer> customerOpt = customerRepository.findById(id);
		if (!customerOpt.isPresent()) {
			throw new ResourceNotFoundException(messageSource.getMessage(MessageCode.CUSTOMER_NOT_FOUND.toString(),
					null, LocaleContextHolder.getLocale()));
		}
		return CustomerConverterUtil.convertToDetailDto(customerOpt.get());
	}

	@Override
	@Transactional
	public CustomerDetailDTO create(CustomerDetailDTO customerDetailDTO) {
		Optional<Customer> customerOpt = customerRepository.findByName(customerDetailDTO.getName());
		if (customerOpt.isPresent()) {
			throw new BadRequestException(messageSource.getMessage(MessageCode.CUSTOMER_DUPLICATED.toString(), null,
					LocaleContextHolder.getLocale()));
		}
		Customer customer = buildEntity(customerDetailDTO);
		customerRepository.save(customer);
		customerDetailDTO.setId(customer.getId());
		CustomerGbDto customerGbDto = CustomerConverterUtil.convertToDto(customer);
		CustomerGBDocument customerGbDocument = GbUnitConverterUtil.convertToDocument(customerGbDto);
		customerGBElasticRepository.insert(customerGbDocument);
		return customerDetailDTO;
	}

	@Override
	@Transactional
	public CustomerDetailDTO update(CustomerDetailDTO customerDetailDTO) {
		Optional<Customer> customerOpt = customerRepository.findById(customerDetailDTO.getId());
		if (!customerOpt.isPresent()) {
			throw new ResourceNotFoundException(messageSource.getMessage(MessageCode.CUSTOMER_NOT_FOUND.toString(), null,
					LocaleContextHolder.getLocale()));
		}
		Customer dbCustomer = customerOpt.get();
		Optional<Customer> customerOptByName = customerRepository.findByName(customerDetailDTO.getName());
		if (customerOptByName.isPresent() && !customerOptByName.get().getId().equals(dbCustomer.getId())) {
			throw new BadRequestException(messageSource.getMessage(MessageCode.CUSTOMER_DUPLICATED.toString(), null,
					LocaleContextHolder.getLocale()));
		}
		List<Project> customerProjects = dbCustomer.getProjects();
		String customerOldName = dbCustomer.getName();

		Customer customer = buildEntity(customerDetailDTO);
		customer.setProjects(customerProjects);
		customerRepository.save(customer);
		CustomerGbDto customerGbDto = CustomerConverterUtil.convertToDto(customer);
		customerGBElasticRepository.update(customerGbDto);

		if(!customerOldName.equals(customerDetailDTO.getName())){
			projectSpringElasticRepo.saveAll(customerProjects.stream()
					.map(projectConverter::convertToDocument).collect(Collectors.toList()));
		}

		return customerDetailDTO;
	}

	private Customer buildEntity(CustomerDetailDTO customerDetailDTO) {
		if (customerDetailDTO.getName().length() > 120) {
			throw new BadRequestException(messageSource.getMessage(MessageCode.CUSTOMER_NAME_TOO_LONG.toString(),
					null, LocaleContextHolder.getLocale()));
		}
		if (customerDetailDTO.getHightlight().length() > 500) {
			throw new BadRequestException(messageSource.getMessage(MessageCode.CUSTOMER_HIGHLIGHT_TOO_LONG.toString(),
					null, LocaleContextHolder.getLocale()));
		}
		if (customerDetailDTO.getCorporation().length() > 1000) {
			throw new BadRequestException(messageSource.getMessage(MessageCode.CUSTOMER_CORPORATION_TOO_LONG.toString(),
					null, LocaleContextHolder.getLocale()));
		}
		return modelMapper.map(customerDetailDTO, Customer.class);
	}

	@Override
	@Transactional
	public String delete(String id) {
		Optional<Customer> customerOpt = customerRepository.findById(id);
		if (!customerOpt.isPresent()) {
			throw new ResourceNotFoundException(messageSource.getMessage(MessageCode.CUSTOMER_NOT_FOUND.toString(),
					null, LocaleContextHolder.getLocale()));
		}	
		List<Project> projects = customerOpt.get().getProjects();
		projects.forEach(project->project.setCustomer(null));
		customerRepository.deleteById(id);
		customerGBElasticRepository.removeById(id);
		projectSpringElasticRepo.saveAll(projects.stream()
				.map(projectConverter::convertToDocument).collect(Collectors.toList()));
		return Constants.SUCCESS;
	}

	@Override
	public List<String> getAllCustomerGbFilters() {
		return customerRepository.findAllByOrderByName().stream().map(Customer::getName).collect(Collectors.toList());
	}

	@Override
	public List<ProjectDto> getProjectsByCustomerAndSkillTag(String customerId, String skillTagName) {
		return projectSkillTagRepository.findProjectsByCustomerIdAndSkillTagName(customerId, skillTagName);
	}
}
