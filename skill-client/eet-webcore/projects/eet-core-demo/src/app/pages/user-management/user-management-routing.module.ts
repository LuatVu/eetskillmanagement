import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { UserManagementComponent } from './user-management/user-management.component';
import { UsersAndGroupsComponent } from './users-and-groups/users-and-groups.component';
import { UserRolesComponent } from './user-roles/user-roles.component';
import { UserPermisionComponent } from './user-permision/user-permision.component';


const routes: Routes = [
  {
    path: '',
    component: UserManagementComponent,
    children: [
      {path:'', redirectTo: 'groups-users', pathMatch: 'full'},
      {path:'groups-users', component: UsersAndGroupsComponent},
      {path:'roles', component: UserRolesComponent},
      {path:'permissions', component: UserPermisionComponent},
    ]
  },
  { path: '**', redirectTo: `${CoreUrl.PersonalInformation}` },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserManagementRoutingModule {}
