import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CORE_URL, API } from '../../shared/constants/api.constants';

@Injectable({
  providedIn: 'root',
})
export class ManageRequestService {
  constructor(public httpClient: HttpClient) {}

  getRequestData(): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.EVALUATION}?requester=false`);
  }

  getPreviousRequestData(): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.EVALUATED}`);
  }


}
