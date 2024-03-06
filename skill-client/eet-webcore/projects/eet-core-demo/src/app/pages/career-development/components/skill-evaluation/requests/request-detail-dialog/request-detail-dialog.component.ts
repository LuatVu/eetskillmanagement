import { formatDate } from '@angular/common';
import { Component, Inject, Input, OnInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { TOKEN_KEY } from '@core/src/lib/shared/util/system.constant';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { finalize } from 'rxjs';
import { experiences_value, levels, behavioral_levels, technical_levels} from '../../mock/mock-data';
import { DATE_FORMAT } from '../../model/request-evaluation.constants';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';

import {
  RequestEvaluationDataComparison,
  RequestEvaluationDataModel,
  Skill,
} from '../../model/skill-evaluation.model';
import { SelectApproverDialogComponent } from '../../select-approver-dialog/select-approver-dialog.component';
import { SkillEvaluationService } from '../../skill-evaluation.service';
import { REQUEST_STATUS, colorAvatar } from '../mock/mock-data';
import { PersonalInfomationModel } from 'projects/eet-core-demo/src/app/pages/user-information/modules/personal-information/personal-infomation.model';
import { PersonalInfomationService } from 'projects/eet-core-demo/src/app/pages/user-information/modules/personal-information/personal-infomation.service';
import { SkillDetailDialogComponent } from '../../skill-detail-dialog/skill-detail-dialog.component';

@Component({
  selector: 'eet-request-detail-dialog',
  templateUrl: './request-detail-dialog.component.html',
  styleUrls: ['./request-detail-dialog.component.scss'],
})
export class RequestDetailDialogComponent implements OnInit {
  @Input() isNotFromDialog: boolean = true;
  private levels: any;
  private experiences: any;
  public imageToShow: any;
  public newshortname!: string;
  public showshortname: boolean = false;
  public showavatar: boolean = false;
  public avatarcolor!: string;
  public avatarbgcolor!: string;
  public personalInfo!: PersonalInfomationModel;
  public associate: boolean = true;
  public associatemanager: boolean = true;
  public isEdited!: boolean;
  private totalStatus: string;
  public displayedColumns: string[] = [
    'skill',
    'skill_group',
    'current_level',
    'old_level',
    'expected_level',
    'current_experience',
    'approver_comment',
    'comment',
    'status',
    'competency_lead'
  ];

  public levelList: string[]
  public technicalLevelList: string[]
  public behavioralLevelList: string[]
  public experienceList: any[] = experiences_value
  public status: Map<string, string> = REQUEST_STATUS;
  public skillData: RequestEvaluationDataModel;
  public isSkillChanged: RequestEvaluationDataComparison[];
  public readonly pageOptions = CONFIG.PAGINATION_OPTIONS;
  dataSource: MatTableDataSource<Skill>;
  @ViewChild(MatPaginator)
  paginator!: MatPaginator;
  @ViewChild(MatSort)
  sort!: MatSort;
  comments: string[];

  constructor(
    private router: Router,
    private skillEvaluationService: SkillEvaluationService,
    private dialogCommonService: DialogCommonService,
    private personalInfomationService: PersonalInfomationService,
    private loader: LoadingService,
    private notification: NotificationService,
    private dialogRef: MatDialogRef<RequestDetailDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private changeDetectorRef : ChangeDetectorRef
  ) {
    this.comments = [];
    this.skillData = {
      id: '',
      requester: '',
      approver: '',
      approver_id: '',
      status: CONFIG.REQUEST_STATUS.APPROVAL_PENDING,
      comment: '',
      request_evaluation_details: [],
      created_date: '',
      updated_date: '',
    };
    this.levelList = levels
    this.technicalLevelList = technical_levels
    this.behavioralLevelList = behavioral_levels
    this.isSkillChanged = [];
    this.dataSource = new MatTableDataSource(
      this.skillData.request_evaluation_details
    );
    this.comLoader = this.loader.showProgressBar();
  }

  ngOnInit(): void {
    this.getskillData(this.data.data.evaluationId);
    this.totalStatus = this.data.status
    let ramdomNumber = Math.floor(Math.random() * 6);
    this.avatarcolor = colorAvatar[ramdomNumber].color;
    this.avatarbgcolor = colorAvatar[ramdomNumber].backgroundColor;

    this.isEdited = this.data['data']?.isEdited as any;
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  //avatar with short name
  getInitials(nameString: any) {
    const fullName = nameString.split(' ');
    const firstname = fullName[0];
    const lastname = fullName[fullName.length - 2];
    const initials = firstname.substring(0, 1) + lastname.substring(0, 1);
    this.newshortname = initials;
    return this.newshortname.toUpperCase();
  }

  onChange(selection: any) {
    return selection;
  }

  //check to highlight skill
  onCheckUpdated(current: any, old: any) {
    return ((current != old) ? 'UPDATED_CLASS' : 'NOT_UPDATED_CLASS')
  }

  onCheckStatus(status: string) {
    return (status != 'APPROVAL_PENDING')
  }

  private comLoader;
  private getskillData(evaluation_id: string) {
    this.skillEvaluationService.getDetailedRequestEvaluation(evaluation_id).pipe(finalize(() => {
      this.loader.hideProgressBar(this.comLoader);
    })).subscribe((data) => {
      this.skillData = data.data;
      let _tmpData = this.skillData.request_evaluation_details
      if (this.skillData.status === 'APPROVAL_PENDING') {
        _tmpData.map(element => {
          element.isDisabled = element.status != 'APPROVAL_PENDING'
        })
      }
      else {
        _tmpData.map(element => {
          element.isDisabled = element.status == 'APPROVED'
        })
      }
      this.dataSource.data = _tmpData
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      if (!this.isEdited) {
        this.getPersonalInfor(this.skillData.approver_id || '');
      }
    });
  }

  //get data from back end
  private getPersonalInfor(loginId: string) {
    this.personalInfomationService.getPersonInfo(loginId).subscribe((data) => {
      this.personalInfo = data;
      this.getInitials(data.name);
      this.imageToShow = data.picture;
      if (this.imageToShow == null) {
        this.showshortname = false;
        this.showavatar = true;
      } else {
        this.showshortname = true;
        this.showavatar = false;
      }
    });
  }

  onEvaluate() {
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: SelectApproverDialogComponent,
      title: 'Select Approver',
      width: '80vw',
      height: 'auto',
      icon: 'a-icon ui-ic-alert-info',
      maxWdith: '800px',
      type: 'edit',
      passingData: {
        personalId: this.getAssociateId()
      },
    });
    dialogRef.afterClosed().subscribe((response) => {
      const comDialog = this.loader.showProgressBar();
      if (response && response.result === true) {
        const currentDate = formatDate(
          new Date(),
          DATE_FORMAT,
          'en-US',
          '+0700'
        );
        let data: RequestEvaluationDataModel = {
          id: this.skillData.id,
          requester: this.getAssociateId(),
          approver: response.id,
          status: this.skillData.status,
          comment: '',
          request_evaluation_details: this.skillData.request_evaluation_details.filter(element => element.isDisabled != true),
          created_date: this.data.data.createdDate,
          updated_date: currentDate,
        };
        this.skillEvaluationService
          .postRequestEvaluation(data).pipe(finalize(() => this.loader.hideProgressBar(comDialog))).subscribe((response) => {
            this.loader.hideProgressBar(comDialog);
            if (response.code !== CONFIG.API_RESPONSE_STATUS.SUCCESS) {
              this.notification.error(response.message);
            } else {
              this.dialogRef.close(true);
            }
          });
      } else {
        this.loader.hideProgressBar(comDialog);
      }
    });
  }

  private getAssociateId(): string {
    const _localStorage = JSON.parse(
      localStorage.getItem(TOKEN_KEY) || '{}'
    );
    return _localStorage.id;
  }

  onResize(element: any) {
    element.target.style.height = '0'
    element.target.style.height = (element.target.scrollHeight + 6) + 'px'
  }

  onReset() { }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  applyColor(element: Skill, type: 'level' | 'experience') {
    if (this.skillData.status !== CONFIG.REQUEST_STATUS.APPROVAL_PENDING) {
      return false;
    }
    if (type === 'level') {
      return element.current_level !== element.old_level ? true : false;
    }
    else {
      return element.current_experience !== element.old_experience ? true : false;
    }
  }
  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  onViewDetail(skillInfo: Skill) {
    let filteredData = skillInfo.skill_description.sort((a, b) => a.level > b.level ? 1 : -1)
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: SkillDetailDialogComponent,
      title: 'Skill Information',
      width: '80vw',
      icon: 'a-icon ui-ic-alert-info',
      height: 'auto',
      maxWdith: '800px',
      type: 'view',
      passingData: {
        skillTitle: skillInfo.skill,
        levelList: filteredData
      },
    });
    dialogRef.afterClosed();
  }

  buildColor_CurrentLevel(element: any){
    return Helpers.buildColor_CurrentLevel(element.old_level, element.expected_level);
  }
}
