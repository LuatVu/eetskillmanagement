import { Component, Inject, OnInit, ViewChild, Input } from '@angular/core';
import { ElasticService } from '../../../../shared/services/elastic.service';
import { LoadingService } from '../../../../shared/services/loading.service';
import { finalize } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OrderBy } from '../../models/order-by.constants';
import { FilterProjectModel, ProjectScope, orderBy, projectBasicInfo } from '../../models/projects.model';
import { ProjectsService } from '../../services/projects.service';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { BoschProjectDetailComponent } from '../bosch-project-detail/bosch-project-detail.component';
import { TranslateService } from '@ngx-translate/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { PermisisonService } from 'projects/eet-core-demo/src/app/shared/services/permisison.service';
import { Subscription } from 'rxjs/internal/Subscription';
import { Projects } from '../../models/projects.model';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { NonBoschProjectDetailComponent } from '../non-bosch-project-detail/non-bosch-project-detail.component';
import { NotificationService } from '@bci-web-core/core';
import { MatPaginator } from '@angular/material/paginator';
import { APP_BASE_HREF } from '@angular/common';
import { animate, keyframes, state, style, transition, trigger } from '@angular/animations';
import { Router } from '@angular/router';
import { BaseResponseModel } from 'projects/eet-core-demo/src/app/shared/models/base.model';
import { ProjectDetailBosch } from '../../models/dialog-data/project-detail-bosch.model';



@Component({
  selector: 'eet-project-list',
  templateUrl: './project-list.component.html',
  styleUrls: ['./project-list.component.scss']
})
export class ProjectListComponent implements OnInit {
  public readonly NOT_APPLICABLE = CONFIG.COMMON_FORM.NOT_APPLICABLE;
  @ViewChild('paginator') paginator: MatPaginator;

  public orderFunctions: orderBy[] = OrderBy;
  public currentlyOrderBy: string = 'projectName';


  projectList: Array<Projects> = [];
  projectToShow: Array<Projects> = [];
  projectLength: number;
  unfilteredProject: Array<Projects> = [];
  filteredProject: Array<Projects> = [];
  topProject: Array<Projects> = [];
  dropdownFilteredProject: Array<Projects> = [];

  pageSizeOptions = CONFIG.PAGINATION_OPTIONS;
  pageSize: number = 10;

  listFilter: FilterProjectModel[] = [];

  selectedType: string;
  selectedStatus: string;
  selectedGB: string;
  selectedProjectScope: string
  selectedGbUnit: string

  isTop: boolean = false;

  constructor(private elasticService: ElasticService, private loaderService: LoadingService,
    private projectsService: ProjectsService,
    @Inject(APP_BASE_HREF) public baseHref: string,
    private router: Router) {

  }

  filterSubscription: Subscription;
  tagSubscription: Subscription;
  getProjectSubscription: Subscription;
  searchSubscription: Subscription;
  topSubscription: Subscription;
  projectChangeSubscription: Subscription;
  switchTabSubscription: Subscription;
  updateProjectSubscription: Subscription
  query: string = '';
  BOSCH_TYPE = CONFIG.PROJECT.PROJECT_TYPE.BOSCH
  ngOnInit() {

    this.projectsService.switchTabEvent.emit(CONFIG.PROJECT.PROJECT_LIST);
    this.searchSubscription = this.projectsService.searchChangeEvent.subscribe(query => {
      if (query.type === CONFIG.PROJECT.PROJECT_LIST) {
        this.query = query.data;
        this.getAllProject(query.data);
      }
    })
    this.filterSubscription = this.projectsService.filterChangeEvent.subscribe(() => {
      this.filterProject(true);
    })
    this.tagSubscription = this.projectsService.tagChangeEvent.subscribe(() => {
      this.filterProject();
    })
   
    this.topSubscription = this.projectsService.topChangeEvent.subscribe(isTop => {
      this.isTop = isTop;
      this.filterProject(true);
    })
    this.projectChangeSubscription = this.projectsService.addProjectEvent.subscribe(() => {
      this.getAllProject('');
    })
   
  }

  undefinedFilter: string;

  ngOnDestroy() {
    this.filterSubscription.unsubscribe();
    this.tagSubscription.unsubscribe();
    this.getProjectSubscription?.unsubscribe();
    this.searchSubscription.unsubscribe();
    this.topSubscription.unsubscribe();
    this.updateProjectSubscription?.unsubscribe()

    this.projectsService.setSelectedGB(this.undefinedFilter);
    this.projectsService.setSelectedStatus(this.undefinedFilter);
    this.projectsService.setSelectedType(this.undefinedFilter);
    this.projectsService.setSelectProjectScope(this.undefinedFilter)
    this.projectsService.setSelectGbUnit(this.undefinedFilter)
  }


  getAllProject(query: string) {
    const loader = this.loaderService.showProgressBar();
    this.getProjectSubscription = this.elasticService.getDocument('project', {
      size: 10000,
      query: query,
      from: 0
    }).pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe(response => {
        response.arrayItems = response.arrayItems.filter((e: Projects) => e.projectType === this.BOSCH_TYPE)
        this.unfilteredProject = Helpers.cloneDeep(response.arrayItems);
        this.filteredProject = Helpers.cloneDeep(this.unfilteredProject);
        this.filterProject(true);
      });
  }

  filterProject(isChangeProjectCount:boolean = false) {
    // filter based on dropdowns
    this.selectedGB = this.projectsService.getSelectedGB();
    this.selectedStatus = this.projectsService.getSelectedStatus();
    this.selectedType = this.projectsService.getSelectedType();
    this.selectedProjectScope = this.projectsService.getSelectProjectScope()
    this.selectedGbUnit = this.projectsService.getSelectGbUnit()
    this.dropdownFilteredProject = this.unfilteredProject.filter(project => {
      const typeMatch = !this.selectedType || project.projectType === this.selectedType;
      const gbMatch = !this.selectedGB || project.customerGB === this.selectedGB;
      const statusMatch = !this.selectedStatus || project.status === this.selectedStatus;
      const projectScopeMatch = !this.selectedProjectScope || project.scopeId === this.selectedProjectScope;
      const gbUnitMatch = !this.selectedGbUnit || project.gbUnit === this.selectedGbUnit;
      return typeMatch && statusMatch && gbMatch && projectScopeMatch && gbUnitMatch
    })
    // filter based on tags
    this.listFilter = this.projectsService.getTagList();
    let selectedProject = this.listFilter.filter(item => item.isSelected);
    if (selectedProject[0] && selectedProject[0].name === 'All') {
      this.filteredProject = this.dropdownFilteredProject;
    } else {
      this.filteredProject = this.dropdownFilteredProject.filter(project => {
        if (project.technologyUsed)
          return selectedProject.some(filter => project.technologyUsed.includes(filter.name));
        else return false;
      })
    }

    if (this.isTop) {
      this.topProject = this.filteredProject.filter(project => {
        if (project.topProject) {
          return project.topProject === true;
        } else return false;
      })
    } else {
      this.topProject = this.filteredProject;
    }

    this.filteredProject = this.topProject;
    this.onDropdownChange(this.currentlyOrderBy);
    if(isChangeProjectCount){
      this.projectsService.fetchListProjectSuccessful(this.filteredProject)
    }
    this.projectToShow = this.filteredProject.slice(0, this.pageSize);
    this.paginator.firstPage();
  }

  onPageChange($event: any) {
    if ($event)
      this.pageSize = $event.pageSize;
    this.projectToShow = this.filteredProject.slice($event.pageIndex * $event.pageSize, $event.pageIndex * $event.pageSize + $event.pageSize);
  }

  get projectToShowLength() {
    return (this.projectToShow && this.projectToShow.length) ? this.projectToShow.length : 0;
  }

  get projectListLength() {
    return (this.filteredProject && this.filteredProject.length) ? this.filteredProject.length : 0;
  }

  onDropdownChange(currentlyOrderBy: any) {
    this.currentlyOrderBy = currentlyOrderBy;
    this.filteredProject.sort((a, b) => {
      if (this.currentlyOrderBy === 'name,desc') {
        return a.projectName.toLowerCase().trim() < b.projectName.toLowerCase().trim() ? 1 : a.projectName.toLowerCase().trim() > b.projectName.toLowerCase().trim() ? -1 : 0
      }
      if (this.currentlyOrderBy === 'startDateReverse') {
        const date1 = new Date(a.startDate);
        const date2 = new Date(b.startDate);
        return date1 < date2 ? 1 : date1 > date2 ? -1 : 0
      }
      const x = (a[this.currentlyOrderBy as keyof projectBasicInfo]?.toString() as string).toLowerCase().trim()
      const y = (b[this.currentlyOrderBy as keyof projectBasicInfo]?.toString() as string).toLowerCase().trim()
      if (this.currentlyOrderBy === 'projectName') {
        return x > y ? 1 : x < y ? -1 : 0
      }

      return x > y ? 1 : x < y ? -1 : 0
    })
    this.projectToShow = this.filteredProject.slice(0, this.pageSize);

  }

}
