import { formatDate } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, LOCALE_ID } from '@angular/core';
import { API, CORE_URL } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { take } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AddnonBoschProjectService {
  constructor(public httpClient: HttpClient, @Inject(LOCALE_ID) private locale: string) { }
  AddProject(idUser: any, formData: any) {
    return this.httpClient
      .post(`${CORE_URL}/${API.ASSOCIATE}/${idUser}/${API.PROJECT}/${API.NON_BOSCH}`, {
        ...formData,
        end_date: formatDate(formData?.end_date, 'yyyy-MM-dd', this.locale),
        start_date: formatDate(formData?.start_date, 'yyyy-MM-dd', this.locale)
      }, {

      })
      .pipe(take(1));
  }
}
