import { Component, Inject, Input, OnInit, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { BciLoaderService } from '@bci-web-core/core';
import { TranslateService } from '@ngx-translate/core';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { finalize } from 'rxjs';
import { APPROVAL_PENDING, APPROVED, COMP_APPROVAL_PENDING_MSG, COMP_APPROVED_MSG, COMP_REJECTED_MSG, EXP_LIST, REJECTED, levelList } from '../../constants/constants';
import { RequestDetail, SkillDetailLevelModel, infoListModel } from '../../models/manage-request.model';
import { ViewRequestDetailService } from './view-previous-request-detail.service';
import { SkillDetailDialogComponent } from '../../../career-development/components/skill-evaluation/skill-detail-dialog/skill-detail-dialog.component';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
@Component({
  selector: 'eet-view-request-detail',
  templateUrl: './view-previous-request-detail.component.html',
  styleUrls: ['./view-previous-request-detail.component.scss'],
})
export class ViewPreviousRequestDetailComponent implements OnInit {
  @Input() isNotFromDialog: boolean = true;
  pageOption = [5, 10, 20];
  infoList: infoListModel;

  levelList = levelList
  experienceList = EXP_LIST

  skillHighlight!: string;
  displayedColumns: string[] = [
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
    'competency_lead'
  ];
  requestDetail: RequestDetail[];
  dataSource = new MatTableDataSource<RequestDetail>();


  @ViewChild(MatPaginator)
  paginator!: MatPaginator;
  @ViewChild(MatSort)
  sort: MatSort = new MatSort();

  public isDisabled: boolean;

  constructor(
    private viewRequestDetailService: ViewRequestDetailService,
    @Inject(MAT_DIALOG_DATA) public requestData: any,
    private loader: BciLoaderService,
    private translateService: TranslateService,
    private dialogRef: MatDialogRef<ViewPreviousRequestDetailComponent>,
    private dialogCommonService: DialogCommonService,
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
