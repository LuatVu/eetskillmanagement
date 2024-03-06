/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.exception;

/**
 *
 * @author LUK1HC
 */

public enum MessageCode {
	
	// Course
	COURSE_NOT_FOUND,	
	COURSE_ASSIGN_SUCCESS,
	SKM_COURSE_TEMPLATE_DOWNLOAD_FAIL,
	SKM_COURSE_IS_BEING_ASSIGNED,

	// Evaluation Request
	REQUEST_EVALUATION_NOT_FOUND,
	CANNOT_UPDATE_REQUEST_EVALUATION,
	REQUEST_EVALUATION_CREATE_FAIL,

	//  SKill
	SKILL_NOT_FOUND,
	SKILL_LEVEL_INVALID,
	SKILL_NAME_ALREADY_EXIST,
	DELETE_SKILL_FAIL,
	SKILL_IN_USE,
	SKM_UPDATE_SKILL_FAIL,
	SKM_SKILL_ALREADY_EXISTS,
	SKM_ADD_SKILL_FAIL,

	// Common
	VALIDATION_ERRORS,
	INVALID_ID,
	INVALID_NAME,
	INVALID_DATE,
	RESOURCE_NOT_FOUND,
	NAME_IS_REQUIRED,
	INVALID_LAYOUT,
	WRONG_DATE_FORMAT,

	// Authentication
	INVALID_CLIENT_ID_SECRET,
	INVALID_USERNAME_PASSWORD,
	NOT_EXISTED_USERNAME,
	INVALID_PASSWORD,
	INVALID_TOKEN,
	EXPIRED_TOKEN,
	FAILED_LOGOUT,
	ACCESS_DENIED,

	// User Management
	UPDATE_PERMISSION_FAIL,

	//Account
	SKM_USER_NOT_FOUND,
	SKM_USER_EXISTED_ALREADY,
	SKM_INTERNAL_USER_CREATE_FAIL,
	SKM_USER_WAITED_TO_APPROVAL,
	SKM_INVALID_LDAP_CHECKED,
	
	// System Server
	INTERNAL_SERVER_ERROR,
	CAN_NOT_CONNECT_LDAP_SERVER,
	DATA_ACCESS_ERROR,
	NOT_AUTHORIZATION,
	RQONE_ERROR,

	//Email Message
	SEND_SUCCESS,
	SUCCESS,

	//Level
	DEFAULT_LEVEL_NOT_INITIALIZED,
	LEVEL_NOT_ASSIGNED,
	LEVEL_NOT_FOUND,

	// Personal project
	PERSONAL_PROJECT_NOT_FOUND,
	ERROR_DELETING_PERSONAL_PROJECT,
	ERROR_DELETING_PROJECT_SKILL_TAG,
	ERROR_DELETING_PHASE_PROJECT,
	PROJECT_MEMBER_PERIOD_OVERLAPPED,

	// Project type
	NO_MATCHING_PROJECT_TYPE_FOUND,
	NO_PROJECT_TYPE_FOUND,

	// Project
	PROJECT_NOT_FOUND,
	ERROR_DELETING_PROJECT,
	PROJECT_DATA_REQUIRED,
	PROJECT_ID_REQUIRED,
	SAVING_PROJECT_FAIL,
	SKM_ERROR_CREATING_PROJECT,
	TECHNOLOGY_USED_NULL,
	PROJECT_TEAM_SIZE_INVALID,
	IMPORT_FAIL,
	DUPLICATED_PROJECT_NAME_IN_FILE,
	PROJECT_NAME_EXIST,
	MANDATORY_FIELD_MISSING,
	PROJECT_IS_DUPLICATE,
	CANNOT_ASSIGN_ANOTHER_NON_BOSCH,
	CANNOT_ADD_PORTFOLIO_FOR_NON_BOSCH,
	SYNC_PROJECT_ELASTIC_FAIL,

	// Start date
	CANNOT_PARSE_START_DATE,
	MEMBER_START_DATE_IS_REQUIRED,

	// End date
	CANNOT_PARSE_END_DATE,
	
	//Error find course
	CANNOT_FIND_COURSE,
	SKM_ERROR_ADDING_COURSE,
	
	//Personal
	PERSONAL_ID_NOT_EXIST,
	EXPERIENCED_NON_BOSCH_TYPE_WRONG,
	EXPERIENCED_NON_BOSCH_WRONG,
	SAVE_PERSONAL_FAIL,

	// Created date
	CANNOT_PARSE_CREATED_DATE,

	// Updated date
	CANNOT_PARSE_UPDATED_DATE,

	// Approved date
	CANNOT_PARSE_APPROVED_DATE,
	
	//Department
	SKM_DEPARTMENT_NOT_FOUND,

	SKM_DEPARTMENT_GROUP_NOT_FOUND,
	
	SKM_ASSOCIATE_ALREADY_EXIST,

	
	//SKILL GROUP
	SKM_SKILL_GROUP_NOT_FOUND,
	SKM_SKILL_GROUP_DELETE_FAIL,
	SKM_SKILL_GROUP_ALREADY_EXISTS,

	// Skill experience level
	SKILL_EXPERIENCE_LEVEL_NOT_FOUND,
	SKM_SKILL_EXPERIENCE_LEVEL_SAVE_FAIL,
	SKM_SKILL_EXPERIENCE_LEVEL_DELETE_FAIL,

	// Competency lead
	COMPETENCY_LEAD_REQUIRED,
	COMPETENCY_LEAD_SAVE_SKILL_FAIL,
	COMPETENCY_LEAD_SKILL_NOT_FOUND,
	COMPETENCY_LEAD_NOT_FOUND,
	SKM_COMPETENCY_LEAD_SAVE_FAIL,
	SKM_COMPETENCY_LEAD_DELETE_FAIL,

	// Skill evaluation
	SKILL_EVALUATION_NOT_FOUND,

	// Personal skill evaluation
	PERSONAL_SKILL_EVALUATION_NOT_FOUND,
	
	// Project Role
	PROJECT_NAME_ALREADY_EXIST,		
	PROJECT_ROLE_NOT_FOUND,    
    PROJECT_ROLE_IS_IN_USE,
    
    // Common Task
    COMMON_TASK_NOT_FOUND,    
    COMMON_TASK_ALREADY_EXIST,
	SKM_COMMON_TASK_ADD_FAIL,

	// Excel
	HANDLE_EXCEL_FAIL,

	// Elastic Search
	CREATE_INDEX_SUCCESS,
	DELETE_INDEX_SUCCESS,
	INDEX_ALREADY_EXIST,
	INDEX_NOT_EXIST,
	ADD_DATA_INDEX_SUCCESS,
	SYNC_DATA_SUCCESS,
	UPDATE_INDEX_SUCCESS,

	// Team
	SKM_TEAM_NOT_FOUND,
	
	// File Upload
	FAILED_STORE_EMPTY_FILE,
	FAILED_STORE_RELATIVE_PATH,
	FILE_NOT_FOUND,
	INVALID_FILE_TYPE,
	BAD_REQUEST,
	SKM_UPDATE_TEAM_MANAGER_FAIL,
	ERROR_UPLOAD_FILE,
	INVALID_PATH,
	READ_FILE_ERROR,
	DELETE_FILE_ERROR,
	MAXIMUM_UPLOAD_SIZE_EXCEEDED,
	
	// Skill Tag
	SKILL_TAG_NOT_FOUND,
	PROJECT_SKILL_TAG_NOT_FOUND,
	ERROR_DELETING_SKILL_TAG,
	SKILL_TAG_ALREADY_EXISTS,

	// Phase
	PHASE_ID_NOT_EXIST,
	
	//Customer
	CUSTOMER_NOT_FOUND,
	CUSTOMER_DUPLICATED,
	CUSTOMER_HIGHLIGHT_TOO_LONG,
	CUSTOMER_CORPORATION_TOO_LONG,
	CUSTOMER_NAME_TOO_LONG,

	//ProjectScope
	PROJECT_SCOPE_NOT_FOUND,
	PROJECT_SCOPE_NAME_ALREADY_EXIST,
	
	//Associate
	ASSOCIATE_NOT_FOUND,
	SYNC_ASSOCIATE_ELASTIC_FAIL
}
