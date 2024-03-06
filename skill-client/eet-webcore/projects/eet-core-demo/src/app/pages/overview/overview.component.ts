import { AfterViewInit, Component, OnInit } from '@angular/core';
import { LoadingService } from '../../shared/services/loading.service';
import { NotificationService } from '@bci-web-core/core';
import { LocalStorageService } from '@core/src/lib/shared/services/storage.service';
import { LayoutService } from './layout.service';
import { Observable, catchError, finalize, of, EMPTY } from 'rxjs';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { PermisisonService } from '../../shared/services/permisison.service';
import { STATUS } from 'angular-in-memory-web-api';
import { TranslateService } from '@ngx-translate/core';
import { LayoutUtils } from './utils';
import { CanDeactivateGuard } from '../../shared/utils/can-deactivate.guard';

@Component({
  selector: 'eet-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.scss'],
})
export class OverviewComponent implements OnInit, CanDeactivateGuard {
  
  public previousData: string;
  public HAS_EDIT_EET_OVERVIEW: boolean = false;
  public isEditting: boolean;

  constructor(
    private comLoader: LoadingService,
    private notifyService: NotificationService,
    private localStorage: LocalStorageService,
    public layoutService: LayoutService,
    private permisisonService: PermisisonService,
    private translate:TranslateService,
    private layOutUtils:LayoutUtils
  ) {
        this.HAS_EDIT_EET_OVERVIEW = this.permisisonService.hasPermission(
            CONFIG.PERMISSIONS.EDIT_EET_OVERVIEW
          );
  }

  ngOnInit(): void {
    this.getLayout();
    this.layoutService.setStatusFailedForSaveEditor({isFailed:false,layoutType:this.layoutService.LAYOUT.overview})
  }
  ngAfterViewInit(): void {
    const content = document.querySelector('.content') as HTMLElement;
    if (content) {
      content.style.overflow = 'auto';
    }
  }

  getLayout() {
    this.layOutUtils.getLayout(this.layoutService.LAYOUT.overview)
  }

  handleEvent(e: any) {
    this.layOutUtils.handleEvent(e,this.layoutService.LAYOUT.overview)
    if(e.type==="edit") this.isEditting = true;
    else if (e.type==="cancel" || e.type==="save") this.isEditting = false;
  }

  canDeactivate(): boolean | Promise<boolean> | import('rxjs').Observable<boolean> { 
    if(this.isEditting==true) {
      return confirm('Are you sure to discard changes and go to another page?');
    }
    return true;
  }
  
}
