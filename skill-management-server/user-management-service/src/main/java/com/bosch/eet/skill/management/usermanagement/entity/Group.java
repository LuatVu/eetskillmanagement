package com.bosch.eet.skill.management.usermanagement.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "`group`")
public class Group {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(name = "name", length = 45)
    private String name;

    @Column(name = "display_name", length = 45)
    private String displayName;

    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "status", nullable = false, length = 45)
    @NotNull
    private String status;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "modified_by", insertable = false)
    private String modifiedBy;

    @OneToMany(mappedBy = "group", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH})
    private List<UserGroup> userGroups;

    @OneToMany(mappedBy = "group", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH})
    private List<GroupRole> groupRoles;

    public List<GroupRole> getGroupRoles() {
        return CollectionUtils.isEmpty(groupRoles) ? new ArrayList<>() : groupRoles;
    }

    public List<UserGroup> getUsersGroup() {
        return CollectionUtils.isEmpty(userGroups) ? new ArrayList<>() : userGroups;
    }

    public void removeUserFromUserGroup(String userName) {
        userGroups.removeIf(userGroup -> userName.equals(userGroup.getUser().getName()));
    }

    public void removeRoleFromGroupRole(String roleId) {
        groupRoles.removeIf(groupRole -> roleId.equals(groupRole.getRole().getId()));
    }
}