/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.usermanagement.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.CollectionUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author LUK1HC
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "`user`")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @NotNull
    private String name;

    @Column(name = "display_name")
    private String displayName;

    private String email;

    @NotNull
    @Column(name = "`type`")
    private String type;

    @NotNull
    @Column(name = "status")
    private String status;

    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "created_date", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_by", insertable = false)
    private String modifiedBy;

    @Column(name = "modified_date", insertable = false)
    private LocalDateTime modifiedDate;

    //bi-directional many-to-one association to MemberRole
    @OneToMany(mappedBy = "user")
    private List<UserRole> userRoles;

    //bi-directional many-to-one association to UserToolRole
    @OneToMany(mappedBy = "user")
    private List<UserToolRole> userToolRoles;

    @OneToMany(mappedBy = "user")
    private List<UserGroup> userGroup;

    public List<UserGroup> getUserGroup() {
        return CollectionUtils.isEmpty(userGroup) ? new ArrayList<>() : userGroup;
    }
}
