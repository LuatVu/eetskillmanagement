import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { ReportUtils } from '../../common/report.utils';
import { ReportForChartModel } from '../../models/report.model';
import { ReportService } from '../../services/report.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { finalize } from 'rxjs/operators';
import { Router } from '@angular/router';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';

@Component({
  selector: 'eet-project-report',
  templateUrl: './project-report.component.html',
  styleUrls: ['./project-report.component.scss']
})
export class ProjectReportComponent implements OnInit {
  @Input() reportData: any = null;
  @Input() allTeamsInFilter: any[] = [];
  @Input() allProjectGbInFilter: any[] = [];
  @Input() savedTeamsInFilterOfProject: string[] = [];
  @Input() savedProjectGbInFilterOfProject: string[] = [];
  @Output() updateSavedFilterEvent = new EventEmitter<any>();
  public _countGetFilterData: number = 0;
  public reRenderTrigger: boolean = true;
  public numOfProjects: number = 0;
  public colors = CONFIG.REPORT.COLORS.map(c => {
    return c;
  });

  public projectsByStatus: ReportForChartModel = {} as ReportForChartModel;
  public projectsByGBs: ReportForChartModel = {} as ReportForChartModel;
  public projectsBySkillCluster: ReportForChartModel = {} as ReportForChartModel;
  public tagCloudFieldForSkillTags = {
    categoryField:'skillTagName',
    valueField:'count'
  }
  constructor(
    private reportService: ReportService,
    private loaderService: LoadingService,
    private router:Router
  ) { }

  ngOnInit() {
  }

  renderChart() {
    this.reRenderTrigger = true;
    this.numOfProjects = this.reportData?.data?.projects;
    this.setDataForProjectByStatus(this.reportData);
    this.setDataForProjectByGBs(this.reportData);
    this.setDataForProjectBySkillCluster(this.reportData);
    setTimeout(() => {
      this.reRenderTrigger = false;
    }, 1);
  }

  setDataForProjectBySkillCluster(res: any) {
    this.projectsBySkillCluster.chartLabels = res?.data?.projects_by_skill_tags?.map((r: any) => r.skillTagName);
    this.projectsBySkillCluster.chartData = res?.data?.projects_by_skill_tags?.map((r: any) => r.count);
    this.projectsBySkillCluster.chartItemColor = ReportUtils.generateColorsArray(this.colors, this.projectsBySkillCluster.chartLabels.length);
  }

  setDataForProjectByGBs(res: any) {
    this.projectsByGBs.chartLabels = res?.data?.projects_by_gb?.map((r: any) => r.name);
    this.projectsByGBs.chartData = res?.data?.projects_by_gb?.map((r: any) => r.projects);
    this.projectsByGBs.chartItemColor = ReportUtils.generateColorsArray(this.colors, this.projectsByGBs.chartLabels.length);
  }

  setDataForProjectByStatus(res: any) {
    this.projectsByStatus.chartLabels = res?.data?.projects_by_status?.map((r: any) => r.status);
    this.projectsByStatus.chartData = res?.data?.projects_by_status?.map((r: any) => r.projects);
    this.projectsByStatus.chartItemColor = ReportUtils.generateColorsArray(this.colors, this.projectsByStatus.chartLabels.length);
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
    data.source = 'project';
    this.updateSavedFilterEvent.emit(data);
    if (data?.filterType === 'Team') {
      this.savedTeamsInFilterOfProject = data?.filterData;
    } else if (data?.filterType === 'GB Unit') {
      this.savedProjectGbInFilterOfProject = data?.filterData;
    }

    // Build filterString
    let filterString = ReportUtils.buildFilterString(this.savedTeamsInFilterOfProject, this.savedProjectGbInFilterOfProject);

    // Get new data
    const loader = this.loaderService.showProgressBar()
    this.reportService.getProjectReportWithFilter(filterString)
    .pipe(finalize(() => {
      this.loaderService.hideProgressBar(loader)
    }))
    .subscribe((res) => {
      this.reportData = res;
      this.renderChart();
    })
  }
  navigateProjectListPage() {
    this.router.navigate([`${CoreUrl.PROJECTS}/${CoreUrl.PROJECT_LIST}`]);
  }
}
