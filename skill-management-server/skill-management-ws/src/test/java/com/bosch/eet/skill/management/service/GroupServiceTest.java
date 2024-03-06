package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.SkillManagementWsApplication;
import com.bosch.eet.skill.management.common.Status;
import com.bosch.eet.skill.management.usermanagement.dto.GroupDTO;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Group;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementBusinessException;
import com.bosch.eet.skill.management.usermanagement.repo.GroupRepository;
import com.bosch.eet.skill.management.usermanagement.repo.GroupRoleRepository;
import com.bosch.eet.skill.management.usermanagement.repo.UserGroupRepository;
import com.bosch.eet.skill.management.usermanagement.service.GroupService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = SkillManagementWsApplication.class)
@ActiveProfiles("test")
public class GroupServiceTest {

	@Autowired
	private GroupService groupService;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private UserGroupRepository userGroupRepository;

	@Autowired
	private GroupRoleRepository groupRoleRepository;

	// find
	@Test
	@Transactional
	void getAllGroup_returnListOfActiveGroup() {

		List<Group> allGroup = groupService.getAllGroup();

		boolean noInactiveGroup = allGroup.parallelStream()
				.noneMatch(groupDTO -> Status.INACTIVE.getLabel().equals(groupDTO.getStatus()));
		assertThat(noInactiveGroup).isTrue();
		assertNotEquals(0, allGroup.size());
	}

	@Test
	@Transactional
	public void findGroupById_groupFound_returnGroup() {

		Group admin = groupService.findGroupById("adf5a395-3359-4fc5-a5a9-c9036206bb29");
		System.out.println(admin.getGroupRoles().size());
		assertThat("Admin").isEqualTo( admin.getName());
		assertNotEquals(0, admin.getGroupRoles().size());
		assertNotEquals(0, admin.getUsersGroup().size());
	}

	@Test
	@Transactional
	public void findGroupById_groupNotFound_throwException() {

		assertThrows(UserManagementBusinessException.class, () -> groupService.findGroupById("Not found"));
	}

	// create
	@Test
	@Transactional
	void createGroup_modifyingUserNotFound_throw() {

		GroupDTO groupDTO = new GroupDTO();

		groupDTO.setGroupName("Test group");
		groupDTO.setStatus(Status.ACTIVE.getLabel());

		assertThrows(UserManagementBusinessException.class, () -> groupService.createGroup(groupDTO, "Not found"));
	}

	@Test
	@Transactional
	void createGroup_groupNameFoundAndActive_throw() {

		GroupDTO groupDTO = new GroupDTO();

		groupDTO.setGroupName("user");
		groupDTO.setStatus(Status.ACTIVE.getLabel());

		assertThrows(UserManagementBusinessException.class, () -> groupService.createGroup(groupDTO, "admin"));
	}

	@Test
	@Transactional
	void createGroup_groupNameFoundAndInActive_groupBecomeActive() {

		GroupDTO groupDTO = new GroupDTO();

		groupDTO.setGroupName("inactive");
		groupDTO.setStatus(Status.INACTIVE.getLabel());

		Group updateGroup = groupService.createGroup(groupDTO, "VOU6HC");

		System.out.println(updateGroup.getDisplayName());
		System.out.println(updateGroup.getName());
		System.out.println(updateGroup.getStatus());
		assertThat(Status.ACTIVE.getLabel()).isEqualTo(updateGroup.getStatus());
		assertThat("VOU6HC").isEqualTo(updateGroup.getModifiedBy());
	}

	@Test
	@Transactional
	void createGroup_groupNameNotFound_saveGroup() {

		GroupDTO groupDTO = new GroupDTO();
		String modifiedUser = "VOU6HC";

		groupDTO.setGroupName("add_group");
		groupDTO.setStatus(Status.ACTIVE.getLabel());

		Group createGroup = groupService.createGroup(groupDTO, modifiedUser);
		assertThat(Status.ACTIVE.getLabel()).isEqualTo(createGroup.getStatus());
		assertThat(modifiedUser).isEqualTo(createGroup.getCreatedBy());
		assertThat(modifiedUser).isEqualTo(createGroup.getModifiedBy());
	}

	// update
	@Test
	@Transactional
	void updateGroup_modifierUserNotFound_throwException() {

		Group group = Group.builder().name("Test group").status(Status.ACTIVE.getLabel()).build();

		GroupDTO groupDTO = new GroupDTO();
		groupDTO.setGroupId(group.getId());
		groupDTO.setGroupName("Update test group");
		groupDTO.setStatus(Status.ACTIVE.getLabel());

		assertThrows(UserManagementBusinessException.class, () -> groupService.updateGroup(groupDTO, "Not found"));
	}

	@Test
	@Transactional
	void updateGroup_groupNameExist_throwException() {

		GroupDTO groupDTO = new GroupDTO();
		groupDTO.setGroupId("admin");
		groupDTO.setGroupName("user");
		groupDTO.setStatus(Status.ACTIVE.getLabel());

		assertThrows(UserManagementBusinessException.class, () -> groupService.createGroup(groupDTO, "admin"));
	}

	@Test
	@Transactional
	void updateGroup_groupNameNotExist_thenThrowGroupNotFound() {

		GroupDTO groupDTO = new GroupDTO();
		groupDTO.setGroupId("admin");
		groupDTO.setGroupName("test_update");
		groupDTO.setStatus(Status.ACTIVE.getLabel());
		String modifiedUser = "VOU6HC";

		assertThrows(UserManagementBusinessException.class, () -> groupService.updateGroup(groupDTO, modifiedUser));
	}

	@Test
	@Transactional
	void deleteGroup_modifyUserNotFound() {

		assertThrows(UserManagementBusinessException.class, () -> groupService.deleteGroup("admin", "Not found"));
	}

	// delete
	@Test
	@Transactional
	void deleteGroup_groupNotFound_throwException() {

		assertThrows(UserManagementBusinessException.class, () -> groupService.deleteGroup("Unknown group", "admin"));
	}

	@Test
	@Transactional
	void deleteGroup_deleteSuccessfully() {

		String adminGroupId = "adf5a395-3359-4fc5-a5a9-c9036206bb29";
		String modifiedUser = "VOU6HC";

		Group group = groupService.deleteGroup(adminGroupId, modifiedUser);

		assertThat(group.getStatus()).isEqualTo(Status.INACTIVE.getLabel());
		assertThat(group.getGroupRoles()).isEmpty();
		assertThat(group.getUserGroups()).isEmpty();
	}

	// addusertogroup
	@Test
	@Transactional
	void addUsersToGroup_groupNotExist() {

		String groupId = "not found";

		UserDTO userNotInGroupYet = UserDTO.builder().name("user_active").build();

		UserDTO userAlreadyInGroup = UserDTO.builder().name("admin").build();

		List<UserDTO> userDTOS = Arrays.asList(userNotInGroupYet, userAlreadyInGroup);
		assertThrows(UserManagementBusinessException.class,
				() -> groupService.addUsersToGroup(groupId, userDTOS, "admin"));
	}

	@Test
	@Transactional
	void addUsersToGroup_groupInactive_throw() {

		String groupId = "inactive";

		UserDTO userNotInGroupYet = UserDTO.builder().name("user_active").build();

		UserDTO userAlreadyInGroup = UserDTO.builder().name("admin").build();

		List<UserDTO> userDTOS = Arrays.asList(userNotInGroupYet, userAlreadyInGroup);
		assertThrows(UserManagementBusinessException.class,
				() -> groupService.addUsersToGroup(groupId, userDTOS, "admin"));
	}

	@Test
	@Transactional
	void addUsersToGroup_modifyingUserNotFound_throw() {

		String groupId = "inactive";

		UserDTO userNotInGroupYet = UserDTO.builder().name("user_active").build();

		UserDTO userAlreadyInGroup = UserDTO.builder().name("admin").build();

		List<UserDTO> userDTOS = Arrays.asList(userNotInGroupYet, userAlreadyInGroup);
		assertThrows(UserManagementBusinessException.class,
				() -> groupService.addUsersToGroup(groupId, userDTOS, "not found"));
	}

	@Test
	@Transactional
	void addUsersToGroup_addSuccessfully() {

//    	Group Admin ID
		String groupId = "adf5a395-3359-4fc5-a5a9-c9036206bb29";
		String modifiedByUser = "VOU6HC";

		UserDTO user1 = UserDTO.builder().id("1").name("user1").build();

		UserDTO user2 = UserDTO.builder().id("2").name("user2").build();

		int sizeBeforeAdd = userGroupRepository.findAll().size();

		List<UserDTO> userDTOS = Arrays.asList(user1, user2);

		Group group = groupService.addUsersToGroup(groupId, userDTOS, modifiedByUser);

		assertThat((int) group.getUserGroups().stream().filter(userGroup -> userGroup.getUser().getName().equals("user1")
				|| userGroup.getUser().getName().equals("user2")).count()).isEqualTo(2);
		
		assertThat(modifiedByUser).isEqualTo(group.getModifiedBy());
		assertThat(userGroupRepository.findAll()).hasSize(sizeBeforeAdd+1);
		assertNotNull(userGroupRepository.findByGroupIdAndUserId(groupId, user1.getId()).orElse(null));
	}

	@Test
	@Transactional
	void removeUserFromGroup_groupNotFound_thenThrow() {

		String groupId = "not found";

		String userId = "admin";

		String modifiedBy = "admin";

		assertThrows(UserManagementBusinessException.class,
				() -> groupService.removeUserFromGroup(groupId, userId, modifiedBy));
	}

	@Test
	@Transactional
	void removeUserFromGroup_modifiedByNotFound_thenThrow() {

		String groupId = "admin";

		String userId = "admin";

		String modifiedBy = "not found";

		assertThrows(UserManagementBusinessException.class,
				() -> groupService.removeUserFromGroup(groupId, userId, modifiedBy));
	}

	@Test
	@Transactional
	void removeUserFromGroup_removeIdNotFound_thenThrow() {

		String groupId = "admin";

		String userId = "not found";

		String modifiedBy = "admin";

		assertThrows(UserManagementBusinessException.class,
				() -> groupService.removeUserFromGroup(groupId, userId, modifiedBy));
	}

	@Test
	@Transactional
	void removeUserFromGroup_groupInactive_thenThrow() {

		String groupId = "inactive";

		String userId = "admin";

		String modifiedBy = "admin";

		assertThrows(UserManagementBusinessException.class,
				() -> groupService.removeUserFromGroup(groupId, userId, modifiedBy));
	}

	@Test
	@Transactional
	void removeUserFromGroup_removeSuccessfully() {

		// Group Admin ID
		String groupId = "adf5a395-3359-4fc5-a5a9-c9036206bb29";

		String userId = "d3cbec19-6680-4320-b611-44b7ca3cb5d6";

		String modifiedBy = "VOU6HC";

		groupService.removeUserFromGroup(groupId, userId, modifiedBy);

		assertNull(userGroupRepository.findByGroupIdAndUserId(groupId, userId).orElse(null));

		assertThat(groupRepository.findById("adf5a395-3359-4fc5-a5a9-c9036206bb29").get().getUserGroups().stream()
		.noneMatch(userGroup -> userGroup.getUser().getId().equals("d3cbec19-6680-4320-b611-44b7ca3cb5d6"))).isTrue();
	}

	@Test
	@Transactional
	void addRoleToGroup_groupIdNotFound_thenThrow() {

		String groupId = "not found";

		String roleExistedId = "admin";

		String roleNotExistedYet = "user_role";

		String modifiedBy = "admin";

		assertThrows(UserManagementBusinessException.class, () -> groupService.addRolesToGroup(groupId,
				Arrays.asList(roleExistedId, roleNotExistedYet), modifiedBy));
	}

	@Test
	@Transactional
	void addRoleToGroup_groupInActive_thenThrow() {

		String groupId = "inactive";

		String roleExistedId = "admin";

		String roleNotExistedYet = "user_role";

		String modifiedBy = "admin";

		assertThrows(UserManagementBusinessException.class, () -> groupService.addRolesToGroup(groupId,
				Arrays.asList(roleExistedId, roleNotExistedYet), modifiedBy));
	}

	@Test
	@Transactional
	void addRoleToGroup_modifyingUserNotFound_thenThrow() {

		String groupId = "admin";

		String roleExistedId = "admin";

		String roleNotExistedYet = "user_role";

		String modifiedBy = "not found";

		assertThrows(UserManagementBusinessException.class, () -> groupService.addRolesToGroup(groupId,
				Arrays.asList(roleExistedId, roleNotExistedYet), modifiedBy));
	}

	@Test
	@Transactional
	void addRoleToGroup_addRoleSuccessfully() {

		String adminGroupId = "adf5a395-3359-4fc5-a5a9-c9036206bb29";

		// Admin role
		String roleExistedId = "rladmin3-d1c0-11ec-81c0-38f3ab0673e4";

		String roleNotExistedYet = "role_not_exist";

		String modifiedBy = "VOU6HC";

		int sizeBeforeAdd = groupRoleRepository.findAll().size();

		Group group = groupService.addRolesToGroup(adminGroupId, Arrays.asList(roleExistedId, roleNotExistedYet),
				modifiedBy);
		assertThat(group.getGroupRoles().stream()
				.anyMatch(groupRole -> groupRole.getRole().getId().equals(roleNotExistedYet))).isTrue();
		assertThat(groupRoleRepository.findAll()).hasSize(sizeBeforeAdd+1);
	}

	@Test
	@Transactional
	void removeRoleToGroup_groupNotFound_thenThrow() {

		String groupId = "not found";

		String roleExistedId = "admin";

		String modifiedBy = "admin";

		assertThrows(UserManagementBusinessException.class,
				() -> groupService.removeUserFromGroup(groupId, roleExistedId, modifiedBy));
	}

	@Test
	@Transactional
	void removeRoleToGroup_groupInactive_thenThrow() {

		String groupId = "inactive";

		String roleExistedId = "admin";

		String modifiedBy = "admin";

		assertThrows(UserManagementBusinessException.class,
				() -> groupService.removeUserFromGroup(groupId, roleExistedId, modifiedBy));
	}

	@Test
	@Transactional
	void removeRoleToGroup_modifyUserNotFound_thenThrow() {

		String groupId = "admin";

		String roleExistedId = "admin";

		String modifiedBy = "not found";

		assertThrows(UserManagementBusinessException.class,
				() -> groupService.removeUserFromGroup(groupId, roleExistedId, modifiedBy));
	}

	@Test
	@Transactional
	void removeRoleFromGroup_roleNotFound_thenThrow() {

		String groupId = "admin";

		String roleNotExistedYet = "not_found";

		String modifiedBy = "admin";

		assertThrows(UserManagementBusinessException.class,
				() -> groupService.removeRoleFromGroup(groupId, roleNotExistedYet, modifiedBy));
	}

	@Test
	@Transactional
	void removeRoleToGroup_removeSuccessfully() {
		// Admin group Id
		String groupId = "adf5a395-3359-4fc5-a5a9-c9036206bb29";

		// Admin role Id
		String roleId = "rladmin3-d1c0-11ec-81c0-38f3ab0673e4";

		String roleExisted = "Administrator";

		String modifiedBy = "VOU6HC";

		groupService.removeRoleFromGroup(groupId, roleId, modifiedBy);

		assertThat(groupRepository.findById(groupId).get().getGroupRoles().stream()
				.noneMatch(roleGroup -> roleGroup.getRole().getName().equals(roleExisted))).isTrue();
		assertNull(groupRoleRepository.findByGroupIdAndRoleId(groupId, roleId).orElse(null));
	}
}
