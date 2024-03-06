import { MatMenuModule } from '@angular/material/menu';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { BciLayoutModule, BciSharedModule } from '@bci-web-core/core';
import { EetAppComponent } from './eet-app/eet-app.component';
import { EetHeaderComponent } from './eet-header/eet-header.component';
import { RouterModule } from '@angular/router';
import { Error403Component } from './eet-errors/403.component';
import { TranslateModule } from '@ngx-translate/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { LoginComponent } from './eet-login/login.component';
import { RegisterComponent } from './eet-register/register.component';
import { Error404Component } from './eet-errors/404.component';
import { Error500Component } from './eet-errors/500.component';
import { ErrorCodeComponent } from './error-code/error-code.component';
import { FlexLayoutModule } from '@angular/flex-layout';
import { EetRequestComponent } from './eet-request/eet-request.component';
import { EetDirectiveModule } from '../shared/directives/eet-directive.module';
import { EetNoAccessComponent } from './eet-no-access/eet-no-access.component';
import { EetWaitingForApprovalComponent } from './eet-waiting-for-approval/eet-waiting-for-approval.component';

const COMPONENTS: any[] = [EetAppComponent, LoginComponent, EetRequestComponent, RegisterComponent, Error403Component, Error404Component, Error500Component, EetWaitingForApprovalComponent];
@NgModule({
  declarations: [...COMPONENTS, EetHeaderComponent, ErrorCodeComponent, EetNoAccessComponent],
  imports: [
    CommonModule,
    RouterModule,
    TranslateModule,
    BciLayoutModule,
    BciSharedModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatFormFieldModule,
    FormsModule,
    MatInputModule,
    ReactiveFormsModule,
    FlexLayoutModule,
    EetDirectiveModule
  ],
  providers: [],
  exports: [...COMPONENTS]
})
export class EetLayoutModule { }
