import { CommonModule, formatDate } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import {
  DateAdapter,
  MAT_DATE_FORMATS,
  NativeDateAdapter
} from '@angular/material/core';
import { MatSortModule } from '@angular/material/sort';
import { SharedModule } from 'projects/core/src/lib/shared/shared.module';
import { SharedCommonModule } from '../shared/shared.module';
import { AuthLayoutComponent } from '../theme/auth-layout/auth-layout.component';
import { MainLayoutComponent } from '../theme/main-layout/main-layout.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { HomePageComponent } from './home-page/home-page.component';
import { PagesRoutingModule } from './pages-routing.module';
import { ClickDragDirective } from './v-model/click-drag.directive';
import { ListProjectDialogComponent } from './v-model/dialog/list-project-dialog/list-project-dialog.component';
import { OrgChartComponent } from './org-chart/org-chart.component';
import { OverviewComponent } from './overview/overview.component';
import { FqaComponent } from './fqa/fqa.component';
import { ReleaseNoteComponent } from './release-note/release-note.component';


export const PICK_FORMATS = {
  parse: { dateInput: { month: 'short', year: 'numeric', day: 'numeric' } },
  display: {
    dateInput: 'input',
    monthYearLabel: { year: 'numeric', month: 'short' },
    dateA11yLabel: { year: 'numeric', month: 'long', day: 'numeric' },
    monthYearA11yLabel: { year: 'numeric', month: 'long' },
  },
};

class PickDateAdapter extends NativeDateAdapter {
  override format(date: Date, displayFormat: Object): string {
    if (displayFormat === 'input') {
      return formatDate(date, 'dd/MM/yyyy', this.locale);
    } else {
      return date.toDateString();
    }
  }
}

@NgModule({
  declarations: [
    DashboardComponent,
    MainLayoutComponent,
    AuthLayoutComponent,
    HomePageComponent,

    ClickDragDirective,
    ListProjectDialogComponent,
    OrgChartComponent,
    OverviewComponent,
    FqaComponent,
    ReleaseNoteComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    SharedCommonModule,
    PagesRoutingModule,
    MatSortModule,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  providers: [
    { provide: DateAdapter, useClass: PickDateAdapter },
    { provide: MAT_DATE_FORMATS, useValue: PICK_FORMATS },
  ],
})
export class PagesModule { }
