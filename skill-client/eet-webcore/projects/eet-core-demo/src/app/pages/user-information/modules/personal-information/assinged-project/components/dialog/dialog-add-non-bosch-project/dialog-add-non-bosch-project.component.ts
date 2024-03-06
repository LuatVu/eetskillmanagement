import { DatePickerHeader } from './../../../../../../../../shared/components/datepicker-header/datepicker-header.component';
import { Component, Inject, LOCALE_ID } from '@angular/core';
import {
  FormBuilder, FormGroup,
  Validators
} from '@angular/forms';

import { FormControl } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { RoleModel } from 'projects/eet-core-demo/src/app/shared/models/common-model';
import { finalize } from 'rxjs/operators';
import { ProjectModel } from '../../../../personal-infomation.model';
import { PersonalInfomationService } from '../../../../personal-infomation.service';
import { ProjectInfoService } from '../../project-info/project-info.service';
import { AddnonBoschProjectService } from './add-non-bosch-project.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';

@Component({
  selector: 'eet-dialog-project',
  templateUrl: './dialog-add-non-bosch-project.component.html',
  styleUrls: ['./dialog-add-non-bosch-project.component.scss'],
})
export class DialogNonBoschProjectComponent {
  public readonly DATE_PICKER_HEADER = DatePickerHeader;

  public projectForm!: FormGroup;
  private selectrole: string = 'Select Roles';
  private projectinfo!: ProjectModel;
  private submitted: boolean = false;
  date: any;
  private isDisable: boolean = true;
  public roles!: RoleModel[];

  constructor(
    public dialogRef: MatDialogRef<DialogNonBoschProjectComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private projectInfoService: ProjectInfoService,
    private addNonBoschProjectService: AddnonBoschProjectService,
    private formBuilder: FormBuilder,
    private matIconRegistry: MatIconRegistry,
    private sanitizer: DomSanitizer,
    private loaderService: LoadingService,
    private personalInfomationService: PersonalInfomationService,
    @Inject(LOCALE_ID) private locale: string
  ) {
    this.projectinfo = {} as ProjectModel;
    this.onBuildForm();
  }

  ngOnInit(): void {
    this.getprojectRole();
  }

  getprojectRole() {
    this.personalInfomationService.getSharedData()
      .subscribe(data => {
        if (data['roleList']) {
          this.roles = data['roleList'] || [];
        }
      })
  }

  onBuildForm() {
    this.projectForm = this.formBuilder.group({
      id: this.formBuilder.control('', [Validators.required]),
      name: this.formBuilder.control(null, [Validators.required]),
      start_date: this.formBuilder.control(null, [Validators.required]),
      end_date: this.formBuilder.control(null, [Validators.required]),
      pm_name: this.formBuilder.control(null, [Validators.required]),
      challenge: this.formBuilder.control(null, [Validators.required]),
      role_id: this.formBuilder.control(null, [Validators.required]),
      tasks: this.formBuilder.control("", [Validators.required]),
      status: this.formBuilder.control({ value: 'New', disabled: true }, [Validators.required]),
      project_type: this.formBuilder.control({ value: 'Non-bosch', disabled: true }, [Validators.required]),
      objective: this.formBuilder.control(null, [Validators.required]),
      description: this.formBuilder.control(null, [Validators.required]),
      technology_used: this.formBuilder.control(null),
      team_size: this.formBuilder.control(null)
    })
  }

  onSave() {
    const loader = this.loaderService.showProgressBar();
    this.addNonBoschProjectService.AddProject(this.personalInfomationService._idUser,
      this.projectForm.getRawValue()
    )
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe((res: any) => {
        this.dialogRef.close(res);
      })
  }


  getDate(): any { //the offset between the user local timezone with UTC. In my use case of Singapore, it give me -480.

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
