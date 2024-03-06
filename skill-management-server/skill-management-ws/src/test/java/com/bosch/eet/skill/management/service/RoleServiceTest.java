package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.SkillManagementWsApplication;
import com.bosch.eet.skill.management.common.Status;
import com.bosch.eet.skill.management.usermanagement.dto.role.AddRoleDTO;
import com.bosch.eet.skill.management.usermanagement.dto.role.UpdateRoleDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Role;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementBusinessException;
import com.bosch.eet.skill.management.usermanagement.repo.GroupRoleRepository;
import com.bosch.eet.skill.management.usermanagement.repo.RolePermissionRepository;
import com.bosch.eet.skill.management.usermanagement.repo.RoleRepository;
import com.bosch.eet.skill.management.usermanagement.service.RoleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SkillManagementWsApplication.class)
@ActiveProfiles("test")
public class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GroupRoleRepository groupRoleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Test
    @Transactional
    public void getAllRoles_returnListRole() {
        List<Role> allRoles = roleService.getAllRoles();
        assertThat(allRoles).hasSize(2);
    }

    @Test
    @Transactional
    public void getRoleDetail_returnRole() {

        Role roleDetail = roleService.getRoleDetail("admin_role");
        assertThat(roleDetail.getName()).isEqualTo("Administrator");
        assertThat(roleDetail.getGroupRoles()).hasSize(0);
        assertThat(roleDetail.getRolePermissions()).hasSize(0);
    }

    @Test
    @Transactional
    public void getRoleDetail_roleNotFound_throwException() {

        assertThrows(UserManagementBusinessException.class, () -> roleService.getRoleDetail("not found"));
    }

    @Test
    @Transactional
    public void addRole_creatingUserNameNotFound_thenThrow() {

        AddRoleDTO addRoleDTO = AddRoleDTO.builder()
                .name("User")
                .status(Status.ACTIVE.getLabel())
                .build();

        List<String> permissionIds = Arrays.asList(
                "005be29f-d1c9-11ec-81c0-38f3ab0673e4",
                "13e55248-d1c5-11ec-81c0-38f3ab0673e4",
                "195283d2-61d5-11ed-b62d-00059a3c7a00"
        );

        addRoleDTO.setPermissionIds(permissionIds);

        assertThrows(UserManagementBusinessException.class, () -> roleService.addRole(addRoleDTO, "not found"));
    }

    @Test
    @Transactional
    public void addRole_addRoleActiveAndNameFound_thenThrow() {

        AddRoleDTO addRoleDTO = AddRoleDTO.builder()
                .name("User")
                .status(Status.ACTIVE.getLabel())
                .build();

        List<String> permissionIds = Arrays.asList(
                "005be29f-d1c9-11ec-81c0-38f3ab0673e4",
                "13e55248-d1c5-11ec-81c0-38f3ab0673e4",
                "195283d2-61d5-11ed-b62d-00059a3c7a00"
        );

        addRoleDTO.setPermissionIds(permissionIds);

        assertThrows(UserManagementBusinessException.class, () -> roleService.addRole(addRoleDTO, "admin"));
    }

    @Test
    @Transactional
    public void addRole_addRoleInactiveAndNameFound_thenActiveAndSave() {

        AddRoleDTO addRoleDTO = AddRoleDTO.builder()
                .name("Inactive_Role")
                .priority(100)
                .build();

        List<String> permissionIds = Arrays.asList(
                "c4e9a010-38b8-4806-a5a9-8d07828f20a2",
                "4dc28c20-de5d-467a-8478-24f2532b2463"
        );

        addRoleDTO.setPermissionIds(permissionIds);

        Role inactive_role = roleService.addRole(addRoleDTO, "admin");
        assertThat(Status.ACTIVE.getLabel()).isEqualTo(inactive_role.getStatus());
        assertThat(inactive_role.getPriority()).isEqualTo(100);
        assertThat(inactive_role.getRolePermissions().stream().filter(
                rolePermission -> rolePermission.getPermission().getId().equals("c4e9a010-38b8-4806-a5a9-8d07828f20a2") ||
                rolePermission.getPermission().getId().equals("4dc28c20-de5d-467a-8478-24f2532b2463")
        		).count()).isEqualTo(2);
    }

    @Test
    @Transactional
    public void updateRole_userModifyNotFound_thenThrow() {

        UpdateRoleDTO updateRoleDTO = UpdateRoleDTO.builder()
                .roleId("user_role")
                .name("Test update role")
                .displayName("Update role")
                .build();

        List<String> permissionIds = Arrays.asList(
                "c4e9a010-38b8-4806-a5a9-8d07828f20a2",
                "4dc28c20-de5d-467a-8478-24f2532b2463"
        );

        updateRoleDTO.setPermissionIds(permissionIds);

        assertThrows(UserManagementBusinessException.class,
                () -> roleService.updateRole(updateRoleDTO, "not found"));
    }

    @Test
    @Transactional
    public void updateRole_roleNotFound_thenThrow() {

        UpdateRoleDTO updateRoleDTO = UpdateRoleDTO.builder()
                .roleId("not_found")
                .name("Test update role")
                .displayName("Update role")
                .build();

        List<String> permissionIds = Arrays.asList(
                "c4e9a010-38b8-4806-a5a9-8d07828f20a2",
                "4dc28c20-de5d-467a-8478-24f2532b2463"
        );

        updateRoleDTO.setPermissionIds(permissionIds);

        assertThrows(UserManagementBusinessException.class,
                () -> roleService.updateRole(updateRoleDTO, "admin"));
    }

    @Test
    @Transactional
    public void updateRole_roleFoundButUpdateNameDuplicate_thenThrow() {

        UpdateRoleDTO updateRoleDTO = UpdateRoleDTO.builder()
                .roleId("user")
                .name("Administrator")
                .displayName("Update role")
                .build();

        List<String> permissionIds = Arrays.asList(
                "c4e9a010-38b8-4806-a5a9-8d07828f20a2",
                "4dc28c20-de5d-467a-8478-24f2532b2463"
        );

        updateRoleDTO.setPermissionIds(permissionIds);

        assertThrows(UserManagementBusinessException.class,
                () -> roleService.updateRole(updateRoleDTO, "admin"));
    }

    @Test
    @Transactional
    public void updateRole_permissionNotFound_thenThrow() {

        UpdateRoleDTO updateRoleDTO = UpdateRoleDTO.builder()
                .roleId("user")
                .name("Administrator")
                .displayName("Update role")
                .build();

        List<String> permissionIds = Arrays.asList(
                "c4e9a010-38b8-4806-a5a9-8d07828f20a2",
                "4dc28c20-de5d-467a-8478-24f2532b246"
        );

        updateRoleDTO.setPermissionIds(permissionIds);

        assertThrows(UserManagementBusinessException.class,
                () -> roleService.updateRole(updateRoleDTO, "admin"));
    }

    @Test
    @Transactional
    public void updateRole_updateSuccessfully() {

        UpdateRoleDTO updateRoleDTO = UpdateRoleDTO.builder()
                .roleId("user_role")
                .name("user_update")
                .displayName("Update role")
                .build();

        List<String> permissionIds = Arrays.asList(
                "c4e9a010-38b8-4806-a5a9-8d07828f20a2",
                "4dc28c20-de5d-467a-8478-24f2532b2463"
        );

        updateRoleDTO.setPermissionIds(permissionIds);
        Role role = roleService.updateRole(updateRoleDTO, "admin");
        assertThat("user_role").isEqualTo(role.getId());
        assertThat("user_update").isEqualTo(role.getName());
        assertThat((int) role.getRolePermissions().stream().filter(
                rolePermission -> rolePermission.getPermission().getId().equals("c4e9a010-38b8-4806-a5a9-8d07828f20a2")
                        || rolePermission.getPermission().getId().equals("4dc28c20-de5d-467a-8478-24f2532b2463")
        ).count()).isEqualTo(2);
    }

    @Test
    @Transactional
    public void deleteRole_modifyUserNotFound_thenThrow() {

        assertThrows(UserManagementBusinessException.class,
                () -> roleService.deleteRole("admin_role", "not found"));
    }

    @Test
    @Transactional
    public void deleteRole_deleteSuccessfully() {
        roleService.deleteRole("admin_role", "admin");

        Role role = roleRepository.findById("admin_role").get();
        assertThat("admin_role").isEqualTo(role.getId());
        assertThat(Status.INACTIVE.getLabel()).isEqualTo(role.getStatus());
        assertThat("admin").isEqualTo(role.getModifiedBy());
        assertThat(role.getRolePermissions()).isEmpty();
        assertThat(role.getGroupRoles()).isEmpty();
        assertNull(groupRoleRepository.findByGroupIdAndRoleId("admin","admin_role").orElse(null));
        assertDoesNotThrow(() -> rolePermissionRepository.findAll().forEach(
                rolePermission -> {
                    if(rolePermission.getRole().getId().equals("admin_role")) {
						throw new RuntimeException();
					}
                }
        ));
    }

    @Test
    @Transactional
    public void deleteRole_roleNotFound_thenThrow() {

        assertThrows(UserManagementBusinessException.class,
                () -> roleService.deleteRole("not found", "admin"));
    }
}
