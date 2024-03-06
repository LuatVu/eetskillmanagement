package com.bosch.eet.skill.management.rest;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.request.group.AddGroupDTO;
import com.bosch.eet.skill.management.dto.request.group.AddRolesToGroupDTO;
import com.bosch.eet.skill.management.dto.request.group.AddUsersToGroupDTO;
import com.bosch.eet.skill.management.dto.request.group.DeleteGroupDTO;
import com.bosch.eet.skill.management.dto.request.group.RemoveRoleFromGroupDTO;
import com.bosch.eet.skill.management.dto.request.group.RemoveUserFromGroupDTO;
import com.bosch.eet.skill.management.dto.request.group.UpdateGroupDTO;
import com.bosch.eet.skill.management.dto.response.AddUserToGroupResponseDTO;
import com.bosch.eet.skill.management.dto.response.GroupResponseDTO;
import com.bosch.eet.skill.management.dto.response.UserResponseDTO;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.GroupFacade;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementBusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("test")
public class GroupRestTest {

    public static final String baseGroupUrl = Routes.URI_REST_GROUP;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupFacade groupFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", authorities = {"VIEW_USER_MANAGEMENT"})
    public void getAllGroup_returnAGroupList() throws Exception {

        List<GroupResponseDTO> groupDTOS = new ArrayList<>();
        GroupResponseDTO groupResponseDTO = GroupResponseDTO.builder()
                .groupId("test")
                .build();

        groupDTOS.add(groupResponseDTO);

        when(groupFacade.getAllGroups()).thenReturn(groupDTOS);

        mockMvc.perform(get(Routes.URI_REST_GROUP))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(MessageCode.SUCCESS.toString())))
                .andExpect(jsonPath("$.data[0].groupId", is("test")));
    }
    
    @Test
    @DisplayName("Get all group - Access denied")
    @WithMockUser(username = "admin", authorities = {"VIEW_ROLE", "VIEW_USER"})
    void getAllGroup_AcessDenied() throws Exception {
        mockMvc.perform(get(Routes.URI_REST_GROUP))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("access_denied")))
                .andExpect(jsonPath("$.error_description", is("Access is denied")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void createGroup_whenCreateSuccessfully_returnSavedGroup() throws Exception {

        AddGroupDTO addGroupDTO = AddGroupDTO.builder()
                .groupName("test")
                .build();

        GroupResponseDTO groupResponseDTO = GroupResponseDTO.builder()
                .groupId("test")
                .build();

        when(groupFacade.createGroup(
                any(), anyString()
        )).thenReturn(groupResponseDTO);

        mockMvc.perform(
                        post(Routes.URI_REST_GROUP)
                                .content(objectMapper.writeValueAsString(addGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(MessageCode.SUCCESS.toString())))
                .andExpect(jsonPath("$.data.groupId", is("test")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void createGroup_whenCreateGroupThrows_return500() throws Exception {

        AddGroupDTO addGroupDTO = AddGroupDTO.builder()
                .groupName("test")
                .build();

        when(groupFacade.createGroup(
                any(), anyString()
        )).thenThrow(new UserManagementBusinessException(
                "test",
                "test"
        ));

        mockMvc.perform(
                        post(Routes.URI_REST_GROUP)
                                .content(objectMapper.writeValueAsString(addGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code", is("test")))
                .andExpect(jsonPath("$.message", is("test")));
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {})
    @DisplayName("Create group - Access denied")
    void createGroup_AccessDenied() throws Exception {
    	 AddGroupDTO addGroupDTO = AddGroupDTO.builder()
                 .groupName("test")
                 .build();
        mockMvc.perform(
                        post(Routes.URI_REST_GROUP)
                                .content(objectMapper.writeValueAsString(addGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("access_denied")))
                .andExpect(jsonPath("$.error_description", is("Access is denied")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"VIEW_USER_MANAGEMENT"})
    @DisplayName("Get group detail - Happycase")
    public void getGroupDetail_getGroupByIdSuccessfully_returnGroupDTO() throws Exception {

        GroupResponseDTO groupResponseDTO = GroupResponseDTO.builder()
                .groupId("test")
                .build();

        when(groupFacade.getGroupDetailsById(
                "test"
        )).thenReturn(groupResponseDTO);

        mockMvc.perform(
                        get(Routes.URI_REST_GROUP + "/{id}", "test")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(MessageCode.SUCCESS.toString())))
                .andExpect(jsonPath("$.data.groupId", is("test")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"VIEW_USER_MANAGEMENT"})
    @DisplayName("Get group detail - return 500")
    public void getGroupDetail_noGroupIdFound_return500() throws Exception {

        when(groupFacade.getGroupDetailsById(
                "test"
        )).thenThrow(new UserManagementBusinessException(
                "group not exist msg", "test"
        ));

        mockMvc.perform(
                        get(Routes.URI_REST_GROUP + "/{id}", "test")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code", is("group not exist msg")))
                .andExpect(jsonPath("$.message", is("test")));
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {})
    @DisplayName("Get group detail - Access denied")
    void getGroupDetail_AccessDenied() throws Exception {
        mockMvc.perform(
                        get(Routes.URI_REST_GROUP + "/{id}", "test")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("access_denied")))
                .andExpect(jsonPath("$.error_description", is("Access is denied")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void updateGroup_requestBodyNoName_returnBadRequest() throws Exception {

        UpdateGroupDTO updateGroupDTO = UpdateGroupDTO.builder()
                .build();

        mockMvc.perform(
                        post(Routes.URI_REST_GROUP + "/{id}", "test")
                                .content(objectMapper.writeValueAsString(updateGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(MessageCode.VALIDATION_ERRORS.toString())))
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void updateGroup_updateGroupFailed_returnException() throws Exception {

        UpdateGroupDTO updateGroupDTO = UpdateGroupDTO.builder()
                .groupName("test")
                .build();

        when(groupFacade.updateGroup(
                any(), anyString()
        )).thenThrow(new SkillManagementException(
                "test", "test", null
        ));

        mockMvc.perform(
                        post(baseGroupUrl + Routes.URI_REST_GROUP_UPDATE)
                                .content(objectMapper.writeValueAsString(updateGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code", is("test")))
                .andExpect(jsonPath("$.message", is("test")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void updateGroup_updateGroupSuccessful_returnDTO() throws Exception {

        UpdateGroupDTO updateGroupDTO = UpdateGroupDTO.builder()
                .groupName("test")
                .build();

        when(groupFacade.updateGroup(
                any(), anyString()
        )).thenReturn(
                GroupResponseDTO.builder()
                        .groupId("test")
                        .groupName("test")
                        .build()
        );

        mockMvc.perform(
                        post(baseGroupUrl + "/{id}", "test")
                                .content(objectMapper.writeValueAsString(updateGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(MessageCode.SUCCESS.toString())))
                .andExpect(jsonPath("$.data.groupId", is("test")));
    }
    
    @Test
    @DisplayName("Update group - Access denied")
    @WithMockUser(username = "admin", authorities = {})
    void updateGroup_AccessDenied() throws Exception {

        UpdateGroupDTO updateGroupDTO = UpdateGroupDTO.builder()
                .groupName("test")
                .build();

        mockMvc.perform(
                        post(baseGroupUrl + "/{id}", "test")
                                .content(objectMapper.writeValueAsString(updateGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("access_denied")))
                .andExpect(jsonPath("$.error_description", is("Access is denied")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void deleteGroup_deleteSuccessfully_returnGroupInactive() throws Exception {

        DeleteGroupDTO deleteGroupDTO = DeleteGroupDTO.builder().build();
        deleteGroupDTO.setGroupId("1");

        GroupResponseDTO groupResponseDTO = GroupResponseDTO.builder()
                .groupName("test")
                .build();

        when(groupFacade.deleteGroup(
                anyString(), anyString()
        )).thenReturn(
                groupResponseDTO
        );

        mockMvc.perform(
                        post(baseGroupUrl + "/{id}/delete", "test")
                                .content(objectMapper.writeValueAsString(deleteGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(MessageCode.SUCCESS.toString())))
                .andExpect(jsonPath("$.data.groupName", is("test")))
        ;
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void deleteGroup_deleteThrowException_return500() throws Exception {

        when(groupFacade.deleteGroup(
                anyString(), anyString()
        )).thenThrow(
                new UserManagementBusinessException("test", "test")
        );

        mockMvc.perform(
                        post(baseGroupUrl + "/{id}/delete", "test")
                )
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code", is("test")))
                .andExpect(jsonPath("$.message", is("test")))
        ;
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {})
    @DisplayName("Delete group - Acess denied")
    void deleteGroup_AcessDenied() throws Exception {
        mockMvc.perform(
                        post(baseGroupUrl + "/{id}/delete", "test")
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("access_denied")))
                .andExpect(jsonPath("$.error_description", is("Access is denied")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void addUserToGroup_noId_returnBadRequest() throws Exception {

        AddUsersToGroupDTO addUsersToGroupDTO = new AddUsersToGroupDTO();

        mockMvc.perform(
                        post(baseGroupUrl + Routes.URI_REST_GROUP_ADD_USERS)
                                .content(objectMapper.writeValueAsString(addUsersToGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(MessageCode.VALIDATION_ERRORS.toString())))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void addUserToGroup_addUserSuccessfully_returnGroupDTO() throws Exception {

        //prepare request body
        List<String> userIds = new ArrayList<>();
        userIds.add("123");

        AddUsersToGroupDTO addUsersToGroupDTO = new AddUsersToGroupDTO();
        addUsersToGroupDTO.setGroupId("1");
        addUsersToGroupDTO.setUsers(userIds);

        //prepare response body
        GroupResponseDTO groupResponseDTO = GroupResponseDTO.builder()
                .groupId("1")
                .users(
                        Collections.singletonList(
                                UserResponseDTO.builder()
                                        .id("123")
                                        .build())
                )
                .build();

        AddUserToGroupResponseDTO addUserToGroupResponseDTO = AddUserToGroupResponseDTO.builder()
                .groupResponseDTO(groupResponseDTO)
                .build();

        when(groupFacade.addUsersToGroup(
                any(), anyString()
        )).thenReturn(
                addUserToGroupResponseDTO
        );

        mockMvc.perform(
                        post(baseGroupUrl + Routes.URI_REST_GROUP_ADD_USERS + "/{id}", "test")
                                .content(objectMapper.writeValueAsString(addUsersToGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
        ;
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void addUserToGroup_addUserThrowException_return500() throws Exception {

        //prepare request body
        List<String> userIds = new ArrayList<>();
        userIds.add("123");

        AddUsersToGroupDTO addUsersToGroupDTO = new AddUsersToGroupDTO();
        addUsersToGroupDTO.setGroupId("1");
        addUsersToGroupDTO.setUsers(userIds);

        when(groupFacade.addUsersToGroup(
                any(), anyString()
        )).thenThrow(
                new UserManagementBusinessException(
                        "test", "test"
                )
        );

        mockMvc.perform(
                        post(baseGroupUrl + Routes.URI_REST_GROUP_ADD_USERS + "/{id}", "test")
                                .content(objectMapper.writeValueAsString(addUsersToGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code", is("test")))
                .andExpect(jsonPath("$.message", is("test")))
        ;
    }
    
	@Test
	@WithMockUser(username = "admin", authorities = {})
	@DisplayName("Add user to group - Access denied")
	void addUserToGroup_AccessDenied() throws Exception {
		// prepare request body
		List<String> userIds = new ArrayList<>();
		userIds.add("123");

		AddUsersToGroupDTO addUsersToGroupDTO = new AddUsersToGroupDTO();
		addUsersToGroupDTO.setGroupId("1");
		addUsersToGroupDTO.setUsers(userIds);

		mockMvc.perform(post(baseGroupUrl + Routes.URI_REST_GROUP_ADD_USERS + "/{id}", "test")
				.content(objectMapper.writeValueAsString(addUsersToGroupDTO))
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
				.andExpect(jsonPath("$.error", is("access_denied")))
				.andExpect(jsonPath("$.error_description", is("Access is denied")));
	}

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void removeUserFromGroup_noId_returnBadRequest() throws Exception {

        RemoveUserFromGroupDTO removeUserFromGroupDTO = new RemoveUserFromGroupDTO();

        mockMvc.perform(
                        post(baseGroupUrl + Routes.URI_REST_GROUP_ADD_USERS)
                                .content(objectMapper.writeValueAsString(removeUserFromGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(MessageCode.VALIDATION_ERRORS.toString())))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void removeUserFromGroup_noUserId_returnBadRequest() throws Exception {

        RemoveUserFromGroupDTO removeUserFromGroupDTO = new RemoveUserFromGroupDTO();
        removeUserFromGroupDTO.setGroupId("123");

        mockMvc.perform(
                        post(baseGroupUrl + Routes.URI_REST_GROUP_ADD_USERS)
                                .content(objectMapper.writeValueAsString(removeUserFromGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(MessageCode.VALIDATION_ERRORS.toString())))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void removeUserFromGroup_removeSuccessfully_returnGroupDTO() throws Exception {

        RemoveUserFromGroupDTO removeUserFromGroupDTO = new RemoveUserFromGroupDTO();
        removeUserFromGroupDTO.setUserId("123");
        removeUserFromGroupDTO.setGroupId("test");

        GroupResponseDTO groupResponseDTO = GroupResponseDTO.builder()
                .groupId("123")
                .build();

        when(groupFacade.removeUserFromGroup(
                anyString(), anyString(), anyString()
        )).thenReturn(
                groupResponseDTO
        );

        mockMvc.perform(
                        post(baseGroupUrl + Routes.URI_REST_GROUP_DEL_USERS + "/{id}", "test")
                                .content(objectMapper.writeValueAsString(removeUserFromGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(MessageCode.SUCCESS.toString())))
                .andExpect(jsonPath("$.data.groupId", is("123")))
        ;
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void removeUserFromGroup_removeThrowException_returnGroupDTO() throws Exception {

        RemoveUserFromGroupDTO removeUserFromGroupDTO = new RemoveUserFromGroupDTO();
        removeUserFromGroupDTO.setUserId("123");
        removeUserFromGroupDTO.setGroupId("test");

        when(groupFacade.removeUserFromGroup(
                anyString(), anyString(), anyString()
        )).thenThrow(
                new UserManagementBusinessException(
                        "test", "test"
                )
        );

        mockMvc.perform(
                        post(baseGroupUrl + Routes.URI_REST_GROUP_DEL_USERS + "/{id}", "test")
                                .content(objectMapper.writeValueAsString(removeUserFromGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code", is("test")))
                .andExpect(jsonPath("$.message", is("test")))
        ;
    }
    
	@Test
	@WithMockUser(username = "admin", authorities = {})
	@DisplayName("Remove user from group - Access denied")
	void removeUserFromGroup_AccessDenied() throws Exception {

		RemoveUserFromGroupDTO removeUserFromGroupDTO = new RemoveUserFromGroupDTO();
		removeUserFromGroupDTO.setUserId("123");
		removeUserFromGroupDTO.setGroupId("test");

		mockMvc.perform(post(baseGroupUrl + Routes.URI_REST_GROUP_DEL_USERS + "/{id}", "test")
				.content(objectMapper.writeValueAsString(removeUserFromGroupDTO))
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
				.andExpect(jsonPath("$.error", is("access_denied")))
				.andExpect(jsonPath("$.error_description", is("Access is denied")));
	}

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void addRolesToGroup_noRoles_returnBadRequest() throws Exception {

        AddRolesToGroupDTO addRolesToGroupDTO = new AddRolesToGroupDTO();
        addRolesToGroupDTO.setGroupId("123");

        mockMvc.perform(
                        post(baseGroupUrl + Routes.URI_REST_GROUP_ADD_USERS)
                                .content(objectMapper.writeValueAsString(addRolesToGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(MessageCode.VALIDATION_ERRORS.toString())))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void addRolesToGroup_addRolesSuccessfully_returnGroupDTO() throws Exception {

        List<String> roleIds = new ArrayList<>();
        roleIds.add("123");

        AddRolesToGroupDTO addRolesToGroupDTO = new AddRolesToGroupDTO();
        addRolesToGroupDTO.setGroupId("123");
        addRolesToGroupDTO.setRoles(roleIds);

        GroupResponseDTO groupResponseDTO = GroupResponseDTO.builder()
                .groupId("test")
                .build();

        when(groupFacade.addRolesToGroup(
                anyString(), anyList(), anyString()
        )).thenReturn(
                groupResponseDTO
        );

        mockMvc.perform(
                        post(baseGroupUrl + Routes.URI_REST_GROUP_ADD_ROLES + "/{id}", "test")
                                .content(objectMapper.writeValueAsString(addRolesToGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(MessageCode.SUCCESS.toString())))
                .andExpect(jsonPath("$.data.groupId", is("test")))
        ;
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void addRolesToGroup_addRolesThrowException_returnException() throws Exception {

        List<String> roleIds = new ArrayList<>();
        roleIds.add("123");

        AddRolesToGroupDTO addRolesToGroupDTO = new AddRolesToGroupDTO();
        addRolesToGroupDTO.setGroupId("123");
        addRolesToGroupDTO.setRoles(roleIds);

        when(groupFacade.addRolesToGroup(
                anyString(), anyList(), anyString()
        )).thenThrow(
                new UserManagementBusinessException(
                        "test", "test"
                )
        );

        mockMvc.perform(
                        post(baseGroupUrl + Routes.URI_REST_GROUP_ADD_ROLES + "/{id}", "test")
                                .content(objectMapper.writeValueAsString(addRolesToGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code", is("test")))
                .andExpect(jsonPath("$.message", is("test")))
        ;
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void removeRolesFromGroup_noGroupID_returnBadRequest() throws Exception {

        RemoveRoleFromGroupDTO removeRoleFromGroupDTO = new RemoveRoleFromGroupDTO();

        mockMvc.perform(
                        post(baseGroupUrl + Routes.URI_REST_GROUP_ADD_USERS)
                                .content(objectMapper.writeValueAsString(removeRoleFromGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(MessageCode.VALIDATION_ERRORS.toString())))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void removeRolesFromGroup_noRole_returnBadRequest() throws Exception {

        RemoveRoleFromGroupDTO removeRoleFromGroupDTO = new RemoveRoleFromGroupDTO();
        removeRoleFromGroupDTO.setGroupId("123");

        mockMvc.perform(
                        post(baseGroupUrl + Routes.URI_REST_GROUP_ADD_USERS)
                                .content(objectMapper.writeValueAsString(removeRoleFromGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(MessageCode.VALIDATION_ERRORS.toString())))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void removeRolesFromGroup_removeRoleSuccessfully_returnGroupDTO() throws Exception {

        RemoveRoleFromGroupDTO removeRoleFromGroupDTO = new RemoveRoleFromGroupDTO();
        removeRoleFromGroupDTO.setGroupId("123");
        removeRoleFromGroupDTO.setRoleId("123");

        GroupResponseDTO groupResponseDTO = GroupResponseDTO.builder()
                .groupId("test")
                .build();

        when(groupFacade.removeRoleFromGroup(
                anyString(), anyString(), anyString()
        )).thenReturn(
                groupResponseDTO
        );

        mockMvc.perform(
                        post(baseGroupUrl + Routes.URI_REST_GROUP_DEL_ROLES + "/{id}", "test")
                                .content(objectMapper.writeValueAsString(removeRoleFromGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(MessageCode.SUCCESS.toString())))
                .andExpect(jsonPath("$.data.groupId", is("test")))
        ;
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_GROUP"})
    public void removeRolesFromGroup_removeRoleThrowException_returnException() throws Exception {

        RemoveRoleFromGroupDTO removeRoleFromGroupDTO = new RemoveRoleFromGroupDTO();
        removeRoleFromGroupDTO.setGroupId("123");
        removeRoleFromGroupDTO.setRoleId("123");

        when(groupFacade.removeRoleFromGroup(
                anyString(), anyString(), anyString()
        )).thenThrow(
                new UserManagementBusinessException(
                        "test", "test"
                )
        );

        mockMvc.perform(
                        post(baseGroupUrl + Routes.URI_REST_GROUP_DEL_ROLES + "/{id}", "test")
                                .content(objectMapper.writeValueAsString(removeRoleFromGroupDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code", is("test")))
                .andExpect(jsonPath("$.message", is("test")))
        ;
    }
    
	@Test
	@WithMockUser(username = "admin", authorities = {})
	@DisplayName("Remove role from group - Access denied")
	void removeRolesFromGroup_AccessDenied() throws Exception {
		RemoveRoleFromGroupDTO removeRoleFromGroupDTO = new RemoveRoleFromGroupDTO();
		removeRoleFromGroupDTO.setGroupId("123");
		removeRoleFromGroupDTO.setRoleId("123");
		mockMvc.perform(post(baseGroupUrl + Routes.URI_REST_GROUP_DEL_ROLES + "/{id}", "test")
				.content(objectMapper.writeValueAsString(removeRoleFromGroupDTO))
				.contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden())
				.andExpect(jsonPath("$.error", is("access_denied")))
				.andExpect(jsonPath("$.error_description", is("Access is denied")));
	}
}
