import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AssociateInformationCopyComponent } from './associate-information.component';
import { SharedModule } from '../../../../../../../core/src/lib/shared/shared.module';
import { SharedCommonModule } from '../../../../shared/shared.module';
import { PersonalInformationModule } from '../personal-information/personal-information.module';
import { AssociateInfoCopyService } from './associate-info.service';
import { AddNewAssociateComponent } from './add-new-associate/add-new-associate.component';
import { EditAssociateComponent } from './edit-associate/edit-associate.component';
import { UserInformationService } from '../../user-information.service';
@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    SharedCommonModule,
    PersonalInformationModule,
  ],
  declarations: [AssociateInformationCopyComponent, AddNewAssociateComponent, EditAssociateComponent],
  exports: [AssociateInformationCopyComponent],
  providers: [AssociateInfoCopyService, UserInformationService]
})
export class AssociateInformationCopyModule { }