import { BasePaginationModel } from "projects/eet-core-demo/src/app/shared/models/base.model";

export interface PaginationModel {
  size: number;
  page: number;
}

export class SupplyPaginationModel extends BasePaginationModel {
  public team: string[];
  public skill: string[];
  public ["skill-group"]: string[];
  public exp: string[];
}

export interface SupplyListResponseModel {
  listSupply: Array<any>;
  total: number;
}

export interface SupplyFilterResponse {
  skill_filter: string[];
  skill_group_filter: string[];
  team_filter: string[];
  exp: string[];
}

export interface SupplyModel {
  id: string;
  subId: string;
  projectId: string;
  status: string;
  projectName: string;
  skillClusterId: string;
  skillClusterName: string;
  skillId: string;
  skill: string;
  level: string;
  assignee: string;
  assignUserName: string;
  candidateName: string;
  supplyType: string;
  location: string;
  expectedDate: Date;
  forecastDate: Date;
  filledDate: Date;
  createdByNtid: string;
  createdByName: string;
  createdDate: Date;
  updatedByName: string
  updatedByNtid: string
  updatedDate: Date;
  note: string;
  canUpdate: boolean;
  canDelete: boolean;
  allowExternal: boolean;
  assignNtId: string;
}

export interface FilterModel {
  key: string;
  name: string;
  originalData: string[];
  selectedData: string[] | string;
  isMultiple: boolean;
  function?: any
}

export interface FilterDataModel {
  projectName: string[];
  skillClusterName: string[];
  createdByName: string[];
  assignUserName: string[];
  status: string[];
}

export interface AssigneeUsers {
  id: string;
  ntId: string;
  name: string;
}

export interface Project {
  id:string,
  name:string
}