import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { Observable } from 'rxjs';
import * as API_PATH from '../../../../../shared/constants/api.constants';
import * as LEARNING from './model/dumb-data.constants';
@Injectable({
  providedIn: 'root'
})
export class MyLearningService {

  constructor(protected httpClient: HttpClient) {}

  //get Course List
  getCourseList(course_type?: string,status?: string):Observable<any>{
      return this.httpClient.get(`${API_PATH.CORE_URL}/${API_PATH.API.COURSE}`, {
        params: {
          ...(course_type) && {
            course_type
          },
          ...(status) && {
            status
          }
        }
      });
  }

  //Delete A Course
  deleteCourse(course_id: string): Observable<any> {
    let formData: any = {course_id : course_id}
    return this.httpClient.post(`${API_PATH.LEARNING.COURSE}/${course_id}/delete`, {
      course_id
    })
  }

  getCourseDetails(id: string) {
    return this.httpClient.get(`${API_PATH.LEARNING.COURSE}/${id}`)
  }

  getMembersOfCourse(id: string) {
    return this.httpClient.get(`${API_PATH.LEARNING.COURSE}/${id}/${API_PATH.API.MEMBERS}`)
  }
    getCategory() {
    return this.httpClient.get(`${API_PATH.LEARNING.CATEGORY}`)
  }
  getLevel() {
    return this.httpClient.get(`${API_PATH.LEARNING.LEVEL}`)
  }
  postCourse(data: any) {
    return this.httpClient.post(`${API_PATH.LEARNING.COURSE}`, data)
  }
  updateCourse(id: string, data: any) {
    return this.httpClient.post(`${API_PATH.LEARNING.COURSE}/${id}/${API_PATH.API.UPDATE}`, data)
  }
  deleteMemberFromCourse(associate_id: string, id: string) {
    return this.httpClient.post(`${CoreUrl.API_PATH}/${API_PATH.API.ASSOCIATE}/${associate_id}/${API_PATH.API.COURSE}/${id}/${API_PATH.API.DELETE}`, null)
  }
  elasticSearch(string: string) {
    return this.httpClient.post(`${CoreUrl.API_PATH}/${API_PATH.API.ELASTIC_SEARCH}/${API_PATH.ELASTIC_DOCUMENT.PERSONAL}`, {
      "query": string,
      "size": LEARNING.DEFAULT_ELASTICSEARCH_PARAMETERS.size,
      "from": LEARNING.DEFAULT_ELASTICSEARCH_PARAMETERS.from
    })
  }
  registerMember(id: string, data: any) {
    return this.httpClient.post(`${API_PATH.LEARNING.COURSE}/${id}/${API_PATH.API.ASSIGN}`, data)
  }
}
