package com.bosch.eet.skill.management.dto;

import java.util.Date;

import com.bosch.eet.skill.management.common.dto.GenericDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponseDTO<T> extends GenericDTO {
	private static final long serialVersionUID = 1L;
	
	private T data;
	private String code;
	private String message;
	private Date timestamps;
}
