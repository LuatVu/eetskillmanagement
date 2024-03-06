/**
 * 
 */
package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.converter.utils.ProjectConverterUtil;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.SkillTagDto;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectSkillTag;
import com.bosch.eet.skill.management.entity.ProjectType;
import com.bosch.eet.skill.management.entity.SkillTag;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.repo.ProjectSkillTagRepository;
import com.bosch.eet.skill.management.repo.SkillTagRepository;
import com.bosch.eet.skill.management.service.impl.SkillTagServiceImpl;

import lombok.extern.slf4j.Slf4j;

/**
 * @author VOU6HC
 */

@Slf4j
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SkillTagServiceTest {

	 	@InjectMocks
	 	@Spy
	    private SkillTagServiceImpl skillTagService;

	    @Mock
	    private SkillTagRepository skillTagRepository;
	    
	    @Mock
        private ProjectSkillTagRepository projectSkillTagRepository;

		@Mock
		private ProjectConverterUtil projectConverter;
	    
	    @Mock
	    private MessageSource messageSourceMock;

	    //find
	    @Test
	    @DisplayName("Find all skill tags")
	    @Transactional
	    void getSkillTag_returnListOfSkillTag() {

			SkillTag.builder()
					.id("1")
					.name("Test Skill Tag")
					.build();

			SkillTagDto skillTagDto = SkillTagDto.builder()
					.id("1")
					.name("Test Skill Tag")
					.build();

			when(skillTagRepository.findAllDetailDtoCountProjectByType(Constants.BOSCH)).thenReturn(
					Collections.singletonList(skillTagDto));

			List<SkillTagDto> actualResult = skillTagService.findAllSkillTags();
			System.out.println(actualResult);
			assertThat(actualResult).isNotEmpty();
	    }

	    //find null
	    @Test
	    @DisplayName("Find empty skill tags")
	    void getSkillTag_returnListOfEmptySkillTag() {
	        when(skillTagRepository.findAll()).thenReturn(
	                new ArrayList<>()
	        );
	        List<SkillTagDto> skills = skillTagService.findAllSkillTags();
	        assertThat(skills).isEmpty();
	    }

		@Test
		@DisplayName("Create skill tag duplicate case")
		void createSkillTag_Duplicate() {
			SkillTagDto skillTagDto = SkillTagDto.builder().id("1").name("a").build();

			SkillTag skillTag = SkillTag.builder().id("1").name("a").build();

			when(skillTagRepository.findByName("a")).thenReturn(Optional.of(skillTag));

			assertThrows(SkillManagementException.class, () -> skillTagService.addSkillTag(skillTagDto));
		}
	    
		@Test
		@DisplayName("Create skill tag - name skill tag is empty")
		void createSkillTag_NameSkillTagIsEmpty() {
			SkillTagDto skillTagDto = SkillTagDto.builder().name(StringUtils.EMPTY).build();
			assertThrows(SkillManagementException.class, () -> skillTagService.addSkillTag(skillTagDto));
		}

		@Test
		@DisplayName("Delete skill tag")
		void deleteSkillTag() {
			SkillTag skillTag = SkillTag.builder().id("1").name("a").build();

			SkillTagDto skillTagDto = SkillTagDto.builder().id("1").name("a").build();

			when(skillTagRepository.findById("1")).thenReturn(Optional.ofNullable(skillTag));
			doNothing().when(skillTagRepository).delete(skillTag);

			assertThat(skillTagService.deleteSkillTag(skillTagDto)).isTrue();
			Mockito.verify(skillTagRepository).deleteById(skillTagDto.getId());
		}
		
		@Test
		@DisplayName("Delete skill tag - skill tag not found")
		void deleteSkillTag_SkillTagNotFound() {
			SkillTagDto skillTagDto = SkillTagDto.builder().id("1").name("a").build();

			when(skillTagRepository.findById("dasdsa")).thenReturn(Optional.ofNullable(null));
			assertThrows(SkillManagementException.class, () -> skillTagService.deleteSkillTag(skillTagDto));
		}

	    @Test
	    @DisplayName("Update skill tag")
	    void updateSkillTag() {
	        SkillTag skillTag = SkillTag.builder()
	                .id("1")
	                .name("as")
	                .order(1l)
	                .build();
	        SkillTag skillTag2 = SkillTag.builder()
	                .id("1")
	                .name("a")
	                .order(2l)
	                .build();

	        SkillTagDto skillTagDto = SkillTagDto.builder()
	                .id("1")
	                .name("a")
	                .order(1l)
	                .build();
	        
	        List<SkillTag> skillTags = new ArrayList<>();
	        skillTags.add(skillTag);
	        
	        List<SkillTag> skillTags2 = new ArrayList<>();
	        skillTags2.add(skillTag2);
	        
	        List<SkillTagDto> skillTagDtos = new ArrayList<>();
	        skillTagDtos.add(skillTagDto);
	        
	        List<String> names = new ArrayList<>();
	        names.add("a");
	        
	        when(skillTagRepository.findByNameIn(names)).thenReturn(skillTags2);
	        
	        when(skillTagRepository.saveAll(skillTags)).thenReturn(skillTags);
	        
	        assertThat(Constants.DONE).isEqualTo(skillTagService.updateOrder(skillTagDtos));
	    }

		@Test
		@DisplayName("Find Skill Tag By Id")
		void findById() {
			SkillTagDto skillTagDto = SkillTagDto.builder()
					.id("1")
					.name("Test Skill Tag DTO")
					.order(1L)
					.build();

			SkillTag skillTag = SkillTag.builder()
					.id("1")
					.name("Test Skill Tag")
					.order(1L)
					.build();

			Optional<SkillTag> of = spy(Optional.of(skillTag));
			when(skillTagRepository.findById(skillTagDto.getId())).thenReturn(of);
			when(of.isPresent()).thenReturn(true); 
			doReturn(skillTagDto).when(skillTagService).convertToDetailDto(skillTag);		
			assertThat(skillTagDto).isEqualTo(skillTagService.findById(skillTagDto.getId()));
		}

	@Test
	@DisplayName("Find Skill Tag By Empty Id - Then throw an exception")
	void findByIdThenThrowAnException() {
		SkillTagDto skillTagDto = SkillTagDto.builder()
				.id("1")
				.name("Test Skill Tag DTO")
				.order(1L)
				.build();

		SkillTag.builder()
				.id("1")
				.name("Test Skill Tag")
				.order(1L)
				.build();

		when(skillTagRepository.findById(skillTagDto.getId())).thenReturn(Optional.empty());
		assertThrows(SkillManagementException.class, () -> skillTagService.findById(skillTagDto.getId()));
	}

		@Test
		@DisplayName("Save all Skill Tags")
		void saveAll() {

			ProjectType projectType = ProjectType.builder()
					.id("1")
					.name(Constants.BOSCH)
					.build();

			Project project = Project.builder()
					.id("1")
					.name("Simple Project")
					.projectType(projectType)
					.build();

			ProjectSkillTag projectSkillTag = ProjectSkillTag.builder()
					.id("1")
					.project(project)
					.build();

			Set<ProjectSkillTag> projectSkillTagSet = new HashSet<>();
			projectSkillTagSet.add(projectSkillTag);

			SkillTag skillTag1 = SkillTag.builder()
					.id("1")
					.name("skill tag 1")
					.projectSkillTags(projectSkillTagSet)
					.build();

			SkillTag skillTag2 = SkillTag.builder()
					.id("2")
					.name("skill tag 2")
					.projectSkillTags(projectSkillTagSet)
					.build();

			SkillTag skillTag3 = SkillTag.builder()
					.id("3")
					.name("skill tag 3")
					.projectSkillTags(projectSkillTagSet)
					.build();

			projectSkillTag.setSkillTag(skillTag1);
			projectSkillTag.setSkillTag(skillTag2);
			projectSkillTag.setSkillTag(skillTag3);

			List<SkillTag> skillTags = new ArrayList<>(Arrays.asList(skillTag1, skillTag2, skillTag3));

			when(skillTagRepository.saveAll(skillTags)).thenReturn(skillTags);

			assertThat(skillTags).isEqualTo(skillTagService.saveAll(skillTags));

		}

	@Test
	@DisplayName("find all")
	void findAll() {

		ProjectType projectType = ProjectType.builder()
				.id("1")
				.name(Constants.BOSCH)
				.build();

		Project project = Project.builder()
				.id("1")
				.name("Simple Project")
				.projectType(projectType)
				.build();

		ProjectSkillTag projectSkillTag = ProjectSkillTag.builder()
				.id("1")
				.project(project)
				.build();

		Set<ProjectSkillTag> projectSkillTagSet = new HashSet<>();
		projectSkillTagSet.add(projectSkillTag);

		SkillTag skillTag1 = SkillTag.builder()
				.id("1")
				.name("skill tag 1")
				.projectSkillTags(projectSkillTagSet)
				.build();

		SkillTag skillTag2 = SkillTag.builder()
				.id("2")
				.name("skill tag 2")
				.projectSkillTags(projectSkillTagSet)
				.build();

		SkillTag skillTag3 = SkillTag.builder()
				.id("3")
				.name("skill tag 3")
				.projectSkillTags(projectSkillTagSet)
				.build();

		List<SkillTag> skillTags = new ArrayList<>(Arrays.asList(skillTag1, skillTag2, skillTag3));

		when(skillTagRepository.findAll()).thenReturn(skillTags);

		System.out.println(skillTagService.findAll().size());

		assertThat(skillTags).isEqualTo(skillTagService.findAll());

	}

	@Test
	@DisplayName("Find Skill Tag By Name Ignore Case")
	void findSkillTagByNameIgnoreCase() {
		String skillTagName = "jAvA";

		SkillTag skillTag = SkillTag.builder()
				.id("1")
				.name("Java")
				.order(1L)
				.build();

		when(skillTagRepository.findByNameIgnoreCase(skillTagName)).thenReturn(Optional.of(skillTag));

		assertNotNull(skillTagService.findByNameIgnoreCase(skillTagName));
	}

	@Test
	@DisplayName("Find list of project by skill tag Id")
	void findListOfProjectBySkillTagId() {
		ProjectType projectType = ProjectType.builder()
				.id("1")
				.name(Constants.BOSCH)
				.build();

		Project project1 = Project.builder()
				.id("1")
				.name("Simple Project 1")
				.projectType(projectType)
				.build();

		Project project2 = Project.builder()
				.id("2")
				.name("Simple Project 2")
				.projectType(projectType)
				.build();

		Project project3 = Project.builder()
				.id("3")
				.name("Simple Project 3")
				.projectType(projectType)
				.build();

		List<Project> projectList = new ArrayList<>(Arrays.asList(project1, project2, project3));
		List<ProjectDto> projectDtoList;
		projectDtoList = projectConverter.convertToListOfProjectContainSkillTag(projectList);

		ProjectSkillTag projectSkillTag = ProjectSkillTag.builder()
				.id("1")
				.build();

		Set<ProjectSkillTag> projectSkillTagSet = new HashSet<>();

		SkillTag skillTag = SkillTag.builder()
				.id("1")
				.name("Java")
				.order(1L)
				.projectSkillTags(projectSkillTagSet)
				.build();

		projectSkillTag.setSkillTag(skillTag);
		projectSkillTagSet.add(projectSkillTag);

		when(skillTagRepository.existsById(skillTag.getId())).thenReturn(true);
		when(projectSkillTagRepository.findProjectBySkillTagId(skillTag.getId())).thenReturn(projectList);
		assertThat(projectDtoList).isEqualTo(skillTagService.findProjectsBySkillTagsId(skillTag.getId()));

	}

	@Test
	@DisplayName("Find list of project by empty skill tag Id - Then throw an exception")
	void findListOfProjectByEmptySkillTagIdThenThrowAnException() {
        String emptySkillTagId = StringUtils.EMPTY;
		ProjectSkillTag projectSkillTag = ProjectSkillTag.builder()
				.id("1")
				.build();

		Set<ProjectSkillTag> projectSkillTagSet = new HashSet<>();

		SkillTag skillTag = SkillTag.builder()
				.id("1")
				.name("Java")
				.order(1L)
				.projectSkillTags(projectSkillTagSet)
				.build();

		projectSkillTag.setSkillTag(skillTag);
		projectSkillTagSet.add(projectSkillTag);

		when(skillTagRepository.existsById(emptySkillTagId)).thenReturn(false);

		assertThrows(SkillManagementException.class, () -> skillTagService.findProjectsBySkillTagsId(emptySkillTagId));

	}

	@Test
	@DisplayName("Update Skill Tag - Happy Case")
	void updateSkillTagHappyCase() {
		SkillTagDto skillTagDto = SkillTagDto.builder()
				.id("1")
				.name("New Skill Tag name")
				.order(1L)
				.build();

		SkillTag skillTag = SkillTag.builder()
				.id("1")
				.name("Old Skill Tag name")
				.order(1L)
				.build();

		skillTag.setName(skillTagDto.getName());
		when(skillTagService.getSkillTagEntity(skillTagDto)).thenReturn(skillTag);
		when(skillTagRepository.findById(skillTag.getId())).thenReturn(Optional.of(skillTag));
		when(skillTagRepository.save(skillTag)).thenReturn(skillTag);
		when(skillTagService.convertToSkillTagSimpleDto(skillTag)).thenReturn(skillTagDto);
		assertThat(skillTagDto).isEqualTo(skillTagService.updateSkillTag(skillTag.getId(), skillTagDto));
	}

	@Test
	@DisplayName("Update Skill Tag with invalid Id - Throw an exception")
	void updateSkillTagWithInvalidIdThenThrowAnException() {
			String invalidSkillTagId = StringUtils.EMPTY;
			SkillTagDto skillTagDto = SkillTagDto.builder().build();

			when(skillTagRepository.findById(invalidSkillTagId)).thenThrow(SkillManagementException.class);

			assertThrows(SkillManagementException.class, () -> skillTagService.updateSkillTag(invalidSkillTagId, skillTagDto));
	}

	@Test
	@DisplayName("Replace SKill Tag when old skill tag not found - then throw an exception")
	void replaceSkillTagWhenOldSkillTagNotFound() {
			SkillTagDto oldSkillTagDTO = SkillTagDto.builder().build();
			SkillTagDto newSkillTagDTO = SkillTagDto.builder()
					.id("1")
					.name("New Skill Tag")
					.order(1L)
					.build();

			SkillTag newSkillTag = SkillTag.builder()
					.id("1")
					.name("New Skill Tag")
					.order(1L)
					.build();

			List<SkillTagDto> skillTagDtoList = Arrays.asList(oldSkillTagDTO, newSkillTagDTO);

			String invalidOldSkillTagId = StringUtils.EMPTY;

			when(skillTagRepository.findById(invalidOldSkillTagId)).thenThrow(SkillManagementException.class);
			when(skillTagRepository.findById(newSkillTagDTO.getId())).thenReturn(Optional.of(newSkillTag));

			assertThrows(SkillManagementException.class, () -> skillTagService.replaceSkillTag(skillTagDtoList));
	}

	@Test
	@DisplayName("Replace SKill Tag when new skill tag not found - then throw an exception")
	void replaceSkillTagWhenNewSkillTagNotFound() {
		SkillTagDto newSkillTagDTO = SkillTagDto.builder().build();
		SkillTagDto oldSkillTagDTO = SkillTagDto.builder()
				.id("1")
				.name("Old Skill Tag")
				.order(1L)
				.build();
		SkillTag oldSkillTag = SkillTag.builder()
				.id("1")
				.name("Old Skill Tag")
				.order(1L)
				.build();

		List<SkillTagDto> skillTagDtoList = Arrays.asList(oldSkillTagDTO, newSkillTagDTO);
		String invalidNewSkillTagId = StringUtils.EMPTY;

		when(skillTagRepository.findById(oldSkillTagDTO.getId())).thenReturn(Optional.of(oldSkillTag));
		when(skillTagRepository.findById(invalidNewSkillTagId)).thenThrow(SkillManagementException.class);

		assertThrows(SkillManagementException.class, () -> skillTagService.replaceSkillTag(skillTagDtoList));
	}

	@Test
	@DisplayName("Replace 2 Skill Tags - Happy Case")
	void replaceSkillTagsHappyCase() {
		ProjectType projectType = ProjectType.builder()
				.id("1")
				.name(Constants.BOSCH)
				.build();

		Project project = Project.builder()
				.id("1")
				.name("Simple Project")
				.projectType(projectType)
				.build();

		SkillTagDto oldSkillTagDTO = SkillTagDto.builder()
				.id("1")
				.name("Old Skill Tag")
				.order(1L)
				.build();

		SkillTag oldSkillTag = SkillTag.builder()
				.id("1")
				.name("Old Skill Tag")
				.order(1L)
				.build();

		SkillTagDto newSkillTagDTO = SkillTagDto.builder()
				.id("2")
				.name("New Skill Tag")
				.order(2L)
				.build();

		SkillTag newSkillTag = SkillTag.builder()
				.id("1")
				.name("New Skill Tag")
				.order(2L)
				.build();

		List<SkillTagDto> skillTagRequest = Arrays.asList(oldSkillTagDTO, newSkillTagDTO);

		ProjectSkillTag projectSkillTag = ProjectSkillTag.builder()
				.id("1")
				.project(project)
				.skillTag(oldSkillTag)
				.build();

		when(skillTagRepository.findById(oldSkillTagDTO.getId())).thenReturn(Optional.of(oldSkillTag));
		when(skillTagRepository.findById(newSkillTagDTO.getId())).thenReturn(Optional.of(newSkillTag));
		when(projectSkillTagRepository.findBySkillTagId(oldSkillTag.getId())).thenReturn(Collections.singletonList(projectSkillTag));
		when(projectSkillTagRepository.findProjectIdBySkillTagId(newSkillTag.getId())).thenReturn(Collections.singletonList(project.getId()));
		doNothing().when(projectSkillTagRepository).deleteById(projectSkillTag.getId());
		doNothing().when(projectSkillTagRepository).replaceSkillTag(projectSkillTag.getSkillTag().getId(), newSkillTag.getId());
		doNothing().when(skillTagRepository).deleteById(oldSkillTag.getId());
		when(skillTagRepository.saveAndFlush(newSkillTag)).thenReturn(newSkillTag);

		assertThat(Constants.DONE).isEqualTo(skillTagService.replaceSkillTag(skillTagRequest));

	}


	    
}
