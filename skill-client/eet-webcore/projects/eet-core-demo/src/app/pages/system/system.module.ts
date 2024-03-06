import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  BciLayoutModule,
  BciSharedModule,
  PicklistModule
} from '@bci-web-core/core';

import { TranslateModule } from '@ngx-translate/core';
import { EetCoreModule } from 'projects/core/src/public-api';
import { UserManagementModule } from '../user-management/user-management.module';
import { CommonDialogs } from '../../shared/components/dialogs/common-dialog';
import { SharedCommonModule } from '../../shared/shared.module';
import { SystemComponent } from './components/pages/system.component';
import { CompetencyLeadComponent } from './components/pages/competency-lead/competency-lead.component';
import { ManageProjectRoleComponent } from './components/pages/manage-project-role/manage-project-role.component';
import { ManageSkillComponent } from './components/pages/manage-skill/manage-skill.component';
import { SystemRoutingModule } from './components/system-routing.module';
import { SharedModule } from '@core/src/lib/shared/shared.module';
import { SkillDetailDialogComponent } from './components/dialogs/manage-skill/skill-detail-dialog/skill-detail-dialog.component';
import { NoticeMessageDialogComponent } from './components/dialogs/manage-project-role/notice-message-dialog/notice-message-dialog.component';

import { LevelDetailDialogComponent } from './components/dialogs/manage-skill/level-detail-dialog/level-detail-dialog.component';
import { AddNewProjectTaskComponent } from './components/dialogs/manage-project-role/add-new-project-task/add-new-project-task.component';
import { AddCompetencyDialogComponent } from './components/dialogs/competency-lead/add-competency-dialog/add-competency-dialog.component';
import { AddCompetencyLeadDialogComponent } from './components/dialogs/competency-lead/add-competency-lead-dialog/add-competency-lead-dialog.component';
import { LineManagerComponent } from './components/pages/line-manager/line-manager.component';
import { LineManagerDialogComponent } from './components/dialogs/line-manager/line-manager/line-manager-dialog.component';
import { CommonConfigComponent } from './components/pages/common-config/common-config.component';
import { ReplaceTagSkillDialogComponent } from './components/dialogs/common-config/replace-tag-skill-dialog/replace-tag-skill-dialog.component';
import { ExpectSkillsLevelForAssociateComponent } from './components/pages/expect-skills-level-for-associate/expect-skills-level-for-associate.component';
import { ManageProjectScopeComponent } from './components/pages/manage-project-scope/manage-project-scope.component';
import { ManageProjectScopeDialogComponent } from './components/dialogs/manage-project-scope/manage-project-scope-dialog/manage-project-scope-dialog.component';
import { ProjectItemComponent } from '../projects/components/project-item/project-item/project-item.component';



@NgModule({
  declarations: [
    SystemComponent,
    AddCompetencyDialogComponent,
    CompetencyLeadComponent,
    ManageProjectRoleComponent,
    ManageSkillComponent,
    SkillDetailDialogComponent,
    AddNewProjectTaskComponent,
    NoticeMessageDialogComponent,
    LevelDetailDialogComponent,
    AddCompetencyLeadDialogComponent,
    LineManagerComponent,
    LineManagerDialogComponent,
    CommonConfigComponent,
    ReplaceTagSkillDialogComponent,
    ExpectSkillsLevelForAssociateComponent,
    ManageProjectScopeComponent,
    ManageProjectScopeDialogComponent,
  ],
  imports: [
    CommonModule,
    SystemRoutingModule,
    SharedModule,
    SharedCommonModule,
    UserManagementModule
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SystemModule { }
