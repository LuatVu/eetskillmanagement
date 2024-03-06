/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.ldap.service.impl;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.bosch.eet.skill.management.ldap.common.Constants;
import com.bosch.eet.skill.management.ldap.exception.LdapException;
import com.bosch.eet.skill.management.ldap.model.LdapInfo;
import com.bosch.eet.skill.management.ldap.service.LdapService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LUK1HC
 */

@Slf4j
@Component
public class LdapServiceImpl implements LdapService {

    @Autowired
    private Environment env;

    @Autowired
    private LdapContextSource contextSource;

    @Autowired
    private LdapTemplate ldapTemplate;

    private boolean authenticate(final String ldapUrl, String principal, final String credentials) throws LdapException {

        // Case: User doesn't input domain(area)
        if (!principal.contains(Constants.BACKSLASH)) {
            // Get domain(area) from LDAP system
            Optional<LdapInfo> optLdapInfo = getPrincipalInfo(principal);
            if (optLdapInfo.isPresent()) {
                final LdapInfo ldapInfo = optLdapInfo.get();
                StringBuilder sb = new StringBuilder();
                sb.append(ldapInfo.getArea());
                sb.append(Constants.BACKSLASH);
                sb.append(principal);
                principal = sb.toString();
            }
        }

        contextSource.setUrl(ldapUrl);
        contextSource.afterPropertiesSet();
        try {
            contextSource.getContext(principal, credentials);
        } catch (AuthenticationException ex) {
            log.error(ex.getMessage());
            throw new LdapException("INVALID_USERNAME_PASSWORD", "Invalid username or password.", ex.getCause());
        } catch (Exception ex) {
            throw new LdapException("INVALID_USERNAME_PASSWORD", "Invalid username or password.", ex.getCause());
        }
        return true;
    }

    @Override
    public boolean authenticate(final String principal, final String credentials) throws LdapException {
        return authenticate(env.getRequiredProperty("ldap.url"), principal, credentials);
    }

    @Override
    public Optional<LdapInfo> getPrincipalInfo(final String principal) throws LdapException {
        LdapInfo ldapInfo = null;
        try {
            List<LdapInfo> ldapInfos = ldapTemplate.search(query().where(Constants.OBJECT_CLASS).is(Constants.PERSON).and(Constants.CN).is(principal), new PersonAttributesMapper());
            if (!CollectionUtils.isEmpty(ldapInfos)) {
                ldapInfo = ldapInfos.iterator().next();
                log.info(ldapInfo.toString());
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new LdapException("CAN_NOT_CONNECT_LDAP_SERVER", "Can not connect to LDAP Server.", ex.getCause());
        }
        return Optional.ofNullable(ldapInfo);
    }

    @Override
    public Optional<LdapInfo> getPrincipalInfoByDisplayName(final String displayName) {
        LdapInfo ldapInfo = null;
        List<LdapInfo> ldapUsers = ldapTemplate.search(query().where(Constants.OBJECT_CLASS).is(Constants.PERSON).and(Constants.DISPLAY_NAME).is(displayName), new PersonAttributesMapper());
        if (!CollectionUtils.isEmpty(ldapUsers)) {
            ldapInfo = ldapUsers.iterator().next();
            log.info(ldapInfo.toString());
        }
        return Optional.ofNullable(ldapInfo);
    }

    @Override
    public Optional<LdapInfo> getGroupInfo(final String group) {
        LdapInfo ldapInfo = null;
        List<LdapInfo> ldapUsers = ldapTemplate.search(query().where(Constants.OBJECT_CLASS).is(Constants.GROUP).and(Constants.DISPLAY_NAME).is(group)
                , new PersonAttributesMapper());
        if (!CollectionUtils.isEmpty(ldapUsers)) {
            ldapInfo = ldapUsers.iterator().next();
            log.info(ldapInfo.toString());
        }
        return Optional.ofNullable(ldapInfo);
    }

    @Override
    public List<LdapInfo> search(final String keyword) {
        ContainerCriteria containerCriteria = LdapQueryBuilder.query().where(Constants.OBJECT_CLASS).is(Constants.PERSON);
        ContainerCriteria containerCriteriaDisplayName = LdapQueryBuilder.query().where(Constants.DISPLAY_NAME).like(wildcardize(keyword));
        return ldapTemplate.search(query().where(Constants.CN).like(wildcardize(keyword))
                        .or(containerCriteriaDisplayName)
                        .and(containerCriteria)
                , new PersonAttributesMapper());
    }

    @Override
    public List<LdapInfo> searchGroup(final String keyword) {
        ContainerCriteria containerCriteriaGroup = LdapQueryBuilder.query().where(Constants.OBJECT_CLASS).is(Constants.GROUP);
        ContainerCriteria containerCriteriaDisplayName = LdapQueryBuilder.query().where(Constants.DISPLAY_NAME).like(wildcardize(keyword));
        return ldapTemplate.search(query().where(Constants.CN).like(wildcardize(keyword))
                        .or(containerCriteriaDisplayName)
                        .and(containerCriteriaGroup),
                new PersonAttributesMapper());
    }

    private String wildcardize(String keyword) {
        if (StringUtils.isEmpty(keyword)) {
            return keyword;
        }
        return String.format("%1$s*", keyword.replace(Constants.ASTERISK, ""));
    }

    @Override
    public Set<LdapInfo> getChildGroups(final String groupDisplayName) {
        Set<LdapInfo> nestedChildGroups = new HashSet<>();
        final Set<LdapInfo> results = getChildGroups(groupDisplayName, nestedChildGroups);
        return results;
    }

    private Set<LdapInfo> getChildGroups(final String displayName, Set<LdapInfo> members) {
        List<String> results = ldapTemplate.search(query().where(Constants.OBJECT_CLASS).is(Constants.GROUP)
                        .and(query().where(Constants.DISPLAY_NAME).is(displayName))
                , (AttributesMapper<String>) attributes -> attributes.get(Constants.DISTINGGUISHED_NAME).get().toString());
        return !CollectionUtils.isEmpty(results) ? getChildGroupsRecursive(results.get(0), null, members) : Collections.<LdapInfo>emptySet();
    }

    @Override
    public List<LdapInfo> getPrincipalInfos(List<String> principals) {
        if(principals.isEmpty()) {
			return Collections.emptyList();
		}
        ContainerCriteria queryToFilterUserFromPrincipals = query().where(Constants.CN).is(principals.get(0));
        principals.forEach(
                principal -> queryToFilterUserFromPrincipals.or(Constants.CN).is(principal)
        );
        ContainerCriteria finalQuery = query().where(Constants.OBJECT_CLASS).is(Constants.PERSON).and(queryToFilterUserFromPrincipals);
        return ldapTemplate.search(finalQuery, new PersonAttributesMapper());
    }

    private Set<LdapInfo> getChildGroupsRecursive(final String dn, final String parentDN, Set<LdapInfo> members) {
        final List<LdapInfo> results = ldapTemplate.search(query().where(Constants.MEMBER_OF).is(dn)
                        .and(query().where(Constants.OBJECT_CLASS).is(Constants.GROUP)) // only filter groups
                , new PersonAttributesMapper()
        );
        for (LdapInfo result : results) {
            String distinguishedName = result.getDistinguishedName();
            if (!(distinguishedName.equals(parentDN) //circular, ignore
                    || members.contains(result) //duplicate, ignore
            )) {
                getChildGroupsRecursive(distinguishedName, dn, members);
            }
        }
        members.addAll(results);
        return members;
    }

}