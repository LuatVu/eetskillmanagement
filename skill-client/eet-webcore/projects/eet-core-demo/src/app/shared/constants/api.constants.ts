import { CoreUrl } from 'nestjs/core-backend/src/constants/apiLocation.constant';
import { environment } from 'projects/eet-core-demo/src/environments/environment';

export const API = {
  ACCOUNT: {
    LDAP_USERS: `api/${environment.apiVersion}/search/user`,
    LDAP_DISTRIBUTION_LIST: `api/${environment.apiVersion}/search/dblist`,
  },
  GROUPS: `api/${environment.apiVersion}/groups`,
  GROUPS_API: {
    ADD_USERS: `api/${environment.apiVersion}/groups/addusers`,
  },
  ROLES: `api/${environment.apiVersion}/role`,
  PERMISSION: `api/${environment.apiVersion}/permissions`,
  COURSE: 'course',
  EXPORT: 'export',
  PROJECT: 'project',
  PROJECTS: 'projects',
  SKILLTAG: 'skilltags',
  TAG: 'tag',
  ASSOCIATE: 'associate',
  PERSONAL: 'personal',
  COMPETENCY_LEAD: 'competency-lead',
  MANAGE_PROJECT_ROLE: 'manage-project-role',
  MANAGE_SKILL: 'manage-skill',
  COMMON_CONFIG: 'common-config',
  EXPECT_SKILLS_LEVEL_FOR_ASSOCIATE: 'expect-skills-level-for-associate',
  FILTER: 'filter',
  SKILL: 'skill',
  ROLE: 'project-role',
  COMMON_TASK: 'common-task',
  GB: 'gb',
  CUSTOMER_GB:'customer-gb',
  AVATAR: 'avatar',
  LEVEL: 'level',
  CATEGORY: 'category',
  EVALUATION: 'evaluation',
  EVALUATED: 'evaluated',
  DEPARTMENT: 'department',
  GROUP: 'groups',
  DEPARTMENT_GROUP: 'department_group',
  SKILL_GROUP: 'skillgroup',
  SKILLGROUP:'skill-group',
  SKILL_TASK: 'skilltags',
  MANAGER: 'manager',
  EXPERIENCE: 'experience',
  ELASTIC_SEARCH: 'elastic/search',
  MEMBERS: 'members',
  ASSIGN: 'assign',
  TEAM: 'team',
  TITLE: 'title',
  HIGHLIGHT: 'highlight',
  DEMAND: 'demand',
  ADDITIONAL_TASK: 'additional-task',
  SKILL_TAG:'skilltags',
  CHECK_EXIST: 'check-exist',
  UPDATE: 'update',
  DELETE: 'delete',
  EDIT: 'edit',
  SAVE: 'save',
  MANAGE:'manage',
  HISTORICAL_LEVEL: 'historical-level',
  ALL_CREATION_INFO: 'demand/all-creation-info',
  CREATE: 'demand/create',
  UPDATE_ORDER_TAG_SKILL:"update-order",
  REPLACE:"replace",
  LINE_MANAGER_ROUTE: "line-manager",
  LINE_MANAGER:"line_manager",
  NON_BOSCH: "non-bosch",
  NAME_OR_NTID: "name-ntid",
  EXPECTED: 'expected',
  SKILL_TYPE: 'skilltypes',
  CUSTOMER: 'customer',
  DEL: 'del',
  PROJECT_SCOPE:'project-scope',
  PORTFOLIO:"portfolio"
};

export const ELASTIC_DOCUMENT = {
  PERSONAL: 'personal',
  SKILL: 'skill',
  PROJECTS: 'project'
};

export const CORE_URL = `api/${environment.apiVersion}`;

export const LEARNING = {
  COURSE: `${CoreUrl.API_PATH}/${API.COURSE}`,
  CATEGORY: `${CoreUrl.API_PATH}/${API.CATEGORY}`,
  LEVEL: `${CoreUrl.API_PATH}/${API.LEVEL}`
};