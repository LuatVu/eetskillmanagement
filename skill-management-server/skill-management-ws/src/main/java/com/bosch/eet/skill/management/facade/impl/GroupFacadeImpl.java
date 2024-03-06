package com.bosch.eet.skill.management.facade.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.Status;
import com.bosch.eet.skill.management.common.UserType;
import com.bosch.eet.skill.management.converter.utils.LDAPRequestConverterUtil;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.request.group.AddUsersToGroupDTO;
import com.bosch.eet.skill.management.dto.response.AddUserToGroupResponseDTO;
import com.bosch.eet.skill.management.dto.response.GroupResponseDTO;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.GroupFacade;
import com.bosch.eet.skill.management.ldap.exception.LdapException;
import com.bosch.eet.skill.management.ldap.model.LdapInfo;
import com.bosch.eet.skill.management.ldap.service.LdapService;
import com.bosch.eet.skill.management.service.PersonalService;
import com.bosch.eet.skill.management.usermanagement.dto.GroupDTO;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.entity.PermissionCategory;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementBusinessException;
import com.bosch.eet.skill.management.usermanagement.service.GroupService;
import com.bosch.eet.skill.management.usermanagement.service.PermissionCategoryService;
import com.bosch.eet.skill.management.usermanagement.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GroupFacadeImpl implements GroupFacade {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private LdapService ldapService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private PersonalService personalService;

    @Autowired
    private PermissionCategoryService permissionCategoryService;

    @Override
    public List<GroupResponseDTO> getAllGroups() {
        List<PermissionCategory> permissionCategories = permissionCategoryService.findAllPermissionCategories();
        return groupService.getAllGroup().stream().map(group -> GroupResponseDTO.convert(group, permissionCategories)).collect(Collectors.toList());
    }

    @Override
    public GroupResponseDTO createGroup(GroupDTO groupDTO, String userName) {
        List<PermissionCategory> permissionCategories = permissionCategoryService.findAllPermissionCategories();
        String status = groupDTO.getStatus() != null ? groupDTO.getStatus() : Status.ACTIVE.getLabel();
        groupDTO.setStatus(status);
        return GroupResponseDTO.convert(groupService.createGroup(groupDTO, userName), permissionCategories);
    }

    @Override
    public GroupResponseDTO getGroupDetailsById(String id) {
        List<PermissionCategory> permissionCategories = permissionCategoryService.findAllPermissionCategories();
        return GroupResponseDTO.convert(groupService.findGroupById(id), permissionCategories);
    }

    @Override
    public GroupResponseDTO updateGroup(GroupDTO groupDetails, String userName) {
        List<PermissionCategory> permissionCategories = permissionCategoryService.findAllPermissionCategories();
        return GroupResponseDTO.convert(groupService.updateGroup(groupDetails, userName), permissionCategories);
    }

    @Override
    public GroupResponseDTO deleteGroup(String groupId, String userName) {
        List<PermissionCategory> permissionCategories = permissionCategoryService.findAllPermissionCategories();
        return GroupResponseDTO.convert(groupService.deleteGroup(groupId, userName), permissionCategories);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddUserToGroupResponseDTO addUsersToGroup(AddUsersToGroupDTO requestDTO, String userName) {

        List<UserDTO> userDTOs = new ArrayList<>();

        Set<String> failUsers = new HashSet<>();

        //handle users that already in the system
        if (!CollectionUtils.isEmpty(requestDTO.getUsers())) {
            userDTOs = userService.findUserByIdAndActive(requestDTO.getUsers());
        }

        //handle users that added in person
        List<UserDTO> ldapUsersDTOs = requestDTO.getLdapUsers().parallelStream()
                .map(request -> LDAPRequestConverterUtil.convertToDTO(request))
                .collect(Collectors.toList());

        Set<String> ldapUserDtoNameSet = new HashSet<>();
        List<UserDTO> usersToAddList = new ArrayList<>();
        for (UserDTO ldapUsersDTO : ldapUsersDTOs) {
            if(ldapUserDtoNameSet.contains(ldapUsersDTO.getName())){
                continue;
            }else{ldapUserDtoNameSet.add(ldapUsersDTO.getName());}

            String ldapUsersDTOName = ldapUsersDTO.getName();
            try {
                LdapInfo ldapInfo = ldapService.getPrincipalInfo(ldapUsersDTOName).orElseThrow(
                        () -> new SkillManagementException(
                                MessageCode.SKM_INVALID_LDAP_CHECKED.toString(),
                                messageSource.getMessage(
                                        MessageCode.SKM_INVALID_LDAP_CHECKED.toString(),
                                        null,
                                        LocaleContextHolder.getLocale()),
                                null
                        )
                );
                ldapUsersDTO.setEmail(ldapInfo.getEmail());
                ldapUsersDTO.setDisplayName(ldapInfo.getDisplayName());
                addLdapUsers(ldapUsersDTO);
                usersToAddList.add(ldapUsersDTO);
            } catch (LdapException e) {
                log.error("Error adding user {}", ldapUsersDTOName);
                failUsers.add(ldapUsersDTOName);
                //TODO notify adding user fail
            }
        }
        userDTOs.addAll(usersToAddList);

        //handle user that added in group
        List<UserDTO> ldapDistributionDTOs = requestDTO.getDistributionLists().parallelStream()
                .map(LDAPRequestConverterUtil::convertToDTO)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        //Check and save distribution as user with type "Group"
        User createdBy = null;
        try {
            createdBy = userService.findByNTId(userName);
        } catch (Exception ignored){}

        Set<String> ldapDistributionDtoNameSet = new HashSet<>();
        List<UserDTO> distributionsToAddList = new ArrayList<>();
        for(UserDTO userDTO: ldapDistributionDTOs){
            if(ldapDistributionDtoNameSet.contains(userDTO.getName())){
                continue;
            }else{ldapDistributionDtoNameSet.add(userDTO.getName());}

            User user = null;
            try {
                user = userService.findByNTId(userDTO.getName());
                if(user!=null) {
                    user.setType(com.bosch.eet.skill.management.usermanagement.consts.Constants.GROUP);
                    user.setStatus(Constants.ACTIVE);
                    userService.updateUser(user);
                    distributionsToAddList.add(userDTO);
                    continue;
                }
            } catch (Exception ignored){}

            if(createdBy != null) {
                userDTO.setType(com.bosch.eet.skill.management.usermanagement.consts.Constants.GROUP);
                userDTO.setStatus(Constants.ACTIVE);
                userDTO.setCreatedBy(createdBy.getId());
                userService.save(userDTO);
            }
            distributionsToAddList.add(userDTO);
        }

        Set<UserDTO> userDTOFromDistributionList = getUserFromGroups(distributionsToAddList);
        userDTOFromDistributionList.forEach(
                userDTO -> {
                    try {
                        addLdapUsers(userDTO);
                    } catch (LdapException e) {
                        log.error("Error adding user {}", userDTO.getName());
                        //TODO notify adding user fail
                        failUsers.add(userDTO.getName());
                    }
                }
        );
        userDTOs.addAll(userDTOFromDistributionList);
        userDTOs.addAll(distributionsToAddList);

        List<PermissionCategory> permissionCategories = permissionCategoryService.findAllPermissionCategories();

        GroupResponseDTO groupResponseDTO = GroupResponseDTO.convert(
                groupService.addUsersToGroup(requestDTO.getGroupId(), userDTOs, userName),
                permissionCategories
        );

        //send email to user added to group. will take time
//        userDTOs.forEach(userDTO ->
//                emailService.mailToUserAddedToGroup(userName, userDTO.getEmail(), groupDTO.getGroupName()));

        return AddUserToGroupResponseDTO.builder()
                .groupResponseDTO(groupResponseDTO)
                .failUsers(failUsers)
                .build();
    }

    private Set<UserDTO> getUserFromGroups(List<UserDTO> ldapDistributionDTOs) {
        return ldapDistributionDTOs.stream().flatMap(ldapDistributionDTO -> {
            Set<LdapInfo> childGroups = ldapService.getChildGroups(ldapDistributionDTO.getDisplayName());
            Set<String> childGroupName = childGroups.stream().map(LdapInfo::getUserId).collect(Collectors.toSet());

            Set<String> allMembers = childGroups.stream().flatMap(cg -> {
                if (!Objects.isNull(cg.getMember())) {
					return cg.getMember().stream();
				}
                return Stream.empty();
            }).collect(Collectors.toSet());
            allMembers.removeAll(childGroupName);

            List<LdapInfo> principalInfos = ldapService.getPrincipalInfos(new ArrayList<>(allMembers));

            return principalInfos.stream().map(
                    ldapInfo -> UserDTO.builder()
                            .name(ldapInfo.getName())
                            .displayName(ldapInfo.getDisplayName())
                            .email(ldapInfo.getEmail())
                            .type(UserType.PERSON.getLabel())
                            .status(Status.ACTIVE.getLabel())
                            .build()
            );
        }).collect(Collectors.toSet());
    }

    @Override
    public GroupResponseDTO removeUserFromGroup(String groupId, String userId, String userName) {

        // Remove distribution
        UserDTO distributionDto;
        try{distributionDto = userService.findById(userId);}
        catch(Exception e){
            e.printStackTrace();
            distributionDto = null;
        }

        if(distributionDto != null) {
            if (distributionDto.getType().equalsIgnoreCase(com.bosch.eet.skill.management.usermanagement.consts.Constants.GROUP)) {
                List<UserDTO> ldapDistributions = new ArrayList<>();
                ldapDistributions.add(distributionDto);

                // Add childGroups to removing user list
                Set<LdapInfo> childGroupsLdap = ldapService.getChildGroups(distributionDto.getDisplayName());
                Set<UserDTO> childGroups = childGroupsLdap.stream().map(
                        ldapInfo -> UserDTO.builder()
                                .name(ldapInfo.getName())
                                .displayName(ldapInfo.getDisplayName())
                                .email(ldapInfo.getEmail())
                                .type(UserType.GROUP.getLabel())
                                .status(Status.ACTIVE.getLabel())
                                .build()
                ).collect(Collectors.toSet());

                Set<UserDTO> userDTOList = getUserFromGroups(ldapDistributions);
                userDTOList.addAll(ldapDistributions);
                userDTOList.addAll(childGroups);

                for (UserDTO userDTO : userDTOList) {
                    String uid = null;
                    try {
                        uid = userService.findByNTId(userDTO.getName()).getId();
                    } catch (Exception ignored) {
                    }

                    if (uid != null) {
						groupService.removeUserFromGroup(groupId, uid, userName);
					}
                }
            }
            // Remove user individually
            else {
                groupService.removeUserFromGroup(groupId, userId, userName);
            }
        }
        List<PermissionCategory> permissionCategories = permissionCategoryService.findAllPermissionCategories();
        return GroupResponseDTO.convert(groupService.findGroupById(groupId), permissionCategories);
    }

    @Override
    public GroupResponseDTO addRolesToGroup(String groupId, List<String> roles, String userName) {
        List<PermissionCategory> permissionCategories = permissionCategoryService.findAllPermissionCategories();
        return GroupResponseDTO.convert(groupService.addRolesToGroup(groupId, roles, userName), permissionCategories);
    }

    @Override
    public GroupResponseDTO removeRoleFromGroup(String groupId, String roleId, String userName) {
        groupService.removeRoleFromGroup(groupId, roleId, userName);
        List<PermissionCategory> permissionCategories = permissionCategoryService.findAllPermissionCategories();
        return GroupResponseDTO.convert(groupService.findGroupById(groupId), permissionCategories);
    }

    private void addLdapUsers(UserDTO ldapUser) throws LdapException {

        try {
            //If username is in DB and is Inactive -> set Active and save
            User user = userService.findByNTId(ldapUser.getName());
            if (!Status.ACTIVE.getLabel().equals(user.getStatus())) {
                user.setStatus(Status.ACTIVE.getLabel());
                userService.updateUser(user);

                personalService.addSkillsToPersonalWhenUserIsActivated(user);
            }
        } catch (UserManagementBusinessException e) {
            log.info("Ldap info {} not found in system yet", ldapUser.getName());
            //if username not in DB -> add new User
            User user = userService.addUser(
                    UserDTO.builder()
                            .name(ldapUser.getName())
                            .displayName(ldapUser.getDisplayName())
                            .email(ldapUser.getEmail())
                            .type(UserType.PERSON.getLabel())
                            .status(Status.ACTIVE.getLabel())
                            .build()
            );

            //add new personal
            personalService.savePersonalWithUser(
                    PersonalDto.builder()
                            .id(user.getId())
                            .code(user.getName())
                            .isUpdated(false)
                            .build()
            );

            //add all skill to user
            personalService.addSkillsToPersonalWhenUserIsActivated(user);
        }
    }
}
