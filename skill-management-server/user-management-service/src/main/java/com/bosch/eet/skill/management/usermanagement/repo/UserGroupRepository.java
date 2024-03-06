package com.bosch.eet.skill.management.usermanagement.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bosch.eet.skill.management.usermanagement.entity.Group;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.entity.UserGroup;

public interface UserGroupRepository extends JpaRepository<UserGroup, String> {
    void deleteByUserAndGroup(User user, Group group);

    void deleteByGroupId(String groupId);

    Optional<UserGroup> findByGroupIdAndUserId(String groupId, String userId);
    
    List<UserGroup> findByGroupId(String groupId);
}