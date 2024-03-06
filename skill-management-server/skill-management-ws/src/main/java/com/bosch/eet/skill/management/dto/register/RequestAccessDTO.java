package com.bosch.eet.skill.management.dto.register;

import com.bosch.eet.skill.management.common.dto.GenericDTO;

import lombok.Builder;
import lombok.Data;

/**
 * @author GNY8HC
 *
 */
@Data
@Builder
public class RequestAccessDTO extends GenericDTO {

	private String username;
	private String reason;
}
