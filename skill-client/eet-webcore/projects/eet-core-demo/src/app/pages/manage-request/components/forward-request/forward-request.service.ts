import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API, CORE_URL } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { BaseResponseModel } from 'projects/eet-core-demo/src/app/shared/models/base.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ForwardRequestService {
  constructor(public httpClient: HttpClient) { }
  getCompetencyLeads(): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.COMPETENCY_LEAD}`);
  }
  forwardCompetencyLead(id: string, formData: any): Observable<any> {
    return this.httpClient.post(`${CORE_URL}/${API.EVALUATION}/${id}/forward`, formData)
  }
  getCompetencyLeadByRequest(requestId: string): Observable<BaseResponseModel>{
    return this.httpClient.get<BaseResponseModel>(`${CORE_URL}/${API.COMPETENCY_LEAD}/${API.EVALUATION}/${requestId}`);
  }
}
