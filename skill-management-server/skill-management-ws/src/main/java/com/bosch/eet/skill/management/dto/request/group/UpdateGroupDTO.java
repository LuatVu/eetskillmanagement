package com.bosch.eet.skill.management.dto.request.group;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.bosch.eet.skill.management.usermanagement.dto.GroupDTO;
import com.bosch.eet.skill.management.usermanagement.validator.IsStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupDTO implements Serializable {

    @NotBlank
    @Size(max = 45)
    private String groupName;

    private String displayName;

    @Size(max = 250)
    private String description;

    @IsStatus
    private String status;

    public static GroupDTO convertToGroupDTO(String groupId, UpdateGroupDTO updateGroupDTO){

        return GroupDTO.builder()
                .groupId(groupId)
                .groupName(updateGroupDTO.getGroupName())
                .displayName(updateGroupDTO.getDisplayName())
                .description(updateGroupDTO.getDescription())
                .status(updateGroupDTO.getStatus())
                .build();
    }
}
