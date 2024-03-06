import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { finalize } from 'rxjs';
import { ERROR_DUPLICATE_ROLE, ERROR_DUPLICATE_TASK, ProjectRole } from '../../../../model/manage-project-role/manage-project-role.model';
import { AddNewProjectTaskService } from './add-new-project-task.service';
@Component({
  selector: 'eet-add-new-project-task',
  templateUrl: './add-new-project-task.component.html',
  styleUrls: ['./add-new-project-task.component.scss']
})
export class AddNewProjectTaskComponent implements OnInit {
  public projectForm!: FormGroup
  private projectInfo!: ProjectRole;
  public isProject: boolean;
  private isError: boolean;
  constructor(
    public dialogRef: MatDialogRef<AddNewProjectTaskComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private formBuilder: FormBuilder,
    private loaderService: LoadingService,
    private addProjectDataService: AddNewProjectTaskService,
    private notify: NotificationService,
    private translateService: TranslateService,) {
    this.isProject = data['data'].isProject;

    this.projectInfo = {} as ProjectRole;
    this.onBuildForm()
  }

  ngOnInit(): void {
  }

  onBuildForm() {
    this.projectForm = this.formBuilder.group({
      id: this.formBuilder.control(''),
      name: this.formBuilder.control(null, [Validators.required, this.whiteSpaceValidator]),
      project_role_id: this.formBuilder.control(this.data['data'].projectId)
    })
  }

  onSave() {
    //check validation of the form
    if (this.projectForm.invalid) {
      this.projectForm.markAllAsTouched();
      this.projectForm.markAsDirty();
      return;
    }


    const loader = this.loaderService.showProgressBar();
    this.projectForm.controls.name.setValue(this.projectForm.controls.name.value.trim())
    if (this.isProject === true) {
      this.addProjectDataService.addProjectData(this.projectForm.getRawValue())
        .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
        .subscribe((res: any) => {
          if (res.code === ERROR_DUPLICATE_ROLE) { this.projectForm.get('name')?.setErrors({ isDuplicate: true }); }
          else {
            this.notify.success(this.translateService.instant('notification.add_project-role_success'));
            this.dialogRef.close(res);
          }
        })
    }
    else {
      this.addProjectDataService.addCommonTaskData(this.projectForm.getRawValue())
        .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
        .subscribe((res: any) => {
          if (res.code === ERROR_DUPLICATE_TASK) { this.projectForm.get('name')?.setErrors({ isDuplicate: true }); }
          else {
            this.notify.success(this.translateService.instant('notification.add_common-task_success'));
            this.dialogRef.close(res);
          }
        })
    }

  }
  
  isFormValid(){
    return this.projectForm.valid;
  }
  whiteSpaceValidator(control: AbstractControl): {[key: string]: any} | null{
      return control.value && control.value?.trim().length === 0? {whiteSpace: true} : null;
  }
}
