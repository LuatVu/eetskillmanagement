
/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.usermanagement.consts;

/**
 * @author GNY8HC
 */
public enum MessageCode {

    SUCCESS,

    // Role
    SKM_ROLE_IS_INACTIVE,
    SKM_GROUP_IS_INACTIVE,
    SKM_ROLE_NOT_FOUND,

    SKM_ROLE_EXISTED_ALREADY,

    SKM_ROLE_CREATE_FAIL,

    SKM_ROLE_UPDATED_FAIL,

    SKM_ROLE_REMOVED_FAIL,

    SKM_ROLE_NAME_IS_EMPTY,

    SKM_INVALID_ROLE_NAME,

    USER_NOT_FOUND_MSG,

    SKM_USER_INACTIVE,

    SKM_GROUP_NOT_EXIST_MSG,
    
    TEAM_NOT_EXIST,

    SKM_GROUP_ALREADY_EXIST,

    USER_IN_GROUP_ALREADY_MSG,

    ROLE_NOT_EXIST_MSG,

    SERVER_ERROR,

    PERMISSION_NOT_FOUND,

    HANDLE_EXCEL_FAIL,

    DEFAULT_LEVEL_NOT_EXIST, 
    
    PERSON_NOT_EXIST,
    
    MISSING_APPROVER_ID,
    MISSING_APPROVER,

    SKM_REQUEST_EVALUATION_NOT_FOUND,

    SKM_HAS_PENDING_REQUEST_EVALUATION,
    
    SKM_REQUEST_EVALUATION_DETAIL_NOT_FOUND,

    SKM_COURSE_ALREADY_EXIST,

    SKM_ERROR_ADDING_COURSE,

    SKM_COURSE_TEMPLATE_DOWNLOAD_FAIL,
    
    SKM_COURSE_DUPLICATE_NAME,

    SKM_COURSE_NOT_FOUND,

    SKM_COURSE_IS_BEING_ASSIGNED,

    SKM_COURSE_DELETE_FAIL,

    INVALID_DATE,
    
    STATUS_NOT_AVAILABLE,
    
    ADD_ASSOCIATE_SUCCESSFUL,
    
    PROJECT_ROLE_NOT_FOUND,
    PROJECT_ROLE_IS_IN_USE,
    PROJECT_NAME_ALREADY_EXIST,
    
    COMMON_TASK_NOT_FOUND,
    COMMON_TASK_AREADY_EXIST,
    SKM_COMMON_TASK_ADD_FAIL,
    
    SKM_SKILL_EXIST,
    
    MISSING_PERMISSION_ID,
    ASSOCIATE_NOT_FOUND
}
