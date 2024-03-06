import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CORE_URL, API } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { BaseResponseModel } from 'projects/eet-core-demo/src/app/shared/models/base.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TechnicalCompetenciesService {
  constructor(public httpClient: HttpClient) { }
  getSkillData(page : number, size :number, skillCluster? : string): Observable<BaseResponseModel> {
      let params = new HttpParams();
      if (page !== undefined) params = params.set('page', page.toString());
      if (size !== undefined) params = params.set('size', size.toString());
      if (skillCluster !== undefined) params = params.set('skillCluster', skillCluster);
      const options = { params: params }; 

      return this.httpClient.get<BaseResponseModel>(`${CORE_URL}/${API.SKILL}/${API.EXPECTED}`, options);
  }
  getAllLevel(): Observable<any> {
      return this.httpClient.get(`${CORE_URL}/${API.LEVEL}`);
  }
  getSkillGroupData():Observable<any>{
      return this.httpClient.get(`${CORE_URL}/${API.SKILL_GROUP}`)
  }
  getDetailSkillData(skillId: string) {
      return this.httpClient.get(`${CORE_URL}/${API.SKILL}/${API.EXPERIENCE}/${skillId}`);
  }
}
