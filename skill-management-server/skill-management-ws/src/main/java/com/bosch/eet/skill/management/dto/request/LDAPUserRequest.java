package com.bosch.eet.skill.management.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LDAPUserRequest extends GenericRequestDTO {
	private static final long serialVersionUID = -352787762324394380L;

	private String country;

	private String city;

	private String displayName;

	@JsonProperty("sAmAccountName")
	private String sAmAccountName;

	private String mail;

	private String phonenumber;
}
