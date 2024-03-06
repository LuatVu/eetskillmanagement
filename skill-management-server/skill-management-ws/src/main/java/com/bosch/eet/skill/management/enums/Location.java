package com.bosch.eet.skill.management.enums;

import org.codehaus.jackson.annotate.JsonCreator;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Location {
    @JsonProperty("HCM")
    HCM("HCM"),
    @JsonProperty("HN")
    HN("HN"),
    @JsonProperty("Any")
    ANY("Any")
    ;

    @Getter
    String locationName;

    Location(String locationName) {
        this.locationName = locationName;
    }

    @Override
    public String toString() {
        return locationName;
    }

    @JsonCreator
    public static Location fromText(String text) {
        for (Location r : Location.values()) {
            if (r.getLocationName().equals(text)) {
                return r;
            }
        }
        throw new IllegalArgumentException();
    }
}
