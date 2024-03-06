import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import {
  FormBuilder, FormGroup,
  Validators
} from '@angular/forms';
import { PermisisonService } from 'projects/eet-core-demo/src/app/shared/services/permisison.service';
import { debounceTime, finalize } from 'rxjs/operators';

import { FormControl } from '@angular/forms';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { RoleModel } from 'projects/eet-core-demo/src/app/shared/models/common-model';
import { ProjectModel } from '../../../personal-infomation.model';
import { PersonalInfomationService } from '../../../personal-infomation.service';
import { AddBoschProjectService } from '../dialog/dialog-add-bosch-project/add-bosch-project.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';

@Component({
  selector: 'eet-project-info',
  templateUrl: './project-info.component.html',
  styleUrls: ['./project-info.component.scss']
})
export class ProjectInfoComponent implements OnInit {
  @Output() onSaveChange: EventEmitter<any> = new EventEmitter<any>();
  @Input() isExpand: boolean = false;
  @Input() isEditMode: boolean = false;
  @Input() selectedProject: ProjectModel;
  private isBoschProject: boolean = false;
  private HAS_EDIT_PERSONAL_PERMISSION: boolean = false;
  private HAS_EDIT_ASSOCIATE_INFO_PERMISSION: boolean = false;
  private defaultSelectedRole: string;

  constructor(
    private formBuilder: FormBuilder,
    private personalInfomationService: PersonalInfomationService,
    private permisisonService: PermisisonService,
    private loaderService: LoadingService,
    private addBoschProjectService: AddBoschProjectService
  ) {
    this.onBuildForm();
  }

  form!: FormGroup;
  private submitted: boolean = false;
  date: any;

  public roles!: RoleModel[];
  public gb: string[] = [];

  @Input() projectId!: string;
  @Input() typeUser!: string;
  @Input() isDisable: boolean = false;

  private getProjectType: string = '';
  private getProjectInfoEdit: any = {};
  private selectable: boolean = true;
  private removable: boolean = true;
  private addOnBlur: boolean = true;
  private getUsersPermissions: string = '';

  ngOnInit(): void {
    this.HAS_EDIT_PERSONAL_PERMISSION = this.permisisonService.hasPermission('EDIT_PERSONAL_PERMISSION')
    this.HAS_EDIT_ASSOCIATE_INFO_PERMISSION = this.permisisonService.hasPermission('EDIT_ASSOCIATE_INFO_PERMISSION')
    this.initFormValue();
    this.form.valueChanges
      .pipe(debounceTime(500))
      .subscribe(key => {
        this.selectedProject = {
          ...this.form.getRawValue(),
          role_name: this.roles.find(f => f.id == this.form.value.role_id)?.name
        }
        this.personalInfomationService.setProjectListState(this.selectedProject);
      });

    this.form.get('role_id')?.valueChanges.subscribe(value => {
      if (value && this.defaultSelectedRole != value && this.isBoschProject) {
        this.defaultSelectedRole = value;
        const loader = this.loaderService.showProgressBar();
        this.addBoschProjectService.getCommonTasksByRole(this.defaultSelectedRole)
          .pipe(finalize(() => this.loaderService.hideProgressBar(loader))).subscribe(taskList => {
            this.taskFormControl.setValue(taskList);
          })
      }
    });
    this.roles = this.personalInfomationService.sharedData.value['roleList'];
    this.gb = this.personalInfomationService.sharedData.value['gbList'];
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes?.isEditMode?.currentValue) {
      this.isBoschProject = this.selectedProject.project_type != 'Non-Bosch';
      this.form.disable();
      this.checkPermissionFromControl();
    } else {
      this.form.disable();
    }
  }

  private checkPermissionFromControl() {
    if (this.isBoschProject) {
      if (this.HAS_EDIT_PERSONAL_PERMISSION || this.HAS_EDIT_ASSOCIATE_INFO_PERMISSION) {
        this.form.controls['additional_tasks'].enable();
        if (this.HAS_EDIT_ASSOCIATE_INFO_PERMISSION) {
          this.form.controls['role_id'].enable();
          this.form.controls['gb_unit'].enable();
        }
      }
    } 
    else {
      if (this.typeUser === CONFIG.TYPE_USER.MANAGER && this.HAS_EDIT_PERSONAL_PERMISSION) {
        this.form.controls['role_id'].enable();
        this.form.controls['tasks'].enable();
        this.form.controls['name'].enable();
        this.form.controls['start_date'].enable();
        this.form.controls['end_date'].enable();
        this.form.controls['pm_name'].enable();
        this.form.controls['team_size'].enable();
        this.form.controls['challenge'].enable();
        this.form.controls['objective'].enable();
        this.form.controls['description'].enable();
        this.form.controls['technology_used'].enable();

      }
    }
  }

  onBuildForm() {
    this.form = this.formBuilder.group({
      id: this.formBuilder.control(null),
      name: ['', [Validators.required]],
      start_date: ['', [Validators.required]],
      end_date: ['', [Validators.required]],
      pm_name: ['', [Validators.required]],
      challenge: ['', [Validators.required]],
      team_size: ['', [Validators.required,]],
      role_id: ['', [Validators.required,]],
      tasks: ['', [Validators.required]],
      additional_tasks: this.formBuilder.control(null, [Validators.required]),
      status: ['', [Validators.required]],
      project_type: ['', [Validators.required]],
      gb_unit: ['', [Validators.required]],
      objective: [''],
      description: ['', [Validators.required]],
      referencelink: [''],
      project_id: [null],
      technology_used: ['']
    });
  }

  initFormValue() {
    this.form.patchValue(this.selectedProject);
    this.defaultSelectedRole = this.selectedProject.role_id;
    this.isEditMode ? this.checkPermissionFromControl() : this.form.disable();
  }

  onTaskListChange(list: Array<any>) {
    this.taskFormControl.setValue(list);
  }

  onAdditionalTaskListChange(list: Array<any>) {
    this.additionalTaskFormControl.setValue(list);
  }

  get formControl() {
    return this.form.controls;
  }

  get taskFormControl(): FormControl {
    return this.form?.get('tasks') as FormControl;
  }

  get additionalTaskFormControl(): FormControl {
    return this.form?.get('additional_tasks') as FormControl;
  }

}
