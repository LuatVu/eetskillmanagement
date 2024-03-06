import { Component, Inject, Input, OnInit, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { TranslateService } from '@ngx-translate/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { finalize } from 'rxjs';
import { APPROVAL_PENDING, APPROVED, COMP_APPROVAL_PENDING_MSG, COMP_APPROVED_MSG, COMP_REJECTED_MSG, EXP_LIST, REJECTED, technicalLevelList, behavioralLevelList } from '../../constants/constants';
import { RequestDetail, SkillDetailLevelModel, infoListModel } from '../../models/manage-request.model';
import { CloseDialogResultModel, CommentDialogComponent } from '../dialogs/comment-dialog/comment-dialog.component';
import { ViewRequestDetailService } from './view-request-detail.service';
import { SkillDetailDialogComponent } from '../../../career-development/components/skill-evaluation/skill-detail-dialog/skill-detail-dialog.component';
import { SkillEvaluationService } from '../../../career-development/components/skill-evaluation/skill-evaluation.service';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';

@Component({
  selector: 'eet-view-request-detail',
  templateUrl: './view-request-detail.component.html',
  styleUrls: ['./view-request-detail.component.scss'],
})
export class ViewRequestDetailComponent implements OnInit {
  @Input() isNotFromDialog: boolean = true;
  public readonly pageSizeOptions = CONFIG.PAGINATION_OPTIONS;
  public infoList: infoListModel;

  public technicalLevelList = technicalLevelList
  public behavioralLevelList = behavioralLevelList
  public experienceList = EXP_LIST

  private skillHighlight!: string;
  public displayedColumns: string[] = [
    'skill_info',
    'skill',
    'skill_group',
    'current_level',
    'old_level',
    'expected_level',
    'experience',
    'comment',
    'is_forwarded',
    'status',
    'competency_lead',
    'action'
  ];
  private requestDetail: RequestDetail[];
  public dataSource = new MatTableDataSource<RequestDetail>();

  @ViewChild(MatPaginator)
  public paginator!: MatPaginator;
  @ViewChild(MatSort)
  sort: MatSort = new MatSort();

  public isDisabled: boolean;

  constructor(
    private viewRequestDetailService: ViewRequestDetailService,
    @Inject(MAT_DIALOG_DATA) public requestData: any,
    private loader: LoadingService,
    private notify: NotificationService,
    private translateService: TranslateService,
    private dialogRef: MatDialogRef<ViewRequestDetailComponent>,
    private dialogCommonService: DialogCommonService,
    private skillEvaluationService: SkillEvaluationService,
  ) {
    this.infoList = { requester: '', approver: '', approver_id: '', manager_comment: '' }

    this.requestDetail = [];
    this.dataSource = new MatTableDataSource(this.requestDetail);
  }

  ngOnInit(): void {
    this.getRequestDetail();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  onChange(selection: any) {
    return selection;
  }

  //api GET
  getRequestDetail() {
    let loading = this.loader.showProgressBar();
    this.viewRequestDetailService
      .getRequestDetailData(this.requestData.data.id).pipe(finalize(() => {
        this.loader.hideProgressBar(loading);
      }))
      .subscribe((response) => {
        let _tmpData = response.data;
        this.infoList = {
          requester: _tmpData.requester,
          approver: _tmpData.approver,
          approver_id: _tmpData.approver_id,
          manager_comment: _tmpData.comment
        }

        this.requestDetail = _tmpData.request_evaluation_details;
        //check if data is all evaluated
        if (this.requestDetail && this.requestDetail.length != 0 && !this.requestDetail.find(f => f.status === APPROVAL_PENDING)) {
          this.dialogRef.close(true);
          return;
        }

        this.requestDetail.map((element) => {
          if (element.status != APPROVAL_PENDING) { element.isDisabled = true }

          element.statusMessage = this.filterStatus(element.status)

          element.experience = this.translateService.instant(`manage_request.experience.${EXP_LIST[element.current_experience]}`)
          element.skillHighlight = this.onCheckUpdated(element.current_level, element.old_level);

        });

        this.dataSource = new MatTableDataSource(this.requestDetail);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      });
  }

  //set experience change
  onWatchExperienceChange(item: RequestDetail) {
    item.current_experience = EXP_LIST.indexOf(item.experience);
  }

  //Check to apply CSS
  onCheckStatus(status: any) {
    if (status === COMP_APPROVED_MSG) return APPROVED;
    else if (status === COMP_REJECTED_MSG) return REJECTED;
    else return APPROVAL_PENDING;
  }

  filterStatus(status: string) {
    if (status === APPROVAL_PENDING) {
      return COMP_APPROVAL_PENDING_MSG;
    } else if (status === APPROVED) {
      return COMP_APPROVED_MSG;
    } else return COMP_REJECTED_MSG;
  }

  //check to highlight skill
  onCheckUpdated(current: any, old: any) {
    return ((current != old) ? 'UPDATED_CLASS' : 'NOT_UPDATED_CLASS')
  }

  //evaluate request
  onEvaluateAll(returnType: string) {

    const _dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: CommentDialogComponent,
      title: this.translateService.instant('dialog.title_confirm'),
      width: '65vw',
      maxWdith: '1000px',
      height: 'auto',
      type: 'view', 
      passingData: {
        titleConfirm:
          returnType == 'APPROVED' ?
            this.translateService.instant('manage_request.dialog.confirm_approve_all_request') :
            this.translateService.instant('manage_request.dialog.confirm_reject_all_request')
      }
    })

    _dialogRef.afterClosed().subscribe((result: CloseDialogResultModel) => {
      if (result?.isConfirm) {
        let loader = this.loader.showProgressBar();

        const _requestDetailData = this.requestDetail;
        _requestDetailData.map(m => delete m.experience)

        this.viewRequestDetailService.updateAllRequestStatus(this.requestData.data.id, {
          id: this.requestData.data.id,
          approver_id: this.infoList.approver_id,
          status: returnType,
          request_evaluation_details: _requestDetailData,
          comment: result.comment
        }).pipe(finalize(() => this.loader.hideProgressBar(loader)))
          .subscribe((data) => {
            if (data) {
              this.dialogRef.close(returnType)
              const _localStorage = JSON.parse(localStorage.getItem('Authorization') || '{}');
              this.skillEvaluationService.getPendingRequest(_localStorage.id).subscribe((rs) => {
                this.viewRequestDetailService.sendPendingRequest('changeCount', rs?.data?.count);
              })
            }
          });
      }
    })
  }

  onEvaluateSingle(element: RequestDetail, returnType: string) {

    const _dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: CommentDialogComponent,
      title: this.translateService.instant('dialog.title_confirm'),
      width: '65vw',
      maxWdith: '1000px',
      height: 'auto',
      type: 'view',
      passingData: {
        titleConfirm:
          returnType == 'APPROVED' ?
            this.translateService.instant('manage_request.dialog.confirm_approve_request') :
            this.translateService.instant('manage_request.dialog.confirm_reject_request')
      }
    })

    _dialogRef.afterClosed().subscribe((result: CloseDialogResultModel) => {
      if (result?.isConfirm) {
        let loader = this.loader.showProgressBar();

        this.viewRequestDetailService.updateSingleRequestStatus(this.requestData.data.id, element.id, {
          approver_comment: result?.comment,
          status: returnType,
          skill_description: { current_level: element.current_level, current_experience: element.current_experience }
        }).pipe((finalize(() =>
          this.loader.hideProgressBar(loader))))
          .subscribe((data) => {
            this.getRequestDetail();
            const _localStorage = JSON.parse(localStorage.getItem('Authorization') || '{}');
            this.skillEvaluationService.getPendingRequest(_localStorage.id).subscribe((rs) => {
              this.viewRequestDetailService.sendPendingRequest('changeCount', rs?.data?.count);
            })
          });
      }
    })

  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  showSkillInfo(name: string, skillInfo: any){
    var skillLevelList = (skillInfo as Array<SkillDetailLevelModel>).sort((a: SkillDetailLevelModel, b: SkillDetailLevelModel) => a.level.localeCompare(b.level));
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: SkillDetailDialogComponent,
      title: this.translateService.instant('manage_request.dialog.skill_information'),
      width: '80vw',
      height: 'auto',
      maxWdith: '800px',
      icon: 'a-icon ui-ic-alert-info',
      type: 'view',
      passingData: {
        skillTitle: name,
        levelList: skillLevelList
      },
    });
  }

  buildColor_CurrentLevel(element: any){    
    return Helpers.buildColor_CurrentLevel(element.old_level, element.expected_level);
  }
}
