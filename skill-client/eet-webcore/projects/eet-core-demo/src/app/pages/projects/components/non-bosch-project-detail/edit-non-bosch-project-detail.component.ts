import { HttpErrorResponse } from '@angular/common/http';
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
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { RoleModel } from 'projects/eet-core-demo/src/app/shared/models/common-model';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { catchError, finalize } from 'rxjs/operators';
import { ProjectDetailNonBosch } from '../../models/dialog-data/project-detail-non-bosch.model';
import { ProjectsService } from '../../services/projects.service';
import { DatePickerHeader } from './../../../../shared/components/datepicker-header/datepicker-header.component';

@Component({
  selector: 'eet-non-bosch-project-detail',
  templateUrl: '../../../user-information/modules/personal-information/assinged-project/components/dialog/dialog-add-non-bosch-project/dialog-add-non-bosch-project.component.html',
  styleUrls: ['../../../user-information/modules/personal-information/assinged-project/components/dialog/dialog-add-non-bosch-project/dialog-add-non-bosch-project.component.scss']
})
export class EditNonBoschProjectDetailComponent implements OnInit {
  public readonly DATE_PICKER_HEADER = DatePickerHeader;

  projectForm!: FormGroup;
  selectrole = 'Select Roles';
  projectinfo!: ProjectDetailNonBosch;
  submitted: boolean = false;
  date: any;
  private isDisable: boolean = true;
  public roles!: RoleModel[];

  constructor(
    public dialogRef: MatDialogRef<EditNonBoschProjectDetailComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private projectInfoService: ProjectsService,
    private addNonBoschProjectService: ProjectsService,
    private formBuilder: FormBuilder,
    private matIconRegistry: MatIconRegistry,
    private sanitizer: DomSanitizer,
    private loaderService: LoadingService,
    private personalInfomationService: ProjectsService,
    private notification: NotificationService
  ) {
    this.projectinfo = {} as ProjectDetailNonBosch;
    this.projectForm = this.formBuilder.group({
      id: this.formBuilder.control(''),
      name: this.formBuilder.control(null, [Validators.required]),
      start_date: this.formBuilder.control(null, [Validators.required]),
      end_date: this.formBuilder.control(null, [Validators.required]),
      pm_name: this.formBuilder.control(null, [Validators.required]),
      challenge: this.formBuilder.control(null, [Validators.required]),
      role: this.formBuilder.control(null, [Validators.required]),
      role_id: this.formBuilder.control(null, [Validators.required]),
      tasks: this.formBuilder.control(null, [Validators.required]),
      status: this.formBuilder.control({ value: 'New', disabled: true }, [Validators.required]),
      project_type: this.formBuilder.control({ value: 'Non-bosch', disabled: true }, [Validators.required]),
      objective: this.formBuilder.control(null, [Validators.required]),
      technology_used: this.formBuilder.control(null),
      team_size: this.formBuilder.control(null),
      description: this.formBuilder.control(null, [Validators.required])
    })
  }

  ngOnInit(): void {
    this.onBuildForm();
  }

  getprojectRole() {
    this.addNonBoschProjectService.getProjectRole().subscribe((response) => {
      this.roles = response as RoleModel[]
    })
  }
  private id: RoleModel
  onBuildForm() {
    const loader = this.loaderService.showProgressBar()
    this.addNonBoschProjectService.getProjectRole().pipe(finalize(() => {
    })).subscribe((response) => {
      this.roles = response as RoleModel[]
      this.addNonBoschProjectService.getProjectDetail(this.data.data.project_id).pipe(finalize(() => {
        this.loaderService.hideProgressBar(loader)
      })).subscribe((response) => {
        const filteredRolesByName = this.roles.filter((val) => {
          return val.name === response.data.role;
        })
        if (filteredRolesByName.length > 1) {
          console.warn("Multiple 'role_id' with the same role name exist!")
        }
        this.id = filteredRolesByName[0]
        this.projectForm = this.formBuilder.group({
          id: this.formBuilder.control(this.data.data.project_id),
          name: this.formBuilder.control(response.data.name, [Validators.required]),
          start_date: this.formBuilder.control(response.data.start_date, [Validators.required]),
          end_date: this.formBuilder.control(response.data.end_date, [Validators.required]),
          pm_name: this.formBuilder.control(response.data.pm_name, [Validators.required]),
          challenge: this.formBuilder.control(response.data.challenge, [Validators.required]),
          role: this.formBuilder.control(response.data.role, [Validators.required]),
          role_id: this.formBuilder.control(this.id || null, [Validators.required]),
          tasks: this.formBuilder.control(response.data.tasks || [], [Validators.required]),
          status: this.formBuilder.control({ value: response.data.status, disabled: true }, [Validators.required]),
          technology_used: this.formBuilder.control(response.data.technology_used),
          team_size: this.formBuilder.control(response.data.team_size),
          project_type: this.formBuilder.control({ value: 'Non-bosch', disabled: true }, [Validators.required]),
          project_type_id: this.formBuilder.control(response.data.project_type_id),
          objective: this.formBuilder.control(response.data.objective, [Validators.required]),
          description: this.formBuilder.control(response.data.description, [Validators.required])
        })
      })
    })

  }

  onSave() {
    // this.projectForm.controls["role"].setValue(this.roles.filter((value) => {
    //   return value.id === this.projectForm.controls["role_id"].value
    // })[0].id)
    // let task: string[] = []
    // for (let i = 0; i < this.projectForm.controls["tasks"].value.length; i++) {
    //   task.push(this.projectForm.controls["tasks"].value[i].name);
    // }
    // this.projectForm.controls["tasks"].setValue(task);
    const loader = this.loaderService.showProgressBar();
    this.addNonBoschProjectService.putProjects(this.data.data.project_id, this.projectForm.getRawValue())
      .pipe(
        finalize(() => this.loaderService.hideProgressBar(loader)),
        catchError((error: HttpErrorResponse) => {
          this.notification.error(error.error.message);
          return error.error;
        })
      )
      .subscribe((res: any) => {
        this.dialogRef.close(res);
      })
  }

  get formControls(): any {
    return this.projectForm.controls;
  }

  onTaskListChange(list: Array<any>) {
    this.taskFormControl.setValue(list);
  }

  get taskFormControl(): FormControl {
    return this.projectForm?.get('tasks') as FormControl;
  }
}
