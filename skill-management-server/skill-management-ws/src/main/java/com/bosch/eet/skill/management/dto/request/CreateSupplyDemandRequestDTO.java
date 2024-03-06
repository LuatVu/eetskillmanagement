package com.bosch.eet.skill.management.dto.request;

import java.time.LocalDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.bosch.eet.skill.management.enums.Level;
import com.bosch.eet.skill.management.enums.Location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSupplyDemandRequestDTO {
    @NotBlank
    String projectId;

    String skillClusterId;

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

    @NotNull
    @Min(1)
    Integer quantity;

    String note;

}
