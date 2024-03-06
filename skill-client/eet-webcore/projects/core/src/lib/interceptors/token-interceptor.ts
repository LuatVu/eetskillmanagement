import { Inject, Injectable, Optional } from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { BASE_URL } from './base-url-interceptor';
import { AuthService, TokenService } from '../authentication';
import { AUTHORIZE_TYPE, TOKEN_KEY } from '../shared/util/system.constant';
import { CoreUrl } from '../shared/util/url.constant';
import { environment } from 'projects/eet-core-demo/src/environments/environment';
import { STATUS } from './error-interceptor';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  private hasHttpScheme = (url: string) => new RegExp('^http(s)?://', 'i').test(url);

  constructor(
    private tokenService: TokenService,
    private router: Router,
    private authService:AuthService,
    @Optional() @Inject(BASE_URL) private baseUrl?: string
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const handler = () => {
      if (request.url.includes(`/${CoreUrl.AUTH}/${CoreUrl.LOGOUT}`)) {
        this.router.navigateByUrl(`/${CoreUrl.AUTH}/${CoreUrl.NO_ACCESS}`);
      }

      if (this.router.url.includes(`/${CoreUrl.AUTH}/${CoreUrl.AUTHENTICATE}`)) {
        this.router.navigateByUrl(`/${CoreUrl.PersonalInformation}`);
      }
    };

    if (this.tokenService.valid() && this.shouldAppendToken(request.url)) {
      return next
        .handle(
          request.clone({
            headers: request.headers.append(`${environment.authType}`, this.tokenService.getBearerToken()),
            withCredentials: true,
          })
        )
        .pipe(
          catchError((error: HttpErrorResponse) => {
            if (error.status === STATUS.INTERNAL_SERVER_ERROR && request.url.includes(CoreUrl.REFRESH_TOKEN)) {
              return this.authService.loginSso();
            }
            return throwError(error);
          }),
          tap(() => handler())
        );
    }

    return next.handle(request).pipe(tap(() => handler()));
  }

  private shouldAppendToken(url: string) {
    return !this.hasHttpScheme(url) || this.includeBaseUrl(url);
  }

  private includeBaseUrl(url: string) {
    if (!this.baseUrl) {
      return false;
    }

    const baseUrl = this.baseUrl.replace(/\/$/, '');

    return new RegExp(`^${baseUrl}`, 'i').test(url);
  }
}