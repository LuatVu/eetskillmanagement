import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API, CORE_URL, ELASTIC_DOCUMENT } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { BaseResponseModel } from 'projects/eet-core-demo/src/app/shared/models/base.model';
import { BehaviorSubject, Observable, map, of } from 'rxjs';
import { AddNewAssociateSharedModel } from './add-new-associate.model';
import * as DUMMY_DATA from './dumb-data.constants';
import { ElasticService } from 'projects/eet-core-demo/src/app/shared/services/elastic.service';

@Injectable({
  providedIn: 'root'
})
export class AddNewAssociateService {

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
    return this.httpClient.get(`${CORE_URL}/${API.GROUP}`);
  }

  getAllTeam() {
    return this.httpClient.get(`${CORE_URL}/${API.TEAM}`);
  }

  getAllTitle() {
    return of(DUMMY_DATA.TITLE);
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

  getGroupByTeam(id: string):Observable<BaseResponseModel> {
    return this.httpClient.get<BaseResponseModel>(`${CORE_URL}/${API.TEAM}/${id}`);
  }

  getLineManagerByTeam(id: string):Observable<BaseResponseModel> {
    return this.httpClient.get<BaseResponseModel>(`${CORE_URL}/${API.TEAM}/${id}/line_manager`);
  }

  searchLDAPUsers(searchUser: string) {
    return this.httpClient.get<BaseResponseModel>(`${API.ACCOUNT.LDAP_USERS}?queryParams=${searchUser}`);
  }

  addAssociate(form: any) {
    return this.httpClient.post(`${CORE_URL}/${API.ASSOCIATE}`, form);
  }

  checkAssociate(NTId: string){
    return this.httpClient.get(`${CORE_URL}/${API.ASSOCIATE}/${NTId}/${API.CHECK_EXIST}`);
  }

}
