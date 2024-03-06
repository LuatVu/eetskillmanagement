import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CORE_URL, API } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ManageSkillService {

  constructor(public httpClient: HttpClient) { }

  getSkillData(): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.SKILL}/${API.EXPERIENCE}`);
  }

  getDetailSkillData(skillId: string) {
    return this.httpClient.get(`${CORE_URL}/${API.SKILL}/${API.EXPERIENCE}/${skillId}`);
  }

  //delete Skill Data
  deleteSkillData(skill_id: string): Observable<any> {
    let formData: any = {skill_id : skill_id}
    return this.httpClient.post(`${CORE_URL}/${API.SKILL}/${skill_id}/delete`,formData)
  }
}
