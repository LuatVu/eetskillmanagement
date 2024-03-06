import { Injectable, Injector } from '@angular/core';
import { Router } from '@angular/router';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { STATUS } from '../interceptors';
import { CoreUrl } from '../shared/util/url.constant';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class AppConfigService {
  // Helper property to resolve the service dependency.
  private get router() {
    return this._injector.get(Router);
  }
  private get authService() {
    return this._injector.get(AuthService);
  }
  private get loader() {
    return this._injector.get(LoadingService);
  }

  constructor(private _injector: Injector, private loaderService: LoadingService) { }

  loginSso(): Promise<any> {
    document.body.innerHTML = `<div class="mark-wrapper">
    <div class='mark'>
<div class="a-activity-indicator" aria-live="off">
  <div class="a-activity-indicator__top-box"></div>
  <div class="a-activity-indicator__bottom-box"></div>
</div>
</div></div>`

    // this.loaderService.showProgressBar();
    return new Promise<void>((resolve, reject) => {
      this.authService
        .loginSso()
        .toPromise()
        .then((response: any) => {
          if (
            response &&
            (response['statusCode'] === STATUS.UNAUTHORIZED ||
              response['statusCode'] === STATUS.INTERNAL_SERVER_ERROR)
          ) {
            if (response['code'] == 'SKM_USER_INACTIVE') {
              // In this case user has request to access to the system.
              localStorage.setItem(response['code'], 'USER_EXISTED_AND_WAITTING_FOR_APPROVAL');
              this.router.navigateByUrl(`${CoreUrl.AUTHENTICATE}/${CoreUrl.WAITING_ACCESS}`);
            } else {
              localStorage.removeItem('SKM_USER_INACTIVE');
              localStorage.removeItem('Authorization');
            }
          } else if (!response || (response && response['statusCode'] == 0)) {
            // cant send request to backend
            this.router.navigateByUrl(`/${CoreUrl.NO_ACCESS}`);
          }
          else {
            // success case
            // this.router.navigate(['/', CoreUrl.USER_INF]);
            localStorage.removeItem('SKM_USER_INACTIVE');
          }
        })
        .then(() => {
          resolve();
        })
        .catch((err) => {
          this.router.navigateByUrl(`${CoreUrl.AUTHENTICATE}/${CoreUrl.NO_ACCESS}`);
          console.error('Error when login with sso');
          resolve();
        });
    });
  }
}
