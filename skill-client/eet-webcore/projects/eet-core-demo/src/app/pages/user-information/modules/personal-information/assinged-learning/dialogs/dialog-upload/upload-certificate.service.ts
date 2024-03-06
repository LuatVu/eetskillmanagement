import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API, CORE_URL } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { Observable, retry } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UploadCertificateService {

   constructor(public httpClient: HttpClient) {}
  uploadCeritifcate(id_user: string,id_course:string, File: File): Observable<any> {
     const formData: FormData = new FormData();
    formData.append('file', File, File.name);
    // formData.append('reportProgress', true);
    // Store form name as "file" with file data
   
    return this.httpClient
      .post(`${CORE_URL}/${API.ASSOCIATE}/${id_user}/${API.COURSE}/${id_course}/certificate`, formData)

      .pipe(retry(3));
  }
}
