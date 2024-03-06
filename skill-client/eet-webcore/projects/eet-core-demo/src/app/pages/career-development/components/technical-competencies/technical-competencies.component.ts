import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { NotificationService } from '@bci-web-core/core';
import { TranslateService } from '@ngx-translate/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { ElasticSearchModel, ElasticService } from 'projects/eet-core-demo/src/app/shared/services/elastic.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { finalize, switchMap, delay } from 'rxjs/operators';
import { LevelModel } from '../../../system/model/expect-skills-level-for-associate/expect-skill-level-for-associate.model';
import { SkillDetailDialogComponent } from '../skill-evaluation/skill-detail-dialog/skill-detail-dialog.component';
import { FilterModel } from '../../../supply-demand/supply.model';
import { ExpectSkillsLevelForAssociateService } from '../../../system/components/pages/expect-skills-level-for-associate/expect-skills-level-for-associate.service';
import { ManageSkillService } from '../../../system/components/pages/manage-skill/manage-skill.service';
import { ExpectedSkillUpdateModel, ExpectedSkillLevelModel, ExpectedLevelModel, SkillGroupModel } from '../../../system/model/expect-skills-level-for-associate/expect-skill-level-for-associate.model';
import { ActivatedRoute } from '@angular/router';
import { CareerDevelopmentService } from '../../career-development.service'
import { Subscription } from 'rxjs';

@Component({
  selector: 'eet-technical-competencies',
  templateUrl: './technical-competencies.component.html',
  styleUrls: ['./technical-competencies.component.scss']
})
export class TechnicalCompetenciesComponent implements OnInit {

  public readonly CONST_ORIGIN : string = "Origin";
  public listExpectSkillUpdate: ExpectedSkillUpdateModel[] = []
  public skillGroupData: FilterModel = {
    isMultiple: true,
    key: 'skillgroup',
    originalData: [],
    selectedData: [],
    name: 'skill'
  };
  isFilterTechnicalSkill: boolean = true;
  readonly DEFAULT_PAGINATION_PAGE = 0;
  readonly DEFAULT_PAGINATION_SIZE = 5;
  public readonly PAGINATION_SIZE_OPTIONS = CONFIG.PAGINATION_OPTIONS;
  public readonly listExpectedSkillLevelValue = CONFIG.EXPECTED_SKILL_LEVEL_VALUE_OPTIONS;
  public levelExpectSkillData: ExpectedSkillLevelModel[] = [];
  public levelData: string[];
  public levelRawData: Map<string, string> = new Map<string, string>();;
  public displayedColumns = ["nameSkill"];
  public dataSource = new MatTableDataSource<ExpectedSkillLevelModel>();
  public totalItems: number;
  // isFilter: ElementRef;
  private elasticSearchModel: ElasticSearchModel;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
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
    private service: CareerDevelopmentService,
    public isFilter: ElementRef
  ) {

    this.totalItems = 0;
    this.elasticSearchModel = {
      from: 0,
      size: 10000,
      query: ""
    };
  }

  private subscription: Subscription;

  ngOnInit(): void {
    // this.getSkillLevelList();
    // this.getSkillGroupList();
    // this.getExpectedSkillList(undefined);
  }

  ngOnDestroy(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngAfterViewInit() {
    this.isFilterTechnicalSkill = this.service.techComp;
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
      levelExpected.nameLevel === level? levelExpected.value = Number.parseInt(element[level].toString()): '';
    })
    var isSkillExist = this.listExpectSkillUpdate.find(item => item.idSkill === element.idSkill);
    if (!isSkillExist) {
      var expectSkillModel: ExpectedSkillUpdateModel = {
        idSkill: element.idSkill,
        levelExpecteds: element.levelExpecteds
      }
      this.listExpectSkillUpdate.push(expectSkillModel);
    } else {
      isSkillExist.levelExpecteds = element.levelExpecteds;
    }
  }
  resetTableInformation(isUpdate?: boolean) {
    this.listExpectSkillUpdate = [];
    this.skillGroupData.selectedData = [];
    this.getExpectedSkillList(undefined, isUpdate);
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

  getSkillLevelList() {
    const loader = this.loaderService.showProgressBar();
    this.expectSkillsLevelForAssociateService.getAllLevel()
      .pipe(finalize(() => { this.loaderService.hideProgressBar(loader) }))
      .subscribe(response => {
        if (response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          response.data.forEach((level: LevelModel) => this.levelRawData.set(level.id, level.name));
          this.levelData = response.data.map((level: LevelModel) => level.name);
          this.levelData.sort(function (lv1: string, lv2: string) { return lv1.localeCompare(lv2) });
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

  getSkillGroupList() {
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

  onSearch(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  edit() {

  }

  onTypeChange($event: any) {
    // console.log($event);
    // console.log(this.isFilterTechnicalSkill);
  }

}
