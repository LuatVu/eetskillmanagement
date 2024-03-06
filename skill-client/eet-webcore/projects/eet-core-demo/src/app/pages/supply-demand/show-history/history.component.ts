import { Component, OnInit, ViewChild, Inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';

import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { Observable, ReplaySubject, Subscription, forkJoin, startWith, takeUntil } from 'rxjs';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { HistoryModel } from './history.model';
import { HistoryService } from './history.service';

@Component({
  selector: 'history-supply',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.scss']
})

export class HistorySupplyComponent implements OnInit {
  @ViewChild(MatSort) sort: MatSort = new MatSort();
  public isShowLoading: boolean = false;
  private subscription: Subscription;
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  historyList: HistoryModel[] = [];
  dataSource: MatTableDataSource<HistoryModel> = new MatTableDataSource();
  displayedColumns: string[] = ['updatedByName', 'updatedDate', 'oldStatus', 'newStatus', 'note'];
  projectId: string;

  constructor(private historyService: HistoryService,
    private loader: LoadingService,
    private notifyService: NotificationService,
    private dialogRef: MatDialogRef<HistorySupplyComponent>,
    private translate: TranslateService,
    @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
  }

  ngOnInit(): void {
    this.projectId = this.data.data.id;
    this.historyService.getHistoryById(this.projectId)
      .pipe(takeUntil(this.destroyed$))
      .subscribe((response: any) => {
        if (response && response.code === 'SUCCESS') {
          this.historyList = response.data;
          this.dataSource = new MatTableDataSource<HistoryModel>();
          this.dataSource.data = response.data || [];
          this.dataSource.sort = this.sort;
        }
      });
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

}
