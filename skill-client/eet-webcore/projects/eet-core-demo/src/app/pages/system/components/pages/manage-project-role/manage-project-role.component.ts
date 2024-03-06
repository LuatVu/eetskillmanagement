import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { ReplaySubject, finalize } from 'rxjs';
import { DataSender, ERROR_USED, ProjectCommonTask, ProjectRole } from '../../../model/manage-project-role/manage-project-role.model';
import { AddNewProjectTaskComponent } from '../../dialogs/manage-project-role/add-new-project-task/add-new-project-task.component';
import { NoticeMessageDialogComponent } from '../../dialogs/manage-project-role/notice-message-dialog/notice-message-dialog.component';
import { ManageProjectRoleService } from './manage-project-role.service';
@Component({
  selector: 'eet-manage-project-role',
  templateUrl: './manage-project-role.component.html',
  styleUrls: ['./manage-project-role.component.scss']
})


export class ManageProjectRoleComponent implements OnInit {
  public projectInfo!: ProjectRole
  public isChosen: boolean = false;
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  private dataSender: DataSender[];
  public selectedOption: any;

  constructor(
    public dialog: DialogCommonService,
    public translate: TranslateService,
    public deleteDialog: MatDialog,
    private loading: LoadingService,
    private notify: NotificationService,
    private translateService: TranslateService,
    private manageProjectRoleService: ManageProjectRoleService
  ) {
    this.projectInfo = {
      id: "", displayName: ""
    };
    this.dataSender = []
  }

  ngOnInit(): void {
    this.getProjectList();
  }

  public projectRoleList: ProjectRole[] = []
  public projectCommonTaskList: ProjectCommonTask[] = []


  onSelectItemInList(project: ProjectRole) {
    this.isChosen = true;
    this.getCommonTaskList(project.id)
    this.projectInfo = project;
    this.selectedOption = project;
  }

  getProjectList() {
    const loader = this.loading.showProgressBar();

    this.manageProjectRoleService.getProjectData().pipe(finalize(() => {
      this.loading.hideProgressBar(loader)
    })).subscribe((data) => {
      this.projectRoleList = data;
    })
  }

  getCommonTaskList(id: string) {
    const loader = this.loading.showProgressBar();

    this.manageProjectRoleService.getCommonTaskData(id).pipe(finalize(() => {
      this.loading.hideProgressBar(loader)
    })).subscribe((data) => {

      this.projectCommonTaskList = data;
      this.projectCommonTaskList = this.projectCommonTaskList.filter(element => element.name.trim() !== "");
      this.projectCommonTaskList.map(element => { element.project_role_id = id })
      
    })
  }


  addProjectRole() {
    const dialogRef = this.dialog.onOpenCommonDialog({
      component: AddNewProjectTaskComponent,
      title: 'system.manage_project_role.dialog.add_project',
      width: "60vw",
      height: 'auto',
      type: 'edit',
      passingData: {
        isProject: true
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.getProjectList();
      }
    });
  }

  addCommonTask(projectInfo: ProjectRole) {
    if (this.isChosen === false) {
      const dialogRef = this.dialog.onOpenCommonDialog({
        component: NoticeMessageDialogComponent,
        title: 'system.manage_project_role.dialog.error',
        width: "40vw",
        height: 'auto',
        type: 'edit',
        passingData: {
          message: this.translateService.instant('notification.not_select_role')
        }
      });
    }
    else {
      const dialogRef = this.dialog.onOpenCommonDialog({
        component: AddNewProjectTaskComponent,
        title: 'system.manage_project_role.dialog.add_common_task',
        width: "60vw",
        height: 'auto',
        type: 'edit',
        passingData: {
          isProject: false,
          projectId: projectInfo.id
        }
      });
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.getCommonTaskList(projectInfo.id);
        }
      });
    }
  }

  onDeleteProjectRole(project: ProjectRole) {
    this.dataSender.push(project)
    const loader = this.loading.showProgressBar();
    this.manageProjectRoleService.deleteProjectData(this.dataSender)
      .pipe(finalize(() => this.loading.hideProgressBar(loader)))
      .subscribe((res) => {
        if (res.code === ERROR_USED) {
          this.notify.error(this.translateService.instant("notification.delete_project_in_use"))
        }
        else {
          this.notify.success(this.translateService.instant("notification.delete_project-role_success"))
          this.getProjectList();
          this.isChosen = false;
          this.projectCommonTaskList = [];
          this.dataSender = []
        }
      })
  }
  onResize(element: any) {
    element.target.style.height = '0'
    element.target.style.height = (element.target.scrollHeight + 6) + 'px'
  }

  onDeleteCommonTask(task: ProjectCommonTask) {
    this.dataSender.push(task)
    const loader = this.loading.showProgressBar();
    this.manageProjectRoleService.deleteCommonTask(this.dataSender)
      .pipe(finalize(() => this.loading.hideProgressBar(loader)))
      .subscribe((res) => {
        this.getProjectList();
        this.getCommonTaskList(task.project_role_id);
        this.notify.success(this.translateService.instant('notification.delete_common-task_success'))
        this.dataSender = []

      })

  }

}
