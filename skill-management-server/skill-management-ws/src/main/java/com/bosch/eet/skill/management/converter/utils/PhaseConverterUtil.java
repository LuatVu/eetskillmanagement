package com.bosch.eet.skill.management.converter.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.PhaseDto;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.VModelDto;
import com.bosch.eet.skill.management.entity.Customer;
import com.bosch.eet.skill.management.entity.GbUnit;
import com.bosch.eet.skill.management.entity.Phase;
import com.bosch.eet.skill.management.entity.PhaseProject;
import com.bosch.eet.skill.management.entity.Project;

@Component
public final class PhaseConverterUtil {

	private PhaseConverterUtil() {
		// prevent instantiation
	}
	
	public static VModelDto convertToDtos(List<Phase> phases) {
		List<PhaseDto> phaseDtos = phases.stream().map(item -> convertToDto(item)).collect(Collectors.toList());
		return VModelDto.builder().phases(phaseDtos).build();
	}
	
	public static PhaseDto convertToDto(Phase phase) {
		List<ProjectDto> projectDtos = phase.getPhaseProjects().stream().map(item -> {
			Project project = item.getProject();
			GbUnit gbUnit = project.getGbUnit();
			Customer customer = project.getCustomer();
			return ProjectDto.builder()
				.id(project.getId())
				.name(project.getName())
				.gbUnit((gbUnit != null)? gbUnit.getName():"")
				.customerGb((customer != null)? customer.getName():"")
				.build();
			}).collect(Collectors.toList());
		
		return PhaseDto.builder()
				.id(phase.getId())
				.name(phase.getName())
				.projects(projectDtos)
				.build();
	}

	public static PhaseDto convertToDtoWithoutProject(Phase phase) {
		return PhaseDto.builder()
				.id(phase.getId())
				.name(phase.getName())
				.description(phase.getDescription())
				.build();
	}

	public static PhaseDto convertToDtoWithoutProject(PhaseProject phaseProject) {
		return PhaseDto.builder()
				.id(phaseProject.getPhase().getId())
				.name(phaseProject.getPhase().getName())
				.description(phaseProject.getPhase().getDescription())
				.build();
	}
}
