import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CORE_URL, API } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PersonalProjectService {
  constructor(public httpClient: HttpClient) { }

  getPersonProject(id: string | undefined): Observable<any> {
    return this.httpClient.get(
      `${CORE_URL}/${API.ASSOCIATE}/${id}/${API.PROJECT}`
    );
  }
  deleteProject(idUser: string, idProject: string): Observable<any> {
    let formData: any= {idUser: idUser, idProject:idProject};
    return this.httpClient.post(`${CORE_URL}/${API.ASSOCIATE}/${idUser}/${API.PROJECT}/${idProject}/delete`, formData);
  }
}
