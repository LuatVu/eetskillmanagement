package com.bosch.eet.skill.management.dto.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserSession implements Serializable {

	@JsonProperty("access_token")
	private String accessToken;
	
	@JsonProperty("refresh_token")
	private String refreshToken;
	
	private String tokenType;
	
	@JsonProperty("expires_in")
	private int expiresIn;
}
