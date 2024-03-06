import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import * as CONFIG_REPORT from './common/data-dumb.constant';
import { AssociateReportComponent } from './components/associate-report/associate-report.component';
import { ReportService } from './services/report.service';
import { NavigationStart, Router } from '@angular/router';
import { ReportTabModel } from './models/report.model';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { PermisisonService } from '../../shared/services/permisison.service';
import { CONFIG } from '../../shared/constants/config.constants';


@Component({
  selector: 'eet-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.scss']
})
export class ReportComponent implements OnInit,OnDestroy {
  @ViewChild(AssociateReportComponent) associateReportComponent: AssociateReportComponent;
  
  public tabList: ReportTabModel [] = CONFIG_REPORT.REPORT_TAB_LIST;
  public selectedTabIndex: number = 0;
  public root_url = "report";
  public associate_url = "associate";
  public project_url = "project";
  public activeTab: string = this.router.url?.split('/')[2] || this.associate_url;

  public loadedFilter: boolean = false;
  constructor(private reportService: ReportService, private router: Router) { 
    let currentTabIndex = this.tabList.findIndex((e) => e.routeName === this.activeTab);
    currentTabIndex =  currentTabIndex > -1 ? currentTabIndex : 1;
    this.selectedTabIndex = currentTabIndex;
  }

  public savedTeamsInFilterOfAssociate: string[] = [];
  public savedProjectGbInFilterOfAssociate: string[] = [];
  public savedTeamsInFilterOfProject: string[] = [];
  public savedProjectGbInFilterOfProject: string[] = [];

  public allTeamsInFilter: string[] = [];
  public allProjectGbInFilter: string[] = [];

  ngOnInit(): void{
    this.router.events.subscribe(event => { // route change
      if (event instanceof NavigationStart && event.url.includes(this.root_url)) {
        const currentProjecTailtUrl = event.url.split('/')[2];
        this.activeTab = currentProjecTailtUrl ? currentProjecTailtUrl : this.associate_url;
        let currentTabIndex = this.tabList.findIndex((e) => e.routeName === this.activeTab);
        currentTabIndex =  currentTabIndex > -1 ? currentTabIndex : 1;
        this.selectedTabIndex = currentTabIndex;        
      }
    });  

    this.reportService.getFilter().subscribe((res) => {
      this.allTeamsInFilter = res?.data?.teams?.map((r: any) => r.name);
      this.allProjectGbInFilter = res?.data?.gb_units?.map((r: any) => r.name);
      this.loadedFilter = true;
    });

    (document.getElementsByClassName('content')[0] as HTMLElement).style.overflowY = 'auto';
    (document.getElementsByClassName('content')[0] as HTMLElement).style.overflowX='hidden'
  }
  ngOnDestroy(): void {
    (document.getElementsByClassName('content')[0] as HTMLElement).style.overflow='unset'
  }
  updateSavedFilterEvent(data: any) {
    /**
     * data: {
     *    source: associate/project
     *    filterType = Team/Project GB
     *    filterData = [... , ...]
     * }
     */

    if (data?.source === 'associate') {
      if (data?.filterType === 'Team') {
        this.savedTeamsInFilterOfAssociate = data?.filterData;
      } else if (data?.filterType === 'Project GB') {
        this.savedProjectGbInFilterOfAssociate = data?.filterData;
      }
    } else if (data?.source === 'project') {
      if (data?.filterType === 'Team') {
        this.savedTeamsInFilterOfProject = data?.filterData;
      } else if (data?.filterType === 'Project GB') {
        this.savedProjectGbInFilterOfProject = data?.filterData;
      }
    }
  }

  onTabSwitch(event: MatTabChangeEvent) {
    const _routeName: string = this.tabList[event.index].routeName;
    this.activeTab = _routeName;
    this.router.navigate([this.root_url, _routeName]);
  }
}
