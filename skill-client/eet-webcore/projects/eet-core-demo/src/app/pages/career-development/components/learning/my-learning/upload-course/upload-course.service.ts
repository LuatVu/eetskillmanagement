import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { retry } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UploadCourseService {
  constructor(public http: HttpClient) { }
  getExcelTemplate() {
    return this.http.get(`${CoreUrl.API_PATH}/course/template`, {
      responseType: 'blob'
    }).pipe(retry(3))
  }
  postCourseUpload(file: File) {
    const formData: FormData = new FormData()
    formData.append('file', file, file.name)
    return this.http.post(`${CoreUrl.API_PATH}/course/upload`, formData)
  }
}
