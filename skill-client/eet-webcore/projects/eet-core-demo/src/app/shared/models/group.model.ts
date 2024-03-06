export interface GroupsModel {
  id: number | string;
  groupId: number | string;
  groupName: string;
  name: string;
  description: string;
  status: string;
  users: UserModel[];
  distributionlists: DistributionModel[];
  roles: RoleModel[];
}

export interface UserModel {
  displayName: string;
  id: number | string;
  isDistributionList: boolean;
}

export interface DistributionModel {
  displayName: string;
  id: number | string;
  isDistributionList: boolean;
  groupId: number | string;
}

export interface RoleModel {
  description: string;
  id?: number | string;
  name: string;
  permissionIds: string[];
  permissionCategories: string[]
}

export interface PermissionModel {
  code: string;
  id?: number | string;
  name: string;
  permissionDTOS: SubPermissionModel[];
  accepted:boolean;
}

export interface SubPermissionModel {
  accepted:boolean;
  belongsToRole: boolean;
  id?: number | string;
  name: string;
  description: string;
  status: string;
}

export type UserDistributionModel = UserModel | DistributionModel;
export type LdapUserDistributionModel = LdapDistributionModel | LdapUserModel;

export interface AddUserDistributionModel {
  groupId: number | string;
  ldapUsers: LdapUserModel[];
  distributionLists: LdapDistributionModel[];
}

export interface LdapUserModel {
  country: string;
  city: string;
  displayName: string;
  sAmAccountName: string;
  mail: string;
  phonenumber: string;
}

export interface LdapDistributionModel {
  city: string;
  country: string;
  displayName: string;
  email: string;
  phonenumber: string;
  name: string;
}

