import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { TranslateService } from '@ngx-translate/core';
import { DEFAULT_COLOUR_MIXED_PROJECT_SCOPE, DEFAULT_COLOUR_PROJECT_SCOPE, ProjectScope } from '../../../model/manage-project-scope/manage-project-scope.constant';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { ProjectsService } from '../../../../projects/services/projects.service';
import { BaseResponseModel } from 'projects/eet-core-demo/src/app/shared/models/base.model';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { PaginDirectionUtil } from 'projects/eet-core-demo/src/app/shared/utils/paginDirectionUtil';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { finalize } from 'rxjs/operators';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { ManageProjectScopeDialogComponent } from '../../dialogs/manage-project-scope/manage-project-scope-dialog/manage-project-scope-dialog.component';
import { T } from '@angular/cdk/keycodes';
import { NotificationService } from '@bci-web-core/core';
import { ManageProjectRoleService } from '../manage-project-role/manage-project-role.service';
import { Subscription} from 'rxjs';
import { ManageProjectScopeService } from '../../../services/manage-project-scope.service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from 'projects/eet-core-demo/src/app/shared/components/dialogs/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'eet-manage-project-scope',
  templateUrl: './manage-project-scope.component.html',
  styleUrls: ['./manage-project-scope.component.scss'],
})
export class ManageProjectScopeComponent implements OnInit,AfterViewInit {
  public displayedColumns: string[] = [
    'ordinal',
    'scope_name',
    'color',
    'color_hover',
    'edit',
    'delete'
  ];
  public dataSource: MatTableDataSource<ProjectScope> = new MatTableDataSource();
  @ViewChild(MatPaginator)
  private paginator!: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  public pageSizeOption = CONFIG.PAGINATION_OPTIONS
  updateProjectScope:Subscription
  public readonly DEFAULT_COLOUR_PROJECT_SCOPE:string= DEFAULT_COLOUR_PROJECT_SCOPE
  public readonly DEFAULT_COLOUR_HOVER_PROJECT_SCOPE:string= DEFAULT_COLOUR_MIXED_PROJECT_SCOPE
  @ViewChild('input', { static: false }) inputElement: ElementRef;
  constructor(
    private translate: TranslateService,
    private projectsService:ProjectsService,
    private loaderService:LoadingService,
    public dialog: DialogCommonService,
    public matDialog:MatDialog,
    private notify:NotificationService,
    private manageProjectScopeService:ManageProjectScopeService
  ) { }
 
  ngOnInit(): void {
    this.getAllProjectScopes()

    PaginDirectionUtil.expandTopForDropDownPagination()

    this.updateProjectScope = this.manageProjectScopeService._changeProjectScope.subscribe((res)=> {
      if(res){
        this.getAllProjectScopes()
        if(this.inputElement){
          this.inputElement.nativeElement.value =''
        }
      }
    })
  }


  getAllProjectScopes() {
    const loader = this.loaderService.showProgressBar();
    this.projectsService.getAllProjectScope().pipe(finalize(() => this.loaderService.hideProgressBar(loader))).subscribe((result: BaseResponseModel) => {
      if(result.code===CONFIG.API_RESPONSE_STATUS.SUCCESS) {
        this.dataSource=new MatTableDataSource<ProjectScope>(result.data)
        this.dataSource.paginator = this.paginator
        this.dataSource.sort=this.sort
      }
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }
  applyFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  addProjectScope() {
    const dialogRef = this.dialog.onOpenCommonDialog({
      component: ManageProjectScopeDialogComponent,
      title: this.translate.instant('system.manage_project_scope.dialog.title_add'),
      width: "915px",
      height: 'auto',
      type: 'edit',
      passingData: {
        type:"add",
      }
    })
    dialogRef.afterClosed().subscribe((response) => {
      if (response) {
      }
    })
  }
 
  editProjectScope(projectScope:ProjectScope) {
    const dialogRef = this.dialog.onOpenCommonDialog({
      component: ManageProjectScopeDialogComponent,
      title: this.translate.instant('system.manage_project_scope.dialog.title_update'),
      width: "915px",
      height: 'auto',
      type: 'edit',
      passingData: {
        type:"edit",
        projectScope:projectScope
      }
    })
    dialogRef.afterClosed().subscribe((response) => {
      if (response) {
      }
    })
  }
  deleteProjectScope(projectScope:ProjectScope) {
    const confirmDialogRef = this.matDialog.open(ConfirmDialogComponent, {
      data: {
        title: this.translate.instant('projects.delete_prompt.title'),
        content: this.translate.instant('projects.delete_prompt.content.name_line') + ': ' + this.truncateText(projectScope.name) + '.\n ' + this.translate.instant('projects.delete_prompt.content.revert_line') + '.',
        btnConfirm: this.translate.instant('projects.delete_prompt.yes'),
        btnCancel: this.translate.instant('projects.delete_prompt.no')
      },
      width: "420px"
    },
    )
    confirmDialogRef.afterClosed().subscribe(response => {
      if (response) {
        const loader = this.loaderService.showProgressBar();
        this.manageProjectScopeService.deleteProjectScope(projectScope.id)
        .pipe(finalize(() => this.loaderService.hideProgressBar(loader))).subscribe((result: BaseResponseModel) => {
          if(result.code===CONFIG.API_RESPONSE_STATUS.SUCCESS) {
            this.notify.success(this.translate.instant('system.manage_project_scope.dialog.delete_success'))
            this.getAllProjectScopes()
          }else{
            this.notify.error(this.translate.instant('system.manage_project_scope.dialog.delete_failed'))
          }
        });
      }
    })
  }
  truncateText(text: string, num: number = 10) {
    return text?.length > num ? text.slice(0, num) + "..." : text
  }
}
