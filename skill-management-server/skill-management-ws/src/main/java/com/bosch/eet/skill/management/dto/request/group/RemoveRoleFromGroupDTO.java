package com.bosch.eet.skill.management.dto.request.group;

import javax.validation.constraints.NotBlank;

import com.bosch.eet.skill.management.dto.request.GenericRequestDTO;

import lombok.Data;

@Data
public class RemoveRoleFromGroupDTO extends GenericRequestDTO {
	private static final long serialVersionUID = 1L;

	private String groupId;

	@NotBlank
	private String roleId;
}
