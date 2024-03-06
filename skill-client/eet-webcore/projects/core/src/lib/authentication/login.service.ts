import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { delay, Observable, of, pipe } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CoreUrl } from '../shared/util/url.constant';
import { Token, User } from './interface';
import { STATUS } from '../interceptors';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  constructor(protected http: HttpClient) { }

  // normal login
  login(username: string, password: string, rememberMe?: boolean) {
    return this.http.post<Token>(
      `${CoreUrl.API_PATH}/${CoreUrl.AUTH}/${CoreUrl.LOGIN}`,
      {
        username,
        password,
        rememberMe,
      }
    );
  }

  // login with sso
  loginSso(): Observable<any> {
    return this.http
      .get<any>(`${CoreUrl.API_PATH}/${CoreUrl.LOGIN_SSO}`)
      .pipe(catchError((error) => this.catchError(error)));
  }

  requestAccess(ntId: string, reason: string) {
    ntId = ntId.trim();
    reason = reason.trim();
    return this.http.post<any>(
      `${CoreUrl.API_PATH}/${CoreUrl.AUTH}/${CoreUrl.REQUEST_ACCESS}`,
      {
        username: ntId,
        reason,
      }
    );
  }

  refresh(params: Record<string, any>) {
    return this.http.post<Token>(
      `${CoreUrl.API_PATH}/${CoreUrl.AUTH}/${CoreUrl.REFRESH_TOKEN}`,
      params
    );
  }

  logout(): Observable<any> {
    return this.http.get<any>(`${CoreUrl.API_PATH}/${CoreUrl.LOGOUT}`, {});
  }

  me() {
    return this.http.get<User>(`${CoreUrl.API_PATH}/${CoreUrl.ME}`);
  }

  private catchError(error: HttpErrorResponse) {
    try {
      let returnError: any = {};
      returnError['data'] = error.error['data'] ? error.error['data'] : null;
      returnError['code'] = error.error['code'] ? error.error['code'] : null;
      returnError['message'] = error.error['message']
        ? error.error['message']
        : null;
      returnError['status'] = error.error['status']
        ? error.error['status']
        : null;
      returnError['statusCode'] = error.status
        ? error.status
        : STATUS.INTERNAL_SERVER_ERROR;
      return of(returnError);
    } catch (ex) {
      console.error(error);
      return of(null);
    }
  }
}
