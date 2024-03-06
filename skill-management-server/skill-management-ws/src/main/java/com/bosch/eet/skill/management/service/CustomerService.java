package com.bosch.eet.skill.management.service;

import java.util.List;

import com.bosch.eet.skill.management.dto.CustomerDetailDTO;
import com.bosch.eet.skill.management.dto.CustomerGbDto;
import com.bosch.eet.skill.management.dto.ProjectDto;

public interface CustomerService {

	public List<CustomerGbDto> findAll();
	
	void syncCustomerGBToElastic();
	
	public CustomerDetailDTO findById(String id);
	
	public CustomerDetailDTO create(CustomerDetailDTO customerDetailDTO);
	
	public CustomerDetailDTO update(CustomerDetailDTO customerDetailDTO);
	
	public String delete(String id);
	
	public List<String> getAllCustomerGbFilters();
	
	public List<ProjectDto> getProjectsByCustomerAndSkillTag(String customerId, String skillTagName);
}
