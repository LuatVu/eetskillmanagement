import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API, CORE_URL } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { Observable, Subject } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class ViewRequestDetailService {

  pendingRequest = new Subject();
  pending$ = this.pendingRequest.asObservable();

  constructor(public httpClient: HttpClient) {  }

  updateAllRequestStatus(id: string, formData: any): Observable<any> {
    return this.httpClient.post(`${CORE_URL}/${API.EVALUATION}/${id}/${API.UPDATE}`, formData);
  }

  updateSingleRequestStatus(id: string, detail_id: string, formData: any): Observable<any> {
    return this.httpClient.post(`${CORE_URL}/${API.EVALUATION}/${id}/${detail_id}/${API.UPDATE}`, formData);
  }

  getRequestDetailData(id: string): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.EVALUATION}/${id}`);
  }

  sendPendingRequest(type: any, data: any){
    this.pendingRequest.next({ type, data });
  }
}
