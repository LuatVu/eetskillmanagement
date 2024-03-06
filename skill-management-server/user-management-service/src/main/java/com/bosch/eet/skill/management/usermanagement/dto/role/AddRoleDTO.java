package com.bosch.eet.skill.management.usermanagement.dto.role;

import com.bosch.eet.skill.management.usermanagement.validator.IsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddRoleDTO {

    @NotBlank
    @Size(max = 45)
    private String name;

    @NotBlank
    @IsStatus
    private String status;

    private String displayName;

    @Size(max = 250)
    private String description;

	
	@Singular
	private List<String> permissionIds;

    private Integer priority;
}
