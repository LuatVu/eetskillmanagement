package com.bosch.eet.skill.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileStorageDTO {

	private static final long serialVersionUID = -179078416201806522L;

	private String id;
	private String name;
	private String extension;
	private Long size;
	private String uri;
	private String token;
	private String type;
	private boolean deleted;
}
