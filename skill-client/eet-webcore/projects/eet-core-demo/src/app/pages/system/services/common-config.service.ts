import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { API } from '../../../shared/constants/api.constants';
import { BehaviorSubject, Observable } from 'rxjs';
import { BaseResponseModel } from '../../../shared/models/base.model';
import { TagSkill } from '../model/common-config/common-config.model';

@Injectable({
  providedIn: 'root'
})
export class CommonConfigService {

  constructor(protected http: HttpClient) { }

  private _confirm : BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  public confirm$ = this._confirm.asObservable();

  setConfirm(confirm: boolean) {
    this._confirm.next(confirm);
  }

  public oldPageIndex: number;

  setOldPageIndex(oldIndex: number) {
    this.oldPageIndex = oldIndex;
  }

  getOldPageIndex(): number {
    return this.oldPageIndex;
  }


  getTagSkill() {
    return this.http.get<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.SKILL_TAG}`)
  }
  updateOrderTagSkill(listTagSkill:any) {
    return this.http.post<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.SKILL_TAG}/${API.UPDATE_ORDER_TAG_SKILL}`, listTagSkill)
  } 
  replaceTag(listTagSkilReplace:TagSkill[]) {
    return this.http.post<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.SKILL_TAG}/${API.REPLACE}`, listTagSkilReplace)
  }
}
