import { LEARNING_STATUS } from "../../pages/career-development/components/learning/my-learning/model/my-learning.model";

export const CONFIG = {
  API_RESPONSE_STATUS: {
    SUCCESS: 'SUCCESS',
    ERROR: 'VALIDATION_ERRORS',
    ADD_ASSOCIATE_SUCCESSFUL: "ADD_ASSOCIATE_SUCCESSFUL",
    UPDATE_ORDER_TAG_SUCCESSFUL:"UPDATE_INDEX_SUCCESS"
  },
  USER_DISTRIBUTION_TYPES: {
    USER: 'User',
    DISTRIBUTION: 'DistributionList'
  },
  TABLET_MIN_WIDTH: 768, // (768px)
  TYPE_USER: {
    ASSOCIATE: 'Associate',
    MANAGER: 'Manager'
  },
  LEARNING_COURSE_TYPE: [{
    value: "Self Study",
    viewValue: "self_study"
  },
  {
    value: "R&D",
    viewValue: "rnd"
  },
  {
    value: "Class room",
    viewValue: "classroom"
  },
  {
    value: "Online",
    viewValue: "online"
  }
  ],
  LEARNING_COURSE_STATUS: [{
    value: LEARNING_STATUS.ON_GOING,
    viewValue: "on_going"
  },
  {
    value: LEARNING_STATUS.NEW,
    viewValue: "new"
  },
{
  value: LEARNING_STATUS.CLOSED,
  viewValue: 'closed'
}],
  AVATAR_COLOR_LIST: [
    {
      color: '#006ead',
      backgroundColor: '#cce2ef',
    },
    {
      color: '#d50005',
      backgroundColor: '#f7cccd',
    },
    {
      color: '#219557',
      backgroundColor: '#d2eadd',
    },
    {
      color: '#fbb03b',
      backgroundColor: '#feefd7',
    },
    {
      color: '#50237f',
      backgroundColor: '#dcd3e5',
    },
    {
      color: '#e02595',
      backgroundColor: '#f9d3ea',
    },
  ],
  PAGINATION_OPTIONS: [5, 10, 20, 100],
  PAGINATION: {
    DEFAULT_PAGE: 0,
    DEFAULT_SIZE: 5
  },
  FILTER_ALL: 'FILTER_ALL',
  PROJECT_STATUS_LIST: ["On-going", "Closed"],
  REQUEST_STATUS: {
    APPROVAL_PENDING: 'APPROVAL_PENDING',
    APPROVED: 'APPROVED',
    REJECTED: 'REJECTED',
    CANCELLED: 'CANCELLED'
  },
  PERMISSIONS: {
    VIEW_PERSONAL_PERMISSION: "VIEW_PERSONAL_PERMISSION",
    VIEW_ASSOCIATE_INFO_PERMISSION: "VIEW_ASSOCIATE_INFO_PERMISSION",
    EDIT_ASSOCIATE_INFO_PERMISSION: "EDIT_ASSOCIATE_INFO_PERMISSION",
    VIEW_DEPARTMENT_PROJECT: "VIEW_DEPARTMENT_PROJECT",
    VIEW_ALL_PROJECTS: "VIEW_ALL_PROJECTS",
    VIEW_SYSTEM: "VIEW_SYSTEM",
    VIEW_EXPECTED_SKILL_LEVEL: "VIEW_EXPECTED_SKILL_LEVEL",
    VIEW_SKILL_EVALUATE: "VIEW_SKILL_EVALUATE",
    VIEW_DEPARTMENT_LEARNING: "VIEW_DEPARTMENT_LEARNING",
    VIEW_MANAGE_REQUEST: "VIEW_MANAGE_REQUEST",
    VIEW_USER_MANAGEMENT: "VIEW_USER_MANAGEMENT",
    VIEW_REPORT: "VIEW_REPORT",
    VIEW_VMODEL: "VIEW_VMODEL",

    //Permission for project
    ASSIGN_ASSOCIATE_TO_PROJECT: "ASSIGN_ASSOCIATE_TO_PROJECT",
    VIEW_PROJECT_DETAIL: "VIEW_PROJECT_DETAIL",
    EDIT_PROJECT: "EDIT_PROJECT",
    DELETE_PROJECT:"DELETE_PROJECT",
    ADD_BOSCH_PROJECT:"ADD_BOSCH_PROJECT",
    EDIT_PORTFOLIO: "EDIT_PORTFOLIO",
    //Permission for project
    USER_DEMAND_SUPPLY: "USER_DEMAND_SUPPLY",
    ADMIN_DEMAND_SUPPLY: "ADMIN_DEMAND_SUPPLY",

    EDIT_EET_OVERVIEW: "EDIT_EET_OVERVIEW",
    EDIT_EET_ORG_CHART: "EDIT_EET_ORG_CHART",
    EDIT_HELP: "EDIT_HELP"
  },

  SKILL_HIGHLIGHT: {
    MAX_SKILL_HIGHLIGHT: 6
  },

  REPORT: {
    COLORS: [
      "#FF6384", // Red
      "#36A2EB", // Blue
      "#FFCE56", // Yellow
      "#4BC0C0", // Teal
      "#9966FF", // Purple
      "#FF9F40", // Orange
      "#28A745", // Green
      "#DC3545", // Dark Red
      "#007BFF", // Dark Blue
      "#FFC107", // Dark Yellow
      "#20C997", // Dark Teal
      "#8C6DD7", // Dark Purple
      "#FF8C00", // Dark Orange
      "#218838", // Dark Green
    ]
  },

  VMODEL:{
    COLORS:[
      "#9E7F00", //brown
      "#007BC0", //Blue
      "#C535BC", //Pink
      "#218838", // Dark Green
    ]
  },

  PROJECT: {
    PROJECT_TYPE: {
      BOSCH: 'Bosch',
      NON_BOSCH: 'Non-Bosch'
    },
    OPEN_PROJECT_TYPE: {
      ADD: 'add',
      VIEW: 'view',
      EDIT: 'edit'
    },
    PROJECT_LIST: 'project-list',
    V_MODEL: 'v-model',
  },
  
  CUSTOMER_GB: {
    CUSTOMER_GB: 'customer-gb',
    PORTFOLIO: 'portfolio',
    DIRECTIONS: ['TOP','RIGHT','BOTTOM','LEFT'],
    COLOR_STYLE: [
      {color:'#00A4FD',backgroundColor:'#007BC0'},
      {color:'#ffce56',backgroundColor:'#9E7F00'},
      {color:'#4AB073',backgroundColor:'#2E908B'},
      {color:'#ff9f40',backgroundColor:'#FF8787'},
      {color:'#00A4FD',backgroundColor:'#9DC9FF'},
      {color:'#ff9f40',backgroundColor:'#2e908b'}
    ]  
  },

  GB_UNIT:['MS', 'PS'],
  PERSONAL_INFORMATION : {
    NON_BOSCH_EXP_YEAR: {
      MIN: 0,
      MAX: 99
    }
  },

  DIRECTION_TOP_EXPAND_DROPDOWN_PAGINATION : 'expand-top',
  DIRECTION_BOTTOM_EXPAND_DROPDOWN_PAGINATION : 'expand-bottom',
  COMMON_DIALOG: {
    DIALOG_CONTAIN_CONTENT_ID: 'frontend-kit-dialog-alert-dialog-info-description'
  },

  COMMON_FORM: {
    NOT_APPLICABLE: '--'
  },

  EXPECTED_SKILL_LEVEL_VALUE_OPTIONS:[0, 1, 1.5, 2, 2.5, 3, 3.5, 4],
  TECHNICAL_EXPECTED_SKILL_LEVEL_VALUE_OPTIONS:[0, 1, 2, 3, 4],
  BEHAVIORAL_EXPECTED_SKILL_LEVEL_VALUE_OPTIONS:[0, 1, 1.5, 2, 2.5, 3, 3.5, 4],
  EXPECTED_SKILL_LEVEL: {
    SKILL_TYPE: {
      TECHNICAL_SKILL:'Technical', 
      BEHAVIORAL_SKILL:'Behavioral'
    },
  }
} as const;
