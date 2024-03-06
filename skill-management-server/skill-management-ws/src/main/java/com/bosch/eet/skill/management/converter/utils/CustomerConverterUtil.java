package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.CustomerDetailDTO;
import com.bosch.eet.skill.management.dto.CustomerGbDto;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.entity.Customer;
import com.bosch.eet.skill.management.entity.PhaseProject;
import com.bosch.eet.skill.management.entity.Project;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public final class CustomerConverterUtil {

	private CustomerConverterUtil() {
		// prevent instantiation
	}
	
	public static CustomerGbDto convertToDto(Customer customer) {
		List<Project> projects = customer.getProjects();
		List<ProjectDto> projectDtos = new ArrayList<>();
		int headCount = 0;
		int vModel = 0;
		Map<String, Integer> gbInfors = new HashMap<>();
		List<String> skillTags = new ArrayList<>();
		if (Objects.nonNull(projects) && !projects.isEmpty()) {
			projectDtos.addAll(projects.stream().map(item -> ProjectDto.builder().id(item.getId()).name(item.getName())
					.isTopProject(item.isTopProject()).build()).collect(Collectors.toList()));
			for (Project project : projects) {
				headCount += Integer.parseInt(project.getTeamSize());
				List<PhaseProject> phaseProjects = project.getPhaseProjects();
				if (phaseProjects != null && !phaseProjects.isEmpty()) {
					vModel++;
				}
				skillTags.addAll(project.getProjectSkillTags().stream().map(item -> item.getSkillTag().getName())
						.collect(Collectors.toList()));
			}
		}
		for (String skillTag : skillTags) {
			Integer projectBySkillTag = gbInfors.get(skillTag);
			gbInfors.put(skillTag, Objects.isNull(projectBySkillTag) ? 1 : projectBySkillTag + 1);
		}
		return CustomerGbDto.builder().id(customer.getId()).gbName(customer.getName()).projectDtoList(projectDtos)
				.headCounts(headCount).vModelCount(vModel).GbInfo(gbInfors).build();
	}
	
	public static List<CustomerGbDto> convertToDtos(List<Customer> listCustomer) {
		List<CustomerGbDto> listDtos = new ArrayList<>();
		for (Customer customer : listCustomer) {
			listDtos.add(convertToDto(customer));
		}
		return listDtos;
	}
	
	public static CustomerDetailDTO convertToDetailDto(Customer customer) {
		CustomerGbDto customerGbDto = convertToDto(customer);
		String hightlight = customer.getHightlight();
		String corporation = customer.getCorporation();
		return CustomerDetailDTO.builder().id(customer.getId())
				.hightlight(Objects.isNull(hightlight) ? StringUtils.EMPTY : hightlight)
				.corporation(Objects.isNull(corporation) ? StringUtils.EMPTY : corporation).name(customer.getName())
				.gbInfo(customerGbDto.getGbInfo()).headCounts(customerGbDto.getHeadCounts())
				.vModelCount(customerGbDto.getVModelCount()).build();
	}
	
}
