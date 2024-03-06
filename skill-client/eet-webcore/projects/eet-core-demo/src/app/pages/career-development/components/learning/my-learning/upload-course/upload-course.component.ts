import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { finalize, take } from 'rxjs/operators';
import { UploadCourseService } from './upload-course.service';

@Component({
  selector: 'eet-upload-course',
  templateUrl: './upload-course.component.html',
  styleUrls: ['./upload-course.component.scss'],
})
export class UploadCourseComponent implements OnInit {
  public files!: File
  @ViewChild('fileUpload') fileUpload!: ElementRef
  constructor(private uploadCourseService: UploadCourseService,
    private loader: LoadingService,
    private notification: NotificationService,
    private dialogRef: MatDialogRef<UploadCourseComponent>) { }

  public isUserUploaded: boolean = false;
  public errorList: string[] = []
  ngOnInit(): void { }

  onChange(event: any) {
    if (event.target.files && event.target.files[0]) {
      this.files = event.target.files[0]
      this.isUserUploaded = true
    }
  }
  onImport() {
    const comLoader = this.loader.showProgressBar()
    this.uploadCourseService.postCourseUpload(this.files).pipe(take(1), finalize(() => this.loader.hideProgressBar(comLoader))).subscribe((response: any) => {
      if (response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
        this.dialogRef.close(true)
      }
      else {
        this.errorList.push(response.message || response.code)
      }
    }
    )
  }

  onDownloadTemplate() {
    const comLoader = this.loader.showProgressBar()
    this.uploadCourseService.getExcelTemplate().pipe(finalize(() => this.loader.hideProgressBar(comLoader))).subscribe((file) => {
      if (file) {
        let FileSaver = require('file-saver');
        const blob = new Blob([file], {
          type: 'application/force-download'
        });
        FileSaver.saveAs(blob, 'upload-course-template.xlsx')
      }
    })
  }
  /*
  export() {
   const loader = this.loader.showProgressBar();
   this.personalInfomationService.getExport(this.personalInfoDetail.id)
     .pipe(finalize(() => this.loader.hideProgressBar(loader)))
     .subscribe((data) => {
       if (data) {
         var FileSaver = require('file-saver');
         const blob = new Blob([data], {
           type: 'application/pdf',
         });
         FileSaver.saveAs(blob, this.personalInfoDetail.name.toString() + '.pdf');
       }
     });
 }
 */
}
