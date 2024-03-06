import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API, CORE_URL, ELASTIC_DOCUMENT } from '../constants/api.constants';
import { map, Observable } from 'rxjs';
import { BaseResponseModel } from '../models/base.model';

export interface ElasticSearchModel {
  query: string;
  size: number;
  from: number;
}

@Injectable({
  providedIn: 'root'
})
export class ElasticService {

  constructor(private http: HttpClient) { }

  getDocument(documentName: string, searchOption: Partial<ElasticSearchModel>): Observable<any> {
    return this.http.post<any>(`${CORE_URL}/${API.ELASTIC_SEARCH}/${documentName}`, searchOption).pipe(
      map((element: BaseResponseModel) => {
        if (element.code == 'SUCCESS' && element?.data?.elements) {
          return {
            totalItems: element?.data?.numberOfResults,
            arrayItems: JSON.parse(element?.data.elements).map((m: any) => m._source)
          }
        }
        return {
          totalItems: 0,
          arrayItems: []
        };
      })
    );
  }
  
  searchPersonalByNameOrNtid(searchOption: Partial<ElasticSearchModel>): Observable<any> {
    return this.http.post<any>(`${CORE_URL}/${API.ELASTIC_SEARCH}/${ELASTIC_DOCUMENT.PERSONAL}/${API.NAME_OR_NTID}`, searchOption).pipe(
      map((element: BaseResponseModel) => {
        if (element.code == 'SUCCESS' && element?.data?.elements) {
          return {
            totalItems: element?.data?.numberOfResults,
            arrayItems: JSON.parse(element?.data.elements).map((m: any) => m._source)
          }
        }
        return {
          totalItems: 0,
          arrayItems: []
        };
      })
    );
  }

}
