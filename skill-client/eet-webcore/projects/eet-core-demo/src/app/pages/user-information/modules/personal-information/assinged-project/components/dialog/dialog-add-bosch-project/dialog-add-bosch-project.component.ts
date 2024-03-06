import {
  Component, Inject,
  OnInit
} from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators
} from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { RoleModel } from 'projects/eet-core-demo/src/app/shared/models/common-model';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { finalize } from 'rxjs/operators';
import { ProjectModel } from '../../../../personal-infomation.model';
import { PersonalInfomationService } from '../../../../personal-infomation.service';
import { AddBoschProjectService } from './add-bosch-project.service';
import { ProjectsService } from 'projects/eet-core-demo/src/app/pages/projects/services/projects.service';
import { PersonalProjectService } from '../../../personal-project.service';
import { Observable, take } from 'rxjs';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { TranslateService } from '@ngx-translate/core';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'eet-dialog-add-bosch-project',
  templateUrl: './dialog-add-bosch-project.component.html',
  styleUrls: ['./dialog-add-bosch-project.component.scss'],
})
export class DialogBoschProjectComponent implements OnInit {
  public form!: FormGroup;
  public roles!: RoleModel[];
  public projectList: ProjectModel[] = [];
  public projectHistorySelected:ProjectModel[] = []
  constructor(
    public dialogRef: MatDialogRef<DialogBoschProjectComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private addBoschProjectService: AddBoschProjectService,
    private formBuilder: FormBuilder,
    private personalInfomationService: PersonalInfomationService,
    private loaderService: LoadingService,
    public projectsService: ProjectsService,
    private personalProjectService: PersonalProjectService,
    private notificationService: NotificationService,
    public translate: TranslateService,
  ) {
  }

  originalId: string;

  disableSelect = new FormControl(this.data.data.type==='edit-personal');

  ngOnInit(): void {
    this.getprojectRole();
    this.getBoschproject();
    this.onBuildForm();

    if (this.data.data.type.includes('edit')) {
      const loader = this.loaderService.showProgressBar()
      this.projectsService.getProjectDetail(this.data.data.project.project_id).pipe(
        finalize(() => {
          this.loaderService.hideProgressBar(loader)
        })
      ).subscribe((response: any) => {
        const selectedUser = response.data?.members.find((member: { id: string; }) => member.id === this.personalInfomationService._idUser);
        this.form = this.formBuilder.group({
          name: this.formBuilder.control(response.data? response.data.name : 'a', [Validators.required]),
          id: this.formBuilder.control(''),
          personal_id: this.formBuilder.control(this.personalInfomationService._idUser),
          project_id: this.formBuilder.control(response.data ? response.data.project_id : null, [Validators.required]),
          role_id: this.formBuilder.control(response.data? selectedUser.role_id : null, [Validators.required]),
          role_name: this.formBuilder.control(response.data? selectedUser.role : null, [Validators.required]),
          tasks: this.formBuilder.control([]),
          project_type: this.formBuilder.control('Bosch', [Validators.required]),
          additional_tasks: this.formBuilder.control(selectedUser.additional_task ? selectedUser.additional_task : ''),
          member_start_date:this.formBuilder.control(this.data.data.project.member_start_date? this.data.data.project.member_start_date : '',[Validators.required]),
          member_end_date:this.formBuilder.control(this.data.data.project.member_end_date? this.data.data.project.member_end_date : ''),
        });
        this.watchOnProjectRoleChange();
        this.form.get("project_id")?.setValue(response.data.project_id);
        this.addBoschProjectService.getCommonTasksByRole( selectedUser.role_id ).subscribe(taskList => {
          this.taskFormControl.setValue(taskList);
        })
        this.personalProjectService.getPersonProject(this.personalInfomationService._idUser)
        .subscribe(data => {
            const project = data.find((p: { project_id: string; }) => p.project_id ===  response.data.project_id);
            this.originalId = project.id;
        })
        this.onProjectChosen({value:response.data ? response.data.project_id : null})
      })
    }
  }


  private getBoschproject() {
    const loader = this.loaderService.showProgressBar();
    this.addBoschProjectService
      .getBoschProjectForDropdown()
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe(data => {
      this.projectList = data;
    })
  }

  async onEditAdditionalTask() {
    const body = {
      "id": this.originalId,
      "additional_tasks": this.form.value.additional_tasks,
      "role_id": this.form.value.role_id,
      "project_id": this.form.value.project_id,
      "member_start_date":Helpers.parseDateTimeToString(new Date(this.form.value.member_start_date)) ,
      "member_end_date":this.form.value.member_end_date ? Helpers.parseDateTimeToString(new Date(this.form.value.member_end_date)) : '',
    }
    const loader = this.loaderService.showProgressBar();
    this.addBoschProjectService.editAdditionalTask(body)
    .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
    .subscribe((res) => {
      if (res) {
        this.dialogRef.close(true);
      }
    })
  }

  onSave() {
    const loader = this.loaderService.showProgressBar();
    this.addBoschProjectService
      .addProject(this.personalInfomationService._idUser, this.form.value)
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe((res) => {
        if (res) {
          this.dialogRef.close(true);
        } 
      })
  }

  private onBuildForm() {
    this.form = this.formBuilder.group({
      name: this.formBuilder.control(''),
      id: this.formBuilder.control(''),
      personal_id: this.formBuilder.control(this.personalInfomationService._idUser),
      project_id: this.formBuilder.control(null, [Validators.required]),
      role_id: this.formBuilder.control(null, [Validators.required]),
      role_name: this.formBuilder.control(null, [Validators.required]),
      tasks: this.formBuilder.control([]),
      project_type: this.formBuilder.control('Bosch', [Validators.required]),
      additional_tasks: this.formBuilder.control(''),
      member_start_date:this.formBuilder.control('',[Validators.required]),
      member_end_date:this.formBuilder.control('')

    });
    this.watchOnProjectRoleChange();
  }


  private setCommonTask(roleId: string) {
      const loader = this.loaderService.showProgressBar();
      this.addBoschProjectService.getCommonTasksByRole(roleId)
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader))).subscribe(taskList => {
          this.taskFormControl.setValue(taskList);
    })
  }


  private watchOnProjectRoleChange() {
    this.form.get('role_id')?.valueChanges.subscribe(roleId => {
      this.form.get('role_name')?.setValue(this.roles.find(f => f.id == roleId)?.name);
      const loader = this.loaderService.showProgressBar();
      this.addBoschProjectService.getCommonTasksByRole(roleId)
        .pipe(finalize(() => this.loaderService.hideProgressBar(loader))).subscribe(taskList => {
          this.taskFormControl.setValue(taskList);
        })
    })
  }

  private getprojectRole() {
    this.personalInfomationService.getSharedData()
      .subscribe(data => {
        if (data['roleList']) {
          this.roles = data['roleList'] || [];
          this.roles = this.roles.sort((a: RoleModel, b: RoleModel) => a.name.localeCompare(b.name))
        }
      })
  }


  get taskFormControl(): FormControl {
    return this.form.get('tasks') as FormControl;
  }

  get additionalTaskFormControl(): FormControl {
    return this.form.get('additional_tasks') as FormControl;
  }

  get projectFormControl(): FormControl {
    return this.form.get('project_id') as FormControl;
  }

  onTaskListChange(list: Array<any>) {
    this.taskFormControl.setValue(list);
  }

  onAdditionalTaskListChange(list: Array<any>) {
    this.additionalTaskFormControl.setValue(list);
  }

  projectIdHistory = null;
  onProjectChosen(event: any){
      this.projectIdHistory = event.value
      this.projectHistorySelected = (this.data['data'].personalProjectList as Array<ProjectModel>).filter(element => element.project_id == event.value)

      if(this.data.data.type.includes('edit')) {
        this.projectHistorySelected = this.projectHistorySelected.filter((e) => e.id !== this.data.data?.project.id)
      }
  }
  filterMemberStartDay = (d: Date | null): boolean => {
    const pj_startDate = new Date(this.projectHistorySelected[0]?.start_date)
    const pj_endDate =this.projectHistorySelected[0]?.end_date ? new Date(this.projectHistorySelected[0]?.end_date) : ''
    const day = (d || new Date());
    day.setHours(7)
    if ((day <= pj_startDate) || (pj_endDate && day > pj_endDate)) {
      return false
    }
    for (let i = 0; i < this.projectHistorySelected.length; i++) {
      if (day >= new Date(this.projectHistorySelected[i].member_start_date) && day <  new Date(this.projectHistorySelected[i].member_end_date)) return false
      if(day.getTime() === new Date(this.projectHistorySelected[i].member_start_date).getTime() && !this.projectHistorySelected[i].member_end_date ) return false
    }
    return true
  };

  filterMemberEndDay = (d: Date | null): boolean => {
    const pj_startDate = new Date(this.projectHistorySelected[0]?.start_date)
    const pj_endDate =this.projectHistorySelected[0]?.end_date ? new Date(this.projectHistorySelected[0]?.end_date) : ''
    const day = (d || new Date());
    day.setHours(7)
    if (!this.form?.getRawValue()?.member_start_date) return false

    let tmp: Date | null = null
    for (const record of this.projectHistorySelected) {
      if (new Date(record.member_start_date) > new Date(this.form?.getRawValue()?.member_start_date)) {
        if (tmp === null || new Date(record.member_start_date) < tmp) {
          tmp = new Date(record.member_start_date)
        }
      }
    }
    if (tmp && day > tmp) {
      return false
    }

    if ((day <= pj_startDate) || (pj_endDate && day > pj_endDate)) {
      return false
    }
    if (day <= new Date(this.form.controls.member_start_date.value)) return false

    return true
  }
}
