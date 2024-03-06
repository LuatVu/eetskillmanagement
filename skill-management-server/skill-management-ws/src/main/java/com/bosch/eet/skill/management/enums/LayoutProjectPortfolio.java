package com.bosch.eet.skill.management.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum LayoutProjectPortfolio {
    PROJECT_BENEFIT("project_benefit"),
    PROJECT_PROBLEM("project_problem"),
    PROJECT_SOLUTION("project_solution"),
    PROJECT_HIGHLIGHT("project_highlight"),
    ;

    private String value;

    LayoutProjectPortfolio(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LayoutProjectPortfolio fromValue(String value) {
        for (LayoutProjectPortfolio layout : LayoutProjectPortfolio.values()) {
            if (layout.getValue().equalsIgnoreCase(value)) {
                return layout;
            }
        }
        throw new IllegalArgumentException("Invalid layout value: " + value);
    }
    
	public static boolean exist(String value) {
		for (LayoutProjectPortfolio layout : values()) {
			if (layout.getValue().equals(value)) {
				return true;
			}
		}
		return false;
	}

}
