package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.SkillTagDto;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectSkillTag;
import com.bosch.eet.skill.management.entity.ProjectType;
import com.bosch.eet.skill.management.entity.SkillTag;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.repo.ProjectRepository;
import com.bosch.eet.skill.management.repo.ProjectSkillTagRepository;
import com.bosch.eet.skill.management.repo.SkillTagRepository;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class SkillTagServiceTestWithoutMock {
	
	@Autowired
	private SkillTagService skillTagService;
	
	@Autowired
	private SkillTagRepository skillTagRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private ProjectSkillTagRepository projectSkillTagRepository;
	
	private SkillTag oldSkillTag;
	
	private SkillTag newSkillTag;
	
	@BeforeEach
	void prepare() throws ParseException {
		long order1 = Long.MAX_VALUE;
		long order2 = order1-1;
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date = format.parse("2023/02/02");
		ProjectType projectType = ProjectType.builder().id("b8987dfa-f6e2-99d3-f511-3df3286bd7c0").name("Bosch")
				.build();

		Project project = Project.builder().createdBy("Huy").projectType(projectType).startDate(date)
				.name("TEST PROJECT").teamSize("2").createdBy("GLO7HC").build();

		Project project2 = Project.builder().createdBy("Huy").projectType(projectType).startDate(date)
				.name("TEST PROJECT 2").teamSize("2").createdBy("GLO7HC").build();

		oldSkillTag = SkillTag.builder().name("skill tag 1").order(order1).build();

		newSkillTag = SkillTag.builder().name("skill tag 2").order(order2).build();
		
		ProjectSkillTag projectSkillTag = ProjectSkillTag.builder().project(project).skillTag(oldSkillTag)
				.build();

		ProjectSkillTag projectSkillTag2 = ProjectSkillTag.builder().project(project2).skillTag(oldSkillTag)
				.build();
		
		ProjectSkillTag projectSkillTag3 = ProjectSkillTag.builder().project(project2).skillTag(newSkillTag)
				.build();
		
		Set<ProjectSkillTag> projectSkillTagSet = new HashSet<>();
		projectSkillTagSet.add(projectSkillTag);
		projectSkillTagSet.add(projectSkillTag2);
		oldSkillTag.setProjectSkillTags(projectSkillTagSet);
		newSkillTag.setProjectSkillTags(Collections.singleton(projectSkillTag3));

		projectRepository.saveAllAndFlush(Arrays.asList(project, project2));
		skillTagRepository.saveAllAndFlush(Arrays.asList(oldSkillTag, newSkillTag));
		projectSkillTagRepository.saveAllAndFlush(Arrays.asList(projectSkillTag, projectSkillTag2, projectSkillTag3));
	}
	
	@Test
	@DisplayName("Create skill tag")
	void createSkillTag() {
		SkillTagDto skillTagDto = SkillTagDto.builder().name("a").build();
		skillTagDto = skillTagService.addSkillTag(skillTagDto);
		assertNotEquals(null, skillTagDto.getId());
		assertThat("a").isEqualTo(skillTagDto.getName());
		SkillTag skillTag = skillTagRepository.findById(skillTagDto.getId()).orElseGet(null);
		assertThat("a").isEqualTo(skillTag.getName());
		assertThat(skillTagDto.getId()).isEqualTo(skillTag.getId());
	}
	
	@Test
	@DisplayName("Replace skill tag")
	void replaceSkillTag() {
		SkillTagDto oldSkillTagDto = SkillTagDto.builder().id(oldSkillTag.getId()).build();

		SkillTagDto newSkillTagDto = SkillTagDto.builder().id(newSkillTag.getId()).build();

		String result = skillTagService.replaceSkillTag(Arrays.asList(oldSkillTagDto, newSkillTagDto));
		assertThat(Constants.DONE).isEqualTo(result);
	}
	
	@Test
	@DisplayName("Replace skill tag - old skill tag not found")
	void replaceSkillTag_OldSkillTagNotFound() {
		SkillTagDto oldSkillTagDto = SkillTagDto.builder().id("dasds").build();

		SkillTagDto newSkillTagDto = SkillTagDto.builder().id(newSkillTag.getId()).build();

		List<SkillTagDto> skillTagDtos = Arrays.asList(oldSkillTagDto, newSkillTagDto);

		assertThrows(SkillManagementException.class, () -> skillTagService.replaceSkillTag(skillTagDtos));
	}
	
	@Test
	@DisplayName("Replace skill tag - new skill tag not found")
	void replaceSkillTag_NewSkillTagNotFound() {
		SkillTagDto oldSkillTagDto = SkillTagDto.builder().id(oldSkillTag.getId()).build();

		SkillTagDto newSkillTagDto = SkillTagDto.builder().id("dsad").build();

		List<SkillTagDto> skillTagDtos = Arrays.asList(oldSkillTagDto, newSkillTagDto);

		assertThrows(SkillManagementException.class, () -> skillTagService.replaceSkillTag(skillTagDtos));
	}
	
	@Test
	@DisplayName("Find project by skill tag id")
	void findProjectsBySkillTagsId() {
		List<ProjectDto> projectdtos = skillTagService.findProjectsBySkillTagsId(newSkillTag.getId());
		assertThat(projectdtos).hasSize(1);
	}
	
	@Test
	@DisplayName("Find project by skill tag id - skill tag not found")
	void findProjectsBySkillTagsId_SkillTagNotFound() {
		assertThrows(SkillManagementException.class, () -> skillTagService.findProjectsBySkillTagsId("dsadasdsd"));
	}
	
	@Test
	@DisplayName("Find by id")
	void findById() {
		SkillTagDto skillTagDto = skillTagService.findById(oldSkillTag.getId());
		assertThat(oldSkillTag.getName()).isEqualTo(skillTagDto.getName());
		}
	
	@Test
	@DisplayName("Find by id - Skill tag not found")
	void findById_SkillTagNotFound() {
		assertThrows(SkillManagementException.class, () -> skillTagService.findById("dsdsadsa"));
	}
	
	@Test
	@DisplayName("Update skill tag")
	void updateSkillTag() {
		String skillTagId = oldSkillTag.getId();
		SkillTagDto request = SkillTagDto.builder().name("New name").build();
		SkillTagDto skillTagDto = skillTagService.updateSkillTag(skillTagId, request);
		assertThat(request.getName()).isEqualTo(skillTagDto.getName());
		assertThat("New name").isEqualTo(skillTagDto.getName());	
		}
	
	@Test
	@DisplayName("Update skill tag - skill tag not found")
	void updateSkillTag_SkillTagNotFound() {
		SkillTagDto request = SkillTagDto.builder().name("New name").build();
		assertThrows(SkillManagementException.class, () -> skillTagService.updateSkillTag("id", request));
	}
	
	@Test
	@DisplayName("Find by ignore")
	void findByIgnore() {
		SkillTag skillTag = skillTagService.findByNameIgnoreCase(oldSkillTag.getName());
		assertThat(oldSkillTag).isEqualTo(skillTag);
		skillTag = skillTagService.findByNameIgnoreCase("dsadsdsad");
		assertThat(skillTag).isNull();
	}
	
	@Test
	@DisplayName("Save all")
	void saveAll() {
		SkillTag skillTag1 = SkillTag.builder().name("skill tag 1").order(1000000l).build();
		SkillTag skillTag2 = SkillTag.builder().name("skill tag 2").order(1000000l + 1).build();
		List<SkillTag> skillTags = skillTagService.saveAll(Arrays.asList(skillTag1, skillTag2));
		assertThat(skillTagRepository.findById(skillTags.get(0).getId())).isPresent();
		assertThat(skillTagRepository.findById(skillTags.get(1).getId())).isPresent();		
	}
	
	@Test
	@DisplayName("Save all")
	void findAll() {
		List<SkillTag> skillTags = skillTagService.findAll();
		assertThat(skillTags).hasSizeGreaterThanOrEqualTo(3);
	}
}
