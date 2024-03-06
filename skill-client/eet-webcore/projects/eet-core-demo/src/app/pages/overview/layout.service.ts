import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CORE_URL } from '../../shared/constants/api.constants';
import { BehaviorSubject, Observable, map, of } from 'rxjs';
export interface statusFailedForSavingEditorModel {
  isFailed:boolean,
  data?:string,
  layoutType:string
}
export interface dataModelSetForLayout {
  layoutType:string,
  data:string
}
@Injectable({
  providedIn: 'root',
})

export class LayoutService {
  readonly LAYOUT: any = {
    overview: 'OVERVIEW_LAYOUT',
    orgchart: 'ORGCHART_LAYOUT',
    help: 'HELP_LAYOUT',
    releaseNote:'RELEASE_NOTE_LAYOUT'
  };
  constructor(private httpClient: HttpClient) {}

  private setDataForLayout$:BehaviorSubject<dataModelSetForLayout> = new BehaviorSubject<dataModelSetForLayout>({layoutType:'',data:''});
  public _setDataForLayout = this.setDataForLayout$.asObservable();

  private statusSaveEditor$:BehaviorSubject<statusFailedForSavingEditorModel> = new BehaviorSubject<statusFailedForSavingEditorModel>({isFailed:false,data:'',layoutType:''});
  public statusSaveEditor = this.statusSaveEditor$.asObservable();

  getLayoutByName(layoutName: string): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/layout/${layoutName}`, {
      responseType: 'text',
    });
  }

  createLayout(fileContent: string, layoutName: string): Observable<any> {

    const blob = new Blob([fileContent], { type: 'text/plain' });
    const file = new File([blob], layoutName + '.txt');

    const formData: FormData = new FormData();
    formData.append('file', file, file.name);
    formData.append('layout', layoutName);

    return this.httpClient.post(`${CORE_URL}/layout`, formData);
  }


  // Observable
  setDataLayoutObservable(data:dataModelSetForLayout) {
    this.setDataForLayout$.next(data)
  }

  setStatusFailedForSaveEditor(data:statusFailedForSavingEditorModel) {
    this.statusSaveEditor$.next(data)
  }
}
