package com.bosch.eet.skill.management.converter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.PicDto;
import com.bosch.eet.skill.management.dto.UserSoftDTO;
import com.bosch.eet.skill.management.dto.request.CreateSupplyDemandRequestDTO;
import com.bosch.eet.skill.management.dto.request.UpdateSupplyDemandRequestDTO;
import com.bosch.eet.skill.management.dto.response.ChangeHistoryResponseDTO;
import com.bosch.eet.skill.management.dto.response.SupplyDemandResponseDTO;
import com.bosch.eet.skill.management.entity.HistorySupplyDemand;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.entity.SupplyDemand;
import com.bosch.eet.skill.management.enums.Assignee;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Group;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.entity.UserGroup;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SupplyDemandConverter {
	
	@Autowired
	private ModelMapper mapper;
	
    public SupplyDemand convertToSupplyDemandEntity(CreateSupplyDemandRequestDTO dto) {
        return mapper.map(dto, SupplyDemand.class);
    }

    public SupplyDemand convertToSupplyDemandEntity(SupplyDemandResponseDTO dto) {
        return mapper.map(dto, SupplyDemand.class);
    }

    public List<SupplyDemandResponseDTO> convertToSupplyDemandResponseDTOs(List<SupplyDemand> entities) {
        return entities.stream().map(this::convertToSupplyDemandResponseDTO)
                .collect(Collectors.toList());
    }

    public SupplyDemandResponseDTO convertToSupplyDemandResponseDTO(SupplyDemand entity) {
        SupplyDemandResponseDTO dto = mapper.map(entity, SupplyDemandResponseDTO.class);
        dto.setSubId("DS-" + entity.getSubId());
        try {
            Project project = entity.getProject();
            dto.setProjectId(project.getId());
            dto.setProjectName(project.getName());
        } catch (EntityNotFoundException ex) {
            dto.setProjectId("-1");
            dto.setProjectName("Deleted");
        }

        try {
            SkillGroup skillGroup = entity.getSkillGroup();
            dto.setSkillClusterId(skillGroup.getId());
            dto.setSkillClusterName(skillGroup.getName());
        } catch (EntityNotFoundException ex) {
            dto.setSkillClusterId("-1");
            dto.setSkillClusterName("Deleted");
        }
        return dto;
    }

    public ChangeHistoryResponseDTO convertToChangeHistoryResDTO(HistorySupplyDemand entity) {
        return mapper.map(entity, ChangeHistoryResponseDTO.class);
    }

    public void convertToUpdatedEntity(SupplyDemand entity, UpdateSupplyDemandRequestDTO dto) {
        mapper.map(dto, entity);
        if (StringUtils.isBlank(dto.getAssignNtId())) {
            entity.setAssignNtId(null);
            entity.setAssignUserName(null);
        }
    }

    public HistorySupplyDemand convertToHistorySupplyEntity(UpdateSupplyDemandRequestDTO dto) {
        return mapper.map(dto, HistorySupplyDemand.class);
    }

    public HistorySupplyDemand convertToHistoryEntity(CreateSupplyDemandRequestDTO dto, UserDTO userDTO) {
        HistorySupplyDemand entity = mapper.map(dto, HistorySupplyDemand.class);
        entity.setUpdatedByNtid(userDTO.getName());
        entity.setUpdatedByName(userDTO.getDisplayName());
        return entity;
    }

    public List<ChangeHistoryResponseDTO> convertToChangeHistoryResDTOs(List<HistorySupplyDemand> entities) {
        Type listType = new TypeToken<List<ChangeHistoryResponseDTO>>() {
        }.getType();
        return mapper.map(entities, listType);
    }

    public List<PicDto> convertToPicDtos(List<Group> entities) {
        List<PicDto> dtos = new ArrayList<>();
        PicDto emp = new PicDto();
        emp.setId("-1");
        emp.setName(Assignee.UNASSIGNEED.getValue());
        dtos.add(emp);
        for (Group entity : entities) {
            PicDto dto = new PicDto();
            dto.setId(entity.getId());
            dto.setName(entity.getDescription());
            List<User> users = entity.getUsersGroup().stream().map(UserGroup::getUser).collect(Collectors.toList());
            dto.setUsers(convertToUserDtos(users));
            dtos.add(dto);
        }
        return dtos;
    }

    public List<UserSoftDTO> convertToUserDtos(List<User> users) {
        return users.stream().map(i -> UserSoftDTO.builder()
                .id(i.getId())
                .ntId(i.getName())
                .name(i.getDisplayName()).build()).collect(Collectors.toList());
    }
}
