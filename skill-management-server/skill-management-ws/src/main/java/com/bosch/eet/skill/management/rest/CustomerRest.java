package com.bosch.eet.skill.management.rest;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.CustomerDetailDTO;
import com.bosch.eet.skill.management.dto.CustomerGbDto;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.service.CustomerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author GLO7HC
 */

@RestController
@RequiredArgsConstructor
@Slf4j
public class CustomerRest {

	@Autowired
	private CustomerService customerService;

	@GetMapping(value = Routes.URI_REST_PROJECT_CUSTOMER_GB)
	public GenericResponseDTO<List<CustomerGbDto>> getAll() {
		return GenericResponseDTO.<List<CustomerGbDto>>builder().code(MessageCode.SUCCESS.toString())
				.data(customerService.findAll()).build();
	}

	@GetMapping(value = Routes.URI_REST_PROJECT_CUSTOMER_GB_DETAIL)
	public GenericResponseDTO<CustomerDetailDTO> getDetail(@PathVariable String id) {
		return GenericResponseDTO.<CustomerDetailDTO>builder().code(MessageCode.SUCCESS.toString())
				.data(customerService.findById(id)).build();
	}

	@PostMapping(value = Routes.URI_REST_PROJECT_CUSTOMER_GB)
	public GenericResponseDTO<CustomerDetailDTO> create(@RequestBody CustomerDetailDTO customerDetailDTO) {
		return GenericResponseDTO.<CustomerDetailDTO>builder().code(MessageCode.SUCCESS.toString())
				.data(customerService.create(customerDetailDTO)).build();
	}

	@PostMapping(value = Routes.URI_REST_PROJECT_CUSTOMER_GB_DETAIL)
	public GenericResponseDTO<CustomerDetailDTO> update(@PathVariable String id,
			@RequestBody CustomerDetailDTO customerDetailDTO) {
		customerDetailDTO.setId(id);
		return GenericResponseDTO.<CustomerDetailDTO>builder().code(MessageCode.SUCCESS.toString())
				.data(customerService.update(customerDetailDTO)).build();
	}
	
	@PostMapping(value = Routes.URI_REST_PROJECT_CUSTOMER_GB_DEL)
	public GenericResponseDTO<String> delete(@PathVariable String id) {
		return GenericResponseDTO.<String>builder().code(MessageCode.SUCCESS.toString())
				.data(customerService.delete(id)).build();
	}
	
    @GetMapping(value = Routes.URI_REST_PROJECT_FILTER_CUSTOMER_GB)
    public GenericResponseDTO<List<String>> getAllCustomerGbFilters() {
        return GenericResponseDTO.<List<String>>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(customerService.getAllCustomerGbFilters())
                .timestamps(new Date())
                .build();
    }
    
	@GetMapping(value = Routes.URI_REST_GET_PROJECTS_BY_SKILLTAG_AND_CUSTOMER)
	public GenericResponseDTO<List<ProjectDto>> getProjectsByCustomerAndSkillTag(
			@RequestParam(name = "customer_id", required = true) String customerId,
			@RequestParam(name = "skill_tag", required =  true) String skillTagName) {
		return GenericResponseDTO.<List<ProjectDto>>builder().code(MessageCode.SUCCESS.toString())
				.data(customerService.getProjectsByCustomerAndSkillTag(customerId, skillTagName)).build();
	}
}
