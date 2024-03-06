package com.bosch.eet.skill.management.config.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class UsernameSSOAuthenticationToken extends UsernamePasswordAuthenticationToken {

	private static final long serialVersionUID = 1L;

	public UsernameSSOAuthenticationToken(String username, Collection<? extends GrantedAuthority> authorities) {
		super(username, "" , authorities);
	}

}
