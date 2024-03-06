/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.common;

import java.util.Base64;

import javax.xml.bind.DatatypeConverter;

/**
 * @author LUK1HC
 *
 */
public final class PasswordHelper {

	public static final String PREFIX_TEXT_PASSWORD = "P4ssw0rd_";
	public static final String SUFFIX_TEXT_PASSWORD = "_P4ssw0rd";
	
	private static final String EMPTY = "";
	
	private PasswordHelper() {
	}
	
	public static String encrypt(final String password) {
		StringBuilder passBuilder = new StringBuilder();
		passBuilder.append(PREFIX_TEXT_PASSWORD).append(password).append(SUFFIX_TEXT_PASSWORD);		
		return Base64.getEncoder().encodeToString(passBuilder.toString().getBytes());
	}
	
	public static String decrypt(final String encryptedPassword) {		
		String decrypt = new String(DatatypeConverter.parseBase64Binary(encryptedPassword));		
		return decrypt.replaceFirst(PREFIX_TEXT_PASSWORD, EMPTY).replaceFirst(SUFFIX_TEXT_PASSWORD, EMPTY);
	}
	
	public static void main(String[] args) {
		String plainText = "almmigration123";
		String encryptedValue = encrypt(plainText);
		String decryptedValue = decrypt(encryptedValue);
		System.out.println("Encrypted value: " + encryptedValue);
		System.out.println("Decrypted value: " + decryptedValue);
	}
	
}
