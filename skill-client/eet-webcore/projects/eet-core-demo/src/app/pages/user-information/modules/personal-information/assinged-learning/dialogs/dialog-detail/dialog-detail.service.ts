import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CORE_URL, API } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DialogDetailService {
  constructor(public httpClient: HttpClient) { }
  getPersonLeanringDetail(id: string, idlearning: string): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.ASSOCIATE}/${id}/${API.COURSE}/${idlearning}`);
  }
}
