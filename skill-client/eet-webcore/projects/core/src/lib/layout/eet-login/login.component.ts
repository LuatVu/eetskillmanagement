import { APP_BASE_HREF } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'projects/core/src/lib/authentication/auth.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { filter } from 'rxjs';
import * as CONSTANT from '../../shared/util/system.constant';
import { CoreUrl } from '../../shared/util/url.constant';

@Component({
  selector: 'eet-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  isSubmitting = false;
  // there're multiple form inside
  // so that, define code for extend in the future
  formActive = 0;

  loginForm = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]],
  });
  //  externalLink = `${CoreUrl.EXTERNAL}`;
  requestLink = `${CoreUrl.REQUEST_ACCESS}`;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private auth: AuthService,
    private NotifyService: NotificationService,
    private loader: LoadingService,
    private translate: TranslateService,
    @Inject(APP_BASE_HREF) public baseHref: string
  ) { }

  ngOnInit() {
  }

  get username() {
    return this.loginForm.get(`${CONSTANT.USERNAME}`);
  }

  get password() {
    return this.loginForm.get(`${CONSTANT.PASSWORD}`);
  }

  login() {
    this.isSubmitting = true;
    const comLoader = this.loader.showProgressBar();
    this.auth
      .login(this.username?.value, this.password?.value)
      .pipe(filter((authenticated: any) => authenticated))
      .subscribe({
        error: (errorRes: HttpErrorResponse) => {
          let errorMessage = 'error.login.' + errorRes.error["code"];
          // Step 1: FE contained the error message yet?
          this.translate.get(errorMessage).subscribe((mappedMessage: any) => {
            errorMessage = (mappedMessage === errorMessage) ? errorRes.error["message"] : mappedMessage;
          })
          this.loginError(errorRes);
          this.loader.hideProgressBar(comLoader);
          if (errorRes.status !== 504) {
            if (errorRes.status === 500) { // TOO MUCH 500 CODE.
              if (errorRes.error['code'] === 'NOT_EXISTED_USERNAME') {
                this.router.navigateByUrl(`${CoreUrl.AUTHENTICATE}/${CoreUrl.REQUEST_ACCESS}`);
              }
              // Message is not mapped in FE, and BE does not have any message associated with error code
              else if (errorRes.error['code'] !== 'INTERNAL_SERVER_ERROR' && errorMessage.length === 0) {
                console.error("Http failure response with code: " + errorRes.error['code'])
                errorMessage = "Looks like we have an internal issue, please try refreshing"
              }
              else { // INTERNAL_SERVER_ERROR
                console.error("Http failure response with message: " + errorRes.error['message'])
                // errorMessage: (Default FE error message).
              }
            }
            this.NotifyService.error((errorMessage && errorMessage.length > 0) ? errorMessage : "Looks like the gateway timeout");
          }
          // If error 504: Looks like the GW TO.
        },
        complete: () => {
          console.info('login success');
          this.translate.get('login.success').subscribe((successMessageValue: any) => {
            this.NotifyService.success(successMessageValue);
          })
          this.router.navigate(['/']);
          this.loader.hideProgressBar(comLoader)
        },
      });
  }

  private loginError(errorRes: HttpErrorResponse) {
    if (errorRes.status === 422) {
      const form = this.loginForm;
      const errors = errorRes.error.errors;
      Object.keys(errors).forEach((key) => {
        form.get(key === 'email' ? 'username' : key)?.setErrors({
          remote: errors[key][0],
        });
      });
    }
    this.isSubmitting = false;
  }
}
