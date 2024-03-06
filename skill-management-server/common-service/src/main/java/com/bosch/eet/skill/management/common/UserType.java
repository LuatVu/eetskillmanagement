package com.bosch.eet.skill.management.common;


public enum UserType {
	PERSON("Person"), GROUP("Group");

	private final String label;

	private UserType(final String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public static String getNameByLabel(final String label) {
		for (UserType e : UserType.values()) {
			if (label.equalsIgnoreCase(e.label))
				return e.name();
		}
		return "";
	}

	public static UserType getName(final String label) {
		String nameByLabel = getNameByLabel(label);
		if("".equals(nameByLabel)) return PERSON;
		return UserType.valueOf(nameByLabel);
	}


}
