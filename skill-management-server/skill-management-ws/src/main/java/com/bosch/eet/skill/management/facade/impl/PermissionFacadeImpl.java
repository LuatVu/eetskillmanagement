package com.bosch.eet.skill.management.facade.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.dto.response.AllPermissionsResponseDTO;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.PermissionFacade;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementBusinessException;
import com.bosch.eet.skill.management.usermanagement.service.PermissionCategoryService;
import com.bosch.eet.skill.management.usermanagement.service.PermissionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionFacadeImpl implements PermissionFacade {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PermissionCategoryService permissionCategoryService;

    private final MessageSource messageSource;
    
    @Override
    public AllPermissionsResponseDTO getAllPermissions() {

        return AllPermissionsResponseDTO.convert(
                permissionService.findAllPermission(),
                permissionCategoryService.findAllPermissionCategories()
        );
    }
    
    @Override
    public PermissionDTO updatePermission(PermissionDTO permissionDto) {
    	try {
    		return permissionService.updatePermission(permissionDto);
    	} catch (UserManagementBusinessException userManagementBusinessException) {
    		throw userManagementBusinessException;
    	} catch (Exception exception) {
    		throw new SkillManagementException(MessageCode.UPDATE_PERMISSION_FAIL.toString(),
                    messageSource.getMessage(MessageCode.UPDATE_PERMISSION_FAIL.toString(), null,
                            LocaleContextHolder.getLocale()),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
}
