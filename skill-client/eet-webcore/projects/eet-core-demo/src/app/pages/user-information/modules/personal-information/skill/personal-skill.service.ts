import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API, CORE_URL } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PersonalSkillService {
  constructor(public httpClient: HttpClient) { }
  getPersonSkill(id: string): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.ASSOCIATE}/${id}`);
  }
}
