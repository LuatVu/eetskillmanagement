import { JdComponent } from './components/jd/jd.component';
import { CareerRoadmapComponent } from './components/career-roadmap/career-roadmap.component';
import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { CareerDevelopmentComponent } from "./career-development.component";
import { LearningComponent } from './components/learning/learning.component';
import { SkillTableComponent } from './components/skill-evaluation/skill-table/skill-table.component';
import { RequestsComponent } from './components/skill-evaluation/requests/requests.component';
import { CONFIG } from '../../shared/constants/config.constants';
import { TechnicalCompetenciesComponent } from './components/technical-competencies/technical-competencies.component';
import { ExpectSkillsLevelForAssociateComponent } from '../system/components/pages/expect-skills-level-for-associate/expect-skills-level-for-associate.component';
import { CanDeactivateGuard } from '../system/components/pages/expect-skills-level-for-associate/can-deactivate.guard';

const routes: Routes = [
    {path: '', component: CareerDevelopmentComponent ,
      children: [
          {path: '', redirectTo:'career-road-map', pathMatch: 'full'},
          {path: 'career-road-map', component: CareerRoadmapComponent},
          {path: 'jd', component: JdComponent, data: {
            permissions: [
              CONFIG.PERMISSIONS.VIEW_DEPARTMENT_LEARNING]
          }},       
          {path: 'learn-courses',component: LearningComponent,
            data: {
              permissions: [
                CONFIG.PERMISSIONS.VIEW_DEPARTMENT_LEARNING]
            }
          },   
          {path: 'self-skill-evaluation', component: SkillTableComponent,
            data: {
              permissions: [
                CONFIG.PERMISSIONS.VIEW_SKILL_EVALUATE]
            },
            canDeactivate: [CanDeactivateGuard]
          },
          {path: 'my-request', component: RequestsComponent,
            data: {
              permissions: [
                CONFIG.PERMISSIONS.VIEW_SKILL_EVALUATE]
            }
          },
          {path: 'technical-competencies', component: ExpectSkillsLevelForAssociateComponent, canDeactivate: [CanDeactivateGuard]}
      ]
    }
]
@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class CareerDevelopmentRoutingModule {}