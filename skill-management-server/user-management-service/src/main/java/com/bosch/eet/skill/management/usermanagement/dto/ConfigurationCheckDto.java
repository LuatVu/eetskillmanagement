package com.bosch.eet.skill.management.usermanagement.dto;

import java.io.Serializable;

import com.bosch.eet.skill.management.common.JsonUtils;

import lombok.Data;

@Data
public class ConfigurationCheckDto implements Serializable{
private static final long serialVersionUID = 1L;
	
	private String configurationId;
	private Boolean isChecked;	
	private String userId;	
	
	public String toJson() {
		return JsonUtils.convertToString(this);
	}
}
