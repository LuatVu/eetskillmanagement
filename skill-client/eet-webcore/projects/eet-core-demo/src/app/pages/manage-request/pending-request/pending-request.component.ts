import { LiveAnnouncer } from '@angular/cdk/a11y';
import { SelectionModel } from '@angular/cdk/collections';
import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';

import { MatPaginator } from '@angular/material/paginator';
import { TranslateService } from '@ngx-translate/core';
import { DialogCommonService } from '../../../shared/services/dialog-common.service';
import { ManageRequestService } from '../manage-request.service';
import { RequestInf } from '../models/manage-request.model';

import { finalize } from 'rxjs';
import { CONFIG } from '../../../shared/constants/config.constants';
import { LoadingService } from '../../../shared/services/loading.service';
import { NotificationService } from '../../../shared/services/notification.service';
import { ForwardRequestComponent } from '../components/forward-request/forward-request.component';
import { ViewRequestDetailComponent } from '../components/view-request-detail/view-request-detail.component';
import { APPROVAL_PENDING, APPROVAL_PENDING_MSG, APPROVED, REJECTED } from '../constants/constants';
import { SkillEvaluationService } from '../../career-development/components/skill-evaluation/skill-evaluation.service';
import { ViewRequestDetailService } from '../components/view-request-detail/view-request-detail.service';
import { PaginDirectionUtil } from '../../../shared/utils/paginDirectionUtil';

@Component({
  selector: 'eet-pending-request',
  templateUrl: './pending-request.component.html',
  styleUrls: ['./pending-request.component.scss'],
})
export class PendingRequestComponent implements OnInit,AfterViewInit {
  @ViewChild(MatPaginator)
  public paginator!: MatPaginator;
  @ViewChild(MatSort) sort: MatSort = new MatSort();
  
  public readonly pageSizeOptions = CONFIG.PAGINATION_OPTIONS;
  public statusClass: string;
  public displayedColumns: string[] = [
    'number',
    'requester',
    'created_date',
    'updated_date',
    'status',
    'action',
  ];
  private requestData: RequestInf[];
  public dataSource = new MatTableDataSource<RequestInf>();
  public selection = new SelectionModel<RequestInf>(true, []);
  public forward_evaluation_title: string = '';

  constructor(
    private _liveAnnouncer: LiveAnnouncer,
    public dialog: MatDialog,
    private dialogCommonService: DialogCommonService,
    private manageRequestService: ManageRequestService,
    private translateService: TranslateService,
    private notificationService: NotificationService,
    private loaderService: LoadingService,
    private skillEvaluationService: SkillEvaluationService,
    private viewRequestDetailService: ViewRequestDetailService,
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
    this.translateService
      .get('manage_request.title.forward_request')
      .subscribe((messageValue: any) => {
        this.forward_evaluation_title = messageValue;
      });
  }

  private getAssociateId(): string {
    const _localStorage = JSON.parse(localStorage.getItem('Authorization') || '{}');
    return _localStorage.id
  }

  getRequestList(isShowNotification?: boolean) {
    this.requestData = [];
    const loader = this.loaderService.showProgressBar();

    this.manageRequestService.getRequestData().pipe(finalize(() => {
      this.loaderService.hideProgressBar(loader);
      if (isShowNotification) { this.notificationService.success(this.translateService.instant('notification.send_request-evaluation')) }
    })).subscribe((data) => {
      let _tmpData = data.data;
      //check if they are competency lead, if they approve/reject all skill then wont display request
      _tmpData.map((element: any) => {
        (element.status === APPROVAL_PENDING) ? element.statusMessage = APPROVAL_PENDING_MSG : ""
        if (!element.competencyLeadEvaluateAll) { this.requestData.push(element); }
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
      component: ViewRequestDetailComponent,
      title: this.translateService.instant("manage_request.title.pending_request_detail"),
      width: '95vw',
      height: 'auto',
      type: 'edit',
      // maxWdith: '1400px',
      passingData: {
        //will pass the change request to the dialog here
        id: request,
      },
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (result === APPROVED) {
          this.notificationService.success(this.translateService.instant('notification.send_request-evaluation_approved')).afterOpened().subscribe(result => {
            this.getRequestList();
          });
        }
        else if (result === REJECTED) {
          this.notificationService.success(this.translateService.instant('notification.send_request-evaluation_rejected')).afterOpened().subscribe(result => {
            this.getRequestList();
          });
        }
        else {
          this.getRequestList(result)
        }

      }
    });
  }

  onForward(item: RequestInf) {
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: ForwardRequestComponent,
      title: this.forward_evaluation_title,
      width: '70vw',
      height: 'auto',
      maxWdith: '1200px',
      type: 'edit',
      passingData: {
        requestId: item.id,
      },
    });

    dialogRef.afterClosed();
  }

  getPendingRequest(){
    const associateId = this.getAssociateId()
    this.skillEvaluationService.getPendingRequest(associateId).subscribe((rs) => {
      this.viewRequestDetailService.sendPendingRequest('changeCount', rs?.data?.count);
    })
  }

}
