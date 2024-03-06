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
public enum Gender {

    M("M", "Male"), FM("FM", "Female"), NA("N/A", "Not available");

    private final String label;

    private final String value;

    public static Gender getGenderByLabel(final String label) {
        if(StringUtils.isEmpty(label)) return Gender.NA;
        switch (label) {
            case "M" : return Gender.M;
            case "FM" : return Gender.FM;
            default: return  Gender.NA;
        }
    }

    public static Gender getGenderByValue(final String value){
        if(StringUtils.isEmpty(value)) return Gender.NA;
        switch (value) {
            case "Male" : return Gender.M;
            case "Female" : return Gender.FM;
            default: return  Gender.NA;
        }
    }
}
