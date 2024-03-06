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
 */

public final class Routes {

    private Routes() {
    }

    public static final String PREFIX_URI_V1 = "/api/v1";

    public static final String URI_REST_SETTING = PREFIX_URI_V1 + "/setting";

    public static final String SLASH = "/";

    // Common
    public static final String URI_REST_HOME = "/home";
    public static final String URI_REST_LOGIN = PREFIX_URI_V1 + "/auth/login";
    public static final String URI_REST_AUTHENTICATE = PREFIX_URI_V1 + "/auth/authenticate";
    public static final String URI_REST_LOGIN_SSO = PREFIX_URI_V1 + "/sso";
    public static final String URI_REST_VALIDATE_TOKEN = PREFIX_URI_V1 + "/validate-token";
    public static final String URI_REST_LOGOUT = PREFIX_URI_V1 + "/logout";
    public static final String URI_REST_REQUEST_ACCESS = PREFIX_URI_V1 + "/auth/register-internal-user";
    public static final String URI_REST_DENY = PREFIX_URI_V1 + "/deny";
    public static final String URI_REST_ERROR = PREFIX_URI_V1 + "/error";
	public static final String URI_REST_REFRESH_TOKEN = PREFIX_URI_V1 + "/auth/refresh-token";
	public static final String URI_REST_ID="/{id}";
	
    // User Management
    public static final String URI_SEARCH_USER = PREFIX_URI_V1 + "/search/user";

    public static final String URI_SEARCH_USER_BY_ROLE = URI_SEARCH_USER + "/role";

    public static final String URI_SEARCH = PREFIX_URI_V1 + "/search";

    //Role
    public static final String URI_REST_ROLE = PREFIX_URI_V1 + "/role";
    public static final String URI_REST_DELETE_ROLE = PREFIX_URI_V1 + "/role/{id}/delete";


    //project role
    public static final String URI_REST_PROJECT_ROLE = PREFIX_URI_V1 + "/project-role";
    public static final String URI_REST_PROJECT_ROLE_UPLOAD = URI_REST_PROJECT_ROLE + "/upload";
    public static final String URI_REST_PROJECT_ROLE_DELETE = URI_REST_PROJECT_ROLE + "/delete";

    public static final String URI_REST_PROJECT = PREFIX_URI_V1 + "/project";
    public static final String URI_REST_PROJECT_DROP_DOWN = URI_REST_PROJECT + "/dropdown";
    public static final String URI_REST_COMPETENCY_LEAD = PREFIX_URI_V1 + "/competency-lead";
    public static final String URI_REST_COMPETENCY_LEAD_ID = URI_REST_COMPETENCY_LEAD + "/{id}";
    public static final String URI_REST_COMPETENCY_LEAD_DELETE = URI_REST_COMPETENCY_LEAD + "/delete";
    public static final String URI_REST_COMPETENCY_LEAD_SKILL_GROUP_ID = URI_REST_COMPETENCY_LEAD + "/skill-group";
    public static final String URI_REST_COMPETENCY_LEAD_BY_EVALUATION = URI_REST_COMPETENCY_LEAD + "/evaluation/{id}";


    public static final String URI_REST_GB = PREFIX_URI_V1 + "/gb";

    public static final String URI_REST_UPLOAD_SKILL = PREFIX_URI_V1 + "/upload-skill";

    public static final String URI_REST_PROJECT_ID = URI_REST_PROJECT + "/{project_id}";
    public static final String URI_REST_PROJECT_ID_SKILL_TAG = URI_REST_PROJECT_ID + "/skill-tags";
    public static final String URI_REST_PROJECT_ID_SKILL_TAG_DELETE = URI_REST_PROJECT_ID_SKILL_TAG + "/{skillTagId}/delete";
    public static final String URI_REST_UPDATE_PROJECT = URI_REST_PROJECT_ID + "/update";
    public static final String URI_REST_DELETE_PROJECT = URI_REST_PROJECT_ID + "/delete";
    public static final String URI_REST_PROJECT_FILTER = URI_REST_PROJECT + "/filter";
    public static final String URI_REST_PROJECT_FILTER_CUSTOMER_GB = URI_REST_PROJECT_FILTER + "/customer-gb";
    public static final String URI_REST_PROJECT_CUSTOMER_GB = URI_REST_PROJECT + "/customer-gb"; // find all projects by customerGb
	public static final String URI_REST_EDIT_ADDITIONAL_TASK = URI_REST_PROJECT + "/additional-task";
	public static final String URI_REST_PROJECT_CUSTOMER_GB_DETAIL = URI_REST_PROJECT_CUSTOMER_GB + "/{id}";
	public static final String URI_REST_PROJECT_CUSTOMER_GB_DEL = URI_REST_PROJECT_CUSTOMER_GB_DETAIL + "/del";
	public static final String URI_REST_CUSTOMER = PREFIX_URI_V1 + "/customer";
	public static final String URI_REST_GET_PROJECTS_BY_SKILLTAG_AND_CUSTOMER = URI_REST_CUSTOMER + "/projects";
	public static final String UIR_REST_PROJECT_PORTFOLIO = URI_REST_PROJECT_ID + "/portfolio";
    public static final String URI_REST_PROJECT_PORTFOLIO_DETAIL = UIR_REST_PROJECT_PORTFOLIO + "/{layout}";


    public static final String URI_REST_IMPORT_PROJECT = URI_REST_PROJECT + "/import/{ntid}";

    //permissions
    public static final String URI_REST_PERMISSION = PREFIX_URI_V1 + "/permissions";
    public static final String URI_REST_PERMISSION_ID = URI_REST_PERMISSION + "/{permission_id}";
    public static final String URI_REST_PERMISSION_UPDATE = URI_REST_PERMISSION_ID + "/update";

    public static final String URI_SEARCH_DISTRIBUTION = PREFIX_URI_V1 + "/search/dblist";

    //courses
    public static final String URI_REST_COURSE = PREFIX_URI_V1 + "/course";
    public static final String URI_REST_COURSE_ID = URI_REST_COURSE + "/{course_id}";
    public static final String URI_REST_COURSE_UPDATE = URI_REST_COURSE_ID + "/update";
    public static final String URI_REST_COURSE_DELETE = URI_REST_COURSE_ID + "/delete";
    public static final String URI_REST_COURSE_ADD_COURSE = URI_REST_COURSE + "/upload";
    public static final String URI_REST_COURSE_TEMPLATE = URI_REST_COURSE + "/template";
    public static final String URI_REST_ASSIGN_COURSE = URI_REST_COURSE_ID + "/assign";
    public static final String URI_REST_COURSE_MEMBERS = URI_REST_COURSE_ID + "/members";

    //category
    public static final String URI_REST_CATEGORY = PREFIX_URI_V1 + "/category";
	
	//Skill
	public static final String URI_REST_SKILL = PREFIX_URI_V1 + "/skill";
	public static final String URI_REST_SKILL_ID = URI_REST_SKILL + "/{id}";
	public static final String URI_REST_EDIT_SKILL = URI_REST_SKILL_ID + "/edit";
	public static final String URI_REST_DELETE_SKILL = URI_REST_SKILL_ID + "/delete";
	public static final String URI_REST_SKILL_EXPERIENCE = URI_REST_SKILL + "/experience";
	public static final String URI_REST_SKILL_EXPECTED = URI_REST_SKILL + "/expected";
	public static final String URI_REST_SKILL_EXPECTED_UPDATE= URI_REST_SKILL_EXPECTED +"/update";
	public static final String URI_REST_SKILL_EXPERIENCE_ID = URI_REST_SKILL_EXPERIENCE + "/{id}";
	public static final String URI_REST_SKILL_UPLOAD = URI_REST_SKILL + "/upload";
	public static final String URI_REST_SKILL_HIGHLIGHT= URI_REST_SKILL+"/highlight/{idPersonal}";
	public static final String URI_REST_SAVE_SKILL_HIGHLIGHT= URI_REST_SKILL_HIGHLIGHT+"/save";
	
	//Skill Group
	public static final String URI_REST_SKILL_GROUP = PREFIX_URI_V1 + "/skillgroup";
	public static final String URI_REST_SKILL_GROUP_ID = URI_REST_SKILL_GROUP + "/{id}";
	public static final String URI_REST_DELETE_SKILL_GROUP = URI_REST_SKILL_GROUP_ID + "/delete";
	
	//Skill Tag
	public static final String URI_REST_SKILL_TAG = PREFIX_URI_V1 + "/skilltags";
	public static final String URI_REST_SKILL_TAG_ID = PREFIX_URI_V1 + "/skilltags/{id}";
	public static final String URI_REST_SKILL_TAG_PROJECT = URI_REST_SKILL_TAG_ID + "/projects";
	public static final String URI_REST_SKILL_TAG_DELETE = URI_REST_SKILL_TAG_ID + "/delete";
	public static final String URI_REST_SKILL_TAG_UPDATE_ORDER = URI_REST_SKILL_TAG + "/update-order";
	public static final String URI_REST_SKILL_TAG_REPLACE = URI_REST_SKILL_TAG + "/replace";

	//Level
	public static final String URI_REST_LEVEL = PREFIX_URI_V1 + "/level";
	public static final String URI_REST_TEAM = PREFIX_URI_V1 + "/team";
	public static final String URI_REST_TEAM_ID = URI_REST_TEAM + "/{id}";
	public static final String URI_REST_TEAM_LINE_MANAGER = URI_REST_TEAM_ID + "/line_manager";
	public static final String URI_REST_DEPARTMENT_GROUP = PREFIX_URI_V1 + "/department_group";

    //associate
    public static final String URI_REST_PERSON = PREFIX_URI_V1 + "/associate";
    public static final String URI_REST_PERSON_FILTER = URI_REST_PERSON + "/filter";
    public static final String URI_REST_PERSON_ID = URI_REST_PERSON + "/{associate_id}";
    public static final String URI_REST_PERSON_CHECKEXIST = URI_REST_PERSON_ID + "/check-exist";
    public static final String URI_REST_PERSON_UPDATE = URI_REST_PERSON_ID + "/update";
    public static final String URI_REST_PERSON_EDIT = URI_REST_PERSON_ID + "/edit";
    public static final String URI_REST_PERSON_DELETE = URI_REST_PERSON_ID + "/delete";
    public static final String URI_REST_PERSON_NOT_EVALUATE = URI_REST_PERSON + "/not-evaluate/{manager_id}";
    public static final String URI_REST_PERSON_ID_EVALUATE = URI_REST_PERSON_ID + "/evaluate";
    public static final String URI_REST_PERSON_ID_EVALUATE_ID = URI_REST_PERSON_ID_EVALUATE + "/{evaluate_id}";
    public static final String URI_REST_PERSON_ID_PROJECT = URI_REST_PERSON_ID + "/project";
    public static final String URI_REST_PERSON_ID_NON_BOSCH_PROJECT = URI_REST_PERSON_ID_PROJECT + "/non-bosch";
    public static final String URI_REST_PERSON_ID_PROJECT_ID = URI_REST_PERSON_ID_PROJECT + "/{id}";
    public static final String URI_REST_PERSONAL_PROJECT_UPDATE = URI_REST_PERSON_ID_PROJECT_ID + "/update";
    public static final String URI_REST_PERSON_ID_COURSE = URI_REST_PERSON_ID + "/course";
    public static final String URI_REST_PERSON_ID_COURSE_ID = URI_REST_PERSON_ID_COURSE + "/{id}";
    public static final String URI_REST_PERSONAL_COURSE_UPDATE = URI_REST_PERSON_ID_COURSE_ID + "/update";
    public static final String URI_REST_PERSONAL_COURSE_DELETE = URI_REST_PERSON_ID_COURSE_ID + "/delete";
    public static final String URI_REST_PERSONAL_COURSE_CERTIFICATE = URI_REST_PERSON_ID_COURSE_ID + "/certificate";
    public static final String URI_REST_PERSONAL_COURSE_CERTIFICATE_DELETE = URI_REST_PERSONAL_COURSE_CERTIFICATE + "/delete";
    public static final String URI_REST_PERSON_ID_SKILL = URI_REST_PERSON_ID + "/skill";
    public static final String URI_REST_PERSON_ID_AVATAR = URI_REST_PERSON_ID + "/avatar";
    public static final String URI_REST_PERSON_ID_EXPORT = URI_REST_PERSON_ID + "/export";
    public static final String URI_REST_PERSONAL_PROJECT_DELETE = URI_REST_PERSON_ID_PROJECT_ID + "/delete";
    public static final String URI_REST_PERSONAL_UPDATE_EMAIL = URI_REST_PERSON + "/updatemail";
    public static final String URI_REST_PERSON_LIST_EXPORT = URI_REST_PERSON + "/export";

    public static final String URI_REST_ASSOCIATE_ID = PREFIX_URI_V1 + "/personal/{associate_id}";

    public static final String URI_REST_PERSON_ID_EVALUATE_ID_FORWARD = URI_REST_PERSON_ID_EVALUATE_ID + "/forward";
    public static final String URI_REST_PERSON_UPDATE_SKILL_GROUP_DATA = URI_REST_PERSON + "/update-skillgroup";

    //Request Evaluation
    public static final String URI_REST_REQUEST_EVALUATION = PREFIX_URI_V1 + "/evaluation";
    public static final String URI_REST_REQUEST_EVALUATED = PREFIX_URI_V1 + "/evaluated";
    public static final String URI_REST_REQUEST_EVALUATION_EVALUATION_ID = URI_REST_REQUEST_EVALUATION + "/{evaluation_id}";
    public static final String URI_REST_REQUEST_EVALUATION_APPROVER_ID = URI_REST_REQUEST_EVALUATION + "/approver/{personal_id}";
    public static final String URI_REST_REQUEST_EVALUATION_ID = URI_REST_REQUEST_EVALUATION + "/{id}";
    public static final String URI_REST_REQUEST_EVALUATION_UPDATE = URI_REST_REQUEST_EVALUATION_ID + "/update";
    public static final String URI_REST_REQUEST_EVALUATION_FORWARD = URI_REST_REQUEST_EVALUATION_ID + "/forward";
    public static final String URI_REST_REQUEST_EVALUATION_DETAIL_UPDATE = URI_REST_REQUEST_EVALUATION_ID + "/{detail_id}" + "/update";
    public static final String URI_REST_REQUEST_EVALUATION_PENDING = URI_REST_REQUEST_EVALUATION + "/request-pending/{personalId}";
	public static final String URI_REST_HISTORICAL_LEVEL = PREFIX_URI_V1 + "/historical-level/{personalId}";

    //Skill Evaluation
    public static final String URI_REST_SKILL_EVALUATION = PREFIX_URI_V1 + "/evaluate";
    public static final String URI_REST_SKILL_EVALUATION_ID = URI_REST_SKILL_EVALUATION + "/{evaluate_id}";
    public static final String URI_REST_SKILL_EVALUATION_PERSONAL_ID = URI_REST_SKILL_EVALUATION + "/personal/{personal_id}";

    // Groups
    public static final String URI_REST_GROUP = PREFIX_URI_V1 + "/groups";
    public static final String URI_REST_GROUP_UPDATE = "/update";
    public static final String URI_REST_GROUP_DELETE = "/{id}/delete";
    public static final String URI_REST_GROUP_ADD_USERS = "/addusers";
    public static final String URI_REST_GROUP_DEL_USERS = "/deluser";
    public static final String URI_REST_GROUP_ADD_ROLES = "/addroles";
    public static final String URI_REST_GROUP_DEL_ROLES = "/delrole";

    //common Tasks
    public static final String URI_REST_COMMONTASKS = PREFIX_URI_V1 + "/project-role/{id}/tasks";
    public static final String URI_REST_COMMONTASK = PREFIX_URI_V1 + "/common-task";
    public static final String URI_REST_COMMONTASK_DELETE = URI_REST_COMMONTASK + "/delete";

    //common
    public static final String URI_REST_COMMON_ADD_PERSONAL = PREFIX_URI_V1 + "/personal/upload";
    public static final String URI_REST_COMMON_ADD_PERSONAL_SKILL = PREFIX_URI_V1 + "/personal/skill/upload";
    public static final String URI_REST_COMMON_ADD_PERSONAL_SKILL_CLUSTER = PREFIX_URI_V1 + "/personal/skill_cluster/upload";

    //Department
    public static final String URI_REST_DEPARTMENT = PREFIX_URI_V1 + "/department";

    //Manager
    public static final String URI_REST_MANAGER = PREFIX_URI_V1 + "/manager";
    public static final String URI_REST_LINE_MANAGER = PREFIX_URI_V1 + "/line-manager/{idPersonal}";

    // Report
    public static final String URI_REST_REPORT = PREFIX_URI_V1 + "/report";
    public static final String URI_REST_REPORT_FILTER = URI_REST_REPORT + "/filter";
    public static final String URI_REST_REPORT_ASSOCIATE = URI_REST_REPORT + "/associate";
    public static final String URI_REST_REPORT_PROJECT = URI_REST_REPORT + "/project";

    //Supply Demand
    public static final String BASE_ENDPOINT_DEMAND = PREFIX_URI_V1 + "/demand";
    public static final String URI_REST_SUPPLY_DEMAND_CREATE = BASE_ENDPOINT_DEMAND + "/create";
    public static final String URI_REST_SUPPLY_DEMAND_CREATE_INFO = BASE_ENDPOINT_DEMAND + "/all-creation-info";
    public static final String URI_REST_SUPPLY_DEMAND_LIST_DEMAND = BASE_ENDPOINT_DEMAND + "/get-all";
    public static final String URI_REST_SUPPLY_DEMAND_DEMAND_BY_SUBID = BASE_ENDPOINT_DEMAND + "/get-by-subid/{subId}";
    public static final String URI_REST_SUPPLY_DEMAND_UPDATE = BASE_ENDPOINT_DEMAND + "/update";
    public static final String URI_REST_SUPPLY_DEMAND_DELETE = BASE_ENDPOINT_DEMAND + "/delete/{id}";
    public static final String URI_REST_SUPPLY_DEMAND_CHANGE_HISTORY = BASE_ENDPOINT_DEMAND + "/history/{id}";

	// Open Authentication 2 (OAuth2)
	public static final String URI_REST_OAUTH2 = "/oauth";
	public static final String URI_REST_OAUTH2_AUTHORIZE = "/authorize";
	public static final String URI_REST_OAUTH2_LOGIN = "/login-authorized";
	public static final String URI_REST_OAUTH2_TOKEN = "/token";
	public static final String URI_REST_OAUTH2_ERROR = "/error";
	public static final String URI_REST_OAUTH2_OAUTH_AUTHORIZE = URI_REST_OAUTH2 + URI_REST_OAUTH2_AUTHORIZE;
	public static final String URI_REST_OAUTH2_OAUTH_LOGIN = URI_REST_OAUTH2 + URI_REST_OAUTH2_LOGIN;
	
	//Elastic Search
	public static final String URI_REST_ELASTIC_IMPORT = PREFIX_URI_V1 + "/elastic";
	public static final String URI_REST_ELASTIC_UPDATE = URI_REST_ELASTIC_IMPORT + "/update";
	public static final String URI_REST_ELASTIC_QUERY = URI_REST_ELASTIC_IMPORT + "/search" +"/{index_name}";
    public static final String URI_REST_ELASTIC_QUERY_PERSONAL_NAME_NTID = URI_REST_ELASTIC_IMPORT + "/search/personal/name-ntid";
    public static final String URI_REST_ELASTIC_DEL = URI_REST_ELASTIC_IMPORT +"/del";
	
	//Insert skill for personal
	public static final String URI_REST_INSERT_SKILL = PREFIX_URI_V1 + "/insert";
	//Phase
	public static final String URI_REST_PHASE=PREFIX_URI_V1+ "/phase";
	public static final String URI_REST_PHASE_DROPDOWN=URI_REST_PHASE+ "/dropdown";
	// File Storage Rest
	public static final String URI_FILE_STORAGE = SLASH + "file";
	public static final String REST_FILE_STORAGE_UPLOAD = SLASH + "upload";
	public static final String REST_FILE_STORAGE_UPLOAD_DIRECTORY = SLASH + "upload-directory";
	public static final String REST_FILE_STORAGE_UPLOAD_IMAGE = SLASH + "upload-img";
	public static final String REST_FILE_STORAGE_UPLOAD_GENERIC_FILES = SLASH + "upload-generic";
	public static final String REST_FILE_STORAGE_DOWNLOAD = SLASH + "download";
	public static final String REST_FILE_STORAGE_DOWNLOAD_IMAGE = SLASH + "download-img";
	public static final String REST_FILE_STORAGE_DOWNLOAD_DOCUMENT = SLASH + "download-document";
	public static final String REST_FILE_STORAGE_DELETE_FILE = SLASH + "delete-file";
	
	// Org Chart Rest
	public static final String URI_ORG_CHART = SLASH + "org-chart";
	public static final String REST_ORG_CHART_GET_BY_ID = SLASH + "get-by-id";
	public static final String REST_ORG_CHART_SAVE = SLASH + "save";
	public static final String REST_ORG_CHART_DELETE = SLASH + "delete";
	
	//Skill type
	public static final String URI_REST_SKILL_TYPE = PREFIX_URI_V1 + "/skilltypes";
	
	//Project scope
	public static final String URI_REST_PROJECT_SCOPE = PREFIX_URI_V1 + "/project-scope";
    public static final String URI_REST_PROJECT_SCOPE_UPDATE = URI_REST_PROJECT_SCOPE + "/update";
    public static final String URI_REST_PROJECT_SCOPE_ID = URI_REST_PROJECT_SCOPE + "/{id}";
	public static final String URI_REST_PROJECT_SCOPE_DELETE = URI_REST_PROJECT_SCOPE_ID + "/delete";

    //Layout
    public static final String URI_REST_LAYOUT = PREFIX_URI_V1 + "/layout";

}
