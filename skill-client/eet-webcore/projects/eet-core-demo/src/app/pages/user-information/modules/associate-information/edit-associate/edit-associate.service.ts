import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CORE_URL, API } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { BaseResponseModel } from 'projects/eet-core-demo/src/app/shared/models/base.model';
import { ElasticService } from 'projects/eet-core-demo/src/app/shared/services/elastic.service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { AddNewAssociateSharedModel } from '../add-new-associate/add-new-associate.model';
import * as DUMMY_DATA from './dumb-data.constants';
import {TITLE } from "../add-new-associate/dumb-data.constants"
@Injectable({
  providedIn: 'root'
})

export class EditAssociateService {

  public sharedData: BehaviorSubject<Partial<AddNewAssociateSharedModel>> = new BehaviorSubject<Partial<AddNewAssociateSharedModel>>({
    isStoredState: false
  });

  constructor(private httpClient: HttpClient, private elasticService: ElasticService) { }

  setSharedData(data: any) {
    this.sharedData.next({ ...this.sharedData.value, ...data });
  }

  getSharedData(): Observable<Partial<AddNewAssociateSharedModel>> {
    return this.sharedData.asObservable();
  }

  getAllLevel() {
    return this.httpClient.get(`${CORE_URL}/${API.LEVEL}`);
  }

  getAllGender() {
    return of(DUMMY_DATA.GENDER);
  }

  getAllDepartment() {
    return this.httpClient.get(`${CORE_URL}/${API.DEPARTMENT}`);
  }

  getAllGroup() {
    return this.httpClient.get(`${CORE_URL}/${API.DEPARTMENT_GROUP}`);
  }

  getAllTeam() {
    return this.httpClient.get(`${CORE_URL}/${API.TEAM}`);
  }

  getAllTitle() {
    return of(TITLE);
  }

  getAllLocation() {
    return of(DUMMY_DATA.LOCATION);
  }

  getAllLineManager() {
    // return this.elasticService.getDocument(ELASTIC_DOCUMENT.PERSONAL, {
    //   query: "",
    //   size: 10000,
    //   from: 0
    // });
    return this.httpClient.get(`${CORE_URL}/${API.MANAGER}`);
  }

  getAllSkillGroup() {
    return this.httpClient.get(`${CORE_URL}/${API.SKILL_GROUP}`);
  }
  getAssociateDetailData(id: string): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.PERSONAL}/${id}`);
  }

  getGroupByTeam(id: string): Observable<BaseResponseModel> {
    return this.httpClient.get<BaseResponseModel>(`${CORE_URL}/${API.TEAM}/${id}`);
  }
  
  getLineManagerByTeam(id: string):Observable<BaseResponseModel> {
    return this.httpClient.get<BaseResponseModel>(`${CORE_URL}/${API.TEAM}/${id}/line_manager`);
  }

  searchLDAPUsers(searchUser: string) {
    return this.httpClient.get<BaseResponseModel>(`${API.ACCOUNT.LDAP_USERS}?queryParams=${searchUser}`);
  }

  editAssociate(id: string, form: any) {
    return this.httpClient.post(`${CORE_URL}/${API.ASSOCIATE}/${id}/${API.EDIT}`, form);
  }
}
