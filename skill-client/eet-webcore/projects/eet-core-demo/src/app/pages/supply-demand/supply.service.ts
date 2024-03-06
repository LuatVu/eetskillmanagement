import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API, CORE_URL } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { Observable, take } from 'rxjs';
import { SupplyFilterResponse, SupplyListResponseModel, SupplyPaginationModel, PaginationModel, SupplyModel } from './supply.model';

@Injectable({
  providedIn: 'platform'
})
export class SupplyService {

  constructor(public httpClient: HttpClient) { }

  getAll(): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.DEMAND}/get-all`);
  }

  getSupplyList(paginationOption: SupplyPaginationModel): Observable<SupplyListResponseModel> {
    let params = {};
    Object.keys(paginationOption).map(option => {
      if (paginationOption[option as keyof SupplyPaginationModel] != null) {
        Object.assign(params, { [option]: paginationOption[option as keyof SupplyPaginationModel] })
      }
    });
    return this.httpClient.get<SupplyListResponseModel>(`${CORE_URL}/${API.DEMAND}`, { params })
  }

  create( supply: SupplyModel | any) : Observable<any>{
    return this.httpClient.post(`${CORE_URL}/${API.DEMAND}/create`, supply);
  }

  update(supply: SupplyModel | any) : Observable<any> {
    return this.httpClient.post(`${CORE_URL}/${API.DEMAND}/update`, supply);
  }

  delete(id: string) : Observable<any> {
    return this.httpClient.post(`${CORE_URL}/${API.DEMAND}/delete/` + id, id);
  }

  getHistoryById(id: string) : Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.DEMAND}/history/`+ id);
  }

  getAllCreationInfo() : Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.DEMAND}/all-creation-info`);
  }

  getSupplyDemandBySubId(subId: number) : Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.DEMAND}/get-by-subid/` + subId);
  }

}
