import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CORE_URL, API } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { take } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AddNewProjectTaskService {

  constructor(public httpClient: HttpClient) { }

  addProjectData(formData: any) {
    return this.httpClient
      .post(`${CORE_URL}/${API.ROLE}`, formData)
      .pipe(take(1));
  }

  addCommonTaskData(formData: any) {
    return this.httpClient.post(`${CORE_URL}/${API.COMMON_TASK}`, formData).pipe(take(1))
  }

}
