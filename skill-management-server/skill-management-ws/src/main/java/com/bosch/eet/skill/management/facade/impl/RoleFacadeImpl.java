package com.bosch.eet.skill.management.facade.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.dto.response.RoleResponseDTO;
import com.bosch.eet.skill.management.facade.RoleFacade;
import com.bosch.eet.skill.management.usermanagement.dto.role.AddRoleDTO;
import com.bosch.eet.skill.management.usermanagement.dto.role.UpdateRoleDTO;
import com.bosch.eet.skill.management.usermanagement.entity.PermissionCategory;
import com.bosch.eet.skill.management.usermanagement.service.PermissionCategoryService;
import com.bosch.eet.skill.management.usermanagement.service.RoleService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoleFacadeImpl implements RoleFacade {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionCategoryService permissionCategoryService;

    @Override
    public List<RoleResponseDTO> getAllRoles() {
        List<PermissionCategory> allPermissionCategories = permissionCategoryService.findAllPermissionCategories();

        return roleService.getAllRoles().stream().map(
                role -> RoleResponseDTO.convert(role, allPermissionCategories)
        ).collect(Collectors.toList());
    }

    @Override
    public RoleResponseDTO getRoleDetail(String id) {
        List<PermissionCategory> allPermissionCategories = permissionCategoryService.findAllPermissionCategories();

        return RoleResponseDTO.convert(roleService.getRoleDetail(id), allPermissionCategories);
    }

    @Override
    public RoleResponseDTO addRole(AddRoleDTO addRoleDTO, String creatingUserName) {
        List<PermissionCategory> allPermissionCategories = permissionCategoryService.findAllPermissionCategories();
        log.info("Create Role: " + addRoleDTO.toString());
        // save to db

        return RoleResponseDTO.convert(roleService.addRole(addRoleDTO, creatingUserName), allPermissionCategories);
    }

    @Override
    public RoleResponseDTO updateRole(String id, UpdateRoleDTO updateRoleDTO, String modifiedUserId) {
        List<PermissionCategory> allPermissionCategories = permissionCategoryService.findAllPermissionCategories();
        log.info("Update Role: " + updateRoleDTO.toString());
        // save to db
        updateRoleDTO.setRoleId(id);
        return RoleResponseDTO.convert(
                roleService.updateRole(updateRoleDTO, modifiedUserId),
                allPermissionCategories
        );
    }

    @Override
    public void deleteRole(String id, String modifiedUserId) {
        log.info("Delete Role: " + id);
        roleService.deleteRole(id, modifiedUserId);
    }
}
