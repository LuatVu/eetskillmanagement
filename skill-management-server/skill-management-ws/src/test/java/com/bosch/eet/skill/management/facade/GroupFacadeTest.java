package com.bosch.eet.skill.management.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;

import com.bosch.eet.skill.management.common.Status;
import com.bosch.eet.skill.management.converter.utils.LDAPRequestConverterUtil;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.request.LDAPDistributionListRequest;
import com.bosch.eet.skill.management.dto.request.LDAPUserRequest;
import com.bosch.eet.skill.management.dto.request.group.AddUsersToGroupDTO;
import com.bosch.eet.skill.management.dto.response.GroupResponseDTO;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.impl.GroupFacadeImpl;
import com.bosch.eet.skill.management.ldap.exception.LdapException;
import com.bosch.eet.skill.management.ldap.model.LdapInfo;
import com.bosch.eet.skill.management.ldap.service.LdapService;
import com.bosch.eet.skill.management.service.PersonalService;
import com.bosch.eet.skill.management.usermanagement.dto.GroupDTO;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Group;
import com.bosch.eet.skill.management.usermanagement.entity.PermissionCategory;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementBusinessException;
import com.bosch.eet.skill.management.usermanagement.service.GroupService;
import com.bosch.eet.skill.management.usermanagement.service.PermissionCategoryService;
import com.bosch.eet.skill.management.usermanagement.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DataJpaTest
@ActiveProfiles("dev")
public class GroupFacadeTest {
    @InjectMocks
    GroupFacadeImpl groupFacade;

    @Mock
    PermissionCategoryService permissionCategoryService;

    @Mock
    GroupService groupService;

    @Mock
    UserService userService;

    @Mock
    LDAPRequestConverterUtil ldapRequestConverter;

    @Mock
    LdapService ldapService;

    @Mock
    MessageSource messageSource;
    @Mock
    PersonalService personalService;

    private final Group group = Group.builder()
            .id("test")
            .name("test")
            .status("Active")
            .build();

    private final PermissionCategory permissionCategory = PermissionCategory.builder()
            .id("test")
            .code("test")
            .build();

    @Test
    @DisplayName("get all group")
    public void testGetAllGroups() {

        List<PermissionCategory> permissionCategories = Collections.singletonList(
                permissionCategory
        );

        List<Group> groups = Collections.singletonList(
                group
        );

        when(
                permissionCategoryService.findAllPermissionCategories()
        ).thenReturn(permissionCategories);

        when(
                groupService.getAllGroup()
        ).thenReturn(groups);

        List<GroupResponseDTO> allGroups = groupFacade.getAllGroups();

        assertNotNull(allGroups);
        assertThat(allGroups).hasSize(1);
        assertThat(allGroups.get(0).getGroupId()).isEqualTo("test");
    }

    @Test
    @DisplayName("create group")
    public void createGroup_whenStatusNull_returnActive() {

        List<PermissionCategory> permissionCategories = Collections.singletonList(
                permissionCategory
        );

        GroupDTO groupDTO = GroupDTO.builder()
                .groupId("test")
                .groupName("test")
                .build();

        when(
                permissionCategoryService.findAllPermissionCategories()
        ).thenReturn(permissionCategories);

        when(
                groupService.createGroup(groupDTO, "systest")
        ).thenReturn(
                group
        );

        GroupResponseDTO response = groupFacade.createGroup(groupDTO, "systest");
        assertThat(groupDTO.getStatus()).isEqualTo("Active");       
        assertThat(response.getGroupId()).isEqualTo("test");       
    }

    @Test
    @DisplayName("update group")
    public void updateGroup() {
        List<PermissionCategory> permissionCategories = Collections.singletonList(
                permissionCategory
        );

        GroupDTO groupDTO = GroupDTO.builder()
                .groupId("test")
                .groupName("test")
                .build();

        when(
                permissionCategoryService.findAllPermissionCategories()
        ).thenReturn(permissionCategories);

        when(
                groupService.updateGroup(groupDTO, "systest")
        ).thenReturn(
                group
        );

        assertThat(groupFacade.updateGroup(groupDTO, "systest").getGroupId()).isEqualTo("test");       
    }

    @Test
    @DisplayName("get group detail")
    public void getGroupDetailById() {
        List<PermissionCategory> permissionCategories = Collections.singletonList(
                permissionCategory
        );

        when(
                permissionCategoryService.findAllPermissionCategories()
        ).thenReturn(permissionCategories);

        when(
                groupService.findGroupById("test")
        ).thenReturn(
                group
        );

        GroupResponseDTO resposne = groupFacade.getGroupDetailsById("test");
        assertThat(resposne.getGroupId()).isEqualTo("test");       
    }

    @Test
    @DisplayName("delete group")
    public void deleteGroup() {

        List<PermissionCategory> permissionCategories = Collections.singletonList(
                permissionCategory
        );

        when(
                permissionCategoryService.findAllPermissionCategories()
        ).thenReturn(permissionCategories);

        when(
                groupService.deleteGroup("test", "test")
        ).thenReturn(
                group
        );
        assertThat(groupFacade.deleteGroup("test", "test").getGroupId()).isEqualTo("test");
    }

    @Test
    @DisplayName("remove user from group")
    public void removeUserFromGroup() {

        doNothing().when(groupService).removeUserFromGroup("test", "test", "systest");

        List<PermissionCategory> permissionCategories = Collections.singletonList(
                permissionCategory
        );

        when(
                permissionCategoryService.findAllPermissionCategories()
        ).thenReturn(permissionCategories);

        when(
                groupService.findGroupById("test")
        ).thenReturn(
                group
        );
        assertThat( groupFacade.removeUserFromGroup("test", "test", "systest").getGroupName()).isEqualTo( "test");  
    }

    @Test
    @DisplayName("add role to group")
    public void addRoleToGroup() {

        List<PermissionCategory> permissionCategories = Collections.singletonList(
                permissionCategory
        );

        when(
                permissionCategoryService.findAllPermissionCategories()
        ).thenReturn(permissionCategories);

        when(
                groupService.addRolesToGroup(
                        "test",
                        Collections.singletonList("test"),
                        "systest"
                )
        ).thenReturn(
                group
        );
        assertThat( groupFacade.addRolesToGroup("test",Collections.singletonList("test"),"systest").getGroupName()).isEqualTo("test");
    }

    @Test
    @DisplayName("remove role from group")
    public void removeRoleFromGroup() {

        List<PermissionCategory> permissionCategories = Collections.singletonList(
                permissionCategory
        );

        when(
                permissionCategoryService.findAllPermissionCategories()
        ).thenReturn(permissionCategories);

        when(
                groupService.findGroupById("test")
        ).thenReturn(
                group
        );
        assertThat(groupFacade.removeRoleFromGroup("test","test", "systest").getGroupName()).isEqualTo("test"); 
    }

    @Test
    @DisplayName("add user to group test happy case")
    public void addUserToGroup_happyCase() throws LdapException {

        LDAPUserRequest ldapUserRequest = new LDAPUserRequest();
        ldapUserRequest.setSAmAccountName("userLdapId");

        LDAPDistributionListRequest ldapDistributionListRequest = new LDAPDistributionListRequest();
        ldapDistributionListRequest.setDisplayName("groupLdapId");

        AddUsersToGroupDTO addUsersToGroupDTO = AddUsersToGroupDTO.builder()
                .groupId("test")
                .users(
                        new ArrayList<>(Collections.singletonList("userId"))
                )
                .ldapUsers(
                        new ArrayList<>(Collections.singletonList(ldapUserRequest))
                )
                .distributionLists(
                        new ArrayList<>(Collections.singletonList(ldapDistributionListRequest))
                )
                .build();

        when(userService.findByIdIn(addUsersToGroupDTO.getUsers()))
                .thenReturn(
                        new ArrayList<>(Collections.singletonList(
                                UserDTO.builder()
                                        .id("userId")
                                        .build()
                        ))
                );

        //addLdapUsers
        LdapInfo ldapInfo = new LdapInfo();
        ldapInfo.setName("ldapInfoName");
        when(ldapService.getPrincipalInfo("ldapUserName"))
                .thenReturn(
                        Optional.of(ldapInfo)
                );

        //addLdapGroups
        when(ldapService.getGroupInfo("ldapGroupName")).thenReturn(
                Optional.of(ldapInfo)
        );

        //when group and user are found in DB and active
        when(userService.findByNTId(ldapInfo.getName()))
                .thenReturn(
                        User.builder()
                                .name(ldapInfo.getName())
                                .status(Status.ACTIVE.getLabel())
                                .build()
                );

//        when(userService.updateUser(any(User.class))).thenReturn(
//                User.builder().build()
//        );

        List<PermissionCategory> permissionCategories = Collections.singletonList(
                permissionCategory
        );

        when(
                permissionCategoryService.findAllPermissionCategories()
        ).thenReturn(permissionCategories);

        when(
                groupService.addUsersToGroup(
                        anyString(), any(), anyString()
                )
        ).thenReturn(
                group
        );
        assertThat(groupFacade.addUsersToGroup(addUsersToGroupDTO, "systest").getGroupResponseDTO().getGroupName()).isEqualTo("test"); 
    }

    @Test
    @DisplayName("add user to group not found ldapuser")
    public void addUserToGroup_notFoundLdapUser_thenThrow() throws LdapException {

        LDAPUserRequest ldapUserRequest = new LDAPUserRequest();
        ldapUserRequest.setSAmAccountName("userLdapId");

        LDAPDistributionListRequest ldapDistributionListRequest = new LDAPDistributionListRequest();
        ldapDistributionListRequest.setDisplayName("groupLdapId");

        AddUsersToGroupDTO addUsersToGroupDTO = AddUsersToGroupDTO.builder()
                .groupId("test")
                .users(
                        Collections.singletonList("userId")
                )
                .ldapUsers(
                        Collections.singletonList(ldapUserRequest)
                )
                .distributionLists(
                        Collections.singletonList(ldapDistributionListRequest)
                )
                .build();

        when(userService.findByIdIn(addUsersToGroupDTO.getUsers()))
                .thenReturn(
                        Collections.singletonList(
                                UserDTO.builder()
                                        .id("userId")
                                        .build()
                        )
                );


        //addLdapUsers
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("test");
        LdapInfo ldapInfo = new LdapInfo();
        ldapInfo.setName("ldapInfoName");
        when(ldapService.getPrincipalInfo("ldapUserName"))
                .thenReturn(
                        Optional.empty()
                );

        assertThrows(
                SkillManagementException.class,
                () -> groupFacade.addUsersToGroup(addUsersToGroupDTO, "systest")
        );
    }

    @Test
    @DisplayName("add user to group not found ldapuser")
    public void addUserToGroup_ldapThrowException_thenThrow() throws LdapException {

        LDAPUserRequest ldapUserRequest = new LDAPUserRequest();
        ldapUserRequest.setSAmAccountName("userLdapId");

        LDAPDistributionListRequest ldapDistributionListRequest = new LDAPDistributionListRequest();
        ldapDistributionListRequest.setDisplayName("groupLdapId");

        AddUsersToGroupDTO addUsersToGroupDTO = AddUsersToGroupDTO.builder()
                .groupId("test")
                .users(
                        Collections.singletonList("userId")
                )
                .ldapUsers(
                        Collections.singletonList(ldapUserRequest)
                )
                .distributionLists(
                        Collections.singletonList(ldapDistributionListRequest)
                )
                .build();

        when(userService.findByIdIn(addUsersToGroupDTO.getUsers()))
                .thenReturn(
                        Collections.singletonList(
                                UserDTO.builder()
                                        .id("userId")
                                        .build()
                        )
                );


        //addLdapUsers
        LdapInfo ldapInfo = new LdapInfo();
        ldapInfo.setName("ldapInfoName");
        when(ldapService.getPrincipalInfo("ldapUserName"))
                .thenThrow(
                        LdapException.class
                );

        assertThrows(
                RuntimeException.class,
                () -> groupFacade.addUsersToGroup(addUsersToGroupDTO, "systest")
        );
    }

    @Test
    @DisplayName("add user to group not found ldapuser")
    public void addUserToGroup_userFoundIsInactive_thenActivateUser() throws LdapException {

        LDAPUserRequest ldapUserRequest = new LDAPUserRequest();
        ldapUserRequest.setSAmAccountName("userLdapId");

        LDAPDistributionListRequest ldapDistributionListRequest = new LDAPDistributionListRequest();
        ldapDistributionListRequest.setDisplayName("groupLdapId");

        AddUsersToGroupDTO addUsersToGroupDTO = AddUsersToGroupDTO.builder()
                .groupId("test")
                .users(
                        new ArrayList<>(Collections.singletonList("userId"))
                )
                .ldapUsers(
                        new ArrayList<>(Collections.singletonList(ldapUserRequest))
                )
                .distributionLists(
                        new ArrayList<>(Collections.singletonList(ldapDistributionListRequest))
                )
                .build();

        when(userService.findByIdIn(addUsersToGroupDTO.getUsers()))
                .thenReturn(
                        new ArrayList<>(
                                Collections.singletonList(
                                        UserDTO.builder()
                                                .id("userId")
                                                .build()
                                )
                        )
                );


        //addLdapUsers
        LdapInfo ldapInfo = new LdapInfo();
        ldapInfo.setName("ldapInfoName");
        when(ldapService.getPrincipalInfo("ldapUserName"))
                .thenReturn(
                        Optional.of(ldapInfo)
                );

        when(userService.findByNTId(ldapInfo.getName()))
                .thenReturn(
                        User.builder()
                                .name(ldapInfo.getName())
                                .status(Status.INACTIVE.getLabel())
                                .build()
                );

        when(userService.updateUser(any(User.class))).thenReturn(
                User.builder().build()
        );

        //groups
        when(ldapService.getGroupInfo("ldapGroupName"))
                .thenReturn(
                        Optional.of(ldapInfo)
                );

        List<PermissionCategory> permissionCategories = Collections.singletonList(
                permissionCategory
        );

        when(
                permissionCategoryService.findAllPermissionCategories()
        ).thenReturn(permissionCategories);

        when(
                groupService.addUsersToGroup(
                        anyString(), any(), anyString()
                )
        ).thenReturn(
                group
        );
        assertThat(groupFacade.addUsersToGroup(addUsersToGroupDTO, "systest").getGroupResponseDTO().getGroupName()).isEqualTo("test");   
    }

    @Test
    @DisplayName("add user to group not found ldapuser")
    public void addUserToGroup_userNotFound_thenAddUser() throws LdapException {

        LDAPUserRequest ldapUserRequest = new LDAPUserRequest();
        ldapUserRequest.setSAmAccountName("userLdapId");

        LDAPDistributionListRequest ldapDistributionListRequest = new LDAPDistributionListRequest();
        ldapDistributionListRequest.setDisplayName("groupLdapId");

        AddUsersToGroupDTO addUsersToGroupDTO = AddUsersToGroupDTO.builder()
                .groupId("test")
                .users(
                        new ArrayList<>(Collections.singletonList("userId"))
                )
                .ldapUsers(
                        new ArrayList<>(Collections.singletonList(ldapUserRequest))
                )
                .distributionLists(
                        new ArrayList<>(Collections.singletonList(ldapDistributionListRequest))
                )
                .build();

        when(userService.findByIdIn(addUsersToGroupDTO.getUsers()))
                .thenReturn(
                        new ArrayList<>(
                                Collections.singletonList(
                                        UserDTO.builder()
                                                .id("userId")
                                                .build()
                                )
                        )
                );


        //addLdapUsers
        LdapInfo ldapInfo = new LdapInfo();
        ldapInfo.setName("ldapInfoName");
        when(ldapService.getPrincipalInfo("ldapUserName"))
                .thenReturn(
                        Optional.of(ldapInfo)
                );

        when(userService.findByNTId(ldapInfo.getName()))
                .thenThrow(
                        UserManagementBusinessException.class
                );

        when(userService.addUser(any(UserDTO.class))).thenReturn(
                User.builder().build()
        );

        doNothing().when(personalService).savePersonalWithUser(any(PersonalDto.class));

        //groups
        when(ldapService.getGroupInfo("ldapGroupName"))
                .thenReturn(
                        Optional.of(ldapInfo)
                );

        List<PermissionCategory> permissionCategories = Collections.singletonList(
                permissionCategory
        );

        when(
                permissionCategoryService.findAllPermissionCategories()
        ).thenReturn(permissionCategories);

        when(
                groupService.addUsersToGroup(
                        anyString(), any(), anyString()
                )
        ).thenReturn(
                group
        );
        assertThat(groupFacade.addUsersToGroup(addUsersToGroupDTO, "systest").getGroupResponseDTO().getGroupName()).isEqualTo("test");   
    }

    @Test
    @DisplayName("add user to group not found ldapuser")
    public void addUserToGroup_groupNotFound_thenThrow() throws LdapException {

        LDAPUserRequest ldapUserRequest = new LDAPUserRequest();
        ldapUserRequest.setSAmAccountName("userLdapId");

        LDAPDistributionListRequest ldapDistributionListRequest = new LDAPDistributionListRequest();
        ldapDistributionListRequest.setDisplayName("groupLdapId");

        AddUsersToGroupDTO addUsersToGroupDTO = AddUsersToGroupDTO.builder()
                .groupId("test")
                .users(
                        Collections.singletonList("userId")
                )
                .ldapUsers(
                        Collections.singletonList(ldapUserRequest)
                )
                .distributionLists(
                        Collections.singletonList(ldapDistributionListRequest)
                )
                .build();

        when(userService.findByIdIn(addUsersToGroupDTO.getUsers()))
                .thenReturn(
                        Collections.singletonList(
                                UserDTO.builder()
                                        .id("userId")
                                        .build()
                        )
                );

        //addLdapUsers
        LdapInfo ldapInfo = new LdapInfo();
        ldapInfo.setName("ldapInfoName");
        when(ldapService.getPrincipalInfo("ldapUserName"))
                .thenReturn(
                        Optional.of(ldapInfo)
                );

        when(userService.findByNTId(ldapInfo.getName()))
                .thenThrow(
                        UserManagementBusinessException.class
                );

        when(userService.addUser(any(UserDTO.class))).thenReturn(
                User.builder().build()
        );

        doNothing().when(personalService).savePersonalWithUser(any(PersonalDto.class));

        //addLdapGroup
        when(ldapService.getGroupInfo("ldapGroupName")).thenReturn(
                Optional.empty()
        );


        assertThrows(
                SkillManagementException.class,
                () -> groupFacade.addUsersToGroup(addUsersToGroupDTO, "systest")
        );
    }
    
    @Test
    @DisplayName("Create group (Access to database)")
    void createGroup_AccessToDatabase() {
    	List<PermissionCategory> permissionCategories = Collections.singletonList(
                permissionCategory
        );

        GroupDTO groupDTO = GroupDTO.builder()
                .groupId("test")
                .groupName("test")
                .build();

        when(
                permissionCategoryService.findAllPermissionCategories()
        ).thenReturn(permissionCategories);

        GroupResponseDTO response = groupFacade.createGroup(groupDTO, "systest");
        assertThat(response).isEqualTo(response);
    }
}
