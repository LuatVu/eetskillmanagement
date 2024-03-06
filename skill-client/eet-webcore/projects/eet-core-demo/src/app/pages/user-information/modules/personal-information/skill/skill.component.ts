import { AfterViewInit, ChangeDetectorRef, Component, Input, OnInit, ViewChild } from '@angular/core';
import { MatIconRegistry } from '@angular/material/icon';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, MatSortable } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { DomSanitizer } from '@angular/platform-browser';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { ReplaySubject, takeUntil } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { SkillDetailDialogComponent } from '../../../../career-development/components/skill-evaluation/skill-detail-dialog/skill-detail-dialog.component';
import { DEFAULT_PAGE_SIZE } from '../constants/constants';
import { SkillModel } from '../personal-infomation.model';
import { PersonalInfomationService } from '../personal-infomation.service';
import { PersonalInfoService } from '../personal.service';
import { threadId } from 'worker_threads';
import { DATA_CONFIG } from '../../associate-information/associate-info.constant';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';

export interface SkillDetailLevelModel {
  name: string;
  description: string;
}

interface skillFilter {
  id: string;
  type: string;
}

@Component({
  selector: 'eet-skill',
  templateUrl: './skill.component.html',
  styleUrls: ['./skill.component.scss'],
})
export class SkillComponent implements OnInit, AfterViewInit {
  @ViewChild(MatSort) sort: MatSort = new MatSort();
  @Input() typeUser: 'Manager' | 'Associate' = 'Manager';
  @Input() paginator:MatPaginator
  public readonly paginationSize = CONFIG.PAGINATION_OPTIONS;
  public experienceMsg: string[] = ['0', '1-6 Months', '6-12 Months', '1-3 Years', '3-5 Years', '5+ Years']; //hardcode experience display value
  public displayedColumns: string[] = ['skill', 'compentency', 'current_level', 'expected_level', 'experience'];
  public dataSource = new MatTableDataSource<SkillModel>();
  public isFilterMainSkill: boolean = true;
  public showMainSkillClusterBox: boolean = true;
  public isFilteringSkillLessThanExpected: boolean = false;
  public searchedWord: string;
  
  private originalDataSource: SkillModel[] = [];
  private skillLevelList: SkillDetailLevelModel[] = [];
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  private mainSkillCluster: string[] = [];
  public _filterValue: string;
  private TECHNICAL_SKILLTYPE = DATA_CONFIG.TECHNICAL_SKILL_TYPE
  private BEHAVIORAL_SKILLTYPE = DATA_CONFIG.BEHAVIORAL_SKILL_TYPE
  public COMPETENCY_DEVELOPMENT = `/${CoreUrl.COMPETENCE_DEVELOPMENT}/self-skill-evaluation`;
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

  constructor(
    private matIconRegistry: MatIconRegistry,
    private sanitizer: DomSanitizer,
    private personalInfoService: PersonalInfoService,
    private personalInfomationService: PersonalInfomationService,
    private cd: ChangeDetectorRef,
    private loaderService: LoadingService,
    private dialogCommonService: DialogCommonService
  ) {
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.dataSource.paginator = this.paginator;
      this.cd.detectChanges();
      this.paginator.pageSize=DEFAULT_PAGE_SIZE;
      this.applyFilter()
      this.dataSource.sort = this.sort;
      this.sort?.sort({
        id: 'skill_group',
        start: 'asc',
        disableClear: false
      });
    }, 0);
  }

  ngOnInit(): void {
    this.personalInfomationService.getPersonalInfoDetail()
      .pipe(takeUntil(this.destroyed$))
      .subscribe(data => {
        if (data) {
          this.originalDataSource = Helpers.cloneDeep(data?.skills || []);
          this.mainSkillCluster = data?.['skill_cluster'] || [];
          this.dataSource = new MatTableDataSource(data.skills);
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
          this.filterType(this.TECHNICAL_SKILLTYPE);
          this.onMainSkillClusterChange(true);
        }
      })
    
      
  }

  applyFilter() {
    this.dataSource.filter = this.searchedWord;
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  filterLessThanExpected(event: any) {
    this.isFilteringSkillLessThanExpected=event
    this.arrFilter[2].value = event
    this.handleFilter()
   
  }

  getSkillDetail(skill: SkillModel) {
    this.skillLevelList = [];
    const loader = this.loaderService.showProgressBar();
    this.personalInfomationService.getSkillDetail(skill.skill_id)
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe(({ data }: any) => {
        this.skillLevelList = (data.skill_levels as Array<SkillDetailLevelModel>).sort((a: SkillDetailLevelModel, b: SkillDetailLevelModel) => a.name.localeCompare(b.name));
        const dialogRef = this.dialogCommonService.onOpenCommonDialog({
          component: SkillDetailDialogComponent,
          title: 'Skill Information',
          width: '80vw',
          height: 'auto',
          maxWdith: '800px',
          icon: 'a-icon ui-ic-alert-info',
          type: 'view',
          passingData: {
            skillTitle: skill.name,
            levelList: this.skillLevelList
          },
        });
      })
  }

  onMainSkillClusterChange(event: any) {
    this.isFilterMainSkill=event
    this.arrFilter[1].value = event
    this.handleFilter()
  }

  public oldFilter: boolean;
  filterType(e: any) {
    this.showMainSkillClusterBox = !this.showMainSkillClusterBox
    const valueToggle = e.data? e.data : e
    this.arrFilter[0].value = valueToggle
    this.arrFilter[3].value=''
    this._filterValue=''
    this.handleFilter()
  }

  buildColor_CurrentLevel(element: any){  
    return Helpers.buildColor_CurrentLevel(element.current_level, element.expected_level);
  }

  truncateText(text: string, num: number = 30) {
    return text?.length > num ? text.slice(0, num) + "..." : text
  }

  customParseFloat(value:string) {
    if (value.includes('.')) {
      return parseFloat(value);
    } else {
      return parseInt(value);
    }
  }

  handleFilter() {
    this.dataSource.data = this.originalDataSource.filter((skill: SkillModel) => {
      const c1 = skill.skill_type === this.arrFilter[0].value;
      const c2 = this.arrFilter[0].value === 'Technical' ? (!this.arrFilter[1].value || this.mainSkillCluster.includes(skill.skill_group)) : true;
      const c3 = !this.arrFilter[2].value || (skill.current_level < skill.expected_level);
      const c4 = !this.arrFilter[3].value || skill.name.toLowerCase().includes(this.arrFilter[3].value.toString().toLowerCase());
      
      return c1 && c2 && c3 && c4;
    });
    
    
    
    
  }
}
