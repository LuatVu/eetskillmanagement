import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CORE_URL, API } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { Observable, retry } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PesonalLearningService {

  constructor(public httpClient: HttpClient) { }

  getPersonLearning(id: string): Observable<any> {
    return this.httpClient.get(
      `${CORE_URL}/${API.ASSOCIATE}/${id}/${API.COURSE}`
    );
  }

  viewCourse(id_user: string, id_course: string): Observable<any> {
    return this.httpClient
      .get(
        `${CORE_URL}/${API.ASSOCIATE}/${id_user}/${API.COURSE}/${id_course}`,
        { responseType: 'arraybuffer' }
      )
      .pipe(retry(3));
  }

  deleteCourse(id_user: string, id_course: string): Observable<any> {
    let formData : any = {id_user: id_user, id_course:id_course}
    return this.httpClient
      .post(
        `${CORE_URL}/${API.ASSOCIATE}/${id_user}/${API.COURSE}/${id_course}/certificate/delete`, formData)
  }
}
