import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';

@Injectable({
    providedIn: 'root'
})
export class VModelService {
    constructor(protected http: HttpClient) {}

    getPhaseData() {
        return this.http.get<any>(`/${CoreUrl.API_PATH}/phase`)
    }
}