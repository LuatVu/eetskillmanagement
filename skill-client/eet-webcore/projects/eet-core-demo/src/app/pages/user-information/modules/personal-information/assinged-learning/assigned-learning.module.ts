import { NgModule } from "@angular/core";
import { AssingedLearningComponent } from './assinged-learning.component';
import { DialogDetailComponent } from './dialogs/dialog-detail/dialog-detail.component';
import { DialogLearningComponent } from './dialogs/dialog-learning/dialog-learning.component';
import { DialogUploadComponent } from './dialogs/dialog-upload/dialog-upload.component';
import { SharedCommonModule } from '../../../../../shared/shared.module';
import { SharedModule } from '../../../../../../../../core/src/lib/shared/shared.module';

@NgModule({
  declarations: [
    AssingedLearningComponent,
    DialogDetailComponent,
    DialogLearningComponent,
    DialogUploadComponent
  ],
  providers: [],
  imports: [
    SharedCommonModule,
    SharedModule
  ],
  exports: [AssingedLearningComponent]
})

export class AssignedLearningModule { }
