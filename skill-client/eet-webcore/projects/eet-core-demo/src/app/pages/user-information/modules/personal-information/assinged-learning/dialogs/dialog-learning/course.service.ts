import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  API,
  CORE_URL,
} from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { Observable, take } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CourseService {
  constructor(public httpClient: HttpClient) {}
  getListCourse(): Observable<any> {
    return this.httpClient.get(`${CORE_URL}/${API.COURSE}`);
  }
   addCourse(id_user: string, formData: any): Observable<any> {
    return this.httpClient.post(`${CORE_URL}/${API.ASSOCIATE}/${id_user}/${API.COURSE}`, formData,
        {}
        
      ).pipe(take(1));
      
  }
}
