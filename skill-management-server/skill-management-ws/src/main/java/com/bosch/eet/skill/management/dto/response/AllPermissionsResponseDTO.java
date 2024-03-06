package com.bosch.eet.skill.management.dto.response;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import com.bosch.eet.skill.management.usermanagement.dto.PermissionCategoryDTO;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Permission;
import com.bosch.eet.skill.management.usermanagement.entity.PermissionCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllPermissionsResponseDTO implements Serializable {

    private List<PermissionDTO> permissionDTOs;

    private List<PermissionCategoryDTO> permissionCategoryDTOs;

    public static AllPermissionsResponseDTO convert(List<Permission> permissions, List<PermissionCategory> permissionCategories){

        List<PermissionDTO> permissionDTOS = permissions.stream().map(
                permission -> PermissionDTO.builder()
                        .id(permission.getId())
                        .name(permission.getName())
                        .code(permission.getCode())
                        .status(permission.getStatus())
                        .description(permission.getDescription())
                        .permissionCategoriesId(permission.getPermissionCategory().getId())
                        .build()
        ).collect(Collectors.toList());

        List<PermissionCategoryDTO> permissionCategoryDTOS = permissionCategories.stream().map(
                permissionCategory -> PermissionCategoryDTO.builder()
                        .id(permissionCategory.getId())
                        .code(permissionCategory.getCode())
                        .name(permissionCategory.getName())
                        .sequence(permissionCategory.getSequence())
                        .build()
        ).collect(Collectors.toList());

        return AllPermissionsResponseDTO.builder()
                .permissionDTOs(permissionDTOS)
                .permissionCategoryDTOs(permissionCategoryDTOS)
                .build();
    }
}
