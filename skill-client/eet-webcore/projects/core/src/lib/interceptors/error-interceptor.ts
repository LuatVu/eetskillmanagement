import { Injectable } from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { CoreUrl } from '../shared/util/url.constant';
import { TranslateService } from '@ngx-translate/core';


export enum STATUS {
  BAD_REQUEST = 400,
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  NOT_FOUND = 404,
  INTERNAL_SERVER_ERROR = 500,
}

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  private errorPages = [STATUS.FORBIDDEN, STATUS.NOT_FOUND, STATUS.INTERNAL_SERVER_ERROR];

  constructor(
    private translate: TranslateService,
    private router: Router,
    private notify: NotificationService
  ) { }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next
      .handle(request)
      .pipe(catchError((error: HttpErrorResponse) => this.handleError(error,request)));
  }

  private handleError(error: HttpErrorResponse,request: HttpRequest<unknown>) {
    switch (error.status) {
      case STATUS.INTERNAL_SERVER_ERROR:
        if(!request.url.includes(CoreUrl.REFRESH_TOKEN) && !request.url.includes(CoreUrl.LOGIN_SSO)){
          this.notify.error(error.error.message);
        }
        break;
      case STATUS.BAD_REQUEST:
        this.translate.get(`error.${error.error.code}`).subscribe(unauthorizeMsg => this.notify.error(unauthorizeMsg));
        break;
      case STATUS.UNAUTHORIZED:
        this.translate.get(`error.${error.status}.message`).subscribe(unauthorizeMsg => this.notify.error(unauthorizeMsg));
        this.router.navigateByUrl(`/${CoreUrl.AUTH}/${CoreUrl.NO_ACCESS}`);
        break;
      case STATUS.FORBIDDEN:
        // 403
        break;
      case STATUS.NOT_FOUND:
        this.router.navigateByUrl(`/${error.status}`, {
          skipLocationChange: true,
        });
        break;
    }

    return throwError(() => error);
  }
}
