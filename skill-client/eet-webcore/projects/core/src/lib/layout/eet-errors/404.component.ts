import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { AuthService } from '../../authentication';
import { CoreUrl } from '../../shared/util/url.constant';

@Component({
  selector: 'app-error-404',
  template: `
    <error-code
      code="404"
      [title]="'error.404.title'"
      [message]="'error.404.message'"
    ></error-code>
  `,
})
export class Error404Component {
  constructor (
    private auth: AuthService,
    private router: Router,
    private loader: LoadingService

  ) {}
  ngOnInit() {
  }
}
