package com.bosch.eet.skill.management.config.security;


import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.ldap.service.LdapService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SSOAuthenticationProvider implements AuthenticationProvider {
//    @Autowired
//    private UserDetailsService userDetailsService;
//    @Autowired
//    private LdapService ldapService;
//
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        final String userName = authentication.getName();
//        log.info("user: " + userName + " is login...");
//        try {
//            // Verify user exists database system or not ?
//            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
//            ldapService.getPrincipalInfo(userName);
//            log.info("User: " + userName + " with granted authorities: " + userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
//            return new UsernameSSOAuthenticationToken(userName, userDetails.getAuthorities());
//        } catch (Exception e) {
//            log.error(e.getLocalizedMessage(), e);
//            throw new BadCredentialsException(e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return authentication.equals(UsernameSSOAuthenticationToken.class);
//    }

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private LdapService ldapService;

    @SuppressWarnings("unchecked")
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Calling CustomAuthenticationProvider.authenticate().");
        final String userName = authentication.getName().toUpperCase();
        log.info("user: " + userName + " is login...");
        try {
            // Verify user exists database system or not ?
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            ldapService.getPrincipalInfo(userName);
            log.info("User: " + userName + " with granted authorities: " + userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
            return new UsernameSSOAuthenticationToken(userName, userDetails.getAuthorities());
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new BadCredentialsException(e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}