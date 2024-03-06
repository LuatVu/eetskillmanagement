package com.bosch.eet.skill.management.usermanagement.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_group")
public class UserGroup implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @ManyToOne
    private User user;

    //bi-directional many-to-one association to Role
    @ManyToOne
    private Group group;

    public void removeUserGroupFromGroup(Group group){
        List<UserGroup> usersGroup = group.getUsersGroup();
        usersGroup.remove(this);
    }

    public void removeUserGroupFromUser(User user){
        List<UserGroup> userGroups = user.getUserGroup();
        userGroups.remove(this);
    }
}
