package com.bosch.eet.skill.management.dto.excel;

import java.time.LocalDate;

import com.bosch.eet.skill.management.common.Gender;
import com.bosch.eet.skill.management.usermanagement.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class XLSXUsersDTO {

    private User user;

    private String departmentName;

    private String grade;

    private String personalNumber;

    private Integer nonBoschExp;

    private Integer experienceAtBosch;

    private String title;

    private String team;

    private LocalDate joinDate;

    private Gender gender;

    private String location;
    
    private Boolean isUpdated;
}
