import { LiveAnnouncer } from '@angular/cdk/a11y';
import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';

import { MatPaginator } from '@angular/material/paginator';
import { TranslateService } from '@ngx-translate/core';
import { DialogCommonService } from '../../../shared/services/dialog-common.service';
import { ManageRequestService } from '../manage-request.service';
import { RequestInf } from '../models/manage-request.model';

import { BciLoaderService, NotificationService } from '@bci-web-core/core';
import { finalize } from 'rxjs';
import { APPROVAL_PENDING, APPROVAL_PENDING_MSG, APPROVED, APPROVED_MSG, APPROVED_OR_REJECTED, APPROVED_OR_REJECTED_MSG, REJECTED, REJECTED_MSG } from '../constants/constants';
import { ViewPreviousRequestDetailComponent } from '../components/view-previous-request-detail/view-previous-request-detail.component';
import { CONFIG } from '../../../shared/constants/config.constants';
import { PaginDirectionUtil } from '../../../shared/utils/paginDirectionUtil';

@Component({
  selector: 'eet-previous-request',
  templateUrl: './previous-request.component.html',
  styleUrls: ['./previous-request.component.scss'],
})
export class PreviousRequestComponent implements OnInit {
  pageOption = [5, 10];
  public statusClass: string;
  displayedColumns: string[] = [
    'number',
    'requester',
    'created_date',
    'updated_date',
    'status',
    'action',
  ];
  requestData: RequestInf[];
  dataSource = new MatTableDataSource<RequestInf>();
  selection = new SelectionModel<RequestInf>(true, []);

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;
  @ViewChild(MatSort) sort: MatSort = new MatSort();

  constructor(
    private _liveAnnouncer: LiveAnnouncer,
    public dialog: MatDialog,
    private dialogCommonService: DialogCommonService,
    private manageRequestService: ManageRequestService,
    private translateService: TranslateService,
    private notificationService: NotificationService,
    private loaderService: BciLoaderService,
  ) {
    this.requestData = [];
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    PaginDirectionUtil.expandTopForDropDownPagination()
  }

  ngOnInit(): void {
    this.getRequestList();
  }

  private getAssociateId(): string {
    const _localStorage = JSON.parse(localStorage.getItem('Authorization') || '{}');
    return _localStorage.id
  }

  getRequestList(isShowNotification?: boolean) {
    this.requestData = [];
    const loader = this.loaderService.showProgressBar();

    this.manageRequestService.getPreviousRequestData().pipe(finalize(() => {
      this.loaderService.hideProgressBar(loader);
      if (isShowNotification) { this.notificationService.success(this.translateService.instant('notification.send_request-evaluation')) }
    })).subscribe((data) => {
      let _tmpData = data.data;
      //check if they are competency lead, if they approve/reject all skill then wont display request
      _tmpData.map((element: any) => {
        if(element.status === APPROVAL_PENDING) element.statusMessage = APPROVAL_PENDING_MSG;
        else if(element.status === APPROVED) element.statusMessage = APPROVED_MSG;
        else if(element.status === REJECTED) element.statusMessage = REJECTED_MSG;
        else if(element.status === APPROVED_OR_REJECTED) element.statusMessage = APPROVED_OR_REJECTED_MSG;

        this.requestData.push(element);
        // if (!element.competencyLeadEvaluateAll) { this.requestData.push(element); }
      });

      this.dataSource = new MatTableDataSource(this.requestData);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    });
  }

  onDisplayButton(status: string) {
    return ((status === APPROVAL_PENDING_MSG) ? true : false)
  }

  //filter by search bar, auto-update on every input
  onSearch(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  onView(request: any) {
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: ViewPreviousRequestDetailComponent,
      title: this.translateService.instant("manage_request.title.previous_request_detail"),
      width: '95vw',
      height: 'auto',
      type: 'view',
      // maxWdith: '1400px',
      passingData: {
        //will pass the change request to the dialog here
        id: request,
      },
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (result === APPROVED) { this.notificationService.success(this.translateService.instant('notification.send_request-evaluation_approved')).afterOpened().subscribe((response) => { this.getRequestList() }); }
        else if (result === REJECTED) { this.notificationService.success(this.translateService.instant('notification.send_request-evaluation_rejected')).afterOpened().subscribe((response) => { this.getRequestList() }); }
        else {
          this.getRequestList(result)
        }

      }
    });
  }
}
