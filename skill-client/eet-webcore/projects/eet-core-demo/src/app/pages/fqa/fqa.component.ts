import { AfterViewInit, Component, OnInit } from '@angular/core';
import { LoadingService } from '../../shared/services/loading.service';
import { NotificationService } from '@bci-web-core/core';
import { LocalStorageService } from '@core/src/lib/shared/services/storage.service';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { PermisisonService } from '../../shared/services/permisison.service';
import { LayoutService } from '../overview/layout.service';
import { catchError, finalize } from 'rxjs/operators';
import { EMPTY } from 'rxjs/internal/observable/empty';
import { of } from 'rxjs/internal/observable/of';
import { STATUS } from 'angular-in-memory-web-api';
import { TranslateService } from '@ngx-translate/core';
import { LayoutUtils } from '../overview/utils';
@Component({
  selector: 'eet-fqa',
  templateUrl: './fqa.component.html',
  styleUrls: ['./fqa.component.scss']
})
export class FqaComponent implements OnInit {
  public previousData:string
  public HAS_EDIT_HELP: boolean = false;
  public layoutHelp = this.layoutService.LAYOUT.help
  constructor(
    private comLoader: LoadingService,
    private notifyService: NotificationService,
    private localStorage:LocalStorageService,
    private permisisonService: PermisisonService,
    public layoutService:LayoutService,
    private translate:TranslateService,
    private layOutUtils:LayoutUtils
  ) {
    this.HAS_EDIT_HELP =  this.permisisonService.hasPermission(
      CONFIG.PERMISSIONS.EDIT_HELP
    );
  }

  ngOnInit(): void {
    this.getLayout()
    this.layoutService.setStatusFailedForSaveEditor({isFailed:false,layoutType:this.layoutService.LAYOUT.help})
  }
  ngAfterViewInit(): void {
    const content = document.querySelector('.content') as HTMLElement
    if (content) {
      content.style.overflow = 'auto'
    }
  }
  getLayout() {
    this.layOutUtils.getLayout(this.layoutService.LAYOUT.help)
  }
  handleEvent(e: any) {
    this.layOutUtils.handleEvent(e,this.layoutService.LAYOUT.help)
  }
  
}
