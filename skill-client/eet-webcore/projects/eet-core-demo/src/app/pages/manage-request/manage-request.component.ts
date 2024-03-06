import { Component, OnInit } from '@angular/core';
import { PaginDirectionUtil } from '../../shared/utils/paginDirectionUtil';
import * as DEFAULT_PARAM from './constants/constants';
import { ManageRequestTabModel } from './models/manage-request.model';
import { NavigationStart, Router } from '@angular/router';
import { MatTabChangeEvent } from '@angular/material/tabs';
@Component({
  selector: 'eet-manage-request',
  templateUrl: './manage-request.component.html',
  styleUrls: ['./manage-request.component.scss']
})
export class ManageRequestComponent implements OnInit {

  public manageRequestTabList: ManageRequestTabModel [] = DEFAULT_PARAM.MANAGE_REQUEST_TAB;
  public selectedTabIndex: number = 0;
  public root_url = "manage-request";
  public pending_request_url = "pending-request";
  public approved_rejected_request_url = "approved-rejected-request";
  public activeTab: string = this.router.url?.split('/')[2] || this.pending_request_url;

  constructor(private router: Router) {
    let currentTabIndex = this.manageRequestTabList.findIndex((e) => e.routeName === this.activeTab);
    currentTabIndex =  currentTabIndex > -1 ? currentTabIndex : 1;
    this.selectedTabIndex = currentTabIndex;
  }

  ngOnInit(): void {
    this.router.events.subscribe(event => { // route change
      if (event instanceof NavigationStart && event.url.includes(this.root_url)) {
        const currentProjecTailtUrl = event.url.split('/')[2];
        this.activeTab = currentProjecTailtUrl ? currentProjecTailtUrl : this.pending_request_url;
        let currentTabIndex = this.manageRequestTabList.findIndex((e) => e.routeName === this.activeTab);
        currentTabIndex =  currentTabIndex > -1 ? currentTabIndex : 1;
        this.selectedTabIndex = currentTabIndex;        
      }
    });  
  }

  onTabSwitch(event: MatTabChangeEvent) {
    const _routeName: string = this.manageRequestTabList[event.index].routeName;
    this.activeTab = _routeName;
    this.router.navigate([this.root_url, _routeName]);
  }

  ngOnDestroy(): void {
    PaginDirectionUtil.removeEventListenerForDirectionExpandPagination()
  }
}
