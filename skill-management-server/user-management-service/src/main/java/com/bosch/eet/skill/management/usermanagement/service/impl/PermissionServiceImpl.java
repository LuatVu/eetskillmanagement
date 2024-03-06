package com.bosch.eet.skill.management.usermanagement.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.common.ObjectMapperUtils;
import com.bosch.eet.skill.management.usermanagement.consts.MessageCode;
import com.bosch.eet.skill.management.usermanagement.converter.PermissionConverter;
import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Permission;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementBusinessException;
import com.bosch.eet.skill.management.usermanagement.repo.PermissionRepository;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;
import com.bosch.eet.skill.management.usermanagement.service.PermissionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepo;
    private final PermissionConverter converter;

    @Autowired
    UserRepository userRepo;

    
    @Autowired
    @Qualifier("CoreUserNamagementMessageSource")
    private MessageSource messageSource;


    @Transactional(readOnly = true)
    @Override
    public PermissionDTO findPermissionByName(String namePermission) {
        try {
            Permission entity = permissionRepo.findByName(namePermission);
            return converter.convertEntityToDTO(entity);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            return null;
        }
    }

    public List<PermissionDTO> getAllPermissions() {
        List<Permission> entities = (List<Permission>) permissionRepo.findAll();
        return converter.convertEntitiesToDTOs(entities);
    }

    @Override
    public List<PermissionDTO> findAllBelongRoles(final List<String> roleIds) {
        final List<Permission> entities = permissionRepo.findAllByRoles(roleIds);
        return ObjectMapperUtils.mapAll(entities, PermissionDTO.class);
    }

    @Override
    public List<Permission> findAllPermission() {
        return (List<Permission>) permissionRepo.findAll();
    }
    
    @Override
    @Transactional
	public PermissionDTO updatePermission(PermissionDTO permissionDto) {
    	if(StringUtils.isBlank(permissionDto.getId())) {
    		throw new UserManagementBusinessException(
                    MessageCode.MISSING_PERMISSION_ID.toString(),
                    messageSource.getMessage(
                            MessageCode.MISSING_PERMISSION_ID.toString(),
                            null,
                            LocaleContextHolder.getLocale()),
                    null
            );
    	}
    	
    	Optional<Permission> permissionOpt = permissionRepo.findById(permissionDto.getId());
    	if(!permissionOpt.isPresent()) {
    		throw new UserManagementBusinessException(
                    MessageCode.PERMISSION_NOT_FOUND.toString(),
                    messageSource.getMessage(
                            MessageCode.PERMISSION_NOT_FOUND.toString(),
                            null,
                            LocaleContextHolder.getLocale()),
                    null
            );
    	}
    	
    	Permission dbPermission = permissionOpt.get();
    	dbPermission.setName(permissionDto.getName().trim());
    	dbPermission.setDescription(permissionDto.getDescription().trim());
    	dbPermission.setModifiedBy(permissionDto.getModifiedBy());
    	dbPermission.setModifiedDate(LocalDateTime.now());
    	
    	Permission resultPermission = permissionRepo.save(dbPermission);
    	return converter.convertEntityToDTO(resultPermission);
    }
}
