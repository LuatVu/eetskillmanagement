package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.CustomerGbDto;
import com.bosch.eet.skill.management.dto.GbUnitDto;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.elasticsearch.document.CustomerGBDocument;
import com.bosch.eet.skill.management.elasticsearch.document.DepartmentProjectSimpleDocument;
import com.bosch.eet.skill.management.elasticsearch.document.ProjectSkillTagSimpleDocument;
import com.bosch.eet.skill.management.entity.GbUnit;

@Component
public final class GbUnitConverterUtil {

	private GbUnitConverterUtil() {
		// prevent instantiation
	}
	
    private static GbUnitDto convertToDTO(GbUnit gbUnit) {
        return GbUnitDto.builder()
                .id(gbUnit.getId())
                .name(gbUnit.getName())
                .build();
    }

    public static List<GbUnitDto> convertToDTOS(List<GbUnit> gbUnits) {
        List<GbUnitDto> gbUnitDtos = new ArrayList<>();
        for (GbUnit gbUnit : gbUnits) {
            gbUnitDtos.add(convertToDTO(gbUnit));
        }
        return gbUnitDtos;
    }
    
	public static CustomerGBDocument convertToDocument(CustomerGbDto customerGbDto) {
		List<DepartmentProjectSimpleDocument> departmentProjectSimpleDocuments = new ArrayList<>();
		for (ProjectDto dto : customerGbDto.getProjectDtoList()) {
			departmentProjectSimpleDocuments.add(DepartmentProjectSimpleDocument.builder().projectId(dto.getId())
					.projectName(dto.getName()).build());
		}
		List<ProjectSkillTagSimpleDocument> skillTagSimpleDocuments = new ArrayList<>();
		for (Map.Entry<String, Integer> map : customerGbDto.getGbInfo().entrySet()) {
			skillTagSimpleDocuments
					.add(ProjectSkillTagSimpleDocument.builder().tag(map.getKey()).projectUsed(map.getValue()).build());
		}
		return CustomerGBDocument.builder().id(customerGbDto.getId()).name(customerGbDto.getGbName()).numOfHC(customerGbDto.getHeadCounts())
				.numOfProject(customerGbDto.getProjectDtoList().size()).toolAccross(customerGbDto.getVModelCount())
				.projectSkillTagSimpleDocuments(skillTagSimpleDocuments).projects(departmentProjectSimpleDocuments)
				.build();
	}

	public static  List<CustomerGBDocument> convertToDocuments(List<CustomerGbDto> customerGbDtos) {
		return customerGbDtos.stream().map(item -> convertToDocument(item)).collect(Collectors.toList());
	}
}
