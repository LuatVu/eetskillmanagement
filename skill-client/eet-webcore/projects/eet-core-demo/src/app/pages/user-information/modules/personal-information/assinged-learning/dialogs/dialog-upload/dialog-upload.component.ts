import { Component, ElementRef, Inject, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { PersonalInfomationService } from '../../../personal-infomation.service';
import { UploadCertificateService } from './upload-certificate.service';

@Component({
  selector: 'eet-dialog-upload',
  templateUrl: './dialog-upload.component.html',
  styleUrls: ['./dialog-upload.component.scss'],
})
export class DialogUploadComponent implements OnInit {
  @ViewChild('fileUpload') fileUpload!: ElementRef;
  private files!: File;
  private MAX_FILE_SIZE: number = 60; // 60Kb

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private uploadCertificateService: UploadCertificateService,
    private personalInfomationService: PersonalInfomationService,
    private notifyService: NotificationService,
    private dialogRef: MatDialogRef<DialogUploadComponent>
  ) { }

  ngOnInit(): void { }
  onChange(event: any) {

    if (event.target.files && event.target.files[0]) {
      const file = event.target.files[0];
      if (file.size / 1024 > this.MAX_FILE_SIZE) {
        this.notifyService.error("Max file size is: " + this.MAX_FILE_SIZE + "kb.")
        this.fileUpload.nativeElement.value = "";
        return;
      }
      this.files = event.target.files[0];
    }
  }
  upload() {
    this.uploadCertificateService.uploadCeritifcate(
      this.personalInfomationService._idUser,
      this.data['data'].id_course, this.files)
      .subscribe(res => {
        this.notifyService.success("Upload success");
        this.dialogRef.close(res);
      })
  }

  get isDisabledUploadButton(): boolean {
    return !this.files;
  }

}
