import { APP_BASE_HREF } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { CoreUrl } from '../../shared/util/url.constant';
import { LoginService } from './../../authentication/login.service';

@Component({
  selector: 'eet-eet-request',
  templateUrl: './eet-request.component.html',
  styleUrls: ['./eet-request.component.scss'],
})
export class EetRequestComponent implements OnInit {
  requestForm = this.fb.group({
    winnt: ['', [Validators.required, Validators.pattern]],
    reason: ['', [Validators.required]],
  });
  loginLink = `/${CoreUrl.AUTHENTICATE}`;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private loginService: LoginService,
    private notifyService: NotificationService,
    private translate: TranslateService,
    private loader: LoadingService,
    @Inject(APP_BASE_HREF) public baseHref: string
  ) { }

  ngOnInit() { }

  get winnt() {
    return this.requestForm.get('winnt');
  }

  get reason() {
    return this.requestForm.get('reason');
  }

  request() {
    const comLoader = this.loader.showProgressBar();
    this.loginService
      .requestAccess(this.winnt?.value, this.reason?.value)
      .subscribe({

        error: (errorRes: HttpErrorResponse) => {
          this.loader.hideProgressBar(comLoader);
          if (errorRes.status !== 504) {
            this.router.navigateByUrl(`${CoreUrl.REQUEST_ACCESS}`)
            let errorMessage = 'error.request_access.' + errorRes.error["code"];
            this.translate.get(errorMessage).subscribe((returnField: any) => {
              errorMessage = (returnField === errorMessage) ? errorRes.error["message"] : returnField;
            })
            this.notifyService.error((errorMessage && errorMessage.length > 0) ? errorMessage : "Looks like the gateway timeout");
          }
        },
        complete: () => {
          this.loader.hideProgressBar(comLoader);
          this.notifyService.success("Your request is sent successfully");
        },
      });
  }
}
