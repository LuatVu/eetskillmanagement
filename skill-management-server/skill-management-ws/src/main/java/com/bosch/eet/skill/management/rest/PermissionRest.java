package com.bosch.eet.skill.management.rest;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.Date;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.response.AllPermissionsResponseDTO;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.PermissionFacade;
import com.bosch.eet.skill.management.usermanagement.consts.MessageCode;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
//@RequestMapping(Routes.URI_REST_PERMISSION)
@SecurityRequirement(
        name = "spring_oauth"
)
public class PermissionRest {

    @Autowired
    private PermissionFacade permissionFacade;
    
    @Autowired
    private MessageSource messageSource;

    @GetMapping(value = Routes.URI_REST_PERMISSION)
    public GenericResponseDTO<AllPermissionsResponseDTO> getAll() {

        GenericResponseDTO<AllPermissionsResponseDTO> response = new GenericResponseDTO<>();
        response.setData(permissionFacade.getAllPermissions());
        response.setCode(MessageCode.SUCCESS.toString());
        response.setTimestamps(new Date());
        return response;
    }
    

    @PostMapping(value = Routes.URI_REST_PERMISSION_UPDATE)
    public GenericResponseDTO<PermissionDTO> updatePermission(@PathVariable(name = "permission_id") String permissionId,
												            @RequestBody @Valid PermissionDTO permissionDto,
												            Authentication auth) {

    	if (StringUtils.isBlank(permissionId)) {
            throw new SkillManagementException(MessageCode.MISSING_PERMISSION_ID.toString(),
                    messageSource.getMessage(MessageCode.MISSING_PERMISSION_ID.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
        }
    	
    	permissionDto.setId(permissionId);
    	permissionDto.setModifiedBy(auth.getName());
    	
        GenericResponseDTO<PermissionDTO> response = new GenericResponseDTO<>();
        response.setData(permissionFacade.updatePermission(permissionDto));
        response.setCode(MessageCode.SUCCESS.toString());
        response.setTimestamps(new Date());
        return response;
    }
}
