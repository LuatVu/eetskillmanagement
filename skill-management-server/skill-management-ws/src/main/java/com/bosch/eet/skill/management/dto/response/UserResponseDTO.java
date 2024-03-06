package com.bosch.eet.skill.management.dto.response;

import java.io.Serializable;
import java.util.List;

import com.bosch.eet.skill.management.usermanagement.dto.PermissionDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO implements Serializable {

    private String id;

    private String name;

    private String displayName;

    private String email;

    private String status;

    private String createdBy;

    private String type;

    private boolean isDistributionList;

    private List<PermissionDTO> permissions;
}
