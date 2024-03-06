package com.bosch.eet.skill.management.usermanagement.dto;

import java.io.Serializable;

import com.bosch.eet.skill.management.common.JsonUtils;

import lombok.Data;

@Data
public class ToolRoleDto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String code;
	
	private String name;
	
	private Boolean isChecked;
	
	public String toJson() {
		return JsonUtils.convertToString(this);
	}
}
