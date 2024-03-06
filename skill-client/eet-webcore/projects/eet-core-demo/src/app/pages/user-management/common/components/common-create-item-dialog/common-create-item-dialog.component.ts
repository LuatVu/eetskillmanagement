import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import * as constants from '../../constants/constants';
import { UserManagementService } from '../../services/user-management.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'eet-common-create-item-dialog',
  templateUrl: './common-create-item-dialog.component.html',
  styleUrls: ['./common-create-item-dialog.component.scss'],
})
export class CommonCreateItemDialogComponent implements OnInit {
  public readonly constants = constants;
  public formData!: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<CommonCreateItemDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private userManagementService: UserManagementService,
    private notify: NotificationService,
    private fb: FormBuilder,
    private translateService:TranslateService
  ) {
    this.formData = this.fb.group({
      name: this.fb.control(null, [Validators.required, Validators.pattern(/^(\s+\S+\s*)*(?!\s).*$/)]),
      description: this.data?.data?.type === constants.TYPE.TYPE_GROUP? this.fb.control(null): this.fb.control(null, [Validators.required]),
    });
  }


  ngOnInit() { }

  close() {
    this.dialogRef.close();
  }

  onSave() {
    if (this.formData.invalid) {
      this.formData.markAllAsTouched();
      this.formData.markAsDirty();
      return;
    }
    if (
      this.userManagementService.createDataItem(
        this.data?.data?.type,
        this.formData.value
      )
    ) {
      Object.keys(this.formData.value).forEach(k => this.formData.value[k] != null ? this.formData.value[k] = this.formData.value[k].trim() : null);
      this.userManagementService
        .createDataItem(this.data?.data?.type, this.formData.value)
        ?.subscribe((res) => {
          if (res?.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
            this.dialogRef.close({
              status: res?.code,
            });
            if(this.data.data?.type === 1) {
              this.notify.success(this.translateService.instant('user_management.dialog.create_user_group_successfully'));
            }else{
              this.notify.success(this.translateService.instant('user_management.dialog.create_new_role_successfully'));
            }
          } else {
            this.notify.error(res?.message);
          }
        }, (err: any) => {
          return err
        });
    }
  }

  get formDataControls() {
    return this.formData.controls;
  }
}
