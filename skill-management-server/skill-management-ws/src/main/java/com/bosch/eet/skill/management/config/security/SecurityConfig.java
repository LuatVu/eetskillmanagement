/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.config.security;

import java.util.Collections;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.enums.Layout;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * @author LUK1HC
 */
@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SSOAuthenticationProvider ssoAuthenticationProvider;

    @Resource(name = "customUserDetailsService")
    private UserDetailsService userDetailsService;

    private static final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
            new AntPathRequestMatcher(Routes.URI_REST_HOME),
            new AntPathRequestMatcher(Routes.URI_REST_LOGIN),
            new AntPathRequestMatcher(Routes.URI_REST_REQUEST_ACCESS),
            new AntPathRequestMatcher(Routes.URI_REST_LOGIN_SSO),
            new AntPathRequestMatcher(Routes.URI_REST_LOGOUT),
            new AntPathRequestMatcher(Routes.URI_REST_DENY),
            new AntPathRequestMatcher(Routes.URI_REST_ERROR),
            new AntPathRequestMatcher(Routes.URI_REST_REFRESH_TOKEN),
            new AntPathRequestMatcher("/oauth/logout"),
            new AntPathRequestMatcher("/fonts/**"),
            new AntPathRequestMatcher(Routes.URI_REST_PROJECT_PORTFOLIO_DETAIL, HttpMethod.GET.name()),
            new AntPathRequestMatcher(Routes.URI_REST_LAYOUT + "/" + Layout.OVERVIEW_LAYOUT, HttpMethod.GET.name()),
            new AntPathRequestMatcher(Routes.URI_REST_LAYOUT + "/" + Layout.ORGCHART_LAYOUT, HttpMethod.GET.name()),
            new AntPathRequestMatcher(Routes.URI_REST_LAYOUT + "/" + Layout.HELP_LAYOUT, HttpMethod.GET.name()),
            new AntPathRequestMatcher(Routes.URI_REST_LAYOUT + "/" + Layout.RELEASE_NOTE_LAYOUT, HttpMethod.GET.name()),
            new AntPathRequestMatcher(Routes.URI_REST_PROJECT_SCOPE, HttpMethod.GET.name()),
            new AntPathRequestMatcher(Routes.URI_REST_PROJECT_FILTER, HttpMethod.GET.name()),
            new AntPathRequestMatcher(Routes.URI_REST_SKILL_TAG, HttpMethod.GET.name()),
            new AntPathRequestMatcher(Routes.URI_REST_GB, HttpMethod.GET.name()),
            new AntPathRequestMatcher(Routes.URI_REST_PROJECT_FILTER_CUSTOMER_GB, HttpMethod.GET.name()),
            new AntPathRequestMatcher(Routes.URI_REST_PHASE, HttpMethod.GET.name()),
            new AntPathRequestMatcher(Routes.URI_REST_ELASTIC_QUERY.replace("{index_name}", Constants.PROJECT_INDEX),
                    HttpMethod.POST.name()),
            new AntPathRequestMatcher(Routes.URI_REST_ELASTIC_QUERY.replace("{index_name}", Constants.CUSTOMER_INDEX),
                    HttpMethod.POST.name()),
            new AntPathRequestMatcher(Routes.URI_REST_PROJECT_CUSTOMER_GB_DETAIL, HttpMethod.GET.name()),
            new AntPathRequestMatcher(Routes.UIR_REST_PROJECT_PORTFOLIO, HttpMethod.GET.name()),

            // swagger
            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/v1/swagger-ui/**"),
            new AntPathRequestMatcher("/v1/swagger-ui.html"),
            new AntPathRequestMatcher("/v1/swagger-ui.html#/**")
    );


    @Override
    public void configure(WebSecurity web) {
        web
                .httpFirewall(allowSemicolonHttpFirewall())
                .ignoring()
                .requestMatchers(PUBLIC_URLS)
        ;
    }

    @Bean
    public HttpFirewall allowSemicolonHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        return firewall;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @SuppressWarnings("deprecation")
    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth
        //        .authenticationProvider(customAuthenticationProvider);
                .authenticationProvider(ssoAuthenticationProvider);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(
                                "spring_oauth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .description("Oauth2 flow")
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .security(Collections.singletonList(
                        new SecurityRequirement().addList("spring_oauth")));
    }


}
