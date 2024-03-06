import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ColorPickerService, Hsva } from 'ngx-color-picker';
import { ManageProjectRoleService } from '../../../pages/manage-project-role/manage-project-role.service';
import { ManageProjectScopeService } from '../../../../services/manage-project-scope.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { finalize } from 'rxjs/operators';
import { BaseResponseModel } from 'projects/eet-core-demo/src/app/shared/models/base.model';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { NotificationService } from '@bci-web-core/core';
import { TranslateService } from '@ngx-translate/core';
import { DEFAULT_COLOUR_MIXED_PROJECT_SCOPE, DEFAULT_COLOUR_PROJECT_SCOPE } from '../../../../model/manage-project-scope/manage-project-scope.constant';
import { ProjectScope } from 'projects/eet-core-demo/src/app/pages/projects/models/projects.model';
import { PROJECT_CARD_MOCK_DATA } from '../../../../model/manage-project-scope/mock-data';

@Component({
  selector: 'eet-manage-project-scope-dialog',
  templateUrl: './manage-project-scope-dialog.component.html',
  styleUrls: ['./manage-project-scope-dialog.component.scss']
})
export class ManageProjectScopeDialogComponent implements OnInit {
  public projectScopeFormGroup!: FormGroup
  public colorPicker: string = DEFAULT_COLOUR_PROJECT_SCOPE;
  public colorPickerHover: string = DEFAULT_COLOUR_MIXED_PROJECT_SCOPE
  
  public colorPickerMixed: string = this.manageProjectScopeService.linearBackgroundProjectCard(this.colorPickerHover,this.colorPicker)
  
  public rgbaText: string = ''
  public rgbaTextColorPickerMixed: string = ''
  public readonly COLOR ='color'
  public readonly COLORHOVER ='colorHover'
  query = ''
  mock = PROJECT_CARD_MOCK_DATA

  constructor(
    @Inject(MAT_DIALOG_DATA) public passingData: any,
    private formBuilder: FormBuilder,
    private cpService: ColorPickerService,
    private manageProjectScopeService: ManageProjectScopeService,
    private loaderService: LoadingService,
    public dialogRef: MatDialogRef<ManageProjectScopeDialogComponent>,
    private notify: NotificationService,
    private translate: TranslateService
  ) {
    if (this.passingData.data.type === 'add') {
      const initProjectScopeData: ProjectScope = {
        id: '',
        name: '',
        colour: this.colorPicker,
        hover_colour: this.colorPickerHover
      }
      this.onBuildForm(initProjectScopeData)
    } else if (this.passingData.data.type === 'edit') {
      const loader = this.loaderService.showProgressBar();
      const currentProjectScope: ProjectScope = this.passingData.data.projectScope

      this.onBuildForm({
        id: currentProjectScope.id,
        name: currentProjectScope.name,
        colour: currentProjectScope.colour,
        hover_colour: currentProjectScope.hover_colour
      })
      this.colorPicker = currentProjectScope.colour || DEFAULT_COLOUR_PROJECT_SCOPE
      this.colorPickerHover = currentProjectScope.hover_colour || DEFAULT_COLOUR_MIXED_PROJECT_SCOPE
      this.colorPickerMixed = this.manageProjectScopeService.linearBackgroundProjectCard(this.projectScopeFormGroup.controls.hover_colour?.value,this.projectScopeFormGroup.controls.colour?.value)
      
      this.loaderService.hideProgressBar(loader)
    }
  }

  ngOnInit(): void {
    this.projectScopeFormGroup = this.formBuilder.group({
      id: this.formBuilder.control(this.projectScopeFormGroup.controls.id?.value),
      name: this.formBuilder.control(this.projectScopeFormGroup.controls.name?.value, [Validators.required, this.textValidator]),
      colour: this.formBuilder.control(this.projectScopeFormGroup.controls.colour?.value, [Validators.required]),
      hover_colour: this.formBuilder.control(this.projectScopeFormGroup.controls.hover_colour?.value, [Validators.required]),

    })
  }
  textValidator(control: AbstractControl): { [key: string]: any } | null {
    if (!control.value) return null
    if (control.value.trim() !== '') {
      return null
    }
    return { 'invalidTextValidator': true };
  }
  onBuildForm(data: ProjectScope) {
    this.projectScopeFormGroup = this.formBuilder.group({
      id: this.formBuilder.control(data.id || null),
      name: this.formBuilder.control(data.name || null, [Validators.required, this.textValidator]),
      colour: this.formBuilder.control(data.colour || this.colorPicker, [Validators.required]),
      hover_colour: this.formBuilder.control(data.hover_colour || this.colorPickerHover, [Validators.required]),
    })
  }
  onSave() {
    this.projectScopeFormGroup.controls.name?.setValue(this.projectScopeFormGroup.controls.name.value?.trim())
    const loader = this.loaderService.showProgressBar();

    if (this.passingData.data.type === 'add') {
      this.manageProjectScopeService.createNewProjectScope(this.projectScopeFormGroup.getRawValue())
        .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
        .subscribe((result: BaseResponseModel) => {
          this.dialogRef.close()
          if (result.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
            this.manageProjectScopeService.updateProjectScopeEvent(true)
            this.notify.success(this.translate.instant('system.manage_project_scope.dialog.add_success'));
          } else {
            this.notify.error(this.translate.instant('system.manage_project_scope.dialog.add_failed'));
          }
        });
    } else if (this.passingData.data.type === 'edit') {
      this.manageProjectScopeService.updateProjectScope(this.projectScopeFormGroup.getRawValue())
        .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
        .subscribe((result: BaseResponseModel) => {
          this.dialogRef.close()
          if (result.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
            this.manageProjectScopeService.updateProjectScopeEvent(true)
            this.notify.success(this.translate.instant('system.manage_project_scope.dialog.update_success'));
          } else {
            this.notify.error(this.translate.instant('system.manage_project_scope.dialog.update_failed'));
          }

        });
    }

  }
  onEventLog(type: string, data: any) {
    if (data?.color) {
      if (type === this.COLOR) {
        this.projectScopeFormGroup.controls.colour?.setValue(data.color)
      } else if (type === this.COLORHOVER) {
        this.projectScopeFormGroup.controls.hover_colour?.setValue(data.color)
      }
    } else {
      if (type === this.COLOR) {
        this.projectScopeFormGroup.controls.colour?.setValue(data)
      } else if (type === this.COLORHOVER) {
        this.projectScopeFormGroup.controls.hover_colour?.setValue(data)
      }
    }
    this.colorPickerMixed = this.manageProjectScopeService.linearBackgroundProjectCard(this.projectScopeFormGroup.controls.hover_colour?.value,this.projectScopeFormGroup.controls.colour?.value)
  }
  onChangeColorHex8(color: string): string {
    const hsva: Hsva | null = this.cpService.stringToHsva(color, true);
    if (hsva !== null) {
      return this.cpService.outputFormat(hsva, 'rgba', null);
    } else {
      return "null"
    }
  }

}
