import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CORE_URL, API } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ManageProjectRoleService {

  constructor(public httpClient: HttpClient) { }

  getProjectData(): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.ROLE}`)
  }

  getCommonTaskData(id: string): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.ROLE}/${id}/tasks`)
  }

  deleteProjectData(formData: any): Observable<any> {
    return this.httpClient.post(`${CORE_URL}/${API.ROLE}/delete`, formData)
  }

  deleteCommonTask(formData: any): Observable<any> {
    return this.httpClient.post(`${CORE_URL}/${API.COMMON_TASK}/delete`, formData)
  }
}
