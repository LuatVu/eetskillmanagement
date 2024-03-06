package com.bosch.eet.skill.management.rest;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.response.RoleResponseDTO;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.facade.RoleFacade;
import com.bosch.eet.skill.management.usermanagement.dto.role.AddRoleDTO;
import com.bosch.eet.skill.management.usermanagement.dto.role.UpdateRoleDTO;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@SecurityRequirement(
        name = "spring_oauth"
)
public class RoleRest {

    @Autowired
    private RoleFacade roleFacade;

    @GetMapping(Routes.URI_REST_ROLE)
    public GenericResponseDTO<List<RoleResponseDTO>> getAll() {
        return GenericResponseDTO.<List<RoleResponseDTO>>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(roleFacade.getAllRoles())
                .build();
    }

    @GetMapping(value = Routes.URI_REST_ROLE + "/{id}")
    public GenericResponseDTO<RoleResponseDTO> getDetail(@PathVariable final String id) {
        return GenericResponseDTO.<RoleResponseDTO>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(roleFacade.getRoleDetail(id))
                .build();
    }

    @PostMapping(Routes.URI_REST_ROLE)
    public GenericResponseDTO<RoleResponseDTO> createRole(
            Authentication auth,
            @Validated @RequestBody final AddRoleDTO roleDTO) {

        return GenericResponseDTO.<RoleResponseDTO>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(roleFacade.addRole(roleDTO, auth.getName()))
                .build();
    }

    @PostMapping(Routes.URI_REST_ROLE + "/{id}")
    public GenericResponseDTO<RoleResponseDTO> updateRole(
            @PathVariable String id,
            @Validated @RequestBody final UpdateRoleDTO roleDTO,
            Authentication auth) {

        return GenericResponseDTO.<RoleResponseDTO>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(
                        roleFacade.updateRole(id, roleDTO, auth.getName())
                )
                .build();
    }

    @PostMapping(Routes.URI_REST_DELETE_ROLE)
    public GenericResponseDTO<RoleResponseDTO> deleteRole(@PathVariable String id, Authentication auth) {
        roleFacade.deleteRole(id, auth.getName());
        return GenericResponseDTO.<RoleResponseDTO>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .build();
    }
}
