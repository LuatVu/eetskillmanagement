import { AfterViewInit, Component, OnInit, ViewChild,ChangeDetectorRef } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { finalize } from 'rxjs';
import { MyRequestEvaluationData } from '../model/request-evaluation-list';
import { DEFAULT_PAGE, DEFAULT_SIZE } from '../model/request-evaluation.constants';
import { SkillEvaluationService } from '../skill-evaluation.service';
import { RequestDetailDialogComponent } from './request-detail-dialog/request-detail-dialog.component';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { PaginDirectionUtil } from 'projects/eet-core-demo/src/app/shared/utils/paginDirectionUtil';

// There will be a class here that converts changes.


@Component({
  selector: 'eet-requests',
  templateUrl: './requests.component.html',
  styleUrls: ['./requests.component.scss'],
})
export class RequestsComponent implements OnInit,AfterViewInit {
  public pageOption = CONFIG.PAGINATION_OPTIONS
  public displayedColumns: string[] = [
    'position',
    'approver',
    'status',
    'created_date',
    'updated_date',
    'approved_date',
    'action',
  ];
  private requestData: MyRequestEvaluationData[] = []
  public isEditAllowed: boolean = false;
  dataSource = new MatTableDataSource(this.requestData);
  @ViewChild(MatPaginator)
  public paginator!: MatPaginator;
  @ViewChild(MatSort)
  sort!: MatSort;
  currentPage: number = DEFAULT_PAGE
  currentSize: number = DEFAULT_SIZE
  constructor(
    public dialog: MatDialog,
    private dialogCommonService: DialogCommonService,
    private requestEvaluationService: SkillEvaluationService,
    private skillEvaluationService: SkillEvaluationService,
    private loader: LoadingService,
    private changeDetectorRef : ChangeDetectorRef
  ) {
    this.comLoader = this.loader.showProgressBar()
  }

  ngOnInit(): void {
    this.getRequestEval()
  }
  ngAfterViewInit(): void {
    PaginDirectionUtil.expandTopForDropDownPagination()
  }
  private comLoader;
  getRequestEval() {
    this.requestEvaluationService.getRequestEvaluation(this.currentPage, this.currentSize).pipe(finalize(() => {
      this.loader.hideProgressBar(this.comLoader);
    })).subscribe(response => {
      const STATUS_MESSAGE: any = {
        APPROVED: "All Approved",
        REJECTED: "All Rejected",
        APPROVAL_PENDING: "Waiting for Approval"
      }
      let _tmpData: any = response.data || [];
      // Sort default data by Created Date.
      _tmpData = _tmpData.sort((_preValue: any, _nextValue: any) => {
        return _preValue.created_date < _nextValue.created_date ? 1 : -1;
      }).sort((_preValue: any, _nextValue: any) => {
        return _preValue.status == 'APPROVAL_PENDING' ? -1 : _nextValue.status == 'APPROVAL_PENDING' ? 1 : 0;
      });
      const _pendingIndex = _tmpData.findIndex((element: any) => element.status == 'APPROVAL_PENDING');
      _tmpData.map((element: any) => {
        element.statusMsg = STATUS_MESSAGE?.[element.status] || 'Some Approved/Rejected';
        element.isEnabled = _pendingIndex != - 1 ?
          element.status == 'APPROVAL_PENDING' : element.status != 'APPROVED'
      });
      this.dataSource.data = _tmpData;
      this.dataSource.sort = this.sort
      this.dataSource.paginator = this.paginator
      this.dataSource.paginator.firstPage();
    })
  }

  onClick(request: any, isEdited: boolean) {
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: RequestDetailDialogComponent,
      title: 'Skill Information',
      width: '80vw',
      icon: 'a-icon ui-ic-alert-info',
      height: 'auto',
      type: isEdited ? 'edit' : 'view',
      passingData: {
        evaluationId: request.id,
        createdDate: request.created_date,
        status: request.status,
        isEdited
      },
    });

    dialogRef.afterClosed().subscribe(response => {
      if (response === true) {
        this.getRequestEval()
      }
    })
  }

  onCheckStatus(request: any) {
    switch (request.status) {
      case 'APPROVED':
        request.statusMsg = 'Approved'
        break
      case 'REJECTED':
        request.statusMsg = 'Rejected'
        break
      case 'APPROVAL_PENDING':
        request.statusMsg = 'Waiting for Approval'
        break
      default:
        request.statusMsg = 'Approved/Rejected'
        break
    }
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }
}
