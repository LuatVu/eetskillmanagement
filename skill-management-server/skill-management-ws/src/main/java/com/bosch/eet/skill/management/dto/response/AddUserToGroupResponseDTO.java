package com.bosch.eet.skill.management.dto.response;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddUserToGroupResponseDTO {

    private GroupResponseDTO groupResponseDTO;

    private Set<String> failUsers;
}
