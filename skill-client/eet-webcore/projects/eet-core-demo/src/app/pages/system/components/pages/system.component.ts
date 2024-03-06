import { Component, OnInit } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { NavigationStart, Router } from '@angular/router';
import { TOKEN_KEY } from '@core/src/lib/shared/util/system.constant';
import { API } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { PermisisonService } from 'projects/eet-core-demo/src/app/shared/services/permisison.service';
import { PaginDirectionUtil } from 'projects/eet-core-demo/src/app/shared/utils/paginDirectionUtil';
import { CommonConfigService } from '../../services/common-config.service';

@Component({
  selector: 'eet-system',
  templateUrl: './system.component.html',
  styleUrls: ['./system.component.scss']
})
export class SystemComponent implements OnInit {

  
  public selectedTabIndex: number = 1;
  public _system = 'system';
  public _competency_lead = 'competency-lead';
  public listOfPermissions: any;
  public confirmation: boolean;


  constructor(private router: Router,
    private permissionService: PermisisonService,
    private commonConfigService: CommonConfigService) { }

  ngOnInit(): void {
    this.router.events.subscribe(event => { // route change
      if (event instanceof NavigationStart && event.url.includes(this._system)) {
        const currentProjecTailtUrl = event.url.split('/')[2]
        this.activeTab = currentProjecTailtUrl ? currentProjecTailtUrl : this._competency_lead
        let currentTabIndex = this.PROJECT_TAB_LIST.findIndex((e) => e.routeName === this.activeTab)
        currentTabIndex =  currentTabIndex > -1 ? currentTabIndex : 1
        this.selectedTabIndex = currentTabIndex
      }
    });
    this.selectedTabIndex = this.PROJECT_TAB_LIST.findIndex((e) => e.routeName === this.activeTab)
    this.listOfPermissions = JSON.parse(localStorage.getItem(TOKEN_KEY) || '{}')['permissions']
    this.hasC = this.listOfPermissions.some((obj: any) => obj.code == CONFIG.PERMISSIONS.VIEW_EXPECTED_SKILL_LEVEL);
    this.PROJECT_TAB_LIST = this.checkRoleForTablist(this.PROJECT_TAB_LIST);

    this.commonConfigService.confirm$.subscribe(data => {
      this.confirmation = data;
      if(data==false) this.stayAtOldPage();
    })

  }
    hasC: boolean = false;

  ngOnDestroy(): void {
    PaginDirectionUtil.removeEventListenerForDirectionExpandPagination()
  }

  public PROJECT_TAB_LIST = [
    { name: 'system.tab_label.competency-lead', routeName: API.COMPETENCY_LEAD },
    { name: 'system.tab_label.manage-skill', routeName: API.MANAGE_SKILL },
    { name: 'system.tab_label.manage-project-scope', routeName: `${API.MANAGE}-${API.PROJECT_SCOPE}` },
    { name: 'system.tab_label.manage-project-role', routeName: API.MANAGE_PROJECT_ROLE },
    { name: 'system.tab_label.line-manager', routeName: API.LINE_MANAGER_ROUTE },
    { name: 'system.tab_label.common-config', routeName: API.COMMON_CONFIG },
  ]

  public activeTab: string = this.router.url?.split('/')[2] || 'competency-lead';

  onProjectTabChange(event: MatTabChangeEvent) {
    const _routeName: string = this.PROJECT_TAB_LIST[event.index].routeName;
    this.activeTab = _routeName;
    this.selectedTabIndex = event.index;
    this.router.navigate(['system', _routeName]);
  }

  stayAtOldPage() {
    const _routeName: string = this.PROJECT_TAB_LIST[this.commonConfigService.getOldPageIndex()].routeName;
    this.activeTab = _routeName;
    this.selectedTabIndex = this.commonConfigService.getOldPageIndex()
  }

  checkRoleForTablist(tablist: any[]){
    tablist = tablist.filter(tab => {
      return tab.permission? this.permissionService.hasPermission(tab.permission): true;
    });
    return tablist;
  }
}
