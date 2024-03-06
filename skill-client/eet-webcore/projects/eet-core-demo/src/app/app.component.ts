/**
 * Copyright (C) 2019 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

import { Component, OnInit } from '@angular/core';
import { delay } from 'rxjs/operators';
import { LoadingService } from './shared/services/loading.service';

@Component({
  selector: 'body',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  public isShowLoading: boolean = false;

  constructor(private loadingService: LoadingService) { }

  ngOnInit() {
    this.loadingService.loadingSub
      .pipe(delay(0)) // This prevents a ExpressionChangedAfterItHasBeenCheckedError for subsequent requests
      .subscribe((loading) => {
        this.isShowLoading = loading;
      });
  }
}
