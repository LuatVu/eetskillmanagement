package com.bosch.eet.skill.management.enums;

import org.codehaus.jackson.annotate.JsonCreator;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

public enum Status {
    @JsonProperty("Draft")
    DRAFT("Draft"),

    @JsonProperty("Open")
    OPEN("Open"),

    @JsonProperty("On going")
    ON_GOING("On going"),

    @JsonProperty("On hold")
    ON_HOLD("On hold"),

    @JsonProperty("Canceled")
    CANCELED("Canceled"),

    @JsonProperty("Filled")
    FIELD("Filled"),
    ;

    @Getter
    String status;

    Status(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }

    @JsonCreator
    public static Status fromText(String text) {
        for (Status r : Status.values()) {
            if (r.getStatus().equals(text)) {
                return r;
            }
        }
        throw new IllegalArgumentException();
    }
}
