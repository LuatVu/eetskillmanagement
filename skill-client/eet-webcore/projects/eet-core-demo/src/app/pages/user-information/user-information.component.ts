import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { PermisisonService } from '../../shared/services/permisison.service';
import { UserInformationService } from './user-information.service';
import { CONFIG } from '../../shared/constants/config.constants';
import { PaginDirectionUtil } from '../../shared/utils/paginDirectionUtil';
import { UserInforTabModel } from './user-information.model';
import * as DEFAULT_PARAMS from './user-information.constant';
import { NavigationStart, Router } from '@angular/router';

@Component({
  selector: 'eet-user-information',
  templateUrl: './user-information.component.html',
  styleUrls: ['./user-information.component.scss']
})
export class UserInformationComponent implements OnInit, OnDestroy {
  public HAS_VIEW_PERSONAL_PERMISSION: boolean = false;
  public HAS_VIEW_ASSOCIATE_INFO_PERMISSION: boolean = false;

  public userInforTabList: UserInforTabModel [] = DEFAULT_PARAMS.USER_INFORMATION_CONFIG.USER_INFORMATION_TAB;
  public selectedTabIndex: number = 0;
  public root_url = "user-information";
  public personal_infor_url = 'personal-info';
  public associate_infor_url = 'associate-info';
  public activeTab: string = this.router.url?.split('/')[2] || 'personal-info';
  public confirmation: boolean;


  constructor(
    private permisisonService: PermisisonService,
    private userInformationService: UserInformationService,
    private router: Router
  ) {
    this.HAS_VIEW_PERSONAL_PERMISSION = this.permisisonService.hasPermission(CONFIG.PERMISSIONS.VIEW_PERSONAL_PERMISSION);
    this.HAS_VIEW_ASSOCIATE_INFO_PERMISSION = this.permisisonService.hasPermission(CONFIG.PERMISSIONS.VIEW_ASSOCIATE_INFO_PERMISSION);

    let currentTabIndex = this.userInforTabList.findIndex((e) => e.routeName === this.activeTab);
    currentTabIndex =  currentTabIndex > -1 ? currentTabIndex : 1;
    this.selectedTabIndex = currentTabIndex;
  }
  ngOnInit(): void {
    this.router.events.subscribe(event => { // route change
      if (event instanceof NavigationStart && event.url.includes(this.root_url)) {
        const currentProjecTailtUrl = event.url.split('/')[2];
        this.activeTab = currentProjecTailtUrl ? currentProjecTailtUrl : this.personal_infor_url;
        let currentTabIndex = this.userInforTabList.findIndex((e) => e.routeName === this.activeTab);
        currentTabIndex =  currentTabIndex > -1 ? currentTabIndex : 1;
        this.selectedTabIndex = currentTabIndex;
      }
    });    

    this.userInformationService.confirm$.subscribe(data => {
      this.confirmation = data;
      if(data==false) this.stayAtOldTab();
    })

    this.setTabSelectedDependOnCurrentUrl()
  }

  onTabSwitch(event: MatTabChangeEvent) {
    const _routeName: string = this.userInforTabList[event.index].routeName;
    this.activeTab = _routeName;
    this.router.navigate(['user-information', _routeName]);
  }

  stayAtOldTab() { 

    this.selectedTabIndex = this.userInformationService.getOldIndex();
  }
 
  ngOnDestroy(): void {
    this.userInformationService.setCurrentViewAssociateMode('');

    PaginDirectionUtil.removeEventListenerForDirectionExpandPagination()
  }
  setTabSelectedDependOnCurrentUrl() {
    const currentChildRoute = this.router.url?.split('/')[2]
    const currentPageIndex = this.userInforTabList?.findIndex((e) => e.routeName?.includes(currentChildRoute))
    this.selectedTabIndex = currentPageIndex
  }
}
