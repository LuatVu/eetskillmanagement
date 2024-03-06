import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { API, CORE_URL } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { Observable, take } from 'rxjs';

interface FormData {
  "id": string,
  "additional_tasks": string,
  "role_id": string,
  "project_id": string
}
@Injectable({
  providedIn: 'root',
})
export class AddBoschProjectService {


  constructor(public httpClient: HttpClient) { }
  addProject(idUser: any, formData: any) {
    return this.httpClient
      .post(
        `${CORE_URL}/${API.ASSOCIATE}/${idUser}/${API.PROJECT}`,
        formData,
        {}
      )
      .pipe(take(1));
  }
  searchBoschProject(): Observable<any> {
    return this.httpClient.get(
      `${CORE_URL}/${CoreUrl.PROJECT}?project_type=Bosch&size=10000`,
      {}
    );
  }

  getBoschProjectForDropdown(): Observable<any> {
    return this.httpClient.get(
      `${CORE_URL}/${CoreUrl.PROJECT}/${CoreUrl.DROPDOWN}?project_type=Bosch`,
      {}
    );
  }

  getCommonTasksByRole(id: string): Observable<any> {
    return this.httpClient.get(
      `${CORE_URL}/${CoreUrl.ROLE}/${id}/${CoreUrl.TASK}`,
      {}
    );
  }

  editAdditionalTask(formData: FormData): Observable<any> {
    return this.httpClient.post(
      `${CORE_URL}/${CoreUrl.PROJECT}/${API.ADDITIONAL_TASK}`,
      formData,
      {}
    )
  }
}
