import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { BaseResponseModel } from 'projects/eet-core-demo/src/app/shared/models/base.model';
import { environment } from 'projects/eet-core-demo/src/environments/environment';
import * as constants from '../../common/constants/constants';
import { map, Observable, take, of } from 'rxjs';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';

@Injectable({providedIn:'root'})
export class UserManagementService {
  constructor(private http: HttpClient, private loader: LoadingService) {

  }

  getAllGroups(): Observable<any> {
    return this.http
      .get<BaseResponseModel>(`${API.GROUPS}`)
      .pipe(take(1));
  }

  getAllRoles(): Observable<any> {
    return this.http
      .get<BaseResponseModel>(`${API.ROLES}`)
      .pipe(take(1));
  }

  getAllPermissions(): Observable<any> {
    return this.http
      .get<BaseResponseModel>(`${API.PERMISSION}`)
      .pipe(take(1));
  }

  getRoleDetailById(groupId: number | string): Observable<any> {
    return this.http
      .get<BaseResponseModel>(`${API.ROLES}/${groupId}`)
      .pipe(take(1));
  }

  getGroupDetailById(groupId: number | string): Observable<any> {
    return this.http
      .get<BaseResponseModel>(`${API.GROUPS}/${groupId}`)
      .pipe(take(1));
  }

  deleteGroup(groupId: number | string): Observable<any> {
    let formData : any = {groupId: groupId}

    return this.http
      .post<BaseResponseModel>(`${API.GROUPS}/${groupId}/delete`, formData)
      .pipe(take(1));
  }

  deleteUserGroup(data: any) {
    return this.http.post<BaseResponseModel>(`${API.GROUPS}/deluser/${data.groupId}`, data).pipe(take(1));
  }

  deleteRoleByGroupDetail(data: any, roleId: any) {
    return this.http.post<BaseResponseModel>(`${API.GROUPS}/delrole/${data.groupId}`, {
      "roleId": roleId
    }).pipe(take(1));
  }

  addRolesGroup(formData: any) {
    return this.http
      .post<BaseResponseModel>(
        `${API.GROUPS}/addroles/${formData.groupId}`,
        formData
      )
      .pipe(take(1));
  }

  deleteRole(roleId: number | string): Observable<any> {
    let formData : any = {roleId: roleId}
    return this.http
      .post<BaseResponseModel>(`${API.ROLES}/${roleId}/delete`,formData)
      .pipe(take(1));
  }

  createDataItem(type: number, data: any) {
    if (type == constants.TYPE.TYPE_GROUP) {
      return this.createGroup(data);
    } else {
      return this.createRole(data);
    }
  }

  updateRole(roleData: any) {
    return this.http
      .post<BaseResponseModel>(`${API.ROLES}/${roleData['id']}`, {
        name: roleData['name'],
        description: roleData['description'],
        permissionIds: roleData['permissionIds'],
      })
      .pipe(take(1));
  }


  updateGroup(groupData: any) {
    return this.http
      .post<BaseResponseModel>(`${API.GROUPS}/${groupData['groupId']}`, {
        groupName: groupData['name'],
        description: groupData['description'],
      })
      .pipe(take(1));
  }

  updatePermission(permissionData: any) {
    return this.http
      .post<BaseResponseModel>(`${API.PERMISSION}/${permissionData['id']}/${API.UPDATE}`, {
        name: permissionData['name'],
        description: permissionData['description'],
      })
      .pipe(take(1));
  }

  createGroup(groupData: any) {
    return this.http
      .post<BaseResponseModel>(`${API.GROUPS}`, {
        groupName: groupData['name'],
        description: groupData['description'],
      })
      .pipe(take(1));
  }

  createRole(roleData: any) {
    return this.http
      .post<BaseResponseModel>(`${API.ROLES}`, {
        name: roleData['name'],
        description: roleData['description'],
        status: 'Active'
      })
      .pipe(take(1));
  }


  // USER AND DISTRIBUTION
  searchLDAPUsers(searchUser: string) {
    return this.http.get<BaseResponseModel>(`${API.ACCOUNT.LDAP_USERS}?queryParams=${searchUser}`).pipe(take(1))
  }

  searchLDAPDistribution(searchDistribution: string) {
    return this.http.get<BaseResponseModel>(`${API.ACCOUNT.LDAP_DISTRIBUTION_LIST}?queryParams=${searchDistribution}`).pipe(take(1));
  }

  addUsers(data: any) {
    return this.http.post<BaseResponseModel>(`${API.GROUPS_API.ADD_USERS}/${data.groupId}`, data).pipe(take(1));
  }

}
