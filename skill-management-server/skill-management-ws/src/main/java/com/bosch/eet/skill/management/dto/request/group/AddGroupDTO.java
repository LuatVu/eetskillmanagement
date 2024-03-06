package com.bosch.eet.skill.management.dto.request.group;

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
public class AddGroupDTO {

    @NotBlank
    @Size(max = 45)
    private String groupName;

    private String displayName;

    @Size(max = 250)
    private String description;

    @IsStatus
    private String status;

    public static GroupDTO convertToGroupDTO(AddGroupDTO addGroupDTO){

        return GroupDTO.builder()
                .groupName(addGroupDTO.getGroupName())
                .displayName(addGroupDTO.getDisplayName())
                .description(addGroupDTO.getDescription())
                .status(addGroupDTO.getStatus())
                .build();
    }
}
