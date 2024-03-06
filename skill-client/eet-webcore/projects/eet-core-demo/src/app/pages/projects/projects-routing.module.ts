import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { ProjectsComponent } from './components/projects.component';
import { VModelComponent } from '../v-model/v-model.component';
import { ProjectListComponent } from './components/project-list/project-list.component';
import { CustomerGbComponent } from './components/customer-gb/customer-gb.component';
import { OurCustomerComponent } from './components/our-customer/our-customer.component';
import { ProjectPortfolioComponent } from './components/project-portfolio/project-portfolio.component';
const routes: Routes = [
  {
    path: '',
    component: ProjectsComponent,
    children: [
      { path: '', redirectTo: 'project-list', pathMatch: 'full' },
      { path: 'project-portfolio', component: ProjectPortfolioComponent},
      { path: 'customer-portfolio', component: OurCustomerComponent},
      { path: 'project-list', component: ProjectListComponent},
      { path: 'v-model', component: VModelComponent },
      { path: 'customer-gb', component: CustomerGbComponent },
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
export class ProjectsRoutingModule { }
