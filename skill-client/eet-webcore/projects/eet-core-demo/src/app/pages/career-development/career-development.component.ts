import { Component, OnInit } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import * as DEFAULT_PARAMS from './constants/career-development-default-parameters.constants';
import { NavigationStart, Router } from '@angular/router';
import { Subscription } from 'rxjs'
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { PermisisonService } from '../../shared/services/permisison.service';
import { CONFIG } from '../../shared/constants/config.constants';
import { CareerDevelopmentService } from './career-development.service';


@Component({
  selector: 'eet-career-development',
  templateUrl: './career-development.component.html',
  styleUrls: ['./career-development.component.scss']
})
export class CareerDevelopmentComponent implements OnInit {
  public tabList = DEFAULT_PARAMS.CAREER_DEVELOPMENT_CONFIG.TAB_LIST;
  public activeTab: string = this.router?.url?.split('/')[2] || 'career-road-map';
  public selectedTabIndex: number = 0;
  public HAS_VIEW_DEPARTMENT_LEARNING:boolean
  public HAS_VIEW_SKILL_EVALUATE:boolean
  private groupPermission:[]=[]
  routeSubscription: Subscription;
  public confirmation: boolean = true;
  public firstTime: boolean = true;
  public readonly COMPETENCE_DEVELOPMENT = CoreUrl.COMPETENCE_DEVELOPMENT;
  constructor(
    private router: Router,
    private permission:PermisisonService,
    private carrerDevelopmentService:CareerDevelopmentService) { }
 
  ngOnInit(): void {
    this.routeSubscription = this.router.events.subscribe(route => {
        if (route instanceof NavigationStart && route.url.includes(this.COMPETENCE_DEVELOPMENT)) {
          this.activeTab = route?.url?.split('/')[2];
        }
        this.selectedTabIndex = this.tabList.findIndex(tab => tab.routeName === this.activeTab) || 0;
    });
    this.selectedTabIndex = this.tabList.findIndex(tab => tab.routeName === this.activeTab);
    
    this.tabList = this.tabList.filter((tab) => {
      if(tab.permission){
        return this.permission.hasPermission(tab.permission);
      } 
      return tab
    })

    this.carrerDevelopmentService.confirm$.subscribe(data => {
      this.confirmation = data; 
      if (data == false) this.stayAtOldPage();
    })

  }

  onTabChange(event: MatTabChangeEvent) {
      const _routeName: string = this.tabList[event.index].routeName;
      this.activeTab = _routeName;
      this.selectedTabIndex = event.index;
      this.router.navigate([this.COMPETENCE_DEVELOPMENT, _routeName]);
  }

  stayAtOldPage() {
    const _routeName: string = this.tabList[this.carrerDevelopmentService.getOldPageIndex()].routeName;
    this.activeTab = _routeName;
    this.selectedTabIndex = this.carrerDevelopmentService.getOldPageIndex();
  }

  ngOnDestroy(): void {
    this.routeSubscription.unsubscribe();
  }

}
