/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.dbservice;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author LUK1HC
 *
 */
public class PredefinedData {

	public static final String OUTPUT_FILE = "./mysql/sql_script/generate_predefined_data.sql";
	public static final String INPUT_FILE = "PreDefinedData.xlsx";
	
	private static void generatePreDefinedData() {
		FileInputStream fis = null;
		XSSFWorkbook workbook = null;
		try {
			fis = new FileInputStream(new File(INPUT_FILE));
			
			// Create Workbook instance holding reference to .xlsx file
			workbook = new XSSFWorkbook(fis);
			
			// Get Skill sheet
			List<GenericObject> skills = getSheetSkill(workbook);
			generateSkill(skills);
			
			// Get Level sheet
			List<GenericObject> levels = getSheetLevel(workbook);
			generateLevel(levels);
			
			// Get Project Role sheet
			List<GenericObject> projectRoles = getSheetProjectRole(workbook);
			generateProjectRole(projectRoles);
			
			
			workbook.close();
			fis.close();            
		} catch (Exception e) {
			System.out.println(e);
		} 
		
	}
	
	private static List<GenericObject> getSheetSkill(XSSFWorkbook workbook) {
		List<GenericObject> genericObjects = new LinkedList<>();
		DataFormatter formatter = new DataFormatter();
		try {

			XSSFSheet sheet = workbook.getSheet("Skill");
			
			for (Row row: sheet) {

				// Skip the first row
	        	if(row.getRowNum() == 0) {
	        	       continue; 
	        	}
	        	
	        	GenericObject genericObject = new GenericObject();

	        	// name
	        	Cell cellName = row.getCell(0);
				String name = formatter.formatCellValue(cellName);
	        	genericObject.setName(name);

	        	// description
	        	Cell cellDescription = row.getCell(1);
				String description = formatter.formatCellValue(cellDescription);
	        	genericObject.setDescription(description);

	        	// sequence
	        	Cell cellSequence = row.getCell(2);
				String sequence = formatter.formatCellValue(cellSequence);
	        	genericObject.setSequence(Integer.valueOf(sequence));
	        	
	        	genericObjects.add(genericObject);
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		return genericObjects;
	}
	
	private static void generateSkill(List<GenericObject> skills) {
		try {
			StringJoiner sj = new StringJoiner("\n", "", "\n");
			sj.add(" USE `skm_db`; ");
			String query = sj.toString();
			System.out.println(query);
			Files.write(Paths.get(OUTPUT_FILE), (query + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			
			for (GenericObject genericObject : skills) {
				StringJoiner sjQuery = new StringJoiner("\n", "", "\n");
				
				sjQuery.add(" SET @skill_id = UUID(); ");
				sjQuery.add(" SET @skill_name = '" + genericObject.getName() + "'; ");
				sjQuery.add(" SET @skill_description = '" + genericObject.getDescription() + "'; ");
				sjQuery.add(" SET @skill_sequence = '" + genericObject.getSequence() + "'; ");
				sjQuery.add(" INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) ");
				sjQuery.add(" SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp ");
				sjQuery.add(" WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; ");
				String content = sjQuery.toString();
				System.out.println(content);
				Files.write(Paths.get(OUTPUT_FILE), (content + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private static List<GenericObject> getSheetLevel(XSSFWorkbook workbook) {
		List<GenericObject> genericObjects = new LinkedList<>();
		DataFormatter formatter = new DataFormatter();
		try {

			XSSFSheet sheet = workbook.getSheet("Level");
			
			for (Row row: sheet) {

				// Skip the first row
	        	if(row.getRowNum() == 0) {
	        	       continue; 
	        	}
	        	
	        	GenericObject genericObject = new GenericObject();

	        	// name
	        	Cell cellName = row.getCell(0);
				String name = formatter.formatCellValue(cellName);
	        	genericObject.setName(name);

	        	// description
	        	Cell cellDescription = row.getCell(1);
				String description = formatter.formatCellValue(cellDescription);
	        	genericObject.setDescription(description);

	        	genericObjects.add(genericObject);
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		return genericObjects;
	}
	
	private static void generateLevel(List<GenericObject> levels) {
		try {
			StringJoiner sj = new StringJoiner("\n", "", "\n");
			sj.add(" USE `skm_db`; ");
			String query = sj.toString();
			System.out.println(query);
			Files.write(Paths.get(OUTPUT_FILE), (query + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			
			for (GenericObject genericObject : levels) {
				StringJoiner sjQuery = new StringJoiner("\n", "", "\n");
				
				sjQuery.add(" SET @level_id = UUID(); ");
				sjQuery.add(" SET @level_name = '" + genericObject.getName() + "'; ");
				sjQuery.add(" SET @level_description = '" + genericObject.getDescription() + "'; ");
				sjQuery.add(" INSERT INTO `level` (`id`, `name`, `description`) ");
				sjQuery.add(" SELECT * FROM (SELECT @level_id, @level_name, @level_description) AS tmp ");
				sjQuery.add(" WHERE NOT EXISTS (SELECT `name` FROM `level` WHERE `name` = @level_name) LIMIT 1; ");
				String content = sjQuery.toString();
				System.out.println(content);
				Files.write(Paths.get(OUTPUT_FILE), (content + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private static List<GenericObject> getSheetProjectRole(XSSFWorkbook workbook) {
		List<GenericObject> genericObjects = new LinkedList<>();
		DataFormatter formatter = new DataFormatter();
		try {

			XSSFSheet sheet = workbook.getSheet("Project Role");
			
			for (Row row: sheet) {

				// Skip the first row
	        	if(row.getRowNum() == 0) {
	        	       continue; 
	        	}
	        	
	        	GenericObject genericObject = new GenericObject();

	        	// name
	        	Cell cellName = row.getCell(0);
				String name = formatter.formatCellValue(cellName);
	        	genericObject.setName(name);

	        	// description
	        	Cell cellDescription = row.getCell(1);
				String description = formatter.formatCellValue(cellDescription);
	        	genericObject.setDescription(description);

	        	// sequence
	        	Cell cellSequence = row.getCell(2);
				String sequence = formatter.formatCellValue(cellSequence);
	        	genericObject.setSequence(Integer.valueOf(sequence));
	        	
	        	genericObjects.add(genericObject);
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		return genericObjects;
	}
	
	private static void generateProjectRole(List<GenericObject> projectRoles) {
		try {
			StringJoiner sj = new StringJoiner("\n", "", "\n");
			sj.add(" USE `skm_db`; ");
			String query = sj.toString();
			System.out.println(query);
			Files.write(Paths.get(OUTPUT_FILE), (query + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			
			for (GenericObject genericObject : projectRoles) {
				StringJoiner sjQuery = new StringJoiner("\n", "", "\n");
				
				sjQuery.add(" SET @project_role_id = UUID(); ");
				sjQuery.add(" SET @project_role_name = '" + genericObject.getName() + "'; ");
				sjQuery.add(" SET @project_role_description = '" + genericObject.getDescription() + "'; ");
				sjQuery.add(" SET @project_role_sequence = '" + genericObject.getSequence() + "'; ");
				
				sjQuery.add(" INSERT INTO `project_role` (`id`, `name`, `description`, `sequence`) ");
				sjQuery.add(" SELECT * FROM (SELECT @project_role_id, @project_role_name, @project_role_description, @project_role_sequence) AS tmp ");
				sjQuery.add(" WHERE NOT EXISTS (SELECT `name` FROM `project_role` WHERE `name` = @project_role_name) LIMIT 1; ");
				String content = sjQuery.toString();
				System.out.println(content);
				Files.write(Paths.get(OUTPUT_FILE), (content + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static void main(String[] args) {
		try {
			Files.deleteIfExists(Paths.get(OUTPUT_FILE));
			generatePreDefinedData();
		} catch (Exception e) {
			System.out.println(e);
		}
	}	
}
