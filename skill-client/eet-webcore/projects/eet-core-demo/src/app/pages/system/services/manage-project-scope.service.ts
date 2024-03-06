import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { API } from '../../../shared/constants/api.constants';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { BaseResponseModel } from '../../../shared/models/base.model';
import { Team } from '../model/line-manager/line-manager.model';
import { ProjectScope } from '../../projects/models/projects.model';

@Injectable({
  providedIn: 'root'
})
export class ManageProjectScopeService {
  private changeProjectScope$:BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public _changeProjectScope = this.changeProjectScope$.asObservable();

  constructor(protected http: HttpClient) { }

  createNewProjectScope(data:ProjectScope):Observable<BaseResponseModel> {
    return this.http.post<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.PROJECT_SCOPE}`, data);
  }
  updateProjectScope(data:ProjectScope):Observable<BaseResponseModel> {
    return this.http.post<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.PROJECT_SCOPE}/${API.UPDATE}`,data)
  }
  getProjectScopeDetail(id:string):Observable<BaseResponseModel> {
    return this.http.get<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.PROJECT_SCOPE}/${id}}`)
  }
 
  deleteProjectScope(id:string):Observable<BaseResponseModel> {
    return this.http.post<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.PROJECT_SCOPE}/${id}/${API.DELETE}`,id)
  }
  // observable
  updateProjectScopeEvent(isChange:boolean) {
    this.changeProjectScope$.next(isChange)
  }

  linearBackgroundProjectCard(fromColor:string,toColor:string) {
    return `linear-gradient(-5.71deg, ${fromColor} 16.67%,  ${toColor} 92.46%)`
  }
}
