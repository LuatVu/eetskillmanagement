package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.converter.PersonalConverter;
import com.bosch.eet.skill.management.converter.utils.CommonTaskConverterUtil;
import com.bosch.eet.skill.management.converter.utils.PhaseConverterUtil;
import com.bosch.eet.skill.management.converter.utils.ProjectRoleConverterUtil;
import com.bosch.eet.skill.management.dto.CommonTaskDto;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.PhaseDto;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.ProjectRoleDto;
import com.bosch.eet.skill.management.dto.VModelDto;
import com.bosch.eet.skill.management.entity.CommonTask;
import com.bosch.eet.skill.management.entity.GbUnit;
import com.bosch.eet.skill.management.entity.PersonalProject;
import com.bosch.eet.skill.management.entity.Phase;
import com.bosch.eet.skill.management.entity.PhaseProject;
import com.bosch.eet.skill.management.entity.ProjectRole;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.repo.CommonTaskRepository;
import com.bosch.eet.skill.management.repo.GBRepository;
import com.bosch.eet.skill.management.repo.PersonalProjectRepository;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.PhaseRepository;
import com.bosch.eet.skill.management.repo.ProjectRoleRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;
import com.bosch.eet.skill.management.service.impl.CommonServiceImpl;
import com.bosch.eet.skill.management.usermanagement.entity.Group;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.entity.UserGroup;
import com.bosch.eet.skill.management.usermanagement.repo.GroupRepository;
import com.bosch.eet.skill.management.usermanagement.repo.UserGroupRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class CommonServiceTest {

    @InjectMocks
    private CommonServiceImpl commonService;
    
    @Mock
    private SkillRepository skillReposistory;

    @Mock
    private ProjectRoleRepository projectRoleRepository;
    @Mock
    private GBRepository gbRepository;
    @Mock
    private CommonTaskRepository commonTasksRepository;
    @Mock
    private PersonalProjectRepository personalProjectRepository;
    @Mock
    private PersonalRepository personalRepository;
	@Mock
    private PhaseRepository phaseRepository;
	@Mock
    private GroupRepository groupRepository;
	@Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private ProjectRoleConverterUtil projectRoleConverter;
    @Mock
    private PersonalConverter personalConverter;
	@Mock
    private PhaseConverterUtil phaseConverter;
    @Mock
    private MessageSource messageSource;
    @Mock
    private MessageCode messageCode;


    //find
    @Test
    @DisplayName("Find all roles")
    @Transactional
    void getRole_returnListOfProjectRole() {
        when(projectRoleRepository.findAll()).thenReturn(
                Arrays.asList(
                        ProjectRole.builder().id("1").name("a").build(),
                        ProjectRole.builder().id("2").name("b").build(),
                        ProjectRole.builder().id("3").name("c").build()
                )
        );

    }
    //find null
    @Test
    @DisplayName("Find empty roles")
    void getRole_returnListOfEmptyProjectRole() {
        Pageable pageable = PageRequest.of(0, 5);
    	Map<String, String> testMap = new HashMap<>();

        when(projectRoleRepository.findAll()).thenReturn(
                new ArrayList<>()
        );
        Page<ProjectRoleDto> roles = commonService.findAllRoles(pageable, testMap);
        assertThat(roles).isEmpty();
    } 
    //find
    @Test
    @DisplayName("Find all GB")
    @Transactional
    void getGB_returnListOfGB() {
        when(gbRepository.findAll()).thenReturn(
                Arrays.asList(
                        GbUnit.builder().id("1").name("a").build(),
                        GbUnit.builder().id("2").name("b").build(),
                        GbUnit.builder().id("3").name("c").build()
                )
        );
        List<String> allGB = commonService.findAllGB();
        assertThat(allGB).isNotEmpty();
    }
    //find null
    @Test
    @DisplayName("Find empty GB")
    @Transactional
    void getGB_returnListOfEmptyGBe() {
        when(gbRepository.findAll()).thenReturn(
                new ArrayList<>()
        );
        List<String> allGB = commonService.findAllGB();
        assertThat(allGB).isEmpty();  
        }

	// Delete project role successful
	@Test
	@DisplayName("delete project role success")
	void deleteProjectRole_success() {
		ProjectRoleDto projectRoleDto = ProjectRoleDto.builder().id("pjid").name("pjname").build();

		ProjectRole projectRole = ProjectRole.builder().id("pjid").name("pjname")
				.commonTask(Arrays.asList(CommonTask.builder().name("hihi").build())).build();

		when(projectRoleRepository.findById("pjid")).thenReturn(Optional.of(projectRole));

		when(personalProjectRepository.findByProjectRoleId("pjid")).thenReturn(Arrays.asList());

		commonService.deleteProjectRole(Arrays.asList(projectRoleDto));
		Mockito.verify(projectRoleRepository).deleteById("pjid");
	}

	// Delete project role throw role not found
	@Test
	@DisplayName("delete project role not found")
	void deleteProjectRole_projectRoleNotFound() {
		ProjectRoleDto projectRoleDto = ProjectRoleDto.builder().id("pjid").name("pjname").build();

		when(projectRoleRepository.findById("pjid")).thenReturn(Optional.empty());

		assertThrows(SkillManagementException.class,
				() -> commonService.deleteProjectRole(Arrays.asList(projectRoleDto)));
	}

	// Delete project role throw role in use
	@Test
	@DisplayName("delete project role in use")
	void deleteProjectRole_projectRoleInUse() {
		ProjectRoleDto projectRoleDto = ProjectRoleDto.builder().id("pjid").name("pjname").build();

		ProjectRole projectRole = ProjectRole.builder().id("pjid").name("pjname")
				.commonTask(Arrays.asList(CommonTask.builder().name("hihi").build())).build();

		when(projectRoleRepository.findById("pjid")).thenReturn(Optional.of(projectRole));

		when(personalProjectRepository.findByProjectRoleId("pjid"))
				.thenReturn(Arrays.asList(PersonalProject.builder().id("ppid").build()));
		assertThrows(SkillManagementException.class,
				() -> commonService.deleteProjectRole(Arrays.asList(projectRoleDto)));

	}

	// Delete common task successful
	@Test
	@DisplayName("Delete common task success")
	void deleteCommonTask_successful() {
		CommonTaskDto ctDto = CommonTaskDto.builder().id("ctid").name("ctname").project_role_id("pjid").build();

		ProjectRole projectRole = ProjectRole.builder().id("pjid").name("pjname")
				.commonTask(Arrays.asList(CommonTask.builder().name("hihi").build())).build();

		CommonTask ct = CommonTask.builder().id("ctid").name("ctname").projectRole(projectRole).build();

		when(commonTasksRepository.findById("ctid")).thenReturn(Optional.of(ct));

		commonService.deleteCommonTask(Arrays.asList(ctDto));
		Mockito.verify(commonTasksRepository).deleteById("ctid");
	}

	// Delete common task not found
	@Test
	@DisplayName("Delete common task not found")
	void deleteCommonTask_notFound() {
		CommonTaskDto ctDto = CommonTaskDto.builder().id("ctid").name("ctname").project_role_id("pjid").build();

		when(commonTasksRepository.findById("ctid")).thenReturn(Optional.empty());

		assertThrows(SkillManagementException.class, () -> commonService.deleteCommonTask(Arrays.asList(ctDto)));
	}

	// Add new project role success
	@Test
	@DisplayName("add new project role success")
	void addProjectRole_happy() {
		ProjectRoleDto projectRoleDto = ProjectRoleDto.builder().id("pjid").name("pjname").build();

		when(projectRoleRepository.findByName(projectRoleDto.getName())).thenReturn(Optional.empty());

		ProjectRole newProjectRole = ProjectRole.builder().id(projectRoleDto.getId()).name(projectRoleDto.getName())
				.status("ACTIVE").build();

		when(projectRoleRepository.save(ArgumentMatchers.any())).thenReturn(newProjectRole);

		ProjectRoleDto newpProjectRoleDto = commonService.addNewProjectRole(projectRoleDto);
		log.info("New project role added: " + newpProjectRoleDto.toString());
		assertThat(newpProjectRoleDto).isNotNull();
	}

	// Add new project role name already exist
	@Test
	@DisplayName("add new project role name already exist")
	void addProjectRole_nameExist() {
		ProjectRoleDto projectRoleDto = ProjectRoleDto.builder().id("pjid").name("pjname").build();

		when(projectRoleRepository.findByName(projectRoleDto.getName()))
				.thenReturn(Optional.of(ProjectRole.builder().name(projectRoleDto.getName()).build()));

		assertThrows(SkillManagementException.class, () -> commonService.addNewProjectRole(projectRoleDto));
	}

	// add new common task success
	@Test
	@DisplayName("add new common task success")
	void addCommonTask_happy() {
		CommonTaskDto ctDto = CommonTaskDto.builder().id("ctid").name("ctname").project_role_id("pjid").build();

		ProjectRole projectRole = ProjectRole.builder().id("pjid").name("pjname")
				.commonTask(Arrays.asList(CommonTask.builder().name("hihi").build())).build();

		when(projectRoleRepository.findById(ctDto.getProject_role_id())).thenReturn(Optional.of(projectRole));

		CommonTask ct = CommonTask.builder().id("ctid").name("ctname").projectRole(projectRole).build();

		when(commonTasksRepository.save(ArgumentMatchers.any())).thenReturn(ct);

		CommonTaskDto newct = commonService.addNewCommonTask(CommonTaskConverterUtil.convertToDTO(ct));
		log.info("New common task added: " + newct.toString());
		assertThat(newct).isNotNull();
	}

	// add new common task project role not found
	@Test
	@DisplayName("add new common task null project role")
	void addCommonTask_nullProjectRole() {
		CommonTaskDto ctDto = CommonTaskDto.builder().id("ctid").name("ctname").project_role_id("pjid").build();

		when(projectRoleRepository.findById(ctDto.getProject_role_id()))
				.thenThrow(SkillManagementException.class);

		assertThrows(SkillManagementException.class, () -> commonService.addNewCommonTask(ctDto));
	}

	// add new common task common task exist
	@Test
	@DisplayName("add new common task success")
	void addCommonTask_nameExist() {
		CommonTaskDto ctDto = CommonTaskDto.builder().id("ctid").name("ctname").project_role_id("pjid").build();

		ProjectRole projectRole = ProjectRole.builder().id("pjid").name("pjname")
				.commonTask(Arrays.asList(CommonTask.builder().name("ctname").build())).build();
		when(projectRoleRepository.findById(ctDto.getProject_role_id())).thenReturn(Optional.of(projectRole));

		assertThrows(SkillManagementException.class, () -> commonService.addNewCommonTask(ctDto));
	}
	
	@Test
	@DisplayName("find line manager in happy case")
	public void findSkillsHightlight_HappyCase() {
		String idManager ="1";
		Optional<User> userDummy = Optional.ofNullable(User.builder().id(idManager).displayName("Name test").build());
		when(personalRepository.findLineManager(idManager)).thenReturn(userDummy);
		assertThat(commonService.findLineManager(idManager)).isEqualTo(personalConverter.userConvertToPersonalDto(userDummy.get()));     
	}

	@Test
	@DisplayName("find line manager (wrong id personal or not exist)")
	public void findLineManager_WrongIdManager() {
		String idManager ="1";
		Optional<User> userDummy = Optional.ofNullable(null);
		when(personalRepository.findLineManager(idManager)).thenReturn(userDummy);
		assertThat(commonService.findLineManager(idManager)).isEqualTo(PersonalDto.builder().id(null).build());  
	}
	@Test
	@DisplayName("find line manager (wrong id personal or not exist)")
	public void findSkillsHightlight_NullIdPersonal() {
		Optional<User> userDummy = Optional.ofNullable(User.builder().build());
		when(personalRepository.findLineManager(null)).thenReturn(userDummy);
		assertThat(commonService.findLineManager(null)).isEqualTo( personalConverter.userConvertToPersonalDto(userDummy.get()));  
	}

	@Test
	@DisplayName("get VModel")
	public void getVModel() {
		Phase phase = Phase.builder()
				.id("testPhaseDto")
				.name("Software Integration & System Integration Test")
				.phaseProjects(new ArrayList<PhaseProject>())
				.build();
		ArrayList<Phase> phaseArrayList = new ArrayList<>();
		phaseArrayList.add(phase);

		PhaseDto phaseDto = PhaseDto.builder()
				.id("testPhaseDto")
				.name("Software Integration & System Integration Test")
				.projects(new ArrayList<ProjectDto>())
				.build();
		ArrayList<PhaseDto> phaseDtoArrayList = new ArrayList<>();
		phaseDtoArrayList.add(phaseDto);
		VModelDto vModelDto = VModelDto.builder()
				.phases(phaseDtoArrayList)
				.build();

		when(phaseRepository.findAll()).thenReturn(phaseArrayList);
		assertThat(vModelDto).isEqualTo(commonService.getVModel());  
	}

	@Test
	@DisplayName("import skill from excel - check value column is mandatory and is required")
	public void import_skill_from_excel() {
		try {
			MultipartFile file = new MockMultipartFile("EET_Skills-Copy", "EET_Skills-Copy.xlsx", "application/vnd.ms-excel",
					new ClassPathResource("EET_Skills - Copy.xlsx").getInputStream());
			List<Skill> skillList = new ArrayList<>();
			List<String> nameSkill = new ArrayList<>();
			HashMap<String, HashMap<String, HashMap<String, Object>>> hashMap = commonService.readSkillFromXlsx(file);
			for (String skillGroup : hashMap.keySet()) {
				for (String skill : hashMap.get(skillGroup).keySet()) {
					String isMandatory = (String) hashMap.get(skillGroup).get(skill).get(Constants.IS_MANDATORY);
					String isRequired = (String) hashMap.get(skillGroup).get(skill).get(Constants.IS_REQUIRED);
					Skill s = Skill.builder()
							.name(skill)
							.build();
					if (StringUtils.isNotEmpty(isMandatory)) {
						s.setIsMandatory(isMandatory.equals(Constants.YES) ? true
								: isMandatory.equals(Constants.NO) ? false : null);
					}
					if (StringUtils.isNotEmpty(isRequired)) {
						s.setIsRequired(isRequired.equals(Constants.YES) ? true
								: isRequired.equals(Constants.NO) ? false : null);
					}
					if(!nameSkill.contains(skill.toUpperCase())) {
						nameSkill.add(skill.toUpperCase());
						skillList.add(s);
					}
				}
			}
			List<Skill> skillFindAll = new ArrayList<>();
			skillFindAll.addAll(skillList);
			skillFindAll.add(Skill.builder().name("ccc").isRequired(true).isMandatory(false).build());
			when(skillReposistory.findAll()).thenReturn(skillFindAll);
			List<Skill> skillImportSuccess = new ArrayList<>();
			
			for (Skill skill : skillList) {
				if(skillFindAll.contains(skill)) {
					skillImportSuccess.add(skill);
				}
			}
			assertThat(skillImportSuccess).isEqualTo(skillList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@DisplayName("Find all personal manager role")
	void findAllPersonalManagerRole() {
		Group group = Group.builder().id("dasdsa").name("name").build();
		User user = User.builder().id("iduser").displayName("Long").build();
		List<PersonalDto> personalDtos = Collections
				.singletonList(PersonalDto.builder().id("iduser").name("Long").build());
		when(groupRepository.findByName(Constants.MANAGER)).thenReturn(Optional.ofNullable(group));
		when(userGroupRepository.findByGroupId(group.getId()))
				.thenReturn(Collections.singletonList(UserGroup.builder().user(user).build()));
		when(personalConverter.userConvertToPersonalDtos(Collections.singletonList(user)))
				.thenReturn(personalDtos);
		assertThat(commonService.findAllPersonalManagerRole()).isEqualTo(personalDtos);
	}
	
	@Test
	@DisplayName("Find all personal manager role group not found")
	void findAllPersonalManagerRole_GroupNotFound() {
		when(groupRepository.findByName(Constants.MANAGER)).thenReturn(Optional.ofNullable(null));
		assertThrows(SkillManagementException.class, () -> commonService.findAllPersonalManagerRole());
	}
}
