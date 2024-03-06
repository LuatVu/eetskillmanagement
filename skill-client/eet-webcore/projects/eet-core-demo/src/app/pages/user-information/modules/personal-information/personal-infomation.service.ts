import {
  HttpClient
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API, CORE_URL } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { Observable, retry, BehaviorSubject, take } from 'rxjs';
import { PersonalInfomationModel, ProjectModel } from './personal-infomation.model';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { BaseResponseModel } from 'projects/eet-core-demo/src/app/shared/models/base.model';

@Injectable({
  providedIn: 'root',
})
export class PersonalInfomationService {
  public personalInfoDetail: BehaviorSubject<Partial<PersonalInfomationModel>> = new BehaviorSubject<Partial<PersonalInfomationModel>>({});
  public sharedData: BehaviorSubject<any> = new BehaviorSubject<any>({});
  public projectCloneList: ProjectModel[] = [];

  getPersonalInfoDetail() {
    return this.personalInfoDetail.asObservable();
  }

  setPersonalInfoDetail(data: any) {
    this.personalInfoDetail.next({ ...this.personalInfoDetail.value, ...data });
  }

  setProjectListState(project: ProjectModel) {
    const index = this.projectCloneList.findIndex(f => f.project_id == project.project_id);
    if (index != -1) {
      this.projectCloneList[index] = project;
    }
    this.setPersonalInfoDetail({ projects: this.projectCloneList });
  }

  getSharedData() {
    return this.sharedData.asObservable();
  }

  setSharedData(data: any) {
    this.sharedData.next({ ...this.sharedData.value, ...data });
  }

  constructor(public httpClient: HttpClient) { }

  getPersonInfo(id: string): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.ASSOCIATE}/${id}`);
  }

  getExport(id: string): Observable<any> {
    return this.httpClient
      .get(`${CORE_URL}/${API.ASSOCIATE}/${id}/${API.EXPORT}`, { responseType: 'blob' })
      .pipe(retry(3));
  }

  getSkillDetail(id: string) {
    return this.httpClient.get(`${CORE_URL}/${API.SKILL}/${API.EXPERIENCE}/${id}`)
  }

  savePersonalInfo(personalInfo: any): Observable<any> {
    return this.httpClient.post(`${CORE_URL}/${API.ASSOCIATE}/${personalInfo.id}/update`, personalInfo)
  }


  get _idUser(): string {
    return this.personalInfoDetail.value['id'] || '';
  }
  
  isExpYearValid(expYear: string){
    if(expYear === null) return false;
    return Number.isInteger(Number(expYear)) 
      && Number(expYear) >= CONFIG.PERSONAL_INFORMATION.NON_BOSCH_EXP_YEAR.MIN 
      && Number(expYear) <= CONFIG.PERSONAL_INFORMATION.NON_BOSCH_EXP_YEAR.MAX ? true: false;
  }
  isBriefInfoValid(briefInfo: string){
    return (briefInfo && briefInfo.trim().length === 0 && briefInfo.length > 0) ? false : true;
  }
  isEditInforValid(){
    return this.isExpYearValid(this.personalInfoDetail.value.experienced_non_bosch!)
      && this.isBriefInfoValid(this.personalInfoDetail.value.brief_info!);
  }

  getHistorycalLevel(page: number, size: number, personalId: string, skillCluster: string): Observable<BaseResponseModel>{
    return this.httpClient.get<BaseResponseModel>(`${CORE_URL}/${API.HISTORICAL_LEVEL}/${personalId}?page=${page}&size=${size}&skillCluster=${skillCluster}`);
  }

}
