import { BasePaginationModel } from "projects/eet-core-demo/src/app/shared/models/base.model";

export interface PaginationModel {
  size: number;
  page: number;
}

export class AssociatePaginationModel extends BasePaginationModel {
  public team: string[];
  public skill: string[];
  public ["skill-group"]: string[];
  public exp: string[];
}

export interface AssociateListResponseModel {
  listPersonal: Array<any>;
  total: number;
}

export interface AssociateFilterResponse {
  skill_filter: string[];
  skill_group_filter: string[];
  team_filter: string[];
  exp: string[];
  level_filter: string[];
}

export interface AssociateModel {
  personalCode: string;
  skills: string[];
  personalId: string;
  level: string;
  displayName: string;
  skillGroups: string[];
  location: string;
  team: string;
  id: string;
  experience: number;
  department: string;
  gbUnit: string;
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
  skillList: string[];
  skillGroupList: string[];
  teamList: string[];
  expList: string[];
  levelList: string[];
}

export interface SkillGroup {
  name:string,
  skill_type:string,
  number_associate:number
}