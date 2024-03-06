/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.dto;

import java.io.Serializable;

import com.bosch.eet.skill.management.common.JsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author LUK1HC
 *
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("access_token")
    private String accessToken;
	
    @JsonProperty("token_type")
    private String tokenType;
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    @JsonProperty("expires_in")
    private Integer expiresIn;
    
    @JsonProperty("scope")
    private String scope;
    
	public String toJson() {
		return JsonUtils.convertToString(this);
	}
    
}
