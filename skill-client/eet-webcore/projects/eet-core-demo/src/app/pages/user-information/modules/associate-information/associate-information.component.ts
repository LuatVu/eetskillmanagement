import { PersonalInfomationService } from './../personal-information/personal-infomation.service';
import { PermisisonService } from './../../../../shared/services/permisison.service';
import { AfterViewInit, Component, OnInit, ViewChild, ChangeDetectorRef, ElementRef, QueryList } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatChip, MatChipInputEvent, MatChipListChange, MatChipSelectionChange } from '@angular/material/chips';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { TranslateService } from '@ngx-translate/core';
import { debug, group } from 'console';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { BehaviorSubject, ReplaySubject, Subject, finalize, map, startWith, takeUntil, switchMap, of } from 'rxjs';
import { ELASTIC_DOCUMENT } from '../../../../shared/constants/api.constants';
import { ElasticSearchModel } from '../../../../shared/services/elastic.service';
import { Helpers } from '../../../../shared/utils/helper';
import { UserInformationService } from '../../user-information.service';
import { CONFIG } from './../../../../shared/constants/config.constants';
import { ElasticService } from './../../../../shared/services/elastic.service';
import { AddNewAssociateComponent } from './add-new-associate/add-new-associate.component';
import { DATA_CONFIG } from './associate-info.constant';
import { AssociateModel, FilterDataModel, FilterModel, SkillGroup } from './associate-info.model';
import { AssociateInfoCopyService } from './associate-info.service';
import { EditAssociateComponent } from './edit-associate/edit-associate.component';
import { elements } from 'chart.js';
import { TOKEN_KEY } from '@core/src/lib/shared/util/system.constant';
import { PaginDirectionUtil } from 'projects/eet-core-demo/src/app/shared/utils/paginDirectionUtil';
import { Router } from '@angular/router';
import { Location } from '@angular/common'
import { PersonalInfomationModel } from '../personal-information/personal-infomation.model';
import { CanDeactivateGuard } from 'projects/eet-core-demo/src/app/shared/utils/can-deactivate.guard';
import { NotificationService } from '@bci-web-core/core';

@Component({
  selector: 'eet-associate-information',
  templateUrl: './associate-information.component.html',
  styleUrls: ['./associate-information.component.scss'],
})

export class AssociateInformationCopyComponent implements OnInit, AfterViewInit, CanDeactivateGuard {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort: MatSort = new MatSort();
  public forceUnselectAllTagSkillGroup: Subject<boolean> = new Subject<boolean>();
  public arrayFilter: FilterModel[] = DATA_CONFIG.ARRAY_FILTER;
  public arrayTagSkillGroup: string[];
  public arrayTagSkillGroupFilter: string[];
  public isSelectAllSkillGroup: boolean = false;
  public getAsso!: string;
  public dataSource: MatTableDataSource<AssociateModel> = new MatTableDataSource();
  public searchControl = new FormControl();
  public totalItems: number;
  public displayedColumns: string[] = DATA_CONFIG.DISPLAY_COLUMNS;
  public filterData: FilterDataModel;

  private originalData: AssociateModel[] = [];
  private elasticSearchModel: ElasticSearchModel;
  private exp: string[] = DATA_CONFIG.EXP_LIST;
  public readonly PAGINATION_SIZE_OPTIONS = CONFIG.PAGINATION_OPTIONS;
  private currentPaginator: MatPaginator;
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  public teamFilterControl = new FormControl([]);
  public currentViewAssociateMode: string = '';
  private checkOnDisplayDetailAssociate: boolean = false;
  public readonly pageSizeOptions = CONFIG.PAGINATION_OPTIONS;
  private currentTagIndexSelected: number = -1
  competencyOptionsList: string[];
  public permissions: Map<string, boolean>;
  public editAssociateInfoPermission: boolean = false;
  public viewAssociateInfoPermission: boolean = false;
  public directedFromProject: boolean = false;
  public getFilterEETDepartment: boolean = false;
  public personalInfomation: PersonalInfomationModel;

  private getAllFilterTeam$ = new BehaviorSubject<boolean>(false)
  constructor(
    private router: Router,
    private associateInfoService: AssociateInfoCopyService,
    private dialogCommonService: DialogCommonService,
    private translate: TranslateService,
    private loaderService: LoadingService,
    private userInformationService: UserInformationService,
    private elasticService: ElasticService,
    private cd: ChangeDetectorRef,
    private permisisonService: PermisisonService,
    private location: Location,
    private cdr: ChangeDetectorRef,
    private personalInfomationService: PersonalInfomationService,
    private notify: NotificationService
  ) {
    const state: any = location.getState();
    this.getAsso = state.id;
    this.currentViewAssociateMode = state.mode;
    this.getFilterEETDepartment = state.EET_DEPARTMENT


    this.elasticSearchModel = {
      from: 0,
      size: 10000,
      query: ""
    };
    this.totalItems = 0;
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;

    this.dataSource.sort = this.sort;
    this.sort?.sort({
      id: DATA_CONFIG.DISPLAY_COLUMNS[0],
      start: 'asc',
      disableClear: false
    });
    this.cdr.detectChanges()
    PaginDirectionUtil.expandTopForDropDownPagination()

    // this.getAllFilterTeam$.subscribe((res) => {
    //   if (res) {
    //     this.arrayFilter[0].selectedData = this.arrayFilter[0]?.originalData
    //     this.watchOnFilterChange('Team', this.arrayFilter[0]?.originalData)
    //   }
    // })

    // setTimeout(() => {
    //   this.watchOnFilterChange('Team', this.arrayFilter[0]?.originalData)
    // }, 0)
  }

  ngOnInit() {

    this.getCurrentUser();
    this.getPermission();
    this.getAllAssociate();
    this.getAllFilterData();
    this.watchOnSearchChange();
    this.getSkillGroup();
    this.userInformationService.getCurrentViewAssociateMode()
      .pipe(takeUntil(this.destroyed$)).subscribe(result => {
        this.currentViewAssociateMode = result.length > 0 ? result : this.currentViewAssociateMode;

        this.cd.detectChanges();
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;

        this.refreshFilter()
      })

    setTimeout(() => {
      this.getPersonalInformation()
    }, 0)
  }

  getAllAssociate(filterChange?: any, searchChange?: any) {
    const loader = this.loaderService.showProgressBar();
    this.elasticService.getDocument(ELASTIC_DOCUMENT.PERSONAL, this.elasticSearchModel)
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe(response => {
        this.originalData = JSON.parse(JSON.stringify(response.arrayItems));

        this.dataSource = new MatTableDataSource<AssociateModel>();
        this.dataSource.data = response.arrayItems || [];
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        this.totalItems = response.totalItems || 0;
        if(filterChange != undefined){
          filterChange();          
        }
        if(searchChange != undefined){
          searchChange();
        }
      });
  }

  getAllFilterData() {
    const loader = this.loaderService.showProgressBar();
    this.associateInfoService.getlistFilter()
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .pipe(map(data => {
        return {
          skillList: data?.['skill_filter'],
          skillGroupList: data?.['skill_group_filter'],
          teamList: data?.['team_filter'],
          expList: this.exp,
          levelList: data?.['level_filter']
        }
      }))
      .subscribe(response => {
        this.filterData = response;
        response.skillGroupList.sort();
        this.arrayFilter.map(elment => {
          elment.originalData = this.filterData?.[elment.key as keyof FilterDataModel] || [];
        })

        this.onSelectAllSkillGroup() // default tag selected is All

        this.getAllFilterTeam$.next(true)
      })
  }

  addNewAssociates() {
    const MIN_DIALOG_WIDTH = 700;
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: AddNewAssociateComponent,
      title: this.translate.instant('manage_associate.add_associate.title'),
      width: (MIN_DIALOG_WIDTH * 100 / CONFIG.TABLET_MIN_WIDTH).toString() + 'vw',
      icon: 'a-icon a-button__icon ui-ic-plus',
      maxWdith: '1170px',
      height: 'auto',
      type: 'edit',
      passingData: {}
    })
    dialogRef.afterClosed().subscribe(result => {
      setTimeout(() => {
        this.getAllAssociate();
      }, 1000);

    })
  }

  watchOnSearchChange() {
    this.searchControl.valueChanges
      .pipe(startWith(null))
      .subscribe((keySearch: string) => {
        this.dataSource.filter = keySearch?.toLowerCase();
        this.totalItems = this.dataSource.filteredData.length;
        if (this.dataSource.paginator) {
          this.dataSource.paginator.firstPage();
        }
      });
  }
  
  watchOnSearch = () => {
    this.dataSource.filter = this.searchControl.value?.toLowerCase();
    this.totalItems = this.dataSource.filteredData.length;
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  watchOnFilterChange = (type?: any, data?: any) =>  {
    if (Array.isArray(data) && type === 'Skill Group') { // when choose skill group, tag selected above -> unselected
      this.arrayTagSkillGroupFilter = []
      this.dataSource.filteredData = this.originalData
    }
    let tmpData: AssociateModel[] = Helpers.cloneDeep(this.originalData);
    this.arrayFilter.filter(f => f?.selectedData && f.selectedData?.length != 0).forEach(element => {
      switch (element?.key) {
        case 'teamList':
          tmpData = Helpers.cloneDeep(tmpData.filter(data => element.selectedData.includes(data.team)));
          break;
        case 'skillGroupList':
          tmpData = Helpers.cloneDeep(tmpData.filter(tmp =>
            (element.selectedData as Array<string>).find(x => {
              if (tmp.skillGroups) {
                return (tmp.skillGroups! as string[]).includes(x)
              } else {
                return false;
              }
            })))
          break;
        case 'skillList':
          tmpData = Helpers.cloneDeep(tmpData.filter(tmp => (element.selectedData as Array<string>).find(x => (tmp.skills as string[]).includes(x))))
          break;
        case 'expList':
          switch (element.selectedData) {
            case '< 5':
              tmpData = Helpers.cloneDeep(tmpData.filter(tmp => tmp?.experience < 5))
              break;
            case '5 - 10':
              tmpData = Helpers.cloneDeep(tmpData.filter(tmp => tmp.experience >= 5 && tmp.experience <= 10))
              break;
            case '> 10':
              tmpData = Helpers.cloneDeep(tmpData.filter(tmp => tmp?.experience && tmp.experience > 10))
              break;
            default:
              break;
          }
          break;
        case 'levelList':
          tmpData = Helpers.cloneDeep(tmpData.filter(tmp => (element.selectedData as Array<string>).find(x => tmp.level === x)))
          break;
        default:
          break;
      }
    });
    this.dataSource.data = Helpers.cloneDeep(tmpData);
    this.totalItems = this.dataSource.filteredData.length;

  }

  btnClick(id: string) {
    this.getAsso = id;
    if (this.getAsso) {
      this.userInformationService.getCurrentAssociateInModeView(this.getAsso)
    }
    this.checkOnDisplayDetailAssociate = true;
    this.userInformationService.setCurrentViewAssociateMode('VIEW_ASSOCIATE_PROFILE');
  }

  btnEditAssociate(data: any) {
    const MIN_DIALOG_WIDTH = 700;
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: EditAssociateComponent,
      title: this.translate.instant('manage_associate.edit_associate.title'),
      width: (MIN_DIALOG_WIDTH * 100 / CONFIG.TABLET_MIN_WIDTH).toString() + 'vw',
      icon: 'a-icon boschicon-bosch-ic-document-edit',
      maxWdith: '1170px',
      height: 'auto',
      type: 'edit',
      passingData: {
        associateInfo: data
      }
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const comLoader = this.loaderService.showProgressBar()
        // wait data from elastic is saved
        setTimeout(() => {
          this.loaderService.hideProgressBar(comLoader);          
          this.getAllAssociate(this.watchOnFilterChange, this.watchOnSearch);
          this.isSelectAllSkillGroup = true;
        }, 1000)
      }
    })
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
    this.dialogCommonService.toggleCloseAllDialog();
  }
  toggleSelected(chip: MatChip) {
    chip.toggleSelected();
  }
  getSkillGroup() {
    const loader = this.loaderService.showProgressBar();
    this.associateInfoService.getSkillGroupData()
      .pipe(
        map(value => {
          if (value.data) {
            value.data = value.data.filter((e: SkillGroup) => e.skill_type === DATA_CONFIG.TECHNICAL_SKILL_TYPE)
            value.data = this.listSkillFilterSortedByAssociateCount(value.data)
            return value.data.map((skillGroupList: { id: string, name: string }) => skillGroupList.name)
          }
        })
      )
      .pipe(finalize(() => {
        this.loaderService.hideProgressBar(loader)
      })).subscribe((response: any) => {
        this.arrayTagSkillGroup = response || [];
        this.arrayTagSkillGroupFilter = [];
      })
  }
  emptyArray(array: string[]) {
    array.splice(0, array.length);
  }
  onSelectAllSkillGroup() {
    const tmpArrFilter = Helpers.cloneDeep(this.arrayFilter)
    this.emitEventForceUnselectAllSkillTag()
    if (!this.isSelectAllSkillGroup) {
      this.currentTagIndexSelected = -1
    }
    this.arrayFilter.find((item, index) => {
      if (item.key === 'skillGroupList') {
        this.arrayTagSkillGroupFilter = item.selectedData as Array<string>
      } else {
        item.selectedData = Helpers.cloneDeep(tmpArrFilter[index].selectedData || [])
      }
    })
    this.emptyArray(this.arrayTagSkillGroupFilter)
    this.isSelectAllSkillGroup = true
    this.emptyArray(this.arrayTagSkillGroupFilter)   // reset = [] when change tab
  }
  onFilterSkillGroup(index: number) {
    const tmpTagSkillGroupFilter = Helpers.cloneDeep(this.arrayTagSkillGroupFilter)
    const tmpArrFilter = Helpers.cloneDeep(this.arrayFilter)
    this.emitEventForceUnselectAllSkillTag()
    this.arrayTagSkillGroupFilter = Helpers.cloneDeep(tmpTagSkillGroupFilter)
    this.isSelectAllSkillGroup = false;
    this.arrayFilter.find((item, index) => {
      if (item.key === 'skillGroupList') {
        this.arrayTagSkillGroupFilter = item.selectedData as Array<string>
      } else {
        item.selectedData = Helpers.cloneDeep(tmpArrFilter[index].selectedData || [])
      }
    })
    if (!tmpTagSkillGroupFilter.includes(this.arrayTagSkillGroup[index])) {
      this.emptyArray(this.arrayTagSkillGroupFilter);
      this.arrayTagSkillGroupFilter.push(this.arrayTagSkillGroup[index]);
    } else {
      this.emptyArray(this.arrayTagSkillGroupFilter);
      this.isSelectAllSkillGroup = true
    }
    this.currentTagIndexSelected = index
    this.watchOnFilterChange();
  }

  exportExcel() {
    const loader = this.loaderService.showProgressBar();
    this.associateInfoService.exportExcelFile(this.dataSource.filteredData.map(item => item.personalId))
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe((data) => {
        if (data) {
          const FileSaver = require('file-saver');
          const blob = new Blob([data], {
            type: 'application/octet-stream',
          });
          FileSaver.saveAs(blob, 'associateList.xlsx');
        }
      });
  }

  refreshFilter() {
    this.arrayFilter.map((e) => {
      e.selectedData = []
    })
    this.emitEventForceUnselectAllSkillTag()
    // this.getPersonalInformation();
  }
  emitEventForceUnselectAllSkillTag() {
    this.forceUnselectAllTagSkillGroup.next(true)
  }
  isSelfInformation(user: any) {
    return this.currentUser?.id === user?.id;
  }
  public currentUser: any;
  getCurrentUser() {
    this.currentUser = JSON.parse(localStorage.getItem('Authorization') || '{}');
  }
  getPermission() {
    this.editAssociateInfoPermission = this.permisisonService.hasPermission(CONFIG.PERMISSIONS.EDIT_ASSOCIATE_INFO_PERMISSION);
    this.viewAssociateInfoPermission = this.permisisonService.hasPermission(CONFIG.PERMISSIONS.VIEW_ASSOCIATE_INFO_PERMISSION);
  }
  listSkillFilterSortedByAssociateCount(skillGroupList: any[]) {
    if (skillGroupList.length > 0) {
      skillGroupList.sort((a: SkillGroup, b: SkillGroup) => {
        if (b.number_associate !== a.number_associate) {
          return b.number_associate - a.number_associate;
        } else if (a.number_associate === b.number_associate) {
          return a.name.localeCompare(b.name);
        } else {
          return 0;
        }
      });
    }

    return skillGroupList
  }
  getPersonalInformation() {
    if (this.getFilterEETDepartment) return;
    const loader = this.loaderService.showProgressBar();
    this.personalInfomationService.getPersonalInfoDetail()
      .pipe(
        takeUntil(this.destroyed$),
        switchMap((data) => {
          if (Object.keys(data).length === 0 && this.currentUser) {
            return this.personalInfomationService.getPersonInfo(this.currentUser.id);
          }
          this.personalInfomation = data as PersonalInfomationModel;
          return of(false);
        }),
      )
      .subscribe(data => {
        this.loaderService.hideProgressBar(loader);
        if (data) {
          this.personalInfomation = data;
        }
        this.arrayFilter[0].selectedData = [this.personalInfomation.team]
        this.watchOnFilterChange('Team', [this.personalInfomation.team]);
      })
  }

  canDeactivate(): boolean | Promise<boolean> | import('rxjs').Observable<boolean> {
    if (this.userInformationService.getEditMode()) {
      this.userInformationService.setOldIndex(1)
      const confirmation = confirm('This page is asking you to confirm that you want to leave — information you\'ve entered may not be saved.');
      if (confirmation == true) this.userInformationService.setEditMode(false);
      this.userInformationService.setConfirm(confirmation);
      return confirmation;
    } else {
      return true;
    }
  }
  deleteAssociate(associate: AssociateModel) {
    const dialogRef = this.dialogCommonService.onOpenConfirm({
      content:
        `${this.translate.instant('manage_associate.delete_associate.content')}
       ${associate.personalCode} 
       ${this.translate.instant('manage_associate.delete_associate.additional')}
       ${this.translate.instant('manage_associate.delete_associate.cannot_revert')}`,
      title: this.translate.instant('manage_associate.delete_associate.title'),
      btnConfirm: this.translate.instant('projects.delete_prompt.yes'),
      btnCancel: this.translate.instant('projects.delete_prompt.no'),
      isShowOKButton: false,
      icon: 'a-icon ui-ic-alert-warning'
    })
    dialogRef.afterClosed().subscribe(response => {
      if (response) {
        const loader = this.loaderService.showProgressBar();
        this.associateInfoService.deleteAssociate(associate.id)
          .pipe(takeUntil(this.destroyed$), finalize(() => this.loaderService.hideProgressBar(loader)))
          .subscribe(data => {
            if (data.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
              this.notify.success(this.translate.instant('manage_associate.delete_associate.success'));
              this.getAllAssociate(this.watchOnFilterChange, this.watchOnSearch);
            } else {
              this.notify.error(this.translate.instant('manage_associate.delete_associate.failed'))
            }
          })
      }
    })

  }
}
