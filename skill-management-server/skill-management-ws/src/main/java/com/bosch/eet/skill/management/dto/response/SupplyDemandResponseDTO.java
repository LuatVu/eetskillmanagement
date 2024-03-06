package com.bosch.eet.skill.management.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class SupplyDemandResponseDTO {
    String id;
    String subId;
    String projectId;
    Status status;
    String projectName;
    String skillClusterId;
    String skillClusterName;
    String skill;
    Level level;
    String assignee;
    String assignNtId;
    String assignUserName;
    String candidateName;
    Boolean allowExternal;
    String supplyType;
    Location location;
    LocalDate expectedDate;
    String createdByNtid;
    String createdByName;
    LocalDateTime createdDate;
    String updatedByName;
    String updatedByNtid;
    LocalDateTime updatedDate;
    String note;
    Boolean canUpdate;
    Boolean canDelete;
    LocalDate forecastDate;
    LocalDate filledDate;

}
