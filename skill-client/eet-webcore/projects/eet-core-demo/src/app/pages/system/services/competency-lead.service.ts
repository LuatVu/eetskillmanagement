import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { API } from '../../../shared/constants/api.constants';
import { Observable } from 'rxjs';
import { BaseResponseModel } from '../../../shared/models/base.model';
import { SkillGroupModel } from '../model/competency-lead/competency-lead.model';

@Injectable({
  providedIn: 'root'
})
export class CompetencyLeadService {

  constructor(protected http: HttpClient) { }

  getCompetency() {
    return this.http.get<any>(`${CoreUrl.API_PATH}/${API.SKILL_GROUP}`)
  }
  getCompetencyLead(id: string) {
    return this.http.get<any>(`${CoreUrl.API_PATH}/${API.COMPETENCY_LEAD}/${id}`)
  }
  getSkillGroupDetail(id: string) {
    return this.http.get<any>(`${CoreUrl.API_PATH}/${API.SKILL_GROUP}/${id}`)
  }
  addCompetency(payload: SkillGroupModel) {
    return this.http.post<any>(`${CoreUrl.API_PATH}/${API.SKILL_GROUP}/`, payload)
  }
  deleteCompetency(id: string) {
    return this.http.post<any>(`${CoreUrl.API_PATH}/${API.SKILL_GROUP}/${id}/${API.DELETE}`, {})
  }

  deleteCompetencyLead(personal_id: string, skill_group_id: string): Observable<BaseResponseModel> {
    return this.http.post<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.COMPETENCY_LEAD}/delete`, { personal_id, skill_group_id });
  }

  addCompetencyLead(data: any) {
    return this.http.post<any>(`${CoreUrl.API_PATH}/${API.COMPETENCY_LEAD}`, data)
  }

  getTags(){
    return this.http.get<any>(`${CoreUrl.API_PATH}/${API.SKILL}`);
  }

  getSkillDetail(id: string) {
    return this.http.get(`${CoreUrl.API_PATH}/${API.SKILL}/${API.EXPERIENCE}/${id}`);
  }

  deleteSkill(skillId: string): Observable<any> {
    return this.http.post(`${CoreUrl.API_PATH}/${API.SKILL}/${skillId}/delete`, {});
  }

  getSkillType(): Observable<BaseResponseModel>{
    return this.http.get<BaseResponseModel>(`${CoreUrl.API_PATH}/skilltypes`);
  }
}
