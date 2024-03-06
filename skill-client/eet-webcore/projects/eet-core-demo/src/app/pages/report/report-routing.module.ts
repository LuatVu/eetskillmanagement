import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ReportComponent } from './report.component';
import { AssociateReportComponent } from './components/associate-report/associate-report.component';
import { ProjectReportComponent } from './components/project-report/project-report.component';


const routes: Routes = [
  {
    path: '',
    component: ReportComponent,
    children: [
      {path: '', redirectTo: 'associate', pathMatch: 'full' },
      { path: 'associate', component: AssociateReportComponent },
      { path: 'project', component:  ProjectReportComponent}
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ReportRoutingModule { }
