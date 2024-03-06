package com.bosch.eet.skill.management.dto.request;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;

import com.bosch.eet.skill.management.enums.Level;
import com.bosch.eet.skill.management.enums.Location;
import com.bosch.eet.skill.management.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateSupplyDemandRequestDTO {

    @NotBlank
    String id;
    String projectId;
    String skillClusterId;
    String skill;
    Level level;
    Status status;
    String assignee;
    Boolean allowExternal;
    String assignNtId;
    String assignUserName;
    String candidateName;
    String supplyType;
    Location location;
    LocalDate expectedDate;
    String note;
    LocalDate forecastDate;
    LocalDate filledDate;

}
