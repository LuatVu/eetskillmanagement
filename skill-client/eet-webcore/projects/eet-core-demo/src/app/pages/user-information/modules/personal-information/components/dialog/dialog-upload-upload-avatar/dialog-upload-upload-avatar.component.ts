import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { finalize } from 'rxjs/operators';
import { PersonalInfomationService } from '../../../personal-infomation.service';
import { UploadAvatarService } from './upload-avatar.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'eet-dialog-upload-upload-avatar',
  templateUrl: './dialog-upload-upload-avatar.component.html',
  styleUrls: ['./dialog-upload-upload-avatar.component.scss'],
})
export class DialogUploadUploadAvatarComponent implements OnInit {
  private files!: File;
  private MAX_FILE_SIZE: number = 16; // Max is 16Mb
  @ViewChild('fileUpload') fileUpload!: ElementRef;

  constructor(
    private uploadAvatarService: UploadAvatarService,
    private personalInfomationService: PersonalInfomationService,
    private notifyService: NotificationService,
    private dialogCommonService: DialogCommonService,
    private dialogRef: MatDialogRef<DialogUploadUploadAvatarComponent>,
    private loaderService: LoadingService,
    private translateService: TranslateService
  ) { }

  ngOnInit(): void {
  }

  onChange(event: any) {
    if (event.target.files && event.target.files[0]) {
      const file = event.target.files[0];
      if (file.size / 1024 > this.MAX_FILE_SIZE * 1024) {
        this.notifyService.error("Max file size is: " + this.MAX_FILE_SIZE + "mb.")
        this.fileUpload.nativeElement.value = "";
        return;
      }
      this.files = event.target.files[0];
    }
  }
  upload() {
    const loader = this.loaderService.showProgressBar();
    this.uploadAvatarService.uploadAvatar(this.personalInfomationService._idUser, this.files)
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader))).subscribe(
        (response: any) => {
          this.personalInfomationService.setPersonalInfoDetail({
            picture: response?.picture
          });
          this.notifyService.success(this.translateService.instant('personal_information.dialog.upload_avatar.upload_success'));
          this.dialogRef.close();
        }
      );
  }

  get isDisabledUploadButton(): boolean {
    return !this.files;
  }

}
