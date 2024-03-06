package com.bosch.eet.skill.management.dto.request.group;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.bosch.eet.skill.management.dto.request.GenericRequestDTO;

import lombok.Data;

@Data
public class AddRolesToGroupDTO extends GenericRequestDTO {

	private static final long serialVersionUID = 1L;

	private String groupId;

	@NotEmpty
	private List<String> roles;
}
