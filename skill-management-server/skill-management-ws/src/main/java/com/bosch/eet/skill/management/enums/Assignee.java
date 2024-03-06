package com.bosch.eet.skill.management.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Assignee {
    @JsonProperty("Unassigned")
    UNASSIGNEED("Unassigned"),
    @JsonProperty("Talent Manager")
    TALENT_MANAGER("Talent Manager"),
    @JsonProperty("Recruitment Team")
    RECRUITMENT_TEAM("Recruitment Team"),
    @JsonProperty("Outsourcing Manager")
    OUTSOURCING_MANAGER("Outsourcing Manager")
    ;

    String value;

    Assignee(String value) {
        this.value = value;
    }
}
