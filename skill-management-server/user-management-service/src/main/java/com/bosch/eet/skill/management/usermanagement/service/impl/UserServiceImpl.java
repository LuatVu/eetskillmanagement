/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.usermanagement.service.impl;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.bosch.eet.skill.management.common.ObjectMapperUtils;
import com.bosch.eet.skill.management.common.Status;
import com.bosch.eet.skill.management.usermanagement.consts.Constants;
import com.bosch.eet.skill.management.usermanagement.consts.MessageCode;
import com.bosch.eet.skill.management.usermanagement.converter.UserManagementConverter;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;
import com.bosch.eet.skill.management.usermanagement.dto.RoleDTO;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Group;
import com.bosch.eet.skill.management.usermanagement.entity.GroupRole;
import com.bosch.eet.skill.management.usermanagement.entity.Role;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.entity.UserGroup;
import com.bosch.eet.skill.management.usermanagement.entity.UserHasConfiguration;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementBusinessException;
import com.bosch.eet.skill.management.usermanagement.repo.UserHasConfigurationRepository;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;
import com.bosch.eet.skill.management.usermanagement.service.PermissionService;
import com.bosch.eet.skill.management.usermanagement.service.RoleService;
import com.bosch.eet.skill.management.usermanagement.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LUK1HC
 */

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserHasConfigurationRepository userHasConfigurationRepository;

    @Autowired
    private UserManagementConverter userManagementConverter;

    @Autowired
    @Qualifier("CoreUserNamagementMessageSource")
    private MessageSource messageSource;

    @Override
    public List<UserDTO> findAll() {
        final List<User> entities = userRepository.findAllByStatus("Active");
        return ObjectMapperUtils.mapAll(entities, UserDTO.class);
    }

    @Override
    @Transactional
    public String save(final UserDTO dto) {
        // TODO Validation

        final User entity = ObjectMapperUtils.map(dto, User.class);
        return userRepository.save(entity).getId();
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserDTO findById(final String id) {
        UserDTO dto = new UserDTO();
        final Optional<User> optEntity = userRepository.findById(id);
        if (optEntity.isPresent()) {
            User entity = optEntity.get();
            dto = ObjectMapperUtils.map(entity, UserDTO.class);
        }
        return dto;
    }

    @Override
    @Transactional
    public void deleteById(final String id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO findActiveUserByName(final String name) {
        UserDTO dto = new UserDTO();
        Optional<User> optEntity = userRepository.findByName(name);
        if (optEntity.isPresent()) {
            User entity = optEntity.get();
            if (!Status.ACTIVE.getLabel().equals(entity.getStatus())) {
                throw new UserManagementBusinessException(
                        MessageCode.SKM_USER_INACTIVE.toString(),
                        messageSource.getMessage(
                                MessageCode.SKM_USER_INACTIVE.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        ),
                        null
                );
            }
            dto = ObjectMapperUtils.map(entity, UserDTO.class);
        }
        return dto;
    }

    @Override
    public List<PermissionDTO> getUserPermissions(final String id) {
        // Get roles belong user
        final List<RoleDTO> rolesDto = roleService.findAllBelongUser(id);

        List<String> rolesId = new LinkedList<>();
        if (!CollectionUtils.isEmpty(rolesDto)) {
            for (RoleDTO roleDto : rolesDto) {
                rolesId.add(roleDto.getId());
            }
        }
        // Get permissions belong user's roles
        return permissionService.findAllBelongRoles(rolesId);
    }

    @Override
    public List<UserDTO> findByConfigurationIdAndIsChecked(final String configurationId, final boolean isChecked) {
        List<UserHasConfiguration> userHasConfigurations = userHasConfigurationRepository.findByConfigurationIdAndIsChecked(configurationId, isChecked);
        List<UserDTO> userDTOS = new LinkedList<>();
        if (!CollectionUtils.isEmpty(userHasConfigurations)) {
            userHasConfigurations.forEach(item -> {
                userDTOS.add(ObjectMapperUtils.map(item.getUser(), UserDTO.class));
            });
        }
        return userDTOS;
    }


    @Override
    public User findByNTId(String userID) {
        final Optional<User> user = userRepository.findByName(userID);
        return user.orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.USER_NOT_FOUND_MSG.toString(),
                        messageSource.getMessage(
                                MessageCode.USER_NOT_FOUND_MSG.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        )
                )
        );
    }

    @Override
    public User findByName(String name) {
        return userRepository.findByName(name).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User addUser(UserDTO userDTO) {

        User user = User.builder()
                .name(userDTO.getName())
                .displayName(userDTO.getDisplayName())
                .email(userDTO.getEmail())
                .type(userDTO.getType())
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .createdBy(Constants.SYS_AD_ID)
                .modifiedBy(Constants.SYS_AD_ID)
                .status(userDTO.getStatus())
                .build();

        return userRepository.save(user);
    }

    @Override
    public List<UserDTO> findByIdIn(List<String> ids) {
        List<User> users = userRepository.findByIdIn(ids);
        return users.parallelStream().map(user -> userManagementConverter.convertToDTO(user)).collect(Collectors.toList());
    }

    @Override
    public boolean existByName(String userName) {
        return userRepository.existsByName(userName);
    }

    @Override
    public List<UserDTO> findUserByNameLike(String phrase) {

        List<User> users = userRepository.findAllByNameLike(phrase);

        return users.parallelStream().map(user -> userManagementConverter.convertToDTO(user)).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<UserDTO> findUserByIdAndActive(List<String> userIDs) {
        List<User> users = userRepository.findByIdIn(userIDs);
        return users.parallelStream().map(
                user -> {
                    if (Status.INACTIVE.getLabel().equals(user.getStatus())) {
                        user.setStatus(Status.ACTIVE.getLabel());
                        userRepository.save(user);
                    }
                    return userManagementConverter.convertToDTO(user);
                }
        ).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> findUserByRoleName(String roleName) {
        Role role = roleService.findRoleByRoleName(roleName);

        List<Group> groups = role.getGroupRoles().stream().map(
                GroupRole::getGroup
        ).collect(Collectors.toList());

        List<UserGroup> userGroups = groups.stream().flatMap(
                group -> group.getUserGroups().stream()
        ).collect(Collectors.toList());

        return userGroups.stream()
                .map(
                        userGroup -> UserDTO.builder()
                                .id(userGroup.getUser().getId())
                                .name(userGroup.getUser().getName())
                                .displayName(userGroup.getUser().getDisplayName())
                                .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public User findByDisplayName(String displayName) {
        return userRepository.findByDisplayName(displayName).orElse(null);
    }

    @Override
    public List<User> findByNameIn(Set<String> ntIds) {
        return userRepository.findByNameIn(ntIds);
    }
}
