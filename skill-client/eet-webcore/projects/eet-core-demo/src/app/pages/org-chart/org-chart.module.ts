import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrgChartComponent } from './org-chart.component';
import { SharedModule } from '@core/src/lib/shared/shared.module';
import { SharedCommonModule } from '../../shared/shared.module';



@NgModule({
  declarations: [OrgChartComponent],
  imports: [
    CommonModule,
    SharedModule,
    SharedCommonModule,
  ]
})
export class OrgChartModule { }
