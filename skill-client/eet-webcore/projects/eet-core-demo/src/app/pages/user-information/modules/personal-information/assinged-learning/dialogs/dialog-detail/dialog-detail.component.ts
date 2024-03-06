import { Component, Inject, Input, OnInit } from '@angular/core';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { finalize } from 'rxjs/operators';
import { DialogCommonService } from '../../../../../../../shared/services/dialog-common.service';
import { PersonalInfomationService } from '../../../personal-infomation.service';
import { PersonalLeanring } from '../../assinged-learning.component';
import { DialogDetailService } from './dialog-detail.service';

@Component({
  selector: 'eet-dialog-detail',
  templateUrl: './dialog-detail.component.html',
  styleUrls: ['./dialog-detail.component.scss'],
})
export class DialogDetailComponent implements OnInit {
  public detailcourse!: PersonalLeanring;

  constructor(
    public dialog: MatDialog,
    private dialogDetailService: DialogDetailService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private personalInfomationService: PersonalInfomationService,
    private loaderService: LoadingService,
    private dialogCommonService: DialogCommonService,
    private translate: TranslateService
  ) { }

  ngOnInit(): void {
    const courseId = this.data['data']?.courseId;
    if (courseId) {
      const loader = this.loaderService.showProgressBar();
      this.dialogDetailService.getPersonLeanringDetail(this.personalInfomationService._idUser, courseId)
        .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
        .subscribe((data) => {
          this.detailcourse = data;
        })
    }
  }

  view(certificate: string) {
    if (certificate) {
      const byteArray = new Uint8Array(
        atob(certificate)
          .split('')
          .map((char) => char.charCodeAt(0))
      );
      const blob = new Blob([byteArray], {
        type: 'application/pdf',
      });
      const url = window.URL.createObjectURL(blob);
      window.open(url);
    } else {
      this.dialogCommonService.onOpenConfirm({
        content: this.translate.instant('notification.you-dont-have-certificate'),
        title: this.translate.instant('dialog.title_confirm'),
        icon: 'a-icon boschicon-bosch-ic-upload',
        isShowOKButton: true
      })
    }
  }
}
