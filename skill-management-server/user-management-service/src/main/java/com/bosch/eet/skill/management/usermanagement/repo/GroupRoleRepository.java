package com.bosch.eet.skill.management.usermanagement.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.bosch.eet.skill.management.usermanagement.entity.Group;
import com.bosch.eet.skill.management.usermanagement.entity.GroupRole;
import com.bosch.eet.skill.management.usermanagement.entity.Role;

public interface GroupRoleRepository extends JpaRepository<GroupRole, String> {

    @Modifying
    void deleteByGroupAndRole(Group group, Role role);

    void deleteGroupRoleByGroupAndRole(Group group, Role role);

    Optional<GroupRole> findByGroupIdAndRoleId(String groupId, String roleId);

    void deleteByRoleId(String roleId);

    void deleteByGroupId(String groupId);
}