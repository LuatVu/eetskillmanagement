package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.SkillManagementWsApplication;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Permission;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementBusinessException;
import com.bosch.eet.skill.management.usermanagement.repo.PermissionRepository;
import com.bosch.eet.skill.management.usermanagement.service.PermissionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SkillManagementWsApplication.class)
@ActiveProfiles("test")
public class PermissionServiceTest {

    @Autowired
    PermissionService permissionService;
    
    @Autowired
    PermissionRepository permissionRepository;

    @Test
    @DisplayName("findPermissionByName")
    @Transactional
    public void findPermissionByName_returnAListPermissionDTO(){

        PermissionDTO viewAllUser = permissionService.findPermissionByName("View all user");

        assertThat("VIEW_ALL_USER").isEqualTo(viewAllUser.getCode());
    }

    @Test
    @DisplayName("getAllPermissions")
    @Transactional
    public void getAllPermissions_returnAllPermission(){

        List<PermissionDTO> allPermissions = permissionService.getAllPermissions();
        assertThat(allPermissions).isNotEmpty();
    }

    @Test
    @DisplayName("findAllBelongRoles")
    @Transactional
    public void findAllBelongRoles_thenReturnListPermissionByRoles(){

        List<PermissionDTO> permissionAdmin = permissionService
                .findAllBelongRoles(Collections.singletonList("rladmin3-d1c0-11ec-81c0-38f3ab0673e4"));
        assertThat(permissionAdmin).isNotEmpty();
    }
    

    @Test
    @DisplayName("update permission happy case")
    @Transactional
    public void updatePermissionHappy(){

    	Permission dbPermission = Permission.builder()
    			.name("testPermission")
    			.description("testPermission")
    			.code("TEST_PERMISSION")
    			.build();
    	
    	String permissionId = permissionRepository.save(dbPermission).getId();
    	
    	PermissionDTO updatePermissionDto = PermissionDTO.builder()
    			.id(permissionId)
    			.name("testPermission edited")
    			.description("testPermission edited")
    			.code("TEST_PERMISSION")
    			.modifiedBy("rrn5hc")
    			.build();
    	
        PermissionDTO resultDto = permissionService.updatePermission(updatePermissionDto);
        assertNotNull(resultDto);
        assertThat("testPermission edited").isEqualTo(resultDto.getName());
        assertThat("testPermission edited").isEqualTo(resultDto.getDescription());
        Permission updatedDbPermission = permissionRepository.findById(permissionId).orElseGet(null);

        assertNotNull(updatedDbPermission);
        assertThat("testPermission edited").isEqualTo(updatedDbPermission.getName());
        assertThat("testPermission edited").isEqualTo(updatedDbPermission.getDescription());
        assertThat("rrn5hc").isEqualTo(updatedDbPermission.getModifiedBy());
        assertNotNull(updatedDbPermission.getModifiedDate());
    }
    
    
    @Test
    @DisplayName("update permission no Id")
    @Transactional
    public void updatePermissionWithoutId(){    	
    	PermissionDTO updatePermissionDto = PermissionDTO.builder()
    			.name("testPermission edited")
    			.description("testPermission edited")
    			.code("TEST_PERMISSION")
    			.modifiedBy("rrn5hc")
    			.build();
    	
        assertThrows(UserManagementBusinessException.class, () -> permissionService.updatePermission(updatePermissionDto));
    }
    
    @Test
    @DisplayName("update permission not found")
    @Transactional
    public void updatePermissionNotFound(){    	
    	PermissionDTO updatePermissionDto = PermissionDTO.builder()
    			.id("testId")
    			.name("testPermission edited")
    			.description("testPermission edited")
    			.code("TEST_PERMISSION")
    			.modifiedBy("rrn5hc")
    			.build();
    	
        assertThrows(UserManagementBusinessException.class, () -> permissionService.updatePermission(updatePermissionDto));
    }
}
