import { NgModule } from "@angular/core";
import { SharedModule } from '../../../../../../../../core/src/lib/shared/shared.module';
import { SharedCommonModule } from '../../../../../shared/shared.module';
import { AssingedProjectComponent } from "./assinged-project.component";
import { DialogBoschProjectComponent } from "./components/dialog/dialog-add-bosch-project/dialog-add-bosch-project.component";
import { DialogNonBoschProjectComponent } from "./components/dialog/dialog-add-non-bosch-project/dialog-add-non-bosch-project.component";
import { DialogViewProjectComponent } from "./components/dialog/dialog-view-project/dialog-view-project.component";
import { ProjectInfoComponent } from "./components/project-info/project-info.component";

@NgModule({
  declarations: [
    AssingedProjectComponent,
    DialogBoschProjectComponent,
    DialogNonBoschProjectComponent,
    DialogViewProjectComponent,
    ProjectInfoComponent
  ],
  providers: [],
  imports: [
    SharedCommonModule,
    SharedModule
  ],
  exports: [
    AssingedProjectComponent
  ]
})

export class AssignedProjectModule { }
