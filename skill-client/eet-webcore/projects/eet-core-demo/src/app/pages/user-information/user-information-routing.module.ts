import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UserInformationComponent } from './user-information.component';
import { PersonalInformationModule } from './modules/personal-information/personal-information.module';
import { AssociateInformationCopyModule } from './modules/associate-information/associate-information.module';
import { CanDeactivateGuard } from '../../shared/utils/can-deactivate.guard';
import { PersonalInformationComponent } from './modules/personal-information/personal-information.component';
import { AssociateInformationCopyComponent } from './modules/associate-information/associate-information.component';

const routes: Routes = [
  {
    path: '',
    component: UserInformationComponent,
    children: [
      {path: '', redirectTo: 'personal-info', pathMatch: 'full'},
      {path: 'personal-info', component: PersonalInformationComponent, canDeactivate: [CanDeactivateGuard]},
      {path: 'associate-info', component: AssociateInformationCopyComponent, canDeactivate: [CanDeactivateGuard]}
    ]
  }
]

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [RouterModule]
})

export class UserInformationRoutingModule { }
