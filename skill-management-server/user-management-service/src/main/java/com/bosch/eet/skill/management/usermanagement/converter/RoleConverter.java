package com.bosch.eet.skill.management.usermanagement.converter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.bosch.eet.skill.management.usermanagement.dto.PermissionCategoryDTO;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;
import com.bosch.eet.skill.management.usermanagement.dto.RoleDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Permission;
import com.bosch.eet.skill.management.usermanagement.entity.Role;
import com.bosch.eet.skill.management.usermanagement.entity.RolePermission;
import com.bosch.eet.skill.management.usermanagement.repo.PermissionRepository;
import com.bosch.eet.skill.management.usermanagement.repo.RolePermissionRepository;
import com.bosch.eet.skill.management.usermanagement.service.PermissionCategoryService;
import com.bosch.eet.skill.management.usermanagement.service.PermissionService;

@Component
public class RoleConverter {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionCategoryService permissionCategoryService;

    public Role convertDTOToEntity(final RoleDTO dto) {
        Role entity = new Role();
        if (dto != null) {
            if (!StringUtils.isEmpty(dto.getId())) { // case update
                entity.setId(dto.getId());
                entity.setCreatedDate(dto.getCreatedDate());

                List<RolePermission> rolePermissions = new ArrayList<>();
                for (String permissionID : dto.getPermissionIds()) {
                    RolePermission rolePermission = new RolePermission();
                    Permission permission = permissionRepository.findById(permissionID).get();
                    rolePermission.setPermission(permission);
                    rolePermission.setRole(entity);
                    rolePermissions.add(rolePermission);
                }
                entity.setRolePermissions(rolePermissions);

            }
            entity.setName(dto.getName());
            entity.setDisplayName(dto.getDisplayName());
            entity.setDescription(dto.getDescription());
            entity.setStatus(dto.getStatus());
            entity.setPriority(dto.getPriority());
        }
        return entity;
    }

    public List<Role> convertDTOsToEntities(final List<RoleDTO> dtos) {
        List<Role> entities = new LinkedList<>();
        if (!CollectionUtils.isEmpty(dtos)) {
            for (RoleDTO dto : dtos) {
                Role entity = convertDTOToEntity(dto);
                entities.add(entity);
            }
        }
        return entities;
    }

    public RoleDTO convertEntityToDTO(final Role entity, boolean isGetDetail) {
        RoleDTO dto = new RoleDTO();
        if (entity != null) {

            List<PermissionCategoryDTO> permissionCategories = permissionCategoryService.getAllPermissionCategories();

            dto.setId(entity.getId());
            dto.setName(entity.getName());
            dto.setStatus(entity.getStatus());
            dto.setDescription(entity.getDescription());
            dto.setPriority(entity.getPriority());

            if (isGetDetail) {
                if (!CollectionUtils.isEmpty(entity.getRolePermissions())) {
                    for (RolePermission rolePermission : entity.getRolePermissions()) {
                        for (PermissionCategoryDTO permissionCategoryDTO : permissionCategories) {
                            for (PermissionDTO permissionDTO : permissionCategoryDTO.getPermissionDTOS()) {
                                if (permissionDTO.getId().equalsIgnoreCase(rolePermission.getPermission().getId())) {
                                    permissionDTO.setBelongsToRole(true);
                                }
                            }
                        }
                    }
                }
            }
            dto.setPermissionCategories(permissionCategories);

        }
        return dto;
    }

    public List<RoleDTO> convertEntitiesToDTOs(final List<Role> entities, boolean isGetDetail) {
        List<RoleDTO> dtos = new LinkedList<>();
        if (!CollectionUtils.isEmpty(entities)) {
            for (Role entity : entities) {
                RoleDTO dto = new RoleDTO();
                dto = convertEntityToDTO(entity, isGetDetail);
                dtos.add(dto);
            }
        }
        return dtos;
    }

}
