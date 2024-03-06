import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse,
} from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import { CoreUrl } from '../shared/util/url.constant';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';

@Injectable()
export class DefaultInterceptor implements HttpInterceptor {
  constructor(private notify: NotificationService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req);
    // if (!req.url.includes(`/${CoreUrl.API}/`)) {
    //   return next.handle(req);
    // }

    // return next.handle(req).pipe(mergeMap((event: HttpEvent<any>) => this.handleOkReq(event)));
  }

  private handleOkReq(event: HttpEvent<any>): Observable<any> {
    if (event instanceof HttpResponse) {
      const body: any = event.body;
      // failure: { code: **, msg: 'failure' }
      // success: { code: 0,  msg: 'success', data: {} }
      if (body && 'code' in body && body.code !== 0) {
        if (body.msg) {
          this.notify.error(body.msg);
        }
        console.error('EVENT ERROR', event);
        return throwError(() =>new Error(JSON.stringify(event)));
      }
    }
    // Pass down event if everything is OK
    return of(event);
  }
}
