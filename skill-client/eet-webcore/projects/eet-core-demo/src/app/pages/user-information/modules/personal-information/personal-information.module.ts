import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { SharedModule } from "@core/src/lib/shared/shared.module";
import { SharedCommonModule } from "projects/eet-core-demo/src/app/shared/shared.module";
import { AssignedLearningModule } from "./assinged-learning/assigned-learning.module";
import { AssignedProjectModule } from "./assinged-project/assigned-project.module";
import { DialogUploadUploadAvatarComponent } from "./components/dialog/dialog-upload-upload-avatar/dialog-upload-upload-avatar.component";
import { DetailInfomationComponent } from "./detail-infomation/detail-infomation.component";
import { PersonalInformationComponent } from "./personal-information.component";
import { SkillComponent } from "./skill/skill.component";
import { DialogMakeSkillHighlightComponent } from './components/dialog/dialog-make-skill-highlight/dialog-make-skill-highlight.component';
import { HistoricalLevelComponent } from './historical-level/historical-level.component';

@NgModule({
  declarations: [
    PersonalInformationComponent,
    SkillComponent,
    DetailInfomationComponent,
    DialogUploadUploadAvatarComponent,
    DialogMakeSkillHighlightComponent,
    HistoricalLevelComponent
  ],
  providers: [],
  imports: [
    CommonModule,
    SharedModule,
    SharedCommonModule,
    AssignedLearningModule,
    AssignedProjectModule
  ],
  exports: [
    PersonalInformationComponent
  ]
})

export class PersonalInformationModule { }
