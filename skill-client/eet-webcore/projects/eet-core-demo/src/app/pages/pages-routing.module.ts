import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@core/src/lib/authentication';
import { Error403Component } from '@core/src/lib/layout/eet-errors/403.component';
import { Error404Component } from '@core/src/lib/layout/eet-errors/404.component';
import { Error500Component } from '@core/src/lib/layout/eet-errors/500.component';
import { LoginComponent } from '@core/src/lib/layout/eet-login/login.component';
import { EetRequestComponent } from '@core/src/lib/layout/eet-request/eet-request.component';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { AuthLayoutComponent } from '../theme/auth-layout/auth-layout.component';
import { MainLayoutComponent } from '../theme/main-layout/main-layout.component';

import { EetNoAccessComponent } from '@core/src/lib/layout/eet-no-access/eet-no-access.component';
import { DashboardComponent } from './dashboard/dashboard.component';

import { HomePageComponent } from './home-page/home-page.component';

import { RequestAccessGuard } from '@core/src/lib/authentication/request-access.guard';
import { EetWaitingForApprovalComponent } from '@core/src/lib/layout/eet-waiting-for-approval/eet-waiting-for-approval.component';
import { NoPermisisonsComponent } from '../shared/components/no-permisisons/no-permisisons.component';
import { CONFIG } from '../shared/constants/config.constants';
import { SkillEvaluationComponent } from './career-development/components/skill-evaluation/skill-evaluation.component';
import { OrgChartComponent } from './org-chart/org-chart.component';
import { OverviewComponent } from './overview/overview.component';
import { FqaComponent } from './fqa/fqa.component';
import { CanDeactivateGuard } from '../shared/utils/can-deactivate.guard';
import { ReleaseNoteComponent } from './release-note/release-note.component';

const routes: Routes = [
  {
    path: `${CoreUrl.AUTHENTICATE}`,
    component: AuthLayoutComponent,
    children: [
      { path: '', redirectTo: `${CoreUrl.NO_ACCESS}`, pathMatch: 'full' },
      { path: `${CoreUrl.NO_ACCESS}`, component: EetNoAccessComponent, canActivate: [RequestAccessGuard] },
      { path: `${CoreUrl.WAITING_ACCESS}`, component: EetWaitingForApprovalComponent },
      { path: `${CoreUrl.REQUEST_ACCESS}`, component: EetRequestComponent, canActivate: [RequestAccessGuard] },
      { path: `${CoreUrl.EXTERNAL}`, component: LoginComponent },
    ],
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivateChild: [AuthGuard],
    children: [
      { path: '', redirectTo: `${CoreUrl.EET_OVERVIEW}`, pathMatch: 'full' },
      { path: `${CoreUrl.HomePage}`, component: HomePageComponent },
      { path: `${CoreUrl.DASHBOARD}`, component: DashboardComponent },
      { path: `${CoreUrl.ERROR_403}`, component: Error403Component },
      { path: `${CoreUrl.ERROR_404}`, component: Error404Component },
      { path: `${CoreUrl.ERROR_500}`, component: Error500Component },
      {
        path: `${CoreUrl.USER_INF}`,
        loadChildren: () =>
          import('./user-information/user-information.module').then(
            (m) => m.UserInformationModule
          ),
        data: {
          permissions: [
            CONFIG.PERMISSIONS.VIEW_PERSONAL_PERMISSION,
            CONFIG.PERMISSIONS.VIEW_ASSOCIATE_INFO_PERMISSION]
        }
      },
      {
        path: `${CoreUrl.DEPARTMENT_PROJECTS}`, loadChildren: () =>
          import('./projects/projects.module').then(
            (m) => m.ProjectsModule
          ),
        data: {
          permissions: []
        }
      },
      //   department projects is not available at the moment
      {
        path: `${CoreUrl.SYSTEM}`,
        loadChildren: () =>
          import('./system/system.module').then(
            (m) => m.SystemModule
          ),
        data: {
          permissions: [CONFIG.PERMISSIONS.VIEW_SYSTEM]
        }
      },
      {
        path: `${CoreUrl.SKILL}`, component: SkillEvaluationComponent,
        data: {
          permissions: [CONFIG.PERMISSIONS.VIEW_SKILL_EVALUATE]
        }
      },
      {
        path: `${CoreUrl.ManageRequest}`,
        loadChildren: () =>
          import('./manage-request/manage-request.module').then((m) => m.ManageRequestModule),
        data: {
          permissions: [CONFIG.PERMISSIONS.VIEW_MANAGE_REQUEST]
        }
      },
      {
        path: `${CoreUrl.USER_MANAGEMENT}`,
        loadChildren: () =>
          import('./user-management/user-management.module').then(
            (m) => m.UserManagementModule
          ),
        data: {
          permissions: [CONFIG.PERMISSIONS.VIEW_USER_MANAGEMENT]
        }
      },
      {
        path: `${CoreUrl.REPORT}`,
        loadChildren: () =>
          import('./report/report.module').then(
            (m) => m.ReportModule
          ),
        data: {
          permissions: [CONFIG.PERMISSIONS.VIEW_REPORT]
        }
      },
      {
        path: `${CoreUrl.SUPPLY}`,
        loadChildren: () =>
          import('./supply-demand/supply.module').then(
            (m) => m.SupplyModule
          ),
        data: {
          permissions: [
            CONFIG.PERMISSIONS.USER_DEMAND_SUPPLY,
            CONFIG.PERMISSIONS.ADMIN_DEMAND_SUPPLY]
        }
      },
      {
        path: `${CoreUrl.COMPETENCE_DEVELOPMENT}`,
        loadChildren: () =>
          import('./career-development/career-development.module').then(
            (m) => m.CareerDevelopmentModule
          ),
        data: {
          permissions: []
        }
      },
       {
        path: `${CoreUrl.ORG_CHART}`, component: OrgChartComponent,
        data: {
          permissions: []
        },
        canDeactivate: [CanDeactivateGuard]
      },
      {
        path: `${CoreUrl.OVERVIEW}`, component: OverviewComponent,
        data: {
          permissions: []
        },
        canDeactivate: [CanDeactivateGuard] 
      },
      {
        path: `${CoreUrl.HELP}`, component: FqaComponent,
        data: {
          permissions: []
        }
      },
      {
        path:`${CoreUrl.RELEASE_NOTE}`, component: ReleaseNoteComponent,
        data: {
          permissions: []
        }
      },
      {
        path: `${CoreUrl.NO_PERMISSION}`,
        component: NoPermisisonsComponent
      }
    ],
  },

  { path: '**', redirectTo: `${CoreUrl.ERROR_404}` },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { useHash: true, enableTracing: false }),
  ],
  exports: [RouterModule],
})
export class PagesRoutingModule { }
