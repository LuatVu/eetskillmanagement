/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.common;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LUK1HC
 */
@Slf4j
@AllArgsConstructor
@Getter
public enum EvaluationStatus {

    Approved("APPROVED", "Approved"),
    Rejected("REJECTED", "Rejected"),
    Cancelled("CANCELLED", "Cancelled"),
    WaitingForApproval("APPROVAL_PENDING", "Waiting for approval"),
    NA("N/A", "NA");

    private final String label;
    private final String value;

    public static EvaluationStatus getEvaluationStatusByLabel(final String label) {
        if(StringUtils.isEmpty(label)) return EvaluationStatus.NA;
        switch (label) {
            case "APPROVED" : return EvaluationStatus.Approved;
            case "REJECTED" : return EvaluationStatus.Rejected;
            case "CANCELLED" : return EvaluationStatus.Cancelled;
            case "APPROVAL_PENDING" : return EvaluationStatus.WaitingForApproval;
            default: return  EvaluationStatus.NA;
        }
    }

    public static EvaluationStatus getEvaluationStatusByValue(final String value){
        if(StringUtils.isEmpty(value)) return EvaluationStatus.NA;
        switch (value) {
            case "Approved" : return EvaluationStatus.Approved;
            case "Rejected" : return EvaluationStatus.Rejected;
            case "Cancelled" : return EvaluationStatus.Cancelled;
            case "Waiting for approval" : return EvaluationStatus.WaitingForApproval;
            default: return  EvaluationStatus.NA;
        }
    }
}
