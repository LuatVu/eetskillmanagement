package com.bosch.eet.skill.management.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.bosch.eet.skill.management.usermanagement.dto.PermissionCategoryDTO;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Permission;
import com.bosch.eet.skill.management.usermanagement.entity.PermissionCategory;
import com.bosch.eet.skill.management.usermanagement.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDTO implements Serializable {

    private String id;

    private String name;

    private String status;

    private String description;

    private Integer priority;

    private List<PermissionDTO> permissionDTOs;

    private List<PermissionCategoryDTO> permissionCategoryDTOs;

    public static RoleResponseDTO convert(Role role, List<PermissionCategory> permissionCategories) {

        //get all permission belong to role
        List<String> permissionIdBelongToRoles = role.getRolePermissions().stream().map(
                rolePermission -> rolePermission.getPermission().getId()
        ).collect(Collectors.toList());

        //get all permission
        List<Permission> allPermission = new ArrayList<>();

        //create permission category dto and add get all permissions which have permission category
        List<PermissionCategoryDTO> permissionCategoryDTOS = permissionCategories.stream().map(
                permissionCategory -> {
                    allPermission.addAll(permissionCategory.getPermissions());
                    return PermissionCategoryDTO.builder()
                            .id(permissionCategory.getId())
                            .name(permissionCategory.getName())
                            .sequence(permissionCategory.getSequence())
                            .build();
                }
        ).collect(Collectors.toList());

        //create permissionDTOs
        List<PermissionDTO> permissionDTOS = allPermission.stream().map(
                permission -> {

                    boolean belongToRole = permissionIdBelongToRoles.contains(permission.getId());

                    return PermissionDTO.builder()
                            .id(permission.getId())
                            .name(permission.getName())
                            .code(permission.getCode())
                            .permissionCategoriesId(permission.getPermissionCategory().getId())
                            .status(permission.getStatus())
                            .description(permission.getDescription())
                            .belongsToRole(belongToRole)
                            .build();
                }
        ).collect(Collectors.toList());

        return RoleResponseDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .status(role.getStatus())
                .description(role.getDescription())
                .permissionDTOs(permissionDTOS)
                .permissionCategoryDTOs(permissionCategoryDTOS)
                .priority(role.getPriority())
                .build();
    }
}
