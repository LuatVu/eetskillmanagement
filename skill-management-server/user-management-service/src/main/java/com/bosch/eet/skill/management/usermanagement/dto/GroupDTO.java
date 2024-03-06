package com.bosch.eet.skill.management.usermanagement.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String groupId;

	@NotBlank
	private String groupName;

	private String displayName;

	private String description;

	private String status;

	private List<UserDTO> users;

	private List<UserDTO> distributionlists;

	private List<RoleDTO> roles;
}
