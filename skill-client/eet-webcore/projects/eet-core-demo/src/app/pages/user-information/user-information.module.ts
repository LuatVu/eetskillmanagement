import { formatDate } from "@angular/common";
import { NgModule } from "@angular/core";
import { DateAdapter, MAT_DATE_FORMATS, NativeDateAdapter } from "@angular/material/core";
import { httpInterceptorProviders } from "@core/src/lib/interceptors";
import { SharedModule } from '../../../../../core/src/lib/shared/shared.module';
import { ViewImageService } from "../../shared/services/view-image.service";
import { SharedCommonModule } from '../../shared/shared.module';
import { AssociateInformationCopyModule } from "./modules/associate-information/associate-information.module";
import { PersonalInformationModule } from './modules/personal-information/personal-information.module';
import { UserInformationRoutingModule } from "./user-information-routing.module";
import { UserInformationComponent } from './user-information.component';
import { UserInformationService } from "./user-information.service";

export const PICK_FORMATS = {
  parse: { dateInput: ['YYYY-MM-DD'] },
  display: {
    dateInput: 'input',
    monthYearLabel: { year: 'numeric', month: 'short' },
    dateA11yLabel: { year: 'numeric', month: 'long', day: 'numeric' },
    monthYearA11yLabel: { year: 'numeric', month: 'long' },
  },
};

class PickDateAdapter extends NativeDateAdapter {
  override format(date: Date, displayFormat: Object): string {
    if (displayFormat === 'input') {
      return formatDate(date, 'dd/MM/yyyy', this.locale);
    } else {
      return date.toDateString();
    }
  }
}

@NgModule({
  declarations: [UserInformationComponent],
  providers: [
    UserInformationService,
    ViewImageService,
    { provide: DateAdapter, useClass: PickDateAdapter },
    { provide: MAT_DATE_FORMATS, useValue: PICK_FORMATS },
    httpInterceptorProviders
  ],
  imports: [
    SharedModule,
    SharedCommonModule,
    UserInformationRoutingModule,
    AssociateInformationCopyModule,
    PersonalInformationModule
  ]
})

export class UserInformationModule { }
