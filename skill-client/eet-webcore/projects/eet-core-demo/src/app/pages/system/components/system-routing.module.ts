import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SystemComponent } from './pages/system.component';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { CompetencyLeadComponent } from './pages/competency-lead/competency-lead.component';
import { ManageProjectRoleComponent } from './pages/manage-project-role/manage-project-role.component';
import { ManageSkillComponent } from './pages/manage-skill/manage-skill.component';
import { LineManagerComponent } from './pages/line-manager/line-manager.component';
import { CommonConfigComponent } from './pages/common-config/common-config.component';
import { ExpectSkillsLevelForAssociateComponent } from './pages/expect-skills-level-for-associate/expect-skills-level-for-associate.component';
import { ManageProjectScopeComponent } from './pages/manage-project-scope/manage-project-scope.component';
import { CanDeactivateGuard } from '../../../shared/utils/can-deactivate.guard';
const routes: Routes = [
  {
    path: '',
    component: SystemComponent,
    children: [
      { path: '', redirectTo: 'competency-lead', pathMatch: 'full' },
      { path: 'competency-lead', component: CompetencyLeadComponent },
      { path: 'manage-project-role', component: ManageProjectRoleComponent },
      { path: 'manage-skill', component: ManageSkillComponent },
      { path: 'line-manager', component: LineManagerComponent },
      { path: 'common-config', component: CommonConfigComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'manage-project-scope', component: ManageProjectScopeComponent },
    ]
  },
  {
    path: '**', redirectTo: `${CoreUrl.PersonalInformation}`
  }
]

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [RouterModule]
})
export class SystemRoutingModule { }
