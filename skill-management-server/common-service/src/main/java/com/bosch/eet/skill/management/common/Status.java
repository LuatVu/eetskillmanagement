/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.common;

/**
 * @author LUK1HC
 *
 */
public enum Status{
	
	ACTIVE("Active"), INACTIVE("Inactive");

	private final String label;

	private Status(final String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	public static String getNameByLabel(final String label) {
		for (Status e : Status.values()) {
			if (label.equalsIgnoreCase(e.label))
				return e.name();
		}
		return null;
	}
	
	public static Status getName(final String label) {
		return Status.valueOf(getNameByLabel(label));
	}
}
