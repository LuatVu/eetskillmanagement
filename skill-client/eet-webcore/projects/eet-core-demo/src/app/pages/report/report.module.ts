import { NgModule } from '@angular/core';
import { SharedModule } from '../../../../../core/src/lib/shared/shared.module';
import { SharedCommonModule } from '../../shared/shared.module';
import { ReportRoutingModule } from './report-routing.module';
import { ReportComponent } from './report.component';
import { AssociateReportComponent } from './components/associate-report/associate-report.component';
import { ProjectReportComponent } from './components/project-report/project-report.component';
import { NgChartsModule } from 'ng2-charts';
import { CHART_BASE_COMPONENTS } from './components/chart-base/chart-base';
import { TagCloudComponent } from './components/chart-base/tag-cloud/tag-cloud.component';

@NgModule({
  declarations: [
    ReportComponent,
    AssociateReportComponent,
    CHART_BASE_COMPONENTS,
    ProjectReportComponent,
    TagCloudComponent
  ],
  imports: [
    SharedModule,
    SharedCommonModule,
    ReportRoutingModule,
    NgChartsModule
  ],
  exports: [

  ],
  providers: [],
})
export class ReportModule { }
