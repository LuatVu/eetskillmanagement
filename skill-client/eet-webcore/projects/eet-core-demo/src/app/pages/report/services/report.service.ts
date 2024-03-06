import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';

@Injectable({
    providedIn: 'root'
})
export class ReportService {
    constructor(protected http: HttpClient) {
    }

    getReport() {
        return this.http.get<any>(`/${CoreUrl.API_PATH}/report`)
    }

    getFilter() {
        return this.http.get<any>(`/${CoreUrl.API_PATH}/report/filter`)
    }

    getReportWithFilter(filterString: string) {
        if (filterString == null || filterString === '') {
            return this.getReport();
        } 
        
        return this.http.get<any>(`/${CoreUrl.API_PATH}/report?` + filterString);
    }

    getAssociateReport() {
        return this.http.get<any>(`/${CoreUrl.API_PATH}/report/associate`)
    }

    getAssociateReportWithFilter(filterString: string) {
        if (filterString == null || filterString === '') {
            return this.getAssociateReport();
        } 
        
        return this.http.get<any>(`/${CoreUrl.API_PATH}/report/associate?` + filterString);
    }

    getProjectReport() {
        return this.http.get<any>(`/${CoreUrl.API_PATH}/report/project`)
    }

    getProjectReportWithFilter(filterString: string) {
        if (filterString == null || filterString === '') {
            return this.getProjectReport();
        } 
        
        return this.http.get<any>(`/${CoreUrl.API_PATH}/report/project?` + filterString);
    }

}