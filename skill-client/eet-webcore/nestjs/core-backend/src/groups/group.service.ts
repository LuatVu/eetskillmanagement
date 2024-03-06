import { Injectable } from '@nestjs/common';
import { readFile, writeFile } from 'fs/promises';
import { join } from 'path';
import { GroupCreateDto, GroupModel } from './group.model';

@Injectable()
export class GroupService {
  filePath: string = join(process.cwd(), './data/user-management-data.json');

  getAllGroups() {
    return readFile(this.filePath).then((fileData) => {
      const groupLst: GroupModel[] = JSON.parse(fileData.toString())['groups'];
      return {
        status: groupLst ? 'SUCCESS' : 'FAILED',
        timestamp: new Date(),
        data: groupLst || null,
        message: groupLst ? '' : 'NOT_FOUND',
      };
    });
  }

  getGroupDetail(groupId: number) {
    return readFile(this.filePath).then((fileData) => {
      const groupLst: GroupModel[] = JSON.parse(fileData.toString())['groups'];
      const group = groupLst.find((f) => f.groupId == groupId);
      return {
        status: group ? 'SUCCESS' : 'FAILED',
        timestamp: new Date(),
        data: group || null,
        message: group ? '' : 'NOT_FOUND',
      };
    });
  }

  createGroup(createGroupBody: GroupCreateDto) {
    return readFile(this.filePath).then((fileData) => {
      const currentData: any = JSON.parse(fileData.toString());
      const groupLst: GroupModel[] = currentData['groups'];
      groupLst.sort((a: GroupModel, b: GroupModel) => a.groupId - b.groupId);
      const newGroup: GroupModel = {
        groupId: groupLst[groupLst.length - 1].groupId + 1,
        groupName: createGroupBody.groupName,
        description: createGroupBody.description,
        status: 'Active',
        users: [],
        roles: [],
      };
      currentData['groups'] = [...groupLst, newGroup];

      return writeFile(this.filePath, JSON.stringify(currentData))
        .then(() => {
          return {
            status: 'SUCCESS',
            timestamp: new Date(),
            data: newGroup,
            message: '',
          };
        })
        .catch(() => {
          return {
            status: 'FALIED',
            timestamp: new Date(),
            data: null,
            message: 'CREATED_GROUP_FAILED',
          };
        });
    });
  }
}
