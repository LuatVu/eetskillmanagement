package com.bosch.eet.skill.management.facade;

import java.util.List;

import com.bosch.eet.skill.management.dto.request.group.AddUsersToGroupDTO;
import com.bosch.eet.skill.management.dto.response.AddUserToGroupResponseDTO;
import com.bosch.eet.skill.management.dto.response.GroupResponseDTO;
import com.bosch.eet.skill.management.usermanagement.dto.GroupDTO;

public interface GroupFacade {
    List<GroupResponseDTO> getAllGroups();

    GroupResponseDTO createGroup(GroupDTO groupDetails, String toString);

    GroupResponseDTO getGroupDetailsById(String id);

    GroupResponseDTO updateGroup(GroupDTO groupDetails, String toString);

    GroupResponseDTO deleteGroup(String groupId, String toString);

    AddUserToGroupResponseDTO addUsersToGroup(AddUsersToGroupDTO requestBody, String toString);

    GroupResponseDTO removeUserFromGroup(String groupId, String userId, String toString);

    GroupResponseDTO addRolesToGroup(String groupId, List<String> roles, String toString);

    GroupResponseDTO removeRoleFromGroup(String groupId, String roleId, String toString);
}
