import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API, CORE_URL } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { Observable, retry, take } from 'rxjs';
import { AssociateFilterResponse, AssociateListResponseModel, AssociatePaginationModel, PaginationModel } from './associate-info.model';

@Injectable({
  providedIn: 'platform'
})
export class AssociateInfoCopyService {
  constructor(public httpClient: HttpClient) { }
  getAssociateInfo(paginationOption: PaginationModel): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.ASSOCIATE}?size=${paginationOption.size}&page=${paginationOption.page}`);
  }
  getlistFilter(): Observable<AssociateFilterResponse> {
    return this.httpClient.get<AssociateFilterResponse>(`${CORE_URL}/${API.ASSOCIATE}/${API.FILTER}`);
  }

  getAssociateList(paginationOption: AssociatePaginationModel): Observable<AssociateListResponseModel> {
    let params = {};
    Object.keys(paginationOption).map(option => {
      if (paginationOption[option as keyof AssociatePaginationModel] != null) {
        Object.assign(params, { [option]: paginationOption[option as keyof AssociatePaginationModel] })
      }
    });
    return this.httpClient.get<AssociateListResponseModel>(`${CORE_URL}/${API.ASSOCIATE}`, { params })
  }

  exportExcelFile(personalIdList: string[]): Observable<any> {
    return this.httpClient
      .post(`${CORE_URL}/${API.ASSOCIATE}/${API.EXPORT}`, personalIdList, {responseType: 'blob'})
      .pipe(retry(3));
  }
  
  getSkillGroupData(): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.SKILL_GROUP}`).pipe(take(1));
  }

  deleteAssociate(associateId:string):Observable<any> {
    return this.httpClient.post(`${CORE_URL}/${API.ASSOCIATE}/${associateId}/${API.DELETE}`,associateId)
  }
}
