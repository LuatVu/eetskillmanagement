package com.bosch.eet.skill.management.dto.request.group;

import java.util.List;

import com.bosch.eet.skill.management.dto.request.LDAPDistributionListRequest;
import com.bosch.eet.skill.management.dto.request.LDAPUserRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddUsersToGroupDTO {

    private static final long serialVersionUID = 1L;

    private String groupId;

    private List<String> users;

    private List<LDAPUserRequest> ldapUsers;

    private List<LDAPDistributionListRequest> distributionLists;
}
