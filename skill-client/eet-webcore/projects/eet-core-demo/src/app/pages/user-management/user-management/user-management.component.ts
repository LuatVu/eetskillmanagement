import { Component, OnInit } from '@angular/core';
import * as DEFAULT_PARAMS from '../common/constants/constants';
import { UserManagementModel } from '../common/model/user-management.model';
import { NavigationStart, Router } from '@angular/router';
import { MatTabChangeEvent } from '@angular/material/tabs';


@Component({
  selector: 'eet-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.scss']
})
export class UserManagementComponent implements OnInit {

  public userManagementTabList: UserManagementModel [] = DEFAULT_PARAMS.USER_MANAGEMENT_TAB;
  public selectedTabIndex: number = 0;
  public root_url = "user-management";
  public groups_users_url = "groups-users";
  public roles_url = "roles";
  public permissions_url = "permissions";
  public activeTab: string = this.router.url?.split('/')[2] || this.groups_users_url;



  constructor(
    private router: Router
  ) { 
    let currentTabIndex = this.userManagementTabList.findIndex((e) => e.routeName === this.activeTab);
    currentTabIndex =  currentTabIndex > -1 ? currentTabIndex : 1;
    this.selectedTabIndex = currentTabIndex;
  }

  ngOnInit(): void {
    this.router.events.subscribe(event => { // route change
      if (event instanceof NavigationStart && event.url.includes(this.root_url)) {
        const currentProjecTailtUrl = event.url.split('/')[2];
        this.activeTab = currentProjecTailtUrl ? currentProjecTailtUrl : this.groups_users_url;
        let currentTabIndex = this.userManagementTabList.findIndex((e) => e.routeName === this.activeTab);
        currentTabIndex =  currentTabIndex > -1 ? currentTabIndex : 1;
        this.selectedTabIndex = currentTabIndex;        
      }
    });
  }

  onTabSwitch(event: MatTabChangeEvent){
    const _routeName: string = this.userManagementTabList[event.index].routeName;
    this.activeTab = _routeName;
    this.router.navigate([this.root_url, _routeName]);
  }

}
