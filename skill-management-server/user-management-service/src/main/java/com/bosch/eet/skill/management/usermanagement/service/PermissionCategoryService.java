package com.bosch.eet.skill.management.usermanagement.service;

import java.util.List;

import com.bosch.eet.skill.management.usermanagement.dto.PermissionCategoryDTO;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;
import com.bosch.eet.skill.management.usermanagement.entity.PermissionCategory;

public interface PermissionCategoryService {


    List<PermissionCategoryDTO> getAllPermissionCategories();

    List<PermissionDTO> getAllPermissionsByPermissionCategoryId();

    List<PermissionCategory> findAllPermissionCategories();
}
