package com.bosch.eet.skill.management.dto.response;

import java.util.List;

import com.bosch.eet.skill.management.ldap.model.LdapInfo;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchUserResponseDTO {

    List<UserDTO> userDTOList;

    List<LdapInfo> ldapInfos;
}
