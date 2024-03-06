import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CORE_URL, API } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { Observable, take } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SkillDetailDialogService {

  constructor(public httpClient: HttpClient) { }

  addSkillData(formData: any) {
    return this.httpClient.post(`${CORE_URL}/${API.SKILL}`, formData).pipe(take(1));
  }

  updateSkillData(formData: any) {
    return this.httpClient.post(`${CORE_URL}/${API.SKILL}/${formData.id}/edit`, formData).pipe(take(1));
  }

  getCompetencyLeadData(): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.ASSOCIATE}`).pipe(take(1));
  }

  getSkillGroupData(): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.SKILL_GROUP}`).pipe(take(1));
  }

  getSkillCompetenceLead(id: string): Observable<any> {
    // let queryParams = new HttpParams();
    // queryParams.set("skill-group-id", id)
    // return this.httpClient.get(`${CORE_URL}/${API.COMPETENCY_LEAD}/${API.SKILLGROUP}`, {params: queryParams});
    return this.httpClient.get(`${CORE_URL}/${API.COMPETENCY_LEAD}/${API.SKILLGROUP}?skill-group-id=${id}`);
  }
}
