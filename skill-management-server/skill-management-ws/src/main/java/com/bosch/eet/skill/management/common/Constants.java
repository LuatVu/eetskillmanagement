/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.common;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author LUK1HC
 */

public final class Constants {

    public static final String GRANT_TYPE_PASS = "password";
    public static final String GRANT_TYPE_REFRESH = "refresh_token";
    public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
	// Resource
	public static final String RESOURCE_ID = "skm-ws-api";
	public static final String ID = "id";

	// Cached
	public static final String CREDENTIALS_CACHE_NAME = "credentialsCache";
	
	// Non-Bosch
	public static final String NONBOSCH = "Non-Bosch";
	
	// Bosch
	public static final String BOSCH = "Bosch";

	// Default project status
    public static final String DEFAULT_PROJECT_STATUS = "New";
	
	//Date Format
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat TIMESTAMP_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter SIMPLE_LOCAL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    //Skill Competency Lead Id
    public static final String SKILL_COMPETENCY_LEAD  =  "personal_id";
    
    //Skill Id
    public static final String SKILL_ID = "skill_id";

    //Skill Experience Level Id
    public static final String SKILL_EXP_LEVEL_ID = "skill_experience_level_id";
    
    //Skill Group ID
    public static final String SKILL_GROUP_ID = "skill_group_id";

    // Level and experience
    public static final String LEVEL = "level";
    public static final String DEFAULT_SKILL_LEVEL = "0";
    public static final float DEFAULT_FLOAT_SKILL_LEVEL = 0;
    public static final Integer DEFAULT_EXPERIENCE = 0;

    // Name
    public static final String NAME = "name";

    // Status
    public static final String STATUS = "status";
    public static final String DONE = "Done";
    public static final String INPROGRESS = "In-progress";
    public static final String ACTIVE = "Active";
    public static final String WAITING_FOR_APPROVAL = "APPROVAL_PENDING";

    // Course
    public static final String COURSE = "course";
    public static final String COURSE_ID = "course_id";
    public static final String TRAINING_COURSE_ID = "training_course_id";
    public static final String COURSE_TYPE = "course_type";
    public static final String COURSE_TYPE_JOIN = "courseType";

    // Project
    public static final String GB = "gb";
    public static final String GB_UNIT = "gbUnit";
    public static final String GB_UNIT_FILTER = "gb_unit";
    public static final String PROJECT_TYPE = "project_type";
    public static final String PROJECT_TYPE_JOIN = "projectType";
    public static final String DEPARTMENT = "department";
    public static final String PROJECT="project";
    public static final String PROJECT_ROLE_ID_DEFAULT = "1";
    public static final String PROJECT_EXISTED = "existed";

    // Team
    public static final String TEAM = "team";

    // Header attribute

    public static final String HEADER_ATTRIBUTE_AUTHENTICATION = "Authentication";
    public static final String HEADER_ATTRIBUTE_IV_USER = "iv-user";
    public static final String HEADER_ATTRIBUTE_CLIENT_ID = "client-id";
    public static final String HEADER_ATTRIBUTE_CLIENT_SECRET = "client-secret";
    public static final String HEADER_ATTRIBUTE_X_FORWARDED_FOR = "X-Forwarded-For";

    // Ip address
    public static final String WAM_IP_ADDR = "10.187.38.";
    public static final String LOCAL_IP_ADDR = "0:0:0:0:0:0:0:1";
    public static final String LOCAL_IP_ADDR_DEV = "127.0.0.1";

    // APPROVED AND REJECTED AND PENDING
    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";
    public static final String APPROVED_OR_REJECTED = "APPROVED/REJECTED";
    public static final String PENDING = "APPROVAL_PENDING";

    // Session attribute

    public static final String SESSION_ATTRIBUTE_AUTHENTICATION = "authentication";
    public static final String SESSION_ATTRIBUTE_USER_INFO = "userInfo";
    // Refer to org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint for details about this constants
    public static final String AUTHORIZATION_REQUEST_ATTR_NAME = "authorizationRequest";
    public static final String ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME = "org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST";

    
    // Manager
    public static final String MANAGER = "ManagerGroup";
    //ID Group
    public static final String ASSOCIATE_GROUP_ID="26aa55a5-a1ff-4625-ba88-5198c7b74dcb";
    
    //Elastic Search
    public static final Integer INTERVAL_IN_MILLISECONDE = 180_000;
    public static final String MODIFICATION_DATE = "modificationDate";
    public static final String PERSON_INDEX = "personal";
    public static final String SKILL_INDEX = "skill";
    public static final String PROJECT_INDEX = "project";
    public static final String CUSTOMER_INDEX = "customer_gb";
    public static final String PERSONAL_SKILL_INDEX = "personalskill";
    
    //WAM Server Link
    public static final String WAM_SERVER = "https://rb-wam-ap.bosch.com/eetportal";
    public static final String MANAGE_PENDING_REQUEST_LINK = WAM_SERVER + "/#/manage-request/pending-request";
    public static final String MY_REQUEST_LINK = WAM_SERVER + "/#/career-development/my-request";

	public static final String DATE_FORMAT = "yyyy-MM-dd";  
	
	//Specific character
	public static final String WHITE_SPACE =" ";
	public static final String COMMA = ",";
	public static final String UNDERSCORE = "_";
	public static final String LINE = "-";
	public static final String SLASH = "/";
	public static final String DOT = ".";
	
	
	//Message import finish
	public static final String IMPORT_SUCCESS ="Import finish";
	public static final String IMPORT_FAILD ="Import faild";
	
	//Level skills
	public static final String LEVEL_0="Level 0";
	public static final String LEVEL_1="Level 1";
	public static final String LEVEL_2="Level 2";
	public static final String LEVEL_3="Level 3";
	public static final String LEVEL_4="Level 4";
	public static final String LEVEL_TEMPLATE="Level ";
	public static final String IS_MANDATORY="Is Mandatory";
	public static final String IS_REQUIRED="Is Required";
	public static final String YES="Y";
	public static final String NO ="N";
	
	//Upload inmage
	public static final String UPLOADED_FOLDER = "app.uploadfolder";
	public static final String UPLOADED_FOLDER_TEMP = UPLOADED_FOLDER + ".temp";
	public static final String UPLOADED_FOLDER_IMAGE = UPLOADED_FOLDER +  ".image";
	
	public static final String APP_API_BASE_URL = "app.api.base-uri";
	
	// Getter Methods for mutable object, Object get by this getter will act more like constant
	public static final String[] getSupportedIMGType() {
		return FILE_TYPE_IMAGE;
	}
	private static final String[] FILE_TYPE_IMAGE = {"apng", "bmp", "gif", "ico", "cur", "jpg", "jpeg", "jfif", "pjpeg", "pjp", "png", "svg", "tif", "tiff", "webp"};
	
	// File share content type
	public static final String FILE_CONTENT_TYPE = "application/octet-stream";
	//Skill group
	public static final String SKILL_GROUP = "skillGroup";
	public static final String TECHNICAL_SKILL_GROUP = "technical";
	//Skill
	public static final String SKILLS ="skills";
	//Common reponse list
	public static final String TOTAL_PAGE ="totalPage";
	public static final String TOTAL_ITEM="totalItem";
	public static final String OBJECT_CLASS = "objectclass";
	public static final String PERSON = "person";
	public static final String CN = "cn";
	public static final String PAGE ="page";
	public static final String SIZE = "size";
	public static final int PAGEABLE_DEFAULT = 10000;
	public static final int DEFAULT_PAGE=0;
	public static final int DEFAULT_SIZE=1;

	//Main Pages path
	public static final String MANAGER_REQUEST = "manage-request";
	
	//Historical change level
	public static final String HISTORICAL_CHANGE_LEVEL ="histories";
	//Elastic
	public static final String CUSTOMER_GB="customer_gb_";
	
	//Message
	public static final String SUCCESS="Success";
	public static final String CAN_NOT_FOUND_PROJECT="Cannot find project";
    
    //Authorities - permission
    public static final String DELETE_PROJECT="DELETE_PROJECT";
    public static final String EDIT_ASSOCIATE_INFO_PERMISSION="EDIT_ASSOCIATE_INFO_PERMISSION";
    public static final String VIEW_USER_MANAGEMENT="VIEW_USER_MANAGEMENT";
    public static final String USER_GROUP="USER_GROUP";
    public static final String CREATE_ROLE="CREATE_ROLE";
    public static final String EDIT_ROLE="EDIT_ROLE";
    public static final String DELETE_ROLE="DELETE_ROLE";
    public static final String VIEW_ASSOCIATE_INFO_PERMISSION="VIEW_ASSOCIATE_INFO_PERMISSION";
    public static final String VIEW_ALL_PROJECTS="VIEW_ALL_PROJECTS";
    public static final String VIEW_DEPARTMENT_LEARNING ="VIEW_DEPARTMENT_LEARNING";
    public static final String VIEW_SYSTEM ="VIEW_SYSTEM";
    public static final String ADD_BOSCH_PROJECT="ADD_BOSCH_PROJECT";
    public static final String EDIT_PROJECT="EDIT_PROJECT";
    public static final String VIEW_PROJECT_DETAIL="VIEW_PROJECT_DETAIL";
    public static final String VIEW_EXPECTED_SKILL_LEVEL="VIEW_EXPECTED_SKILL_LEVEL";
    public static final String EDIT_EXPECTED_SKILL_LEVEL="EDIT_EXPECTED_SKILL_LEVEL";
    public static final String VIEW_SKILL_EVALUATE="VIEW_SKILL_EVALUATE";
    public static final String VIEW_REPORT="VIEW_REPORT";
    
    public static final String REQUESTER_ID ="requester_id";
    public static final String APPROVER_ID ="approver_id";

    //Extention file
    public static final String TXT_EXT ="txt";
    
    //Department
    public static final String MS_EET ="MS/EET";
    
    //Personal Title
    public static final String INTERNSHIP_TITLE = "Internship";

    //Expected skill levels
    public static final Set<Float> EXPECTED_SKILL_LEVELS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(0.0F, 1.0F, 1.5F, 2.0F, 2.5F, 3.0F, 3.5F, 4.0F)));
}
