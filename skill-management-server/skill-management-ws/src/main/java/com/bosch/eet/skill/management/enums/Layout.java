package com.bosch.eet.skill.management.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Layout {
    OVERVIEW_LAYOUT("overview_layout"),
    ORGCHART_LAYOUT("orgchart_layout"),
    HELP_LAYOUT("help_layout"),
    RELEASE_NOTE_LAYOUT("release_note_layout");
    
    private String value;

    Layout(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Layout fromValue(String value) {
        for (Layout layout : Layout.values()) {
            if (layout.getValue().equalsIgnoreCase(value)) {
                return layout;
            }
        }
        throw new IllegalArgumentException("Invalid layout value: " + value);
    }
}
