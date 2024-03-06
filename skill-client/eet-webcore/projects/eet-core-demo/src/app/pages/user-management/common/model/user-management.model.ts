export interface UserGroupModel {
  groupId: number | string;
  groupName: string;
  description: string;
  status: string;
  users: number[] | string[];
  roles: number[] | string[];
}

export interface RoleModel {
  id: number | string;
  roleID: number | string;
  roleName: string;
  displayName: string;
}

export interface UserOrDistributionModel {

}

export interface UserAndDistributionModel extends LdapUserModel, DistributionModel {
}

export interface LdapUserModel {
  country: string;
  city: string;
  displayName: string;
  sAmAccountName: string;
  mail: string;
  phonenumber: string;
}

export interface DistributionModel {
  city: string;
  country: string;
  displayName: string;
  email: string;
  phonenumber: string;
  isDistributionList: boolean;
  childGroups: ChildGroupModel[]
}

export interface ChildGroupModel {

}

export interface UserManagementModel {
  name: string;
  routeName: string;
}