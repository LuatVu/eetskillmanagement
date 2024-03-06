import { AfterViewInit, Component, OnInit } from '@angular/core';
import { LoadingService } from '../../shared/services/loading.service';
import { NotificationService } from '@bci-web-core/core';
import { LocalStorageService } from '@core/src/lib/shared/services/storage.service';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { PermisisonService } from '../../shared/services/permisison.service';
import { catchError, finalize } from 'rxjs/operators';
import { LayoutService } from '../overview/layout.service';
import { EMPTY } from 'rxjs/internal/observable/empty';
import { of } from 'rxjs/internal/observable/of';
import { STATUS } from 'angular-in-memory-web-api';
import { TranslateService } from '@ngx-translate/core';
import { LayoutUtils } from '../overview/utils';
import { CanDeactivateGuard } from '../../shared/utils/can-deactivate.guard';
@Component({
  selector: 'eet-org-chart',
  templateUrl: './org-chart.component.html',
  styleUrls: ['./org-chart.component.scss']
})
export class OrgChartComponent implements OnInit, AfterViewInit, CanDeactivateGuard {
  public previousData:string;
  public HAS_EDIT_EET_ORG_CHART: boolean = false;
  public layoutOrgChart = this.layoutService.LAYOUT.orgchart;
  public isEditting: boolean;
  constructor(
    private comLoader: LoadingService,
    private notifyService: NotificationService,
    private localStorage:LocalStorageService,
    private permisisonService: PermisisonService,
    public layoutService: LayoutService,
    private translate:TranslateService,
    private layOutUtils:LayoutUtils

  ) {
    this.HAS_EDIT_EET_ORG_CHART = this.permisisonService.hasPermission(
      CONFIG.PERMISSIONS.EDIT_EET_ORG_CHART
    );
  }

  ngOnInit(): void {
    this.getLayout()
    this.layoutService.setStatusFailedForSaveEditor({isFailed:false,layoutType:this.layoutService.LAYOUT.orgchart})
  }
  ngAfterViewInit(): void {
    const content = document.querySelector('.content') as HTMLElement
    if (content) {
      content.style.overflow = 'auto'
    }
  }
  getLayout() {
    this.layOutUtils.getLayout(this.layoutService.LAYOUT.orgchart)
  }
  handleEvent(e: any) {
    this.layOutUtils.handleEvent(e,this.layoutService.LAYOUT.orgchart)
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
