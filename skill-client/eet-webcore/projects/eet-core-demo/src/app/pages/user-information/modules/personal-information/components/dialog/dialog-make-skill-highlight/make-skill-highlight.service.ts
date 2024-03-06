import { Injectable } from '@angular/core';
import {
  HttpClient
} from '@angular/common/http';
import { Observable, retry, BehaviorSubject, take } from 'rxjs';
import { CORE_URL, API } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';

@Injectable({
  providedIn: 'root'
})
export class MakeSkillHighlightService {

  constructor(public httpClient: HttpClient) { }

  getSkillsByPersonalId(id: string): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.SKILL}/${API.HIGHLIGHT}/${id}`)
  }

  postSkillsHighlightByPersonalId(skillIds: string[], userId: string): Observable<any> {
    return this.httpClient.post(`${CORE_URL}/${API.SKILL}/${API.HIGHLIGHT}/${userId}/${API.SAVE}`, skillIds)
  }
  
}
