package com.bosch.eet.skill.management.usermanagement.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "group_role")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupRole {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = true)
    private Group group;

    @ManyToOne
    @JoinColumn(name="role_id", nullable=true)
    private Role role;

    public void removeGroupRoleFromGroup(Group group){
        group.getGroupRoles().remove(this);
    }

    public void removeGroupRoleFromRole(Role role){
        role.getGroupRoles().remove(this);
    }
}