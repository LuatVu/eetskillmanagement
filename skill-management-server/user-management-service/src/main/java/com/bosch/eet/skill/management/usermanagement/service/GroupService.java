package com.bosch.eet.skill.management.usermanagement.service;

import java.util.List;

import com.bosch.eet.skill.management.usermanagement.dto.GroupDTO;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Group;

public interface GroupService {

    List<Group> getAllGroup();

    Group createGroup(GroupDTO groupDetails, String modifiedByUser);

    Group findGroupById(String id);

    List<Group> findGroupByStartPrefix(String prefix);

    Group updateGroup(GroupDTO groupDTO, String modifiedByUser);

    Group deleteGroup(String groupId, String modifiedByUser);

    Group addUsersToGroup(String groupId, List<UserDTO> userDTOs, String modifiedByUser);

    void removeUserFromGroup(String groupId, String userId, String modifiedByUser);

    Group addRolesToGroup(String groupId, List<String> roles, String modifiedByUser);

    void removeRoleFromGroup(String groupId, String roleId, String modifiedByUser);

}
