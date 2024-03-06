/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.ldap.common;


/**
 * @author LUK1HC
 *
 */
public class Constants {

	private Constants() {
	}
	
	public static final String BACKSLASH = "\\";
	public static final String SLASH = "/";
	public static final String COMMA = ",";
	public static final String COMMA_SPACE = ", ";
	public static final String NEW_LINE = "\n";
	public static final String NEW_LINE_WITH_SPACE = " \n ";
	public static final String EQUAL = "=";
	public static final String SPACE = " ";
	public static final String ASTERISK = "*";
	public static final String REGEXP_FOR_SEARCH_STRING= ".*";
	public static final String DOT = ".";
	public static final String EMPTY = "";
	public static final String REGEXP = "\\<.*?\\>|&nbsp";
	public static final String NOT_SPECIAL_CHARACTER_ARRAY = "[^A-Za-z0-9 ]";
	public static final String REGEX_SOME_SPECIAL_CHARACTER = ".*[\\\\/:\\\"*<>|?].*$";
	public static final String REGEX_SOME_SPECIAL_CHARACTER_FOR_PROJECT = ".*[\\\\:\\\"*<>|?].*$";
	public static final String SPLIT_SPACE_BETWEEN_STRING = "\\s+";
	public static final String BIGDECIMAL_FORMAT = "0.00";
	
	public static final String OBJECT_CLASS = "objectclass";
	public static final String PERSON = "person";
	public static final String USER = "user";
	public static final String GROUP = "group";
	public static final String CN = "cn";
	public static final String NAME = "name";
	public static final String SN = "sn";
	public static final String GIVEN_NAME = "givenName";
	public static final String DISPLAY_NAME = "displayName";
	public static final String MAIL = "mail";
	public static final String CO = "co";
	public static final String DEPARTMENT = "department";
	public static final String DISTINGGUISHED_NAME = "distinguishedName";
	public static final String MEMBER = "member";
	public static final String MEMBER_OF = "memberOf";
	public static final String OBJECT_CATEGORY = "objectCategory";
	public static final String PROXY_ADDRESSES = "proxyAddresses";
	public static final String COMPANY = "company";
	public static final String OFFICE = "physicalDeliveryOfficeName";
	public static final String PICTURE = "thumbnailPhoto";

}