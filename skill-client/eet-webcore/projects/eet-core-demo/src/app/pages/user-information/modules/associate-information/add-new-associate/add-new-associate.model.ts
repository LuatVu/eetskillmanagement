import { AssociateModel } from "../associate-info.model";

export interface AddNewAssociate {
  name: string;
  ntid: string;
  email: string;
  group: string;
  title: string;
  joinDate: any;
  skillGroup: string[];
  level: string;
  gender?: 'Male | Female';
  department: string;
  team: string;
  location: string;
  lineManager: string;
  nonBoschExp?: number;
}
export interface GroupModel {
  displayName: string;
  groupId: string;
}

export interface CommonModel {
  name: string;
  code: string;
}

export interface AddNewAssociateSharedModel {
  isStoredState: boolean;
  levelList: Array<string>;
  genderList: Array<any>;
  departmentList: Array<any>;
  groupList: Array<GroupModel>;
  teamList: Array<any>;
  titleList: Array<any>;
  locationList: Array<any>;
  lineManagerList: Array<AssociateModel>;
  skillGroupList: Array<any>;
}

export interface LdapUserModel {
  displayName: string;
}
