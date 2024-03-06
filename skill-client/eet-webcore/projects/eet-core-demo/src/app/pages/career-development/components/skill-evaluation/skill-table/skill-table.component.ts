import { formatDate } from '@angular/common';
import { Component, ElementRef, Inject, Input, LOCALE_ID, OnInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { map } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { DEFAULT_PAGE, DEFAULT_SIZE } from '../model/request-evaluation.constants';
import { RequestEvaluationDataModel, Skill } from '../model/skill-evaluation.model';
import { SelectApproverDialogComponent } from '../select-approver-dialog/select-approver-dialog.component';
import { SkillDetailDialogComponent } from '../skill-detail-dialog/skill-detail-dialog.component';
import { SkillEvaluationService } from '../skill-evaluation.service';
import { NotificationService } from '@bci-web-core/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { PaginDirectionUtil } from 'projects/eet-core-demo/src/app/shared/utils/paginDirectionUtil';
import { technical_levels, behavioral_levels, experiences, experiences_value } from '../mock/mock-data';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { DATA_CONFIG } from '../../../../user-information/modules/associate-information/associate-info.constant';
import { CareerDevelopmentService } from '../../../career-development.service';
import { CanDeactivateGuard } from 'projects/eet-core-demo/src/app/shared/utils/can-deactivate.guard';

@Component({
  selector: 'eet-skill-table',
  templateUrl: './skill-table.component.html',
  styleUrls: ['./skill-table.component.scss'],
})
export class SkillTableComponent implements OnInit, CanDeactivateGuard {
  @Input() isNotFromDialog: boolean = true;
  // @Output() isSuccessfullySubmitted: EventEmitter<boolean> = new EventEmitter<boolean>();

  public displayedColumns: string[] = [
    'skill_group',
    'skill',
    'evaluate_level',
    'current_level',
    'expected_level',
    'current_experience',
    'comment',
  ];
  public readonly paginationOption = CONFIG.PAGINATION_OPTIONS;
  public levels: string[];
  public experiences: number[] = experiences;
  public experiences_value: string[] = experiences_value;
  private skillData: Skill[];
  private uneditedData: Skill[];
  public dataSource = new MatTableDataSource<Skill>();
  public evaluateAllowed: boolean = false;
  page: number = DEFAULT_PAGE
  size: number = DEFAULT_SIZE
  ROW_MAX_HEIGHT= 45;
  @ViewChild(MatPaginator)
  public paginator!: MatPaginator;
  @ViewChild(MatSort)
  sort!: MatSort;
  public showMainSkillCluster: boolean = true;
  public isFilterLessThanExpected: boolean = false;
  public _filterValue: string;
  public isTechnical: boolean = false;
  public isEditted: boolean = false;

  constructor(
    private router: Router,
    private skillEvaluationService: SkillEvaluationService,
    private dialogCommonService: DialogCommonService,
    private comLoader: LoadingService,
    private notifyService: NotificationService,
    @Inject(LOCALE_ID) private locale: string,
    private changeDetectorRef : ChangeDetectorRef,
    private careerDevelopmentService:CareerDevelopmentService
  ) {
    this.uneditedData = [];
    this.skillData = [];
    this.dataSource = new MatTableDataSource(this.skillData);
  }
  private TECHNICAL_SKILLTYPE = DATA_CONFIG.TECHNICAL_SKILL_TYPE
  private BEHAVIORAL_SKILLTYPE = DATA_CONFIG.BEHAVIORAL_SKILL_TYPE
  private arrFilter = [
    {
      id:"toggle",
      value:this.TECHNICAL_SKILLTYPE
    },
    {
      id:"mainSkill",
      value:true
    },
    {
      id:"notMeetExpectation",
      value:false
    },
    {
      id:"search",
      value:''
    }
  ]
  ngOnInit(): void {
    this.getskillData(this.getAssociateId());
    this.isEvaluateAllowed();
    
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.sort?.sort({
      id: 'skill_group',
      start: 'asc',
      disableClear: false
    });

    PaginDirectionUtil.expandTopForDropDownPagination()

    const contentDiv = document.querySelector('.content') as HTMLDivElement;
    if (contentDiv) {
      contentDiv.style.overflow = 'auto';
    } 

    this.levels = this.isTechnical ? technical_levels : behavioral_levels; 
  }
  isDataChanged = false;
  public isFilterMainSkill: boolean = true;

  onChange(element: Skill, event: any) {
    if (JSON.stringify(this.skillData) === JSON.stringify(this.uneditedData)) {
      this.isDataChanged = false;
      this.isEditted = false;
    }
    else {
      this.isEditted = true;
      this.isDataChanged = true;
    }
  }

  filterLessThanExpected(event: any) {
    this.isFilterLessThanExpected = event;
    this.arrFilter[2].value = event
    this.handleFilter()
  }

  public oldFilter: boolean;

  filterType(e: any) {
    this.isTechnical = !this.isTechnical;
    this.showMainSkillCluster = e.data === 'Behavioral' ? false : true;
    const valueToggle = e.data? e.data : e
    this.arrFilter[3].value=''
    this._filterValue=''
    this.arrFilter[0].value = valueToggle
    this.handleFilter()

    this.levels = this.isTechnical ? technical_levels : behavioral_levels; 
  }
  
  private mainSkillCluster: string[] = [];
  onMainSkillClusterChange(event: any) {
    this.arrFilter[1].value = event
    this.handleFilter()
  }
  private getAssociateId(): string {
    const _localStorage = JSON.parse(localStorage.getItem('Authorization') || '{}');
    return _localStorage.id
  }

  private getskillData(associateID: string, isShowProgressBar?: boolean) {
    const loader = this.comLoader.showProgressBar()
    this.skillEvaluationService.getPersonalEvaluate(associateID).pipe(
      map(result => {
        if (result.code === CONFIG.API_RESPONSE_STATUS.SUCCESS && !result.data.comment) {
          result.data.comment = ""
        }
        return result;
      }),
      /**
      map(element => {
        for(let i = 0; i < element.data.listSkill.length; i++) {
          if (!!element.data.listSkill[i].skill_description && element.data.listSkill[i].skill_description.length > 0) {
            element.data.listSkill[i].currentLevelList = (element.data.listSkill[i].skill_description.map((skillDesc: any) => {
              return skillDesc.level.split(' ')[1]
            })).sort((a: any, b: any) => {
              return a > b ? 1 : a < b ? -1 : 0
            })
          }
        }
        return element;
      })
      */
    ).pipe(finalize(() => {
      this.comLoader.hideProgressBar(loader)
    })).subscribe((data: any) => {

      this.uneditedData = JSON.parse(JSON.stringify(data.data.listSkill))
      this.skillData = JSON.parse(JSON.stringify(data.data.listSkill))

      this.skillData.map(element => {
        element.old_level = element.current_level
        element.old_experience = element.current_experience
      })

      this.mainSkillCluster = data.data.mainSkillCluster || []
      this.dataSource.data = this.skillData;
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.onMainSkillClusterChange(true)
    });
  }

  private isEvaluateAllowed() {
    const loader = this.comLoader.showProgressBar()
    this.skillEvaluationService.getRequestEvaluation(this.page, this.size)
      .pipe(finalize(() => this.comLoader.hideProgressBar(loader)))
      .subscribe((data) => {
        this.evaluateAllowed = (data.data.filter(function (item: any) {
          return item.status === CONFIG.REQUEST_STATUS.APPROVAL_PENDING
        })).length > 0 ? false : true
      })
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

  onResize(element: any) {
    element.target.style.height = '0'
    element.target.style.height = (element.target.scrollHeight + 6) + 'px'
  }

  //check to highlight skill
  onCheckUpdated(current: any, old: any) {
    return ((current != old) ? 'UPDATED_CLASS' : 'NOT_UPDATED_CLASS')
  }

  onEvaluate() {
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: SelectApproverDialogComponent,
      title: 'Select Approver',
      width: '80vw',
      height: 'auto',
      icon: 'a-icon ui-ic-alert-info',
      maxWdith: '800px',
      type: 'view',
      passingData: {
        personalId: this.getAssociateId()
      }
    });
    dialogRef.afterClosed().subscribe((response) => {
      if (!!response && response.result === true) {
        const loader = this.comLoader.showProgressBar()
        const currentDate = formatDate(new Date(), 'yyyy-MM-dd', this.locale)
        let requestEvaluationDetails: Skill[] = []
        this.skillData.map(element => {
          if (element.current_level != element.old_level || element.current_experience != element.old_experience) { requestEvaluationDetails.push(element) }
        })

        let data: RequestEvaluationDataModel = {
          requester: this.getAssociateId(),
          approver: response.id,
          status: CONFIG.REQUEST_STATUS.APPROVAL_PENDING,
          comment: '',
          request_evaluation_details: requestEvaluationDetails,
          created_date: currentDate,
        }
        this.skillEvaluationService.postRequestEvaluation(data).pipe(finalize(() => this.comLoader.hideProgressBar(loader))).subscribe((response) => {
            this.isEvaluateAllowed();
            if(response && response?.code && response?.code == CONFIG.API_RESPONSE_STATUS.SUCCESS){
              this.notifyService.success(response?.message);
              this.getskillData(this.getAssociateId());
            }else {
              this.notifyService.error(response?.message);
            }
          },
          (error) => {
            this.notifyService.error(error?.error?.message);
          }
          )
      }
    });
    this.isEditted = false;
    this.careerDevelopmentService.setConfirm(true);
  }

  onReset() {
    this.getskillData(this.getAssociateId())
    this.isDataChanged = false;
    this.careerDevelopmentService.setConfirm(true);
    this.isEditted = false;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.arrFilter[3].value = filterValue.trim().toLocaleLowerCase()
    this.handleFilter()
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  returnSelectedOption(element: any) {
    return false;
  }
  checkElementIsOverflow(element: any){
    return element.scrollHeight > this.ROW_MAX_HEIGHT;
  }

  ngAfterViewChecked(): void {
    //Called after every check of the component's view. Applies to components only.
    //Add 'implements AfterViewChecked' to the class.
    this.changeDetectorRef.detectChanges();
  }

  buildColor_CurrentLevel(element: any){
    return Helpers.buildColor_CurrentLevel(element.old_level, element.expected_level);
  }

  truncateText(text: string, num: number = 25) {
    return text?.length > num ? text.slice(0, num) + "..." : text
  }
  handleFilter() {
    this.dataSource.data = this.skillData.filter((skill: any) => {
      const c1 = !this.arrFilter[0].value || skill.skill_type === this.arrFilter[0].value;
      const c2 = this.arrFilter[0].value === 'Technical' ? (!this.arrFilter[1].value || this.mainSkillCluster.includes(skill.skill_group)) : true;
      const c3 = !this.arrFilter[2].value || (skill.current_level < skill.expected_level);
      const c4 = !this.arrFilter[3].value || skill.skill.toLowerCase().includes(this.arrFilter[3].value.toString().toLowerCase());
      
      return c1 && c2 && c3 && c4;
    });
  }

  customParseFloat(value:string) {
    const floatValue = parseFloat(value);
    return Number.isInteger(floatValue) ? parseInt(value) : floatValue;
  }


  canDeactivate(): boolean | Promise<boolean> | import('rxjs').Observable<boolean> {
    if(this.isEditted) {
        this.careerDevelopmentService.setOldPageIndex(1);
        const confirmation = confirm('This page is asking you to confirm that you want to leave — information you\'ve entered may not be saved.');
        this.careerDevelopmentService.setConfirm(confirmation);
        return confirmation;
      } else {
          this.careerDevelopmentService.setConfirm(true);
          return true;
      }
    }
    
}