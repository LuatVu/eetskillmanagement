import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { SelectWithSearchComponent } from 'projects/eet-core-demo/src/app/shared/components/select-with-search/select-with-search.component';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { ReportUtils } from '../../common/report.utils';
import { ReportForChartModel } from '../../models/report.model';
import { ReportService } from '../../services/report.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { finalize } from 'rxjs/operators';
import { Router } from '@angular/router';
import { PermisisonService } from 'projects/eet-core-demo/src/app/shared/services/permisison.service';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';

@Component({
  selector: 'eet-associate-report',
  templateUrl: './associate-report.component.html',
  styleUrls: ['./associate-report.component.scss']
})
export class AssociateReportComponent implements OnInit {
  @Input() reportData: any = null;
  @Input() allTeamsInFilter: any[] = [];
  @Input() allProjectGbInFilter: any[] = [];
  @Input() savedTeamsInFilterOfAssociate: string[] = [];
  @Input() savedProjectGbInFilterOfAssociate: string[] = [];
  @Output() updateSavedFilterEvent = new EventEmitter<any>();
  public _countGetFilterData: number = 0;
  public reRenderTrigger: boolean = true;

  public numOfAssociates: number = 0;
  public numOfInternals: number = 0;
  public numOfFixedTerms: number = 0;
  public numOfExternals: number = 0;
  public colors = CONFIG.REPORT.COLORS.map(c => {
    return c;
  });

  public associateByTeam: ReportForChartModel = {} as ReportForChartModel;
  public associateBySkillCluster: ReportForChartModel = {} as ReportForChartModel;
  public associateByLevels: ReportForChartModel = {} as ReportForChartModel;
  public associateBySkills: ReportForChartModel = {} as ReportForChartModel;
  public associateByProjectGB: ReportForChartModel = {} as ReportForChartModel;
  public tagCloudFieldForSkillGroup ={
    categoryField:'name',
    valueField:'associates'
  }
  public HAS_VIEW_ASSOCIATE_INFO_PERMISSION: boolean = false;
  constructor(
    private reportService: ReportService,
    private loaderService: LoadingService,
    private router: Router,
    private permisisonService:PermisisonService
  ) { 
    this.HAS_VIEW_ASSOCIATE_INFO_PERMISSION = this.permisisonService.hasPermission(CONFIG.PERMISSIONS.VIEW_ASSOCIATE_INFO_PERMISSION);
  }

  ngOnInit(): void {
  }

  renderChart() {
    this.reRenderTrigger = true;
    this.setDataForGeneralInformation(this.reportData);
    this.setDataForAssociateByTeam(this.reportData);
    this.setDataForAssociateBySkillCluster(this.reportData);
    this.setDataForAssociateByLevel(this.reportData);
    this.setDataForAssociateBySkill(this.reportData);
    this.setDataForAssociateByGB(this.reportData);
    setTimeout(() => {
      this.reRenderTrigger = false;
    }, 1);
  }

  setDataForAssociateByGB(res: any) {
    this.associateByProjectGB.chartLabels = res?.data?.associates_by_gb?.map((r: any) => r.name);
    this.associateByProjectGB.chartData = res?.data?.associates_by_gb?.map((r: any) => r.associates);
    this.associateByProjectGB.chartItemColor = ReportUtils.generateColorsArray(this.colors, this.associateByProjectGB.chartLabels.length);
  }
  setDataForAssociateBySkill(res: any) {
    this.associateBySkills.chartLabels = res?.data?.associates_by_skills?.map((r: any) => r.name);
    this.associateBySkills.chartData = res?.data?.associates_by_skills?.map((r: any) => r.associates);
    this.associateBySkills.chartItemColor = ReportUtils.generateColorsArray(this.colors, this.associateBySkills.chartLabels.length);
  }

  setDataForAssociateByLevel(res: any) {
    this.associateByLevels.chartLabels = res?.data?.associates_by_levels?.map((r: any) => r.name);
    this.associateByLevels.chartData = res?.data?.associates_by_levels?.map((r: any) => r.associates);
    this.associateByLevels.chartItemColor = ReportUtils.generateColorsArray(this.colors, this.associateByLevels.chartLabels.length);
  }

  setDataForAssociateBySkillCluster(res: any) {
    this.associateBySkillCluster.chartLabels = res?.data?.associates_by_skill_cluster?.map((r: any) => r.name);
    this.associateBySkillCluster.chartData = res?.data?.associates_by_skill_cluster?.map((r: any) => r.associates);
    this.associateBySkillCluster.chartItemColor = ReportUtils.generateColorsArray(this.colors, this.associateBySkillCluster.chartLabels.length);
  }

  setDataForAssociateByTeam(res: any) {
    this.associateByTeam.chartLabels = res.data?.associates_by_team?.map((r: any) => r.name);
    this.associateByTeam.chartData = res.data?.associates_by_team?.map((r: any) => r.associates);
    this.associateByTeam.chartItemColor = ReportUtils.generateColorsArray(this.colors, this.associateByTeam.chartLabels.length);
  }

  setDataForGeneralInformation(res: any) {
    this.numOfAssociates = res.data?.associates;
    this.numOfFixedTerms = res.data?.fixedTerms;
    this.numOfInternals = res.data?.internals;
    this.numOfExternals = res.data.externals;
  }

  onFilterChange(data: any) {
    /** 
     * data: {
     *    filterData = Team/Project GB,
     *    filterData = [... , ...]
     * }
     */
    this._countGetFilterData++;
    if (this._countGetFilterData < 2) {
      return;
    }
    
    // Update saved filter 
    data.source = 'associate';
    this.updateSavedFilterEvent.emit(data);
    if (data?.filterType === 'Team') {
      this.savedTeamsInFilterOfAssociate = data?.filterData;
    } else if (data?.filterType === 'Project GB') {
      this.savedProjectGbInFilterOfAssociate = data?.filterData;
    }

    // Build filterString
    let filterString = ReportUtils.buildFilterString(this.savedTeamsInFilterOfAssociate, this.savedProjectGbInFilterOfAssociate);

    // Get new data
    const loader = this.loaderService.showProgressBar()
    this.reportService.getAssociateReportWithFilter(filterString)
    .pipe(finalize(() => {
      this.loaderService.hideProgressBar(loader)
    }))
    .subscribe((res) => {
      this.reportData = res;
      this.renderChart();
    })
  }
  navigateToAssociateInfo() {
    if(!this.HAS_VIEW_ASSOCIATE_INFO_PERMISSION) return
    this.router.navigate([`${CoreUrl.USER_INF}/${CoreUrl.ASSOCIATE_INF}`], { state: { 'EET_DEPARTMENT': true} });
  }
}
