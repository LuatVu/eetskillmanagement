import { MenuItem } from './menu-item.model';
/*
 * Copyright (C) 2018 Robert Bosch GmbH Copyright (C) 2018 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

import {
  ChangeDetectorRef,
  Component,
  ComponentRef,
  Input,
  ViewContainerRef,
  ViewEncapsulation,
} from '@angular/core';
import { Router } from '@angular/router';
import {
  BciLayoutIntl,
  BreadcrumbsService,
  ClosableOverlayComponentModel,
  SidebarNavItem,
} from '@bci-web-core/core';
import { Overlay, OverlayConfig } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { BehaviorSubject } from 'rxjs';
import { ViewRequestDetailService } from 'projects/eet-core-demo/src/app/pages/manage-request/components/view-request-detail/view-request-detail.service';
import { CoreUrl } from '../../shared/util/url.constant';
import { PENDING_REQUEST } from './const';

@Component({
  selector: 'eet-header',
  templateUrl: './eet-header.component.html',
  styleUrls: ['./eet-header.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class EetHeaderComponent {
  @Input() public header = '';
  @Input() public logoRoute = '/';

  @Input() public menuItems: MenuItem[] = [];

  public pending_title=PENDING_REQUEST


  constructor(
    public layoutIntl: BciLayoutIntl,
    private router: Router,
    public breadcrumbService: BreadcrumbsService,
    public overlay: Overlay,
    public viewContainerRef: ViewContainerRef,
    private changeDet: ChangeDetectorRef,
    private viewRequestDetailService: ViewRequestDetailService
  ) { }

  ngOnInit() {
    this.viewRequestDetailService.pending$.subscribe((response: any) => {
      if (response.type === 'changeMenuCount') {
        // this.menuItems = response.data;
        this.changeDet.markForCheck();
      }
    })
  }

  onLogoClicked() {
    // TODO: Cursor:pointer when logoLink or logoRoute is set
    
  }
  onMenuClick(event: MouseEvent, navItem: MenuItem) {
   
    if (navItem.cb) {
      navItem.cb(event, navItem);
    } else if (navItem.overlay) {
      this.openOverlay(event, navItem);
    }
    if(navItem.id==='guest') {
      this.router.navigateByUrl(`/${CoreUrl.AUTHENTICATE}/${CoreUrl.NO_ACCESS}`)
    }
    if(navItem.id==='user'){
      this.router.navigate(['/', CoreUrl.USER_INF]);
    }
    this.changeDet.markForCheck();
   
  }
  private openOverlay(event: MouseEvent, navItem: MenuItem) {
    const strategy = this.overlay
      .position()
      .flexibleConnectedTo(event.currentTarget as Element)
      .withPositions([
        {
          originX: 'end',
          originY: 'bottom',
          overlayX: 'start',
          overlayY: 'top',
          offsetX: 0,
          offsetY: 0,
        },
      ]);

    const config = new OverlayConfig({
      positionStrategy: strategy,
      hasBackdrop: true,
      backdropClass: 'cdk-overlay-transparent-backdrop',
    });
    const overlayRef = this.overlay.create(config);

    const overlayComponent = navItem.overlay!.component;
    const overlayConfig = navItem.overlay?.config;

    const componentRef: ComponentRef<
      ClosableOverlayComponentModel<typeof overlayConfig>
    > = overlayRef.attach(
      new ComponentPortal(overlayComponent, this.viewContainerRef)
    );
    componentRef.instance.config = overlayConfig;
    componentRef.instance.onClose.subscribe(() => {
      overlayRef.detach();
      this.changeDet.markForCheck();
    });
    overlayRef.backdropClick().subscribe(() => {
      overlayRef.detach();
      this.changeDet.markForCheck();
    });
  }
}
