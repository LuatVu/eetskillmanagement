package com.bosch.eet.skill.management.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum SkillEvaluationStatus {

    APPROVAL_PENDING("APPROVAL_PENDING"),

    APPROVED("APPROVED");

    private String value;
}
