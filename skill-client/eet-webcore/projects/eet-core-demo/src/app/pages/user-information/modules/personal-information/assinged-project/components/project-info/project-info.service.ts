import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API, CORE_URL } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { RoleModel } from 'projects/eet-core-demo/src/app/shared/models/common-model';
import { Observable } from 'rxjs';
import { ProjectModel } from '../../../personal-infomation.model';
import { PersonalInfomationService } from '../../../personal-infomation.service';

@Injectable({
  providedIn: 'root',
})
export class ProjectInfoService {
  public projectUpdate: ProjectModel[] = [];

  constructor(public httpClient: HttpClient, private personalInfomationService: PersonalInfomationService) { }

  getProjectInfo(idProject: string, idUser: string): Observable<any> {
    return this.httpClient.get(
      `${CORE_URL}/${API.ASSOCIATE}/${idUser}/${API.PROJECT}/${idProject}`
    );
  }

  getProjectRole(): Observable<RoleModel> {
    return this.httpClient.get<RoleModel>(`${CORE_URL}/${API.ROLE}`);
  }
  getProjectGB(): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.GB}`);
  }


}
