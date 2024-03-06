export enum TYPE {
  TYPE_GROUP = 1,
  TYPE_ROLE = 2,
  TYPE_PERMISSION = 3,
}

export enum LOCATION {
  HCM = "HCM",
  HN = "HN"
}

export enum ASSIGNEE {
  UNASSIGNED = "Unassigned",
  TALENT_MANAGER = "Talent Manager",
  RECRUITMENT_TEAM = "Recruitment Team",
  OUTSOURCING_MANAGER = "Outsourcing Manager"
}

export enum SUPPLY_TYPE {
  INTERNAL = "Internal",
  EXTERNAL = "External",
  RECRUITMENT = "Recruitment"
}

export enum SUPPLY_STATUS {
  DRAFT = "Draft",
  OPEN = "Open",
  ON_HOLD = "On hold",
  ON_GOING = "On going",
  CANCEL = "Canceled",
  FILLED = "Filled",
}

export const WIDTH_OF_CREATE_DIALOG = '500px';

export const WIDTH_OF_ADD_LIST_USER_DIALOG = '570px';

export const WIDTH_OF_CONFIRMATION_DIALOG = 'auto';

export const USER_MANAGEMENT_TAB = [
  {name: 'user_management.groups_users_submenu', routeName:'groups-users'},
  {name: 'user_management.roles_submenu', routeName:'roles'},
  {name: 'user_management.permissions_submenu', routeName:'permissions'}
]