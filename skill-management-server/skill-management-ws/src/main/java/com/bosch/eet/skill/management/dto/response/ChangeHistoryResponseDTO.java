package com.bosch.eet.skill.management.dto.response;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ChangeHistoryResponseDTO {
    String updatedByName;

    Date updatedDate;

    String oldStatus;

    String newStatus;

    String candidateName;

    String assignee;

    String assignUserName;

    String supplyType;

    String note;
}
