import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { API } from '../../../shared/constants/api.constants';
import { Observable, Subject } from 'rxjs';
import { BaseResponseModel } from '../../../shared/models/base.model';
import { Team } from '../model/line-manager/line-manager.model';

@Injectable({
  providedIn: 'root'
})
export class LineManagerService {
  private existUpdateInTeam = new Subject();
  public updateTeam$ = this.existUpdateInTeam.asObservable();
  constructor(protected http: HttpClient) { }

  getAllLineManager() :Observable<BaseResponseModel> {
    return this.http.get<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.MANAGER}`);
  }
  getAllTeam():Observable<BaseResponseModel>{
    return this.http.get<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.TEAM}`);
  }
  updateTeam(id:string,team:Team):Observable<BaseResponseModel>{
    return this.http.post<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.TEAM}/${id}`,team);
  }
  doUpdateTeam(data:boolean) {
    this.existUpdateInTeam.next({data})
  } 
}
