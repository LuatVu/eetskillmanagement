import { AfterViewInit, ChangeDetectorRef, Component, HostListener, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { NotificationService } from '@bci-web-core/core';
import { TOKEN_KEY } from '@core/src/lib/shared/util/system.constant';
import { TranslateService } from '@ngx-translate/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { ElasticSearchModel, ElasticService } from 'projects/eet-core-demo/src/app/shared/services/elastic.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { Observable, Subscription, filter, finalize } from 'rxjs';
import { delay, switchMap } from 'rxjs/operators';
import { SkillDetailDialogComponent } from '../../../../career-development/components/skill-evaluation/skill-detail-dialog/skill-detail-dialog.component';
import { Competency } from '../../../model/competency-lead/competency-lead.model';
import { ExpectedLevelModel, ExpectedSkillLevelModel, ExpectedSkillUpdateModel, FilterModel, LevelModel, SkillGroupModel } from '../../../model/expect-skills-level-for-associate/expect-skill-level-for-associate.model';
import { CompetencyLeadService } from '../../../services/competency-lead.service';
import { ManageSkillService } from '../manage-skill/manage-skill.service';
import { ExpectSkillsLevelForAssociateService } from './expect-skills-level-for-associate.service';
import { EetToggleModel } from './expect-skill-level-for-associate.model';
import { PaginDirectionUtil } from 'projects/eet-core-demo/src/app/shared/utils/paginDirectionUtil';
import { NavigationEnd, NavigationStart, Router } from '@angular/router';
import { EventEmitter } from '@angular/core';
import { elements } from 'chart.js';
import { throws } from 'assert';
import { ActivatedRoute } from '@angular/router';
import {Location} from '@angular/common';
import { stringToArray } from '@ag-grid-community/core';
import { CanComponentDeactivate, CanDeactivateGuard } from './can-deactivate.guard';
import { CareerDevelopmentService } from '../../../../career-development/career-development.service';
import { ToggleDataEmitModel } from 'projects/eet-core-demo/src/app/shared/components/toggle/model';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';

@Component({
  selector: 'eet-expect-skills-level-for-associate',
  templateUrl: './expect-skills-level-for-associate.component.html',
  styleUrls: ['./expect-skills-level-for-associate.component.scss']
})
export class ExpectSkillsLevelForAssociateComponent implements OnInit, AfterViewInit, CanDeactivateGuard {
  public readonly CONST_ORIGIN : string = "Origin";
  public listExpectSkillUpdate: ExpectedSkillUpdateModel[] = []
  public skillGroupData: FilterModel = {
    isMultiple: true,
    key: 'skillgroup',
    originalData: [],
    selectedData: [],
    name: 'skill'
  };
  readonly DEFAULT_PAGINATION_PAGE = 0;
  readonly DEFAULT_PAGINATION_SIZE = 5;
  private readonly LEVEL_DEFAULT_DATA=['L50', 'L51', 'L52', 'L53'];
  public readonly PAGINATION_SIZE_OPTIONS = CONFIG.PAGINATION_OPTIONS;
  public listExpectedSkillLevelValue: any;
  public readonly technicalListExpectedSkillLevelValue = CONFIG.TECHNICAL_EXPECTED_SKILL_LEVEL_VALUE_OPTIONS;
  public readonly behavioralListExpectedSkillLevelValue = CONFIG.BEHAVIORAL_EXPECTED_SKILL_LEVEL_VALUE_OPTIONS;
  public levelExpectSkillData: ExpectedSkillLevelModel[] = [];
  public levelData: string[] = this.LEVEL_DEFAULT_DATA;
  public levelRawData: Map<string, string> = new Map<string, string>();
  public displayedColumns = ["nameSkill"];
  public dataSource = new MatTableDataSource<ExpectedSkillLevelModel>();
  public totalItems: number;
  private elasticSearchModel: ElasticSearchModel;
  public selectedSkillGroup: string = '';
  public selectedSkillType: string = this.translate.instant('system.expect_skill_level_for_associate.switch_skill');
  public skillTypeList: {[key: string]: string} = CONFIG.EXPECTED_SKILL_LEVEL.SKILL_TYPE;
  public editMode: boolean = false;
  public showEditButton: boolean = false;
  public defaultSelect: any;
  public toggleChecked: boolean = false;
  public routerSubcription: Subscription;
  public param: string;
  public firstSelect: boolean = true;
  public editted: boolean = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @HostListener('window:beforeunload', ['$event'])
  onWindowClose(event: any): void {
    if (this.editMode) {
      event.preventDefault();
      event.returnValue = false;
    }
  }
  constructor(
    public dialog: DialogCommonService,
    public translate: TranslateService,
    public deleteDialog: MatDialog,
    public manageSkillService: ManageSkillService,
    public expectSkillsLevelForAssociateService: ExpectSkillsLevelForAssociateService,
    private elasticService: ElasticService,
    private notify: NotificationService,
    private loaderService: LoadingService,
    private changeDetectorRef: ChangeDetectorRef,
    private competencyLeadService: CompetencyLeadService,
    private router: Router,
    private route: ActivatedRoute,
    private location: Location,
    private careerDevelopmentService:CareerDevelopmentService
  ) {
    this.totalItems = 0;
    this.elasticSearchModel = {
      from: 0,
      size: 10000,
      query: ""
    };

    
  }

  canEdit: boolean = false;

  ngOnInit(): void {
    this.getSkillLevelList();
    this.getSkillGroupList(this.toggleChecked);

    const listOfPermissions = JSON.parse(localStorage.getItem(TOKEN_KEY) || '{}')['permissions']
    this.canEdit = !listOfPermissions.some((obj: any) => obj.code == 'EDIT_EXPECTED_SKILL_LEVEL');
  
    if (this.router.url.includes(CoreUrl.COMPETENCE_DEVELOPMENT)) this.showEditButton = true;
    else {this.editMode = true;
    this.showEditButton = false}

    
    
    this.routerSubcription = this.route.queryParams.subscribe(params => {
      this.param = params['param'];
    });


  



  }

  ngAfterViewInit() {
    PaginDirectionUtil.expandTopForDropDownPagination();
    if (this.param == 'behav') this.toggleChecked = true; 
    else if (this.param == 'tech') this.toggleChecked = false;
    else if (this.expectSkillsLevelForAssociateService.selectedSkillType?.length > 0) {
      this.toggleChecked = this.expectSkillsLevelForAssociateService.selectedSkillType === 'behav' ? true : false;
    }

    if (this.expectSkillsLevelForAssociateService.selectedSkill) {
      this.getExpectedSkillListFilter(this.expectSkillsLevelForAssociateService.selectedSkill);
    }

    if (this.expectSkillsLevelForAssociateService.selectedListElement) {
      this.defaultSelect = this.expectSkillsLevelForAssociateService.selectedListElement;
    }

    this.listExpectedSkillLevelValue = this.toggleChecked ? this.behavioralListExpectedSkillLevelValue : this.technicalListExpectedSkillLevelValue;
  }

  back() {
    this.location.back();
  }

  isExpectLevelChanged(skillId: string){
    const skill : ExpectedSkillLevelModel | null = this.dataSource.data.find(skill => skill.idSkill === skillId) || null;
    if(skill === null) return false;
    for(let level of this.levelData){
      if(skill[level] !== skill[level+ this.CONST_ORIGIN])  
        return true;
    }
    return false;
  }
  onChangeExpectLevel(level: string , element: ExpectedSkillLevelModel) {
    if(!this.isExpectLevelChanged(element.idSkill)){
      this.listExpectSkillUpdate =  this.listExpectSkillUpdate
        .filter(skill => skill.idSkill !== element.idSkill);
      return;
    }
    element.levelExpecteds.forEach((levelExpected: ExpectedLevelModel) => {
      levelExpected.nameLevel === level? levelExpected.value = Number.parseFloat(element[level].toString()): '';
    })
    var isSkillExist = this.listExpectSkillUpdate.find(item => item.idSkill === element.idSkill);
    if (!isSkillExist) {
      var expectSkillModel: ExpectedSkillUpdateModel = {
        idSkill: element.idSkill,
        levelExpecteds: element.levelExpecteds
      }
      this.listExpectSkillUpdate.push(expectSkillModel);
      this.editted = true;
      this.careerDevelopmentService.setEditted(this.editted);
    } else {
      isSkillExist.levelExpecteds = element.levelExpecteds;
    }
  }
  resetTableInformation(isUpdate?: boolean) {

    if (this.editted == false) {
      this.listExpectSkillUpdate = [];
      this.skillGroupData.selectedData = [];
      this.getExpectedSkillList(this.selectedSkillGroup? this.selectedSkillGroup: undefined, isUpdate);
      this.editMode = false;
      this.editted = false;
      this.firstSelect = true;
    } else if (this.editted == true) {
      const ref = this.dialog.onOpenConfirm({
        content:'Cancel will undo all changes made in current skill, press \'Yes\' to confirm',
        title:'',
      });
      ref.afterClosed().subscribe( response => {
        if (response) {
          this.editted = false;
          this.listExpectSkillUpdate = [];
          this.skillGroupData.selectedData = [];
          this.getExpectedSkillList(this.selectedSkillGroup? this.selectedSkillGroup: undefined, isUpdate);
          this.editMode = false;
          this.editted = false;
          this.firstSelect = true;
          this.careerDevelopmentService.setConfirm(true);
        }
      } 
      )
    }
  }
  resetPagination(){
    this.paginator.firstPage();
    this.paginator._changePageSize(this.DEFAULT_PAGINATION_SIZE);
  }
  onViewDetail(skillId: string) {
    let loader = this.loaderService.showProgressBar();
    this.expectSkillsLevelForAssociateService.getDetailSkillData(skillId)
      .pipe(finalize(() => { this.loaderService.hideProgressBar(loader) }))
      .subscribe((response: any) => {
        if (response && response.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          const dialogRef = this.dialog.onOpenCommonDialog({
            component: SkillDetailDialogComponent,
            title: this.translate.instant('manage_request.dialog.skill_information'),
            width: '80vw',
            icon: 'a-icon ui-ic-alert-info',
            height: 'auto',
            maxWdith: '800px',
            type: 'view',
            passingData: {
              skillTitle: response.data.skill_name,
              levelList: response.data.skill_levels.sort((a: any, b: any) => a.name > b.name ? 1 : -1)
            },

          });
          dialogRef.afterClosed();
        }
      })
  }
  //on change skill cluster
  watchOnFilterChange() {
    if (this.skillGroupData.selectedData.length > 0) {
      let tmpData = (this.skillGroupData.selectedData as Array<string>).join(',');
      this.getExpectedSkillListFilter(tmpData);
      return;
    }
    this.getExpectedSkillListFilter(undefined);
  }
  saveChange() {
    let loader = this.loaderService.showProgressBar();
    this.expectSkillsLevelForAssociateService.putExpectSkillUpdate(this.listExpectSkillUpdate)
      .pipe(finalize(() => { this.loaderService.hideProgressBar(loader) }))
      .subscribe((response) => {
        this.listExpectSkillUpdate = [];
        if (response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.skillGroupData.selectedData = [];
          this.listExpectSkillUpdate = [];
          this.skillGroupData.selectedData = [];
          this.getExpectedSkillList(this.selectedSkillGroup? this.selectedSkillGroup: undefined, true);
          this.editMode = false;
          this.editted = false;
          this.firstSelect = true;
        }
      });
      this.editMode = false;
      this.careerDevelopmentService.setConfirm(true);
  }

  changeToEditMode() {
    this.editMode = true;
  }

  getSkillLevelList() {
    const loader = this.loaderService.showProgressBar();
    this.expectSkillsLevelForAssociateService.getAllLevel()
      .pipe(finalize(() => { this.loaderService.hideProgressBar(loader) }))
      .subscribe(response => {
        if (response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          response.data.forEach((level: LevelModel) => this.levelRawData.set(level.id, level.name));
          // this.levelData = response.data.map((level: LevelModel) => level.name);
          // this.levelData.sort(function (lv1: string, lv2: string) { return lv1.localeCompare(lv2) });
          this.levelData.forEach(level => {
            this.displayedColumns.push(level);
          });
        }
      });
  }
  //init expected skill list
  getExpectedSkillList(skillCluster?: string, isUpdate?: boolean) {
    const page = this.paginator === undefined ? this.DEFAULT_PAGINATION_PAGE : this.paginator.pageIndex;
    const size = this.paginator === undefined ? this.DEFAULT_PAGINATION_SIZE : this.paginator.pageSize;
    const loader = this.loaderService.showProgressBar();
    this.expectSkillsLevelForAssociateService.getSkillData(page, size)
    .pipe(
      switchMap(response =>{
        if(response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS){
          this.totalItems = response.data.totalItem;
        }
        return this.expectSkillsLevelForAssociateService.getSkillData(this.DEFAULT_PAGINATION_PAGE, this.totalItems, skillCluster);
      }),
      delay(0),
      finalize(()=>{
        if (isUpdate) this.notify.success(this.translate.instant('system.expect_skill_level_for_associate.notification.save_change_success'));
        this.resetPagination();
        this.loaderService.hideProgressBar(loader);
      })
    )
      .subscribe(response => {
        if (response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.levelExpectSkillData = response.data.skills;
          this.levelExpectSkillData.forEach(skill => {
            skill.levelExpecteds.forEach((level: ExpectedLevelModel) => {
              skill[level.nameLevel] = level.value;
              skill[level.nameLevel + this.CONST_ORIGIN] = level.value;
            });
          });
          this.dataSource = new MatTableDataSource<ExpectedSkillLevelModel>(this.levelExpectSkillData);
          this.dataSource.sort = this.sort;
          this.dataSource.paginator = this.paginator;
          this.totalItems = response.data.totalItem;
        }
      });
  }
  ////filter expected skill list
  getExpectedSkillListFilter(skillCluster?: string) {
    const page = this.paginator === undefined ? this.DEFAULT_PAGINATION_PAGE : this.paginator.pageIndex;
    const size = this.paginator === undefined ? this.DEFAULT_PAGINATION_SIZE : this.paginator.pageSize;
    const loader = this.loaderService.showProgressBar();
    this.expectSkillsLevelForAssociateService.getSkillData(page, size)
    .pipe(
      switchMap(response =>{
        if(response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS){
          this.totalItems = response.data.totalItem;
        }
        return this.expectSkillsLevelForAssociateService.getSkillData(page, this.totalItems, skillCluster);
      }),
      delay(0),
      finalize(()=>{
        this.loaderService.hideProgressBar(loader);
        this.changeDetectorRef.detectChanges();
      })
    )
      .subscribe(response => {
        if (response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.levelExpectSkillData = response.data.skills;
          this.levelExpectSkillData.forEach(skill => {
            skill.levelExpecteds.forEach((level: ExpectedLevelModel) => {
              skill[level.nameLevel] = level.value;
              skill[level.nameLevel + this.CONST_ORIGIN] = level.value;
            });
          });
          this.dataSource = new MatTableDataSource<ExpectedSkillLevelModel>(this.levelExpectSkillData);
          this.dataSource.data.forEach(skill =>{
            this.listExpectSkillUpdate.forEach(updateSkill =>{
              if(skill.idSkill === updateSkill.idSkill){
                updateSkill.levelExpecteds.forEach(levelExpected =>{
                  const lvName = this.levelRawData.get(levelExpected.idLevel);
                  if(!lvName) return;
                  skill[lvName.toString()] = levelExpected.value;
                })
              }
            });
          });
          this.dataSource.sort = this.sort;
          this.dataSource.paginator = this.paginator;
          this.totalItems = response.data.totalItem;
        }
      });
  }

  getSkillGroupClusterList() {
    const loader = this.loaderService.showProgressBar();
    this.expectSkillsLevelForAssociateService.getSkillGroupData()
      .pipe(finalize(() => {
        this.loaderService.hideProgressBar(loader)
      }))
      .subscribe((response) => {
        if (response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.skillGroupData.originalData = (response?.data as SkillGroupModel[]).map(skill => skill.name);
        }
      })
  }

  isElementOverflow(element : any){
    return element.offsetWidth < element.scrollWidth;
  }

  ngOnDestroy(): void {
    this.routerSubcription.unsubscribe;


  }

  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  onSelectCompetency($event: any){
    if (this.editted == false) {
      this.selectedSkillGroup = $event.name;
      this.getExpectedSkillListFilter($event.name);
      this.defaultSelect = $event;
    } else if (this.editted == true) {
      const ref = this.dialog.onOpenConfirm({
        content:'Changing to another skill will undo all changes made in current skill, press \'Yes\' to confirm',
        title:'',
      });
      ref.afterClosed().subscribe( response => {
        if (response) {
          this.editted = false;
          this.selectedSkillGroup = $event.name;
          this.getExpectedSkillListFilter($event.name);
          this.defaultSelect = $event;
          this.listExpectSkillUpdate = [];
          this.skillGroupData.selectedData = [];
          this.getExpectedSkillList(this.selectedSkillGroup? this.selectedSkillGroup: undefined, false);
        }
      } 
      )
    }
  }

  getSkillGroupList(checked: boolean) {

    const loader = this.loaderService.showProgressBar();
    let filteredData: any[];
    this.competencyLeadService.getCompetency().pipe(finalize(() => {
      this.loaderService.hideProgressBar(loader);
    })).subscribe((response) => {
      if (checked) {
        filteredData = response.data.filter((item: any) => item.skill_type == "Behavioral");
      } else {
        filteredData = response.data.filter((item: any) => item.skill_type == "Technical");
      }
      this.skillGroupData.originalData = filteredData.sort((a, b) => {
        const x = a.name.toLowerCase().trim();
        const y = b.name.toLowerCase().trim();
        return x > y ? 1 : x < y ? -1 : 0;
      })
      if(this.skillGroupData.originalData[0]){
        this.defaultSelect = this.skillGroupData.originalData[0];
        this.onSelectCompetency(this.defaultSelect);
      }
    })
  }

  onSkillTypeChange($event: any){
    const isChecked = $event?.target?.checked;
    this.selectedSkillType = isChecked? this.skillTypeList.TECHNICAL_SKILL: this.skillTypeList.BEHAVIORAL_SKILL;
  }

  getSkillGroupBySkillType(skillType: string){
    this.listExpectSkillUpdate = [];
    this.levelExpectSkillData = [];
    this.dataSource = new MatTableDataSource<ExpectedSkillLevelModel>(this.levelExpectSkillData);
    if(this.paginator){
      this.dataSource.paginator = this.paginator;
      this.resetPagination();
    }
    //call api get skill
  }

  isInit: boolean = true;

  toggleChange($event: ToggleDataEmitModel){
    if (this.isInit) {
      this.isInit = false;
    } else {
      this.getSkillGroupList($event.checked);
    }
    this.dataSource = new MatTableDataSource<ExpectedSkillLevelModel>();
    this.dataSource.paginator = this.paginator;
    this.defaultSelect = null;

    this.listExpectSkillUpdate = [];
    this.skillGroupData.selectedData = [];

    this.listExpectedSkillLevelValue = $event.checked ? this.behavioralListExpectedSkillLevelValue : this.technicalListExpectedSkillLevelValue;
  }


  public firstTime: boolean = true;

  canDeactivate(): boolean | Promise<boolean> | import('rxjs').Observable<boolean> {
  if(this.editted) {
    this.careerDevelopmentService.setOldPageIndex(0);
      if(this.firstTime) {
          const confirmation = confirm('This page is asking you to confirm that you want to leave — information you\'ve entered may not be saved.');
          this.careerDevelopmentService.setConfirm(confirmation);
          if(confirmation) this.firstTime=true; else this.firstTime = false;
          return confirmation;
      } else {
        this.firstTime = true;
        this.careerDevelopmentService.setConfirm(false);
        return false;
      }
    } else {
        this.careerDevelopmentService.setConfirm(true);
        return true;
    }
  }

}


