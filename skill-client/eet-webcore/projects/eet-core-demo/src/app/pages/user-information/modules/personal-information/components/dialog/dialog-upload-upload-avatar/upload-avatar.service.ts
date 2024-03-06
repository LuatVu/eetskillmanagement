import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API, CORE_URL } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UploadAvatarService {
  constructor(public httpClient: HttpClient) { }
  uploadAvatar(id: string, File: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', File, File.name);
    return this.httpClient
      .post(`${CORE_URL}/${API.ASSOCIATE}/${id}/${API.AVATAR}`, formData)
  }
}
