import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { BciLayoutModule, BciSharedModule, PicklistModule } from '@bci-web-core/core';
import { SharedModule } from '@core/src/lib/shared/shared.module';
import { EetCoreModule } from '@core/src/public-api';
import { TranslateModule } from '@ngx-translate/core';
import { SharedCommonModule } from '../../shared/shared.module';
import { BoschProjectDetailComponent } from './components/bosch-project-detail/bosch-project-detail.component';
import { MembersInfoTableComponent } from './components/bosch-project-detail/members-info-table/members-info-table.component';
import { ProjectMemberDialogComponent } from './components/bosch-project-detail/project-member-dialog/project-member-dialog.component';
import { EditNonBoschProjectDetailComponent } from './components/non-bosch-project-detail/edit-non-bosch-project-detail.component';
import { NonBoschProjectDetailComponent } from './components/non-bosch-project-detail/non-bosch-project-detail.component';
import { ProjectListComponent } from './components/project-list/project-list.component';
import { ProjectsComponent } from './components/projects.component';
import { UploadProjectsComponent } from './components/upload-projects/upload-projects.component';
import { ProjectsRoutingModule } from './projects-routing.module';
import { ProjectsService } from './services/projects.service';
import { CustomerGbComponent } from './components/customer-gb/customer-gb.component';
import { FormControl, ReactiveFormsModule  } from '@angular/forms';
import { MaterialModule } from '@core/src/lib/shared/material.module';
import { MatChipsModule } from '@angular/material/chips';
import { VModelComponent } from '../v-model/v-model.component';
import { ScrollingModule } from '@angular/cdk/scrolling';
import { OurCustomerComponent } from './components/our-customer/our-customer.component';
import { ProjectPortfolioComponent } from './components/project-portfolio/project-portfolio.component';
import { SkillProjectDialogComponent } from './components/customer-gb/dialog/skill-project-dialog/skill-project-dialog.component';
import { ProjectItemComponent } from './components/project-item/project-item/project-item.component';


@NgModule({
  declarations: [
    ProjectsComponent,
    NonBoschProjectDetailComponent,
    EditNonBoschProjectDetailComponent,
    UploadProjectsComponent,
    ProjectListComponent,
    CustomerGbComponent,
    VModelComponent,
    OurCustomerComponent,
    ProjectPortfolioComponent,
    SkillProjectDialogComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    SharedCommonModule,
    PicklistModule,
    BciLayoutModule,
    BciSharedModule,
    EetCoreModule,
    TranslateModule,
    ProjectsRoutingModule,
    ReactiveFormsModule,
    MaterialModule,
    MatChipsModule,
    ScrollingModule
  ],
  providers: [
  ]
})
export class ProjectsModule { }
