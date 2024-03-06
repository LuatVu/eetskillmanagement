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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.request.group.AddGroupDTO;
import com.bosch.eet.skill.management.dto.request.group.AddRolesToGroupDTO;
import com.bosch.eet.skill.management.dto.request.group.AddUsersToGroupDTO;
import com.bosch.eet.skill.management.dto.request.group.RemoveRoleFromGroupDTO;
import com.bosch.eet.skill.management.dto.request.group.RemoveUserFromGroupDTO;
import com.bosch.eet.skill.management.dto.request.group.UpdateGroupDTO;
import com.bosch.eet.skill.management.dto.response.AddUserToGroupResponseDTO;
import com.bosch.eet.skill.management.dto.response.GroupResponseDTO;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.GroupFacade;
import com.bosch.eet.skill.management.usermanagement.dto.GroupDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;

@SecurityRequirement(
        name = "spring_oauth"
)
@Slf4j
@RestController
@RequestMapping(value = Routes.URI_REST_GROUP)
public class GroupRest {

    @Autowired
    private GroupFacade groupFacade;

    @GetMapping
    public GenericResponseDTO<List<GroupResponseDTO>> getAllGroups() {
        return GenericResponseDTO.<List<GroupResponseDTO>>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(groupFacade.getAllGroups())
                .build();
    }

    @PostMapping
    @Operation(description = "Create Group")
    public GenericResponseDTO<GroupResponseDTO> createGroup(
            @Validated @RequestBody AddGroupDTO groupDetails, Authentication auth)  {

        GroupDTO groupDTO = AddGroupDTO.convertToGroupDTO(groupDetails);

        return GenericResponseDTO.<GroupResponseDTO>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(groupFacade.createGroup(groupDTO, auth.getName()))
                .build();
    }

    @GetMapping(value = "/{id}")
    public GenericResponseDTO<GroupResponseDTO> getGroupDetail(@PathVariable String id) {
        return GenericResponseDTO.<GroupResponseDTO>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(groupFacade.getGroupDetailsById(id))
                .build();
    }

    @PostMapping(value = "/{id}")
    public GenericResponseDTO<GroupResponseDTO> updateGroup(
            @PathVariable String id,
            @Validated @RequestBody UpdateGroupDTO groupDetails,
            Authentication auth) throws SkillManagementException {

        GroupDTO groupDTO = UpdateGroupDTO.convertToGroupDTO(id, groupDetails);

        return GenericResponseDTO.<GroupResponseDTO>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(groupFacade.updateGroup(groupDTO, auth.getName()))
                .build();
    }

    @PostMapping(value = Routes.URI_REST_GROUP_DELETE)
    public GenericResponseDTO<GroupResponseDTO> deleteGroup(
            @PathVariable String id,
            Authentication auth) {

        return GenericResponseDTO.<GroupResponseDTO>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(groupFacade.deleteGroup(id, auth.getName()))
                .build();
    }

    @PostMapping(value = Routes.URI_REST_GROUP_ADD_USERS + "/{id}")
    public GenericResponseDTO<AddUserToGroupResponseDTO> addUsersToGroup(
            @PathVariable String id,
            @Validated @RequestBody AddUsersToGroupDTO request,
            Authentication auth) {

//        StopWatch watch = new StopWatch();
//        watch.start();
        request.setGroupId(id);

        AddUserToGroupResponseDTO data = groupFacade.addUsersToGroup(request, auth.getName());
//        watch.stop();
//        log.info("LoginResponse:" + response.toJson());
//        log.info("Login API took " + watch.getTotalTimeMillis() + "ms ~= " + watch.getTotalTimeSeconds() + "s ~= ");

        return GenericResponseDTO.<AddUserToGroupResponseDTO>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(data)
                .build();

    }

    @PostMapping(value = Routes.URI_REST_GROUP_DEL_USERS + "/{id}")
    public GenericResponseDTO<GroupResponseDTO> removeUserFromGroup(
            @PathVariable String id,
            @Validated @RequestBody RemoveUserFromGroupDTO request,
            Authentication auth) {

        request.setGroupId(id);

        return GenericResponseDTO.<GroupResponseDTO>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(groupFacade.removeUserFromGroup(request.getGroupId(), request.getUserId(), auth.getName()))
                .build();
    }

    @PostMapping(value = Routes.URI_REST_GROUP_ADD_ROLES + "/{id}")
    public GenericResponseDTO<GroupResponseDTO> addRolesToGroup(
            @PathVariable String id,
            @Validated @RequestBody AddRolesToGroupDTO request,
            Authentication auth) {

        request.setGroupId(id);

        return GenericResponseDTO.<GroupResponseDTO>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(groupFacade.addRolesToGroup(request.getGroupId(), request.getRoles(), auth.getName()))
                .build();
    }

    @PostMapping(value = Routes.URI_REST_GROUP_DEL_ROLES + "/{id}")
    public GenericResponseDTO<GroupResponseDTO> removeRoleFromGroup(
            @PathVariable String id,
            @Validated @RequestBody RemoveRoleFromGroupDTO request,
            Authentication auth) {

        request.setGroupId(id);

        return GenericResponseDTO.<GroupResponseDTO>builder()
                .code(MessageCode.SUCCESS.toString())
                .timestamps(new Date())
                .data(groupFacade.removeRoleFromGroup(request.getGroupId(), request.getRoleId(), auth.getName()))
                .build();
    }
}