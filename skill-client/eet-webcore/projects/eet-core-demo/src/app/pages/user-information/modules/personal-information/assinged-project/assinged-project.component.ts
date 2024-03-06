import {
  animate,
  state,
  style,
  transition,
  trigger
} from '@angular/animations';
import { AfterViewInit, ChangeDetectorRef, Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { TranslateService } from '@ngx-translate/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { finalize } from 'rxjs/operators';
import { ProjectModel } from '../personal-infomation.model';
import { PersonalInfomationService } from '../personal-infomation.service';
import { DialogBoschProjectComponent } from './components/dialog/dialog-add-bosch-project/dialog-add-bosch-project.component';
import { DialogNonBoschProjectComponent } from './components/dialog/dialog-add-non-bosch-project/dialog-add-non-bosch-project.component';
import { DialogViewProjectComponent } from './components/dialog/dialog-view-project/dialog-view-project.component';
import { PersonalProjectService } from './personal-project.service';
import { NonBoschProjectDetailComponent } from '../../../../projects/components/non-bosch-project-detail/non-bosch-project-detail.component';
import { EditNonBoschProjectDetailComponent } from '../../../../projects/components/non-bosch-project-detail/edit-non-bosch-project-detail.component';
import { PermisisonService } from 'projects/eet-core-demo/src/app/shared/services/permisison.service';
import { PaginDirectionUtil } from 'projects/eet-core-demo/src/app/shared/utils/paginDirectionUtil';
import { DEFAULT_PAGE_SIZE } from '../constants/constants';

@Component({
  selector: 'eet-assinged-project',
  templateUrl: './assinged-project.component.html',
  styleUrls: ['./assinged-project.component.scss'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({ height: '0px', minHeight: '0' })),
      state('expanded', style({ height: '*' })),
      transition(
        'expanded <=> collapsed',
        animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')
      ),
    ]),
  ],
})
export class AssingedProjectComponent implements OnInit, AfterViewInit{
  @Input() isEditMode: boolean = false;
  @Input() addboschproject = false;
  public columnsToDisplay = [
    'Project',
    'ProjectType',
    'Role',
    'TeamSize',
    'Status',
    'StartDate',
    'EndDate',
    'Action',
  ];
  public columnsToDisplayWithExpand = [...this.columnsToDisplay];
  public expandedElement: ProjectModel | null;
  @Input() personalID!: string;
  @Input() typeUser!: string;
  private personalProject!: ProjectModel[];
  private projectInfo = this.personalProject;
  public dataSource = new MatTableDataSource<any>();

  public element: boolean = true;
  @Input() isDisable: boolean = true; deleteOn = this.isDisable;
  @Input() paginator: MatPaginator
  @ViewChild(MatSort) sort: MatSort = new MatSort();
  public readonly paginationSize = CONFIG.PAGINATION_OPTIONS;
  public configBoschProject = CONFIG.PROJECT.PROJECT_TYPE.BOSCH;
  public configNonBoschProject = CONFIG.PROJECT.PROJECT_TYPE.NON_BOSCH;
  public searchedWord: string;

  public HAS_EDIT_ASSOCIATE_INFO_PERMISSION: boolean = false;

  constructor(
    private personalProjectService: PersonalProjectService,
    public dialog: MatDialog,
    private dialogCommonService: DialogCommonService,
    private personalInfomationService: PersonalInfomationService,
    private loaderService: LoadingService,
    private translateService: TranslateService,
    private notifyService: NotificationService,
    private permisisonService: PermisisonService,
    private cd: ChangeDetectorRef
  ) {
    this.HAS_EDIT_ASSOCIATE_INFO_PERMISSION = this.permisisonService.hasPermission(CONFIG.PERMISSIONS.EDIT_ASSOCIATE_INFO_PERMISSION);
  }
  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.paginator.pageSize = DEFAULT_PAGE_SIZE
    this.applyFilter()
    this.cd.detectChanges();
    setTimeout(() => {
      this.handleSortTable()

      
    },0)
  }

  ngOnInit(): void {
    this.getProjectList();
  }

  private getProjectList() {
    this.personalInfomationService.getPersonalInfoDetail()
      .subscribe(data => {
        this.personalProject = data.projects?.slice() || [];
        this.dataSource = new MatTableDataSource(data.projects);
        this.dataSource.paginator = this.paginator;
        this.handleSortTable()
      });
  }

  applyFilter() {
    this.dataSource.filter = this.searchedWord;
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  openDialogAddBoschProject() {
    if (this.typeUser === CONFIG.TYPE_USER.ASSOCIATE) {
      const dialog = this.dialogCommonService.onOpenCommonDialog({
        component: DialogBoschProjectComponent,
        title: this.translateService.instant('personal_information.assign_project.dialog.bosch_project.title'),
        width: '40vw',
        height: 'auto',
        icon: 'a-icon a-button__icon ui-ic-plus',
        type: 'edit',
        passingData: {
          projectsUser: this.personalProject,
          personalProjectList: this.dataSource.data,
          type: 'add'
        }
      })
      dialog.afterClosed().subscribe(result => {
        if (result) {
          this.notifyService.success(this.translateService.instant('personal_information.assign_project.dialog.bosch_project.notification_add_success'));
          this.getAssignProject();
        }
      });
    } else {
      const dialog = this.dialogCommonService.onOpenCommonDialog({
        component: NonBoschProjectDetailComponent,
        title: this.translateService.instant('personal_information.assign_project.dialog.non_bosch_project.title'),
        width: '75vw',
        maxWdith: '1200px',
        icon: 'a-icon a-button__icon ui-ic-plus',
        height: 'auto',
        type: 'edit',
        passingData: {
          type: 'add'
        }
      });
      dialog.afterClosed().subscribe(result => {
        if (result?.id) {
          this.notifyService.success(this.translateService.instant('personal_information.assign_project.dialog.non_bosch_project.notification_add_success'));
          this.getAssignProject();
        }
      });
    }
  }
  removeSelectedRows(selectedItem: any) {
    const dialogRef = this.dialogCommonService.onOpenConfirm({
      content: this.translateService.instant('personal_information.assign_project.confirm_delete_project'),
      title: this.translateService.instant('dialog.title_confirm'),
      btnConfirm: this.translateService.instant('button.yes'),
      btnCancel: this.translateService.instant('button.no'),
      icon: 'a-icon ui-ic-alert-warning'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const loader = this.loaderService.showProgressBar();
        this.personalProjectService.deleteProject(this.personalInfomationService._idUser, selectedItem)
          .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
          .subscribe((res) => {
            if (res?.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
              this.getAssignProject();
              this.notifyService.success(this.translateService.instant('personal_project.delete_project_successfully'));
            }
          })
      }
    })
  }

  getAssignProject() {
    const loader = this.loaderService.showProgressBar();
    this.personalProjectService.getPersonProject(this.personalInfomationService._idUser)
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe(data => {
        this.personalInfomationService.setPersonalInfoDetail({ projects: data });
      })
  }

  onProjectSave(updateProject: ProjectModel, currentProject: ProjectModel) {
    const _index = this.dataSource.data.findIndex(f => f.id == currentProject.id);
    const _currentProjectList = this.dataSource.data;
    _currentProjectList[_index] = updateProject;
    const loader = this.loaderService.showProgressBar();
    setTimeout(() => {
      this.loaderService.hideProgressBar(loader);
      this.dataSource = new MatTableDataSource(_currentProjectList);
    }, 500);
  }

  onExpandElement(project: ProjectModel) {
    this.expandedElement = this.expandedElement?.id == project?.id ? null : project;
  }

  onViewProject(project: ProjectModel) {
    const _dialog = this.dialogCommonService.onOpenCommonDialog({
      component: DialogViewProjectComponent,
      title: project.project_type == 'Non-Bosch' ? "View Non Bosch Project Detail" : "View Bosch Project Detail",
      width: "80vw",
      maxWdith: '1200px',
      height: 'auto',
      passingData: project,
      icon: 'a-icon ui-ic-watch-on',
      type: 'view',
    });
  }

  onEditProject(project: ProjectModel) {
    if (this.typeUser === CONFIG.TYPE_USER.ASSOCIATE) {
      if (project.project_type === "Bosch") { // associate + edit bosch
        const dialog = this.dialogCommonService.onOpenCommonDialog({
          component: DialogBoschProjectComponent,
          title: this.translateService.instant('personal_information.assign_project.dialog.bosch_project.edit_project'),
          width: '40vw',
          height: 'auto',
          icon: 'a-icon boschicon-bosch-ic-edit',
          type: 'edit',
          passingData: {
            projectsUser: this.personalProject,
            personalProjectList: this.dataSource.data,
            project: project,
            type: 'edit-associate'
          }
        }).afterClosed().subscribe((response) => {
          if (response) {
            this.notifyService.success(this.translateService.instant('personal_project.update_bosch_project_successfully'));
            this.getAssignProject();
          }
        });
      } else { // associate + edit non bosch
        const dialogRef = this.dialogCommonService.onOpenCommonDialog({
          component: NonBoschProjectDetailComponent,
          title: "Edit Non Bosch Project Detail",
          icon: 'a-icon boschicon-bosch-ic-edit',
          width: "80vw",
          maxWdith: '1200px',
          height: 'auto',
          type: 'edit',
          passingData: {
            type: 'edit',
            project_id: project.project_id
          }
        }).afterClosed().subscribe((response) => {
          if (response) {
            this.notifyService.success(this.translateService.instant('personal_project.update_non_bosch_project_successfully'));
            this.getAssignProject();
          }
        });
      }
    } else {  // personal
      if (project.project_type === "Bosch") { // personal + edit bosch
        const dialog = this.dialogCommonService.onOpenCommonDialog({
          component: DialogBoschProjectComponent,
          title: this.translateService.instant('personal_information.assign_project.dialog.bosch_project.edit_project'),
          width: '40vw',
          height: 'auto',
          icon: 'a-icon boschicon-bosch-ic-edit',
          type: 'edit',
          passingData: {
            projectsUser: this.personalProject,
            personalProjectList: this.dataSource.data,
            project: project,
            type: 'edit-personal'
          }
        }).afterClosed().subscribe((response) => {
          if (response) {
            this.notifyService.success(this.translateService.instant('personal_project.update_bosch_project_successfully'));
            this.getAssignProject();
          }
        });
      } else { // personal + edit non bosch
        const dialogRef = this.dialogCommonService.onOpenCommonDialog({
          component: NonBoschProjectDetailComponent,
          title: "Edit Non Bosch Project Detail",
          icon: 'a-icon boschicon-bosch-ic-edit',
          width: "80vw",
          maxWdith: '1200px',
          height: 'auto',
          type: 'edit', 
          passingData: {
            type: 'edit',
            project_id: project.project_id
          }
        }).afterClosed().subscribe((response) => {
          if (response) {
            this.notifyService.success(this.translateService.instant('personal_project.update_non_bosch_project_successfully'));
            this.getAssignProject();
          }
        });
      }
    }
  }



  isDeleteAllowed(project_type: string) {
    if (project_type === this.configNonBoschProject || (project_type === this.configBoschProject && this.typeUser === CONFIG.TYPE_USER.ASSOCIATE)) {
      return true
    }
    return false
  }
  
  handleSortTable() {
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (item, property) => {
      switch (property) {
        case 'Project':
          return item.name.toLowerCase();
        case 'ProjectType':
          return item.project_type.toLowerCase();
        case 'Role':
          return item.role_name.toLowerCase();
        case 'TeamSize':
          return item.team_size;
        case 'Status':
          return item.status.toLowerCase();
        case 'StartDate':
          return item.member_start_date;
        case 'EndDate':
          if (item.member_end_date) {
            return new Date(item.member_end_date);
          }
          return new Date(new Date().getFullYear() + 10, new Date().getMonth(), new Date().getDate())
      }
    };
  }

}
