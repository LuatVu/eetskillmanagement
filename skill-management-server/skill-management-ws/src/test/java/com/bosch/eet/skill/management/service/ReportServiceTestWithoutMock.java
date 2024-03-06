package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.dto.GbUnitDto;
import com.bosch.eet.skill.management.dto.GroupProjectBySkillTag;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.ReportDto;
import com.bosch.eet.skill.management.entity.GbUnit;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectSkillTag;
import com.bosch.eet.skill.management.entity.ProjectType;
import com.bosch.eet.skill.management.entity.SkillTag;
import com.bosch.eet.skill.management.repo.GbUnitRepository;
import com.bosch.eet.skill.management.repo.ProjectRepository;
import com.bosch.eet.skill.management.repo.ProjectSkillTagRepository;
import com.bosch.eet.skill.management.repo.SkillTagRepository;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class ReportServiceTestWithoutMock {

	@Autowired
	private ReportService reportService;

	@Autowired
	private ProjectSkillTagRepository projectSkillTagRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private SkillTagRepository skillTagRepository;
	
	@Autowired
	private GbUnitRepository gbUnitRepository;

	private static final String NAME_SKILL_TAG_2 = "Name skill tag 2";

	private static final String NAME_SKILL_TAG_1 = "Name skill tag 1";
	
	private GbUnit gbUnit;

	@BeforeEach
	void prepare() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date = format.parse("2023/02/02");
		SkillTag skillTag1 = SkillTag.builder().name(NAME_SKILL_TAG_1).order(Long.MAX_VALUE).build();
		SkillTag skillTag2 = SkillTag.builder().name(NAME_SKILL_TAG_2).order(Long.MAX_VALUE - 1).build();
		ProjectType projectType = ProjectType.builder().id("b8987dfa-f6e2-99d3-f511-3df3286bd7c0").name("Bosch")
				.build();
		gbUnit = GbUnit.builder().name("Test gb unit").build();
		gbUnitRepository.saveAndFlush(gbUnit);
		Project project = Project.builder().name("Data jpa project").startDate(date).projectType(projectType)
				.teamSize("2").createdBy("GLO7HC").createdDate(date).hightlight("").problemStatement("").solution("")
				.status("New").benefits("").gbUnit(gbUnit).build();
		ProjectSkillTag projectSkillTag1 = ProjectSkillTag.builder().project(project).skillTag(skillTag1).build();
		ProjectSkillTag projectSkillTag2 = ProjectSkillTag.builder().project(project).skillTag(skillTag2).build();
		skillTagRepository.saveAllAndFlush(Arrays.asList(skillTag1, skillTag2));
		projectRepository.saveAndFlush(project);
		projectSkillTagRepository.saveAllAndFlush(Arrays.asList(projectSkillTag1, projectSkillTag2));
	}

	@Test
	@DisplayName("Report project")
	void testReportProject() {
		Map<String, String> filter = new HashMap<>();
		ReportDto result = reportService.generateProjectReport(filter);
		assertThat(result.getProjects()).isGreaterThanOrEqualTo(1);
		
		Optional<ProjectDto> projectDtoOpt = result.getProjectsByStatus().stream()
				.filter(item -> item.getStatus().equals("New")).findFirst();
		ProjectDto projectDto = projectDtoOpt.get();
		assertThat(projectDto.getProjects()).isGreaterThanOrEqualTo(1);
		
		Optional<GbUnitDto> gbUnitDtoOpt = result.getProjectsByGb().stream()
				.filter(item -> item.getName().equals(gbUnit.getName())).findFirst();
		GbUnitDto gbUnitDto = gbUnitDtoOpt.get();
		assertThat(gbUnitDto.getName()).isEqualTo(gbUnit.getName());
		assertThat(gbUnitDto.getProjects()).isGreaterThanOrEqualTo(1);
		
		
		Optional<GroupProjectBySkillTag> groupProjectBySkillTagOpt = result.getProjectBySkillTags().stream()
				.filter(item -> item.getSkillTagName().equals(NAME_SKILL_TAG_1)).findFirst();
		GroupProjectBySkillTag groupProjectBySkillTag = groupProjectBySkillTagOpt.get();
		assertThat(NAME_SKILL_TAG_1).isEqualTo(groupProjectBySkillTag.getSkillTagName());
		assertThat(groupProjectBySkillTag.getCount()).isGreaterThanOrEqualTo(1);
		
		groupProjectBySkillTagOpt = result.getProjectBySkillTags().stream()
				.filter(item -> item.getSkillTagName().equals(NAME_SKILL_TAG_2)).findFirst();
		groupProjectBySkillTag = groupProjectBySkillTagOpt.get();
		assertThat(NAME_SKILL_TAG_2).isEqualTo(groupProjectBySkillTag.getSkillTagName());
		assertThat(groupProjectBySkillTag.getCount()).isGreaterThanOrEqualTo(1);
	}
	
	@Test
	@DisplayName("Report project - Filter")
	void testReportProject_Filter() {
		Map<String, String> filter = new HashMap<>();
		filter.put("gb_unit", gbUnit.getName());
		ReportDto result = reportService.generateProjectReport(filter);
		
		//Status
		assertThat(result.getProjects()).isGreaterThanOrEqualTo(1);
		Optional<ProjectDto> projectDtoOpt = result.getProjectsByStatus().stream()
				.filter(item -> item.getStatus().equals("New")).findFirst();
		ProjectDto projectDto = projectDtoOpt.get();
		assertThat(projectDto.getProjects()).isGreaterThanOrEqualTo(1);
		
		//GB unit
		Optional<GbUnitDto> gbUnitDtoOpt = result.getProjectsByGb().stream()
				.filter(item -> item.getName().equals(gbUnit.getName())).findFirst();
		GbUnitDto gbUnitDto = gbUnitDtoOpt.get();
		assertThat(gbUnitDto.getName()).isEqualTo(gbUnit.getName());
		assertThat(gbUnitDto.getProjects()).isGreaterThanOrEqualTo(1);
		
		//Skill tag
		Optional<GroupProjectBySkillTag> groupProjectBySkillTagOpt = result.getProjectBySkillTags().stream()
				.filter(item -> item.getSkillTagName().equals(NAME_SKILL_TAG_1)).findFirst();
		GroupProjectBySkillTag groupProjectBySkillTag = groupProjectBySkillTagOpt.get();
		assertThat(NAME_SKILL_TAG_1).isEqualTo(groupProjectBySkillTag.getSkillTagName());
		assertThat(groupProjectBySkillTag.getCount()).isEqualTo(1);		
		groupProjectBySkillTagOpt = result.getProjectBySkillTags().stream()
				.filter(item -> item.getSkillTagName().equals(NAME_SKILL_TAG_2)).findFirst();
		groupProjectBySkillTag = groupProjectBySkillTagOpt.get();
		assertThat(NAME_SKILL_TAG_2).isEqualTo(groupProjectBySkillTag.getSkillTagName());
		assertThat(groupProjectBySkillTag.getCount()).isEqualTo(1);		
	}
}
