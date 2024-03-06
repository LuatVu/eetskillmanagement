import { Component, OnInit } from '@angular/core';
import { CONFIG } from '../../shared/constants/config.constants';
import { PermisisonService } from '../../shared/services/permisison.service';
import { LayoutService } from '../overview/layout.service';
import { LayoutUtils } from '../overview/utils';
import { NotificationService } from '@bci-web-core/core';
import { LocalStorageService } from '@core/src/lib/shared/services/storage.service';
import { TranslateService } from '@ngx-translate/core';
import { LoadingService } from '../../shared/services/loading.service';

@Component({
  selector: 'eet-release-note',
  templateUrl: './release-note.component.html',
  styleUrls: ['./release-note.component.scss']
})
export class ReleaseNoteComponent implements OnInit {
  public previousData:string
  public HAS_EDIT_RELEASE_NOTE: boolean = false;
  public layoutHelp = this.layoutService.LAYOUT.releaseNote
  constructor(
    private permisisonService: PermisisonService,
    public layoutService:LayoutService,
    private layOutUtils:LayoutUtils
  ) {
    this.HAS_EDIT_RELEASE_NOTE =  this.permisisonService.hasPermission(
      CONFIG.PERMISSIONS.EDIT_HELP
    );
  }

  ngOnInit(): void {
    this.getLayout()
    this.layoutService.setStatusFailedForSaveEditor({isFailed:false,layoutType:this.layoutService.LAYOUT.releaseNote})
  }
  ngAfterViewInit(): void {
    const content = document.querySelector('.content') as HTMLElement
    if (content) {
      content.style.overflow = 'auto'
    }
  }
  getLayout() {
    this.layOutUtils.getLayout(this.layoutService.LAYOUT.releaseNote)
  }
  handleEvent(e: any) {
    this.layOutUtils.handleEvent(e,this.layoutService.LAYOUT.releaseNote)
  }

}
