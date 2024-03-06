package com.bosch.eet.skill.management.usermanagement.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.common.ObjectMapperUtils;
import com.bosch.eet.skill.management.common.Status;
import com.bosch.eet.skill.management.ldap.common.Constants;
import com.bosch.eet.skill.management.usermanagement.consts.MessageCode;
import com.bosch.eet.skill.management.usermanagement.converter.RoleConverter;
import com.bosch.eet.skill.management.usermanagement.dto.RoleDTO;
import com.bosch.eet.skill.management.usermanagement.dto.role.AddRoleDTO;
import com.bosch.eet.skill.management.usermanagement.dto.role.UpdateRoleDTO;
import com.bosch.eet.skill.management.usermanagement.entity.GroupRole;
import com.bosch.eet.skill.management.usermanagement.entity.Permission;
import com.bosch.eet.skill.management.usermanagement.entity.Role;
import com.bosch.eet.skill.management.usermanagement.entity.RolePermission;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementBusinessException;
import com.bosch.eet.skill.management.usermanagement.repo.GroupRoleRepository;
import com.bosch.eet.skill.management.usermanagement.repo.PermissionRepository;
import com.bosch.eet.skill.management.usermanagement.repo.RolePermissionRepository;
import com.bosch.eet.skill.management.usermanagement.repo.RoleRepository;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;
import com.bosch.eet.skill.management.usermanagement.service.RoleService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleConverter converter;

    @Autowired
    @Qualifier("CoreUserNamagementMessageSource")
    private MessageSource messageSource;

    @Autowired
    private GroupRoleRepository groupRoleRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAllByStatus(Status.ACTIVE.getLabel());
    }

    @Override
    public Role getRoleDetail(String id) {

        return roleRepository.findById(id).orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.ROLE_NOT_EXIST_MSG.toString(),
                        messageSource.getMessage(
                                MessageCode.ROLE_NOT_EXIST_MSG.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        )
                )
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role addRole(AddRoleDTO addRoleDTO, String creatingUserName) {

        User creatingUser = userRepository.findByName(creatingUserName).orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.USER_NOT_FOUND_MSG.toString(),
                        messageSource.getMessage(
                                MessageCode.USER_NOT_FOUND_MSG.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        )
                )
        );
        
        //find role by name ignore case. If role name existed and status is inactive -> active role and then update
        //else if name existed and role is active -> throw exception
        Optional<Role> roleByName = roleRepository.findByNameIgnoreCase(addRoleDTO.getName().trim());
        if (roleByName.isPresent()) {
            Role role = roleByName.get();

            if (Status.INACTIVE.getLabel().equals(role.getStatus())) {

                UpdateRoleDTO updateRoleDTO = UpdateRoleDTO.builder()
                        .roleId(role.getId())
                        .name(addRoleDTO.getName().trim())
                        .status(Status.ACTIVE.getLabel())
                        .displayName(addRoleDTO.getDisplayName())
                        .description(addRoleDTO.getDescription().trim())
                        .permissionIds(addRoleDTO.getPermissionIds())
                        .priority(addRoleDTO.getPriority())
                        .build();

                return updateRole(updateRoleDTO, creatingUserName);
            }

            log.error(addRoleDTO.getName() + " is duplicated.");
            throw new UserManagementBusinessException(
                    MessageCode.SKM_ROLE_EXISTED_ALREADY.toString(),
                    messageSource.getMessage(MessageCode.SKM_ROLE_EXISTED_ALREADY.toString(), null,
                            LocaleContextHolder.getLocale()),
                    null);
        }

        validateRoleName(addRoleDTO.getName());

        Role role = Role.builder()
                .name(addRoleDTO.getName().trim())
                .displayName(addRoleDTO.getDisplayName())
                .description(addRoleDTO.getDescription().trim())
                .status(addRoleDTO.getStatus())
                .createdDate(LocalDateTime.now())
                .createdBy(creatingUser.getId())
                .modifiedDate(LocalDateTime.now())
                .modifiedBy(creatingUser.getId())
                .rolePermissions(new ArrayList<>())
                .priority(addRoleDTO.getPriority())
                .build();

        List<RolePermission> rolePermissions = permissionRepository.findPermissionsById(addRoleDTO.getPermissionIds())
                .stream().map(
                        permission -> {
                            RolePermission rolePermission = RolePermission.builder()
                                    .role(role)
                                    .permission(permission)
                                    .build();

                            permission.getRolePermissions().add(rolePermission);
                            return rolePermission;
                        }
                ).collect(Collectors.toList());

        role.getRolePermissions().addAll(rolePermissions);

        return roleRepository.save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role updateRole(UpdateRoleDTO updateRoleDTO, String modifyingUserName) {

        User modifyingUser = userRepository.findByName(modifyingUserName).orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.USER_NOT_FOUND_MSG.toString(),
                        messageSource.getMessage(
                                MessageCode.USER_NOT_FOUND_MSG.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        )
                )
        );

        Role updateRole = roleRepository.findById(updateRoleDTO.getRoleId()).orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.ROLE_NOT_EXIST_MSG.toString(),
                        messageSource.getMessage(
                                MessageCode.ROLE_NOT_EXIST_MSG.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        )
                )
        );

        validateRoleName(updateRoleDTO.getName());

        String status = updateRoleDTO.getStatus() != null ? updateRoleDTO.getStatus() : updateRole.getStatus();
        String roleName = updateRoleDTO.getName() != null ? updateRoleDTO.getName().trim() : updateRole.getName();

        if (roleRepository.findByNameIgnoreCase(roleName).isPresent()
                && !updateRole.getId().equals(updateRoleDTO.getRoleId())) {

            log.error(updateRoleDTO.getName() + " is duplicated.");
            throw new UserManagementBusinessException(
                    MessageCode.SKM_ROLE_EXISTED_ALREADY.toString(),
                    messageSource.getMessage(MessageCode.SKM_ROLE_EXISTED_ALREADY.toString(), null,
                            LocaleContextHolder.getLocale()),
                    null);
        }

        //update new info to role
        updateRole.setName(roleName);
        updateRole.setDisplayName(updateRoleDTO.getDisplayName());
        updateRole.setDescription(updateRoleDTO.getDescription().trim());
        updateRole.setStatus(status);
        updateRole.setModifiedDate(LocalDateTime.now());
        updateRole.setModifiedBy(modifyingUser.getId());
        updateRole.setPriority(updateRoleDTO.getPriority());

        //get all permission in permissionIds
        List<RolePermission> rolePermissions = updateRoleDTO.getPermissionIds().stream().map(
                id -> {
                    Permission permission = permissionRepository.findById(id).orElseThrow(
                            () -> new UserManagementBusinessException(
                                    MessageCode.PERMISSION_NOT_FOUND.toString(),
                                    messageSource.getMessage(MessageCode.PERMISSION_NOT_FOUND.toString(), null,
                                            LocaleContextHolder.getLocale()),
                                    null)
                    );

                    RolePermission rolePermission = RolePermission.builder()
                            .permission(permission)
                            .role(updateRole)
                            .build();

                    //add rela to permission
                    permission.getRolePermissions().add(rolePermission);

                    return rolePermission;
                }
        ).collect(Collectors.toList());

        //delete all existed rela
        rolePermissionRepository.deleteByRoleId(updateRole.getId());

        //add rela to role
        updateRole.setRolePermissions(rolePermissions);
        return roleRepository.save(updateRole);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(String id, String modifyingUserName) {

        User modifyingUser = userRepository.findByName(modifyingUserName).orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.USER_NOT_FOUND_MSG.toString(),
                        messageSource.getMessage(
                                MessageCode.USER_NOT_FOUND_MSG.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        )
                )
        );

        Role role = roleRepository.findById(id).orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.SKM_ROLE_NOT_FOUND.toString(),
                        messageSource.getMessage(MessageCode.SKM_ROLE_NOT_FOUND.toString(), null,
                                LocaleContextHolder.getLocale()),
                        null)
        );

        role.setStatus(Status.INACTIVE.getLabel());
        role.setModifiedBy(modifyingUser.getId());
        role.setModifiedDate(LocalDateTime.now());
        role.setGroupRoles(new ArrayList<>());
        role.setRolePermissions(new ArrayList<>());

        roleRepository.save(role);
        rolePermissionRepository.deleteByRoleId(id);
        groupRoleRepository.deleteByRoleId(id);
    }

    @Override
    public List<RoleDTO> findByIdIn(List<String> roleIds) {
        List<Role> roles = roleRepository.findByIdIn(roleIds);
        return roles.parallelStream().map(role -> converter.convertEntityToDTO(role, true)).collect(Collectors.toList());
    }

    @Override
    public List<RoleDTO> findAllBelongUser(String userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.USER_NOT_FOUND_MSG.toString(),
                        messageSource.getMessage(
                                MessageCode.USER_NOT_FOUND_MSG.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        )
                )
        );

        List<Role> roleList = user.getUserGroup().stream().flatMap(
                userGroup -> userGroup.getGroup().getGroupRoles().stream().map(
                        GroupRole::getRole
                )
        ).collect(Collectors.toList());
        return ObjectMapperUtils.mapAll(roleList, RoleDTO.class);
    }

    //Check empty, check invalid regex, check
    private void validateRoleName(final String name) {
        String regex = Constants.REGEX_SOME_SPECIAL_CHARACTER;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);

        if (StringUtils.isEmpty(name)) {
            throw new UserManagementBusinessException(MessageCode.SKM_ROLE_NAME_IS_EMPTY.toString(),
                    messageSource.getMessage(MessageCode.SKM_ROLE_NAME_IS_EMPTY.toString(), null,
                            LocaleContextHolder.getLocale()),
                    null);
        }

        if (matcher.matches()) {
            throw new UserManagementBusinessException(MessageCode.SKM_INVALID_ROLE_NAME.toString(),
                    messageSource.getMessage(MessageCode.SKM_INVALID_ROLE_NAME.toString(), null,
                            LocaleContextHolder.getLocale()),
                    null);
        }
    }

    @Override
    public Role findRoleByRoleName(String roleName) {
        return roleRepository.findByNameIgnoreCase(roleName).orElseThrow(
                () -> new UserManagementBusinessException(
                        MessageCode.ROLE_NOT_EXIST_MSG.toString(),
                        messageSource.getMessage(
                                MessageCode.ROLE_NOT_EXIST_MSG.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        )
                )
        );
    }
}
