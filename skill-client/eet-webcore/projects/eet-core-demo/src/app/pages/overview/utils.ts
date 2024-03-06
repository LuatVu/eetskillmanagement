import { Injectable } from '@angular/core';
import { LoadingService } from '../../shared/services/loading.service';
import { LayoutService } from './layout.service';
import { catchError, finalize } from 'rxjs/operators';
import { EMPTY, of } from 'rxjs';
import { NotificationService } from '@bci-web-core/core';
import { TranslateService } from '@ngx-translate/core';
import { STATUS } from 'angular-in-memory-web-api';
import { CONFIG } from '../../shared/constants/config.constants';

@Injectable({
  providedIn: 'root',
})

export class LayoutUtils {
    public defaultData: string;
  constructor(
    private comLoader:LoadingService,
    private layoutService:LayoutService,
    private notifyService:NotificationService,
    private translate:TranslateService
    ) {}

  getLayout(layoutName:string) {
    const loader = this.comLoader.showProgressBar();
    this.layoutService
      .getLayoutByName(layoutName)
      .pipe(
        catchError((err) => {
            this.defaultData = '<p></p>';
          return EMPTY;
        }),
        finalize(() => this.comLoader.hideProgressBar(loader))
      )
      .subscribe((rs) => {
        this.layoutService.setDataLayoutObservable({layoutType:layoutName,data:rs})
      });
  }
  handleEvent(e: any,layout:string) {
    switch (e.type) {
      case 'save':
        this.handleSave(e.data,layout)
        break
      case 'preview':
        this.handlePreView()
        break
      case 'edit':
        this.handleEdit()
        break
      case 'cancel':
        this.handleCancel()
        break
      default:

    }
  }
  handleSave(data: string,layout:string) {
    const loader = this.comLoader.showProgressBar();
    this.layoutService
      .createLayout(data, layout)
      .pipe(
        catchError((err: any) => {
          this.notifyService.error(this.translate.instant("editor.maximum_upload_size_exceeded"))
          this.layoutService.setStatusFailedForSaveEditor({isFailed:true,data,layoutType:layout})
          return of(err);
        }),
        finalize(() => this.comLoader.hideProgressBar(loader))
      )
      .subscribe((rs) => {
        if(rs.status === STATUS.BAD_REQUEST){
          this.notifyService.error(this.translate.instant("editor.maximum_upload_size_exceeded"))
          this.layoutService.setStatusFailedForSaveEditor({isFailed:true,data,layoutType:layout})
        }else if(rs.status === STATUS.INTERNAL_SERVER_ERROR) {
          this.notifyService.error(this.translate.instant("editor.server_error"))
        }
        else if(rs.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.notifyService.success(this.translate.instant("editor.save_success"))
          this.layoutService.setStatusFailedForSaveEditor({isFailed:false,layoutType:layout})
          this.getLayout(layout)
        }
      });
  }
  handlePreView() {
    const loader = this.comLoader.showProgressBar();
    setTimeout(() => {
      this.comLoader.hideProgressBar(loader);
    }, 100);
  }
  handleEdit() {
    const loader = this.comLoader.showProgressBar();
    setTimeout(() => {
      this.comLoader.hideProgressBar(loader);
    }, 100);
  }
  handleCancel() {}
}
