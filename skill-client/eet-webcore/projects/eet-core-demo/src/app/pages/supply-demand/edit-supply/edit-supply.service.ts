import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {
  API,
  CORE_URL,
  ELASTIC_DOCUMENT,
} from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { BehaviorSubject, Observable, map, of } from 'rxjs';
import { AddDemandSharedModel } from './../add-new-supply/add-new-supply.model';

@Injectable({
  providedIn: 'root',
})
export class EditDemandService {
  public sharedData: BehaviorSubject<Partial<AddDemandSharedModel>> =
    new BehaviorSubject<Partial<AddDemandSharedModel>>({
      isStoredState: false,
    });

  constructor(private httpClient: HttpClient) {}

  setSharedData(data: any) {
    this.sharedData.next({ ...this.sharedData.value, ...data });
  }

  getSharedData(): Observable<Partial<AddDemandSharedModel>> {
    return this.sharedData.asObservable();
  }

  getAllCreationInfo() {
    return this.httpClient.get(`${CORE_URL}/${API.ALL_CREATION_INFO}`);
  }

  getAllLevel() {
    return this.httpClient.get(`${CORE_URL}/${API.LEVEL}`);
  }

  createDemand(form: any) {
    return this.httpClient.post(`${CORE_URL}/${API.CREATE}`, form);
  }

  edit(supply: any): Observable<any> {
    const frontendURL = window.location.href;
    const headers = new HttpHeaders().set('request_URL', frontendURL);
    return this.httpClient.post(`${CORE_URL}/${API.DEMAND}/update`, supply,{headers});
  }
}
