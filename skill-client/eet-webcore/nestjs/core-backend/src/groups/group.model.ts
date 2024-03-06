export interface GroupModel {
  groupId: number;
  groupName: string;
  description: string;
  status: string;
  users: number[];
  roles: number[];
}

export interface GroupCreateDto {
  groupName: string;
  description: string;
}

export interface BaseReponseModel {
  status: string;
  message: string;
  data: any;
  timestamp: Date;
  code: string;
}
