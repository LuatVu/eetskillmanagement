package com.bosch.eet.skill.management.usermanagement.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.common.Status;
import com.bosch.eet.skill.management.usermanagement.consts.MessageCode;
import com.bosch.eet.skill.management.usermanagement.dto.GroupDTO;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Group;
import com.bosch.eet.skill.management.usermanagement.entity.GroupRole;
import com.bosch.eet.skill.management.usermanagement.entity.Role;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.entity.UserGroup;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementBusinessException;
import com.bosch.eet.skill.management.usermanagement.repo.GroupRepository;
import com.bosch.eet.skill.management.usermanagement.repo.GroupRoleRepository;
import com.bosch.eet.skill.management.usermanagement.repo.RoleRepository;
import com.bosch.eet.skill.management.usermanagement.repo.UserGroupRepository;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;
import com.bosch.eet.skill.management.usermanagement.service.GroupService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GroupRoleRepository groupRoleRepository;

    @Autowired
    @Qualifier("CoreUserNamagementMessageSource")
    private MessageSource messageSource;

    @Override
    public List<Group> getAllGroup() {
        //Only find active group
        return groupRepository.findAllByStatus(Status.ACTIVE.getLabel());
    }

    @Override
    public Group findGroupById(String id) {
        return groupRepository.findByIdAndStatus(id, Status.ACTIVE.getLabel()).orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.SKM_GROUP_NOT_EXIST_MSG.toString(),
                        messageSource.getMessage(
                                MessageCode.SKM_GROUP_NOT_EXIST_MSG.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        ),
                        null
                )
        );
    }

    @Override
    public List<Group> findGroupByStartPrefix(String prefix) {
        return groupRepository.findByNameStartingWith(prefix);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Group createGroup(GroupDTO groupDTO, String modifiedByUser) {

        //Check modifying user
        if (!userRepository.existsByName(modifiedByUser)) {
            throw new UserManagementBusinessException(
                    MessageCode.USER_NOT_FOUND_MSG.toString(),
                    messageSource.getMessage(
                            MessageCode.USER_NOT_FOUND_MSG.toString(),
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    null
            );
        }
        
        groupDTO.setGroupName(groupDTO.getGroupName().trim());
        if(groupDTO.getDescription() != null) {
        	groupDTO.setDescription(groupDTO.getDescription().trim());
        }
        

        //Check group name exist
        Optional<Group> groupByName = groupRepository.findByName(groupDTO.getGroupName());
        if (groupByName.isPresent()) {

            Group group = groupByName.get();

            if (Status.INACTIVE.getLabel().equals(group.getStatus())) {
                groupDTO.setGroupId(group.getId());
                groupDTO.setStatus(Status.ACTIVE.getLabel());
                return updateGroup(groupDTO, modifiedByUser);
            }

            throw new UserManagementBusinessException(
                    MessageCode.SKM_GROUP_ALREADY_EXIST.toString(),
                    messageSource.getMessage(
                            MessageCode.SKM_GROUP_ALREADY_EXIST.toString(),
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    null
            );
        }

        //create entity to save
        Group createGroup = Group.builder()
                .name(groupDTO.getGroupName())
                .description(groupDTO.getDescription())
                .status(groupDTO.getStatus())
                .displayName(groupDTO.getDisplayName())
                .createdBy(modifiedByUser)
                .createdDate(LocalDateTime.now())
                .modifiedBy(modifiedByUser)
                .modifiedDate(LocalDateTime.now())
                .build();

        return groupRepository.save(createGroup);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Group updateGroup(GroupDTO groupDTO, String modifiedByUser) {
        //Check modifying user
        if (!userRepository.existsByName(modifiedByUser)) {
            throw new UserManagementBusinessException(
                    MessageCode.USER_NOT_FOUND_MSG.toString(),
                    messageSource.getMessage(
                            MessageCode.USER_NOT_FOUND_MSG.toString(),
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    null
            );
        }

        //Check group exist
        Group existingGroup = groupRepository.findById(groupDTO.getGroupId()).orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.SKM_GROUP_NOT_EXIST_MSG.toString(),
                        messageSource.getMessage(
                                MessageCode.SKM_GROUP_NOT_EXIST_MSG.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        ),
                        null
                )
        );
        
        groupDTO.setGroupName(groupDTO.getGroupName().trim());
        groupDTO.setDescription(groupDTO.getDescription().trim());

        //Check group name
        String groupName = groupDTO.getGroupName() != null ? groupDTO.getGroupName() : existingGroup.getName();
        if (!groupName.equals(existingGroup.getName()) && groupRepository.existsGroupByName(groupName)) {
            throw new UserManagementBusinessException(
                    MessageCode.SKM_GROUP_ALREADY_EXIST.toString(),
                    messageSource.getMessage(
                            MessageCode.SKM_GROUP_ALREADY_EXIST.toString(),
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    null
            );
        }

        String status = groupDTO.getStatus() != null ? groupDTO.getStatus() : existingGroup.getStatus();

        existingGroup.setName(groupName);
        existingGroup.setDescription(groupDTO.getDescription());
        existingGroup.setStatus(status);
        existingGroup.setDisplayName(groupDTO.getDisplayName());
        existingGroup.setModifiedBy(modifiedByUser);
        existingGroup.setModifiedDate(LocalDateTime.now());

        return existingGroup;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Group deleteGroup(String groupId, String modifiedByUser) {
        //Check modifying user
        if (!userRepository.existsByName(modifiedByUser)) {
            throw new UserManagementBusinessException(
                    MessageCode.USER_NOT_FOUND_MSG.toString(),
                    messageSource.getMessage(
                            MessageCode.USER_NOT_FOUND_MSG.toString(),
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    null
            );
        }

        Group existingGroup = groupRepository.findByIdAndStatus(groupId, Status.ACTIVE.getLabel()).orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.SKM_GROUP_NOT_EXIST_MSG.toString(),
                        messageSource.getMessage(
                                MessageCode.SKM_GROUP_NOT_EXIST_MSG.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        ),
                        null
                )
        );

        existingGroup.setStatus(Status.INACTIVE.getLabel());

        //clear relationship
        existingGroup.setUserGroups(new ArrayList<>());
        existingGroup.setGroupRoles(new ArrayList<>());

        //del grouprole and usergroup
        userGroupRepository.deleteByGroupId(existingGroup.getId());
        groupRoleRepository.deleteByGroupId(existingGroup.getId());

        existingGroup.setModifiedBy(modifiedByUser);
        existingGroup.setModifiedDate(LocalDateTime.now());

        return groupRepository.save(existingGroup);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Group addUsersToGroup(String groupId, List<UserDTO> userDTOs, String modifiedByUser) {

        //Check if group exist
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.SKM_GROUP_NOT_EXIST_MSG.toString(),
                        messageSource.getMessage(
                                MessageCode.SKM_GROUP_NOT_EXIST_MSG.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        ),
                        null
                )
        );

        if (Status.INACTIVE.getLabel().equals(group.getStatus())) {
            throw new UserManagementBusinessException(
                    MessageCode.SKM_GROUP_IS_INACTIVE.toString(),
                    messageSource.getMessage(
                            MessageCode.SKM_GROUP_IS_INACTIVE.toString(),
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    null
            );
        }

        //Check modifying user
        if (!userRepository.existsByName(modifiedByUser)) {
            throw new UserManagementBusinessException(
                    MessageCode.USER_NOT_FOUND_MSG.toString(),
                    messageSource.getMessage(
                            MessageCode.USER_NOT_FOUND_MSG.toString(),
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    null
            );
        }

        List<UserGroup> newUserGroups = userDTOs.stream()
                .map(userDTO -> userRepository.findByName(userDTO.getName()))
                //filter all user already exist in group
                .filter(
                        optUser -> optUser.filter(
                                user -> !userGroupRepository.findByGroupIdAndUserId(groupId, user.getId()).isPresent()
                        ).isPresent())
                .map(
                        optUser -> {
                            User user = optUser.get();

                            //create user_group
                            UserGroup userGroup = UserGroup.builder()
                                    .user(user)
                                    .group(group)
                                    .build();

                            //get List<UserGroup> in User and add the new userGroup
                            List<UserGroup> userUserGroups = user.getUserGroup();

                            userUserGroups.add(userGroup);

                            //set List back to user
                            user.setUserGroup(userUserGroups);
                            return userGroup;
                        }
                ).collect(Collectors.toList());

        List<UserGroup> userGroups = group.getUserGroups();
        userGroups.addAll(newUserGroups);

        group.setUserGroups(userGroups);
        group.setModifiedDate(LocalDateTime.now());
        group.setModifiedBy(modifiedByUser);

        //save group -> also save group user
        return groupRepository.save(group);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserFromGroup(String groupId, String userId, String modifiedByUser) {
        //Check if group exist
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.SKM_GROUP_NOT_EXIST_MSG.toString(),
                        messageSource.getMessage(
                                MessageCode.SKM_GROUP_NOT_EXIST_MSG.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        ),
                        null
                )
        );

        if (Status.INACTIVE.getLabel().equals(group.getStatus())) {
            throw new UserManagementBusinessException(
                    MessageCode.SKM_GROUP_IS_INACTIVE.toString(),
                    messageSource.getMessage(
                            MessageCode.SKM_GROUP_IS_INACTIVE.toString(),
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    null
            );
        }

        //Check modifying user
        if (!userRepository.existsByName(modifiedByUser)) {
            throw new UserManagementBusinessException(
                    MessageCode.USER_NOT_FOUND_MSG.toString(),
                    messageSource.getMessage(
                            MessageCode.USER_NOT_FOUND_MSG.toString(),
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    null
            );
        }

        //Check if removed user exist
        User removedUser = userRepository.findById(userId).orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.USER_NOT_FOUND_MSG.toString(),
                        messageSource.getMessage(
                                MessageCode.USER_NOT_FOUND_MSG.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        ),
                        null
                )
        );

        //clear relationship
        group.removeUserFromUserGroup(removedUser.getName());

        //delete user group in DB
        userGroupRepository.deleteByUserAndGroup(removedUser, group);
        log.info("Delete user : " + removedUser.getName() + " from group : " + group.getName());

        group.setModifiedDate(LocalDateTime.now());
        group.setModifiedBy(modifiedByUser);

        groupRepository.save(group);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Group addRolesToGroup(String groupId, List<String> roles, String modifiedByUser) {
        //Check if group exist
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.SKM_GROUP_NOT_EXIST_MSG.toString(),
                        messageSource.getMessage(
                                MessageCode.SKM_GROUP_NOT_EXIST_MSG.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        ),
                        null
                )
        );

        if (Status.INACTIVE.getLabel().equals(group.getStatus())) {
            throw new UserManagementBusinessException(
                    MessageCode.SKM_GROUP_IS_INACTIVE.toString(),
                    messageSource.getMessage(
                            MessageCode.SKM_GROUP_IS_INACTIVE.toString(),
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    null
            );
        }

        //Check modifying user
        if (!userRepository.existsByName(modifiedByUser)) {
            throw new UserManagementBusinessException(
                    MessageCode.USER_NOT_FOUND_MSG.toString(),
                    messageSource.getMessage(
                            MessageCode.USER_NOT_FOUND_MSG.toString(),
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    null
            );
        }

        //Check role exist and create new groupRoleList
        List<GroupRole> newGroupRole = roles.parallelStream()
                .filter(
                        //filter role that already assigned to group
                        roleId -> !groupRoleRepository.findByGroupIdAndRoleId(groupId, roleId).isPresent()
                )
                .map(roleId -> roleRepository.findById(roleId))
                .filter(Optional::isPresent)
                .map(
                        roleOpt -> GroupRole
                                .builder()
                                .role(roleOpt.get())
                                .group(group)
                                .build()
                )
                .collect(Collectors.toList());

        List<GroupRole> existGroupRole = group.getGroupRoles();
        existGroupRole.addAll(newGroupRole);

        group.setGroupRoles(existGroupRole);
        group.setModifiedDate(LocalDateTime.now());
        group.setModifiedBy(modifiedByUser);

        return groupRepository.save(group);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRoleFromGroup(String groupId, String roleId, String modifiedByUser) {
        //Check if group exist
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.SKM_GROUP_NOT_EXIST_MSG.toString(),
                        messageSource.getMessage(
                                MessageCode.SKM_GROUP_NOT_EXIST_MSG.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        ),
                        null
                )
        );

        if (Status.INACTIVE.getLabel().equals(group.getStatus())) {
            throw new UserManagementBusinessException(
                    MessageCode.SKM_GROUP_IS_INACTIVE.toString(),
                    messageSource.getMessage(
                            MessageCode.SKM_GROUP_IS_INACTIVE.toString(),
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    null
            );
        }

        //Check modifying user
        if (!userRepository.existsByName(modifiedByUser)) {
            throw new UserManagementBusinessException(
                    MessageCode.USER_NOT_FOUND_MSG.toString(),
                    messageSource.getMessage(
                            MessageCode.USER_NOT_FOUND_MSG.toString(),
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    null
            );
        }

        //Check role exist and remove role group
        Role role = roleRepository.findById(roleId).orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.ROLE_NOT_EXIST_MSG.toString(),
                        messageSource.getMessage(
                                MessageCode.ROLE_NOT_EXIST_MSG.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        ),
                        null
                )
        );

        //clear relationship
        group.removeRoleFromGroupRole(roleId);

        //delete rela in db
        groupRoleRepository.deleteByGroupAndRole(group, role);

        group.setModifiedDate(LocalDateTime.now());
        group.setModifiedBy(modifiedByUser);

        groupRepository.save(group);
    }
}
