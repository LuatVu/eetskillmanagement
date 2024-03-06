import { D, V } from '@angular/cdk/keycodes';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { AfterViewInit, AfterViewChecked, ChangeDetectorRef, Component, ElementRef, HostListener, Inject, NgModule, OnInit, SimpleChange, SimpleChanges, ViewChild, OnDestroy } from '@angular/core';
import { waitForAsync } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { SELECT_PANEL_INDENT_PADDING_X } from '@angular/material/select/select';
import { NavigationStart, Router } from '@angular/router';
import { TOKEN_KEY } from '@core/src/lib/shared/util/system.constant';
import { TranslateService } from '@ngx-translate/core';
import { Subscription, debounceTime, finalize } from 'rxjs';
import { ConfirmDialogComponent } from '../../../shared/components/dialogs/confirm-dialog/confirm-dialog.component';
import { CONFIG } from '../../../shared/constants/config.constants';
import { DialogCommonService } from '../../../shared/services/dialog-common.service';
import { ElasticService } from '../../../shared/services/elastic.service';
import { LoadingService } from '../../../shared/services/loading.service';
import { NotificationService } from '../../../shared/services/notification.service';
import { Helpers } from '../../../shared/utils/helper';
import * as DEFAULT_PARAMS from '../constants/projects-default-parameters.constants';
import { OrderBy } from '../models/order-by.constants';
import { CustomerUpdateEventModel, FilterModel, FilterProjectModel, ProjectScope, ProjectTabModel, ProjectType, gb, orderBy, projectBasicInfo } from '../models/projects.model';
import { ProjectsService } from '../services/projects.service';
import { BoschProjectDetailComponent } from './bosch-project-detail/bosch-project-detail.component';
import { EditNonBoschProjectDetailComponent } from './non-bosch-project-detail/edit-non-bosch-project-detail.component';
import { NonBoschProjectDetailComponent } from './non-bosch-project-detail/non-bosch-project-detail.component';
import { UploadProjectsComponent } from './upload-projects/upload-projects.component';
import { BaseResponseModel } from '../../../shared/models/base.model';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { ToggleDataEmitModel } from '../../../shared/components/toggle/model';




// List of permissions
@Component({
  selector: 'eet-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.scss'],
})
export class ProjectsComponent implements OnInit, AfterViewInit, AfterViewChecked, OnDestroy {
  public CUSTOMER_GB = DEFAULT_PARAMS.CUSTOMER_GB
  public PROJECT_SCOPE = DEFAULT_PARAMS.PROJECT_SCOPE
  public GB_UNIT = DEFAULT_PARAMS.GB_UNIT
  public PROJECT_STATUS = DEFAULT_PARAMS.PROJECT_STATUS
  public PROJECT_TYPE = DEFAULT_PARAMS.PROJECT_TYPE

  public _gbPlaceholder = "Customer GB";
  public _statusPlaceholder = "Project Status";
  public _typePlaceholder = "Project Type";
  public _vmodel = "v-model";
  public _project_list = "project-list";
  public _customer_gb = "customer-gb";
  public _projects = 'projects';
  public _project_portfolio = 'project-portfolio';
  public _customer_portfolio = 'customer-portfolio';

  public projectTabList: ProjectTabModel[] = DEFAULT_PARAMS.PROJECT_CONFIG.PROJECT_TAB_LIST;
  public selectedProjectTabIndex: number = 1;
  public isTopSelected: boolean = false;


  public listFilter: FilterProjectModel[] = [];
  public selectedTabIndex: number = -1;

  public readonly LIST_DATA_TOOGLE: string[] = ["All Projects", "Top Projects"]
  public projectScopeList: ProjectScope[] = []
  tagFilter: FilterProjectModel[];
  allTag: FilterProjectModel = {
    name: "All",
    order: -999,
    isSelected: true,
    projectCount: 0
  }

  private controlsBtnSkillTag: any

  getTagFilter() {
    let comLoader = this.loader.showProgressBar()
    this.projectsService.getFilterTagList().pipe(finalize(() => {
      this.loader.hideProgressBar(comLoader)
    })).subscribe(response => {
      response.data = response.data.map((e: FilterProjectModel) => {
        return {
          ...e,
          projectCount: e.projectCount
        }
      })
      this.listFilter = response.data;
      this.listFilter.unshift(this.allTag); // must unshift all_tags when sort
      this.projectsService.setTagList(this.listFilter);

      this.onFilterClick(0)
    })
  }

  onFilterClick(index: number) {
    if (index === 0) {
      this.setSelectAllTag()
    } else {
      this.listFilter[index].isSelected = !this.listFilter[index].isSelected;
      this.listFilter.forEach(tag => {
        if (tag !== this.listFilter[index])
          tag.isSelected = false;
      })
      if (!this.listFilter[index].isSelected) {
        this.setSelectAllTag()
      }
    }
    this.projectsService.setTagList(this.listFilter);

    this.projectsService.tagChangeEvent.emit();
  }

  setSelectAllTag() {
    this.listFilter.forEach(tag => {
      if (tag !== this.listFilter[0])
        tag.isSelected = false;
    })
    this.listFilter[0].isSelected = true
  }

  public searchControl: FormControl = new FormControl('');
  public activeTab: string = this.router.url?.split('/')[2] || 'project-list';
  public searchControlBG: FormControl = new FormControl('');
  public filterItems: string[] = [
    'Cloud',
    'Data Analytics BI',
    'DevOps',
    'Frontend',
    'JAVA',
    'MSA',
    'Python',
    'Testing',
    'RPA',
    'Cloud',
    'Data Analytics BI',
    'DevOps',
    'Frontend',
    'JAVA',
    'MSA',
    'Python',
    'Testing',
    'RPA',
    'Cloud',
    'Data Analytics BI',
    'DevOps',
    'Frontend',
    'JAVA',
    'MSA',
    'Python',
    'Testing',
    'RPA'
  ]
  navLinks = ["home", "tab 2", "tab 3", "tab 4", "tab 5"];
  public arrayFilter: FilterModel[] = DEFAULT_PARAMS.PROJECT_DATA.ARRAY_FILTER;
  public topProjectArrayFilter: FilterModel[] = DEFAULT_PARAMS.PROJECT_DATA.TOP_PROJECT_ARRAY_FILTER
  compareTest = (ob1: any, ob2: any) => {
    if (ob1 == ob2) {
      return true;
    } else {
      return false;
    }
  }

  tabs = ['First', 'Second', 'Third', 'Fourth', 'Fifth', 'Six', 'Seven', 'Eight', 'Nine', 'Ten', 'Eleven']

  public permission: Map<string, boolean>;
  constructor(public dialog: MatDialog,
    private cd: ChangeDetectorRef,
    private projectsService: ProjectsService,
    private loader: LoadingService,
    private dialogCommonService: DialogCommonService,
    public translate: TranslateService,
    public notifyService: NotificationService,
    private elasticService: ElasticService,
    private router: Router,
    private changeDetectorRef: ChangeDetectorRef
  ) {

    this.permission = new Map([
      ['ADD_BOSCH_PROJECT', false],
      ['DELETE_PROJECT', false],
      ['EDIT_PROJECT', false],
      ['VIEW_PROJECT_DETAIL', false],
      ['VIEW_VMODEL', false]
    ]);
    let listOfPermissions = JSON.parse(localStorage.getItem(TOKEN_KEY) || '{}')['permissions']
    if (listOfPermissions && listOfPermissions.length > 0) {
      for (let i = 0; i < listOfPermissions.length; i++) {
        if (this.permission.has(listOfPermissions[i]['code'])) {
          this.permission.set(listOfPermissions[i]['code'], true)
        }
      }
    }

  }

  ngAfterViewInit(): void {
    this.handleAddHoverAutoSlideSkillTag()
  }

  @HostListener('document:keydown', ['$event'])
  keydown(e: KeyboardEvent) {
  }

  public totalProjects: number = -1
  public projectInfoList: projectBasicInfo[] = []
  public yearList: string[] = []
  public customerGBList: string[] = []
  public teamList: string[] = []
  public orderFunctions: orderBy[] = OrderBy
  public typeFilter: Set<'Bosch' | 'Non-Bosch'> = new Set()
  public statusFilter: Set<string> = new Set()
  public listGbUnit: string[] = []
  public listCustomerGb: string[] = []
  public qurey: string
  switchTabSubcription: Subscription;
  updateProjectSubscription: Subscription
  private changeCustomerSubscription: Subscription;
  ngOnInit(): void {
    this.getAllProjectScopes()
    this.switchTabSubcription = this.projectsService.switchTabEvent.subscribe((tab) => {
      switch (tab) {
        case CONFIG.PROJECT.PROJECT_LIST:
          this.searchControl.setValue('');
          break;
        case CONFIG.CUSTOMER_GB.CUSTOMER_GB:
          this.searchControlBG.setValue('');
          this.activeTab = tab;
          break;
        default:
          this.activeTab = tab;
          break;
      }
    })

    this.projectsService.viewProjectDetailEvent.subscribe((projectId: string) => {
      this.showProjectDetail(projectId);
    })

    this.getDepartmentProjectsList(null, null);
    this.projectsService.setTagList(this.listFilter);
    this.getFilter();
    this.getTagFilter();

    this.searchControl.valueChanges.pipe(debounceTime(500)).subscribe(query => {
      this.qurey = query
      this.projectsService.searchChangeEvent.emit({ type: CONFIG.PROJECT.PROJECT_LIST, data: query });
    })
    this.searchControlBG.valueChanges.pipe(debounceTime(500)).subscribe(query => {
      this.projectsService.searchChangeEvent.emit({ type: CONFIG.CUSTOMER_GB.CUSTOMER_GB, data: query });
    })
    this.getListGbUnit()
    this.getListCustomerGb()

    this.router.events.subscribe(event => { // route change
      if (event instanceof NavigationStart && event.url.includes(this._projects)) {
        const currentProjecTailtUrl = event.url.split('/')[2]
        this.activeTab = currentProjecTailtUrl ? currentProjecTailtUrl : this._project_list
        // check if in project portfolio so don't change the current selected tab
        if (currentProjecTailtUrl.includes(this._project_portfolio) || currentProjecTailtUrl.includes(this._customer_portfolio)) {
          return;
        }
        let currentTabIndex = this.projectTabList.findIndex((e) => e.routeName === this.activeTab)
        currentTabIndex = currentTabIndex > -1 ? currentTabIndex : 2
        this.selectedProjectTabIndex = currentTabIndex

        // default tags selected when change tab
        if (currentProjecTailtUrl === this._project_list) {
          this.listFilter.forEach((e, i) => {
            if (i !== 0) {
              e.isSelected = false
            } else {
              e.isSelected = true
            }
          })
          
          setTimeout(() => {
            this.handleAddHoverAutoSlideSkillTag()
          },0)
        }
      }
    });
    this.selectedProjectTabIndex = this.projectTabList.findIndex((e) => e.routeName === this.activeTab)

    this.updateProjectSubscription = this.projectsService._changeProject.subscribe((res) => {
      if (res) {
        this.projectsService.searchChangeEvent.emit({ type: CONFIG.PROJECT.PROJECT_LIST, data: this.qurey });
      }
    })
    this.projectsService._fetchListProjectSuccessful.subscribe((data) => {
      this.listFilter.forEach(tags => {
        tags.projectCount = data.reduce((count, project) =>
          count + (project.technologyUsed?.includes(tags.name) ? 1 : 0), 0);
      });
    })
    this.changeCustomerSubscription = this.projectsService._updateCustomer.subscribe((event: CustomerUpdateEventModel) => {
      this.getFilter();
    });
  }

  ngOnDestroy() {
    this.switchTabSubcription.unsubscribe();
    this.updateProjectSubscription?.unsubscribe()

    this.projectsService.setSelectProjectScope('');
    this.projectsService.setSelectedType('');
    this.projectsService.setSelectedGB('');
    this.projectsService.setSelectedStatus('');
    this.projectsService.setSelectGbUnit('');
    this.changeCustomerSubscription.unsubscribe();

    this.handleRemoveHoverAutoSlideSkillTag()
  }

  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  public currentPage: number = DEFAULT_PARAMS.DEFAULT_PAGE
  pageSize: number = DEFAULT_PARAMS.DEFAULT_ITEMS_PER_PAGE
  public pageSizeOption = CONFIG.PAGINATION_OPTIONS

  public searchString?: string
  public currentlyOrderBy: string = 'startDate'
  public currentlySelectedGB: gb[] = [];
  public currentlySelectedTeam: string[] = [];
  public currentlySelectedYear: string[] = [];
  public projectType: any;
  getDepartmentProjectsList(newProject: any, option: "save" | "remove" | null) {
    this.projectInfoList = this.customerGBList = this.teamList = this.yearList = []
    this.projectsService.getFilterList().subscribe(response => {
      this.arrayFilter.filter((r) => { return r.key === 'startDate' })[0].originalData = response.data.year_filter
      this.arrayFilter.filter((r) => { return r.key === 'customerGB' })[0].originalData = response.data.customer_gb_filter
      this.arrayFilter.filter((r) => { return r.key === 'team' })[0].originalData = response.data.team_filter
      this.projectType = Helpers.cloneDeep(response.data.project_type_filter)
      this.getProjects(newProject, option)
    })

  }

  private unfilteredList: projectBasicInfo[] = []
  getProjects(newProject: any, option: "save" | "remove" | null) {

    this.elasticService.getDocument('project', {
      size: 10000,
      query: '',
      from: 0
    }).subscribe(data => {
      this.projectInfoList = data?.arrayItems || [];
      if (newProject != null) {
        let projectBasicInfo: projectBasicInfo = {
          id: newProject.project_id,
          projectName: newProject.name,
          projectId: newProject.project_id,
          technologyUsed: newProject.technology_used,
          description: newProject.des,
          teamSize: newProject.team
        }

        let existProjectIndex = this.projectInfoList.findIndex(p => p.id === projectBasicInfo.id)
        if (existProjectIndex !== -1) {
          this.projectInfoList.splice(existProjectIndex, 1);
        }

        if (option === 'save') {
          this.projectInfoList.push(projectBasicInfo);
        }
      }

      this.unfilteredList = this.projectInfoList || [];
      this.totalProjects = data?.totalItems || 0;
    })

  }

  getListGbUnit() {
    this.projectsService.getGBUnit().subscribe((result) => {
      this.listGbUnit = Helpers.cloneDeep(result)
    });
  }

  getListCustomerGb() {
    this.projectsService.getCustomerGb().subscribe((result: BaseResponseModel) => {
      this.listCustomerGb = Helpers.cloneDeep(result.data)
    });

  }
  applyFilter(event: Event) {
  }

  onDropdownChange() {
    this.projectInfoList.sort((a, b) => {
      if (this.currentlyOrderBy === 'name,desc') {
        return a.projectName.toLowerCase().trim() < b.projectName.toLowerCase().trim() ? 1 : a.projectName.toLowerCase().trim() > b.projectName.toLowerCase().trim() ? -1 : 0
      }
      const x = (a[this.currentlyOrderBy as keyof projectBasicInfo]?.toString() as string).toLowerCase().trim()
      const y = (b[this.currentlyOrderBy as keyof projectBasicInfo]?.toString() as string).toLowerCase().trim()
      if (this.currentlyOrderBy === 'projectName') {
        return x > y ? 1 : x < y ? -1 : 0
      }
      return x < y ? 1 : x > y ? -1 : 0
    })
  }

  onSearch(event: any) {
    const filterValue = (event.target as HTMLInputElement).value;
    let tmpData = Helpers.cloneDeep(this.projectInfoList).filter((val: projectBasicInfo) => {
      return val.projectName.toLowerCase().trim().includes(filterValue.toLowerCase())
    })
    this.projectInfoList = tmpData;
    this.totalProjects = this.projectInfoList.length
  }

  onSearchChange() {
  }

  onPaginationChange(event: any) {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
  }

  addItem() {
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: BoschProjectDetailComponent,
      title: this.translate.instant('projects.detail.title_add'),
      icon: 'a-icon boschicon-bosch-ic-add',
      width: '80vw',
      height: 'auto',
      type: 'edit',
      passingData: {
        type: 'add',
        project_type_id: this.projectType.filter((value: any) => {
          return value.name === 'Bosch'
        })[0].id
      }
    })
    dialogRef.afterClosed().subscribe(resp => {
      if (resp) {
        this.getDepartmentProjectsList(resp.data, 'save')
        this.projectsService.addProjectEvent.emit();
      }
    })
  }

  onAddCustomerGb() {
    this.projectsService.switchTabEvent.emit(CONFIG.CUSTOMER_GB.PORTFOLIO);
    this.router.navigate(['projects', 'customer-portfolio'], { state: { type: 'add' } });
  }

  showProjectDetail(projectId: string) {
    this.activeTab = 'project-portfolio';
    this.router.navigate(['projects', 'project-portfolio'], { state: { projectId } });
  }


  deleteItem(data: any) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: this.translate.instant('projects.delete_prompt.title'),
        content: this.translate.instant('projects.delete_prompt.content.name_line') + ': ' + data.projectName + '.\n ' + this.translate.instant('projects.delete_prompt.content.revert_line') + '.',
        btnConfirm: this.translate.instant('projects.delete_prompt.yes'),
        btnCancel: this.translate.instant('projects.delete_prompt.no')
      },
      width: "420px"
    },
    )
    dialogRef.afterClosed().subscribe(response => {
      if (response === true) {
        let comLoader = this.loader.showProgressBar()
        this.projectsService.deleteProjects(data.projectId).pipe(finalize(() => {
          this.loader.hideProgressBar(comLoader)
        })).subscribe(resp => {
          if (resp.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
            let projectToRemove: any = { id: data.projectId, project_id: data.projectId };
            this.getDepartmentProjectsList(projectToRemove, 'remove');
            this.notifyService.success(this.translate.instant('projects.delete_prompt.delete_successfully'));
          }
          else {
            this.notifyService.error(resp.message || resp.code)
          }
        }
        )
      }
    })
  }

  changeItem(data: any, type: 'edit' | 'view') {
    if (data.projectType === 'Bosch') {
      const dialogRef = this.dialogCommonService.onOpenCommonDialog({
        component: BoschProjectDetailComponent,
        title: (type === 'edit') ? this.translate.instant('projects.detail.title_edit') : this.translate.instant('projects.detail.title'),
        width: '80vw',
        height: 'auto',
        type: type,
        passingData: {
          type: type,
          project_id: data.projectId
        }
      })
      dialogRef.afterClosed().subscribe(resp => {
        if (resp) {
          this.getDepartmentProjectsList(resp, 'save');
        }
      })
    }
    else if (data.projectType === 'Non-Bosch') {
      const dialogRef = this.dialogCommonService.onOpenCommonDialog({
        component: (type === 'edit') ? EditNonBoschProjectDetailComponent : NonBoschProjectDetailComponent,
        title: (type === 'edit') ? this.translate.instant('projects.detail.title_nonBosch_edit') : this.translate.instant('projects.detail.title_nonBosch'),
        width: '80vw',
        height: 'auto',
        type: type,
        passingData: {
          type: type,
          project_id: data.projectId
        }
      })
      dialogRef.afterClosed().subscribe(resp => {
        if (resp) {
          this.getDepartmentProjectsList(resp.data, 'save')
        }
      })
    }
    else {
      console.error('Project Type is not valid!')
    }
  }
  removeActiveTag(event: object | number) {
    let element;
    if (typeof event === 'object') {
      element = document.getElementById('mat-option-0');
    }
    else {
      const id = 'mat-option-' + event;
      element = document.getElementById(id);
    }
  }
  onUpload() {
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: UploadProjectsComponent,
      title: 'projects.import.title',
      icon: 'a-icon a-button__icon  boschicon-bosch-ic-import',
      width: '600px',
      height: 'auto',
      type: 'edit',
      passingData: {}
    });
    dialogRef.afterClosed().subscribe(response => {
      if (response === true) {
        this.notifyService.success(this.translate.instant('projects.import.import_successfully'))
        this.getDepartmentProjectsList(null, null)
        this.getTagFilter()
        this.projectsService.addProjectEvent.emit();
      }
    });
  }

  onSelectTab(value: string) {
    this.activeTab = value;
    this.router.navigateByUrl(`/projects/${value}`);
  }
  handleSearch() {
    this.projectsService.searchChangeEvent.emit({ type: CONFIG.CUSTOMER_GB.CUSTOMER_GB, data: this.searchControl.value.toString() });
  }
  handleSearchBG() {
    this.projectsService.searchChangeEvent.emit({ type: CONFIG.CUSTOMER_GB.CUSTOMER_GB, data: this.searchControlBG.value.toString() });
  }
  tab: any;
  selectedTab(e: any) {
    this.tab = e
  }
  onSearchChangeCustomerGb() {
    this.projectsService.searchChangeEvent.emit({ type: CONFIG.CUSTOMER_GB.CUSTOMER_GB, data: this.searchControlBG.value.toString() });
  }

  gbList: string[] = [];
  statusList = CONFIG.PROJECT_STATUS_LIST;
  typeList: ProjectType[] = [];

  getFilter() {
    this.projectsService.getFilterList().subscribe(
      response => {
        this.gbList = response.data.customer_gb_filter;
        this.gbList = this.gbList.slice().sort((a, b) => a.trim().localeCompare(b.trim(), undefined, { sensitivity: 'base' }));
        this.typeList = response.data.project_type_filter;
      }
    );
  }

  selectedGB: string;
  selectedStatus: string;
  selectedType: string;
  selectedProjectScope: string
  onSelectType(e: any) {
    this.projectsService.setSelectedType(e);
    this.projectsService.filterChangeEvent.emit();
  }

  onSelectGB(e: any) {
    this.projectsService.setSelectedGB(e);
    this.projectsService.filterChangeEvent.emit();
  }

  onSelectStatus(e: any) {
    this.projectsService.setSelectedStatus(e);
    this.projectsService.filterChangeEvent.emit();
  }

  onProjectTabChange(event: MatTabChangeEvent) {
    const _routeName: string = this.projectTabList[event.index].routeName;
    this.activeTab = _routeName;
    this.router.navigate(['projects', _routeName]);
  }

  toggleIsTop() {
    this.isTopSelected = !this.isTopSelected;
    this.projectsService.topChangeEvent.emit(this.isTopSelected);
  }
  toggleChange(event: ToggleDataEmitModel) {
    if (event?.checked) {
      this.isTopSelected = false
      this.toggleIsTop()
    } else {
      if (this.listFilter.length) {
        this.isTopSelected = true
        this.toggleIsTop()
        this.onFilterClick(0)
      }
    }
  }
  truncateText(text: string, num: number = 8) {
    return text?.length > num ? text.slice(0, num) + "..." : text
  }
  getAllProjectScopes() {
    this.projectsService.getAllProjectScope().subscribe((result: BaseResponseModel) => {
      if (result.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
        this.projectScopeList = result.data
        this.projectsService.getProjectScopeEvent(result.data)
      }
    });
  }
  onChangeProjectScope(e: any) {
    this.projectsService.setSelectProjectScope(e);
    this.projectsService.filterChangeEvent.emit();
  }
  onChangeGBUnit(e: any) {
    this.projectsService.setSelectGbUnit(e);
    this.projectsService.filterChangeEvent.emit();
  }
  onChangeGBForVmodel(type: string, data: string) {
    this.projectsService.selectGbData(type, data)
  }

  handleAddHoverAutoSlideSkillTag() {
    const controlsBtn = document.querySelector('#container-tag-filter')?.querySelectorAll('.mat-ripple.mat-tab-header-pagination');
    this.controlsBtnSkillTag = controlsBtn
    if (controlsBtn && controlsBtn.length > 0) {
      for (let i = 0; i < controlsBtn.length; i++) {
        controlsBtn[i]?.addEventListener('mouseenter', () => this.setClickEventForControlsSkillTag(controlsBtn[i] as HTMLElement));
      }
    }
  }
  handleRemoveHoverAutoSlideSkillTag() {
    if (this.controlsBtnSkillTag && this.controlsBtnSkillTag.length > 0) {
      for (let i = 0; i < this.controlsBtnSkillTag.length; i++) {
        this.controlsBtnSkillTag[i]?.removeEventListener('mouseenter', () => this.setClickEventForControlsSkillTag(this.controlsBtnSkillTag[i] as HTMLElement))
    }
  }}
  setClickEventForControlsSkillTag(controls: HTMLElement) {
    controls.click()
  }
}

