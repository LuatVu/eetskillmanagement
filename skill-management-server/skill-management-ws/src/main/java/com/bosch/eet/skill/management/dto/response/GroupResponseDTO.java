package com.bosch.eet.skill.management.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import com.bosch.eet.skill.management.common.UserType;
import com.bosch.eet.skill.management.usermanagement.entity.Group;
import com.bosch.eet.skill.management.usermanagement.entity.PermissionCategory;
import com.bosch.eet.skill.management.usermanagement.entity.Role;
import com.bosch.eet.skill.management.usermanagement.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponseDTO implements Serializable {

    private String groupId;

    @NotBlank
    private String groupName;

    private String displayName;

    private String description;

    private String status;

    private List<UserResponseDTO> users;

    private List<UserResponseDTO> distributionlists;

    private List<RoleResponseDTO> roles;

    public static GroupResponseDTO convert(Group group, List<PermissionCategory> permissionCategories){

        List<UserResponseDTO> userList = new ArrayList<>();

        List<UserResponseDTO> disList = new ArrayList<>();

        //map user to UserResponseDTO
        group.getUsersGroup().forEach(
                userGroup -> {

                    User user = userGroup.getUser();

                    UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .displayName(user.getDisplayName())
                            .email(user.getEmail())
                            .status(user.getStatus())
                            .createdBy(user.getCreatedBy())
                            .type(user.getType())
                            .build();

                    if(UserType.GROUP.getLabel().equals(user.getType())){
                        userResponseDTO.setDistributionList(true);
                        disList.add(userResponseDTO);
                    } else{
                        userResponseDTO.setDistributionList(false);
                        userList.add(userResponseDTO);
                    }
                }
        );

        //map role to RoleResponseDTO
        List<RoleResponseDTO> roleResponseDTOS = group.getGroupRoles().stream().map(
                groupRole -> {
                    Role role = groupRole.getRole();
                    return RoleResponseDTO.convert(role, permissionCategories);
                }
        ).collect(Collectors.toList());

        return GroupResponseDTO.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .displayName(group.getDisplayName())
                .description(group.getDescription())
                .status(group.getStatus())
                .users(userList)
                .distributionlists(disList)
                .roles(roleResponseDTOS)
                .build();
    }
}
