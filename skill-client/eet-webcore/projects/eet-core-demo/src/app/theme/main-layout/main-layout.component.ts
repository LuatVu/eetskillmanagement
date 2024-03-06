import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router } from '@angular/router';
import {
  BciImprintComponent,
  BciLayoutIntl, ModalWindowService,
  NavigationService,
  SidebarNavItem
} from '@bci-web-core/core';
import { AuthService, isEmptyObject } from '@core/src/lib/authentication';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { TranslateService } from '@ngx-translate/core';
import { SettingsService } from 'projects/core/src/lib/shared/services/settings.service';
import { MenuItem } from 'projects/core/src/public-api';
import { ViewRequestDetailService } from '../../pages/manage-request/components/view-request-detail/view-request-detail.service';
import { NotificationService } from '../../shared/services/notification.service';
import { PageNavigationService } from '../../shared/services/page-navigation.service';
import { PermisisonService } from '../../shared/services/permisison.service';
import { SkillEvaluationService } from '../../pages/career-development/components/skill-evaluation/skill-evaluation.service';
import { PENDING_REQUEST } from '@core/src/lib/layout/eet-header/const';

@Component({
  selector: 'eet-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss'],
})
export class MainLayoutComponent implements OnInit {
  public title: string = 'EET Portal';
  private titleLineTwo: string = 'EET Portal';
  public sidebarLinks: SidebarNavItem[] = [];
  private permissionUser = JSON.parse(localStorage.getItem('Authorization') || '{}');
  private PermissionName = this.permissionUser.permissions;
  private usermanager: string = 'user-management';
  private managerequest: string = 'manage-request';
  private departmentproject: string = 'department-project';
  private skillevaluate: string = 'skill-evaluation';
  // main nav side bar
  public menuItems: MenuItem[] = [
    {
      title: 'Language',
      icon: 'bosch-ic-translate',
      children: [
        {
          id: 'i18n',
          title: 'English',
          cb: (e, menu) => this.onChangeLanguage(e, menu),
        },
      ],
      hide:true
    },
  ];

  // nav side bar footer
  public sidebarFooterLinks: SidebarNavItem[] = [];
  private pendingRequestCount: any;
  private notification: any;
  private pendingRequestNotification: any;
  constructor(
    private router: Router,
    private auth: AuthService,
    private titleService: Title,
    private bciLayoutIntl: BciLayoutIntl,
    private navigationService: NavigationService,
    private modalWindowService: ModalWindowService,
    private translate: TranslateService,
    private settings: SettingsService,
    private changeDet: ChangeDetectorRef,
    public layoutIntl: BciLayoutIntl,
    private notifyService: NotificationService,
    private permisisonService: PermisisonService,
    private pageNavigationService: PageNavigationService,
    private skillEvaluationService: SkillEvaluationService,
    private viewRequestDetailService: ViewRequestDetailService,
  ) {
    translate.addLangs(['i18n']);
  }

  error404 = false;
  searchPermission(code: string, PermissionName: any) {
    for (let i = 0; i < PermissionName.length; i++) {
      if (PermissionName[i].code === code) {
        return PermissionName[i];
      }
    }
  }


  ngOnInit() {
    let userMenu = {
      id: 'guest',
      title: "Guest",
      icon: 'Bosch-Ic-user',
    };
    this.menuItems.unshift(userMenu);

    this.auth.change().subscribe((user: any) => {
      if (!isEmptyObject(user)) {
        if (this.sidebarFooterLinks.find(f => f.id == 'user')) {
          return;
        }
         userMenu = {
          id: 'user',
          title: user.displayName,
          icon: 'Bosch-Ic-user',
        };
        const notificationMenu = {
          title: "Notification",
          icon: 'bosch-ic-notification',
          children: [],
        };
        this.menuItems = this.menuItems.filter((e)=>e.id !=='guest')
        this.notification = notificationMenu;
        this.sidebarFooterLinks.unshift(userMenu);
        this.menuItems.unshift(userMenu);
        this.menuItems.unshift(notificationMenu);
        this.getNotification();
      }
    });
    this.initialize();
    this.updateNotification();
  }


  /**
   * initialize menu and app's title
   */
  initialize() {
    this.translate.get('appName').subscribe((appName: any) => {
      this.titleService.setTitle(appName);
      this.title = appName;
    });

    this.translate.get('menu.logout.btn').subscribe((logoutBtn: any) => {
      this.bciLayoutIntl.logout = logoutBtn;
    });

    // get sidebar & translate manually
    this.navigationService.getNavigationItems().subscribe({
      next: (response: (SidebarNavItem[] | any)) => {
        setTimeout(() => {
          if (response && response.length != 0) {
            const _response = response.filter(
              (f: SidebarNavItem | any) =>
                f?.permissions?.length===0 || f?.permissions.find((permisison: string) => this.permisisonService.hasPermission(permisison))
            ).slice();

            // filter for sub menu
            for(var i = 0; i< _response.length; i++){
              if(_response[i].items){
                const _items = _response[i].items.filter(
                  (f: SidebarNavItem | any) =>
                    f?.permissions?.length===0 
                    || f?.permissions.find((permisison: string) => this.permisisonService.hasPermission(permisison))
                ).slice();  
                _response[i].items = _items;
              }              
            }

            this.sidebarLinks = _response;
            this.sidebarLinks.forEach(item => {
              if(item.title === CoreUrl.TRAVEL_PLAN){
                item.cb = ()=>{
                  window.open(item.url, '_blank');
                }
              }
            });
            this.pageNavigationService.setPageNavigationItems(_response);
            this.translateManual(this.sidebarLinks, 'menu');
          }
          this.changeDet.detectChanges();
        }, 100);
      },
    });

    this.translateFooterLink(this.sidebarFooterLinks, 'menu');
  }

  getNotification() {
    // pending request notification
    this.skillEvaluationService.getPendingRequest(this.permissionUser.id).subscribe((rs) => {
      this.pendingRequestCount = rs?.data?.count;
      if (this.pendingRequestCount > 0) {
        if (this.notification != undefined) {
          this.notification.icon = 'bosch-ic-notification-active';
          if (this.notification.badge != undefined) { this.notification.badge += this.pendingRequestCount; }
          else { this.notification.badge = this.pendingRequestCount; }
        };

        this.notifyService.information(
          this.translate.instant('skill.request_pending_notification.content') + ": " + this.pendingRequestCount,
          this.translate.instant('skill.request_pending_notification.view'))
          .onAction().subscribe(() => { this.router.navigate(['/manage-request']) })

        const pendingRequestNotificationMenu = {
          id: 'pendingRequest',
          title: this.translate.instant('skill.request_pending_notification.pending_notification') + ' (' + this.pendingRequestCount + ")",
          cb: () => { this.router.navigate(['/manage-request']); }
        };
        this.pendingRequestNotification = pendingRequestNotificationMenu;
        this.notification?.children?.unshift(pendingRequestNotificationMenu);
      }
    })
  }

  updateNotification() {
    this.viewRequestDetailService.pending$.subscribe((response: any) => {
      if (response.type === 'changeCount') {
        //   const testMenu = {
        //     title: "Test",
        //     icon: 'bosch-ic-notification',
        //     children: [],
        //   };
        //   this.menuItems.unshift(testMenu)
        //   console.log(this.menuItems);

        let newPendingRequest = response.data as number
        if (this.pendingRequestCount != newPendingRequest) {
          if (this.notification.badge != undefined) {
            this.notification.icon = 'bosch-ic-notification-active';
            this.notification.badge -= (this.pendingRequestCount - newPendingRequest);
            this.pendingRequestCount = newPendingRequest;
            this.pendingRequestNotification.title = PENDING_REQUEST + this.pendingRequestCount + ')';
            // console.log(this.menuItems)
          }
          else {
            this.pendingRequestCount = newPendingRequest;
            this.notification.badge = this.pendingRequestCount;
            if(this.pendingRequestNotification == undefined){
              const refreshPendingRequestNotification = {
                id: 'pendingRequest',
                title: this.translate.instant('skill.request_pending_notification.pending_notification') + ' (' + this.pendingRequestCount + ")",
                cb: () => { this.router.navigate(['/manage-request']); }
              };
              this.pendingRequestNotification = refreshPendingRequestNotification;
              this.notification?.children?.unshift(refreshPendingRequestNotification);
            }else{
              this.pendingRequestNotification.title = PENDING_REQUEST + this.pendingRequestCount + ')';
            }
          }
        }
        this.viewRequestDetailService.sendPendingRequest('changeMenuCount', this.menuItems);
      }
    })
  }

  /**
   * logout action
   * TODO
   */
  logout() {
    this.auth.logout().subscribe({
      error: (errorRes: HttpErrorResponse) => { },
      complete: () => {
        this.translate.use('i18n');
        this.settings.setLanguage('i18n');
        this.initialize();
        this.translate
          .get('logout.success')
          .subscribe((successMessageValue: any) => {
            this.notifyService.success(successMessageValue);
          });
        this.router.navigateByUrl(`/${CoreUrl.AUTH}/${CoreUrl.LOGIN}`);
      },
    });
  }

  onAbout() {
    this.modalWindowService.openDialogWithComponent(BciImprintComponent);
  }

  /**
   *
   * @param event on change language event
   * @param navItem nav item
   */
  onChangeLanguage(event: MouseEvent, navItem: MenuItem) {
    const langChange = navItem.id as string;
    this.translate.use(langChange);
    this.settings.setLanguage(langChange);
    this.initialize();
  }

  /**
   *
   * @param menu perform translate navsidebar
   * @param namespace key
   */
  translateManual(menu: SidebarNavItem[], namespace: string) {
    menu.forEach((item) => {
      const menuName = `${namespace}.${item.title}`;
      this.translate
        .get(menuName)
        .subscribe((translateTitle: any) => (item.title = translateTitle));
      if (item.items && item.items.length > 0) {
        this.translateManual(item.items, menuName);
      }
    });
  }

  translateFooterLink(menu: SidebarNavItem[], namespace: string) {
    menu.forEach((item) => {
      if (item.id === 'user') {
        const menuName = `${namespace}.logout.title`;
        this.translate.get(menuName).subscribe((translateTitle: any) => {
          const tempItem = item as any;
          if (tempItem && tempItem.overlay) {
            tempItem.overlay.config.title = translateTitle;
          }
          item = tempItem;
        });
      } else {
        const menuName = `${namespace}.${item.id}`;
        this.translate
          .get(menuName)
          .subscribe((translateTitle: any) => (item.title = translateTitle));
      }
    });
  }
}
